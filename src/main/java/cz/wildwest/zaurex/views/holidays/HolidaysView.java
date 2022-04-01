package cz.wildwest.zaurex.views.holidays;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.components.Badge;
import cz.wildwest.zaurex.components.gridd.GenericDataProvider;
import cz.wildwest.zaurex.components.gridd.Gridd;
import cz.wildwest.zaurex.data.DatePickerI18n;
import cz.wildwest.zaurex.data.Role;
import cz.wildwest.zaurex.data.entity.Holiday;
import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.data.service.HolidayService;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.LocalDateTimeFormatter;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.List;

@PageTitle("Dovolená")
@Route(value = "holidays/yours", layout = MainLayout.class)
@RolesAllowed({"SALESMAN", "WAREHOUSEMAN"})
public class HolidaysView extends VerticalLayout {

    private static final TemporalAmount MAX_FROM_DATE_DISTANCE = Period.ofYears(1);
    
    private final Gridd<Holiday> grid;

    public HolidaysView(HolidayService holidayService, AuthenticatedUser authenticatedUser) {
        User user = authenticatedUser.get().orElseThrow();
        grid = new Gridd<>(Holiday.class,
                new GenericDataProvider<>(holidayService, Holiday.class, () -> holidayService.findAll(user)),
                () -> new Holiday(user),
                true,
                buildEditor(),
                "Požádat o dovolenou",
                "Upravit požadavek",
                "Zrušit požadavek"
        );
        configureColumns();
        //
        setSizeFull();
        add(grid);
        if (!user.getRoles().contains(Role.MANAGER)) {
            grid.getCrud().addNewListener(event -> makeReadonly(false));
            grid.getCrud().addEditListener(event -> makeReadonly(event.getItem().getFromDate().isBefore(LocalDate.now().plusDays(1))));
            grid.getCrud().addEditListener(event -> getStatus().setValue(Holiday.Status.PENDING));
            grid.getCrud().addSaveListener(event -> Notification.show("Váš požadavek byl odeslán manažerovi! 🏖️"));
        }
    }

    private void configureColumns() {
        grid.addColumn("Datum od", new TextRenderer<>(item -> item.getFromDate().format(LocalDateTimeFormatter.ofFullDate())), false);
        grid.addColumn("Datum do", new TextRenderer<>(item -> item.getToDate().format(LocalDateTimeFormatter.ofFullDate())), false);
        grid.addColumn("Stav", new ComponentRenderer<>(holiday -> {
            Badge badge;
            if (holiday.getStatus() == Holiday.Status.APPROVED) badge = new Badge("schválena", Badge.BadgeVariant.SUCCESS);
            else if (holiday.getStatus() == Holiday.Status.DENIED) badge = new Badge("zamítnuta", Badge.BadgeVariant.ERROR);
            else badge = new Badge("předána ke schválení", Badge.BadgeVariant.CONTRAST);
            return badge;
        }), true);
        grid.addColumn("Poznámka", new TextRenderer<>(Holiday::getUserMessage), true);
        grid.addColumn("Odpověď manažera", new TextRenderer<>(Holiday::getManagerResponse), true);
    }

    private void makeReadonly(boolean readonly) {
        List.<HasValue<?, ?>>of(fromDate, toDate, userMessage).forEach(field -> field.setReadOnly(readonly));
        grid.getCrud().getDeleteButton().setEnabled(!readonly);
        grid.getCrud().getSaveButton().setEnabled(!readonly);
    }

    private DatePicker fromDate;
    private DatePicker toDate;
    @SuppressWarnings("FieldCanBeLocal")
    private TextArea userMessage;

    private Select<Holiday.Status> status;
    private TextArea managerResponse;
    private Select<User> owner;

    private FormLayout formLayout;

    private Binder<Holiday> binder;

    private BinderCrudEditor<Holiday> buildEditor() {
        fromDate = new DatePicker("Datum od");
        fromDate.setMin(LocalDate.now().plusDays(1));
        fromDate.setMax(LocalDate.now().plus(MAX_FROM_DATE_DISTANCE));
        toDate = new DatePicker("Datum do");
        toDate.setMin(fromDate.getMin());
        fromDate.addValueChangeListener(event -> toDate.setMin(fromDate.getValue()));
        toDate.addValueChangeListener(event -> fromDate.setMax(toDate.getValue()));
        toDate.setEnabled(false);
        fromDate.addValueChangeListener(event -> toDate.setEnabled(event.getValue() != null));
        userMessage = new TextArea("Poznámka");
        //
        fromDate.setI18n(DatePickerI18n.DATE_PICKER_I_18_N);
        toDate.setI18n(DatePickerI18n.DATE_PICKER_I_18_N);
        //
        status = new Select<>(Holiday.Status.values());
        status.setRenderer(new ComponentRenderer<>(status1 -> new Badge(status1.getText(), status1 == Holiday.Status.APPROVED ? Badge.BadgeVariant.SUCCESS : status1 == Holiday.Status.DENIED ? Badge.BadgeVariant.ERROR : Badge.BadgeVariant.CONTRAST)));
        status.setLabel("Stav");
        status.setRequiredIndicatorVisible(true);
        managerResponse = new TextArea("Odpověď manažera");
        owner = new Select<>();
        owner.setLabel("Osoba");
        owner.setRequiredIndicatorVisible(true);
        owner.setRenderer(new TextRenderer<>(User::getName));
        //
        binder = new BeanValidationBinder<>(Holiday.class);
        binder.bindInstanceFields(this);
        binder.removeBinding(fromDate);
        binder.removeBinding(toDate);
        binder.removeBinding(owner);
        binder.forField(fromDate)
                .withValidator((Validator<LocalDate>) (value, context) -> {
                    if (value == null || toDate.getValue() == null || value.minusDays(1).isBefore(toDate.getValue())) return ValidationResult.ok();
                    else return ValidationResult.error("datum od nesmí být po datu do");
                })
                .asRequired()
                .bind("fromDate");
        binder.forField(toDate)
                .withValidator((Validator<LocalDate>) (value, context) -> {
                    if (value == null || fromDate.getValue() == null || value.plusDays(1).isAfter(fromDate.getValue())) return ValidationResult.ok();
                    else return ValidationResult.error("datum do nesmí být před datem od");
                })
                .asRequired()
                .bind("toDate");
        formLayout = new FormLayout(fromDate, toDate, userMessage);
        formLayout.setColspan(fromDate, 1);
        formLayout.setColspan(toDate, 1);
        formLayout.setColspan(userMessage, 2);
        return new BinderCrudEditor<>(binder, formLayout);
    }

    public void useOwnerField(List<User> users) {
        owner.setItems(users);
        binder.forField(owner).bind("owner");
    }

    public Gridd<Holiday> getGrid() {
        return grid;
    }

    public DatePicker getFromDate() {
        return fromDate;
    }

    public DatePicker getToDate() {
        return toDate;
    }

    public TextArea getUserMessage() {
        return userMessage;
    }

    public Select<Holiday.Status> getStatus() {
        return status;
    }

    public TextArea getManagerResponse() {
        return managerResponse;
    }

    public FormLayout getFormLayout() {
        return formLayout;
    }

    public Select<User> getOwner() {
        return owner;
    }
}

