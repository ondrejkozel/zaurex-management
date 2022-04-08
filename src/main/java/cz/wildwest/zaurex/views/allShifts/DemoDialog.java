package cz.wildwest.zaurex.views.allShifts;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.function.SerializableConsumer;
import org.vaadin.stefan.fullcalendar.Delta;
import org.vaadin.stefan.fullcalendar.Entry;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class DemoDialog extends Dialog {
	private static final long serialVersionUID = 1L;

    private SerializableConsumer<Entry> onSaveConsumer;

    private SerializableConsumer<Entry> onDeleteConsumer;

    private final Entry tmpEntry;

    private final CustomDateTimePicker fieldStart;
    private final CustomDateTimePicker fieldEnd;
    private final Binder<Entry> binder;
    private final Entry entry;
    private boolean initTimeWhenActivated;

    public DemoDialog(Entry entry, boolean newInstance) {
        this.entry = entry;
        this.initTimeWhenActivated = entry.isAllDay();

        // tmp entry is a copy. we will use its start and end to represent either the start/end or the recurring start/end
        this.tmpEntry = entry.copy();

        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        setWidth("500px");

        // init fields

        TextField fieldTitle = new TextField("Title");
        Checkbox fieldAllDay = new Checkbox("All day event");

        fieldStart = new CustomDateTimePicker("Start");
        fieldEnd = new CustomDateTimePicker("End");

//        boolean allDay = this.tmpEntry.isAllDay();
//        fieldStart.setDateOnly(allDay);
//        fieldEnd.setDateOnly(allDay);

        Span infoEnd = new Span("End is always exclusive, e.g. for a 1 day event you need to set for instance 4th of May as start and 5th of May as end.");
        infoEnd.getStyle().set("font-size", "0.8em");
        infoEnd.getStyle().set("color", "gray");

        // layouting - MUST be initialized here, otherwise might lead to null pointer exception
        /*, fieldRecurring*/
        VerticalLayout componentsLayout = new VerticalLayout(fieldTitle,
                new HorizontalLayout(fieldAllDay/*, fieldRecurring*/),
                fieldStart, fieldEnd, infoEnd);

        componentsLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        componentsLayout.setSizeFull();
        componentsLayout.setSpacing(false);

        fieldAllDay.addValueChangeListener(event -> {
            fieldStart.setDateOnly(event.getValue());
            fieldEnd.setDateOnly(event.getValue());

            if (initTimeWhenActivated && !event.getValue()) {
                initTimeWhenActivated = false; // init the time once with "now"
                fieldStart.setValue(fieldStart.getValue().toLocalDate().atTime(LocalTime.now()));
                fieldEnd.setValue(fieldEnd.getValue().toLocalDate().atTime(LocalTime.now().plusHours(1)));
            }
        });


        // init binder

        binder = new Binder<>(Entry.class);

        // required fields
        binder.forField(fieldTitle).asRequired().bind(Entry::getTitle, Entry::setTitle);
        binder.forField(fieldStart).asRequired().bind(Entry::getStart, Entry::setStart);
        binder.forField(fieldEnd).asRequired().bind(Entry::getEnd, Entry::setEnd);

        // optional fields
        binder.bind(fieldAllDay, Entry::isAllDay, Entry::setAllDay);

        binder.setBean(this.tmpEntry);

        fieldStart.addValueChangeListener(event -> {
            LocalDateTime oldStart = event.getOldValue();
            LocalDateTime newStart = event.getValue();
            LocalDateTime end = fieldEnd.getValue();


            if (oldStart != null && newStart != null && end != null) {
                Delta delta = Delta.fromLocalDates(oldStart, newStart);
                end = delta.applyOn(end);
                fieldEnd.setValue(end);
            }
        });

        // init buttons
        Button buttonSave = new Button("Save");
        buttonSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonSave.addClickListener(e -> onSave());

        Button buttonCancel = new Button("Cancel", e -> close());
        buttonCancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttons = new HorizontalLayout(buttonSave, buttonCancel);
        buttons.setPadding(true);
        buttons.getStyle().set("border-top", "1px solid #ddd");

        if (!newInstance) {
            Button buttonRemove = new Button("Remove", e -> onRemove());
            buttonRemove.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            buttons.add(buttonRemove);
        }

        Scroller scroller = new Scroller(componentsLayout);
        VerticalLayout outer = new VerticalLayout();
        outer.add(scroller, buttons);
        outer.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        outer.setFlexGrow(1, scroller);
        outer.setSizeFull();
        outer.setPadding(false);
        outer.setSpacing(false);

        add(outer);

        fieldTitle.focus();
    }

    protected void onSave() {
        if (onSaveConsumer == null) {
            throw new UnsupportedOperationException("No save consumer set");
        }

        if (binder.validate().isOk()) {
            // to prevent accidentally "disappearing" days
            if (this.tmpEntry.isAllDay() && this.tmpEntry.getStart().toLocalDate().equals(this.tmpEntry.getEnd().toLocalDate())) {
                this.tmpEntry.setEnd(this.tmpEntry.getEnd().plusDays(1));
            }
            onSaveConsumer.accept(this.entry);
            close();
            entry.copyFrom(tmpEntry);
        }
    }

    protected void onRemove() {
        if (onDeleteConsumer == null) {
            throw new UnsupportedOperationException("No remove consumer set");
        }
        onDeleteConsumer.accept(entry);
        close();
    }

    public void setDeleteConsumer(SerializableConsumer<Entry> onDeleteConsumer) {
        this.onDeleteConsumer = onDeleteConsumer;
    }

    public void setSaveConsumer(SerializableConsumer<Entry> onSaveConsumer) {
        this.onSaveConsumer = onSaveConsumer;
    }
}
