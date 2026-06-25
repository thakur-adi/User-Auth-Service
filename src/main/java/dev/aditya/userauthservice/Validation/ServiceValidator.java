package dev.aditya.userauthservice.Validation;

import dev.aditya.userauthservice.Exceptions.CredentialMismatchException;
import dev.aditya.userauthservice.Exceptions.UserAlreadyExistsException;
import dev.aditya.userauthservice.Exceptions.UserNotFoundException;
import dev.aditya.userauthservice.Model.Status;
import dev.aditya.userauthservice.Model.User;
import dev.aditya.userauthservice.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


import java.util.Optional;

@Component
public class ServiceValidator {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //Validating whether User even exists or not
    public User validateExistingUser(String email) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty() || optionalUser.get().getCurrentStatus().equals(Status.DELETED)) {
            throw new UserNotFoundException("User with email:" + email + " not found or has been Banned! Please Signup first then continue!!");
        }
        return optionalUser.get();
    }

    //Validating whether User already exists
    public void validateNewUser(String email) throws UserAlreadyExistsException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException("Seems like Email: '" + email + "' has already registered. \n\nPlease log in using registered Email and Password\n OR \nuse another Email!!");
        }
    }

    //Validate User's Password
    public void validateUserPassword(String password, User existingUser) throws CredentialMismatchException {
        if (!bCryptPasswordEncoder.matches(password, existingUser.getPassword())) {
            throw new CredentialMismatchException("Wrong Email-Id or Password. Please try again!!");
        }
        else {return;}
    }
}

