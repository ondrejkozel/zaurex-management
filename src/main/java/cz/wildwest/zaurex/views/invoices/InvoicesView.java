package cz.wildwest.zaurex.views.invoices;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.components.gridd.GenericDataProvider;
import cz.wildwest.zaurex.components.gridd.Gridd;
import cz.wildwest.zaurex.components.gridd.NumberGriddCell;
import cz.wildwest.zaurex.data.entity.Invoice;
import cz.wildwest.zaurex.data.service.InvoiceService;
import cz.wildwest.zaurex.views.LineAwesomeIcon;
import cz.wildwest.zaurex.views.LocalDateTimeFormatter;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;

@PageTitle("Faktury")
@Route(value = "invoices", layout = MainLayout.class)
@RolesAllowed({"SHIFT_LEADER", "MANAGER"})
public class InvoicesView extends VerticalLayout {

    private final Gridd<Invoice> grid;

    public InvoicesView(InvoiceService invoiceService) {
        grid = new Gridd<>(Invoice.class,
                new GenericDataProvider<>(invoiceService, Invoice.class),
                Invoice::new,
                false,
                buildEditor(),
                "",
                "Faktura",
                "");
        configureColumns();
        //
        setSizeFull();
        add(grid);
        //
        grid.getCrud().addEditListener(event -> pdfAnchor.setHref(event.getItem().getId()));
    }

    private void configureColumns() {
        grid.addColumn("Datum vystavení", new TextRenderer<>(item -> item.getIssuedAt().format(LocalDateTimeFormatter.ofMediumDateTime())), true);
        grid.addColumn("Vystavil", new TextRenderer<>(Invoice::getIssuedBy), true);
        grid.addColumn("Celkem k úhradě", new ComponentRenderer<>(item -> new NumberGriddCell(String.format(LocalDateTimeFormatter.LOCALE, "%.2f Kč", item.getTotalPrice()))), true);
        grid.addColumn("Forma úhrady", new TextRenderer<>(item -> item.getPaymentForm().getText()), false);
        grid.addColumn("PDF", new ComponentRenderer<>(invoice -> new PdfAnchor(invoice, new Button(new LineAwesomeIcon("la la-file-pdf")))), true).setFlexGrow(0);
    }

    PdfAnchor pdfAnchor;

    private CrudEditor<Invoice> buildEditor() {
        TextField issuedBy = new TextField("Vystavil");
        DateTimePicker issuedAt = new DateTimePicker("Vystaveno");
        TextArea details = new TextArea("Položky");
        TextField totalPrice = new TextField("Celkem k úhradě");
        totalPrice.setHelperText("Další informace získáte v PDF verzi faktury.");
        //
        Button buildPdfButton = new Button("Sestavit PDF", new LineAwesomeIcon("las la-file-pdf"));
        pdfAnchor = new PdfAnchor(buildPdfButton);
        //
        FormLayout formLayout = new FormLayout(issuedBy, issuedAt, details, totalPrice, new HorizontalLayout(pdfAnchor));
        formLayout.setColspan(details, 2);
        formLayout.setColspan(totalPrice, 2);
        //
        Binder<Invoice> binder = new BeanValidationBinder<>(Invoice.class);
        binder.forField(issuedBy).bindReadOnly(Invoice::getIssuedBy);
        binder.forField(issuedAt).bindReadOnly(Invoice::getIssuedAt);
        binder.forField(details).bindReadOnly(invoice -> {
            StringBuilder stringBuilder = new StringBuilder();
            invoice.getItems().forEach(item -> stringBuilder.append(item.getAmount()).append("x ").append(item.getLabel()).append(" – ").append(item.getVariantLabel()).append(", ").append(item.getTotalPrice()).append(" Kč\n"));
            return stringBuilder.toString();
        });
        binder.forField(totalPrice).bindReadOnly(invoice -> String.format(LocalDateTimeFormatter.LOCALE, "%.2f Kč", invoice.getTotalPrice()));
        return new BinderCrudEditor<>(binder, formLayout);
    }

    private static class PdfAnchor extends Anchor {

        public PdfAnchor(Button button) {
            add(button);
            setTarget("_blank");
        }

        public PdfAnchor(Invoice invoice, Button button) {
            this(button);
            setHref(invoice.getId());
        }

        public void setHref(long invoiceId) {
            setHref("/invoices/export?id=" + invoiceId);
        }
    }
}

