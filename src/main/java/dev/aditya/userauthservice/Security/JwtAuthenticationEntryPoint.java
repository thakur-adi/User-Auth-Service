package dev.aditya.userauthservice.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

//This class acts as a security gatekeeper. It dictates exactly what happens when an anonymous user or a client with an invalid/expired JSON Web Token (JWT) tries to access a protected API route
//So whenever we catch an exception(in filter) we pass it here, to give out a proper response back to client.
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint{
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
                throws IOException, ServletException
    {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());//Since this is called for JwtAuthentication we can default set it to unauthorized.
        response.setContentType(MediaType.APPLICATION_JSON.toString()); //Tells client that the response incoming is of JSON type
        response.getWriter().write(authException.getMessage());// this sends in the response back to client
    }
}
