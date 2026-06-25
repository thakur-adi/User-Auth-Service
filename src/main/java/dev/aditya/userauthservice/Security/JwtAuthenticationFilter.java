package dev.aditya.userauthservice.Security;

import dev.aditya.userauthservice.Exceptions.InvalidTokenException;
import dev.aditya.userauthservice.Exceptions.SessionNotExistException;
import dev.aditya.userauthservice.Model.TokenType;
import dev.aditya.userauthservice.Validation.JwtValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/*This Filter will always execute regardless of what endpoint we use or what method we use permitAll() or authenticated().
 Once the request goes through each check then it checks whether to permit it or stop it.
 When login request hits the filter it goes through all logic, and then it passes to filterChain.doFilter(request, response) -> then it is passed to security filter chain, there it sees that it has permitAll() enabled that means even if the authentication failed allow it to hit controller.
So technically Filter will always run its just that after going through the filter it checks what should happen next.
*/
@Component//because this has been declared as a component Spring will auto execute it everytime.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private JwtValidator jwtValidator;
    private static final List<String> AUTH_BASED_ENDPOINTS = List.of("/profile", "/reset"); //keep just servlet path, ignore context path
    private static final List<String> REFRESH_BASED_ENDPOINTS = List.of("/auth/logout", "/auth/refresh");


    JwtAuthenticationFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = "";
        try {

                String servletPath = request.getServletPath();
    //Don't use if condition as login or signup will have to pass through it. We'll have to add a check for them as well.(1.refresh_based,2.Auth_based,3.All_other)

                //check for refresh expecting endpoints
                for (String endpoint : REFRESH_BASED_ENDPOINTS) {
                    if (servletPath.equals(endpoint))
                    {
                        //Extracting Tokens
                        Optional<String>tokenOpt = Arrays.stream(request.getCookies())
                            .filter(cookie -> cookie.getName().equals("refreshToken"))
                            .map(Cookie::getValue)
                            .findAny();
                        token=tokenOpt.isPresent()?tokenOpt.get():null;

                        //Validating Refresh Tokens
                        Claims refreshClaims = jwtValidator.validate(token, TokenType.REFRESH);
                        saveSecurityContextFromClaims(refreshClaims);
                        break;
                    }
            }
                //check for auth expecting endpoints
                for (String endpoint : AUTH_BASED_ENDPOINTS) {
                    if (servletPath.equals(endpoint)) {
                        //Extracting Tokens
                        token = request.getHeader(HttpHeaders.AUTHORIZATION);

                        //Validating Auth Token
                        Claims authClaims = jwtValidator.validate(token, TokenType.AUTH);
                        saveSecurityContextFromClaims(authClaims);
                        break;
                    }
            }
            filterChain.doFilter(request, response);
        }
        catch (InvalidTokenException e) {
            /*  One way to do it -->
                If my InvalidToken Exception inherits some other type of exception like RuntimeException or Exception
                Then pass some other type of exception here which inherits AuthenticationException which in this case is BadCredentialsException
                But is it always Bad Credentials? --> NO, Token Expiry doesn't come under BadCredentials. It's not right to pass a generic Exception.

                jwtAuthenticationEntryPoint.commence(request,response,new BadCredentialsException(e.getMessage(),e));

                Better to go with the 2nd option--> make your custom exception inherit from AuthenticationException and just use that.
            */
            jwtAuthenticationEntryPoint.commence(request,response,e);
        } catch (SessionNotExistException e) {
            jwtAuthenticationEntryPoint.commence(request,response,e);
        } catch (ExpiredJwtException e){
            jwtAuthenticationEntryPoint.commence(request,response,new InvalidTokenException("Token has expired! Please Login again!!"));
        } catch (JwtException e){
            jwtAuthenticationEntryPoint.commence(request,response,new InvalidTokenException("Invalid Token! Token manipulator detected!!!"));
        }
    }

    private void saveSecurityContextFromClaims(Claims claims){
        //This (UsernamePasswordAuthenticationToken) is by default used by devs in industry.
        // There are others as well with different use case but most commonly this gets used for custom filters like JWTFilter etc. even tho it doesn't have username and password.
        //Why? Because it's a convenient built-in implementation that already stores: Principal,Credentials,Authorities,Authenticated status. Spring Security understands it perfectly.
        //We can create our own as well -> public class CustomJwtAuthenticationToken implements Authentication {...} ---- (JwtAuthenticationToken ->already exists for OAUTH2)
        UsernamePasswordAuthenticationToken authenticationToken =
                UsernamePasswordAuthenticationToken.authenticated(claims, claims.getSubject(), null);

        // This saves the claims into ThreadLocal(A map in thread cache unique to each thread).
        // This can now be fetched later in other layers(controller,service etc.) when required
        SecurityContext newContext = SecurityContextHolder.createEmptyContext();
        newContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(newContext);
    }
}