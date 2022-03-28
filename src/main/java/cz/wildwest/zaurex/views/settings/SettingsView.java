package cz.wildwest.zaurex.views.settings;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.data.service.UserService;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.LineAwesomeIcon;
import cz.wildwest.zaurex.views.MainLayout;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.security.PermitAll;

@PageTitle("Nastavení")
@Route(value = "settings", layout = MainLayout.class)
@PermitAll
public class SettingsView extends VerticalLayout {

    private final UserService userService;
    private final User user;
    private final PasswordEncoder passwordEncoder;

    public SettingsView(UserService userService, AuthenticatedUser authenticatedUser, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.user = authenticatedUser.get().orElseThrow();
        this.passwordEncoder = passwordEncoder;
        //
        buildChangePasswordField();
        //
        setSpacing(false);
        setSizeFull();
    }

    private void buildChangePasswordField() {
        add(new H3("Změna hesla"));
        PasswordField passwordField = new PasswordField();
        passwordField.setPlaceholder("nové heslo");
        passwordField.setHelperText("Nové heslo musí mít alespoň 8 znaků.");
        passwordField.setMinLength(8);
        Button submit = new Button("Potvrdit", new LineAwesomeIcon("las la-check"));
        passwordField.addValueChangeListener(event -> submit.setEnabled(!passwordField.isInvalid()));
        passwordField.setValueChangeMode(ValueChangeMode.EAGER);
        submit.setDisableOnClick(true);
        submit.setEnabled(false);
        submit.addClickListener(clickEvent -> changePassword(passwordField.getValue()));
        add(new HorizontalLayout(passwordField, submit));
    }

    private void changePassword(String unhashedPassword) {
        user.setHashedPassword(passwordEncoder.encode(unhashedPassword));
        userService.save(user);
        Notification.show("Úspěšně jsme vám nastavili nové heslo! 🌞");
    }
}
