package dev.aditya.userauthservice.Validation;

import dev.aditya.userauthservice.Exceptions.InvalidUserInput;
import dev.aditya.userauthservice.Model.RoleName;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;


@Component
public class ControllerValidator {
    private static final String E164_REGEX = "^[6-9]\\d{9}$";
    private static final Pattern PATTERN_PHONE = Pattern.compile(E164_REGEX);
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z]+\\.[A-Za-z]{2,6}$";
    private static final Pattern PATTERN_EMAIL = Pattern.compile(EMAIL_REGEX);

    public String validateEmail(String email){
        email = basicStringValidationChecks("Email",email);
        if(!PATTERN_EMAIL.matcher(email).matches()){
            throw new InvalidUserInput("Invalid Email format! Please try again!!");
        }
        return email;
    }

    public String validatePhoneNumber(String phoneNumber){
        phoneNumber = basicStringValidationChecks("Phone Number",phoneNumber);
        if(!PATTERN_PHONE.matcher(phoneNumber).matches()){
            throw new InvalidUserInput("The Phone Number provided is Invalid!! Please try again with just 10 digits and starting with [6-9] for indian local format!");
        }
        return phoneNumber;}

    public String validateRole(String role){
        role = basicStringValidationChecks("Role",role);
        for(RoleName roles : RoleName.values()){
            if(role.equalsIgnoreCase(roles.toString())){
                return role;
            }
        }
        throw new InvalidUserInput("Invalid Role input! Please provide an appropriate one!");
    }

    public String basicStringValidationChecks(String identifier, String value){
        if(value == null){
            throw new InvalidUserInput(identifier+" cannot be null. Please provide an appropriate value!");
        }
        else if (value.isBlank()) {
            throw new InvalidUserInput(identifier+" cannot be empty. Please provide an appropriate value!");
        }
        return value;
    }


    //    public String validateDateOfBirth(String dateOfBirth) throws DataFormatException {
//        dateOfBirth = basicStringValidationChecks("Date of Birth",dateOfBirth);
//        if (dateOfBirth.length() > 10) {
//            throw new DataFormatException("Invalid Date of Birth. Please enter proper Date and try again!");
//        }
//        else if(LocalDate.parse())
//        return dateOfBirth;
//    }ro
}


