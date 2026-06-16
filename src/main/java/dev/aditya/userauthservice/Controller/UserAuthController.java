package dev.aditya.userauthservice.Controller;

import dev.aditya.userauthservice.Dto.*;
import dev.aditya.userauthservice.Exceptions.UserAlreadyExistsException;
import dev.aditya.userauthservice.Model.User;
import dev.aditya.userauthservice.Service.IUserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/user")
public class UserAuthController {

    @Autowired
    IUserAuthService userAuthService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signupUser(@RequestBody SignupRequestDTO signupRequestDTO) throws UserAlreadyExistsException, DataFormatException {

        User newUser = userAuthService.signUp(signupRequestDTO.getName(),signupRequestDTO.getEmail(),signupRequestDTO.getPassword(),
                                              signupRequestDTO.getDateOfBirth(),signupRequestDTO.getPhoneNumber(),
                                              signupRequestDTO.getAddress(),signupRequestDTO.getRole());

        SignupResponseDTO newSignupResponseDTO = new SignupResponseDTO();
        newSignupResponseDTO.convertToDtoFrom(newUser);

        return new ResponseEntity<>(newSignupResponseDTO,HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequestDTO){

        return null;

    }
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestBody LogoutRequestDTO logoutRequestDTO){

        return null;

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
}
