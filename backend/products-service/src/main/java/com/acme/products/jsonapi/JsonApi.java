package com.acme.products.jsonapi;

import java.util.List;
import java.util.Map;


public class JsonApi {
    public record Resource<T>(String type, String id, T attributes, Map<String,String> links) {}
    public record PageMeta(int number, int size, long totalElements, int totalPages) {}
    public record ListResponse<T>(List<Resource<T>> data, Map<String,String> links, Map<String,Object> meta) {}
    public record OneResponse<T>(Resource<T> data) {}
    public record ErrorItem(String status, String title, String detail) {}
    public record ErrorResponse(List<ErrorItem> errors) {}
}