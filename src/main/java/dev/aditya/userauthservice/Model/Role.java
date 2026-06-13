package dev.aditya.userauthservice.Model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Role extends Base{

   // String roleName;
   private RoleName roleName;
}
