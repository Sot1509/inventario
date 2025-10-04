package com.acme.inventory.web;


import com.acme.inventory.jsonapi.JsonApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.List;


@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<JsonApi.ErrorResponse> notFound(NotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new JsonApi.ErrorResponse(List.of(new JsonApi.ErrorItem("404","Not Found", ex.getMessage()))));
        }


        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<JsonApi.ErrorResponse> badReq(IllegalArgumentException ex){
        return ResponseEntity.badRequest()
        .body(new JsonApi.ErrorResponse(List.of(new JsonApi.ErrorItem("400","Bad Request", ex.getMessage()))));
        }
    }