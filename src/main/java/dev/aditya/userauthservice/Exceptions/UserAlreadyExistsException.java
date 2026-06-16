package dev.aditya.userauthservice.Exceptions;

public class UserAlreadyExistsException extends Exception{

    static String message = "User already Exists. Please log in using registered Email and Password!!";

    public UserAlreadyExistsException(){super(message);}
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
