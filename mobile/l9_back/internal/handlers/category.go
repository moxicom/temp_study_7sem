package handlers

import (
	"net/http"

	"expense-tracker/internal/repository"
	"expense-tracker/pkg/errors"

	"github.com/gin-gonic/gin"
)

type CategoryHandler struct {
	repo *repository.CategoryRepository
}

func NewCategoryHandler() *CategoryHandler {
	return &CategoryHandler{
		repo: repository.NewCategoryRepository(),
	}
}

func (h *CategoryHandler) GetCategories(c *gin.Context) {
	categories, err := h.repo.GetAll()
	if err != nil {
		c.JSON(http.StatusInternalServerError, errors.NewInternalError("Failed to get categories"))
		return
	}

	c.JSON(http.StatusOK, gin.H{"categories": categories})
}
