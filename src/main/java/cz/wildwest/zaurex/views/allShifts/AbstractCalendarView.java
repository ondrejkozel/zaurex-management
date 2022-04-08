package cz.wildwest.zaurex.views.allShifts;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.views.LocalDateTimeFormatter;
import elemental.json.Json;
import elemental.json.JsonObject;
import org.vaadin.stefan.fullcalendar.*;
import org.vaadin.stefan.fullcalendar.dataprovider.EagerInMemoryEntryProvider;
import org.vaadin.stefan.fullcalendar.dataprovider.EntryProvider;

import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

/**
 * A basic class for simple calendar views, e.g. for demo or testing purposes. Takes care of
 * creating a toolbar, a description element and embedding the created calendar into the view.
 * Also registers a dates rendered listener to update the toolbar.
 */
public abstract class AbstractCalendarView extends VerticalLayout {
    protected final FullCalendar calendar;
    private final CalendarViewToolbar toolbar;

    // TODO add scheduler support

    public AbstractCalendarView() {
        calendar = createCalendar(createDefaultInitialOptions());

        calendar.addEntryClickedListener(this::onEntryClick);
        calendar.addEntryDroppedListener(this::onEntryDropped);
        calendar.addEntryResizedListener(this::onEntryResized);
        calendar.addDayNumberClickedListener(this::onDayNumberClicked);
        calendar.addBrowserTimezoneObtainedListener(this::onBrowserTimezoneObtained);
        calendar.addMoreLinkClickedListener(this::onMoreLinkClicked);
        calendar.addTimeslotClickedListener(this::onTimeslotClicked);
        calendar.addTimeslotsSelectedListener(this::onTimeslotsSelected);
        calendar.addViewSkeletonRenderedListener(this::onViewSkeletonRendered);
        calendar.addDatesRenderedListener(this::onDatesRendered);
        calendar.addWeekNumberClickedListener(this::onWeekNumberClicked);

        toolbar = new CalendarViewToolbar(
                calendar,
                true,
                false,
                true,
                true,
                false,
                this::onEntriesCreated,
                this::onEntriesRemoved,
                null
        );

        calendar.setTimezone(new Timezone(ZoneId.of("Europe/Paris")));
        calendar.setLocale(LocalDateTimeFormatter.LOCALE);

        calendar.setHeightByParent();

        calendar.addDatesRenderedListener(event -> toolbar.updateInterval(event.getIntervalStart()));

        add(toolbar, calendar);

        setFlexGrow(1, calendar);
        setHorizontalComponentAlignment(Alignment.STRETCH, calendar);

        setSizeFull();
    }

    /**
     * Creates the plain full calendar instance with all initial options. The given default initial options are created by
     * {@link #createDefaultInitialOptions()} beforehand.
     * <p></p>
     * The calender is automatically embedded afterwards and connected with the toolbar (if one is created, which
     * is the default). Also all event listeners will be initialized with a default callback method.
     *
     * @param defaultInitialOptions default initial options
     * @return calendar instance
     */
    protected abstract FullCalendar createCalendar(JsonObject defaultInitialOptions);

    /**
     * Creates a default set of initial options.
     *
     * @return initial options
     */
    protected JsonObject createDefaultInitialOptions() {
        JsonObject initialOptions = Json.createObject();
        JsonObject eventTimeFormat = Json.createObject();
//{ hour: 'numeric', minute: '2-digit', timeZoneName: 'short' }
        eventTimeFormat.put("hour", "2-digit");
        eventTimeFormat.put("minute", "2-digit");
        eventTimeFormat.put("timeZoneName", "short");
        eventTimeFormat.put("meridiem", false);
        eventTimeFormat.put("hour12", false);
        initialOptions.put("eventTimeFormat", eventTimeFormat);
        return initialOptions;
    }

    /**
     * Called by the calendar's entry click listener. Noop by default.
     *
     * @param event event
     * @see FullCalendar#addEntryClickedListener(ComponentEventListener)
     */
    protected void onEntryClick(EntryClickedEvent event) {
    }

    /**
     * Called by the calendar's entry drop listener (i. e. an entry has been dragged around / moved by the user).
     * Applies the changes to the entry and calls {@link #onEntryChanged(Entry)} by default.
     *
     * @param event event
     * @see FullCalendar#addEntryDroppedListener(ComponentEventListener)
     */
    protected void onEntryDropped(EntryDroppedEvent event) {
        event.applyChangesOnEntry();
        onEntryChanged(event.getEntry());
    }

    /**
     * Called by the calendar's entry resize listener.
     * Applies the changes to the entry and calls {@link #onEntryChanged(Entry)} by default.
     *
     * @param event event
     * @see FullCalendar#addEntryResizedListener(ComponentEventListener)
     */
    protected void onEntryResized(EntryResizedEvent event) {
        event.applyChangesOnEntry();
        onEntryChanged(event.getEntry());
    }

    /**
     * Called by the calendar's week number click listener. Noop by default.
     *
     * @param event event
     * @see FullCalendar#addWeekNumberClickedListener(ComponentEventListener)
     */
    protected void onWeekNumberClicked(WeekNumberClickedEvent event) {

    }

