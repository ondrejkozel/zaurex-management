package cz.wildwest.zaurex.views.employees;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;

@PageTitle("Zaměstnanci")
@Route(value = "employees", layout = MainLayout.class)
@RolesAllowed("MANAGER")
public class EmployeesView extends VerticalLayout {
}
