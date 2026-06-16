package dev.aditya.userauthservice.Service;

import dev.aditya.userauthservice.Exceptions.UserAlreadyExistsException;
import dev.aditya.userauthservice.Model.Role;
import dev.aditya.userauthservice.Model.RoleName;
import dev.aditya.userauthservice.Model.User;
import dev.aditya.userauthservice.Repository.RoleRepository;
import dev.aditya.userauthservice.Repository.SessionRepository;
import dev.aditya.userauthservice.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public User signUp(String name, String email, String password, String dateOfBirth, String phoneNumber,
                       String address, String role) throws UserAlreadyExistsException, DataFormatException {

        if(userRepository.findByEmail(email).isPresent())
        {
            throw new UserAlreadyExistsException("Email: "+email+" already registered. Please log in using registered Email and Password!!");
        }
        return userRepository.save(buildNewUserFromParams(name,email,password,dateOfBirth,phoneNumber,address,role));
    }


    //HELPER METHODS

    //helper method to create a new user from DTO parameters
    private User buildNewUserFromParams(String name, String email, String password, String dateOfBirth,
                                         String phoneNumber, String address, String roleName) throws DataFormatException {

        User newUser = User.builder()
                        .setName(name)
                        .setEmail(email)
                        .setPassword(bCryptPasswordEncoder.encode(password))
                        .setDateOfBirth(convertLocalDateFromString(dateOfBirth))
                        .setPhoneNumber(phoneNumber)
                        .setAddress(address)
                        .addRoles(getRoleFromDB(roleName))
                        .build();
        return newUser;
    }

    //helper to find whether a role exists inm a db or not and return
    private Role getRoleFromDB(String roleName){
            Optional<Role> optionalRole = roleRepository.findRoleByRoleName(RoleName.valueOf(roleName.toUpperCase()));
            if(optionalRole.isPresent()){
                return optionalRole.get();
            }
            Role newRole = new Role();
            newRole.setRoleName(RoleName.valueOf(roleName.toUpperCase()));
            return roleRepository.save(newRole);
        }


    //helper function to create a local date from string
    private LocalDate convertLocalDateFromString(String dateOfBirth) throws DataFormatException {
        if(dateOfBirth.length()>10){
            throw new DataFormatException("Invalid Date. Please enter proper Date and try again!");
        }
        LocalDate dob = LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return dob;
    }

}
