package dev.aditya.userauthservice.Service;

import dev.aditya.userauthservice.Exceptions.*;
import dev.aditya.userauthservice.Model.*;
import dev.aditya.userauthservice.Repository.RoleRepository;
import dev.aditya.userauthservice.Repository.SessionRepository;
import dev.aditya.userauthservice.Repository.UserRepository;
import dev.aditya.userauthservice.Validation.ServiceValidator;
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
public class UserAuthService implements IUserAuthService {

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

    @Autowired
    private ServiceValidator serviceValidator;


    @Override
    public User signup(String name, String email, String password, String dateOfBirth, String phoneNumber,
                       String address, String role) throws UserAlreadyExistsException, DataFormatException {

        serviceValidator.validateNewUser(email);
        User newUser = buildNewUserFromParams(name, email, password, true,
                                              convertLocalDateFromString(dateOfBirth),phoneNumber, address, role);
        return userRepository.save(newUser);
    }

    @Override
    public Session login(String email, String password) throws UserNotFoundException, CredentialMismatchException {

        User existingUser = serviceValidator.validateExistingUser(email);
        serviceValidator.validateUserPassword(password,existingUser);
        Session newSession = buildNewSession(existingUser);

        return sessionRepository.save(newSession);
    }

    @Override
    public Session logout(UUID refreshTokenId) {
        Session existingSession = sessionRepository.findByRefreshTokenId(refreshTokenId).get();   //validateSession(refreshToken,TokenType.REFRESH);
        existingSession.setCurrentStatus(Status.DELETED);
        return sessionRepository.save(existingSession);
    }

    @Override
    public Session refresh(UUID refreshTokenId){
        //Claims claims = validateToken(refreshToken,TokenType.REFRESH);
        Session existingSession = sessionRepository.findByRefreshTokenId(refreshTokenId).get();//validateSession(refreshToken,TokenType.REFRESH);
        Session newSession = buildNewSession(existingSession.getUser());//claims.getSubject()));
        newSession.setId(existingSession.getId());
        return sessionRepository.save(newSession);
    }

    @Override
    public User viewUserProfile(String email) throws UserNotFoundException {
        User existingUser = serviceValidator.validateExistingUser(email);
        return existingUser;
    }


    @Override
    public User updateUserProfile(String currentEmail, String name, String email, String dateOfBirth, String phoneNumber,
                                  String address, String role) throws UserNotFoundException, DataFormatException {

        User existinguser = serviceValidator.validateExistingUser(currentEmail);
        User newUser = buildNewUserFromParams(name, email, existinguser.getPassword(),
                                false, convertLocalDateFromString(dateOfBirth),phoneNumber,address,role);
        newUser.setId(existinguser.getId());

        return userRepository.save(newUser);
    }

    @Override
    public User resetPassword(String email, String password) throws UserNotFoundException, DataFormatException {
        User existingUser = serviceValidator.validateExistingUser(email);
        User newUser = buildNewUserFromParams(existingUser.getName(), existingUser.getEmail(), password, true
                                              ,existingUser.getDateOfBirth()
                                              ,existingUser.getPhoneNumber(), existingUser.getAddress()
                                              ,existingUser.getRoles().getFirst().getRoleName().toString());
        newUser.setId(existingUser.getId());
        userRepository.save(newUser);
        List<Session> activeSessions = sessionRepository.findAllByUserAndCurrentStatus(existingUser, Status.ACTIVE);
        for (Session session : activeSessions) {
            session.setCurrentStatus(Status.DELETED);
            sessionRepository.save(session);
        }
        return newUser;
    }


    //HELPER METHODS

    //helper method to create a new user from DTO parameters
    private User buildNewUserFromParams(String name, String email, String password, Boolean encodePassword, LocalDate dateOfBirth,
                                        String phoneNumber, String address, String roleName) throws DataFormatException {

        User newUser = User.builder()
                .setName(name)
                .setEmail(email)
                .setPassword(encodePassword ? bCryptPasswordEncoder.encode(password) : password)
                .setDateOfBirth(dateOfBirth)
                .setPhoneNumber(phoneNumber)
                .setAddress(address)
                .addRoles(getRoleFromDB(roleName))
                .build();
        return newUser;
    }

    //helper to find whether a role exists inm a db or not and return
    private Role getRoleFromDB(String roleName) {
        Optional<Role> optionalRole = roleRepository.findRoleByRoleName(RoleName.valueOf(roleName.toUpperCase()));
        if (optionalRole.isPresent() && optionalRole.get().getCurrentStatus().equals(Status.ACTIVE)) {
            return optionalRole.get();
        }
        Role newRole = new Role();
        newRole.setRoleName(RoleName.valueOf(roleName.toUpperCase()));
        return roleRepository.save(newRole);
    }

    //helper function to create a local date from string
    private LocalDate convertLocalDateFromString(String dateOfBirth) {
//        if (dateOfBirth.length() > 10) {
//            throw new DataFormatException("Invalid Date. Please enter proper Date and try again!");
//        } this is not required as the parser function below checks for everything and will throw DateTimeParseException
        dateOfBirth = dateOfBirth.replace(" ","");
        LocalDate dob = LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return dob;
    }

    //helper method for creation of a new session on every login,refresh
    private Session buildNewSession(User user) {
        Session session = new Session();
        session.setRefreshTokenId(UUID.randomUUID());
        session.setAuthToken(generateJWT(TokenType.AUTH, user,session.getRefreshTokenId()));
        session.setRefreshToken(generateJWT(TokenType.REFRESH, user,session.getRefreshTokenId()));
        session.setUser(user);
        return session;
    }

    //helper for creating a JWT based on Token type
    private String generateJWT(TokenType tokenType, User user,UUID refreshSessionId) {
        Date today = new Date();
        long expiryInMS;
        Date expiryDate = new Date();
        String token;
        //Switch case fails for some reason
        if (tokenType.equals(TokenType.AUTH)) {
            expiryInMS = today.getTime() + 10 * 60 * 1000; //10 mins validity
            expiryDate.setTime(expiryInMS);
            token = Jwts.builder()
                    .subject(user.getEmail())
                    .claim("User-Id:", user.getId())
                    .claim("Name: ", user.getName())
                    .claim("Email:", user.getEmail())
                    .claim("Roles: ", user.getRoles().toString())
                    .issuer("Amazon-Copy.com")
                    .issuedAt(today)
                    .expiration(expiryDate)
                    .signWith(secretKey)
                    .compact();
        } else {
            expiryInMS = today.getTime() + 7 * 24 * 60 * 60 * 1000; //7 days validity
            expiryDate.setTime(expiryInMS);
            token = Jwts.builder()
                    .subject(user.getEmail())
                    .id(String.valueOf(refreshSessionId))
                    .issuedAt(today)
                    .expiration(expiryDate)
                    .signWith(secretKey)
                    .compact();

        }
        return token;
    }

}


/*
    //Validates the incoming Token and returns a proper valid claim
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
        if(existingSession.isEmpty() ){//|| existingSession.get().getCurrentStatus()== Status.DELETED){
            throw new SessionNotExistException("Session doesn't exist! Please Login again!!");
        }
        return existingSession.get();
    }

*/
