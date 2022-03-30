package cz.wildwest.zaurex.views.invoices;

import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
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
    }

    private void configureColumns() {
        grid.addColumn("Datum vystavení", new TextRenderer<>(item -> item.getIssuedAt().format(LocalDateTimeFormatter.ofMediumDateTime())), true);
        grid.addColumn("Vystavil", new TextRenderer<>(Invoice::getIssuedBy), false);
        grid.addColumn("Celkem k úhradě", new ComponentRenderer<>(item -> new NumberGriddCell(item.getTotalPrice() + " Kč")), true);
    }

    private CrudEditor<Invoice> buildEditor() {
        TextField issuedBy = new TextField("Vystavil");
        DateTimePicker issuedAt = new DateTimePicker("Vystaveno");
        TextArea details = new TextArea("Podrobnosti");
        TextField totalPrice = new TextField("Celkem k úhradě");
        //
        FormLayout formLayout = new FormLayout(issuedBy, issuedAt, details, totalPrice);
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
        binder.forField(totalPrice).bindReadOnly(invoice -> invoice.getTotalPrice() + " Kč");
        return new BinderCrudEditor<>(binder, formLayout);
    }
}

