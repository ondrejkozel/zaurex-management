package cz.wildwest.zaurex.views.sell;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.data.entity.Invoice;
import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.data.entity.WarehouseItem;
import cz.wildwest.zaurex.data.service.InvoiceService;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.LineAwesomeIcon;
import cz.wildwest.zaurex.views.LocalDateTimeFormatter;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PageTitle("Prodat")
@Route(value = "sell", layout = MainLayout.class)
@RolesAllowed("SALESMAN")
public class SellView extends Div {

    private final Map<WarehouseItem.Variant, Integer> items;
    private final InvoiceService invoiceService;
    private final User user;

    public SellView(InvoiceService invoiceService, AuthenticatedUser authenticatedUser) {
        this.invoiceService = invoiceService;
        this.user = authenticatedUser.get().orElseThrow();
        addClassNames("sell-view", "flex", "flex-col", "h-full");

        items = new HashMap<>();

        Main content = new Main();
        content.addClassNames("grid", "gap-xl", "items-start", "justify-center", "max-w-screen-md", "mx-auto", "pb-l",
                "px-l");

        content.add(createCheckoutForm());
        content.add(createAside());
        add(content);
    }

    private Component createCheckoutForm() {
        Section checkoutForm = new Section();
        checkoutForm.addClassNames("flex", "flex-col", "flex-grow");

        H2 header = new H2("Pokladna");
        header.addClassNames("mb-0", "mt-xl", "text-3xl");
//        Paragraph note = new Paragraph("All fields are required unless otherwise noted");
//        note.addClassNames("mb-xl", "mt-0", "text-secondary");
        checkoutForm.add(header);

        checkoutForm.add(createPersonalDetailsSection());
        checkoutForm.add(createPaymentInformationSection());
        checkoutForm.add(createPurchaserSection());
        checkoutForm.add(new Hr());
        checkoutForm.add(createFooter());

        return checkoutForm;
    }

    private Section createPersonalDetailsSection() {
        Section personalDetails = new Section();
        personalDetails.addClassNames("flex", "flex-col", "mb-xl", "mt-m");

        Paragraph stepOne = new Paragraph("Krok 1/3");
        stepOne.addClassNames("m-0", "text-s", "text-secondary");

        H3 header = new H3("Personal details");
        header.addClassNames("mb-m", "mt-s", "text-2xl");

        TextField name = new TextField("Name");
        name.setRequiredIndicatorVisible(true);
        name.setPattern("[\\p{L} \\-]+");

        EmailField email = new EmailField("Email address");
        email.setRequiredIndicatorVisible(true);

        TextField phone = new TextField("Phone number");
        phone.setRequiredIndicatorVisible(true);
        phone.setPattern("[\\d \\-\\+]+");

        Checkbox rememberDetails = new Checkbox("Remember personal details for next time");
        rememberDetails.addClassNames("mt-s");

        personalDetails.add(stepOne, header, name, email, phone, rememberDetails);
        return personalDetails;
    }

    RadioButtonGroup<Invoice.PaymentForm> paymentForm;

    private Component createPaymentInformationSection() {
        Section paymentInfo = new Section();
        paymentInfo.addClassNames("flex", "flex-col", "mb-xl", "mt-m");
        //
        Paragraph stepTwo = new Paragraph("Krok 2/3");
        stepTwo.addClassNames("m-0", "text-s", "text-secondary");
        //
        H3 header = new H3("Platba");
        header.addClassNames("mb-m", "mt-s", "text-2xl");
        //
        Paragraph transferInfo = new Paragraph(String.format("Datum splatnosti: %s.", LocalDateTime.now().plus(Invoice.TRANSFER_MATURITY_LIMIT).format(LocalDateTimeFormatter.ofLongDate())));
        transferInfo.addClassNames("text-s", "text-secondary");
        //
        paymentForm = new RadioButtonGroup<>();
        paymentForm.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        paymentForm.setLabel("Forma úhrady");
        paymentForm.setItems(Invoice.PaymentForm.values());
        paymentForm.setRequired(true);
        paymentForm.setRenderer(new TextRenderer<>(Invoice.PaymentForm::getText));
        paymentForm.addValueChangeListener(event -> transferInfo.setVisible(event.getValue() == Invoice.PaymentForm.TRANSFER));
        paymentForm.setValue(Invoice.PaymentForm.CASH);
        //
        paymentInfo.add(stepTwo, header, paymentForm, transferInfo);
        return paymentInfo;
    }

    private TextField ic;
    private TextField companyName;
    private TextField purchaserName;
    private TextField address;
    private TextField postalCode;
    private TextField city;

    private Span purchaseFields;

    private boolean specifyPurchaserStatus = false;

