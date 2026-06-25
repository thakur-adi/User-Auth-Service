package dev.aditya.userauthservice.Exceptions;

public class InvalidUserInput extends RuntimeException{
    static String defaultMessage = "Invalid input! Please provide appropriate input";
    public InvalidUserInput(){
        super(defaultMessage);
    }
    public InvalidUserInput(String message){super(message);}
}
