package dev.aditya.userauthservice.Exceptions;

public class SessionNotExistException extends Exception{

    public static String defaultMessage = "Session is invalid. It doesn't Exist anymore";

    public SessionNotExistException(){
        super(defaultMessage);
    }
    public SessionNotExistException(String message){
        super(message);
    }
}
