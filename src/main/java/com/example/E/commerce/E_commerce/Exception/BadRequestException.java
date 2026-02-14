package com.example.E.commerce.E_commerce.Exception;

public class BadRequestException extends RuntimeException
{
    public BadRequestException(String message)
    {
        super(message);
    }
}
