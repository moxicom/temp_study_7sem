package main

import (
	"context"
	"fmt"
	"log"
	"net"
	"net/http"
	"strings"
	"time"
)

// logging middleware
func loggingMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		log.Printf("%s %s %s\n", r.RemoteAddr, r.Method, r.URL.Path)
		next.ServeHTTP(w, r)
	})
}

// basic JWT auth middleware (for demo only — simplifies verification)
func jwtAuthMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		// For demo: accept Authorization: Bearer <token>
		// token "demo-token" will be considered valid;
		auth := r.Header.Get("Authorization")
		if auth == "" {
			// no token: allow GETs, but require for write methods
			ctx := context.WithValue(r.Context(), "user", nil)
			next.ServeHTTP(w, r.WithContext(ctx))
			return
		}
		parts := strings.SplitN(auth, " ", 2)
		if len(parts) != 2 || strings.ToLower(parts[0]) != "bearer" {
			http.Error(w, "invalid auth header", http.StatusUnauthorized)
			return
		}
		token := parts[1]
		if token != "demo-token" {
			http.Error(w, "invalid token", http.StatusUnauthorized)
			return
		}
		// set user in ctx
		ctx := context.WithValue(r.Context(), "user", "demo-user")
		next.ServeHTTP(w, r.WithContext(ctx))
	})
}

func isAuthRequest(r *http.Request) bool {
	user := r.Context().Value("user")
	return user != nil
}

// Idempotency helpers
func idempotencyLookup(r *http.Request) string {
	key := r.Header.Get("Idempotency-Key")
	if key == "" {
		return ""
	}
	idempotencyMu.Lock()
	defer idempotencyMu.Unlock()
	if val, ok := idempotency[key]; ok {
		return val
	}
	return ""
}
func idempotencyStore(r *http.Request, resourceID string) {
	key := r.Header.Get("Idempotency-Key")
	if key == "" {
		return
	}
	idempotencyMu.Lock()
	defer idempotencyMu.Unlock()
	idempotency[key] = resourceID
}

// Simple rate limiter per IP
func rateLimitMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		ip := clientIP(r)
		now := time.Now()
		rateMu.Lock()
		cr, ok := rate[ip]
		if !ok || now.After(cr.Reset) {
			cr = &clientRate{Remaining: RateLimitPerMinute - 1, Reset: now.Add(time.Minute)}
			rate[ip] = cr
		} else {
			if cr.Remaining <= 0 {
				// exhausted
				remaining := 0
				w.Header().Set("X-Limit-Remaining", fmt.Sprintf("%d", remaining))
				// Retry-After: seconds until reset
				retry := int(cr.Reset.Sub(now).Seconds())
				w.Header().Set("Retry-After", fmt.Sprintf("%d", retry))
				rateMu.Unlock()
				http.Error(w, "rate limit exceeded", http.StatusTooManyRequests)
				return
			}
			cr.Remaining--
		}
		remaining := cr.Remaining
		resetSeconds := int(cr.Reset.Sub(now).Seconds())
		rateMu.Unlock()

		w.Header().Set("X-Limit-Remaining", fmt.Sprintf("%d", remaining))
		// also expose when reset will happen
		w.Header().Set("X-RateLimit-Reset", fmt.Sprintf("%d", resetSeconds))
		next.ServeHTTP(w, r)
	})
}

func clientIP(r *http.Request) string {
	// try X-Forwarded-For
	xff := r.Header.Get("X-Forwarded-For")
	if xff != "" {
		parts := strings.Split(xff, ",")
		return strings.TrimSpace(parts[0])
	}
	host, _, err := net.SplitHostPort(r.RemoteAddr)
	if err != nil {
		return r.RemoteAddr
	}
	return host
}
