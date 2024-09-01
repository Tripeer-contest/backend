package com.j10d207.tripeer.exception;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j//전역 controller에서 발생하는 예외를 잡아 처리함
public class CustomExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponseEntity> handleCustomException(CustomException e){
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponseEntity> handleCustomValidException(MethodArgumentNotValidException e) {
        return ErrorResponseEntity.CustomValid(e);

    }

    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity<ErrorResponseEntity> handleCustomExpiredJwtException(ExpiredJwtException e) {
        return ErrorResponseEntity.CustomJwtExpired(e);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorResponseEntity> handleRuntimeException(RuntimeException e) {
        log.error("Runtime 오류 발생 {}", e.getMessage());
        return ErrorResponseEntity.CustomRuntime(e);
    }
}
