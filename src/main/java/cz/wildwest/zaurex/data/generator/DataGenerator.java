package cz.wildwest.zaurex.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;
import cz.wildwest.zaurex.data.Role;
import cz.wildwest.zaurex.data.entity.Holiday;
import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.data.entity.WarehouseItem;
import cz.wildwest.zaurex.data.service.HolidayService;
import cz.wildwest.zaurex.data.service.WarehouseItemVariantService;
import cz.wildwest.zaurex.data.service.WarehouseService;
import cz.wildwest.zaurex.data.service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository, WarehouseService warehouseService, WarehouseItemVariantService warehouseItemVariantService, HolidayService holidayService) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            //
            logger.info("Generating demo data");
            //
            createUsers(passwordEncoder, userRepository);
            createWarehouseItems(warehouseService, warehouseItemVariantService);
            createHolidays(userRepository, holidayService);
            //
            logger.info("Generated demo data");
        };
    }

    private void createUsers(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        User salesman = new User();
        salesman.setName("Prodavač");
        salesman.setUsername("prodavac");
        salesman.setHashedPassword(passwordEncoder.encode("prodavac"));
        salesman.setRoles(Collections.singleton(Role.SALESMAN));
        salesman.setHasChangedPassword(true);
        //
        User warehouseman = new User();
        warehouseman.setName("Skladník");
        warehouseman.setUsername("skladnik");
        warehouseman.setHashedPassword(passwordEncoder.encode("skladnik"));
        warehouseman.setRoles(Collections.singleton(Role.WAREHOUSEMAN));
        warehouseman.setHasChangedPassword(true);
        //
        User shiftLeader = new User();
        shiftLeader.setName("Vedoucí směny");
        shiftLeader.setUsername("vedoucismeny");
        shiftLeader.setHashedPassword(passwordEncoder.encode("vedoucismeny"));
        shiftLeader.setRoles(Collections.singleton(Role.SHIFT_LEADER));
        shiftLeader.setHasChangedPassword(true);
        //
        User manager = new User();
        manager.setName("Manažer");
        manager.setUsername("manazer");
        manager.setHashedPassword(passwordEncoder.encode("manazer"));
        manager.setRoles(Set.of(Role.values()));
        manager.setHasChangedPassword(true);
        //
        User shiftLeader2 = new User();
        shiftLeader2.setName("Vedocí směny prodavač");
        shiftLeader2.setUsername("vedoucismeny2");
        shiftLeader2.setHashedPassword(passwordEncoder.encode("vedoucismeny2"));
        shiftLeader2.setRoles(Set.of(Role.SHIFT_LEADER, Role.SALESMAN));
        shiftLeader2.setHasChangedPassword(true);
        //
        userRepository.saveAll(List.of(salesman, warehouseman, shiftLeader, manager, shiftLeader2));
    }

    private void createWarehouseItems(WarehouseService warehouseService, WarehouseItemVariantService warehouseItemVariantService) {
        WarehouseItem bunda_tilak = new WarehouseItem("Bunda Tilak", "Zimní bunda Tilak vás zahřeje v každém ročním období!", WarehouseItem.Category.HIKING);
        WarehouseItem energeticka_tycinka = new WarehouseItem("Energetická tyčinka Nevím", "Bla bla popis", WarehouseItem.Category.OTHER);
        WarehouseItem celovka = new WarehouseItem("Čelovka Petzl Tikkina 250", "Nová verze kompaktní svítilny pro každodenní použití od firmy Petzl s názvem Tikkina má svítivost 250 lumenů a dosvit až 60 metrů.", WarehouseItem.Category.OTHER);
        warehouseService.saveAll(List.of(bunda_tilak, energeticka_tycinka, celovka));
        warehouseItemVariantService.saveAll(Set.of(
                new WarehouseItem.Variant(bunda_tilak, "zelená", 84, 499.9),
                new WarehouseItem.Variant(bunda_tilak, "černá", 10, 514.9),
                //
                new WarehouseItem.Variant(energeticka_tycinka, "malá", 100, 35),
                new WarehouseItem.Variant(energeticka_tycinka, "dvojitá", 34, 64),
                //
                new WarehouseItem.Variant(celovka, "varianta", 9, 519)
        ));
    }

    private void createHolidays(UserRepository userRepository, HolidayService holidayService) {
        Holiday holiday = new Holiday(userRepository.findByUsername("skladnik"), LocalDate.now(), LocalDate.now().plusWeeks(1));
        holiday.setUserMessage("Chci jet k moři.");
        Holiday holiday2 = new Holiday(userRepository.findByUsername("skladnik"), LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(1).plusWeeks(1));
        holiday2.setUserMessage("Prosím pustťe mě \uD83D\uDE1E");
        holiday2.setStatus(Holiday.Status.APPROVED);
        holiday2.setManagerResponse("Tak jo :)");
        holidayService.saveAll(List.of(holiday, holiday2));
    }

}