package cz.wildwest.zaurex.components;

import com.vaadin.flow.component.html.Span;

public class Badge extends Span {

    public Badge(String text, BadgeVariant badgeVariant) {
        super(text);
        getElement().getThemeList().add("badge " + badgeVariant.clazz);
        addClassName("custom-badge");
        if (badgeVariant == BadgeVariant.COUNTER) getStyle().set("margin-inline-start", "var(--lumo-space-s)");
    }

    public enum BadgeVariant {
        DEFAULT(""), SUCCESS("success"), ERROR("error"), CONTRAST("contrast"), COUNTER("badge pill small contrast");

        private final String clazz;

        BadgeVariant(String clazz) {
            this.clazz = clazz;
        }
    }
}
