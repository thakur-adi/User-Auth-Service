package dev.aditya.userauthservice.Controller;

import dev.aditya.userauthservice.Dto.*;
import dev.aditya.userauthservice.Exceptions.*;
import dev.aditya.userauthservice.Model.Session;
import dev.aditya.userauthservice.Model.User;
import dev.aditya.userauthservice.Service.IUserAuthService;
import dev.aditya.userauthservice.Validation.ControllerValidator;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.DataFormatException;

@RestController
//@RequestMapping("/user") //Not required anymore since this has been set as context path, adding this would make url -> /user/user/login (context path + servlet path)
public class UserAuthController {

    @Autowired
    IUserAuthService userAuthService;

    @Autowired
    ControllerValidator controllerValidator;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signupUser(@RequestBody SignupRequestDTO signupRequestDTO)
                                               throws UserAlreadyExistsException, DataFormatException
    {
        User newUser = userAuthService.signup(controllerValidator.basicStringValidationChecks("Name",signupRequestDTO.getName())
                                              ,controllerValidator.validateEmail(signupRequestDTO.getEmail())
                                              ,controllerValidator.basicStringValidationChecks("Password",signupRequestDTO.getPassword())
                                              ,signupRequestDTO.getDateOfBirth()
                                              ,controllerValidator.validatePhoneNumber(signupRequestDTO.getPhoneNumber())
                                              ,controllerValidator.basicStringValidationChecks("Address",signupRequestDTO.getAddress())
                                              ,controllerValidator.validateRole(signupRequestDTO.getRole()));

        SignupResponseDTO newSignupResponseDTO = new SignupResponseDTO();
        newSignupResponseDTO.convertToDtoFrom(newUser);

        return new ResponseEntity<>(newSignupResponseDTO,HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDTO loginRequestDTO)
                                    throws UserNotFoundException, CredentialMismatchException
    {
        Session newSession = userAuthService.login(controllerValidator.validateEmail(loginRequestDTO.getEmail())
                                                   ,controllerValidator.basicStringValidationChecks("Password",loginRequestDTO.getPassword())
                                                   ,getUserHttpRequestDetails());

        HttpHeaders newHeader = buildHeaderFromCookies("refreshToken",newSession.getRefreshToken(),1*24*60*60);
        newHeader.setBearerAuth(newSession.getAuthToken());
        return new ResponseEntity<>("Welcome Back "+ newSession.getUser().getName() +"! How can we serve you today?"
                                    ,newHeader,HttpStatus.OK);

    }


    @PostMapping("/ref/logout")
    public ResponseEntity<String> logoutUser() throws UserNotFoundException, SessionNotExistException
        /*public ResponseEntity<String> logoutUser(@CookieValue(name = "refreshToken") String refreshToken)
        * Used earlier before moving to central/filter based authentication*/
    {
        Claims claims = (Claims) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Session session = userAuthService.logout(UUID.fromString(claims.getId()));

        HttpHeaders newHeader = buildHeaderFromCookies("refreshToken", "",0);
        newHeader.setBearerAuth("");

        return new ResponseEntity<>("GoodeBye "+session.getUser().getName()+"!! Hope to see you soon!",newHeader,HttpStatus.OK);

    }


    @PostMapping("/ref/refresh")
    public ResponseEntity<String> refreshToken()  throws SessionNotExistException, InvalidTokenException, UserNotFoundException
        /* public ResponseEntity<String> refreshToken(@CookieValue(name = "refreshToken") String refreshToken)
        used earlier before moving to central/filter based authentication*/
    {
        Claims claims = (Claims) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Session session = userAuthService.refresh(UUID.fromString(claims.getId()));

        HttpHeaders newHeader = buildHeaderFromCookies("refreshToken",session.getRefreshToken(),1*24*60*60);
        newHeader.setBearerAuth(session.getAuthToken());

        return new ResponseEntity<>("Tokens have been generated please continue!",newHeader,HttpStatus.CREATED);
    }

    //This handles validation for other microservices, if it reaches controller that means Auth token is valid
    @PostMapping("/validate")
    public ResponseEntity<String> validateToken(){
        Claims claims = (Claims) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HttpHeaders newHeader = new HttpHeaders();
        newHeader.add("X-USER-ID",claims.get("User-Id:").toString());
        newHeader.add("X-USER-EMAIL",claims.getSubject());
        newHeader.add("X-USER-PHONE",claims.get("Phone:").toString());
        newHeader.add("X-USER-NAME",claims.get("Name:").toString());
        newHeader.add("X-USER-ROLES",claims.get("Roles:").toString());
        return new ResponseEntity<>("Successfully Validated!!",newHeader,HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDTO> viewUserProfile() throws UserNotFoundException
        /*@RequestHeader(HttpHeaders.AUTHORIZATION) String authToken)
        * */
    {
        Claims claims = (Claims) SecurityContextHolder.getContext().getAuthentication().getPrincipal();//userAuthService.validateToken(authToken, TokenType.AUTH);
        User existingUser = userAuthService.viewUserProfile(claims.getSubject());
        ProfileResponseDTO profileResponseDTO = new ProfileResponseDTO();
        profileResponseDTO.convertToDtoFrom(existingUser);
        return new ResponseEntity<>(profileResponseDTO,HttpStatus.OK);
    }


    @PutMapping("/profile")
    public ResponseEntity<ProfileResponseDTO> updateUserProfile(@RequestBody ProfileUpdateRequestDTO profileUpdateRequestDTO)
                                                throws UserNotFoundException, DataFormatException
    {
        Claims claims =(Claims) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //userAuthService.validateToken(authToken,TokenType.AUTH);
        User newUser = userAuthService.updateUserProfile(claims.getSubject()
                                                         ,controllerValidator.basicStringValidationChecks("Name",profileUpdateRequestDTO.getName())
                                                         ,controllerValidator.validateEmail(profileUpdateRequestDTO.getEmail())
                                                         ,profileUpdateRequestDTO.getDateOfBirth()
                                                         ,controllerValidator.validatePhoneNumber(profileUpdateRequestDTO.getPhoneNumber())
                                                         ,controllerValidator.basicStringValidationChecks("Address",profileUpdateRequestDTO.getAddress())
                                                         ,controllerValidator.validateRole(profileUpdateRequestDTO.getRole())
                                                         ,getUserHttpRequestDetails());
        ProfileResponseDTO profileResponseDTO=new ProfileResponseDTO();
        profileResponseDTO.convertToDtoFrom(newUser);

        return new ResponseEntity<>(profileResponseDTO,HttpStatus.OK);

    }



    @PutMapping("/reset")
    public ResponseEntity<String> resetUserPassword(@RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO)
                                    throws UserNotFoundException, DataFormatException, SessionNotExistException
    {
        Claims claims =  (Claims) SecurityContextHolder.getContext().getAuthentication().getPrincipal();//userAuthService.validateToken(authToken,TokenType.AUTH);
        User newUser = userAuthService.resetPassword(claims.getSubject()
                                                    ,controllerValidator.basicStringValidationChecks("Password",resetPasswordRequestDTO.getPassword())
                                                    ,getUserHttpRequestDetails());

        HttpHeaders newHeader = buildHeaderFromCookies( "refreshToken","",0);
        newHeader.setBearerAuth("");
        return new ResponseEntity<>("Your password has been reset "+newUser.getName()+"! Please Login again!",newHeader,HttpStatus.OK);
    }





    // Helper methods

    //creates a response cookies and then adds it into headers
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

    private Map<String,String> getUserHttpRequestDetails(){

        Map<String,String> userDetails = new HashMap<>();
    /* Fetch the thread-bound request attributes container
    This .getRequestAttributes() method reaches inside that thread-specific locker and pulls out the container holding,
    all the current request's metadata (headers, cookies, and custom parameters we saved).
    The .getRequestAttributes() method returns a generic, low-level interface (RequestAttributes).
    Because we are working in a standard web application, we explicitly cast it to its web-specific implementation(ServletRequestAttributes).
    This unlocks the .getRequest() method, allowing us to read our custom attributes like "clientPlatform" or "clientIp"
    */
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            /*
            The HttpServletRequest object automatically aggregates all network and protocol metadata sent over the wire [HttpServletRequest (Jakarta Servlet)]
            - Network Properties: Client IP addresses (getRemoteAddr()) and session IDs [HttpServletRequest (Jakarta Servlet)]
            - HTTP Headers: Meta-information like User-Agent, Sec-CH-UA-Platform, or authentication tokens [HttpServletRequest (Jakarta Servlet)]
            - Request Data: The URL path being called, the HTTP method (GET, POST), and query parameters [HttpServletRequest (Jakarta Servlet)].

            Why is it called HttpServletRequest?
            -> - Http: It specifically understands the HTTP/HTTPS protocol layers
               - Servlet: It belongs to the foundational Java Servlet API (jakarta.servlet.http),
                          which forms the core web engine underlying Spring Boot [HttpServletRequest (Jakarta Servlet)].
               - Request: It contains the incoming data from the client, completely distinct from HttpServletResponse
                          (which is what you send back to the client) [HttpServletRequest (Jakarta Servlet)].
             */
            // Extract the actual HttpServletRequest object for this thread
            HttpServletRequest request = attributes.getRequest();

            // Pull the specific values our filter saved earlier
            userDetails.put("clientPlatform",(String) request.getAttribute("clientPlatform"));
            userDetails.put("clientIp",(String) request.getAttribute("clientIp"));
        }
        return userDetails;

    }
}
