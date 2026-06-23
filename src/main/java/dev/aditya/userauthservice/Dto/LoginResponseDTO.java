package dev.aditya.userauthservice.Dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dev.aditya.userauthservice.Model.Session;
import lombok.Getter;
import lombok.Setter;
//This was removed after I moved to Cookies and Headers to save tokens
@Getter
@Setter
@JsonPropertyOrder({"message","authToken","refreshToken"})
public class LoginResponseDTO {
    String authToken;
    String refreshToken;
    String message;
    public LoginResponseDTO convertToDtoFromSession(Session session)
    {
        this.setAuthToken(session.getAuthToken());
        this.setRefreshToken(session.getRefreshToken());
        this.setMessage("Welcome Back "+ session.getUser().getName() +"! How can we serve you today?");
        return this;
    }
}
