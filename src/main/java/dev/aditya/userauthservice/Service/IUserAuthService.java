package dev.aditya.userauthservice.Service;

import dev.aditya.userauthservice.Exceptions.*;
import dev.aditya.userauthservice.Model.Session;
import dev.aditya.userauthservice.Model.User;

import java.util.UUID;
import java.util.zip.DataFormatException;

public interface IUserAuthService {

    User signup(String name, String email, String password, String dateOfBirth, String phoneNumber, String address, String role) throws UserAlreadyExistsException, DataFormatException;

    Session login(String email, String password) throws UserNotFoundException, CredentialMismatchException;

    Session logout(UUID refreshTokenId) throws UserNotFoundException, SessionNotExistException;

    Session refresh(UUID refreshTokenId) throws SessionNotExistException, InvalidTokenException, UserNotFoundException;

    User viewUserProfile(String email) throws UserNotFoundException;

    User updateUserProfile(String currentEmail, String name, String email, String dateOfBirth, String phoneNumber, String address, String role) throws UserNotFoundException, DataFormatException;

    User resetPassword(String email,String password) throws UserNotFoundException, DataFormatException, SessionNotExistException;

}
