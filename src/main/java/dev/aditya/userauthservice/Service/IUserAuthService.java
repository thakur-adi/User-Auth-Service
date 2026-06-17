package dev.aditya.userauthservice.Service;

import dev.aditya.userauthservice.Exceptions.CredentialMismatchException;
import dev.aditya.userauthservice.Exceptions.SessionNotExistException;
import dev.aditya.userauthservice.Exceptions.UserAlreadyExistsException;
import dev.aditya.userauthservice.Exceptions.UserNotFoundException;
import dev.aditya.userauthservice.Model.Session;
import dev.aditya.userauthservice.Model.User;

import java.util.zip.DataFormatException;

public interface IUserAuthService {

    User signup(String name, String email, String password, String dateOfBirth, String phoneNumber, String address, String role) throws UserAlreadyExistsException, DataFormatException;

    Session login(String email, String password) throws UserNotFoundException, CredentialMismatchException;


    Session logout(String email, String authToken) throws UserNotFoundException, SessionNotExistException;
}
