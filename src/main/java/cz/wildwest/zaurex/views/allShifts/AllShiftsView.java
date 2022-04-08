package cz.wildwest.zaurex.views.allShifts;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;

@PageTitle("Všechny směny")
@Route(value = "shifts/all", layout = MainLayout.class)
@RolesAllowed({"SHIFT_LEADER", "MANAGER"})
public class AllShiftsView extends VerticalLayout {
    public AllShiftsView() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(new H3("Ačkoliv nás to velmi mrzí,"));
        verticalLayout.add(new Html("<p>se směnami utekl <i class=\"las la-dog\"></i>. Naše plány byly skvělé, nicméně už nezbyl čas na realizaci. " +
                "Přicházíme s <a href='https://www.adk.cz/eshop/formular-tydenni-plan-a5-pravy-2022-53-listu'>alternativním řešením</a>.</p>"));
        verticalLayout.setMaxWidth("400px");
        verticalLayout.setSpacing(false);
        verticalLayout.setWidth("unset");
        add(verticalLayout);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }
}
