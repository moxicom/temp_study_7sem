package main

import (
	"fmt"
	"net/http"
	"time"

	"github.com/go-chi/chi/v5"
	"github.com/google/uuid"
)

// --- V1 handlers (ignore priority)
func listProjectsV1(w http.ResponseWriter, _ *http.Request) {
	projectsMu.RLock()
	defer projectsMu.RUnlock()
	out := make([]*Project, 0, len(projects))
	for _, p := range projects {
		out = append(out, p)
	}
	writeJSON(w, http.StatusOK, out)
}

func createProjectV1(w http.ResponseWriter, r *http.Request) {
	// require auth for creating (jwtAuthMiddleware sets ctx if valid)
	if !isAuthRequest(r) {
		http.Error(w, "unauthorized", http.StatusUnauthorized)
		return
	}

	var req struct {
		Name        string `json:"name"`
		Description string `json:"description"`
	}
	if err := parseJSON(r, &req); err != nil {
		http.Error(w, "bad request", http.StatusBadRequest)
		return
	}
	// handle idempotency
	if id := idempotencyLookup(r); id != "" {
		projectsMu.RLock()
		if p, ok := projects[id]; ok {
			projectsMu.RUnlock()
			writeJSON(w, http.StatusCreated, p)
			return
		}
		projectsMu.RUnlock()
	}

	now := time.Now().UTC()
	id := uuid.NewString()
	p := &Project{ID: id, Name: req.Name, Description: req.Description, CreatedAt: now, UpdatedAt: now}
	projectsMu.Lock()
	projects[id] = p
	projectsMu.Unlock()

	idempotencyStore(r, id)

	writeJSON(w, http.StatusCreated, p)
}

func getProjectV1(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "projectId")
	projectsMu.RLock()
	p, ok := projects[id]
	projectsMu.RUnlock()
	if !ok {
		http.Error(w, "not found", http.StatusNotFound)
		return
	}
	writeJSON(w, http.StatusOK, p)
}

func updateProjectV1(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "projectId")
	var req struct {
		Name        string `json:"name"`
		Description string `json:"description"`
	}
	if err := parseJSON(r, &req); err != nil {
		http.Error(w, "bad request", http.StatusBadRequest)
		return
	}
	projectsMu.Lock()
	defer projectsMu.Unlock()
	p, ok := projects[id]
	if !ok {
		http.Error(w, "not found", http.StatusNotFound)
		return
	}
	p.Name = req.Name
	p.Description = req.Description
	p.UpdatedAt = time.Now().UTC()
	writeJSON(w, http.StatusOK, p)
}

func deleteProjectV1(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "projectId")
	projectsMu.Lock()
	defer projectsMu.Unlock()
	delete(projects, id)
	writeJSON(w, http.StatusOK, map[string]string{"message": "project deleted"})
}

func listTasksV1(w http.ResponseWriter, r *http.Request) {
	projectId := chi.URLParam(r, "projectId")
	tasksMu.RLock()
	defer tasksMu.RUnlock()
	out := []*Task{}
	for _, t := range tasks {
		if t.ProjectID == projectId {
			out = append(out, t)
		}
	}
	writeJSON(w, http.StatusOK, out)
}

func createTaskV1(w http.ResponseWriter, r *http.Request) {
	if !isAuthRequest(r) {
		http.Error(w, "unauthorized", http.StatusUnauthorized)
		return
	}
	projectId := chi.URLParam(r, "projectId")
	var req struct {
		Title       string `json:"title"`
		Description string `json:"description"`
	}
	if err := parseJSON(r, &req); err != nil {
		http.Error(w, "bad request", http.StatusBadRequest)
		return
	}
	// idempotency
	if id := idempotencyLookup(r); id != "" {
		tasksMu.RLock()
		if t, ok := tasks[id]; ok {
			tasksMu.RUnlock()
			writeJSON(w, http.StatusCreated, t)
			return
		}
		tasksMu.RUnlock()
	}
	now := time.Now().UTC()
	id := uuid.NewString()
	t := &Task{
		ID: id, ProjectID: projectId, Title: req.Title, Description: req.Description,
		Status: "todo", CreatedAt: now, UpdatedAt: now,
	}
	tasksMu.Lock()
	tasks[id] = t
	tasksMu.Unlock()
	idempotencyStore(r, id)
	writeJSON(w, http.StatusCreated, t)
}

func getTaskV1(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "taskId")
	tasksMu.RLock()
	t, ok := tasks[id]
	tasksMu.RUnlock()
	if !ok {
		http.Error(w, "not found", http.StatusNotFound)
		return
	}
	writeJSON(w, http.StatusOK, t)
}

