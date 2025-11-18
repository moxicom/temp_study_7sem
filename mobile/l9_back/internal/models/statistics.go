package models

type Statistics struct {
	TotalAmount  float64             `json:"totalAmount"`
	ExpenseCount int                 `json:"expenseCount"`
	ByCategory   []CategoryStat      `json:"byCategory"`
	ByPeriod     []PeriodStat        `json:"byPeriod"`
}

type CategoryStat struct {
	Category string  `json:"category"`
	Amount   float64 `json:"amount"`
	Count    int     `json:"count"`
}

type PeriodStat struct {
	Period string  `json:"period"`
	Amount float64 `json:"amount"`
}

