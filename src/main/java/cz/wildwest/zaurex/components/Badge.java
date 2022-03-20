package cz.wildwest.zaurex.components;

import com.vaadin.flow.component.html.Span;

public class Badge extends Span {

    public Badge(String text, BadgeVariant badgeVariant) {
        super(text);
        getElement().getThemeList().add("badge " + badgeVariant.clazz);
        addClassName("custom-badge");
    }

    public enum BadgeVariant {
        DEFAULT(""), SUCCESS("success"), ERROR("error"), CONTRAST("contrast");

        private final String clazz;

        BadgeVariant(String clazz) {
            this.clazz = clazz;
        }
    }
}
