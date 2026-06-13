package dev.aditya.userauthservice.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class User extends Base{

    private String name;
    private String email;
    private String password;
    private Date dateOfBirth;
    private String phoneNumber;
    private  String address;
    @ManyToMany
    private List<Role> roles;
}
