package com.j10d207.tripeer.exception;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ErrorResponseEntity {
    private int status;
    private String name;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode e){
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .status(e.getHttpStatus().value())
                        .name(e.name())
                        .code(e.getCode())
                        .message(e.getMessage())
                        .build());
    }

    public static ResponseEntity<ErrorResponseEntity> CustomValid(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        // 검증 실패한 모든 필드 오류를 가져오기
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        // 오류 메시지를 가져오기
        String errorMessage = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(ErrorCode.INVALID_ARGUMENT.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .status(ErrorCode.INVALID_ARGUMENT.getHttpStatus().value())
                        .name("INVALID_ARGUMENT")
                        .code(ErrorCode.INVALID_ARGUMENT.getCode())
                        .message(errorMessage)
                        .build());
    }

    public static ResponseEntity<ErrorResponseEntity> CustomJwtExpired(ExpiredJwtException e) {
        return ResponseEntity
                .status(ErrorCode.EXPIRED_JWT.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .status(ErrorCode.EXPIRED_JWT.getHttpStatus().value())
                        .name("Expired_Jwt")
                        .code(ErrorCode.EXPIRED_JWT.getCode())
                        .message(e.getMessage())
                        .build());
    }
}
