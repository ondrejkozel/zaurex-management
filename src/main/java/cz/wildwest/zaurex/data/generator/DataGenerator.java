package cz.wildwest.zaurex.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;
import cz.wildwest.zaurex.data.Role;
import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.data.service.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            //
            logger.info("Generating demo data");
            //
            logger.info("... generating 4 User entities...");
            User salesman = new User();
            salesman.setName("Prodavač");
            salesman.setUsername("prodavac");
            salesman.setHashedPassword(passwordEncoder.encode("prodavac"));
            salesman.setRoles(Collections.singleton(Role.SALESMAN));
            //
            User warehouseman = new User();
            warehouseman.setName("Skladník");
            warehouseman.setUsername("skladnik");
            warehouseman.setHashedPassword(passwordEncoder.encode("skladnik"));
            warehouseman.setRoles(Collections.singleton(Role.WAREHOUSEMAN));
            //
            User shiftLeader = new User();
            shiftLeader.setName("Vedoucí směny");
            shiftLeader.setUsername("vedoucismeny");
            shiftLeader.setHashedPassword(passwordEncoder.encode("vedoucismeny"));
            shiftLeader.setRoles(Collections.singleton(Role.SHIFT_LEADER));
            //
            User manager = new User();
            manager.setName("Manažer");
            manager.setUsername("manazer");
            manager.setHashedPassword(passwordEncoder.encode("manazer"));
            manager.setRoles(Set.of(Role.values()));
            //
            userRepository.saveAll(List.of(salesman, warehouseman, shiftLeader, manager));
            //
            logger.info("Generated demo data");
        };
    }

}