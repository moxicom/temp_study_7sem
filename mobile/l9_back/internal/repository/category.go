package repository

import (
	"expense-tracker/internal/database"
	"expense-tracker/internal/models"
)

type CategoryRepository struct {
	db interface{}
}

func NewCategoryRepository() *CategoryRepository {
	return &CategoryRepository{
		db: database.DB,
	}
}

func (r *CategoryRepository) GetAll() ([]models.Category, error) {
	var categories []models.Category
	err := database.DB.Find(&categories).Error
	return categories, err
}

