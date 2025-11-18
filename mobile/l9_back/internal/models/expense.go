package models

import (
	"time"

	"gorm.io/gorm"
)

type Expense struct {
	ID          uint           `json:"id" gorm:"primaryKey"`
	Amount      float64        `json:"amount" gorm:"not null;check:amount >= 0"`
	Category    string         `json:"category" gorm:"not null"`
	Description *string        `json:"description"`
	Date        time.Time      `json:"date" gorm:"not null"`
	CreatedAt   time.Time      `json:"createdAt"`
	UpdatedAt   time.Time      `json:"updatedAt"`
	DeletedAt   gorm.DeletedAt `json:"-" gorm:"index"`
}

type ExpenseRequest struct {
	Amount      float64  `json:"amount" binding:"required,min=0"`
	Category    string   `json:"category" binding:"required"`
	Description *string  `json:"description"`
	Date        string   `json:"date" binding:"required"`
}

type ExpenseFilter struct {
	Category  *string
	StartDate *time.Time
	EndDate   *time.Time
}

