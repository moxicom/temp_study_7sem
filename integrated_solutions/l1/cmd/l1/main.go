package main

import (
	"encoding/json"
	"log"
	"net/http"
	"sync"
	"time"

	"github.com/go-chi/chi/v5"
)

type Project struct {
	ID          string    `json:"id"`
	Name        string    `json:"name"`
	Description string    `json:"description,omitempty"`
	CreatedAt   time.Time `json:"createdAt"`
	UpdatedAt   time.Time `json:"updatedAt"`
}

type Task struct {
	ID          string    `json:"id"`
	ProjectID   string    `json:"projectId"`
	Title       string    `json:"title"`
	Description string    `json:"description,omitempty"`
	Status      string    `json:"status"`
	Priority    int       `json:"priority,omitempty"` // v2 field; safe to ignore in v1
	CreatedAt   time.Time `json:"createdAt"`
	UpdatedAt   time.Time `json:"updatedAt"`
}

// In-memory stores:
var (
	projectsMu sync.RWMutex
	projects   = map[string]*Project{}

	tasksMu sync.RWMutex
	tasks   = map[string]*Task{}

	// idempotency store: key -> resource id
	idempotencyMu sync.Mutex
	idempotency   = map[string]string{}

	// simple rate limiter: ip -> (lastReset, count)
	rateMu sync.Mutex
	rate   = map[string]*clientRate{}
)

type clientRate struct {
	Remaining int
	Reset     time.Time
}

const RateLimitPerMinute = 60

func main() {
	r := chi.NewRouter()

	// Global middlewares
	r.Use(loggingMiddleware)
	r.Use(rateLimitMiddleware)
	r.Use(jwtAuthMiddleware) // in this example, JWT optional for GETs, required for writes

	r.Route("/api/v1", func(r chi.Router) {
		r.Get("/projects", listProjectsV1)
		r.Post("/projects", createProjectV1)
		r.Get("/projects/{projectId}", getProjectV1)
		// tasks
		r.Get("/projects/{projectId}/tasks", listTasksV1)
		r.Post("/projects/{projectId}/tasks", createTaskV1)
		r.Get("/tasks/{taskId}", getTaskV1)
		r.Put("/tasks/{taskId}", updateTaskV1)
	})

	r.Route("/api/v2", func(r chi.Router) {
		r.Get("/projects", listProjectsV2)
		r.Post("/projects", createProjectV2)
		r.Get("/projects/{projectId}/tasks", listTasksV2) // supports filter by priority/status
		r.Post("/projects/{projectId}/tasks", createTaskV2)
		r.Get("/tasks/{taskId}", getTaskV2)
		r.Put("/tasks/{taskId}", updateTaskV2)
	})

	log.Println("listening :8080")
	http.ListenAndServe(":8080", r)
}

// helpers
func writeJSON(w http.ResponseWriter, code int, v interface{}) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(code)
	json.NewEncoder(w).Encode(v)
}

func parseJSON(r *http.Request, dst interface{}) error {
	return json.NewDecoder(r.Body).Decode(dst)
}
