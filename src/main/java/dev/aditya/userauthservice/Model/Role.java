package dev.aditya.userauthservice.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Role extends Base{

   // String roleName;
   @Enumerated(EnumType.STRING)
   private RoleName roleName;
}
