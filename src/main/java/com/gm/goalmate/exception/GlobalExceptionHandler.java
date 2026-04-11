package com.gm.goalmate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> createResponseEntity(HttpStatusCode httpStatusCode, String message) {
        String finalMessage = (message != null) ? message : "예기치 못한 오류가 발생했습니다.";
        ErrorResponse response = ErrorResponse.builder()
                .status(httpStatusCode.value())
                .message(finalMessage)
                .build();

        return ResponseEntity.status(httpStatusCode).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage(); //여러 에러 중 첫번째 에러만
        return createResponseEntity(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e) {
        return createResponseEntity(e.getStatusCode(), e.getReason());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        return createResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = String.format("잘못된 값('%s') 입니다.", e.getValue());

        if(e.getRequiredType() != null && e.getRequiredType().isEnum()) {
            message += String.format(" (사용 가능한 값: '%s')", Arrays.toString(e.getRequiredType().getEnumConstants()));
        }

        return createResponseEntity(HttpStatus.BAD_REQUEST, message);
    }
}
