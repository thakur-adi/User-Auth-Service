package dev.aditya.userauthservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDTO {
    String email;
    String refreshToken;
}
