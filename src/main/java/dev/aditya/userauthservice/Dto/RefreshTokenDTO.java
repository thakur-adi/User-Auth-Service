package dev.aditya.userauthservice.Dto;

import dev.aditya.userauthservice.Model.Session;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenDTO {

    String authToken;
    String refreshToken;

    public RefreshTokenDTO convertToDtoFrom(Session session){
        this.setAuthToken(session.getAuthToken());
        this.setRefreshToken(session.getRefreshToken());
        return this;
    }
}
