package errors

import "net/http"

type APIError struct {
	Message string `json:"message"`
	Code    int    `json:"code,omitempty"`
	Details string `json:"details,omitempty"`
}

func (e *APIError) Error() string {
	return e.Message
}

func NewBadRequest(message string) *APIError {
	return &APIError{
		Message: message,
		Code:    http.StatusBadRequest,
	}
}

func NewNotFound(message string) *APIError {
	return &APIError{
		Message: message,
		Code:    http.StatusNotFound,
	}
}

func NewInternalError(message string) *APIError {
	return &APIError{
		Message: message,
		Code:    http.StatusInternalServerError,
	}
}

func NewInternalErrorWithDetails(message, details string) *APIError {
	return &APIError{
		Message: message,
		Code:    http.StatusInternalServerError,
		Details: details,
	}
}

