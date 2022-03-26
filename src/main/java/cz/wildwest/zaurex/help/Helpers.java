package cz.wildwest.zaurex.help;

import cz.wildwest.zaurex.views.homePage.HomePageView;
import cz.wildwest.zaurex.views.warehouse.WarehouseView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Helpers {
    private Helpers() {}

    private final static Map<Class<?>, Helper> HELPER_MAP;

    static {
        HELPER_MAP = new HashMap<>();
        buildHelpers();
    }

    private static void buildHelpers() {
        HELPER_MAP.put(WarehouseView.class, new Helper(
                    "<p>Zde můžete zjistit, jaké věci se aktuálně nachází na skladě. Mohou se zde přidávat i odstraňovat produkty a varianty.</p>"
            + "<h4>Přístup</h4>" +
                    "<p>Plný přístup sem má pouze manažer. Pokladník se sem může podívat kolik je kterého produktu, ale nemůže tu nic upravovat.</p>" +
            "<h4>Ovládací prvky</h4>" +
                "<h5>Zobrazení</h5>" +
                        "<p>Nahoře nad seznamem je tlačítko \"Zobrazit\", díky kterému si můžete nastavit, které sloupce se mají zobrazovat. Změníte stav snadno, když kliknete na vlastnost, kterou chcete změnit, z nabídky, která se vám objeví.</p>"+           
                "<h5>Přidávání nového zboží</h5>" +
                        "<p>Nové zboží se dá přidat tlačítkem v pravém dolním roku obrazovky Nové zboží, anebo klávesovou zkratkou <span class\"kzkratka\">Alt + N</span>. U nového zboží je potřeba nastavit název a vybrat, do jaké kategorie zboží patří. Nepovinné pole je krátký popis, kde můžete výrobek více specifikovat. Pokud rovnou chcete přidat variantu, rozklikněte \"Varianty\" a stiskněte přidat variantu. Každá varianta potřebuje mít název (barvu), cenu a množství (může být i 0). Nepovinné je napsání velikosti nebo nějaké jiné poznámky, která specifikuje danou variantu.</p>"
                + "<h5>Upravování již existujícího zboží</h5>"
                        + "<p>Upravovat určité zboží (název, popis a přidávání, úprava či odstraňování variant) se dá po kliknutí na určité zboží. Před uzavřením nesmíte zapomenout své změny uložit, jinak budou zahozeny. Ukládají se tlačítkem v pravém dolním rohu okna na upravování zboží. Pokud se vám okno na upravování zboží nedaří otevřít, zkontrolujte, zda náhodou nemáte zapnutou možnost Vícenásobného výběru.</p>"
                + "<h5>Odstraňování zboží</h5>"
                        + "<p>Pokud kupříkladu je zboží vloženo chybně, anebo dojde a nechcete, aby dělalo seznam nepřehledný, můžete ho odstranit. Pokud chcete odstranit variantu, můžete tak jednoduše učinit kliknutím na červený křížek napravo od nic v oknu na upravování zboží. Pokud chcete odstranit celé zboží, bez ohledu na to, kolik má variant, jde to udělat tlačítkem \"Odstranit...\", které naleznete při levém dolním okraji okna na upravování zboží. Další možnost jak zboží odstranit ze seznamu, je přes Vícenásobný výber, který je popsán níže.</p>"
                + "<h5>Vícenásobný výběr</h5>"
                        + "<p>Jde o možnost odstranit více kusů zboží zároveň. Dá se spusti buď stisknutím tlačítka \"Možnosti\", anebo klávesou zkratkou <span class\"kzkratka\">Alt + S</span>. Po aktivaci této možnosti by se měl u levého okraje řádku se zbožím objevit čtvereček, do kterého pokud kliknete, danou položku označíte. Označené položky pak můžete snadno odstraniť červeným tlačítkem \"Odstranit vybrané\", které se objeví u dolního okraje obrazovky nalevo od tlačítka \"Nové zboží\". </p>"
 ));
        HELPER_MAP.put(HomePageView.class, new Helper(
                    "<p>Toto je nápověda, která vám vždy ujasní základní pokyny k obsluze dané stránky, pokud by to z ní samotné nebylo jasné. Také se tam dozvíte, jaké osoby na danou stránku mají přístup a jaké tam mají pravomoce. Na této stránce jsou základní informace o fungování systému.</p>"
        ));
    }

    public static Optional<Helper> getHelper(Class<?> view) {
        if (!hasHelper(view)) return Optional.empty();
        return Optional.of(HELPER_MAP.get(view));
    }

    public static boolean hasHelper(Class<?> view) {
        return HELPER_MAP.containsKey(view);
    }
}
