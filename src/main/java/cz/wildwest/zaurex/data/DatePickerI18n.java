package cz.wildwest.zaurex.data;

import com.vaadin.flow.component.datepicker.DatePicker;

import java.util.List;
import java.util.stream.Collectors;

public class DatePickerI18n {

    private DatePickerI18n() {}

    public static final DatePicker.DatePickerI18n DATE_PICKER_I_18_N;

    static {
        DATE_PICKER_I_18_N = new DatePicker.DatePickerI18n();
        DATE_PICKER_I_18_N.setMonthNames(List.of(
                "leden", "únor", "březen", "duben", "květen", "červen", "červenec", "srpen", "září", "říjen", "listopad", "prosinec"
        ));
        DATE_PICKER_I_18_N.setWeekdays(List.of(
                "pondělí", "úterý", "středa", "čtvrtek", "pátek", "sobota", "neděle"
        ));
        DATE_PICKER_I_18_N.setWeekdaysShort(DATE_PICKER_I_18_N.getWeekdays().stream().map(s -> s.substring(0, 2)).collect(Collectors.toList()));
        DATE_PICKER_I_18_N.setFirstDayOfWeek(0);
        DATE_PICKER_I_18_N.setWeek("týden");
        DATE_PICKER_I_18_N.setToday("dnes");
        DATE_PICKER_I_18_N.setCancel("zrušit");
    }
}
