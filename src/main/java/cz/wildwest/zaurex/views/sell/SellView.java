package cz.wildwest.zaurex.views.sell;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.components.PdfAnchor;
import cz.wildwest.zaurex.components.VariantSelect;
import cz.wildwest.zaurex.data.entity.Invoice;
import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.data.entity.WarehouseItem;
import cz.wildwest.zaurex.data.service.InvoiceService;
import cz.wildwest.zaurex.data.service.WarehouseItemVariantService;
import cz.wildwest.zaurex.data.service.WarehouseService;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.LineAwesomeIcon;
import cz.wildwest.zaurex.views.LocalDateTimeFormatter;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import javax.validation.*;
import javax.validation.metadata.ConstraintDescriptor;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@PageTitle("Prodat")
@Route(value = "sell", layout = MainLayout.class)
@RolesAllowed("SALESMAN")
public class SellView extends Div {

    private final InvoiceService invoiceService;
    private final User user;
    private final WarehouseService warehouseService;
    private final WarehouseItemVariantService warehouseItemVariantService;

    public SellView(InvoiceService invoiceService, AuthenticatedUser authenticatedUser, WarehouseService warehouseService, WarehouseItemVariantService warehouseItemVariantService) {
        this.invoiceService = invoiceService;
        this.user = authenticatedUser.get().orElseThrow();
        this.warehouseService = warehouseService;
        this.warehouseItemVariantService = warehouseItemVariantService;
        addClassNames("sell-view", "flex", "flex-col", "h-full");

        Main content = new Main();
        content.addClassNames("grid", "gap-xl", "items-start", "justify-center", "mx-auto", "pb-l",
                "px-l");
        content.getStyle().set("max-width", "870px");

        content.add(createCheckoutForm());
        content.add(createAside());
        add(content);
    }

    private Section checkoutForm;

    private Component createCheckoutForm() {
        checkoutForm = new Section();
        checkoutForm.addClassNames("flex", "flex-col", "flex-grow");
        rebuildCheckoutForm();
        return checkoutForm;
    }

    private void rebuildCheckoutForm() {
        List<WarehouseItem> itemsWithTransientValues = warehouseService.findAllSellable();
        warehouseService.fetchTransientVariants(itemsWithTransientValues);
        checkoutForm.removeAll();
        H2 header = new H2("Pokladna");
        header.addClassNames("mb-0", "mt-xl", "text-3xl");
//        Paragraph note = new Paragraph("All fields are required unless otherwise noted");
//        note.addClassNames("mb-xl", "mt-0", "text-secondary");
        checkoutForm.add(header);

        checkoutForm.add(createItemsSection(itemsWithTransientValues));
        checkoutForm.add(createPaymentInformationSection());
        checkoutForm.add(createPurchaserSection());
        checkoutForm.add(new Hr());
        checkoutForm.add(createFooter());
    }

    private ItemsEditor itemsEditor;

    private Section createItemsSection(List<WarehouseItem> itemsWithTransientValues) {
        Section personalDetails = new Section();
        personalDetails.addClassNames("flex", "flex-col", "mb-xl", "mt-m");

        Paragraph stepOne = new Paragraph("Krok 1/3");
        stepOne.addClassNames("m-0", "text-s", "text-secondary");

        H3 header = new H3("Polo??ky");
        header.addClassNames("mb-m", "mt-s", "text-2xl");

        itemsEditor = new ItemsEditor(itemsWithTransientValues);
        itemsEditor.addValueChangeListener(event -> setRecapitulationItems(event.getValue()));

        personalDetails.add(stepOne, header, itemsEditor);
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
        paymentForm.setLabel("Forma ??hrady");
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

        H3 header = new H3("Odb??ratel");
        header.addClassNames("mb-m", "mt-s", "text-2xl");

        Checkbox specifyPurchaser = new Checkbox("Specifikovat odb??ratele");

        ic = new TextField("I??");
        ic.setPattern("^[0-9]{0,50}$");
        ic.setMaxLength(50);

        companyName = new TextField("N??zev firmy");
        companyName.setMaxLength(50);

        purchaserName = new TextField("Jm??no odb??ratele");
        purchaserName.setMaxLength(50);
        purchaserName.setRequiredIndicatorVisible(true);

        address = new TextField("Adresa");
        address.setMaxLength(50);
        address.setRequiredIndicatorVisible(true);

        Div subSection = new Div();
        subSection.addClassNames("flex", "flex-wrap", "gap-m");

        postalCode = new TextField("PS??");
        postalCode.setRequiredIndicatorVisible(true);
        postalCode.setPattern("^[0-9]{1,5}$");
        postalCode.setMaxLength(5);
        postalCode.addValueChangeListener(event -> {
            if (event.getValue().contains(" ")) postalCode.setValue(event.getValue().replace(" ", ""));
        });

        city = new TextField("M??sto");
        city.setMaxLength(50);
        city.setRequiredIndicatorVisible(true);
        city.addClassNames("flex-grow");

        subSection.add(postalCode, city);

        purchaseFields = new Span(ic, companyName, purchaserName, address, subSection);
        purchaseFields.getStyle().set("display", "grid");
        purchaseFields.getStyle().set("overflow", "hidden");

        purchaserFields = List.of(ic, companyName, purchaserName, address, postalCode, city);
        purchaserFields.forEach(textField -> textField.addValueChangeListener(event -> {
            if (event.getValue().length() > textField.getMaxLength()) textField.setValue(event.getOldValue());
        }));

        specifyPurchaser.addValueChangeListener(event -> switchPurchaseFieldsEnabled());
        setPurchaseFieldsEnabled(false);

        shippingDetails.add(stepThree, header, specifyPurchaser, purchaseFields);
        return shippingDetails;
    }

