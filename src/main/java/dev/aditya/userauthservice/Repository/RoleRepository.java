package dev.aditya.userauthservice.Repository;

import dev.aditya.userauthservice.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    Role save(Role role);
}
