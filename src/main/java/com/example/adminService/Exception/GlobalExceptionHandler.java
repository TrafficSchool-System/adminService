package com.example.adminService.Exception;

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

        Map<String, Object> body = buildError("Valideringsfel", "Vänligen kontrollera dina uppgifter och försök igen.",
                HttpStatus.BAD_REQUEST.value());
        body.put("details", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Map<String, Object>> handleWebClientException(WebClientResponseException ex) {
        // Hantera fel från andra microservices
        if (ex.getStatusCode().value() == 409) {
            // CONFLICT - t.ex. personnummer eller email finns redan
            return new ResponseEntity<>(
                    buildError("Duplicerat värde", "Personnummer eller e-post finns redan registrerad i systemet.",
                            HttpStatus.CONFLICT.value()),
                    HttpStatus.CONFLICT);
        } else if (ex.getStatusCode().value() == 404) {
            // NOT FOUND
            return new ResponseEntity<>(
                    buildError("Användare hittades inte", "Den användare du söker efter kunde inte hittas.",
                            HttpStatus.NOT_FOUND.value()),
                    HttpStatus.NOT_FOUND);
        } else if (ex.getStatusCode().value() >= 400 && ex.getStatusCode().value() < 500) {
            // CLIENT ERROR
            return new ResponseEntity<>(
                    buildError("Felaktig förfrågan", "Vänligen kontrollera dina uppgifter och försök igen.",
                            ex.getStatusCode().value()),
                    ex.getStatusCode());
        } else {
            // SERVER ERROR
            return new ResponseEntity<>(
                    buildError("Tjänsten är tillfälligt otillgänglig", "Vänligen försök igen om en stund.",
                            HttpStatus.SERVICE_UNAVAILABLE.value()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> errorResponse = buildError(
                "Ett fel uppstod",
                "Något gick fel vid bearbetningen. Vänligen försök igen.",
                HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorResponse = buildError(
                "Ett oväntat fel uppstod",
                "Något gick fel. Vänligen försök igen senare eller kontakta support om problemet kvarstår.",
                HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
