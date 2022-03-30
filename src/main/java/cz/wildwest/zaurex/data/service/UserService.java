package cz.wildwest.zaurex.data.service;

import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.data.service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService extends GenericService<User, UserRepository> {

    private final HolidayService holidayService;

    public UserService(UserRepository userRepository, HolidayService holidayService) {
        super(userRepository);
        this.holidayService = holidayService;
    }

    @Override
    public List<User> findAll() {
        return super.findAll().stream().sorted(Comparator.comparing(User::getName)).collect(Collectors.toList());
    }

    public User findByUsername(String username) {
        return mainRepository.findByUsername(username);
    }

    @Override
    public void delete(User objekt) {
        holidayService.deleteAll(objekt);
        super.delete(objekt);
    }

    @Override
    public void deleteAll(Iterable<User> objekty) {
        objekty.forEach(holidayService::deleteAll);
        super.deleteAll(objekty);
    }
}