    private void switchPurchaseFieldsEnabled() {
        setPurchaseFieldsEnabled(!specifyPurchaserStatus);
    }

    private List<TextField> purchaserFields;

    private void setPurchaseFieldsEnabled(boolean enabled) {
        specifyPurchaserStatus = enabled;
        purchaserFields.forEach(component -> {
            component.setValue(component.getEmptyValue());
            component.setEnabled(enabled);
        });
        purchaseFields.getStyle().set("height", enabled ? "unset" : "0");
    }

    private Footer createFooter() {
        Footer footer = new Footer();
        footer.addClassNames("flex", "items-center", "justify-between", "my-m");

        Button cancel = new Button("Obnovit");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        cancel.addClickListener(event -> clean());

        Button pay = new Button("Prodat", new LineAwesomeIcon("las la-dollar-sign"));
        pay.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        pay.addClickListener(event -> sellAndSaveInvoice());

        footer.add(cancel, pay);
        return footer;
    }

    private void clean() {
        rebuildCheckoutForm();
        itemsEditor.clean();
    }

    private UnorderedList itemsRecapitulationList;

    private Aside createAside() {
        Aside aside = new Aside();
        aside.addClassNames("bg-contrast-5", "box-border", "p-l", "rounded-l", "sticky");
        Header headerSection = new Header();
        headerSection.addClassNames("flex", "items-center", "justify-between", "mb-m");
        H3 header = new H3("Rekapitulace");
        header.addClassNames("m-0");
        headerSection.add(header);

        itemsRecapitulationList = new UnorderedList();
        itemsRecapitulationList.addClassNames("list-none", "m-0", "p-0", "flex", "flex-col", "gap-m");

        setRecapitulationItems(new HashMap<>());

        aside.add(headerSection, itemsRecapitulationList);
        return aside;
    }

    private void setRecapitulationItems(Map<WarehouseItem.Variant, Integer> items) {
        itemsRecapitulationList.removeAll();
        items.forEach((variant, integer) -> itemsRecapitulationList.add(createListItem(integer + "x " + variant.getOf().getTitle(), variant.getColour(), String.format(LocalDateTimeFormatter.LOCALE, "%.2f K??", variant.getPrice() * integer))));
        if (!items.isEmpty()) {
            itemsRecapitulationList.add(new Hr());
            final Double[] totalPrice = {0d};
            items.forEach((variant, integer) -> totalPrice[0] += variant.getPrice() * integer);
            itemsRecapitulationList.add(createListItem("Celkem", "", String.format(LocalDateTimeFormatter.LOCALE, "%.2f K??", totalPrice[0]), true));
        }
        else {
            itemsRecapitulationList.add(new Label("Seznam polo??ek je pr??zdn??."));
        }
    }

    private ListItem createListItem(String primary, String secondary, String price) {
        return createListItem(primary, secondary, price, false);
    }

    private ListItem createListItem(String primary, String secondary, String price, boolean bold) {
        ListItem item = new ListItem();
        item.addClassNames("flex", "justify-between");

        Div subSection = new Div();
        subSection.addClassNames("flex", "flex-col");

        subSection.add(new Span(primary));
        Span secondarySpan = new Span(secondary);
        secondarySpan.addClassNames("text-s text-secondary");
        subSection.add(secondarySpan);

        Span priceSpan = new Span(price);

        if (bold) item.getStyle().set("font-weight", "bold");

        item.add(subSection, priceSpan);
        return item;
    }

    private void sellAndSaveInvoice() {
        var generatedModelValue = itemsEditor.generateModelValue();
        List<Invoice.Item> invoiceItems = new ArrayList<>();
        generatedModelValue.forEach((variant, integer) -> invoiceItems.add(new Invoice.Item(variant, integer)));
        Invoice invoice = new Invoice(user, invoiceItems, paymentForm.getValue());
        if (specifyPurchaserStatus) {
            invoice.setPurchaserInfo(new Invoice.PurchaserInfo(ic.getValue(), companyName.getValue(), purchaserName.getValue(), address.getValue(), postalCode.getValue() + ", " + city.getValue()));
        }
        //
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Invoice>> violations = new HashSet<>(validator.validate(invoice));
        if (violations.isEmpty()) validateCount(invoice.getItems(), itemsEditor.count(), violations);
        if (!violations.isEmpty()) {
            violations.forEach(invoiceConstraintViolation -> Notification.show(invoiceConstraintViolation.getMessage()));
            return;
        }
        //
        generatedModelValue.forEach((variant, integer) -> {
            variant.setQuantity(variant.getQuantity() - integer);
            warehouseItemVariantService.save(variant);
        });
        //
        invoiceService.save(invoice);
        buildAndShowNewInvoiceNotification(invoice);
        clean();
    }

