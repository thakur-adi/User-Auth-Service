package dev.aditya.userauthservice.Exceptions;

public class UserNotFoundException extends Exception{

    static String defaultMessage = "User doesn't exist";
    public UserNotFoundException(){
        super(defaultMessage);
    }
    public UserNotFoundException(String message){
        super(message);
    }
}