    private Section createPurchaserSection() {
        Section shippingDetails = new Section();
        shippingDetails.addClassNames("flex", "flex-col", "mb-xl", "mt-m");

        Paragraph stepThree = new Paragraph("Krok 3/3");
        stepThree.addClassNames("m-0", "text-s", "text-secondary");

        H3 header = new H3("Odběratel");
        header.addClassNames("mb-m", "mt-s", "text-2xl");

        Checkbox specifyPurchaser = new Checkbox("Specifikovat odběratele");

        ic = new TextField("IČ");
        ic.setMaxLength(50);

        companyName = new TextField("Název firmy");
        companyName.setMaxLength(50);

        purchaserName = new TextField("Jméno odběratele");
        purchaserName.setMaxLength(50);
        purchaserName.setRequiredIndicatorVisible(true);

        address = new TextField("Adresa");
        address.setMaxLength(50);
        address.setRequiredIndicatorVisible(true);

        Div subSection = new Div();
        subSection.addClassNames("flex", "flex-wrap", "gap-m");

        postalCode = new TextField("PSČ");
        postalCode.setRequiredIndicatorVisible(true);
        postalCode.setPattern("^[0-9]{1,5}$");
        postalCode.addValueChangeListener(event -> {
            if (event.getValue().contains(" ")) postalCode.setValue(event.getValue().replace(" ", ""));
        });

        city = new TextField("Město");
        city.setMaxLength(50);
        city.setRequiredIndicatorVisible(true);
        city.addClassNames("flex-grow");

        subSection.add(postalCode, city);

        purchaseFields = new Span(ic, companyName, purchaserName, address, subSection);
        purchaseFields.getStyle().set("display", "grid");
        purchaseFields.getStyle().set("overflow", "hidden");

        specifyPurchaser.addValueChangeListener(event -> switchPurchaseFieldsEnabled());
        setPurchaseFieldsEnabled(false);

        shippingDetails.add(stepThree, header, specifyPurchaser, purchaseFields);
        return shippingDetails;
    }

    private void switchPurchaseFieldsEnabled() {
        setPurchaseFieldsEnabled(!specifyPurchaserStatus);
    }

    private void setPurchaseFieldsEnabled(boolean enabled) {
        specifyPurchaserStatus = enabled;
        List.of(ic, companyName, purchaserName, address, postalCode, city).forEach(component -> component.setEnabled(enabled));
        purchaseFields.getStyle().set("height", enabled ? "unset" : "0");
    }

    private Footer createFooter() {
        Footer footer = new Footer();
        footer.addClassNames("flex", "items-center", "justify-between", "my-m");

        Button cancel = new Button("Začít od začátku");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        cancel.addClickListener(event -> UI.getCurrent().getPage().reload());

        Button pay = new Button("Prodat", new LineAwesomeIcon("las la-dollar-sign"));
        pay.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        pay.addClickListener(event -> sellAndSaveInvoice());

        footer.add(cancel, pay);
        return footer;
    }

    private Aside createAside() {
        Aside aside = new Aside();
        aside.addClassNames("bg-contrast-5", "box-border", "p-l", "rounded-l", "sticky");
        Header headerSection = new Header();
        headerSection.addClassNames("flex", "items-center", "justify-between", "mb-m");
        H3 header = new H3("Rekapitulace");
        header.addClassNames("m-0");
        Button edit = new Button(new LineAwesomeIcon("las la-broom"));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR);
        headerSection.add(header, edit);

        UnorderedList ul = new UnorderedList();
        ul.addClassNames("list-none", "m-0", "p-0", "flex", "flex-col", "gap-m");

        ul.add(createListItem("Vanilla cracker", "With wholemeal flour", "$7.00"));
        ul.add(createListItem("Vanilla blueberry cake", "With blueberry jam", "$8.00"));
        ul.add(createListItem("Vanilla pastry", "With wholemeal flour", "$5.00"));

        aside.add(headerSection, ul);
        return aside;
    }

    private ListItem createListItem(String primary, String secondary, String price) {
        ListItem item = new ListItem();
        item.addClassNames("flex", "justify-between");

        Div subSection = new Div();
        subSection.addClassNames("flex", "flex-col");

        subSection.add(new Span(primary));
        Span secondarySpan = new Span(secondary);
        secondarySpan.addClassNames("text-s text-secondary");
        subSection.add(secondarySpan);

        Span priceSpan = new Span(price);

        item.add(subSection, priceSpan);
        return item;
    }

    private void sellAndSaveInvoice() {
        // TODO: 05.04.2022 sell
        //
        List<Invoice.Item> invoiceItems = new ArrayList<>();
        items.forEach((variant, integer) -> invoiceItems.add(new Invoice.Item(variant.getOf().getTitle(), variant.getColour(), integer, variant.getPrice())));
        Invoice invoice = new Invoice(user, invoiceItems, paymentForm.getValue());
        if (specifyPurchaserStatus) {
            invoice.setPurchaserInfo(new Invoice.PurchaserInfo(ic.getValue(), companyName.getValue(), purchaserName.getValue(), address.getValue(), postalCode.getValue() + ", " + city.getValue()));
        }
        invoiceService.save(invoice);
    }
}
