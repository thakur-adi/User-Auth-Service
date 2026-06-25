package dev.aditya.userauthservice.Validation;

import dev.aditya.userauthservice.Exceptions.InvalidTokenException;
import dev.aditya.userauthservice.Exceptions.SessionNotExistException;
import dev.aditya.userauthservice.Model.Status;
import dev.aditya.userauthservice.Model.TokenType;
import dev.aditya.userauthservice.Repository.SessionRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;


@Component
public class JwtValidator {

    private final SecretKey secretKey;

    private SessionRepository sessionRepository;

    public JwtValidator(SecretKey secretKey,SessionRepository sessionRepository) {
        this.secretKey = secretKey;
        this.sessionRepository =sessionRepository;
    }

    public Claims validate(String token, TokenType tokenType) {
        if(token == null){
            throw new  InvalidTokenException("Token is NULL! Please try again!!");
        }
       else if(tokenType == TokenType.AUTH ) {
            if(!token.startsWith("Bearer "))// || sessionRepository.findByAuthToken(token).get().getCurrentStatus()==Status.DELETED)
            {
                throw new InvalidTokenException("Auth Token provided is Invalid. Please try again!");
            }
            token = token.substring(7);
            if(!sessionRepository.existsSessionByAuthTokenAndUser_CurrentStatus(token,Status.ACTIVE,Status.ACTIVE)){
                throw new SessionNotExistException("The Session you are looking for doesn't exist.(Possibly a token manipulator) Please provide valid Authorization and try again!");
            }
        }
        else if(!sessionRepository.existsByRefreshTokenAndCurrentStatusAndUser_CurrentStatus(token,Status.ACTIVE,Status.ACTIVE))
        {
            throw new SessionNotExistException("The Session you are looking for doesn't exist.(Possibly a token manipulator) Please provide valid RefreshToken and try again!");
        }

        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        String email = claims.getSubject();

        if(claims.isEmpty() || email == null){
            throw new JwtException("Token Invalid! No subject found. Please try again!!");
        }
        return claims;
    }
}

