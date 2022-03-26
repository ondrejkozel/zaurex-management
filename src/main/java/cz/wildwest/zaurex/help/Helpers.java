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
        //jak tvořit nápovědu:
        HELPER_MAP.put(HomePageView.class, new Helper(
                "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Vestibulum fermentum tortor id mi. " +
                        "Maecenas fermentum, sem in pharetra pellentesque, velit turpis volutpat ante, in pharetra metus odio a lectus.",
                "Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos hymenaeos. " +
                        "Vivamus ac leo pretium faucibus. Sed convallis magna eu sem." +
                        "Integer malesuada. Mauris elementum mauris vitae tortor. Sed convallis magna eu sem. Etiam neque. " +
                        "Aenean fermentum risus id tortor. Mauris dictum facilisis augue. Et harum quidem rerum facilis est et expedita distinctio. " +
                        "Nullam feugiat, turpis at pulvinar vulputate, erat libero tristique tellus, nec bibendum odio risus sit amet ante. " +
                        "Sed elit dui, pellentesque a, faucibus vel, interdum nec, diam. Etiam dictum tincidunt diam."
        ));
        HELPER_MAP.put(WarehouseView.class, new Helper(
                "Nápověda skladu, blah blah",
                //dlouhý text klidně může zůstat prázdný:
                ""
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
