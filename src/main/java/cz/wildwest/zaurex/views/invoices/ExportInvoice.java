package cz.wildwest.zaurex.views.invoices;

import com.lowagie.text.DocumentException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class ExportInvoice {

    @GetMapping("/invoices/export")
    public void exportToPDF(HttpServletResponse response, @RequestParam String id) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        //
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=faktura.pdf";
        response.setHeader(headerKey, headerValue);
        //
        InvoicesPdfExporter exporter = new InvoicesPdfExporter();
        exporter.export(response);

    }
}
