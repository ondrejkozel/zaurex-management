package cz.wildwest.zaurex.views;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Span;

/**
 * Simple wrapper to create icons using LineAwesome iconset. See
 * https://icons8.com/line-awesome
 */
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class LineAwesomeIcon extends Span {
    public LineAwesomeIcon(String lineawesomeClassnames) {
        if (!lineawesomeClassnames.isEmpty()) {
            addClassNames(lineawesomeClassnames);
        }
    }
}
