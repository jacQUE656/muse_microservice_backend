package com.example.userservice.exceptionHandler;

import com.example.common_lib.msException.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.common_lib.payload.enums.ErrorCode.*;


@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionHandler {

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleException(final DisabledException ex){

        log.debug(ex.getMessage(), ex);
        final  ErrorResponse body = ErrorResponse.builder()
                .code(ERR_USR_DISABLED.getCode())
                .message(EMAIL_ALREADY_EXISTS.getDefaultMessage())
                .build();
        return ResponseEntity.status(ERR_USR_DISABLED.getStatus())
                .body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleException(final BadCredentialsException exp){
        log.debug(exp.getMessage(), exp);

        final ErrorResponse response = ErrorResponse.builder()
                .code(BAD_CREDENTIALS.getCode())
                .message(BAD_CREDENTIALS.getDefaultMessage())
                .build();

        return ResponseEntity.status(BAD_CREDENTIALS.getStatus())
                .body(response);

    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(final UsernameNotFoundException exp) {

        log.debug(exp.getMessage(), exp);

        final ErrorResponse response = ErrorResponse.builder()
                .code(USERNAME_NOT_FOUND.getCode())
                .message(USERNAME_NOT_FOUND.getDefaultMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
