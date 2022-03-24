package cz.wildwest.zaurex.views.holidays;

import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
import cz.wildwest.zaurex.data.entity.Holiday;
import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.data.service.HolidayService;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAmount;
import java.util.Optional;

@PageTitle("Dovolená")
@Route(value = "holidays/yours", layout = MainLayout.class)
@RolesAllowed({"SALESMAN", "WAREHOUSEMAN"})
public class HolidaysView extends VerticalLayout {

    // TODO: 24.03.2022 aby nešlo upravovat ani mazat probíhající nebo proběhlou dovolenou

    private static final TemporalAmount MAX_FROM_DATE_DISTANCE = Period.ofYears(1);
    
    private final Gridd<Holiday> grid;

    public HolidaysView(HolidayService holidayService, AuthenticatedUser authenticatedUser) {
        User user = authenticatedUser.get().orElseThrow();
        grid = new Gridd<>(Holiday.class,
                new GenericDataProvider<>(holidayService, Holiday.class, holidayService1 -> holidayService.findAll(user)),
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
    }

    private void configureColumns() {
        grid.addColumn("Datum od", new TextRenderer<>(item -> item.getFromDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))));
        grid.addColumn("Datum do", new TextRenderer<>(item -> item.getToDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))));
        grid.addColumn("Poznámka", new TextRenderer<>(Holiday::getUserMessage));
        grid.addColumn("Stav", new ComponentRenderer<>(holiday -> {
            Badge badge;
            if (holiday.getStatus() == Holiday.Status.APPROVED) badge = new Badge("Schválena", Badge.BadgeVariant.SUCCESS);
            else if (holiday.getStatus() == Holiday.Status.DENIED) badge = new Badge("Zamítnuta", Badge.BadgeVariant.ERROR);
            else badge = new Badge("Předáno ke schválení", Badge.BadgeVariant.CONTRAST);
            return badge;
        }));
        grid.addColumn("Odpověď manažera", new TextRenderer<>(Holiday::getManagerResponse));
    }

    private DatePicker fromDate;
    private DatePicker toDate;
    @SuppressWarnings("FieldCanBeLocal")
    private TextArea userMessage;

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
        Binder<Holiday> binder = new BeanValidationBinder<>(Holiday.class);
        binder.bindInstanceFields(this);
        binder.removeBinding(fromDate);
        binder.removeBinding(toDate);
        binder.forField(fromDate)
                .withValidator((Validator<LocalDate>) (value, context) -> {
                    if (toDate.getValue() == null || value.minusDays(1).isBefore(toDate.getValue())) return ValidationResult.ok();
                    else return ValidationResult.error("datum od nesmí být po datu do");
                })
                .asRequired()
                .bind("fromDate");
        binder.forField(toDate)
                .withValidator((Validator<LocalDate>) (value, context) -> {
                    if (fromDate.getValue() == null || value.plusDays(1).isAfter(fromDate.getValue())) return ValidationResult.ok();
                    else return ValidationResult.error("datum do nesmí být před datem od");
                })
                .asRequired()
                .bind("toDate");
        //
        FormLayout formLayout = new FormLayout(fromDate, toDate, userMessage);
        formLayout.setColspan(fromDate, 1);
        formLayout.setColspan(toDate, 1);
        formLayout.setColspan(userMessage, 2);
        return new BinderCrudEditor<>(binder, formLayout);
    }
    
}

