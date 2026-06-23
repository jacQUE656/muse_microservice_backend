package com.example.common_lib.msException;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class ErrorResponse {
    private String message;
    private String code;
    private List<ValidationError> validationErrors;


    @Getter
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    @ToString
    public static class ValidationError {
        private String field;
        private String code;
        private String message;
    }
}