    private void validateCount(List<Invoice.Item> invoiceItems, int itemCount, Set<ConstraintViolation<Invoice>> violations) {
        if (itemCount != invoiceItems.size()) violations.add(new ConstraintViolation<>() {
            @Override
            public String getMessage() {
                return "V??echny polo??ky seznamu nejsou unik??tn??.";
            }

            @Override
            public String getMessageTemplate() {
                return null;
            }

            @Override
            public Invoice getRootBean() {
                return null;
            }

            @Override
            public Class<Invoice> getRootBeanClass() {
                return null;
            }

            @Override
            public Object getLeafBean() {
                return null;
            }

            @Override
            public Object[] getExecutableParameters() {
                return new Object[0];
            }

            @Override
            public Object getExecutableReturnValue() {
                return null;
            }

            @Override
            public Path getPropertyPath() {
                return null;
            }

            @Override
            public Object getInvalidValue() {
                return null;
            }

            @Override
            public ConstraintDescriptor<?> getConstraintDescriptor() {
                return null;
            }

            @Override
            public <U> U unwrap(Class<U> type) {
                return null;
            }
        });
    }

    private void buildAndShowNewInvoiceNotification(Invoice invoice) {
        Button pdfButton = new Button("Zobrazit PDF", new LineAwesomeIcon("las la-file-pdf"));
        HorizontalLayout layout = new HorizontalLayout(new Label("Transakce byla dokon??ena."), new PdfAnchor(invoice, pdfButton));
        layout.setSpacing(true);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        Notification notification = new Notification();
        notification.setDuration(5000);
        notification.add(layout);
        notification.open();
        pdfButton.addClickListener(event -> notification.close());
    }

    public class ItemsEditor extends CustomField<Map<WarehouseItem.Variant, Integer>> {

        private final VerticalLayout itemEditorsLayout;
        private final List<WarehouseItem> itemsWithTransientValues;

        public ItemsEditor(List<WarehouseItem> itemsWithTransientValues) {
            this.itemsWithTransientValues = itemsWithTransientValues;
            itemEditorsLayout = new VerticalLayout();
            itemEditorsLayout.setPadding(false);
            Button addButton = new Button("P??idat polo??ku", new LineAwesomeIcon("las la-plus"));
            addButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            addButton.addClickListener(event -> addButtonClicked());
            //
            Button cleanButton = new Button(new LineAwesomeIcon("las la-broom"));
            cleanButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR);
            cleanButton.addClickListener(event -> clean());
            //
            VerticalLayout verticalLayout = new VerticalLayout(itemEditorsLayout, new HorizontalLayout(addButton, cleanButton));
            verticalLayout.setPadding(false);
            add(verticalLayout);
            //
            addButtonClicked();
        }

        private VariantSelect addButtonClicked() {
            VariantSelect variantSelect = new VariantSelect(itemsWithTransientValues);
            variantSelect.setSellMode();
            Span order = new Span("#" + (itemEditorsLayout.getChildren().count() + 1));
            order.addClassNames("text-s", "text-secondary");
            Span blank = new Span();
            Button delete = new Button(VaadinIcon.CLOSE.create());
            delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR);
            HorizontalLayout child = new HorizontalLayout(order, blank, delete);
            child.setFlexGrow(1, blank);
            child.setWidthFull();
            VerticalLayout parent = new VerticalLayout(child, variantSelect);
            parent.setPadding(false);
            parent.setSpacing(false);
            delete.addClickListener(event -> {
                itemEditorsLayout.remove(parent);
                setRecapitulationItems(generateModelValue());
            });
            itemEditorsLayout.add(parent);
            return variantSelect;
        }

        public void clean() {
            itemEditorsLayout.removeAll();
            addButtonClicked();
            setRecapitulationItems(new HashMap<>());
        }

        @Override
        protected Map<WarehouseItem.Variant, Integer> generateModelValue() {
            Map<WarehouseItem.Variant, Integer> map = new HashMap<>();
            itemEditorsLayout.getChildren().map(component -> component.getChildren().collect(Collectors.toList()).get(1)).forEach(component -> {
                VariantSelect variantSelect = (VariantSelect) component;
                WarehouseItem.Variant variant = variantSelect.generateModelValue();
                if (variant != null) map.put(variant, variantSelect.getAmount());
            });
            return map;
        }

        public int count() {
            return (int) itemEditorsLayout.getChildren().count();
        }

        @Override
        protected void setPresentationValue(Map<WarehouseItem.Variant, Integer> newPresentationValue) {
            newPresentationValue.forEach((variant, integer) -> {
                VariantSelect variantSelect = addButtonClicked();
                variantSelect.setPresentationValue(variant);
                variantSelect.setAmount(integer);
            });
        }
    }
}
