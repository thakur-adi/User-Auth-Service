package dev.aditya.userauthservice.Exceptions;

import org.springframework.security.core.AuthenticationException;

public class SessionNotExistException extends AuthenticationException {

    public static String defaultMessage = "Session is invalid. It doesn't Exist anymore";

    public SessionNotExistException(){
        super(defaultMessage);
    }
    public SessionNotExistException(String message){
        super(message);
    }
}
