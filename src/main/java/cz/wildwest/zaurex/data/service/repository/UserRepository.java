package cz.wildwest.zaurex.data.service.repository;

import cz.wildwest.zaurex.data.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends GenericRepository<User> {
    User findByUsername(String username);
}