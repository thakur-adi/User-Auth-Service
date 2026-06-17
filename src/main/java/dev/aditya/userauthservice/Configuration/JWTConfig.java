package dev.aditya.userauthservice.Configuration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JWTConfig {

    @Bean
    public SecretKey createSecretKey(){
        return Jwts.SIG.HS256.key().build();
    }
}
