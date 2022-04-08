/*
 * Copyright 2020, Stefan Uebe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package cz.wildwest.zaurex.views.allShifts;

import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.data.service.ShiftService;
import cz.wildwest.zaurex.data.service.UserService;
import cz.wildwest.zaurex.views.MainLayout;
import elemental.json.JsonObject;
import org.vaadin.stefan.fullcalendar.*;
import org.vaadin.stefan.fullcalendar.Entry.RenderingMode;
import org.vaadin.stefan.fullcalendar.dataprovider.CallbackEntryProvider;

import javax.annotation.security.RolesAllowed;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;

@Route(value = "calendar", layout = MainLayout.class)
@RolesAllowed("MANAGER")
public class AllShiftsView extends AbstractCalendarView {

    private final UserService userService;
    private final ShiftService shiftService;

    public AllShiftsView(UserService userService, ShiftService shiftService) {
        super();

        this.userService = userService;
        this.shiftService = shiftService;
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.addItem("test");
        return menuBar;
    }

    @Override
    protected FullCalendar createCalendar(JsonObject defaultInitialOptions) {
        FullCalendar calendar = FullCalendarBuilder.create()
                .withAutoBrowserTimezone()
                .withInitialOptions(defaultInitialOptions)
                .withEntryLimit(3)
                .withScheduler("GPL-My-Project-Is-Open-Source")
                .build();

        calendar.setHeightByParent();
        ((FullCalendarScheduler) calendar).setResourceAreaWidth("15%");
        ((FullCalendarScheduler) calendar).setSlotMinWidth("100");
        ((FullCalendarScheduler) calendar).setResourcesInitiallyExpanded(false);

        calendar.setNowIndicatorShown(true);
        calendar.setNumberClickable(true);
        calendar.setTimeslotsSelectable(true);

        calendar.setSlotMinTime(LocalTime.of(7, 0));
        calendar.setSlotMaxTime(LocalTime.of(17, 0));

        calendar.setBusinessHours(
                new BusinessHours(LocalTime.of(9, 0), LocalTime.of(17, 0), BusinessHours.DEFAULT_BUSINESS_WEEK),
                new BusinessHours(LocalTime.of(12, 0), LocalTime.of(15, 0), DayOfWeek.SATURDAY),
                new BusinessHours(LocalTime.of(12, 0), LocalTime.of(13, 0), DayOfWeek.SUNDAY)
        );

//        ((FullCalendarScheduler) calendar).addEntryDroppedSchedulerListener(event -> {
//            System.out.println("Old resource: " + event.getOldResource());
//            System.out.println("New resource: " + event.getNewResource());
//            Entry entry = event.getEntry();
//            System.out.println(entry.getStart() + " / " + entry.getStart());
//        });

//        ((FullCalendarScheduler) calendar).addTimeslotsSelectedSchedulerListener((event) -> {
//            System.out.println( "ZoneId: " + event.getSource().getTimezone().getZoneId() );
//            LocalDateTime startDate = event.getStart();
//            System.out.println( "getStart(): " + event.getStart() );
//            System.out.println( "getStartWithOffset():  " + event.getStartWithOffset() );
//
//
//            ResourceEntry entry = new ResourceEntry();
//
//            entry.setStart(event.getStart());
//            entry.setEnd(event.getEnd());
//            entry.setAllDay(event.isAllDay());
//
//            entry.setColor("dodgerblue");
//            entry.setCalendar(calendar);
//
//            DemoDialog dialog = new DemoDialog(entry, true, userService.findAll());
//            dialog.setSaveConsumer(e -> onEntriesCreated(Collections.singletonList(e)));
//            dialog.open();
//        });

//        calendar.addEntryClickedListener(event -> {
//            if (event.getEntry().getRenderingMode() != RenderingMode.BACKGROUND && event.getEntry().getRenderingMode() != RenderingMode.INVERSE_BACKGROUND) {
//                DemoDialog dialog = new DemoDialog(event.getEntry(), false, userService.findAll());
//                dialog.setSaveConsumer(this::onEntryChanged);
//                dialog.setDeleteConsumer(e -> onEntriesRemoved(Collections.singletonList(e)));
//                dialog.open();
//            }
//        });

        ((FullCalendarScheduler) calendar).setEntryResourceEditable(false);

        calendar.setEntryDidMountCallback(
                "function(info) { "
                        + "    if(info.event.extendedProps.cursors != undefined) { "
                        + "        if(!info.event.startEditable) { "
                        + "            info.el.style.cursor = info.event.extendedProps.cursors.disabled;"
                        + "        } else { "
                        + "            info.el.style.cursor = info.event.extendedProps.cursors.enabled;"
                        + "        }"
                        + "    }"
                        + "}");

        populateEntries(calendar);

        return calendar;
    }

    private void populateEntries(FullCalendar calendar) {
        calendar.setEntryProvider(new CallbackEntryProvider<>(entryQuery -> shiftService.findAll().stream().map(shift -> {
            ResourceEntry resourceEntry = new ResourceEntry();
            setValues(calendar, resourceEntry, shift.getOwner().getName(), shift.getFromDateTime(), shift.getToDateTime(), "gray");
            return resourceEntry;
        }), (SerializableFunction<String, ResourceEntry>) s -> new ResourceEntry()
        ));
    }

    static void setValues(FullCalendar calendar, ResourceEntry entry, String title, LocalDateTime start, LocalDateTime end, String color) {
        entry.setCalendar(calendar);
        entry.setTitle(title);
        entry.setStart(start);
        entry.setEnd(end);
        entry.setColor(color);
    }

    @Override
    protected void onTimeslotsSelected(TimeslotsSelectedEvent event) {
        super.onTimeslotsSelected(event);
        System.out.println(event.getClass().getSimpleName() + ": " + event);
    }

    @Override
    protected void onDayNumberClicked(DayNumberClickedEvent event) {
        super.onDayNumberClicked(event);
        System.out.println(event.getClass().getSimpleName() + ": " + event);
    }

    @Override
    protected void onWeekNumberClicked(WeekNumberClickedEvent event) {
        super.onWeekNumberClicked(event);
        System.out.println(event.getClass().getSimpleName() + ": " + event);
    }

    @Override
    protected void onViewSkeletonRendered(ViewSkeletonRenderedEvent event) {
        super.onViewSkeletonRendered(event);
        System.out.println(event.getClass().getSimpleName() + ": " + event);
    }

    @Override
    protected void onEntryResized(EntryResizedEvent event) {
        super.onEntryResized(event);
        System.out.println(event.getClass().getSimpleName() + ": " + event);
    }

    @Override
    protected void onEntryDropped(EntryDroppedEvent event) {
        super.onEntryDropped(event);
        System.out.println(event.getClass().getSimpleName() + ": " + event);
    }

    @Override
    protected void onEntryClick(EntryClickedEvent event) {
        super.onEntryClick(event);
        System.out.println(event.getClass().getSimpleName() + ": " + event);
    }

    @Override
    protected void onBrowserTimezoneObtained(BrowserTimezoneObtainedEvent event) {
        super.onBrowserTimezoneObtained(event);
        System.out.println(event.getClass().getSimpleName() + ": " + event);
    }

    @Override
    protected void onDatesRendered(DatesRenderedEvent event) {
        super.onDatesRendered(event);
        System.out.println(event.getClass().getSimpleName() + ": " + event);
    }

    @Override
    protected void onMoreLinkClicked(MoreLinkClickedEvent event) {
        super.onMoreLinkClicked(event);
        System.out.println(event.getClass().getSimpleName() + ": " + event);
    }

    @Override
    protected void onTimeslotClicked(TimeslotClickedEvent event) {
        super.onTimeslotClicked(event);
        System.out.println(event.getClass().getSimpleName() + ": " + event);
    }
}