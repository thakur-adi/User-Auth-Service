package dev.aditya.userauthservice.Exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;
import java.util.zip.DataFormatException;

@ControllerAdvice
public class GlobalExceptionHandler {

    //For Predefined Exceptions

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException(NullPointerException e){
        return new ResponseEntity<>("Unknown issue encountered!! Please Try again later!",HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<String> handleMissingRequestCookieException(MissingRequestCookieException e)
    {
        return new ResponseEntity<>("Cookies are missing!! Please login again you have been logged out!!",HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> handleDateTimeParseException(DateTimeParseException e){
        return new ResponseEntity<>("Invalid Date of Birth!! Please enter proper Date(DD/MM/YYYY) and try again!",HttpStatus.UNPROCESSABLE_CONTENT);
    }
/*
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException e){
        return new ResponseEntity<>("Token has expired! Please Login again!!",HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleJwtException(JwtException e){
        return new ResponseEntity<>("Invalid Token! Please try again!",HttpStatus.UNAUTHORIZED);
    }
*/

    //For custom defined Exceptions

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CredentialMismatchException.class)
    public ResponseEntity<String> handleCredentialsMismatchException(CredentialMismatchException e)
    {
        return new ResponseEntity<>(e.getMessage(),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidUserInput.class)
    public ResponseEntity<String> handleInvalidUserInput(InvalidUserInput e)
    {
        return new ResponseEntity<>(e.getMessage(),HttpStatus.UNPROCESSABLE_CONTENT);
    }


    /*
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException e){
        return new ResponseEntity<>(e.getMessage(),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SessionNotExistException.class)
    public ResponseEntity<String> handleSessionNotExistException(SessionNotExistException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
    */
}
