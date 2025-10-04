package com.acme.inventory.jsonapi;


import java.util.List;
import java.util.Map;


public class JsonApi {
    public record Resource<T>(String type, String id, T attributes, Map<String,String> links) {}
    public record OneResponse<T>(Resource<T> data) {}
    public record ErrorItem(String status, String title, String detail) {}
    public record ErrorResponse(List<ErrorItem> errors) {}
}