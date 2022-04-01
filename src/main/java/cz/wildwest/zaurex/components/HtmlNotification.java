package cz.wildwest.zaurex.components;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.notification.Notification;

public class HtmlNotification {
    public static void show(String html) {
        Notification notification = new Notification();
        notification.setDuration(5000);
        notification.add(new Html(html));
        notification.open();
    }
}
