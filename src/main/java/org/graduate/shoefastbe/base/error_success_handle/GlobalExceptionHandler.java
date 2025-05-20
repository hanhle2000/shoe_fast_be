package org.graduate.shoefastbe.base.error_success_handle;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        String[] exMes = ex.getMessage().split("-");
        ErrorResponse errorResponse = new ErrorResponse(exMes[0], exMes[1]);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
