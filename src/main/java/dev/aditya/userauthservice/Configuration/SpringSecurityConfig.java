package dev.aditya.userauthservice.Configuration;

import dev.aditya.userauthservice.Security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
public class SpringSecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // SecurityFilterChain is a middleware provided by Spring Security, this stops malicious requests even before it reaches the controller layer.
    // All requests have to go through this (and the custom filter)first. Once it gets approved by this filter chain only then are they allowed to hit controller layer.
    // It's like a funnel where only the requests which clear all the layers of this filter passes through. So to provide a full security throughout the service we use this.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.authorizeHttpRequests((authorize) ->authorize
                                            .requestMatchers("/signup","/login").permitAll() // We allow any request coming to signup & login endpoints to hit controller even if they fail our custom filter authentication.
                                            .anyRequest().authenticated()) // that's not the case with remaining endpoints(profile,logout etc.), they have to be authenticated i.e. pass the custom designed filter.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) //This tells where the filter should run?(i.e. before UseNamePasswordAuthentication Filter), not whether it should run?
                .csrf((csrf)-> csrf.disable());// We disable csrf(not required in modern systems anymore, w/o disabling Spring won't allow to bypass to controller layer).

        return httpSecurity.build();
    }

}
