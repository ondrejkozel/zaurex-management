package cz.wildwest.zaurex.views;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class LocalDateTimeFormatter {
    private LocalDateTimeFormatter() {}

    public static final Locale LOCALE = new Locale("cs", "CZ");

    public static DateTimeFormatter ofShortDate() {
        return DateTimeFormatter.ofPattern("dd. MM. yyyy");
    }

    public static DateTimeFormatter ofLongDate() {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).localizedBy(LOCALE);
    }

    public static DateTimeFormatter ofFullDate() {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).localizedBy(LOCALE);
    }

    public static DateTimeFormatter ofShortDateTime() {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).localizedBy(LOCALE);
    }

    public static DateTimeFormatter ofMediumDateTime() {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).localizedBy(LOCALE);
    }
}
