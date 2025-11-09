package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"sync"
	"time"

	"github.com/gorilla/mux"
)

// Item представляет элемент данных в хранилище
type Item struct {
	ID        int       `json:"id"`
	Name      string    `json:"name"`
	CreatedAt time.Time `json:"created_at"`
}

// Response представляет стандартный ответ API
type Response struct {
	Success bool        `json:"success"`
	Data    interface{} `json:"data,omitempty"`
	Message string      `json:"message,omitempty"`
}

// Storage представляет in-memory хранилище
type Storage struct {
	mu     sync.RWMutex
	items  map[int]*Item
	nextID int
}

var storage = &Storage{
	items:  make(map[int]*Item),
	nextID: 1,
}

// @title Simple API Service
// @version 1.0
// @description Простой API сервис для нагрузочного тестирования с k6
// @termsOfService http://swagger.io/terms/

// @contact.name API Support
// @contact.email support@example.com

// @host localhost:8080
// @BasePath /api/v1

// @schemes http

func main() {
	// Инициализация тестовых данных
	initTestData()

	r := mux.NewRouter()
	api := r.PathPrefix("/api/v1").Subrouter()

	// Health check endpoint
	// @Summary Health check
	// @Description Проверка работоспособности сервиса
	// @Tags health
	// @Accept json
	// @Produce json
	// @Success 200 {object} Response
	// @Router /health [get]
	api.HandleFunc("/health", healthHandler).Methods("GET")

	// Get all items
	// @Summary Получить все элементы
	// @Description Возвращает список всех элементов из хранилища
	// @Tags items
	// @Accept json
	// @Produce json
	// @Success 200 {object} Response
	// @Router /items [get]
	api.HandleFunc("/items", getItemsHandler).Methods("GET")

	// Get items count (должен быть зарегистрирован ПЕРЕД /items/{id})
	// @Summary Получить количество элементов
	// @Description Возвращает общее количество элементов в хранилище
	// @Tags items
	// @Accept json
	// @Produce json
	// @Success 200 {object} Response
	// @Router /items/count [get]
	api.HandleFunc("/items/count", getItemsCountHandler).Methods("GET")

	// Get item by ID
	// @Summary Получить элемент по ID
	// @Description Возвращает элемент по указанному ID
	// @Tags items
	// @Accept json
	// @Produce json
	// @Param id path int true "ID элемента"
	// @Success 200 {object} Response
	// @Failure 404 {object} Response
	// @Router /items/{id} [get]
	api.HandleFunc("/items/{id}", getItemByIDHandler).Methods("GET")

	// Swagger JSON endpoint
	r.HandleFunc("/swagger.json", swaggerHandler).Methods("GET")

	// Swagger UI endpoint (простая HTML страница)
	r.HandleFunc("/swagger", swaggerUIHandler).Methods("GET")

	port := ":8080"
	fmt.Printf("Сервер запущен на порту %s\n", port)
	fmt.Printf("API доступен по адресу: http://localhost%s/api/v1\n", port)
	fmt.Printf("Swagger UI: http://localhost%s/swagger\n", port)
	log.Fatal(http.ListenAndServe(port, r))
}

func initTestData() {
	storage.mu.Lock()
	defer storage.mu.Unlock()

	// Добавляем несколько тестовых элементов
	for i := 1; i <= 10; i++ {
		storage.items[i] = &Item{
			ID:        i,
			Name:      fmt.Sprintf("Item %d", i),
			CreatedAt: time.Now().Add(-time.Duration(i) * time.Hour),
		}
		storage.nextID = i + 1
	}
}

func healthHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(Response{
		Success: true,
		Message: "Service is healthy",
	})
}

func getItemsHandler(w http.ResponseWriter, r *http.Request) {
	storage.mu.RLock()
	defer storage.mu.RUnlock()

	items := make([]*Item, 0, len(storage.items))
	for _, item := range storage.items {
		items = append(items, item)
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(Response{
		Success: true,
		Data:    items,
	})
}

func getItemByIDHandler(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	id := vars["id"]

	var itemID int
	if _, err := fmt.Sscanf(id, "%d", &itemID); err != nil {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusBadRequest)
		json.NewEncoder(w).Encode(Response{
			Success: false,
			Message: "Invalid ID format",
		})
		return
	}

	storage.mu.RLock()
	item, exists := storage.items[itemID]
	storage.mu.RUnlock()

	w.Header().Set("Content-Type", "application/json")
	if !exists {
		w.WriteHeader(http.StatusNotFound)
		json.NewEncoder(w).Encode(Response{
			Success: false,
			Message: "Item not found",
		})
		return
	}

	json.NewEncoder(w).Encode(Response{
		Success: true,
		Data:    item,
	})
}

func getItemsCountHandler(w http.ResponseWriter, r *http.Request) {
	storage.mu.RLock()
	count := len(storage.items)
	storage.mu.RUnlock()

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(Response{
		Success: true,
		Data: map[string]int{
			"count": count,
		},
	})
}

