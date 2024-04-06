package com.database.thiendb.Exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ InvalidRequestException.class, ObjectNotFoundException.class })
    private ResponseEntity<Map<String, String>> handleException(ResponseStatusException ex) {
        Map<String, String> resp = new HashMap<>();
        resp.put("error", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(resp);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<Map<String, String>> handleException(MethodArgumentTypeMismatchException ex) {
        Map<String, String> resp = new HashMap<>();
        String param = ex.getName();
        String expectedType = ex.getRequiredType().getSimpleName();
        String actualType = ex.getValue().getClass().getSimpleName();
        resp.put("error",
                String.format("Parameter '%s' should be of type '%s', but got: '%s'", param, expectedType, actualType));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }
    
}