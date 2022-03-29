package cz.wildwest.zaurex.help;

import cz.wildwest.zaurex.views.employees.EmployeesView;
import cz.wildwest.zaurex.views.holidays.HolidaysView;
import cz.wildwest.zaurex.views.holidaysForApproval.HolidaysForApprovalView;
import cz.wildwest.zaurex.views.homePage.HomePageView;
import cz.wildwest.zaurex.views.settings.SettingsView;
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
                        "<p>Nové zboží se dá přidat tlačítkem v pravém dolním roku obrazovky Nové zboží, anebo klávesovou zkratkou <code>Alt + N</code>. U nového zboží je potřeba nastavit název a vybrat, do jaké kategorie zboží patří. Nepovinné pole je krátký popis, kde můžete výrobek více specifikovat. Pokud rovnou chcete přidat variantu, rozklikněte \"Varianty\" a stiskněte přidat variantu. Každá varianta potřebuje mít název (barvu), cenu a množství (může být i 0). Nepovinné je napsání velikosti nebo nějaké jiné poznámky, která specifikuje danou variantu.</p>"
                + "<h5>Upravování již existujícího zboží</h5>"
                        + "<p>Upravovat určité zboží (název, popis a přidávání, úprava či odstraňování variant) se dá po kliknutí na určité zboží. Před uzavřením nesmíte zapomenout své změny uložit, jinak budou zahozeny. Ukládají se tlačítkem v pravém dolním rohu okna na upravování zboží. Pokud se vám okno na upravování zboží nedaří otevřít, zkontrolujte, zda náhodou nemáte zapnutou možnost Vícenásobného výběru.</p>"
                + "<h5>Odstraňování zboží</h5>"
                        + "<p>Pokud kupříkladu je zboží vloženo chybně, anebo dojde a nechcete, aby dělalo seznam nepřehledný, můžete ho odstranit. Pokud chcete odstranit variantu, můžete tak jednoduše učinit kliknutím na červený křížek napravo od nic v oknu na upravování zboží. Pokud chcete odstranit celé zboží, bez ohledu na to, kolik má variant, jde to udělat tlačítkem \"Odstranit...\", které naleznete při levém dolním okraji okna na upravování zboží. Další možnost jak zboží odstranit ze seznamu, je přes Vícenásobný výber, který je popsán níže.</p>"
                + "<h5>Vícenásobný výběr</h5>"
                        + "<p>Jde o možnost odstranit více kusů zboží zároveň. Dá se spusti buď stisknutím tlačítka \"Možnosti\", anebo klávesou zkratkou <code>Alt + S</code>. Po aktivaci této možnosti by se měl u levého okraje řádku se zbožím objevit čtvereček, do kterého pokud kliknete, danou položku označíte. Označené položky pak můžete snadno odstraniť červeným tlačítkem \"Odstranit vybrané\", které se objeví u dolního okraje obrazovky nalevo od tlačítka \"Nové zboží\". </p>"
        ));
        HELPER_MAP.put(SettingsView.class, new Helper(
                    "<p>Na této stránce není zatím nic víc, než měnění hesla. Pro změnění hesla zadejte nové heslo do rámečku a stiskněte potvrdit.</p>"
            ));
        HELPER_MAP.put(HomePageView.class, new Helper(
                    "<p>Toto je nápověda, která vám vždy ujasní základní pokyny k obsluze dané stránky, pokud by to z ní samotné nebylo jasné. Také se tam dozvíte, jaké osoby na danou stránku mají přístup a jaké tam mají pravomoce. Na této stránce jsou základní informace o fungování systému.</p>"
        ));
        HELPER_MAP.put(HolidaysView.class, new Helper(
                    "<p>Zde můžete požádat o dovolenou</p>"
            + "<h4>Kdo má jaké pravomoce?</h4>" +
                    "<p>O dovolenou může požádat každý. Dovolené schvaluje manažer.</p>" +
		 "<h4>Schvalování</h4>"+
		"<p>Každá dovolená má tři různé stavy: Nevyřízen, Schváleno, zamítnuto. Manažer také může připojit vlastní poznámku, proč dovolenou zamítl, anebo schválil</p>" +
            "<h4>Ovládací prvky</h4>" +
                "<h5>Zobrazení</h5>" +
                        "<p>Nahoře nad seznamem je tlačítko \"Zobrazit\", díky kterému si můžete nastavit, které sloupce se mají zobrazovat. Změníte stav snadno, když kliknete na vlastnost, kterou chcete změnit, z nabídky, která se vám objeví. </p>"+           
                "<h5>Přidávání nové dovolené</h5>" +
                        "<p>Nová dovolená se dá přidat tlačítkem v pravém dolním roku obrazovky Nová dovolená, anebo klávesovou zkratkou <code>Alt + N</code>. U nové dovolené je potřeba nastavit datum a poznámku, kterou uvidí manažer. Poznámka je nepovinná</p>"
                + "<h5>Upravování již existující žádosti o dovolenou</h5>"
                        + "<p>Upravování vámi podané žádosti je možné po kliknutí na určitou žádost. Tato možnost je pouze, pokud dovolená ještě nenastala. Pokud dovolená již byla schválena a vy ji přesto upravíte, musí být znovu schválena. Pokud se vám okno na upravování dovolené nedaří otevřít, zkontrolujte, zda náhodou nemáte zapnutou možnost Vícenásobného výběru.</p>"
                + "<h5>Odstraňování dovolené</h5>"
                        + "<p>Dovolenou můžete odstranit. Jde to udělat tlačítkem \"Odstranit...\", které naleznete při levém dolním okraji okna na upravování žádosti o dovolenou. Další možnost jak žádost o dovolenou odstranit, je přes Vícenásobný výber, který je popsán níže.</p>"
                + "<h5>Vícenásobný výběr</h5>"
                        + "<p>Jde o možnost odstranit více žádostí o dovolenou zároveň. Dá se spusti buď stisknutím tlačítka \"Možnosti\", anebo klávesou zkratkou <code>Alt + S</code>. Po aktivaci této možnosti by se měl u levého okraje řádku se žádostí o dovolené objevit čtvereček, do kterého pokud kliknete, danou položku označíte. Označené položky pak můžete snadno odstraniť červeným tlačítkem \"Odstranit vybrané\", které se objeví u dolního okraje obrazovky nalevo od tlačítka \"Nová dovolená\". </p>"

        ));
        HELPER_MAP.put(EmployeesView.class, new Helper(
                    "<p>Zde můžete přidávat, upravovat a odstraňovat zaměstnance. Dají se zde také obnovit zapomenutá hesla.</p>"
            + "<h4>Kdo sem může?</h4>" +
                    "<p>Přístup na tuto stránku má pouze manažer</p>" +
            "<h4>Ovládací prvky</h4>" +
		"<h5>Zapomenuté heslo</h5>" +
		"<p>Pokud zaměstnanec zapomněl heslo, stáčí jednoduše kliknout na jeho řádek a na pravé straně okna, které se vám objeví, možnost obnovit heslo. Pokud tuto možnost vyberete, heslo daného zaměstnance se změní na \"heslo\". Doporučte okamžitě po přihlášení zaměstnanci heslo změnit, protože \"heslo\" není nejbezpečnější heslo, obzlášť pokud by ho mělo více pracovníků.</h5>" +
                "<h5>Zobrazení</h5>" +
                        "<p>Nahoře nad seznamem je tlačítko \"Zobrazit\", díky kterému si můžete nastavit, které sloupce se mají zobrazovat. Změníte stav snadno, když kliknete na vlastnost, kterou chcete změnit, z nabídky, která se vám objeví. </p>"+           
                "<h5>Přidávání nových zaměstnanců</h5>" +
                        "<p>Nový zaměstnanci se dají přidat tlačítkem v pravém dolním roku obrazovky \"Nový zaměstnanec\", anebo klávesovou zkratkou <code>Alt + N</code>. U nového zaměstnance musíte vyplnit přihlašovací jméno (bude sloužit jako login) a jeho jméno (jak má být v seznamu veden). Mohou se mu nastavit role. Heslo takto vzniklého zaměstnance bude \"heslo\", dokud si to sám nezmění. Pokud tedy jste zaměstnavatel, tlačte na své zaměstnance, ať tento úkon udělají co nejdřív pro bezpečnost vaší firmy.</p>"
                + "<h5>Upravování již existující zaměstnanců</h5>"
                        + "<p>Upravování parametrů zaměstnance (login, jméno a role) je možné po kliknutí na určitou osobu. Pokud se vám okno na upravování záznamu zaměstnanců nedaří otevřít, zkontrolujte, zda náhodou nemáte zapnutou možnost Vícenásobného výběru.</p>"
                + "<h5>Odstraňování zaměstnanců</h5>"
                        + "<p>Nepohodlného zaměstnance můžete odstranit. Jde to udělat tlačítkem \"Odstranit...\", které naleznete při levém dolním okraji okna na upravování záznamech o zaměstnancích. Další možnost jak zaměstnance odstranit, je přes Vícenásobný výber, který je popsán níže.</p>"
                + "<h5>Vícenásobný výběr</h5>"
                        + "<p>Jde o možnost odstranit více zaměstnanců ze systému zároveň. Dá se spusti buď stisknutím tlačítka \"Možnosti\", anebo klávesou zkratkou <code>Alt + S</code>. Po aktivaci této možnosti by se měl u levého okraje řádků se záznamy zaměstnanců objevit čtvereček, do kterého pokud kliknete, danou položku označíte. Označené položky pak můžete snadno odstraniť červeným tlačítkem \"Odstranit vybrané\", které se objeví u dolního okraje obrazovky nalevo od tlačítka \"Nový zaměstnanec\". </p>"

        ));
        HELPER_MAP.put(HolidaysForApprovalView.class, new Helper(
                    "<p>Zde můžete nastavovat dovolenou a schvalovat žádosti o dovolené od zaměstnanců</p>"
            + "<h4>Kdo má jaké pravomoce?</h4>" +
                    "<p>O dovolenou může požádat každý. Dovolené schvaluje manažer.</p>" +
		 
            "<h4>Ovládací prvky</h4>" +
                "<h5>Zobrazení</h5>" +
                        "<p>Nahoře nad seznamem je tlačítko \"Zobrazit\", díky kterému si můžete nastavit, které sloupce se mají zobrazovat. Změníte stav snadno, když kliknete na vlastnost, kterou chcete změnit, z nabídky, která se vám objeví. Je tam i možnost \"Pouze nevyřízené\", která skryje vyřízené žádosti. </p>"+           
                "<h5>Přidávání nové dovolené</h5>" +
                        "<p>Nová dovolená se dá přidat tlačítkem v pravém dolním roku obrazovky Nová dovolená, anebo klávesovou zkratkou <code>Alt + N</code>. U nové dovolené je potřeba nastavit datum. Jako manažer máte právo vytvářet jakémukoli zaměstnanci dovolenou.</p>"
                + "<h5>Upravování již existující žádosti o dovolenou a schvalování</h5>"
                        + "<p>Upravování žádostí je možné po kliknutí na určitou žádost. Stejným způsobem se dá žádost i schvalovat nebo zamítat. Pokud se vám okno na upravování dovolené nedaří otevřít, zkontrolujte, zda náhodou nemáte zapnutou možnost Vícenásobného výběru.</p>"
                + "<h5>Odstraňování dovolené</h5>"
                        + "<p>Dovolenou můžete odstranit. Jde to udělat tlačítkem \"Odstranit...\", které naleznete při levém dolním okraji okna na upravování žádosti o dovolenou. Další možnost jak žádost o dovolenou odstranit, je přes Vícenásobný výber, který je popsán níže.</p>"
                + "<h5>Vícenásobný výběr</h5>"
                        + "<p>Jde o možnost odstranit více žádostí o dovolenou zároveň. Dá se spusti buď stisknutím tlačítka \"Možnosti\", anebo klávesou zkratkou <code>Alt + S</code>. Po aktivaci této možnosti by se měl u levého okraje řádku se žádostí o dovolené objevit čtvereček, do kterého pokud kliknete, danou položku označíte. Označené položky pak můžete snadno odstraniť červeným tlačítkem \"Odstranit vybrané\", které se objeví u dolního okraje obrazovky nalevo od tlačítka \"Nová dovolená\". </p>"

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
