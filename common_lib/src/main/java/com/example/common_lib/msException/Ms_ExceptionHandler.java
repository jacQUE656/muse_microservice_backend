package com.example.common_lib.msException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;


import static com.example.common_lib.payload.enums.ErrorCode.INTERNAL_EXCEPTION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class Ms_ExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleException(final BusinessException ex){

        final ErrorResponse body = ErrorResponse.builder()
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .build();
        log.info("Business exception: {}", ex.getMessage());
        log.debug(ex.getMessage(), ex);
        return ResponseEntity.status(
                        ex.getErrorCode()
                                .getStatus() != null ? ex.getErrorCode()
                                                       .getStatus() : BAD_REQUEST)
                .body(body);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception exp) {

        log.error(exp.getMessage(), exp);
        final ErrorResponse response = ErrorResponse.builder()
                .code(INTERNAL_EXCEPTION.getCode())
                .message(INTERNAL_EXCEPTION.getDefaultMessage())
                .build();
        return ResponseEntity.status(INTERNAL_EXCEPTION.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException ex){

        final List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    final String fieldName = ((FieldError) error).getField();
                    final String errorCode = error.getDefaultMessage();
                    errors .add(ErrorResponse.ValidationError.builder()
                            .field(fieldName)
                            .code(errorCode)
                            .message(errorCode)
                            .build());
                });
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .validationErrors(errors)
                .build();
        return ResponseEntity.status(BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(final EntityNotFoundException ex){
        log.debug(ex.getMessage(), ex);

        final ErrorResponse response = ErrorResponse.builder()
                .code("TBD")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response , HttpStatus.NOT_FOUND);
    }


}