    /**
     * Called by the calendar's dates rendered listener. Noop by default.
     * Please note, that there is a separate dates rendered listener taking
     * care of updating the toolbar.
     *
     * @param event event
     * @see FullCalendar#addDatesRenderedListener(ComponentEventListener)
     */
    protected void onDatesRendered(DatesRenderedEvent event) {

    }

    /**
     * Called by the calendar's view skeleton rendered listener. Noop by default.
     *
     * @param event event
     * @see FullCalendar#addViewSkeletonRenderedListener(ComponentEventListener)
     */
    protected void onViewSkeletonRendered(ViewSkeletonRenderedEvent event) {

    }

    /**
     * Called by the calendar's timeslot selected listener. Noop by default.
     *
     * @param event event
     * @see FullCalendar#addTimeslotsSelectedListener(ComponentEventListener)
     */
    protected void onTimeslotsSelected(TimeslotsSelectedEvent event) {

    }

    /**
     * Called by the calendar's timeslot clicked listener. Noop by default.
     *
     * @param event event
     * @see FullCalendar#addTimeslotClickedListener(ComponentEventListener)
     */
    protected void onTimeslotClicked(TimeslotClickedEvent event) {

    }

    /**
     * Called by the calendar's "more" link clicked listener. Noop by default.
     *
     * @param event event
     * @see FullCalendar#addMoreLinkClickedListener(ComponentEventListener)
     */
    protected void onMoreLinkClicked(MoreLinkClickedEvent event) {
    }

    /**
     * Called by the calendar's browser timezone obtained listener. Noop by default.
     * Please note, that the full calendar builder registers also a listener, when the
     * {@link FullCalendarBuilder#withAutoBrowserTimezone()} option is used.
     *
     * @param event event
     * @see FullCalendar#addBrowserTimezoneObtainedListener(ComponentEventListener)
     */
    protected void onBrowserTimezoneObtained(BrowserTimezoneObtainedEvent event) {

    }

    /**
     * Called by the calendar's day number click listener. Noop by default.
     *
     * @param event event
     * @see FullCalendar#addDayNumberClickedListener(ComponentEventListener)
     */
    protected void onDayNumberClicked(DayNumberClickedEvent event) {

    }

    /**
     * Called by the toolbar, when one of the "Create sample entries" button has been pressed to simulate the
     * creation of new data. Might be called by any other source, too.
     * <p></p>
     * Intended to update the used backend. By default it will check, if the used entry provider is eager in memory
     * and in that case automatically update the entry provider (to prevent unnecessary code duplication when
     * the default entry provider is used).
     *
     * @param entries entries to add
     */
    protected void onEntriesCreated(Collection<Entry> entries) {
        // The eager in memory provider provider provides API to modify its internal cache and takes care of pushing
        // the data to the client - no refresh call is needed (or even recommended here)
        if (getCalendar().isEagerInMemoryEntryProvider()) {
            ((EagerInMemoryEntryProvider<Entry>) getCalendar().getEntryProvider()).addEntries(entries);
        }
    }

    /**
     * Called by the toolbar, when the "Remove entries" button has been pressed to simulate the removal of entries.
     * Might be called by any other source, too.
     * <p></p>
     * Intended to update the used backend. By default it will check, if the used entry provider is eager in memory
     * and in that case automatically update the entry provider (to prevent unnecessary code duplication when
     * the default entry provider is used).
     *
     * @param entries entries to remove
     */
    protected void onEntriesRemoved(Collection<Entry> entries) {
        // The eager in memory provider provider provides API to modify its internal cache and takes care of pushing
        // the data to the client - no refresh call is needed (or even recommended here)
        if (getCalendar().isEagerInMemoryEntryProvider()) {
            ((EagerInMemoryEntryProvider<Entry>) getCalendar().getEntryProvider()).removeEntries(entries);
        }
    }

    /**
     * Called, when one of the sample entries have been modified, e. g. by an event.
     * Might be called by any other source, too.
     * <p></p>
     * Intended to update the used backend. By default it will check, if the used entry provider is eager in memory
     * and in that case automatically update the entry provider (to prevent unnecessary code duplication when
     * the default entry provider is used).
     *
     * @param entry entry that has changed
     */
    protected void onEntryChanged(Entry entry) {
        // The eager in memory provider provider provides API to modify its internal cache and takes care of pushing
        // the data to the client - no refresh call is needed (or even recommended here)
        if (getCalendar().isEagerInMemoryEntryProvider()) {
            ((EagerInMemoryEntryProvider<Entry>) getCalendar().getEntryProvider()).updateEntry(entry);
        }
    }

    private FullCalendar getCalendar() {
        return calendar;
    }

    /**
     * Returns the entry provider set to the calendar. Will be available after {@link #createCalendar(JsonObject)}
     * has been called.
     *
     * @return entry provider or null
     */
    protected EntryProvider<Entry> getEntryProvider() {
        return getCalendar().getEntryProvider();
    }


}
