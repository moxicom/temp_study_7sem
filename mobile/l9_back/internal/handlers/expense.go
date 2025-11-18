package handlers

import (
	"net/http"
	"strconv"
	"time"

	"expense-tracker/internal/models"
	"expense-tracker/internal/repository"
	"expense-tracker/pkg/errors"

	"github.com/gin-gonic/gin"
)

type ExpenseHandler struct {
	repo *repository.ExpenseRepository
}

func NewExpenseHandler() *ExpenseHandler {
	return &ExpenseHandler{
		repo: repository.NewExpenseRepository(),
	}
}

func (h *ExpenseHandler) GetExpenses(c *gin.Context) {
	filter := &models.ExpenseFilter{}

	if category := c.Query("category"); category != "" {
		filter.Category = &category
	}

	if startDateStr := c.Query("startDate"); startDateStr != "" {
		if startDate, err := time.Parse(time.RFC3339, startDateStr); err == nil {
			filter.StartDate = &startDate
		}
	}

	if endDateStr := c.Query("endDate"); endDateStr != "" {
		if endDate, err := time.Parse(time.RFC3339, endDateStr); err == nil {
			filter.EndDate = &endDate
		}
	}

	expenses, err := h.repo.GetAll(filter)
	if err != nil {
		c.JSON(http.StatusInternalServerError, errors.NewInternalError("Failed to get expenses"))
		return
	}

	c.JSON(http.StatusOK, gin.H{"expenses": expenses})
}

func (h *ExpenseHandler) GetExpenseByID(c *gin.Context) {
	id, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, errors.NewBadRequest("Invalid expense ID"))
		return
	}

	expense, err := h.repo.GetByID(uint(id))
	if err != nil {
		c.JSON(http.StatusNotFound, errors.NewNotFound("Expense not found"))
		return
	}

	c.JSON(http.StatusOK, expense)
}

func (h *ExpenseHandler) CreateExpense(c *gin.Context) {
	var req models.ExpenseRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, errors.NewBadRequest(err.Error()))
		return
	}

	date, err := time.Parse(time.RFC3339, req.Date)
	if err != nil {
		c.JSON(http.StatusBadRequest, errors.NewBadRequest("Invalid date format. Use ISO 8601 format"))
		return
	}

	expense := &models.Expense{
		Amount:      req.Amount,
		Category:    req.Category,
		Description: req.Description,
		Date:        date,
	}

	if err := h.repo.Create(expense); err != nil {
		c.JSON(http.StatusInternalServerError, errors.NewInternalError("Failed to create expense"))
		return
	}

	c.JSON(http.StatusCreated, expense)
}

func (h *ExpenseHandler) UpdateExpense(c *gin.Context) {
	id, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, errors.NewBadRequest("Invalid expense ID"))
		return
	}

	_, err = h.repo.GetByID(uint(id))
	if err != nil {
		c.JSON(http.StatusNotFound, errors.NewNotFound("Expense not found"))
		return
	}

	var req models.ExpenseRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, errors.NewBadRequest(err.Error()))
		return
	}

	date, err := time.Parse(time.RFC3339, req.Date)
	if err != nil {
		c.JSON(http.StatusBadRequest, errors.NewBadRequest("Invalid date format. Use ISO 8601 format"))
		return
	}

	expense := &models.Expense{
		Amount:      req.Amount,
		Category:    req.Category,
		Description: req.Description,
		Date:        date,
	}

	if err := h.repo.Update(uint(id), expense); err != nil {
		c.JSON(http.StatusInternalServerError, errors.NewInternalError("Failed to update expense"))
		return
	}

	updatedExpense, err := h.repo.GetByID(uint(id))
	if err != nil {
		c.JSON(http.StatusInternalServerError, errors.NewInternalError("Failed to get updated expense"))
		return
	}

	c.JSON(http.StatusOK, updatedExpense)
}

func (h *ExpenseHandler) DeleteExpense(c *gin.Context) {
	id, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, errors.NewBadRequest("Invalid expense ID"))
		return
	}

	_, err = h.repo.GetByID(uint(id))
	if err != nil {
		c.JSON(http.StatusNotFound, errors.NewNotFound("Expense not found"))
		return
	}

	if err := h.repo.Delete(uint(id)); err != nil {
		c.JSON(http.StatusInternalServerError, errors.NewInternalError("Failed to delete expense"))
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Expense deleted successfully"})
}

func (h *ExpenseHandler) GetStatistics(c *gin.Context) {
	period := c.DefaultQuery("period", "month")
	if period != "day" && period != "week" && period != "month" && period != "year" {
		period = "month"
	}

	var startDate *time.Time
	var endDate *time.Time

	if startDateStr := c.Query("startDate"); startDateStr != "" {
		if parsed, err := time.Parse(time.RFC3339, startDateStr); err == nil {
			startDate = &parsed
		}
	}

	if endDateStr := c.Query("endDate"); endDateStr != "" {
		if parsed, err := time.Parse(time.RFC3339, endDateStr); err == nil {
			endDate = &parsed
		}
	}

	stats, err := h.repo.GetStatistics(period, startDate, endDate)
	if err != nil {
		c.JSON(http.StatusInternalServerError, errors.NewInternalError("Failed to get statistics"))
		return
	}

	c.JSON(http.StatusOK, stats)
}

