package cz.wildwest.zaurex.views.holidays;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.components.Badge;
import cz.wildwest.zaurex.components.gridd.GenericDataProvider;
import cz.wildwest.zaurex.components.gridd.Gridd;
import cz.wildwest.zaurex.data.entity.Holiday;
import cz.wildwest.zaurex.data.service.HolidayService;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@PageTitle("Dovolená")
@Route(value = "holidays/yours", layout = MainLayout.class)
@RolesAllowed({"SALESMAN", "WAREHOUSEMAN"})
public class HolidaysView extends VerticalLayout {
    
    private final Gridd<Holiday> grid;

    public HolidaysView(HolidayService holidayService) {
        grid = new Gridd<>(Holiday.class,
                new GenericDataProvider<>(holidayService, Holiday.class),
                Holiday::new,
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

    private BinderCrudEditor<Holiday> buildEditor() {
        
        Binder<Holiday> binder = new BeanValidationBinder<>(Holiday.class);
        //binder.bindInstanceFields(this);
        //
        FormLayout formLayout = new FormLayout(new Text("Tady zatím nic není"));
        return new BinderCrudEditor<>(binder, formLayout);
    }
    
}

