package cz.wildwest.zaurex.views.invoices;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;

@PageTitle("Faktury")
@Route(value = "invoices", layout = MainLayout.class)
@RolesAllowed({"SHIFT_LEADER"})
public class InvoicesView extends Div {
}

