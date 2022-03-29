package cz.wildwest.zaurex.views.addToWarehouse;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;

@PageTitle("Naskladnit")
@Route(value = "warehouse/add", layout = MainLayout.class)
@RolesAllowed("WAREHOUSEMAN")
public class AddToWarehouseView extends Div {

}
