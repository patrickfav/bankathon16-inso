package at.bankathon.dao;

import at.bankathon.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by mario on 22.04.16.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
