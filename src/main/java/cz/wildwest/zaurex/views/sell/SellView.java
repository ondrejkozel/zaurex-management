package cz.wildwest.zaurex.views.sell;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.data.entity.Invoice;
import cz.wildwest.zaurex.data.entity.WarehouseItem;
import cz.wildwest.zaurex.views.LineAwesomeIcon;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.util.HashMap;
import java.util.Map;

@PageTitle("Prodat")
@Route(value = "sell", layout = MainLayout.class)
@RolesAllowed("SALESMAN")
public class SellView extends Div {

    private final Map<WarehouseItem.Variant, Integer> items;

    public SellView() {
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
        checkoutForm.add(createShippingAddressSection());
        checkoutForm.add(createPaymentInformationSection());
        checkoutForm.add(new Hr());
        checkoutForm.add(createFooter());

        return checkoutForm;
    }

    private Section createPersonalDetailsSection() {
        Section personalDetails = new Section();
        personalDetails.addClassNames("flex", "flex-col", "mb-xl", "mt-m");

        Paragraph stepOne = new Paragraph("Checkout 1/3");
        stepOne.addClassNames("m-0", "text-s", "text-secondary");

        H3 header = new H3("Personal details");
        header.addClassNames("mb-m", "mt-s", "text-2xl");

        TextField name = new TextField("Name");
        name.setRequiredIndicatorVisible(true);
        name.setPattern("[\\p{L} \\-]+");
        name.addClassNames("mb-s");

        EmailField email = new EmailField("Email address");
        email.setRequiredIndicatorVisible(true);
        email.addClassNames("mb-s");

        TextField phone = new TextField("Phone number");
        phone.setRequiredIndicatorVisible(true);
        phone.setPattern("[\\d \\-\\+]+");
        phone.addClassNames("mb-s");

        Checkbox rememberDetails = new Checkbox("Remember personal details for next time");
        rememberDetails.addClassNames("mt-s");

        personalDetails.add(stepOne, header, name, email, phone, rememberDetails);
        return personalDetails;
    }

    private Section createShippingAddressSection() {
        Section shippingDetails = new Section();
        shippingDetails.addClassNames("flex", "flex-col", "mb-xl", "mt-m");

        Paragraph stepTwo = new Paragraph("Checkout 2/3");
        stepTwo.addClassNames("m-0", "text-s", "text-secondary");

        H3 header = new H3("Shipping address");
        header.addClassNames("mb-m", "mt-s", "text-2xl");

        ComboBox countrySelect = new ComboBox("Country");
        countrySelect.setRequiredIndicatorVisible(true);
        countrySelect.addClassNames("mb-s");

        TextArea address = new TextArea("Street address");
        address.setMaxLength(200);
        address.setRequiredIndicatorVisible(true);
        address.addClassNames("mb-s");

        Div subSection = new Div();
        subSection.addClassNames("flex", "flex-wrap", "gap-m");

        TextField postalCode = new TextField("Postal Code");
        postalCode.setRequiredIndicatorVisible(true);
        postalCode.setPattern("[\\d \\p{L}]*");
        postalCode.addClassNames("mb-s");

        TextField city = new TextField("City");
        city.setRequiredIndicatorVisible(true);
        city.addClassNames("flex-grow", "mb-s");

        subSection.add(postalCode, city);

        ComboBox stateSelect = new ComboBox("State");
        stateSelect.setRequiredIndicatorVisible(true);
        stateSelect.setVisible(false);
        countrySelect.addValueChangeListener(e -> {
            stateSelect.setVisible(countrySelect.getValue().equals("United States"));
        });

        Checkbox sameAddress = new Checkbox("Billing address is the same as shipping address");
        sameAddress.addClassNames("mt-s");

        Checkbox rememberAddress = new Checkbox("Remember address for next time");

        shippingDetails.add(stepTwo, header, countrySelect, address, subSection, stateSelect, sameAddress,
                rememberAddress);
        return shippingDetails;
    }

    private Component createPaymentInformationSection() {
        Section paymentInfo = new Section();
        paymentInfo.addClassNames("flex", "flex-col", "mb-xl", "mt-m");

        Paragraph stepThree = new Paragraph("Checkout 3/3");
        stepThree.addClassNames("m-0", "text-s", "text-secondary");

        H3 header = new H3("Platba");
        header.addClassNames("mb-m", "mt-s", "text-2xl");

        RadioButtonGroup<Invoice.PaymentForm> paymentForm = new RadioButtonGroup<>();
        paymentForm.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        paymentForm.setLabel("Forma úhrady");
        paymentForm.setItems(Invoice.PaymentForm.values());
        paymentForm.setValue(Invoice.PaymentForm.CASH);
        paymentForm.setRequired(true);
        paymentForm.setRenderer(new TextRenderer<>(Invoice.PaymentForm::getText));
        paymentInfo.add(stepThree, header, paymentForm);
        return paymentInfo;
    }

    private Footer createFooter() {
        Footer footer = new Footer();
        footer.addClassNames("flex", "items-center", "justify-between", "my-m");

        Button cancel = new Button("Začít od začátku");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        cancel.addClickListener(event -> UI.getCurrent().getPage().reload());

        Button pay = new Button("Prodat", new LineAwesomeIcon("las la-dollar-sign"));
        pay.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        footer.add(cancel, pay);
        return footer;
    }

    private Aside createAside() {
        Aside aside = new Aside();
        aside.addClassNames("bg-contrast-5", "box-border", "p-l", "rounded-l", "sticky");
        Header headerSection = new Header();
        headerSection.addClassNames("flex", "items-center", "justify-between", "mb-m");
        H3 header = new H3("Objednávka");
        header.addClassNames("m-0");
        Button edit = new Button("Odstranit vše");
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
}