func updateTaskV1(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "taskId")
	var req struct {
		Title       *string `json:"title"`
		Description *string `json:"description"`
		Status      *string `json:"status"`
	}
	if err := parseJSON(r, &req); err != nil {
		http.Error(w, "bad request", http.StatusBadRequest)
		return
	}
	tasksMu.Lock()
	defer tasksMu.Unlock()
	t, ok := tasks[id]
	if !ok {
		http.Error(w, "not found", http.StatusNotFound)
		return
	}
	if req.Title != nil {
		t.Title = *req.Title
	}
	if req.Description != nil {
		t.Description = *req.Description
	}
	if req.Status != nil {
		t.Status = *req.Status
	}
	t.UpdatedAt = time.Now().UTC()
	writeJSON(w, http.StatusOK, t)
}

func deleteTaskV1(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "taskId")
	tasksMu.Lock()
	defer tasksMu.Unlock()
	delete(tasks, id)
	writeJSON(w, http.StatusOK, map[string]string{"message": "task deleted"})
}

// --- V2 handlers (support priority and filters)
func listProjectsV2(w http.ResponseWriter, r *http.Request) {
	listProjectsV1(w, r)
}

func createProjectV2(w http.ResponseWriter, r *http.Request) {
	createProjectV1(w, r)
}

func listTasksV2(w http.ResponseWriter, r *http.Request) {
	projectId := chi.URLParam(r, "projectId")
	status := r.URL.Query().Get("status")
	priorityQ := r.URL.Query().Get("priority")
	var priorityInt int
	if priorityQ != "" {
		// parse
		fmt.Sscanf(priorityQ, "%d", &priorityInt)
	}
	tasksMu.RLock()
	defer tasksMu.RUnlock()
	out := []*Task{}
	for _, t := range tasks {
		if t.ProjectID != projectId {
			continue
		}
		if status != "" && t.Status != status {
			continue
		}
		if priorityQ != "" && t.Priority != priorityInt {
			continue
		}
		out = append(out, t)
	}
	writeJSON(w, http.StatusOK, out)
}

func createTaskV2(w http.ResponseWriter, r *http.Request) {
	if !isAuthRequest(r) {
		http.Error(w, "unauthorized", http.StatusUnauthorized)
		return
	}
	projectId := chi.URLParam(r, "projectId")
	var req struct {
		Title       string `json:"title"`
		Description string `json:"description"`
		Priority    int    `json:"priority"`
	}
	if err := parseJSON(r, &req); err != nil {
		http.Error(w, "bad request", http.StatusBadRequest)
		return
	}
	// default priority if 0
	if req.Priority == 0 {
		req.Priority = 3
	}
	// idempotency
	if id := idempotencyLookup(r); id != "" {
		tasksMu.RLock()
		if t, ok := tasks[id]; ok {
			tasksMu.RUnlock()
			writeJSON(w, http.StatusCreated, t)
			return
		}
		tasksMu.RUnlock()
	}

	now := time.Now().UTC()
	id := uuid.NewString()
	t := &Task{
		ID: id, ProjectID: projectId, Title: req.Title, Description: req.Description,
		Status: "todo", Priority: req.Priority, CreatedAt: now, UpdatedAt: now,
	}
	tasksMu.Lock()
	tasks[id] = t
	tasksMu.Unlock()
	idempotencyStore(r, id)
	writeJSON(w, http.StatusCreated, t)
}

func getTaskV2(w http.ResponseWriter, r *http.Request) {
	getTaskV1(w, r)
}

func updateTaskV2(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "taskId")
	var req struct {
		Title       *string `json:"title"`
		Description *string `json:"description"`
		Status      *string `json:"status"`
		Priority    *int    `json:"priority"`
	}
	if err := parseJSON(r, &req); err != nil {
		http.Error(w, "bad request", http.StatusBadRequest)
		return
	}
	tasksMu.Lock()
	defer tasksMu.Unlock()
	t, ok := tasks[id]
	if !ok {
		http.Error(w, "not found", http.StatusNotFound)
		return
	}
	if req.Title != nil {
		t.Title = *req.Title
	}
	if req.Description != nil {
		t.Description = *req.Description
	}
	if req.Status != nil {
		t.Status = *req.Status
	}
	if req.Priority != nil {
		t.Priority = *req.Priority
	}
	t.UpdatedAt = time.Now().UTC()
	writeJSON(w, http.StatusOK, t)
}
