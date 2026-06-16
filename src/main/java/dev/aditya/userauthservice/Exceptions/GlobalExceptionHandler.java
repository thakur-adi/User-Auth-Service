package dev.aditya.userauthservice.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.zip.DataFormatException;

@ControllerAdvice
public class GlobalExceptionHandler {

    //For Predefined Exceptions

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException(NullPointerException e){
        return new ResponseEntity<>("Unkown issue encountered. Please Try again later!",HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(DataFormatException.class)
    public ResponseEntity<String> handleDateFormatException(DataFormatException e){
        return new ResponseEntity<>("Unkown issue encountered. Please Try again later!",HttpStatus.SERVICE_UNAVAILABLE);
    }


    //For custom defined Exceptions
    @ExceptionHandler(SessionNotExistException.class)
    public ResponseEntity<String> handleSessionNotExistException(SessionNotExistException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
}
