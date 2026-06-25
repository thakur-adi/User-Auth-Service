package dev.aditya.userauthservice.Model;

import jakarta.persistence.Entity;

import jakarta.persistence.ManyToMany;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
public class User extends Base{

    @Getter //instead of using a class level lombok getter, we use field level, to avoid sending in original Roles List as an o/p to client(Security reasons).
    private String name;
    @Getter
    private String email;
    @Getter
    private String password;
    @Getter
    private LocalDate dateOfBirth; //Date is a legacy system. 'LocalDate' provides a robust way to represent dates without time or timezone information, thread safety etc.
    @Getter
    private String phoneNumber;
    @Getter
    private String address;
    @ManyToMany
    private List<Role> roles;

    protected User(){} //Keeps JPA happy. This has to be present so that jpa can work properly as we have an @Entity present.

    private User(UserBuilder userBuilder){
        this.name = userBuilder.name;
        this.email = userBuilder.email;
        this.password = userBuilder.password;
        this.dateOfBirth = userBuilder.dateOfBirth;
        this.phoneNumber = userBuilder.phoneNumber;
        this.address = userBuilder.address;
        this.roles = List.copyOf(userBuilder.roles);
    }

    public List<Role> getRoles() {
        return List.copyOf(roles);
    }

    public static UserBuilder builder(){
        return new UserBuilder();
    }

    //A builder class to create User as there are too many variables easy to mess up, also makes User class immutable.
    public static class UserBuilder{

        private String name;
        private String email;
        private String password;
        private LocalDate dateOfBirth;
        private String phoneNumber;
        private String address;
        private List<Role> roles;

        private UserBuilder(){
            roles = new ArrayList<>();
        }

        public UserBuilder setName(String name) {
            this.name = name;
            return this;
        }


        public UserBuilder setEmail(String email) {
            this.email = email;
            return this;
        }



        public UserBuilder setPassword(String password) {
            this.password = password;
            return this;
        }



        public UserBuilder setDateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }


        public UserBuilder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public UserBuilder setAddress(String address) {
            this.address = address;
            return this;
        }


        public UserBuilder addRoles(Role role) {
            this.roles.add(role);
            return this;
        }

        public User build(){
            return new User(this);
        }


    }
}

