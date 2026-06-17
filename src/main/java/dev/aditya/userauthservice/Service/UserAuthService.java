package dev.aditya.userauthservice.Service;

import dev.aditya.userauthservice.Exceptions.CredentialMismatchException;
import dev.aditya.userauthservice.Exceptions.SessionNotExistException;
import dev.aditya.userauthservice.Exceptions.UserAlreadyExistsException;
import dev.aditya.userauthservice.Exceptions.UserNotFoundException;
import dev.aditya.userauthservice.Model.*;
import dev.aditya.userauthservice.Repository.RoleRepository;
import dev.aditya.userauthservice.Repository.SessionRepository;
import dev.aditya.userauthservice.Repository.UserRepository;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.DataFormatException;

@Service
public class UserAuthService implements IUserAuthService{

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SecretKey secretKey;


    @Override
    public User signup(String name, String email, String password, String dateOfBirth, String phoneNumber,
                       String address, String role) throws UserAlreadyExistsException, DataFormatException {

        if(userRepository.findByEmail(email).isPresent())
        {
            throw new UserAlreadyExistsException("Seems like Email: '"+email+"' has already registered. \n\nPlease log in using registered Email and Password\n OR \nuse another Email!!");
        }
        User newUser = buildNewUserFromParams(name,email,password,dateOfBirth,phoneNumber,address,role);
        return userRepository.save(newUser);
    }

    @Override
    public Session login(String email, String password) throws UserNotFoundException, CredentialMismatchException {

        User existingUser = getValidUser(email);

        if (!bCryptPasswordEncoder.matches(password,existingUser.getPassword())) {
            throw new CredentialMismatchException("Wrong Email-Id or Password. Please try again!!");
        }

        Session newSession = new Session();
        newSession.setAuthToken(generateJWT(TokenType.AUTH,existingUser));
        newSession.setRefreshToken(generateJWT(TokenType.REFRESH, existingUser));
        newSession.setUser(getValidUser(email));

        return sessionRepository.save(newSession);
    }

    @Override
    public Session logout(String email, String authToken) throws UserNotFoundException, SessionNotExistException {
        User existingUser = getValidUser(email);
        if(sessionRepository.findByAuthToken(authToken).isEmpty() || !sessionRepository.findByAuthToken(authToken).equals(sessionRepository.findByUser(existingUser)) || sessionRepository.findByAuthToken(authToken).get().getCurrentStatus().equals(Status.DELETED))
        {
            throw new SessionNotExistException("The session you are looking for doesn't exist. Please provide proper Email and Token!");
        }
        Session existingSession = sessionRepository.findByAuthToken(authToken).get();
        existingSession.setCurrentStatus(Status.DELETED);
        return sessionRepository.save(existingSession);
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
            if(optionalRole.isPresent() && optionalRole.get().getCurrentStatus().equals(Status.ACTIVE)){
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

    //helper for creating a JWT based on Token type
    private String generateJWT(TokenType tokenType, User user){
        Date today = new Date();
        long expiryInMS;
        Date expiryDate = new Date();
        String token;
        //Switch case fails for some reason
        if(tokenType.equals(TokenType.AUTH)){
                expiryInMS= today.getTime()+10*60*1000; //10 mins validity
                expiryDate.setTime(expiryInMS);
                token  = Jwts.builder()
                            .subject(user.getEmail())
                            .claim("User-Id:",user.getId())
                            .claim("Name: ",user.getName())
                            .claim("Email:",user.getEmail())
                            .claim("Roles: ",user.getRoles().toString())
                            .issuer("Ecommerce.com")
                            .issuedAt(new Date())
                            .expiration(expiryDate)
                            .signWith(secretKey)
                            .compact();
        }
        else{
            expiryInMS= today.getTime()+7*24*60*60*1000; //7 days validity
            expiryDate.setTime(expiryInMS);
            token = Jwts.builder()
                    .subject(user.getEmail())
                    .id(UUID.randomUUID().toString())
                    .issuedAt(today)
                    .expiration(expiryDate)
                    .signWith(secretKey)
                    .compact();

        }
        return token;
        }

    //Validation helper as Session table contains a reference for User object which will fail at runtime as Hibernate expects a validity check for nested objects
    private User getValidUser(String email) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty() || optionalUser.get().getCurrentStatus().equals(Status.DELETED)) {
            throw new UserNotFoundException("User with email:"+email+"not found or has been Banned! Please Signup first then continue!!");
        }
        return optionalUser.get();
    }

    //helper method for creation of a new session on every login,refresh

}
