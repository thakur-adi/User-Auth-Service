package dev.aditya.userauthservice.Dto;

import lombok.Getter;
import lombok.Setter;
//This was removed after I moved to Cookies and Headers to save tokens
@Getter
@Setter
public class LogoutRequestDTO {
    String email;
    String refreshToken;
}
