package dev.aditya.userauthservice.Exceptions;

public class SessionNotExist extends Exception{

    public static String defaultMessage = "Session is invalid. It doesn't Exist anymore";

    public SessionNotExist(){
        super(defaultMessage);
    }
    public SessionNotExist(String message){
        super(message);
    }
}
