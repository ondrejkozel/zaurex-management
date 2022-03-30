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
                + "<p>Aplikace byla vytvořena do nultého ročníku soutěže Tour de App. Aplikaci vytvořil tým Brněnský divoký západ ze školy Gymnázium Brno, třída Kapitána Jaroše. Členy týmu jsou studenti třetího ročníku čtyletého studia Ondřej Kozel, který se zasloužil o funkčnost aplikace a Marek Přibyl, který mu nosil pití.</p>"
                + "<H4>Uživatelská příručka</H4>"
                + "Na každé stránce v pravém horním rohu se nachází jednoduché vysvětlení, co se na dané straně nachází a jak se obsluhuje. Stránky, na které daná osoba nemá přístup, se jí ani v nabídce nezobrazují. Ve stručnosti na stránce \"Prodat\" může člověk prodávat zboží, co je na skladě. Faktura o prodeji se uloží mezi faktury. Na sklad ho můžete dostat přes stranu \"Sklad\", kde člověk může zboží přidávat, upravovat nebo mazat (nedostane z toho fakturu, tedy tato strana neslouží k prodeji zboží) a kde je seznam, co je aktuálně na skladě. V položce \"Vaše směny\" si můžete zobrazit rozdělení vlastních směn. Na stránce \"Všechny směny\" se dají rozdělovat a upravovat směny pracovníků. Na stránce \"Dovolené\" může člověk podávat, upravovat a manažer schvalovat dovolené. Stránka \"Faktury\" slouží jako jakýsi archiv všech faktur veškerých prodejů zboží. V neposlední řadě je tu stránka \"Zaměstanci\", kde manažer může upravovat, přidávat i mazat zaměstnance ze systému. Také zde jde vygenerovat nové heslo zaměstnanci, který své staré nešťastnou náhodou zapomněl. Hesla se dají snadno měnit v \"Nastavení\"."
                + "<H4>Technické požadavky</H4>"
                + "<p>Aplikace je navržená tak, aby měla minimální požadavky na internetové připojení. Systémové požadavy jsou alespoň Windows Vista SP2 nebo Mac OS X 10.8.3. Minimální potřebná velikost obrazovky je 300&nbsp;x&nbsp;300&nbsp;pixelů, což prakticky všechny chytré mobily mají. Pro orientace, nejmenší dotykové telefony mají šířku přes 450&nbsp;px a výšku okolo 1000&nbsp;px.</p>"
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
