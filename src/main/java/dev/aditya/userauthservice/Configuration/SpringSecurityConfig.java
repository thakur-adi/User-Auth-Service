package dev.aditya.userauthservice.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
public class SpringSecurityConfig {

    // SecurityFilterChain is a middleware provided by Spring Security, this stops malicious requests even before it reaches the controller layer.
    // All requests have to go through this first. Once it gets approved by this filter only then are they allowed to hit controller layer.
    // It's like a funnel where only the requests which clear all the layers of this filter passes through. So to provide a throughout security we use this.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.authorizeHttpRequests((authorize) ->authorize
                                            //authorize.requestMatchers("/user/signup").permitAll() // We allow any request coming to (signup,login or reset) endpoints to hit without any authentication.
//                                                     .requestMatchers("user/login").permitAll()
//                                                     .requestMatchers("/user/reset").permitAll()
                                                     .anyRequest().permitAll()) // that's not the case with remaining endpoints(profile,logout), they have to be authenticated.
                    .csrf((csrf)-> csrf.disable());// We disable csrf(not required in modern systems anymore, w/o disabling Spring won't allow to bypass to controller layer).
                /*
                    .authorizeHttpRequests((auth)->auth.requestMatchers("/user/login").permitAll())
                    .authorizeHttpRequests((auth)-> auth.anyRequest().authenticated());
                 */
        return httpSecurity.build();
    }

}
