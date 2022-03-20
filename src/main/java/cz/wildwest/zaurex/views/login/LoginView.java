package cz.wildwest.zaurex.views.login;

import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.homePage.HomePageView;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        setAction("login");
        //
        setI18n(buildLocalization());
        //
        setForgotPasswordButtonVisible(false);
        //
        setOpened(true);
    }

    private LoginI18n buildLocalization() {
        LoginI18n loginI18n = LoginI18n.createDefault();
        //
        loginI18n.setHeader(new LoginI18n.Header());
        loginI18n.getHeader().setTitle("Zaurex management");
        loginI18n.getHeader().setDescription("Vítejte v managementu! Můžeme začít.");
        //
        loginI18n.setForm(new LoginI18n.Form());
        loginI18n.getForm().setTitle("Přihlásit se");
        loginI18n.getForm().setUsername("Uživatelské jméno");
        loginI18n.getForm().setPassword("Heslo");
        loginI18n.getForm().setSubmit("Pokračovat");
        loginI18n.getForm().setForgotPassword("Zapomenuté heslo");
        //
        loginI18n.setErrorMessage(new LoginI18n.ErrorMessage());
        loginI18n.getErrorMessage().setTitle("Nesprávné přihlašovací údaje");
        loginI18n.getErrorMessage().setMessage("Ověřte, že bylo správně zadáno uživatelské jméno a heslo a zkuste to znovu.");
        //
        loginI18n.setAdditionalInformation("Pokud si nepamatujete svoje přihlašovací údaje, kontaktujte manažera.");
        return loginI18n;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (authenticatedUser.get().isPresent()) {
            setOpened(false);
            beforeEnterEvent.forwardTo(HomePageView.class);
        }
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) setError(true);
    }
}
