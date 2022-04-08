package cz.wildwest.zaurex.data.generator;

import cz.wildwest.zaurex.data.service.*;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Service
public class DataRegenerator {

    private final List<Truncatable> allServices;

    private final EntityManager entityManager;
    private final ConfigurationService configurationService;
    private final HolidayService holidayService;
    private final InvoiceService invoiceService;
    private final UserService userService;
    private final WarehouseItemVariantService warehouseItemVariantService;
    private final WarehouseService warehouseService;
    private final ShiftService shiftService;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger;

    public DataRegenerator(@Autowired EntityManager entityManager, ConfigurationService configurationService, HolidayService holidayService, InvoiceService invoiceService, UserService userService, WarehouseItemVariantService warehouseItemVariantService, WarehouseService warehouseService, ShiftService shiftService, PasswordEncoder passwordEncoder) {
        this.shiftService = shiftService;
        this.passwordEncoder = passwordEncoder;
        this.allServices = List.of(configurationService, holidayService, invoiceService, userService, warehouseItemVariantService, warehouseService, shiftService);
        this.entityManager = entityManager;
        this.configurationService = configurationService;
        this.holidayService = holidayService;
        this.invoiceService = invoiceService;
        this.userService = userService;
        this.warehouseItemVariantService = warehouseItemVariantService;
        this.warehouseService = warehouseService;
        logger = LoggerFactory.getLogger(getClass());
    }

    public void regenerate() {
        truncateDatabase();
        regenerateDatabase();
    }

    private void truncateDatabase() {
        Session session = entityManager.unwrap(Session.class);
        session.doWork(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SET FOREIGN_KEY_CHECKS = 0;")) {
                preparedStatement.executeUpdate();
                logger.info("Disabled foreign key checks.");
            } catch (SQLException e) {
                logger.error(String.format("Cannot disable foreign key checks: %s: %s", e, e.getCause()));
            }

            logger.info("Starting truncation.");
            allServices.forEach(Truncatable::truncate);
            logger.info("Truncation done.");

            try (PreparedStatement preparedStatement = connection.prepareStatement("SET FOREIGN_KEY_CHECKS = 1;")) {
                preparedStatement.executeUpdate();
                logger.info("Enabled foreign key checks.");
            } catch (SQLException e) {
                logger.error(String.format("Cannot enable foreign key checks: %s: %s", e, e.getCause()));
            }
        });
    }

    private void regenerateDatabase() {
        new DataGenerator().generateDemoData(passwordEncoder, userService, warehouseService, warehouseItemVariantService, holidayService, invoiceService, configurationService, shiftService, logger);
    }

}
