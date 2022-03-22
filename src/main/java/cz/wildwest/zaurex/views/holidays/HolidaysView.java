package cz.wildwest.zaurex.views.holidays;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.components.gridd.GenericDataProvider;
import cz.wildwest.zaurex.components.gridd.Gridd;
import cz.wildwest.zaurex.data.entity.Holiday;
import cz.wildwest.zaurex.data.service.HolidayService;
import cz.wildwest.zaurex.views.MainLayout;
import javax.annotation.security.RolesAllowed;

@PageTitle("Dovolená")
@Route(value = "holidays/yours", layout = MainLayout.class)
@RolesAllowed({"SALESMAN", "WAREHOUSEMAN"})
public class HolidaysView extends Div {
    
    private final Gridd<Holiday> grid;

    public HolidaysView(HolidayService holidayService) {
        grid = new Gridd<>(Holiday.class,
                new GenericDataProvider(holidayService, Holiday.class),
                Holiday::new,
                true,
                buildEditor(),
                "Požádat o dovolenou",
                "Upravit požadavek",
                "Odstranit požadavek"
        );
        //
        setSizeFull();
        add(grid);
    }
    
    private BinderCrudEditor<Holiday> buildEditor() {
        
        Binder<Holiday> binder = new BeanValidationBinder<>(Holiday.class);
        //binder.bindInstanceFields(this);
        //
        FormLayout formLayout = new FormLayout(new Text("Tady zatím nic není"));
        return new BinderCrudEditor<>(binder, formLayout);
    }
    
};

