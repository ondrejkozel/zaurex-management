package cz.wildwest.zaurex.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import cz.wildwest.zaurex.data.entity.Invoice;

public class PdfAnchor extends Anchor {

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
