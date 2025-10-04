package com.acme.products.web;


public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg){ super(msg); }
}