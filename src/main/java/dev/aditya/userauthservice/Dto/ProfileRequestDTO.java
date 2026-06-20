package dev.aditya.userauthservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileRequestDTO {
    private String name;
    private String email;
    private String dateOfBirth;
    private String phoneNumber;
    private String address;
    private String role;
}
