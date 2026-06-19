package dev.aditya.userauthservice.Dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dev.aditya.userauthservice.Model.Role;
import dev.aditya.userauthservice.Model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({"name","email","dateOfBirth","phoneNumber","address","roles"})
public class ProfileResponseDTO {
    private String name;
    private String email;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String address;
    private List<String> roles;



    public void convertToDtoFrom(User existingUser) {
       this.setName(existingUser.getName());
       this.setEmail(existingUser.getEmail());
       this.setAddress(existingUser.getAddress());
       this.setDateOfBirth(existingUser.getDateOfBirth());
       this.setPhoneNumber(existingUser.getPhoneNumber());
       this.setRoles(convertRoles(existingUser.getRoles()));
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
