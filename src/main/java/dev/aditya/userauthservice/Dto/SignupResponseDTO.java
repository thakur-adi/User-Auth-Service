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
    private final String message ="You have been successfully registered! Please login using registered Email and Password!!"; //can't make it static as jackson ignores static while serialization/deserialization
    private String name;
    private String email;
    private List<String> roles;

    //convert from User
    public SignupResponseDTO from(User user){
        this.setName(user.getName());
        this.setEmail(user.getEmail());
        this.setRoles(convertRoles(user.getRoles()));
        return this;
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
