package cz.wildwest.zaurex.views.holidaysForApproval;

import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.components.gridd.Gridd;
import cz.wildwest.zaurex.data.entity.Holiday;
import cz.wildwest.zaurex.data.service.HolidayService;
import cz.wildwest.zaurex.data.service.UserService;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.MainLayout;
import cz.wildwest.zaurex.views.holidays.HolidaysView;

import javax.annotation.security.RolesAllowed;

@PageTitle("Dovolené ke schválení")
@Route(value = "holidays/approve", layout = MainLayout.class)
@RolesAllowed("MANAGER")
public class HolidaysForApprovalView extends VerticalLayout {

    private final HolidaysView holidaysView;

    private final Gridd<Holiday> grid;
    private final UserService userService;

    private boolean displayPendingOnly = true;

    public HolidaysForApprovalView(HolidayService holidayService, UserService userService, AuthenticatedUser authenticatedUser) {
        this.userService = userService;
        holidaysView = new HolidaysView(holidayService, authenticatedUser);
        //
        grid = holidaysView.getGrid();
        configureDataProvider(holidayService);
        configureHolidaysView();
        //
        add(holidaysView);
        setPadding(false);
        setSizeFull();
        //
        grid.getCrud().addNewListener(event -> setEditing(false));
        grid.getCrud().addEditListener(event -> setEditing(true));
    }

    private void setEditing(boolean editing) {
        holidaysView.getOwner().setReadOnly(editing);
        holidaysView.getUserMessage().setReadOnly(editing);
    }

    private void configureDataProvider(HolidayService holidayService) {
        grid.getDataProvider().setFetchFunction(() -> {
            if (displayPendingOnly) return holidayService.findAllPending();
            return holidayService.findAll();
        });
    }

    private void configureHolidaysView() {
        holidaysView.useOwnerField(userService.findAll());
        holidaysView.getFormLayout().addComponentAsFirst(holidaysView.getOwner());
        holidaysView.getFormLayout().addComponentAsFirst(holidaysView.getStatus());
        holidaysView.getFormLayout().add(holidaysView.getManagerResponse(), 2);
        grid.setNewObjectButtonText("Nová dovolená");
        //
        grid.addColumn("Osoba", new TextRenderer<>(item -> item.getOwner().getName()));
        //
        MenuItem view = grid.getMenuBar().getItems().stream().filter(menuItem -> menuItem.getText().equals("Zobrazit")).findFirst().orElseThrow();
        view.getSubMenu().add(new Hr());
        MenuItem pendingOnly = view.getSubMenu().addItem("Pouze nevyřízené", event -> {
            displayPendingOnly = !displayPendingOnly;
            grid.refreshAll();
        });
        pendingOnly.setCheckable(true);
        pendingOnly.setChecked(displayPendingOnly);
        //
    }

}
