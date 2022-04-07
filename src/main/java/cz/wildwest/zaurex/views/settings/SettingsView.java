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

    private Button invoicingSubmit;

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
    private TextField accountNumberField;
    private IntegerField icoField;

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
        passwordField.addValueChangeListener((event) -> {
            passwordField.setHelperText("Síla hesla: " + getPasswordStrength(event.getValue()));
        });
        //
        PasswordField passwordCheck = new PasswordField();
        passwordCheck.setPlaceholder("Potvrďte napsáním znovu");
        passwordCheck.addValueChangeListener((event) -> {
            passwordCheck.setHelperText("Heslo se " + getCheck(passwordField.getValue(),passwordCheck.getValue()));
        });
        
        Button submit = new Button("Potvrdit", new LineAwesomeIcon("las la-check"));
        passwordField.addValueChangeListener(event -> submit.setEnabled(!passwordField.isInvalid()));
        submit.setDisableOnClick(true);
        submit.setEnabled(false);
        submit.addClickListener(clickEvent -> changePassword(passwordField.getValue(), passwordCheck.getValue()));
        add(new HorizontalLayout(passwordField, passwordCheck, submit));
    }
    public String getCheck(String password, String secondPassword){
    return password.equals(secondPassword) ? "shoduje" : "neshoduje";
    };
    
    public String getPasswordStrength(String password){
    var strength=0;
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
    };
    
    private void changePassword(String unhashedPassword, String checkPassword) {
      if(unhashedPassword.equals(checkPassword)) { 
        user.setHashedPassword(passwordEncoder.encode(unhashedPassword));
        user.setHasChangedPassword(true);
        userService.save(user);
        Notification.show("Úspěšně jsme vám nastavili nové heslo! 🌞");
    }
      else{
          Notification.show("Hesla se neshodují");
      }
    }
}
