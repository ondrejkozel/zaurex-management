package cz.wildwest.zaurex.views.invoices;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import cz.wildwest.zaurex.data.entity.Invoice;
import cz.wildwest.zaurex.views.LocalDateTimeFormatter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

public class InvoicesPdfExporter {

    public InvoicesPdfExporter() {
    }

    public void export(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        headerFont.setSize(18);
        Paragraph p = new Paragraph("Faktura z " + LocalDateTime.now().format(LocalDateTimeFormatter.ofMediumDateTime()), headerFont);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);
        document.close();
    }
}
