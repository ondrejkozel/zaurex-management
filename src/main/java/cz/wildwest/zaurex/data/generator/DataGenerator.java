package cz.wildwest.zaurex.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;
import cz.wildwest.zaurex.data.Role;
import cz.wildwest.zaurex.data.entity.*;
import cz.wildwest.zaurex.data.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserService userService, WarehouseService warehouseService, WarehouseItemVariantService warehouseItemVariantService, HolidayService holidayService, InvoiceService invoiceService, ConfigurationService configurationService) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userService.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            //
            logger.info("Generating demo data");
            //
            createConfiguration(configurationService);
            createUsers(passwordEncoder, userService);
            createWarehouseItems(warehouseService, warehouseItemVariantService);
            createHolidays(userService, holidayService);
            createInvoices(invoiceService, warehouseItemVariantService, userService);
            //
            logger.info("Generated demo data");
        };
    }

    private void createConfiguration(ConfigurationService configurationService) {
        configurationService.saveAll(List.of(
                new Configuration(Configuration.StandardKey.ICO, "123456789"),
                new Configuration(Configuration.StandardKey.BANK_ACCOUNT_NUMBER, "46816-486646/6210")
        ));
    }

    private void createUsers(PasswordEncoder passwordEncoder, UserService userService) {
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
        userService.saveAll(List.of(salesman, warehouseman, shiftLeader, manager, shiftLeader2));
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

    private void createHolidays(UserService userService, HolidayService holidayService) {
        Holiday holiday = new Holiday(userService.findByUsername("skladnik"), LocalDate.now(), LocalDate.now().plusWeeks(1));
        holiday.setUserMessage("Chci jet k moři.");
        Holiday holiday2 = new Holiday(userService.findByUsername("skladnik"), LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(1).plusWeeks(1));
        holiday2.setUserMessage("Prosím pustťe mě \uD83D\uDE1E");
        holiday2.setStatus(Holiday.Status.APPROVED);
        holiday2.setManagerResponse("Tak jo :)");
        holidayService.saveAll(List.of(holiday, holiday2));
    }

    private void createInvoices(InvoiceService invoiceService, WarehouseItemVariantService warehouseItemVariantService, UserService userService) {
        Invoice invoice = new Invoice(userService.findAll().get(0), warehouseItemVariantService.findAll().stream().map(item -> new Invoice.Item(item, 1)).collect(Collectors.toList()), Invoice.PaymentForm.CARD);
        Invoice invoice2 = new Invoice(userService.findAll().get(1), warehouseItemVariantService.findAll().stream().map(item -> new Invoice.Item(item, 10)).collect(Collectors.toList()), Invoice.PaymentForm.TRANSFER);
        invoiceService.save(invoice);
        invoiceService.save(invoice2);
    }

}