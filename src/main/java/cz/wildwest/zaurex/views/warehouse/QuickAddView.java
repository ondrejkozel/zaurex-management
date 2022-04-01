package cz.wildwest.zaurex.views.warehouse;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;

@PageTitle("Rychle naskladnit")
@Route(value = "warehouse/quick_add", layout = MainLayout.class)
@RolesAllowed({"WAREHOUSEMAN", "MANAGER"})
public class QuickAddView extends VerticalLayout {

}
