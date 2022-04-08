package cz.wildwest.zaurex.views.settings;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.data.Role;
import cz.wildwest.zaurex.data.entity.Configuration;
import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.data.service.ConfigurationService;
import cz.wildwest.zaurex.data.service.UserService;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.LineAwesomeIcon;
import cz.wildwest.zaurex.views.MainLayout;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.security.PermitAll;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PageTitle("Nastavení")
@Route(value = "settings", layout = MainLayout.class)
@PermitAll
public class SettingsView extends VerticalLayout {

    private final UserService userService;
    private final User user;
    private final ConfigurationService configurationService;
    private final PasswordEncoder passwordEncoder;
    private Button invoicingSubmit;
    private TextField accountNumberField;
    private IntegerField icoField;

    public SettingsView(UserService userService, AuthenticatedUser authenticatedUser, PasswordEncoder passwordEncoder, ConfigurationService configurationService) {
        this.userService = userService;
        this.user = authenticatedUser.get().orElseThrow();
        this.configurationService = configurationService;
        Set<Role> roles = user.getRoles();
        this.passwordEncoder = passwordEncoder;
        //
        if (roles.contains(Role.MANAGER)) buildInvoicing();
        buildChangePasswordField();
        //
        setSpacing(false);
        setSizeFull();
    }

    private void buildInvoicing() {
        add(new H3("Fakturace"));
        buildChangeIcoField();
        buildChangeAccoutNumberField();
        invoicingSubmit = new Button("Potvrdit", new LineAwesomeIcon("las la-check"));
        invoicingSubmit.setDisableOnClick(true);
        invoicingSubmit.setEnabled(false);
        invoicingSubmit.addClickListener(event -> {
            configurationService.save(Configuration.StandardKey.ICO, String.valueOf(icoField.getValue()));
            configurationService.save(Configuration.StandardKey.BANK_ACCOUNT_NUMBER, accountNumberField.getValue());
        });
        add(invoicingSubmit);
    }

    private void invoicingFieldsValueChanged() {
        invoicingSubmit.setEnabled(!accountNumberField.isInvalid() && !icoField.isInvalid());
    }

    private void buildChangeAccoutNumberField() {
        accountNumberField = new TextField("Číslo bankovního účtu");
        accountNumberField.setValueChangeMode(ValueChangeMode.EAGER);
        accountNumberField.setPattern("^([0-9]{2,6}-|)[0-9]{2,10}\\/[0-9]{4}$");
        accountNumberField.setValue(configurationService.getValue(Configuration.StandardKey.BANK_ACCOUNT_NUMBER).orElse(""));
        accountNumberField.addValueChangeListener(event -> invoicingFieldsValueChanged());
        accountNumberField.addValueChangeListener((event) -> {
            if (accountNumberField.isInvalid()) accountNumberField.setHelperText("Vámi zadané parametry nevyhovují formátu (xxxxxx-)xxxxxxxxxx/xxxx.");
            else accountNumberField.setHelperText("");
        });
        add(accountNumberField);
    }

    private void buildChangeIcoField() {
        icoField = new IntegerField("Identifikační číslo");
        icoField.setPlaceholder("nenastaveno");
        icoField.setValueChangeMode(ValueChangeMode.EAGER);
        icoField.setValue(Integer.parseInt(configurationService.getValue(Configuration.StandardKey.ICO).orElse("")));
        icoField.addValueChangeListener(event -> invoicingFieldsValueChanged());
        add(icoField);
    }

    private void buildChangePasswordField() {
        add(new H3("Změna hesla"));
        PasswordField passwordField = new PasswordField();
        passwordField.setPlaceholder("nové heslo");
        passwordField.setHelperText("Nové heslo musí mít alespoň 8 znaků.");
        passwordField.setMinLength(8);
        passwordField.setValueChangeMode(ValueChangeMode.EAGER);
        passwordField.addValueChangeListener((event) -> passwordField.setHelperText("Síla hesla: " + getPasswordStrength(event.getValue())));
        //
        PasswordField passwordCheckField = new PasswordField();
        passwordCheckField.setPlaceholder("opište");
        passwordCheckField.addValueChangeListener((event) -> passwordCheckField.setHelperText("Hesla se " + (arePasswordsEqual(passwordField.getValue(), passwordCheckField.getValue()) ? "shodují" : "neshodují")));
        passwordCheckField.setValueChangeMode(ValueChangeMode.EAGER);
        Button passwordSubmitButton = new Button("Potvrdit", new LineAwesomeIcon("las la-check"));
        passwordSubmitButton.setDisableOnClick(true);
        passwordSubmitButton.setEnabled(false);
        passwordSubmitButton.addClickListener(clickEvent -> changePassword(passwordField.getValue(), passwordCheckField.getValue()));
        add(new HorizontalLayout(passwordField, passwordCheckField, passwordSubmitButton));
        //
        passwordField.addValueChangeListener(event -> passwordFieldsValueChanged(passwordSubmitButton, passwordField.getValue(), passwordCheckField.getValue()));
        passwordCheckField.addValueChangeListener(event -> passwordFieldsValueChanged(passwordSubmitButton, passwordField.getValue(), passwordCheckField.getValue()));
    }

    private void passwordFieldsValueChanged(Button submitButton, String newPassword, String newPasswordCheck) {
        submitButton.setEnabled(arePasswordsEqual(newPassword, newPasswordCheck) && newPassword.length() >= 8);
    }

    public boolean arePasswordsEqual(String password, String secondPassword) {
        return password.equals(secondPassword);
    }

    public String getPasswordStrength(String password) {
        var strength = 0;

        /*
        Toto by šlo vylepšit nějak takto:

            List<Pattern> patterns = List.of(
                    Pattern.compile("[a-z]"), //small letters
                    Pattern.compile("[A-Z]"), //capital letters
                    Pattern.compile("[0-9]"), //numbers
                    Pattern.compile("\\W") //special characters
            );
            for (Pattern pattern : patterns) {
                if (pattern.matcher(password).find()) strength += 1;
            }
            strength *= password.length();
            return strength <= 20 ? "slabé" : strength <= 30 ? "střední" : "silné";

        */

        Pattern a = Pattern.compile("[a-z]");
        Matcher b = a.matcher(password);
        if (b.find()){
            strength+=1;
        }
        Pattern c = Pattern.compile("[A-Z]");
        Matcher d = c.matcher(password);
        if (d.find()){
            strength+=1;
        }
        Pattern e = Pattern.compile("[0-9]");
        Matcher f = e.matcher(password);
        if (f.find()){
            strength+=1;
        }
        Pattern g = Pattern.compile("\\W");
        Matcher h = g.matcher(password);
        if (h.find()){
            strength+=1;
        }
        int x = password.length();
        return strength*x <= 20 ? "slabé" : strength*x <= 30 ? "střední" : "silné";
    }

    private void changePassword(String unhashedPassword, String checkPassword) {
        if (arePasswordsEqual(unhashedPassword, checkPassword)) {
            user.setHashedPassword(passwordEncoder.encode(unhashedPassword));
            user.setHasChangedPassword(true);
            userService.save(user);
            Notification.show("Úspěšně jsme vám nastavili nové heslo! 🌞");
        } else Notification.show("Hesla se neshodují 😔");
    }
}
