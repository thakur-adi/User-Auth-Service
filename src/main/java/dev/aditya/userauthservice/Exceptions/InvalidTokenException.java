package dev.aditya.userauthservice.Exceptions;

public class InvalidTokenException extends Exception{

    static String defaultMessage = "Invalid Token. Please try again!!";
    public InvalidTokenException(){
        super(defaultMessage);
    }
    public InvalidTokenException(String message){
        super(message);
    }
}
