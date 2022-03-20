package cz.wildwest.zaurex.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.chat.ChatView;
import cz.wildwest.zaurex.views.holidays.HolidaysView;
import cz.wildwest.zaurex.views.holidaysForApproval.HolidaysForApprovalView;
import cz.wildwest.zaurex.views.homePage.HomePageView;
import cz.wildwest.zaurex.views.addToWarehouse.AddToWarehouseView;
import cz.wildwest.zaurex.views.invoices.InvoicesView;
import cz.wildwest.zaurex.views.sell.SellView;
import cz.wildwest.zaurex.views.warehouse.WarehouseView;
import cz.wildwest.zaurex.views.yoursShifts.YoursShiftsView;
import cz.wildwest.zaurex.views.allShifts.AllShiftsView;
import cz.wildwest.zaurex.views.employees.EmployeesView;
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

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("view-toggle");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("view-title");

        Header header = new Header(toggle, viewTitle);
        header.addClassNames("view-header");
        return header;
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

    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[]{
//                new MenuItemInfo("Hlavní strana", "la la-home", HomePageView.class),

                new MenuItemInfo("Prodat", "la la-wallet", SellView.class),

                new MenuItemInfo("Naskladnit", "la la-box", AddToWarehouseView.class),

                new MenuItemInfo("Sklad", "la la-boxes", WarehouseView.class),

                new MenuItemInfo("Vaše směny", "la la-screwdriver", YoursShiftsView.class),

                new MenuItemInfo("Všechny směny", "la la-tools", AllShiftsView.class),

                new MenuItemInfo("Dovolená", "la la-mug-hot", HolidaysView.class),

                new MenuItemInfo("Dovolené ke schválení", "la la-question-circle", HolidaysForApprovalView.class),

                new MenuItemInfo("Faktury", "la la-file-invoice-dollar", InvoicesView.class),

                new MenuItemInfo("Zaměstnanci", "la la-users", EmployeesView.class),

//                new MenuItemInfo("Chat", "la la-comments", ChatView.class),

        };
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
            lineAwesomeIcon.addClassNames("font-medium");
            Button logoutButton = new Button(lineAwesomeIcon,
                    clickEvent -> authenticatedUser.logout());
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
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
