package com.singidunum.encrypted_drive_backend.configs.exceptions;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<Object> handleCustomException(CustomException e) {
        ErrorResponse error = new ErrorResponse(e.getHttpStatus().value(), e.getErrorCode(), e.getMessage());
        return new ResponseEntity<>(error, e.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
           MethodArgumentNotValidException ex,
           HttpHeaders headers,
           HttpStatusCode status,
           WebRequest request
    ) {
        List<FieldError> errors = ex.getFieldErrors();
        Map<String, Object> errorMessage = new HashMap<>();

        errors.forEach(fieldError -> {
            errorMessage.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ErrorCode.VALIDATION.getCode(), errorMessage);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

//    @Override
//    protected ResponseEntity<Object> HttpMessageNotReadableException(
//            HttpMessageNotReadableException ex,
//            HttpHeaders headers,
//            HttpStatusCode status,
//            WebRequest request
//    ) {
//        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ErrorCode.VALIDATION.getCode(), "JSON Body error");
//        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//    }
}
