package cz.wildwest.zaurex.views.aboutApp;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.views.MainLayout;
import javax.annotation.security.PermitAll;

@PageTitle("O aplikaci")
@Route(value = "about", layout = MainLayout.class)
@PermitAll
public class AboutAppView extends VerticalLayout {

    public AboutAppView() {
        setSpacing(false);
        //
        Image zaurex = new Image("images/logo-full.png", "Zaurex");
        zaurex.addClassNames("home-page-logo");
        add(zaurex);
        //
        add(new H2("Vítejte v Zaurex management"));
        add(new Paragraph("Lorem ipsum dolor sit amet"));
        //
        setSizeFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        getStyle().set("text-align", "left");
    }

}
