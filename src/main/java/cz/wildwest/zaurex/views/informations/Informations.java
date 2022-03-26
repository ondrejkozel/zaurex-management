package cz.wildwest.zaurex.views.informations;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.views.MainLayout;
import javax.annotation.security.PermitAll;

@PageTitle("Informace k používání")
@Route(value = "informations", layout = MainLayout.class)
@PermitAll
public class Informations extends VerticalLayout {

    public Informations() {
        setSpacing(false);
        //
        add(new H1("Vítejte v uživatelské příručce"));
        add(new Paragraph("Zde se nachází podrobný návod k používání této tohoto systému"));
        //
        add(new H2("Sklad"));
        //        
        add(new H3("Obecný popis"));
        add(new Paragraph("Zde můžete zjistit, jaké věci se aktuálně nachází na skladě. Mohou se zde přidávat i odstraňovat zboží a jeho varianty. "));
        //        
        add(new H3("Přístup"));
        add(new Paragraph("Plný přístup sem má pouze manažer. Pokladník se sem může podívat kolik je kterého produktu, ale nemůže tu nic upravovat."));
        //     
        add(new H3("Ovládací prvky"));
        add(new H4("Přidávání nového zboží"));
        add(new Paragraph("Nové zboží se dá přidat tlačítkem v pravém dolním roku obrazovky Nové zboží, anebo klávesovou zkratkou alt + N. U nového zboží je potřeba nastavit název a vybrat, do jaké kategorie zboží patří. Nepovinné pole je krátký popis, kde můžete výrobek více specifikovat. Pokud rovnou chcete přidat variantu, rozklikněte \"Varianty\" a stiskněte přidat variantu. Každá varianta potřebuje mít název (barvu), cenu a množství (může být i 0). Nepovinné je napsání velikosti nebo nějaké jiné poznámky, která specifikuje danou variantu."));
        add(new H4("Upravování již existujícího zboží"));
        add(new Paragraph("Pokud chcete upravit již existující zboží, stačí na něj jednoduše kliknout. Můžete tam upravovat nejen zboží samotné, ale i jeho varynty. Ty se zde dají nejen upravovat, ale i pridávat (opět stiskutím přidat variantu), anebo odebrat variantu (stsknutím červeného křížku napravo od varianty). Všechny změny musejí být uloženy tlačítkem Uložit, jinak nebudou aplikovány."));
        add(new H4("Odstraňování zboží"));
        add(new Paragraph("Doplním až mit to zase pojede"));
        //
        //
        add(new H2("Dovolené"));
        //        
        add(new H3("Obecný popis"));
        add(new Paragraph("Dopním až mi to zase bude fungovat, moc se omlouvám, že jsou se mnou takové komplikace"));
        //        
        
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "left");
    }

}
