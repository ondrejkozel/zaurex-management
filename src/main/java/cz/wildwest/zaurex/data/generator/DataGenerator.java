package cz.wildwest.zaurex.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;
import cz.wildwest.zaurex.data.Role;
import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.data.entity.WarehouseItem;
import cz.wildwest.zaurex.data.service.WarehouseService;
import cz.wildwest.zaurex.data.service.repository.UserRepository;
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
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository, WarehouseService warehouseService) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            //
            logger.info("Generating demo data");
            //
            logger.info("... generating 5 User entities...");
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
            User shiftLeader2 = new User();
            shiftLeader2.setName("Vedocí směny prodavač");
            shiftLeader2.setUsername("vedoucismeny2");
            shiftLeader2.setHashedPassword(passwordEncoder.encode("vedoucismeny2"));
            shiftLeader2.setRoles(Set.of(Role.SHIFT_LEADER, Role.SALESMAN));
            //
            userRepository.saveAll(List.of(salesman, warehouseman, shiftLeader, manager, shiftLeader2));
            //
            logger.info("Generated demo data");
            //
            WarehouseItem bunda_tilak = new WarehouseItem("Bunda Tilak", "Zimní bunda Tilak vás zahřeje v každém ročním období!");
            bunda_tilak.setVariants(Set.of(
                    new WarehouseItem.Variant("zelená", 84, 499.9),
                    new WarehouseItem.Variant("černá", 10, 514.9)
            ));
            bunda_tilak.setCategory(WarehouseItem.Category.HIKING);
            warehouseService.save(bunda_tilak);
        };
    }

}