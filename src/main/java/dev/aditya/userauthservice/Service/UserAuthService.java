package dev.aditya.userauthservice.Service;

import dev.aditya.userauthservice.Exceptions.*;
import dev.aditya.userauthservice.Model.*;
import dev.aditya.userauthservice.Repository.RoleRepository;
import dev.aditya.userauthservice.Repository.SessionRepository;
import dev.aditya.userauthservice.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

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
        User newUser = buildNewUserFromParams(name,email,password,true,dateOfBirth,phoneNumber,address,role);
        return userRepository.save(newUser);
    }

    @Override
    public Session login(String email, String password) throws UserNotFoundException, CredentialMismatchException {

        User existingUser = validateUserIsEmpty(email);

        if (!bCryptPasswordEncoder.matches(password,existingUser.getPassword())) {
            throw new CredentialMismatchException("Wrong Email-Id or Password. Please try again!!");
        }
        Session newSession = buildNewSession(existingUser);
        return sessionRepository.save(newSession);
    }

    @Override
    public Session logout(String refreshToken) throws SessionNotExistException {
        Session existingSession = validateSession(refreshToken,TokenType.REFRESH);
        existingSession.setCurrentStatus(Status.DELETED);
        return sessionRepository.save(existingSession);
    }

    @Override
    public Session refresh(String refreshToken) throws SessionNotExistException, InvalidTokenException, UserNotFoundException {
        Claims claims = validateToken(refreshToken,TokenType.REFRESH);
        Session existingSession  = validateSession(refreshToken,TokenType.REFRESH);
        Session newSession = buildNewSession(validateUserIsEmpty(claims.getSubject()));
        newSession.setId(existingSession.getId());
        return sessionRepository.save(newSession);
    }

    @Override
    public User viewUserProfile(String email) throws UserNotFoundException {
        User existingUser = validateUserIsEmpty(email);
        return existingUser;
    }


    @Override
    public User updateUserProfile(String currentEmail, String name, String email, String dateOfBirth, String phoneNumber,
                                  String address, String role) throws UserNotFoundException, DataFormatException {
        User existinguser = validateUserIsEmpty(currentEmail);
        User newUser = buildNewUserFromParams(name, email, existinguser.getPassword(),false, dateOfBirth, phoneNumber, address, role);
        newUser.setId(existinguser.getId());
        return userRepository.save(newUser);
    }

    @Override
    public void resetPassword(String authToken, String email, String password) throws UserNotFoundException, DataFormatException, SessionNotExistException {
        User existingUser = validateUserIsEmpty(email);
        User newUser = buildNewUserFromParams(existingUser.getName(), existingUser.getEmail(), password,true
                                             ,existingUser.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                             ,existingUser.getPhoneNumber(),existingUser.getAddress()
                                             ,existingUser.getRoles().getFirst().getRoleName().toString());
        userRepository.save(newUser);
        Session existingSession = validateSession(authToken,TokenType.AUTH);
        existingSession.setCurrentStatus(Status.DELETED);
        sessionRepository.save(existingSession);

    }





    //HELPER METHODS

    //helper method to create a new user from DTO parameters
    private User buildNewUserFromParams(String name, String email, String password,Boolean encodePassword, String dateOfBirth,
                                         String phoneNumber, String address, String roleName) throws DataFormatException {

        User newUser = User.builder()
                        .setName(name)
                        .setEmail(email)
                        .setPassword(encodePassword?bCryptPasswordEncoder.encode(password):password)
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
    private User validateUserIsEmpty(String email) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty() || optionalUser.get().getCurrentStatus().equals(Status.DELETED)){
            throw new UserNotFoundException("User with email:"+email+" not found or has been Banned! Please Signup first then continue!!");
        }
        return optionalUser.get();
    }

    //helper method for creation of a new session on every login,refresh
    private Session buildNewSession(User existingUser){
        Session session = new Session();
        session.setAuthToken(generateJWT(TokenType.AUTH,existingUser));
        session.setRefreshToken(generateJWT(TokenType.REFRESH, existingUser));
        session.setUser(existingUser);
        return session;
    }

    //Validates the incoming Token and returns a proper valid claim
    @Override
    public Claims validateToken(String token,TokenType tokenType) throws InvalidTokenException {
        try{
            if (!token.startsWith("Bearer ")){
                throw new InvalidTokenException("Token provided is Invalid. Please try again!");
            }
            String authToken = token.substring(7);
            Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(authToken).getPayload();
            String email = claims.getSubject();
            if(claims.isEmpty() || email == null ){
                throw new InvalidTokenException("Token provided is Invalid. Please try again!");
            }

            validateSession(authToken, tokenType);
            return claims;
        }
        catch(SessionNotExistException e){
            throw new InvalidTokenException("Token provided is Invalid. Please try again!");
        }
    }

    //validates the session by different token types
    private Session validateSession(String token, TokenType tokenType) throws SessionNotExistException {
        Optional<Session> existingSession;
        if(tokenType == TokenType.REFRESH){
            existingSession= sessionRepository.findByRefreshToken(token);
        }
        else{
             existingSession = sessionRepository.findByAuthToken(token);
        }
        if(existingSession.isEmpty() || existingSession.get().getCurrentStatus()== Status.DELETED){
            throw new SessionNotExistException("Session doesn't exist! Please Login again!!");
        }
        return existingSession.get();
    }

}
