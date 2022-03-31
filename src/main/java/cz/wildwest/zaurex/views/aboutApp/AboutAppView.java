package cz.wildwest.zaurex.views.aboutApp;


import com.vaadin.flow.component.Html;
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
        add(new Html("<div>"+
                "<H2>O aplikaci</H2>"
                + "<p>Aplikace byla vytvořena do nultého ročníku soutěže Tour de App. Aplikaci vytvořil tým <b>Brněnský divoký západ</b> ze školy Gymnázium Brno, třída Kapitána Jaroše. Členy týmu jsou studenti třetího ročníku čtyletého studia <b>Ondřej Kozel</b>, který se zasloužil o funkčnost aplikace a <b>Marek Přibyl</b>, který mu nosil pití.</p>"
                + "<H4>Uživatelská příručka</H4>"
                + "Na každé stránce se nachází jednoduché vysvětlení, co se na dané straně nachází a jak se obsluhuje. Toto vysvětlení se dá spustit buď kliknutím na ikonku otazníku v pravém horním rohu, anebo klávesovou zkratkou <code>Alt&nbsp;+&nbsp;H</code>. Stránky, na které daná osoba nemá přístup, se jí v nabídce nezobrazují."
                + "<H4>Technické požadavky</H4>"
                + "<p>Aplikace je navržená tak, aby měla minimální požadavky na internetové připojení. Systémové požadavy pro počítač jsou alespoň Windows Vista SP2 nebo Mac OS X 10.8.3. Minimální potřebná velikost obrazovky je 300&nbsp;x&nbsp;300&nbsp;pixelů.</p>"
                + "<H4>Kontaktní údaje</H4>"
                + "<p>V případě jakýchkoli problémů s aplikací nás neváhejte kontaktovat, rádi vám pomůžeme:</p>"
                + "<p>E-mail: xkozel00@jaroska.cz</p>"
                + "<p>Kontaktní adresa: třída Kapitána Jaroše 1829/14, 658&nbsp;70 Brno</p>"+
                "</div>"));
        //
        setSizeFull();
        getStyle().set("text-align", "justify");
        getStyle().set("vertical-align", "text-top");
    }

}
