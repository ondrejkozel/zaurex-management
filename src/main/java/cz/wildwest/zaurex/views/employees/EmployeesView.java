package cz.wildwest.zaurex.views.employees;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.components.gridd.GenericDataProvider;
import cz.wildwest.zaurex.components.gridd.Gridd;
import cz.wildwest.zaurex.data.Role;
import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.data.service.UserService;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.LineAwesomeIcon;
import cz.wildwest.zaurex.views.MainLayout;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@PageTitle("Zaměstnanci")
@Route(value = "employees", layout = MainLayout.class)
@RolesAllowed("MANAGER")
public class EmployeesView extends VerticalLayout {

    private static final String DEFAULT_PASSWORD = "heslo";

    private final Gridd<User> grid;
    private final PasswordEncoder passwordEncoder;

    public EmployeesView(UserService userService, AuthenticatedUser authenticatedUser, PasswordEncoder passwordEncoder) {
        User user = authenticatedUser.get().orElseThrow();
        this.passwordEncoder = passwordEncoder;
        grid = new Gridd<>(User.class,
                new GenericDataProvider<>(userService, User.class),
                User::new,
                true,
                buildEditor(),
                "Nový zaměstnanec",
                "Upravit zaměstnance",
                "Odstranit zaměstnance"
        );
        add(grid);
        configureColumns();
        setSizeFull();
        //
        grid.getCrud().addEditListener(userEditEvent -> grid.getCrud().getDeleteButton().setEnabled(!userEditEvent.getItem().equals(user)));
        grid.getCrud().addEditListener(userEditEvent -> passwordReset = false);
        grid.addMultiSelectionListener(selectionEvent -> {
            if (selectionEvent.getAllSelectedItems().contains(user)) grid.deselect(user);
        });
        grid.getCrud().addSaveListener(userSaveEvent -> {
            if (userSaveEvent.getItem().equals(user)) authenticatedUser.logout();
        });
        grid.getCrud().addNewListener(userNewEvent -> {
            userNewEvent.getItem().setHashedPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            passwordReset = true;
        });
        grid.getCrud().addSaveListener(userSaveEvent -> {
            if (passwordReset) showUserHasNewPasswordNotification(userSaveEvent.getItem().getName());
        });
        grid.getCrud().addNewListener(userNewEvent -> resetPasswordButton.setVisible(false));
        grid.getCrud().addEditListener(userEditEvent -> {
            resetPasswordButton.setVisible(true);
            resetPasswordButton.setEnabled(true);
        });
        grid.getCrud().addNewListener(userNewEvent -> {
            roles.managerEnabled = true;
            roles.refresh();
        });
        grid.getCrud().addEditListener(userEditEvent -> {
            roles.managerEnabled = !userEditEvent.getItem().equals(user);
            roles.refresh();
        });
    }

    private static int comparePrioritizeManager(Role o1, Role o2) {
        if (o1.equals(o2)) return 0;
        if (o1.equals(Role.MANAGER)) return -10;
        if (o2.equals(Role.MANAGER)) return 10;
        return o1.compareTo(o2);
    }

    private static int compareDoNotPrioritizeManager(Role o1, Role o2) {
        if (o1.equals(o2)) return 0;
        if (o1.equals(Role.MANAGER)) return 10;
        if (o2.equals(Role.MANAGER)) return -10;
        return o1.compareTo(o2);
    }

    private void showUserHasNewPasswordNotification(String name) {
        Notification.show(String.format("Uživateli %s bylo nastaveno výchozí heslo: \"%s\"", name, DEFAULT_PASSWORD));
    }

    private boolean passwordReset;

    private void configureColumns() {
        grid.addColumn("Jméno", new TextRenderer<>(User::getName), true);
        grid.addColumn("Uživatelské jméno", new TextRenderer<>(User::getUsername), false);
        grid.addColumn("Role", new TextRenderer<>(user -> user.getRoles().stream().sorted(EmployeesView::comparePrioritizeManager).map(Role::getText).collect(Collectors.joining(", "))), true);
    }

    @SuppressWarnings("FieldCanBeLocal")
    private TextField username;
    @SuppressWarnings("FieldCanBeLocal")
    private TextField name;
    private RolesField roles;

    private Button resetPasswordButton;

    private BinderCrudEditor<User> buildEditor() {
        username = new TextField("Uživatelské jméno");
        username.setRequired(true);
        name = new TextField("Jméno");
        name.setRequired(true);
        roles = new RolesField();
        //
        resetPasswordButton = new Button("Obnovit heslo", new LineAwesomeIcon("las la-redo-alt"));
        resetPasswordButton.setDisableOnClick(true);
        //
        Binder<User> binder = new BeanValidationBinder<>(User.class);
        resetPasswordButton.addClickListener(clickEvent -> {
            grid.getCrud().getEditor().getItem().setHashedPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            passwordReset = true;
            grid.getCrud().setDirty(true);
        });
        //
        binder.bindInstanceFields(this);
        FormLayout formLayout = new FormLayout(username, name, roles, new HorizontalLayout(resetPasswordButton));
        return new BinderCrudEditor<>(binder, formLayout);
    }

    private static class RolesField extends CustomField<Set<Role>> {

        CheckboxGroup<Role> roleCheckboxGroup;

        public RolesField() {
            roleCheckboxGroup = new CheckboxGroup<>();
            roleCheckboxGroup.setLabel("Role");
            roleCheckboxGroup.setItems(Arrays.stream(Role.values()).sorted(EmployeesView::compareDoNotPrioritizeManager).collect(Collectors.toList()));
            add(roleCheckboxGroup);
            //
            roleCheckboxGroup.addSelectionListener(event -> {
                if (event.getAddedSelection().contains(Role.MANAGER)) roleCheckboxGroup.select(Role.values());
            });
            roleCheckboxGroup.setItemEnabledProvider((SerializablePredicate<Role>) role -> {
                if (role.equals(Role.MANAGER)) return managerEnabled;
                return true;
            });
        }

        private void addRenderedLabel(Checkbox item) {
            item.setLabel(Role.valueOf(item.getLabel()).getText());
        }

        private boolean managerEnabled;

        @Override
        protected Set<Role> generateModelValue() {
            return roleCheckboxGroup.getSelectedItems();
        }

        @Override
        protected void setPresentationValue(Set<Role> roles) {
            if (roles == null) roleCheckboxGroup.deselectAll();
            else roleCheckboxGroup.select(roles);
        }

        public void refresh() {
            roleCheckboxGroup.setReadOnly(false);
            roleCheckboxGroup.getChildren().forEach(item -> addRenderedLabel((Checkbox) item));
        }
    }
}
