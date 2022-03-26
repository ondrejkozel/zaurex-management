package cz.wildwest.zaurex.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import cz.wildwest.zaurex.data.Role;
import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.help.Helper;
import cz.wildwest.zaurex.help.Helpers;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.addToWarehouse.AddToWarehouseView;
import cz.wildwest.zaurex.views.allShifts.AllShiftsView;
import cz.wildwest.zaurex.views.employees.EmployeesView;
import cz.wildwest.zaurex.views.holidays.HolidaysView;
import cz.wildwest.zaurex.views.holidaysForApproval.HolidaysForApprovalView;
import cz.wildwest.zaurex.views.homePage.HomePageView;
import cz.wildwest.zaurex.views.invoices.InvoicesView;
import cz.wildwest.zaurex.views.sell.SellView;
import cz.wildwest.zaurex.views.warehouse.WarehouseView;
import cz.wildwest.zaurex.views.yoursShifts.YoursShiftsView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    /**
     * A simple navigation item component, based on ListItem element.
     */
    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            link.addClassNames("menu-item-link");
            link.setRoute(view);
            //
            Span text = new Span(menuTitle);
            text.addClassNames("menu-item-text");
            //
            LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon(iconClass);
            lineAwesomeIcon.addClassNames("menu-item-icon");
            //
            link.add(lineAwesomeIcon, text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }

    }

    private H1 viewTitle;

    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Button helpButton;

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("view-toggle");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");
        //
        viewTitle = new H1();
        viewTitle.addClassNames("view-title", "flex-auto");
        //
        LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("las la-question");
        lineAwesomeIcon.setTitle("Zobrazit nápovědu...");
        helpButton = new Button(lineAwesomeIcon);
        helpButton.setClassName("help-button");
        helpButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        helpButton.addClickListener(this::showHelpDialog);
        //
        Header header = new Header(toggle, viewTitle, helpButton);
        header.addClassNames("view-header");
        return header;
    }

    private void showHelpDialog(ClickEvent<Button> clickEvent) {
        var contentClass = getContent().getClass();
        Optional<Helper> helper = Helpers.getHelper(contentClass);
        if (helper.isEmpty()) Notification.show("Pro tuto stránku bohužel nápovědu nemáme 😔");
        else buildAndShowHelpDialog(helper.get());
    }

    private void buildAndShowHelpDialog(Helper helper) {
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.addClassNames("custom-dialog-layout");
        dialogLayout.add(
                new Paragraph(helper.shortText()),
                new Paragraph(helper.longText())
        );
        //
        ConfirmDialog dialog = new ConfirmDialog(getCurrentPageTitle(), "", "Zavřít", event -> {});
        dialog.setConfirmButtonTheme("tertiary");
        dialog.add(dialogLayout);
        dialog.open();
    }

    private Component createDrawerContent() {
        Image appName = new Image("images/napis-zaurex-trans.png", "Zaurex");
        RouterLink routerLink = new RouterLink();
        routerLink.setRoute(HomePageView.class);
        routerLink.add(appName);
        routerLink.addClassNames("app-name-container");
        appName.addClassNames("app-name");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(routerLink,
                createNavigation(), createFooter());
        section.addClassNames("drawer-section");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("menu-item-container");
        nav.getElement().setAttribute("aria-labelledby", "views");

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames("navigation-list");
        nav.add(list);

        for (MenuItemInfo menuItem : createMenuItems()) {
            if (accessChecker.hasAccess(menuItem.getView())) {
                list.add(menuItem);
            }

        }
        return nav;
    }

    private List<MenuItemInfo> createMenuItems() {
        MenuItemInfo holidays = new MenuItemInfo("Dovolená", "la la-mug-hot", HolidaysView.class);
        List<MenuItemInfo> menuItemInfos = new ArrayList<>(List.of(
//              new MenuItemInfo("Hlavní strana", "la la-home", HomePageView.class),

                new MenuItemInfo("Prodat", "la la-wallet", SellView.class),

                new MenuItemInfo("Naskladnit", "la la-box", AddToWarehouseView.class),

                new MenuItemInfo("Sklad", "la la-boxes", WarehouseView.class),

                new MenuItemInfo("Vaše směny", "la la-screwdriver", YoursShiftsView.class),

                new MenuItemInfo("Všechny směny", "la la-tools", AllShiftsView.class),

                holidays,

                new MenuItemInfo("Dovolené ke schválení", "la la-question-circle", HolidaysForApprovalView.class),

                new MenuItemInfo("Faktury", "la la-file-invoice-dollar", InvoicesView.class),

                new MenuItemInfo("Zaměstnanci", "la la-users", EmployeesView.class)

//                new MenuItemInfo("Chat", "la la-comments", ChatView.class),
        ));
        if (authenticatedUser.get().orElseThrow().getRoles().contains(Role.MANAGER)) menuItemInfos.remove(holidays);
        return menuItemInfos;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("footer");

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            //
            Avatar avatar = new Avatar(user.getName());
            avatar.addClassNames("me-xs");
            //
            Span name = new Span(user.getName());
            name.addClassNames("font-medium", "text-s", "text-secondary", "flex-auto");
            //
            LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("las la-power-off");
            Button logoutButton = new Button(lineAwesomeIcon,
                    clickEvent -> authenticatedUser.logout());
            lineAwesomeIcon.setTitle("Odhlásit se...");
            logoutButton.addClassNames("px-xs");
            //
            layout.add(avatar, name, logoutButton);
        }
        else {
            Anchor loginLink = new Anchor("login", "Přihlásit se");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
        helpButton.setVisible(Helpers.hasHelper(getContent().getClass()));
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
