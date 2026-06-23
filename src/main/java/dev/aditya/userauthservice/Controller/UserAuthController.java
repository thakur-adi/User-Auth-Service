package dev.aditya.userauthservice.Controller;

import dev.aditya.userauthservice.Dto.*;
import dev.aditya.userauthservice.Exceptions.*;
import dev.aditya.userauthservice.Model.Session;
import dev.aditya.userauthservice.Model.TokenType;
import dev.aditya.userauthservice.Model.User;
import dev.aditya.userauthservice.Service.IUserAuthService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.zip.DataFormatException;

@RestController
//@RequestMapping("/user") //Not required anymore since this has been set as context path, adding this would make url -> /user/user/login (context path + servlet path)
public class UserAuthController {

    @Autowired
    IUserAuthService userAuthService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signupUser(@RequestBody SignupRequestDTO signupRequestDTO)
                                               throws UserAlreadyExistsException, DataFormatException
    {

        User newUser = userAuthService.signup(signupRequestDTO.getName(),signupRequestDTO.getEmail(),signupRequestDTO.getPassword(),
                                              signupRequestDTO.getDateOfBirth(),signupRequestDTO.getPhoneNumber(),
                                              signupRequestDTO.getAddress(),signupRequestDTO.getRole());

        SignupResponseDTO newSignupResponseDTO = new SignupResponseDTO();
        newSignupResponseDTO.convertToDtoFrom(newUser);

        return new ResponseEntity<>(newSignupResponseDTO,HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDTO loginRequestDTO)
                                    throws UserNotFoundException, CredentialMismatchException
    {
        Session newSession = userAuthService.login(loginRequestDTO.getEmail(),loginRequestDTO.getPassword());

        HttpHeaders newHeader = buildHeaderFromCookies("refreshToken",newSession.getRefreshToken(),1*24*60*60);
        newHeader.setBearerAuth(newSession.getAuthToken());
        return new ResponseEntity<>("Welcome Back "+ newSession.getUser().getName() +"! How can we serve you today?"
                                    ,newHeader,HttpStatus.OK);

    }


    @PostMapping("/auth/logout")
    public ResponseEntity<String> logoutUser(@CookieValue(name = "refreshToken") String refreshToken)
                                     throws UserNotFoundException, SessionNotExistException
    {

        Session session = userAuthService.logout(refreshToken);

        HttpHeaders newHeader = buildHeaderFromCookies("", "",0);
        newHeader.setBearerAuth("");
        return new ResponseEntity<>("GoodeBye "+session.getUser().getName()+"!! Hope to see you soon!",newHeader,HttpStatus.OK);

    }


    @PostMapping("/auth/refresh")
    public ResponseEntity<String> refreshToken(@CookieValue(name = "refreshToken") String refreshToken)
                                    throws SessionNotExistException, InvalidTokenException, UserNotFoundException
    {
        Session session = userAuthService.refresh(refreshToken);
        HttpHeaders newHeader = buildHeaderFromCookies("refreshToken",session.getRefreshToken(),1*24*60*60);
        newHeader.setBearerAuth(session.getAuthToken());
        return new ResponseEntity<>("Tokens have been generated please continue!",newHeader,HttpStatus.CREATED);
    }


    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDTO> viewUserProfile()//@RequestHeader(HttpHeaders.AUTHORIZATION) String authToken)
                                                throws UserNotFoundException, InvalidTokenException
    {
        Claims claims = (Claims) SecurityContextHolder.getContext().getAuthentication().getPrincipal();//userAuthService.validateToken(authToken, TokenType.AUTH);
        User existingUser = userAuthService.viewUserProfile(claims.getSubject());
        ProfileResponseDTO profileResponseDTO = new ProfileResponseDTO();
        profileResponseDTO.convertToDtoFrom(existingUser);
        return new ResponseEntity<>(profileResponseDTO,HttpStatus.OK);
    }


    @PutMapping("/profile")
    public ResponseEntity<ProfileResponseDTO> updateUserProfile(//@RequestHeader(HttpHeaders.AUTHORIZATION) String authToken,
                                                                @RequestBody ProfileUpdateRequestDTO profileUpdateRequestDTO)
                                                throws InvalidTokenException, UserNotFoundException, DataFormatException
    {
        Claims claims =(Claims) SecurityContextHolder.getContext().getAuthentication().getPrincipal();// userAuthService.validateToken(authToken,TokenType.AUTH);
        User newUser = userAuthService.updateUserProfile(claims.getSubject(), profileUpdateRequestDTO.getName()
                                                         , profileUpdateRequestDTO.getEmail(), profileUpdateRequestDTO.getDateOfBirth()
                                                         , profileUpdateRequestDTO.getPhoneNumber(), profileUpdateRequestDTO.getAddress()
                                                         , profileUpdateRequestDTO.getRole());
        ProfileResponseDTO profileResponseDTO=new ProfileResponseDTO();
        profileResponseDTO.convertToDtoFrom(newUser);

        return new ResponseEntity<>(profileResponseDTO,HttpStatus.OK);

    }



    @PutMapping("/reset")
    public ResponseEntity<String> resetUserPassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String authToken,
                                                    @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO)
                                    throws InvalidTokenException, UserNotFoundException, DataFormatException, SessionNotExistException
    {
        Claims claims =  (Claims) SecurityContextHolder.getContext().getAuthentication().getPrincipal();//userAuthService.validateToken(authToken,TokenType.AUTH);
        User newUser = userAuthService.resetPassword(claims.getSubject(), resetPasswordRequestDTO.getPassword());

        HttpHeaders newHeader = buildHeaderFromCookies( "","",0);
        newHeader.setBearerAuth("");
        return new ResponseEntity<>("Your password has been reset "+newUser.getName()+"! Please Login again!",newHeader,HttpStatus.OK);
    }





    // Helper methods

    private HttpHeaders buildHeaderFromCookies(String cookieName, String cookieTokenValue,long cookieExpiryAge)
    {

        ResponseCookie responseCookie = ResponseCookie.from(cookieName,cookieTokenValue)
                                                      .httpOnly(Boolean.TRUE)
                                                      .secure(Boolean.TRUE)
                                                      .sameSite("strict") // This acts as a very basic modern CSRF protection
                                                      .path("/user/auth") //This should always include the whole path -> context path + servlet path + .....
                                                      //This is seconds not milliseconds
                                                      .maxAge(cookieExpiryAge)//1->future date,0->delete,-1->deleted on every browser close
                                                      .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.SET_COOKIE,responseCookie.toString());
        return httpHeaders;
    }

}
