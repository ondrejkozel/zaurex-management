package cz.wildwest.zaurex.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import cz.wildwest.zaurex.data.Role;
import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.help.Helper;
import cz.wildwest.zaurex.help.Helpers;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.about.AboutView;
import cz.wildwest.zaurex.views.allShifts.AllShiftsView;
import cz.wildwest.zaurex.views.employees.EmployeesView;
import cz.wildwest.zaurex.views.holidays.HolidaysView;
import cz.wildwest.zaurex.views.holidaysForApproval.HolidaysForApprovalView;
import cz.wildwest.zaurex.views.homePage.HomePageView;
import cz.wildwest.zaurex.views.invoices.InvoicesView;
import cz.wildwest.zaurex.views.sell.SellView;
import cz.wildwest.zaurex.views.settings.SettingsView;
import cz.wildwest.zaurex.views.warehouse.WarehouseView;
import cz.wildwest.zaurex.views.yoursShifts.YoursShiftsView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    /**
     * A simple navigation item component, based on ListItem element.
     */
    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        Span text;

        public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            link.addClassNames("menu-item-link");
            link.setRoute(view);
            //
            text = new Span(menuTitle);
            text.addClassNames("menu-item-text");
            //
            LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon(iconClass);
            lineAwesomeIcon.addClassNames("menu-item-icon");
            //
            link.add(lineAwesomeIcon, text);
            add(link);
        }

        private void setTextValue(String text) {
            this.text.setText(text);
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
        //
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
        //
        checkChangePasswordNotifier();
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
        lineAwesomeIcon.setTitle("Zobrazit n??pov??du...");
        helpButton = new Button(lineAwesomeIcon);
        helpButton.setClassName("help-button");
        helpButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        helpButton.addClickListener(this::showHelpDialog);
        helpButton.addClickShortcut(Key.KEY_H, KeyModifier.ALT);
        //
        Header header = new Header(toggle, viewTitle, helpButton);
        header.addClassNames("view-header");
        return header;
    }

    private void showHelpDialog(ClickEvent<Button> clickEvent) {
        var contentClass = getContent().getClass();
        Optional<Helper> helper = Helpers.getHelper(contentClass);
        if (helper.isEmpty()) Notification.show("Pro tuto str??nku bohu??el n??pov??du nem??me ????");
        else buildAndShowHelpDialog(helper.get());
    }

    private void buildAndShowHelpDialog(Helper helper) {
        Scroller scroller = new Scroller();
        scroller.setContent(new Html("<span>" + helper.html() + "</span>"));
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.setClassName("help-scroller");
        //
        ConfirmDialog dialog = new ConfirmDialog(getCurrentPageTitle(), "", "Zav????t", event -> {});
        dialog.setConfirmButtonTheme("tertiary");
        dialog.add(scroller);
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
        MenuItemInfo holidays = new MenuItemInfo("Dovolen??", "la la-mug-hot", HolidaysView.class);
        MenuItemInfo warehouse = new MenuItemInfo("Sklad", "la la-boxes", WarehouseView.class);
        List<MenuItemInfo> menuItemInfos = new ArrayList<>(List.of(

                new MenuItemInfo("Prodat", "la la-wallet", SellView.class),

                warehouse,

//                new MenuItemInfo("Va??e sm??ny", "la la-screwdriver", YoursShiftsView.class),

                new MenuItemInfo("V??echny sm??ny", "la la-tools", AllShiftsView.class),

                holidays,

                new MenuItemInfo("Dovolen??", "la la-question-circle", HolidaysForApprovalView.class),

                new MenuItemInfo("Faktury", "la la-file-invoice-dollar", InvoicesView.class),

                new MenuItemInfo("Zam??stnanci", "la la-users", EmployeesView.class),

                new MenuItemInfo("Nastaven??", "la la-cog", SettingsView.class),

                new MenuItemInfo("O aplikaci", "la la-question-circle", AboutView.class)

        ));
        if (authenticatedUser.get().isPresent()) {
            Set<Role> roles = authenticatedUser.get().get().getRoles();
            if (roles.contains(Role.MANAGER)) menuItemInfos.remove(holidays);
            if (roles.contains(Role.WAREHOUSEMAN) && !roles.contains(Role.MANAGER)) warehouse.setTextValue("Naskladnit");
        }
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
            lineAwesomeIcon.setTitle("Odhl??sit se...");
            logoutButton.addClassNames("px-xs");
            //
            layout.add(avatar, name, logoutButton);
        }
        else {
            Anchor loginLink = new Anchor("login", "P??ihl??sit se");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
        if (Helpers.hasHelper(getContent().getClass())) helpButton.removeClassName("display-none");
        else if (!helpButton.hasClassName("display-none")) helpButton.addClassName("display-none");
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    private void checkChangePasswordNotifier() {
        if (authenticatedUser.get().isPresent() && !authenticatedUser.get().get().isHasChangedPassword())
            Notification.show("Pro lep???? zabezpe??en?? si v nastaven?? zm????te heslo ????");
    }
}
