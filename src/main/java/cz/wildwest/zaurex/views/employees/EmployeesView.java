package cz.wildwest.zaurex.views.employees;

import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.components.gridd.GenericDataProvider;
import cz.wildwest.zaurex.components.gridd.Gridd;
import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.data.service.UserService;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;

@PageTitle("Zaměstnanci")
@Route(value = "employees", layout = MainLayout.class)
@RolesAllowed("MANAGER")
public class EmployeesView extends VerticalLayout {

    private final Gridd<User> grid;

    public EmployeesView(UserService userService, AuthenticatedUser authenticatedUser) {
        User user = authenticatedUser.get().orElseThrow();
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
        grid.addSelectionListener(selectionEvent -> {
            if (grid.isMultiselectActive() && selectionEvent.getAllSelectedItems().contains(user)) grid.deselect(user);
        });
    }

    private void configureColumns() {
        grid.addColumn("Jméno", new TextRenderer<>(User::getName), true);
        grid.addColumn("Uživatelské jméno", new TextRenderer<>(User::getUsername), false);
    }

    private BinderCrudEditor<User> buildEditor() {
        Binder<User> binder = new BeanValidationBinder<>(User.class);
        FormLayout formLayout = new FormLayout(new Label("Ještě tady nic není!"));
        return new BinderCrudEditor<>(binder, formLayout);
    }
}
