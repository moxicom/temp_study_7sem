package repository

import (
	"expense-tracker/internal/database"
	"expense-tracker/internal/models"
	"time"

	"gorm.io/gorm"
)

type ExpenseRepository struct {
	db *gorm.DB
}

func NewExpenseRepository() *ExpenseRepository {
	return &ExpenseRepository{
		db: database.DB,
	}
}

func (r *ExpenseRepository) GetAll(filter *models.ExpenseFilter) ([]models.Expense, error) {
	var expenses []models.Expense
	query := r.db.Model(&models.Expense{})

	if filter != nil {
		if filter.Category != nil && *filter.Category != "" {
			query = query.Where("category = ?", *filter.Category)
		}
		if filter.StartDate != nil {
			query = query.Where("date >= ?", *filter.StartDate)
		}
		if filter.EndDate != nil {
			query = query.Where("date <= ?", *filter.EndDate)
		}
	}

	err := query.Order("date DESC").Find(&expenses).Error
	return expenses, err
}

func (r *ExpenseRepository) GetByID(id uint) (*models.Expense, error) {
	var expense models.Expense
	err := r.db.First(&expense, id).Error
	if err != nil {
		return nil, err
	}
	return &expense, nil
}

func (r *ExpenseRepository) Create(expense *models.Expense) error {
	return r.db.Create(expense).Error
}

func (r *ExpenseRepository) Update(id uint, expense *models.Expense) error {
	return r.db.Model(&models.Expense{}).Where("id = ?", id).Updates(expense).Error
}

func (r *ExpenseRepository) Delete(id uint) error {
	return r.db.Delete(&models.Expense{}, id).Error
}

func (r *ExpenseRepository) GetStatistics(period string, startDate, endDate *time.Time) (*models.Statistics, error) {
	stats := &models.Statistics{}

	query := r.db.Model(&models.Expense{})

	if startDate != nil {
		query = query.Where("date >= ?", *startDate)
	}
	if endDate != nil {
		query = query.Where("date <= ?", *endDate)
	}

	// Total amount and count
	var totalAmount struct {
		Total float64
		Count int64
	}
	query.Select("COALESCE(SUM(amount), 0) as total, COUNT(*) as count").Scan(&totalAmount)
	stats.TotalAmount = totalAmount.Total
	stats.ExpenseCount = int(totalAmount.Count)

	// By category
	var categoryStats []models.CategoryStat
	categoryQuery := query
	if startDate != nil {
		categoryQuery = categoryQuery.Where("date >= ?", *startDate)
	}
	if endDate != nil {
		categoryQuery = categoryQuery.Where("date <= ?", *endDate)
	}
	categoryQuery.Select("category, COALESCE(SUM(amount), 0) as amount, COUNT(*) as count").
		Group("category").
		Scan(&categoryStats)
	stats.ByCategory = categoryStats

	// By period
	var periodStats []models.PeriodStat
	periodQuery := query
	if startDate != nil {
		periodQuery = periodQuery.Where("date >= ?", *startDate)
	}
	if endDate != nil {
		periodQuery = periodQuery.Where("date <= ?", *endDate)
	}

	var dateFormat string
	switch period {
	case "day":
		dateFormat = "YYYY-MM-DD"
	case "week":
		dateFormat = "YYYY-\"W\"WW"
	case "month":
		dateFormat = "YYYY-MM"
	case "year":
		dateFormat = "YYYY"
	default:
		dateFormat = "YYYY-MM"
	}

	periodQuery.Select("TO_CHAR(date, ?) as period, COALESCE(SUM(amount), 0) as amount", dateFormat).
		Group("period").
		Order("period ASC").
		Scan(&periodStats)
	stats.ByPeriod = periodStats

	return stats, nil
}

