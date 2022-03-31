package cz.wildwest.zaurex.components.gridd;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class NumberGriddCell extends HorizontalLayout {
    public NumberGriddCell(String text) {
        super(new Span(text));
        setJustifyContentMode(JustifyContentMode.END);
    }
}
