package dev.aditya.userauthservice.Repository;

import dev.aditya.userauthservice.Model.Session;
import dev.aditya.userauthservice.Model.Status;
import dev.aditya.userauthservice.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session,Long> {

    Optional<Session> findByRefreshToken(String refreshToken);

    //user_CurrentStatus -> _ tells jpa that it's a Nested property
    Boolean existsByRefreshTokenAndCurrentStatusAndUser_CurrentStatus(String refreshToken,Status sessionStatus,Status userStatus);

    Optional<Session> findByAuthToken(String authToken);


    Boolean existsSessionByAuthTokenAndUser_CurrentStatus(String authToken,Status sessionStatus,Status userStatus);

    List<Session> findAllByUserAndCurrentStatus(User user, Status currentStatus);

    Session save(Session session);
}
