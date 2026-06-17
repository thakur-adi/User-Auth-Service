package dev.aditya.userauthservice.Exceptions;

public class CredentialMismatchException extends Exception{

    static String defaultMessage = "Credentials Mismatch! Please try again!!";
    public CredentialMismatchException(){
        super(defaultMessage);
    }
    public CredentialMismatchException(String message){
        super(message);
    }
}
