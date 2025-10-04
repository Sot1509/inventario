package com.acme.products.web;

import com.acme.products.jsonapi.JsonApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.util.List;



@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<JsonApi.ErrorResponse> handleValidation(MethodArgumentNotValidException ex){
        var errs = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> new JsonApi.ErrorItem("400","Validation error", fe.getField()+": "+fe.getDefaultMessage()))
        .toList();
        return ResponseEntity.badRequest().body(new JsonApi.ErrorResponse(errs));
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<JsonApi.ErrorResponse> handleNotFound(NotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new JsonApi.ErrorResponse(List.of(new JsonApi.ErrorItem("404","Not Found", ex.getMessage()))));
    }
}
