package dev.aditya.userauthservice.Service;

import dev.aditya.userauthservice.Exceptions.UserAlreadyExistsException;
import dev.aditya.userauthservice.Model.User;

import java.util.zip.DataFormatException;

public interface IUserAuthService {

    User signUp(String name, String email, String password, String dateOfBirth,String phoneNumber, String address, String role) throws UserAlreadyExistsException, DataFormatException;

}