func swaggerHandler(w http.ResponseWriter, r *http.Request) {
	swagger := map[string]interface{}{
		"openapi": "3.0.0",
		"info": map[string]interface{}{
			"title":       "Simple API Service",
			"version":     "1.0",
			"description": "Простой API сервис для нагрузочного тестирования с k6",
		},
		"servers": []map[string]string{
			{"url": "http://localhost:8080/api/v1"},
		},
		"paths": map[string]interface{}{
			"/health": map[string]interface{}{
				"get": map[string]interface{}{
					"tags":        []string{"health"},
					"summary":     "Health check",
					"description": "Проверка работоспособности сервиса",
					"responses": map[string]interface{}{
						"200": map[string]interface{}{
							"description": "Success",
							"content": map[string]interface{}{
								"application/json": map[string]interface{}{
									"schema": map[string]interface{}{
										"$ref": "#/components/schemas/Response",
									},
								},
							},
						},
					},
				},
			},
			"/items": map[string]interface{}{
				"get": map[string]interface{}{
					"tags":        []string{"items"},
					"summary":     "Получить все элементы",
					"description": "Возвращает список всех элементов из хранилища",
					"responses": map[string]interface{}{
						"200": map[string]interface{}{
							"description": "Success",
							"content": map[string]interface{}{
								"application/json": map[string]interface{}{
									"schema": map[string]interface{}{
										"$ref": "#/components/schemas/Response",
									},
								},
							},
						},
					},
				},
			},
			"/items/{id}": map[string]interface{}{
				"get": map[string]interface{}{
					"tags":        []string{"items"},
					"summary":     "Получить элемент по ID",
					"description": "Возвращает элемент по указанному ID",
					"parameters": []map[string]interface{}{
						{
							"name":        "id",
							"in":          "path",
							"required":    true,
							"description": "ID элемента",
							"schema": map[string]interface{}{
								"type": "integer",
							},
						},
					},
					"responses": map[string]interface{}{
						"200": map[string]interface{}{
							"description": "Success",
							"content": map[string]interface{}{
								"application/json": map[string]interface{}{
									"schema": map[string]interface{}{
										"$ref": "#/components/schemas/Response",
									},
								},
							},
						},
						"404": map[string]interface{}{
							"description": "Not Found",
							"content": map[string]interface{}{
								"application/json": map[string]interface{}{
									"schema": map[string]interface{}{
										"$ref": "#/components/schemas/Response",
									},
								},
							},
						},
					},
				},
			},
			"/items/count": map[string]interface{}{
				"get": map[string]interface{}{
					"tags":        []string{"items"},
					"summary":     "Получить количество элементов",
					"description": "Возвращает общее количество элементов в хранилище",
					"responses": map[string]interface{}{
						"200": map[string]interface{}{
							"description": "Success",
							"content": map[string]interface{}{
								"application/json": map[string]interface{}{
									"schema": map[string]interface{}{
										"$ref": "#/components/schemas/Response",
									},
								},
							},
						},
					},
				},
			},
		},
		"components": map[string]interface{}{
			"schemas": map[string]interface{}{
				"Item": map[string]interface{}{
					"type": "object",
					"properties": map[string]interface{}{
						"id": map[string]interface{}{
							"type":        "integer",
							"description": "ID элемента",
						},
						"name": map[string]interface{}{
							"type":        "string",
							"description": "Название элемента",
						},
						"created_at": map[string]interface{}{
							"type":        "string",
							"format":      "date-time",
							"description": "Время создания",
						},
					},
				},
				"Response": map[string]interface{}{
					"type": "object",
					"properties": map[string]interface{}{
						"success": map[string]interface{}{
							"type":        "boolean",
							"description": "Успешность операции",
						},
						"data": map[string]interface{}{
							"description": "Данные ответа",
							"oneOf": []map[string]interface{}{
								{"type": "array", "items": map[string]interface{}{"$ref": "#/components/schemas/Item"}},
								{"$ref": "#/components/schemas/Item"},
								{"type": "object"},
							},
						},
						"message": map[string]interface{}{
							"type":        "string",
							"description": "Сообщение",
						},
					},
				},
			},
		},
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(swagger)
}

func swaggerUIHandler(w http.ResponseWriter, r *http.Request) {
	html := `<!DOCTYPE html>
<html>
<head>
    <title>API Documentation</title>
    <link rel="stylesheet" type="text/css" href="https://unpkg.com/swagger-ui-dist@4.5.0/swagger-ui.css" />
    <style>
        html {
            box-sizing: border-box;
            overflow: -moz-scrollbars-vertical;
            overflow-y: scroll;
        }
        *, *:before, *:after {
            box-sizing: inherit;
        }
        body {
            margin:0;
            background: #fafafa;
        }
    </style>
</head>
<body>
    <div id="swagger-ui"></div>
    <script src="https://unpkg.com/swagger-ui-dist@4.5.0/swagger-ui-bundle.js"></script>
    <script src="https://unpkg.com/swagger-ui-dist@4.5.0/swagger-ui-standalone-preset.js"></script>
    <script>
        window.onload = function() {
            const ui = SwaggerUIBundle({
                url: "/swagger.json",
                dom_id: '#swagger-ui',
                deepLinking: true,
                presets: [
                    SwaggerUIBundle.presets.apis,
                    SwaggerUIStandalonePreset
                ],
                plugins: [
                    SwaggerUIBundle.plugins.DownloadUrl
                ],
                layout: "StandaloneLayout"
            });
        };
    </script>
</body>
</html>`
	w.Header().Set("Content-Type", "text/html")
	w.Write([]byte(html))
}
