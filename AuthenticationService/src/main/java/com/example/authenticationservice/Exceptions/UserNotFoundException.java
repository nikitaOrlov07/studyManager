package com.example.authenticationservice.Exceptions;


public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message); // Вызываем конструктор суперкласса с сообщением
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
