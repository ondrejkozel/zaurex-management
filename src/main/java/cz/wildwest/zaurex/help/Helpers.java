package cz.wildwest.zaurex.help;

import cz.wildwest.zaurex.views.employees.EmployeesView;
import cz.wildwest.zaurex.views.holidays.HolidaysView;
import cz.wildwest.zaurex.views.holidaysForApproval.HolidaysForApprovalView;
import cz.wildwest.zaurex.views.homePage.HomePageView;
import cz.wildwest.zaurex.views.invoices.InvoicesView;
import cz.wildwest.zaurex.views.sell.SellView;
import cz.wildwest.zaurex.views.warehouse.QuickAddView;
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
    
    private final static String ZOBRAZENI = 
                "<h4>Ovládací prvky</h4>"
            +   "<h5>Zobrazení</h5>" +
                "<p>Nahoře nad seznamem je tlačítko <b>Zobrazit</b>, díky kterému si můžete nastavit, které sloupce mají být viditelné.</p>"
            ;
    //
    //
    private static String multipleChoice(String customText) {
        return String.format(
                "<h5>Vícenásobný výběr</h5>"
            +   "<p>Jde o možnost odstranit více %s zároveň. Dá se spusti buď stisknutím tlačítka <b>Možnosti</b> nebo klávesou zkratkou <code>Alt&nbsp;+&nbsp;S</code>. Označené položky pak můžete snadno odstraniť červeným tlačítkem <b>Odstranit vybrané</b>. </p>"
            ,   customText
        );
    }
    //
    //    
    private static String addNew(String customText, String anotherCustomText) {
        return String.format(
                "<h5>Přidání %s</h5>" 
            +   "<p>%s se přidá tlačítkem v pravém dolním roku obrazovky <b>%s</b> nebo klávesovou zkratkou <code>Alt&nbsp;+&nbsp;N</code>."
            ,   customText
            ,   anotherCustomText
            ,   anotherCustomText
        );
    }
    //
    // 
    private static String delete(String customText, String anotherCustomText, String differentCustomText) {
        return String.format(
                "<h5>Odstraňování %s</h5>" 
            +   "<p>Pokud chcete odstranit %s, lze to udělat tlačítkem <b>Odstranit...</b>, které naleznete v okně na upravování %s. Další možnost jak položku odstranit ze seznamu je přes <i>Vícenásobný výběr</i>, který je popsán níže."
            ,   customText
            ,   anotherCustomText
            ,   differentCustomText
        );
    }
    //
    //
        
    private static void buildHelpers() {
        HELPER_MAP.put(WarehouseView.class, new Helper(
                "<p>Zde můžete zjistit, jaké věci se aktuálně nachází na skladě. Mohou se zde přidávat i odstraňovat produkty a varianty.</p>"
            +   ZOBRAZENI 
            +   addNew("nového zboží", "Nové zboží")
            +   "U nového zboží je potřeba nastavit název a vybrat, do jaké kategorie zboží patří. Nepovinné pole je krátký popis, kde můžete výrobek více specifikovat. Pokud rovnou chcete přidat variantu, rozklikněte <b>Varianty</b> a stiskněte přidat variantu. Každá varianta potřebuje mít název (barvu), cenu a množství (může být i 0). Nepovinné je napsání velikosti nebo nějaké jiné poznámky, která specifikuje danou variantu.</p>"
            +   "<h6>Naskladnění již existujícího zboží</h6>"
            +   "<p>Již existující zboží lze snadno přidat pomocí tlačítka <b>Rychle naskladnit</b>. Pokud ho zmáčknete při držení klávesy <code>Alt</code>, otevře se nová stránka.</p>"
            +   "<h5>Upravování již existujícího zboží</h5>"
            +   "<p>Upravovat určité zboží (název, popis a přidávání, úprava či odstraňování variant) se dá po kliknutí na určité zboží. Před uzavřením nesmíte zapomenout své změny uložit, jinak budou zahozeny. Ukládají se tlačítkem v pravém dolním rohu okna na upravování zboží. Pokud se vám okno na upravování zboží nedaří otevřít, zkontrolujte, zda náhodou nemáte zapnutou možnost <i>Vícenásobného výběru</i>.</p>"
            +   delete("zboží","celé zboží, bez ohledu na to, kolik má variant","zboží")
            +   multipleChoice("věcí ze sklad")
        ));
        //
        //
        HELPER_MAP.put(HomePageView.class, new Helper(
                    "<p>Toto je nápověda, která vám vždy ujasní základní pokyny k obsluze dané stránky, pokud by to z ní samotné nebylo jasné. Dají se spustit buď otazníkem v pravém horním rohu nebo klávesovou zkratkou <code>Alt&nbsp;+&nbsp;H</code>. Na této stránce jsou základní informace o fungování systému.</p>"
        ));
        //
        //
        HELPER_MAP.put(SellView.class, new Helper(
                "<p>Zde můžete prodávat produkty ze skladu.</p>"
            +   "<h4>Ovládací prvky</h4>" 
            +   "<h5>Přidávání zboží k prodeji</h5>" 
            +   "<p> Zboží přidáte k prodeji možností <b>Přidat položku</b>. U prodávaného produktu musíte vybrat nejprve název zboží, následně variantu a nakonec množství. Program by vám neměl dovolit prodat více zboží, než je na skladě a pokud tedy tento údaj zadáte chybně, sám vás opraví na maximum možné </p>"           
            +   "<h5>Vymazávání položky na prodej</h5>"
            +   "<p> Pokud si kupříkladu zákazník nákup určité položky rozmyslel, dá se snadno odstranit červeným křížkem napravo od položky. Pokud chcete vymazat celý seznam, stačí stisknout ikonku červeného koštěte </p>"
            +   "<h5>Přidání kontaktních údajů zákazníka</h5>" 
            +   "<p> Pokud chcete přidat kontaktní údaje na zákazníka, jde to snadno přes možnost <b>Specifikovat odběratele</b>.  </p>"
            +   "<h5>Vytvoření PDF faktury</h5>" 
            +   "<p> PDF faktura se dá vytvořit po provedení transakce stiskutím tlačítka v levém dolním rohu, anebo zpětně na stránce <b>Faktury</b>. </p>"              
            +   "<h5>Platební metoda</h5>"
            +   "<p> Při prodeji položek je zapotřebí vybrat z nabídky způsob platby. </p>"    
            +   "<h5>Potvrzení prodeje</h5>" 
            +   "<p> Prodej se provede pouze pokud obědnávka splňuje všechny požadavky po stisknutí tlačítka <b>Prodat</b></p>" 
        ));
        //
        //
        HELPER_MAP.put(HolidaysView.class, new Helper(
                "<p>Zde můžete požádat o dovolenou</p>"
            +   "<h4>Schvalování</h4>"
            +   "<p>Každá dovolená má tři různé stavy: nevyřízeno, schváleno, zamítnuto. Manažer také může připojit vlastní poznámku, proč dovolenou zamítl, anebo schválil</p>" 
            +   ZOBRAZENI 
            +   addNew("nové žádosti o dovolenou", "Nová dovolená")
            +   "U nové dovolené je potřeba nastavit datum a volitelně poznámku, kterou uvidí manažer.</p>"
            +   "<h5>Upravování již existující žádosti o dovolenou</h5>"
            +   "<p>Upravování vámi podané žádosti je možné po kliknutí na určitou žádost. Tato možnost je pouze, pokud dovolená ještě nenastala. Pokud dovolená již byla schválena a vy ji přesto upravíte, musí být znovu schválena. Pokud se vám okno na upravování dovolené nedaří otevřít, zkontrolujte, zda náhodou nemáte zapnutou možnost <i>Vícenásobného výběru</i>.</p>"
            +   delete("žádostí o dovolenou","určitou žádost o dovolenou","určité dovolené")
            +   multipleChoice("žádostí o dovolenou")
        ));
        //
        //
        HELPER_MAP.put(EmployeesView.class, new Helper(
                "<p>Zde můžete přidávat, upravovat a odstraňovat zaměstnance. Dají se zde také obnovit zapomenutá hesla.</p>"
            +   ZOBRAZENI
            +   "<h5>Zapomenuté heslo</h5>" 
            +   "<p>Pokud zaměstnanec zapomněl heslo, stáčí jednoduše kliknout na jeho řádek a na pravé straně okna, které se vám objeví, naleznete možnost <b>Obnovit heslo</b>. Pokud tuto možnost vyberete, heslo daného zaměstnance se změní na \"heslo\". Doporučte okamžitě po přihlášení zaměstnanci heslo změnit, protože \"heslo\" není nejbezpečnější heslo, obzlášť pokud by ho mělo více pracovníků.</h5>" 
            +   addNew("nového zaměstnance", "Nový zaměstnanec")+"U nového zaměstnance musíte vyplnit přihlašovací jméno (bude sloužit jako login) a jeho jméno (jak má být v seznamu veden). Mohou se mu nastavit role. Heslo takto vzniklého zaměstnance bude \"heslo\", dokud si ho sám nezmění.</p>"
            +   "<h5>Upravování již existující zaměstnanců</h5>"
            +   "<p>Upravování parametrů zaměstnance (login, jméno a role) je možné po kliknutí na určitou osobu. Pokud se vám okno na upravování záznamu zaměstnanců nedaří otevřít, zkontrolujte, zda náhodou nemáte zapnutou možnost <i>Vícenásobného výběru</i>.</p>"
            +   delete("zaměstnanců","nepohodlného zaměstnance","zaměstnanců")
            +   multipleChoice("zaměstnanců")
        ));
        //
        //
        HELPER_MAP.put(InvoicesView.class, new Helper(
                "<p>Zde si můžete prohlížet faktury nákupů, které proběhly přes pokladnu.</p>"
            +   ZOBRAZENI 
            +   "<h5>Zobrazení detailu platby</h5>"
            +   "<p>Prohlížení detailů aktivity je možné po kliknutí na určitou aktivitu. Stejným způsobem se dá vytvářet i PDF faktura.</p>"
            +   "<h5>Vytváření PDF faktury z archivu</h5>"
            +   "<p>Soubor ve formátu PDF se dá snadno vytvořit zapomocí kliknutí na ikonu PDF ve sloupci PDF a řádku transakce, ze které fakturu chcete vytvořit. Druhá možnost je přes prohlídnutí si detailů aktivity.</p>"	
        ));
        //
        //
        HELPER_MAP.put(HolidaysForApprovalView.class, new Helper(
                "<p>Zde můžete nastavovat dovolenou a schvalovat žádosti o dovolené od zaměstnanců</p>"
            +   ZOBRAZENI 
            +   " Možnost <b>Pouze nevyřízené</b>, která skryje vyřízené žádosti. </p>"
            +   addNew("nové žádosti o dovolenou", "Nová dovolená")
            +   "U nové dovolené je potřeba nastavit datum. Jako manažer máte právo vytvářet jakémukoli zaměstnanci dovolenou.</p>"
            +   "<h5>Upravování již existující žádosti o dovolenou a schvalování</h5>"
            +   "<p>Upravování žádostí je možné po kliknutí na určitou žádost. Stejným způsobem se dá žádost i schvalovat nebo zamítat. Pokud se vám okno na upravování dovolené nedaří otevřít, zkontrolujte, zda náhodou nemáte zapnutou možnost Vícenásobného výběru.</p>"
            +   delete("žádostí o dovolenou","určitou žádost o dovolenou","určité dovolené")
            +   multipleChoice("žádostí o dovolenou a obecně dovolených")
        ));
        //
        //
        HELPER_MAP.put(QuickAddView.class, new Helper(
                "<p>Zde můžete rychle naskladňovat již existující zboží, nelze zde vytvářet nové zboží, ani nové varianty.</p>"
            +   "<h4>Ovládací prvky</h4>"
            +    "<p>Naskladňovat již existující zboží zde můžete celkem jednoduše nejdříve vybráním názbu zboží, klávesnice vám může pomoct s vyhledáváním, následně musíte vyplnit variantu a na závěr počet. Takto definované zboží přidáte jednoduše tlačítkem <b>Potvrdit</b></p>"
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
