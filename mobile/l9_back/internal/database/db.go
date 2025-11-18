package database

import (
	"fmt"
	"log"
	"os"

	"expense-tracker/internal/models"

	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"gorm.io/gorm/logger"
)

var DB *gorm.DB

func Connect() error {
	host := os.Getenv("DB_HOST")
	user := os.Getenv("DB_USER")
	password := os.Getenv("DB_PASSWORD")
	dbname := os.Getenv("DB_NAME")
	port := os.Getenv("DB_PORT")

	if port == "" {
		port = "5432"
	}

	dsn := fmt.Sprintf("host=%s user=%s password=%s dbname=%s port=%s sslmode=disable TimeZone=UTC",
		host, user, password, dbname, port)

	var err error
	DB, err = gorm.Open(postgres.Open(dsn), &gorm.Config{
		Logger: logger.Default.LogMode(logger.Info),
	})

	if err != nil {
		return fmt.Errorf("failed to connect to database: %w", err)
	}

	log.Println("Database connected successfully")

	return nil
}

func Migrate() error {
	err := DB.AutoMigrate(
		&models.Expense{},
		&models.Category{},
	)

	if err != nil {
		return fmt.Errorf("failed to migrate database: %w", err)
	}

	log.Println("Database migrations completed")

	// Seed default categories if they don't exist
	seedCategories()

	return nil
}

func seedCategories() {
	defaultCategories := []models.Category{
		{Name: "Продукты", Icon: stringPtr("🍔")},
		{Name: "Транспорт", Icon: stringPtr("🚗")},
		{Name: "Жилье", Icon: stringPtr("🏠")},
		{Name: "Здоровье", Icon: stringPtr("🏥")},
		{Name: "Развлечения", Icon: stringPtr("🎬")},
		{Name: "Одежда", Icon: stringPtr("👕")},
		{Name: "Образование", Icon: stringPtr("📚")},
		{Name: "Прочее", Icon: stringPtr("📦")},
	}

	for _, cat := range defaultCategories {
		var existing models.Category
		result := DB.Where("name = ?", cat.Name).First(&existing)
		if result.Error == gorm.ErrRecordNotFound {
			DB.Create(&cat)
		}
	}
}

func stringPtr(s string) *string {
	return &s
}
