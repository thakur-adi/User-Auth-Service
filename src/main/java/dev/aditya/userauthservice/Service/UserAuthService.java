package dev.aditya.userauthservice.Service;

import dev.aditya.userauthservice.Exceptions.UserAlreadyExistsException;
import dev.aditya.userauthservice.Model.Role;
import dev.aditya.userauthservice.Model.RoleFactory;
import dev.aditya.userauthservice.Model.User;
import dev.aditya.userauthservice.Repository.RoleRepository;
import dev.aditya.userauthservice.Repository.SessionRepository;
import dev.aditya.userauthservice.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;

@Service
public class UserAuthService implements IUserAuthService{

    @Autowired
    private SessionRepository sessionRepo;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public User signUp(String name, String email, String password, String dateOfBirth, String phoneNumber, String address, String role) throws UserAlreadyExistsException, DataFormatException {

        if(userRepository.findByEmail(email).isPresent())
        {
            throw new UserAlreadyExistsException("Email already registered. Please log in using registered Email and Password!!");
        }

        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(bCryptPasswordEncoder.encode(password));
        newUser.setDateOfBirth(convertLocalDateFromString(dateOfBirth));
        newUser.setPhoneNumber(phoneNumber);
        newUser.setAddress(address);
        Role role1 = roleRepository.save(RoleFactory.createRole(role));
        newUser.setRoles(List.of(role1));


        return userRepository.save(newUser);
    }

    //helper function to create a local date from string
    LocalDate convertLocalDateFromString(String dateOfBirth) throws DataFormatException {
        if(dateOfBirth.length()>10){
            throw new DataFormatException("Invalid Date. Please enter proper Date and try again!");
        }
        LocalDate dob = LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return dob;}

}
