package dev.aditya.userauthservice.Controller;

import dev.aditya.userauthservice.Dto.*;
import dev.aditya.userauthservice.Exceptions.*;
import dev.aditya.userauthservice.Model.Session;
import dev.aditya.userauthservice.Model.TokenType;
import dev.aditya.userauthservice.Model.User;
import dev.aditya.userauthservice.Service.IUserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/user")
public class UserAuthController {

    @Autowired
    IUserAuthService userAuthService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signupUser(@RequestBody SignupRequestDTO signupRequestDTO)
                                                        throws UserAlreadyExistsException, DataFormatException {

        User newUser = userAuthService.signup(signupRequestDTO.getName(),signupRequestDTO.getEmail(),signupRequestDTO.getPassword(),
                                              signupRequestDTO.getDateOfBirth(),signupRequestDTO.getPhoneNumber(),
                                              signupRequestDTO.getAddress(),signupRequestDTO.getRole());

        SignupResponseDTO newSignupResponseDTO = new SignupResponseDTO();
        newSignupResponseDTO.convertToDtoFrom(newUser);

        return new ResponseEntity<>(newSignupResponseDTO,HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDTO loginRequestDTO)
                                                        throws UserNotFoundException, CredentialMismatchException {
        Session newSession = userAuthService.login(loginRequestDTO.getEmail(),loginRequestDTO.getPassword());

        HttpHeaders newHeader = buildHeaderForTokens(newSession.getAuthToken(),newSession.getRefreshToken(),1);

        /*LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.convertToDtoFromSession(newSession);*/
        return new ResponseEntity<>("Welcome Back "+ newSession.getUser().getName() +"! How can we serve you today?"
                                    ,newHeader,HttpStatus.OK);

    }

    @PostMapping("/auth/logout")
    public ResponseEntity<String> logoutUser(@CookieValue(name = "refreshToken") String refreshToken)
                                                throws UserNotFoundException, SessionNotExistException {

        Session session = userAuthService.logout(refreshToken);

        HttpHeaders newHeader = buildHeaderForTokens("", "",0);

        return new ResponseEntity<>("GoodeBye "+session.getUser().getName()+"!! Hope to see you soon!",newHeader,HttpStatus.OK);

    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<String> refreshToken(@CookieValue(name = "refreshToken") String refreshToken)
                                    throws SessionNotExistException, InvalidTokenException, UserNotFoundException {
        Session session = userAuthService.refresh(refreshToken);
        HttpHeaders newHeader = buildHeaderForTokens(session.getAuthToken(),session.getRefreshToken(),1);
//        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO();
//        refreshTokenDTO.convertToDtoFrom(session);
        return new ResponseEntity<>("Tokens have been generated please continue!",newHeader,HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDTO> viewUserProfile(@PathVariable String userEmail ){

        return null;

    }
    @PutMapping("/profile")
    public ResponseEntity<ProfileResponseDTO> updateUserProfile(@RequestBody ProfileRequestDTO profileRequestDTO){

        return null;

    }
    @PostMapping("/reset")
    public ResponseEntity<String> resetUserPassword(@RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO){

        return null;

    }
    
    
    // Helper methods

    private HttpHeaders buildHeaderForTokens(String authToken, String refreshToken,long maxAgeDaysCount){

        //String nameOfCookie = (tokenType==TokenType.REFRESH)?"refreshToken":"authToken";

        ResponseCookie responseCookie = ResponseCookie.from("refreshToken",refreshToken)
                                                      .httpOnly(Boolean.TRUE)
                                                      .secure(Boolean.TRUE)
                                                      .sameSite("strict")
                                                      .path("/user/auth")
                                                      //This is seconds not milliseconds
                                                      .maxAge(maxAgeDaysCount*24*60*60)
                                                      .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.SET_COOKIE,responseCookie.toString());
        httpHeaders.setBearerAuth(authToken);
        return httpHeaders;
    }
}
