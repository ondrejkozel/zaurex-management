package cz.wildwest.zaurex.data.service;

import cz.wildwest.zaurex.data.entity.User;
import java.util.Optional;
import java.util.UUID;

import cz.wildwest.zaurex.data.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService extends GenericService<User, UserRepository> {

    public UserService(UserRepository userRepository) {
        super(userRepository);
    }
}
