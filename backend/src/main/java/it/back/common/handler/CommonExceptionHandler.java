package it.back.common.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import it.back.common.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;

/**
 * 공통 예외처리
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class CommonExceptionHandler {


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> RuntimeExceptHandler(Exception e) {

        String message =
            e.getMessage() != null && e.getMessage().length() > 0 ?
                e.getMessage() : "서버에 오류가 있습니다";

        ErrorResponse error = new ErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


}
