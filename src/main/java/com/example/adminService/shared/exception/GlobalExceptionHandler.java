package com.example.adminService.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildError(String error, String message, int status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        return body;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        Map<String, Object> body = buildError("Validation error", "Please check your input and try again.",
                HttpStatus.BAD_REQUEST.value());
        body.put("details", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Map<String, Object>> handleWebClientException(WebClientResponseException ex) {
        // Handle errors from other microservices
        if (ex.getStatusCode().value() == 409) {
            // CONFLICT - e.g. personal number or email already exists
            return new ResponseEntity<>(
                    buildError("Duplicate value", "Personal number or email is already registered in the system.",
                            HttpStatus.CONFLICT.value()),
                    HttpStatus.CONFLICT);
        } else if (ex.getStatusCode().value() == 404) {
            // NOT FOUND
            return new ResponseEntity<>(
                    buildError("User not found", "The user you are looking for could not be found.",
                            HttpStatus.NOT_FOUND.value()),
                    HttpStatus.NOT_FOUND);
        } else if (ex.getStatusCode().value() >= 400 && ex.getStatusCode().value() < 500) {
            // CLIENT ERROR
            return new ResponseEntity<>(
                    buildError("Invalid request", "Please check your input and try again.",
                            ex.getStatusCode().value()),
                    ex.getStatusCode());
        } else {
            // SERVER ERROR
            return new ResponseEntity<>(
                    buildError("Service temporarily unavailable", "Please try again in a moment.",
                            HttpStatus.SERVICE_UNAVAILABLE.value()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> errorResponse = buildError(
                "An error occurred",
                "Something went wrong during processing. Please try again.",
                HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorResponse = buildError(
                "An unexpected error occurred",
                "Something went wrong. Please try again later or contact support if the problem persists.",
                HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
