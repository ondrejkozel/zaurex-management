package cz.wildwest.zaurex.views.about;


import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.PermitAll;

@PageTitle("O aplikaci")
@Route(value = "about", layout = MainLayout.class)
@PermitAll
public class AboutView extends VerticalLayout {

    public AboutView() {
        setSpacing(false);
        //
        VerticalLayout aboutLayout = new VerticalLayout();
        aboutLayout.addClassName("about-div");
        //
        Image appName = new Image("images/napis-zaurex-trans.png", "Zaurex");
        appName.setMaxWidth("100%");
        appName.setId("appname");
        Accordion accordion = new Accordion();
        accordion.add("Zaurex management", new Html("<p>Aplikace byla vytvořena při nultém ročníku soutěže Tour de App. Aplikaci vytvořil tým <b>Brněnský divoký západ</b> z Gymnázia Brno, třída Kapitána Jaroše. Členy týmu jsou studenti třetího ročníku čtyletého studia Ondřej Kozel, který se zasloužil o funkčnost aplikace, a Marek Přibyl, který mu nosil pití.</p>"));
        accordion.add("Uživatelská příručka", new Html("<p>Na každé stránce se nachází jednoduché vysvětlení, co se na dané straně nachází a jak se obsluhuje. Toto vysvětlení se dá spustit buď kliknutím na ikonku otazníku v pravém horním rohu, anebo klávesovou zkratkou <code>Alt&nbsp;+&nbsp;H</code>.</p>"));
        accordion.add("Technické požadavky", new Html("<p>Ke spuštění aplikace stačí moderní webový prohlížeč. Jelikož je Management progresivní webová aplikace, můžete si ji stáhnout do svého zařízení a používat ji jako nativní aplikaci – stačí kliknout na tlačítko instalace v adresovém řádku (desktop) nebo kliknout na výzvu <i>Přidat na domovskou obrazovku</i> (mobilní zařízení).</p>"));
        accordion.add("Hlášení chyb", new Html("<p>Pokud jste narazili na chybu, budeme rádi, když nám ji nahlásíte!<br><a class=\"github-button\" href=\"https://github.com/ondrejkozel/zaurex-management/issues\" data-size=\"large\" data-show-count=\"true\" aria-label=\"Issue ondrejkozel/zaurex-management on GitHub\">Issue</a></p>"));
        accordion.add("Kontaktní údaje", new Html("<p>V případě jakýchkoli problémů s aplikací nás neváhejte kontaktovat, rádi vám pomůžeme:<br>" +
                "E-mail: <a href=\"mailto:xkozel00@jaroska.cz?subject=Zaurex\">xkozel00@jaroska.cz</a><br>" +
                "Kontaktní adresa: <a href=\"https://mapy.cz/s/cavovadera\">třída Kapitána Jaroše 1829/14, 658 70, Brno</a></p>"));
        HorizontalLayout madeBy = new HorizontalLayout(new Span("Made with ❤️ by Wildwest, 2022"));
        madeBy.setWidthFull();
        madeBy.setJustifyContentMode(JustifyContentMode.CENTER);
        madeBy.setClassName("made-by");
        aboutLayout.add(appName, accordion, madeBy);
        //
        add(aboutLayout);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }

}
