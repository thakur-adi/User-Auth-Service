package dev.aditya.userauthservice.Repository;

import dev.aditya.userauthservice.Model.Session;
import dev.aditya.userauthservice.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session,Long> {

    Optional<Session> findByRefreshToken(String refreshToken);

    Optional<Session> findByAuthToken(String authToken);

    Optional<Session> findByUser(User user);

    Session save(Session session);
}
