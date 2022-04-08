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
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.data.service.UserService;
import cz.wildwest.zaurex.views.MainLayout;
import cz.wildwest.zaurex.views.allShifts.util.EntryManager;
import cz.wildwest.zaurex.views.allShifts.util.ResourceManager;
import elemental.json.JsonObject;
import org.vaadin.stefan.fullcalendar.*;
import org.vaadin.stefan.fullcalendar.Entry.RenderingMode;

import javax.annotation.security.RolesAllowed;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Route(value = "calendar", layout = MainLayout.class)
@RolesAllowed("MANAGER")
public class AllShiftsView extends AbstractCalendarView {

    private final UserService userService;

    public AllShiftsView(UserService userService) {
        super();

        this.userService = userService;
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

        ((FullCalendarScheduler) calendar).addEntryDroppedSchedulerListener(event -> {
            System.out.println("Old resource: " + event.getOldResource());
            System.out.println("New resource: " + event.getNewResource());
            Entry entry = event.getEntry();
            System.out.println(entry.getStart() + " / " + entry.getStart());
        });

        ((FullCalendarScheduler) calendar).addTimeslotsSelectedSchedulerListener((event) -> {
            System.out.println( "ZoneId: " + event.getSource().getTimezone().getZoneId() );
            LocalDateTime startDate = event.getStart();
            System.out.println( "getStart(): " + event.getStart() );
            System.out.println( "getStartWithOffset():  " + event.getStartWithOffset() );


            ResourceEntry entry = new ResourceEntry();

            entry.setStart(event.getStart());
            entry.setEnd(event.getEnd());
            entry.setAllDay(event.isAllDay());

            entry.setColor("dodgerblue");
            entry.setCalendar(calendar);

            DemoDialog dialog = new DemoDialog(entry, true, userService.findAll());
            dialog.setSaveConsumer(e -> onEntriesCreated(Collections.singletonList(e)));
            dialog.open();
        });

        calendar.addEntryClickedListener(event -> {
            if (event.getEntry().getRenderingMode() != RenderingMode.BACKGROUND && event.getEntry().getRenderingMode() != RenderingMode.INVERSE_BACKGROUND) {
                DemoDialog dialog = new DemoDialog(event.getEntry(), false, userService.findAll());
                dialog.setSaveConsumer(this::onEntryChanged);
                dialog.setDeleteConsumer(e -> onEntriesRemoved(Collections.singletonList(e)));
                dialog.open();
            }
        });

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
        LocalDate now = LocalDate.now();

        Resource meetingRoomRed = ResourceManager.createResource((Scheduler) calendar, "Meetingroom Red", "#ff0000");
        Resource meetingRoomGreen = ResourceManager.createResource((Scheduler) calendar, "Meetingroom Green", "green");
        Resource meetingRoomBlue = ResourceManager.createResource((Scheduler) calendar, "Meetingroom Blue", "blue");
        Resource meetingRoomOrange = ResourceManager.createResource((Scheduler) calendar, "Meetingroom Orange", "orange", null,
                new BusinessHours(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));

        Resource computer1A = ResourceManager.createResource((Scheduler) calendar, "Computer 1A", "lightbrown");
        Resource computer1B = ResourceManager.createResource((Scheduler) calendar, "Computer 1B", "lightbrown");
        Resource computer1C = ResourceManager.createResource((Scheduler) calendar, "Computer 1C", "lightbrown");

        ResourceManager.createResource((Scheduler) calendar, "Computer room 1", "brown", Arrays.asList(computer1A, computer1B, computer1C));

        Resource computerRoom2 = ResourceManager.createResource((Scheduler) calendar, "Computer room 2", "brown");
        // here we must NOT use createResource, since they are added to the calendar later
        Resource computer2A = new Resource(null, "Computer 2A", "lightbrown");
        Resource computer2B = new Resource(null, "Computer 2B", "lightbrown");
        Resource computer2C = new Resource(null, "Computer 2C", "lightbrown");

        // not realistic, just a demonstration of automatic recursive adding
        computer2A.addChild(new Resource(null, "Mouse", "orange"));
        computer2A.addChild(new Resource(null, "Screen", "orange"));
        computer2A.addChild(new Resource(null, "Keyboard", "orange"));

        List<Resource> computerRoom2Children = Arrays.asList(computer2A, computer2B, computer2C);
        computerRoom2.addChildren(computerRoom2Children);
        ((Scheduler) calendar).addResources(computerRoom2Children);

        EntryManager.createTimedEntry(calendar, "Meeting 1", now.withDayOfMonth(3).atTime(10, 0), 120, null, meetingRoomBlue, meetingRoomGreen, meetingRoomRed);

        EntryManager.createTimedEntry(calendar, "Meeting 2", now.withDayOfMonth(3).atTime(10, 0), 120, null, meetingRoomOrange);
        EntryManager.createTimedEntry(calendar, "Meeting 3", now.withDayOfMonth(7).atTime(11, 30), 120, null, meetingRoomRed);

        HashMap<String, Object> extendedProps = new HashMap<String, Object>();
        HashMap<String, Object> cursors = new HashMap<String, Object>();
        cursors.put("enabled", "pointer");
        cursors.put("disabled", "not-allowed");
        extendedProps.put("cursors", cursors);

        EntryManager.createTimedEntry(calendar, "Meeting 4", now.withDayOfMonth(12).atTime(9, 0), 120, null, extendedProps, meetingRoomGreen);
        EntryManager.createTimedEntry(calendar, "Meeting 5", now.withDayOfMonth(13).atTime(10, 0), 120, null, meetingRoomGreen);
        EntryManager.createTimedEntry(calendar, "Meeting 6", now.withDayOfMonth(17).atTime(11, 30), 120, null, meetingRoomBlue);
        EntryManager.createTimedEntry(calendar, "Meeting 7", now.withDayOfMonth(22).atTime(9, 0), 120, null, meetingRoomRed);
        EntryManager.createTimedEntry(calendar, "Meeting 8", now.withDayOfMonth(4).atTime(10, 0), 120, null);

        EntryManager.createTimedBackgroundEntry(calendar, now.withDayOfMonth(3).atTime(10, 0), 120, null);
        EntryManager.createTimedEntry(calendar, "Meeting 9", now.withDayOfMonth(7).atTime(11, 30), 120, "mediumseagreen");
        EntryManager.createTimedEntry(calendar, "Meeting 10", now.withDayOfMonth(15).atTime(9, 0), 120, "mediumseagreen");
        EntryManager.createTimedEntry(calendar, "Meeting 11", now.withDayOfMonth(18).atTime(10, 0), 120, "mediumseagreen");
        EntryManager.createTimedEntry(calendar, "Meeting 12", now.withDayOfMonth(17).atTime(11, 30), 120, "mediumseagreen");
        EntryManager.createTimedEntry(calendar, "Meeting 13", now.withDayOfMonth(24).atTime(9, 0), 120, "mediumseagreen");

        EntryManager.createTimedEntry(calendar, "Grocery Store", now.withDayOfMonth(7).atTime(17, 30), 45, "violet");
        EntryManager.createTimedEntry(calendar, "Dentist", now.withDayOfMonth(20).atTime(11, 30), 60, "violet");
        EntryManager.createTimedEntry(calendar, "Cinema", now.withDayOfMonth(10).atTime(20, 30), 140, "dodgerblue");
        EntryManager.createDayEntry(calendar, "Short trip", now.withDayOfMonth(17), 2, "dodgerblue");
        EntryManager.createDayEntry(calendar, "John's Birthday", now.withDayOfMonth(23), 1, "gray");
        EntryManager.createDayEntry(calendar, "This special holiday", now.withDayOfMonth(4), 1, "gray");

        EntryManager.createDayEntry(calendar, "Multi 1", now.withDayOfMonth(12), 2, "tomato");
        EntryManager.createDayEntry(calendar, "Multi 2", now.withDayOfMonth(12), 2, "tomato");
        EntryManager.createDayEntry(calendar, "Multi 3", now.withDayOfMonth(12), 2, "tomato");
        EntryManager.createDayEntry(calendar, "Multi 4", now.withDayOfMonth(12), 2, "tomato");
        EntryManager.createDayEntry(calendar, "Multi 5", now.withDayOfMonth(12), 2, "tomato");
        EntryManager.createDayEntry(calendar, "Multi 6", now.withDayOfMonth(12), 2, "tomato");
        EntryManager.createDayEntry(calendar, "Multi 7", now.withDayOfMonth(12), 2, "tomato");
        EntryManager.createDayEntry(calendar, "Multi 8", now.withDayOfMonth(12), 2, "tomato");
        EntryManager.createDayEntry(calendar, "Multi 9", now.withDayOfMonth(12), 2, "tomato");
        EntryManager.createDayEntry(calendar, "Multi 10", now.withDayOfMonth(12), 2, "tomato");


        EntryManager.createDayBackgroundEntry(calendar, now.withDayOfMonth(4), 6, "#B9FFC3");
        EntryManager.createDayBackgroundEntry(calendar, now.withDayOfMonth(19), 2, "#CEE3FF");
        EntryManager.createTimedBackgroundEntry(calendar, now.withDayOfMonth(20).atTime(11, 0), 150, "#ff0000");

        EntryManager.createRecurringEvents(calendar);
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