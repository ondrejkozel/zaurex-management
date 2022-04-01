package cz.wildwest.zaurex.views.invoices;

import cz.wildwest.zaurex.data.entity.Configuration;
import cz.wildwest.zaurex.data.entity.Invoice;
import cz.wildwest.zaurex.data.service.ConfigurationService;
import cz.wildwest.zaurex.data.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
public class ExportInvoice {

    private final InvoiceService invoiceService;
    private final ConfigurationService configurationService;

    public ExportInvoice(InvoiceService invoiceService, ConfigurationService configurationService) {
        this.invoiceService = invoiceService;
        this.configurationService = configurationService;
    }

    @GetMapping("/invoices/export")
    public void exportToPDF(HttpServletResponse response, @RequestParam String id) throws Exception {
        Optional<Invoice> invoice = invoiceService.find(Long.parseLong(id));
        if (invoice.isEmpty()) throw new Exception("Ilegal invice id.");
        //
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=faktura_" + invoice.get().getNumber() + ".pdf";
        response.setHeader(headerKey, headerValue);
        //
        InvoicesPdfExporter exporter = new InvoicesPdfExporter(invoice.get(), configurationService.getValue(Configuration.StandardKey.ICO).orElse(""), configurationService.getValue(Configuration.StandardKey.BANK_ACCOUNT_NUMBER).orElse(""));
        exporter.export(response);
    }
}
