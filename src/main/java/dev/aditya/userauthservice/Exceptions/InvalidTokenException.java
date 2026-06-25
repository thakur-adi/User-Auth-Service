package dev.aditya.userauthservice.Exceptions;

import org.springframework.security.core.AuthenticationException;

public class InvalidTokenException extends AuthenticationException {

    static String defaultMessage = "Invalid Token. Please try again!!";
    public InvalidTokenException(){
        super(defaultMessage);
    }
    public InvalidTokenException(String message){
        super(message);
    }
}
