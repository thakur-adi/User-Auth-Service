package dev.aditya.userauthservice.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Session extends Base{

    private String authToken;
    private String refreshToken;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
 }
