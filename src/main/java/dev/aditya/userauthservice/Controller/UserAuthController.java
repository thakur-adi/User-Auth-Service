package dev.aditya.userauthservice.Controller;

import dev.aditya.userauthservice.Dto.SignupRequestDTO;
import dev.aditya.userauthservice.Dto.SignupResponseDTO;
import dev.aditya.userauthservice.Service.IUserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserAuthController {

    @Autowired
    IUserAuthService userAuthService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupRequestDTO signupRequestDTO){

        return null;

    }
}
