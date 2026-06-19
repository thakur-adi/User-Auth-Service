package dev.aditya.userauthservice.Dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dev.aditya.userauthservice.Model.Role;
import dev.aditya.userauthservice.Model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({"message","name","email","roles"})
public class SignupResponseDTO {
    private String name;
    private String email;
    private List<String> roles;
    //can't make it static as jackson ignores static while serialization/deserialization
    private String message;

    //convert from User
    public void convertToDtoFrom(User user){
        this.setName(user.getName());
        this.setEmail(user.getEmail());
        this.setRoles(convertRoles(user.getRoles()));
        this.setMessage("Hello "+name.toUpperCase()+ "! You have been successfully registered! Please login using registered Email '" + email +"' and Password!!");
    }

    //Helper for converting roles from Role class to String
    private List<String> convertRoles(List<Role> userRoles){
        List<String> convertedRoles = new ArrayList<>();
        for(Role role : userRoles)
        {
            convertedRoles.add(role.getRoleName().toString());
        }
        return convertedRoles;
    }

}
