package dev.aditya.userauthservice.Repository;

import dev.aditya.userauthservice.Model.Role;
import dev.aditya.userauthservice.Model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findRoleByRoleName(RoleName roleName);

    Role save(Role role);
}
