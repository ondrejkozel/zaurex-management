package cz.wildwest.zaurex.views.invoices;

import com.lowagie.text.*;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import cz.wildwest.zaurex.data.entity.Invoice;
import cz.wildwest.zaurex.views.LocalDateTimeFormatter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

public class InvoicesPdfExporter {

    private static final Font TITLE = new Font(Font.HELVETICA, 26, Font.BOLD);
    private static final Font BOLD;
    static {
        BOLD = new Font();
        BOLD.setStyle(Font.BOLD);
    }

    private final Invoice invoice;
    private final String title;

    public InvoicesPdfExporter(Invoice invoice) {
        this.invoice = invoice;
        this.title = "Faktura – daňový doklad #" + invoice.getNumber();
    }

    public void export(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter pdfWriter = PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        //
        addMetaData(document);
        addTitle(document);
        addEmptyLines(document, 1);
        addInfo(document);
        addEmptyLines(document, 1);
        addItems(document);
        ColumnText.showTextAligned(pdfWriter.getDirectContent(), Element.ALIGN_LEFT, new Phrase("Vygenerováno " + LocalDateTime.now().format(LocalDateTimeFormatter.ofMediumDateTime())), 30, 30, 0);
        //
        document.close();
    }

    private void addMetaData(Document document) {
        document.addTitle(title);
        document.addCreator("Zaurex management");
    }

    private void addTitle(Document document) {
        Paragraph header = new Paragraph();
        header.add(new Paragraph(title, TITLE));
        document.add(header);
    }

    private void addInfo(Document document) {
        PdfPTable table = new PdfPTable(2);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        //
        table.addCell(getLeftCell("Datum vystavení"));
        table.addCell(getLeftCell(invoice.getIssuedAt().format(LocalDateTimeFormatter.ofShortDate())));
        //
        table.addCell(getLeftCell("Vystavil"));
        table.addCell(getLeftCell(invoice.getIssuedBy()));
        //
        document.add(table);
    }

    private void addItems(Document document) {
        PdfPTable table = new PdfPTable(new float[] {1, 10, 3, 3, 3});
        table.setWidthPercentage(100);
        //
        table.addCell(getCell("ks", BOLD));
        table.addCell(getCell("název", BOLD));
        table.addCell(getCell("varianta", BOLD));
        table.addCell(getCell("cena/ks", BOLD));
        table.addCell(getCell("celkem", BOLD));
        invoice.getItems().forEach(item -> {
            table.addCell(getRightCell(String.valueOf(item.getAmount())));
            table.addCell(getLeftCell(item.getLabel()));
            table.addCell(getLeftCell(item.getVariantLabel()));
            table.addCell(getRightCell(String.format("%.2f", item.getPricePerOne())));
            table.addCell(getRightCell(String.format("%.2f", item.getTotalPrice())));
        });
        for (int i = 0; i < 5; i++) {
            PdfPCell cell = getCell(i == 0 ? String.valueOf(invoice.getItems().size()) : i == 4 ? String.format("%.2f CZK", invoice.getTotalPrice()) : "", BOLD, Rectangle.TOP);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
        }
        //
        document.add(table);
    }

    private PdfPCell getRightCell(String text) {
        PdfPCell pdfPCell = getCell(text);
        pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return pdfPCell;
    }

    private PdfPCell getLeftCell(String text) {
        PdfPCell pdfPCell = getCell(text);
        pdfPCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return pdfPCell;
    }

    private PdfPCell getCell(String text) {
        return getCell(text, new Font());
    }

    private PdfPCell getCell(String text, Font font) {
        return getCell(text, font, Rectangle.NO_BORDER);
    }

    private PdfPCell getCell(String text, Font font, int border) {
        Phrase phrase = new Phrase(text, font);
        PdfPCell pdfPCell = new PdfPCell(phrase);
        pdfPCell.setBorder(border);
        return pdfPCell;
    }

    private void addEmptyLines(Document document, int number) {
        for (int i = 0; i < number; i++) {
            document.add(new Paragraph(" "));
        }
    }
}
