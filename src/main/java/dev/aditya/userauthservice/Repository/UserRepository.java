package dev.aditya.userauthservice.Repository;


import dev.aditya.userauthservice.Model.Role;
import dev.aditya.userauthservice.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    //List<Role> findRolesByEmail(String Email);

    User save(User user);
}
