package cz.wildwest.zaurex.views.homePage;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.views.MainLayout;
import javax.annotation.security.PermitAll;

@PageTitle("Hlavní strana")
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class HomePageView extends VerticalLayout {

    public HomePageView() {
        setSpacing(false);
        //
        Image zaurex = new Image("images/logo-full.png", "Zaurex");
        zaurex.addClassNames("home-page-logo");
        add(zaurex);
        //
        add(new H2("Vítejte v Zaurex management"));
        add(new Paragraph("Pokud jsou se systémem jakékoliv problémy, nebojte se nám napsat na zákaznickou podporu!"));
        add(new Paragraph("E-mail: xkozel00@jaroska.cz"));
        add(new Paragraph("Adresa: třída Kapitána Jaroše 1829, 602 00 Brno-střed-Černá Pole"));
        //
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

}
