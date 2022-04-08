package cz.wildwest.zaurex.views.invoices;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import cz.wildwest.zaurex.data.entity.Invoice;
import cz.wildwest.zaurex.views.LocalDateTimeFormatter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;

public class InvoicesPdfExporter {

    private static final Font TITLE = new Font(Font.HELVETICA, 26, Font.BOLD);
    private static final Font BOLD;
    static {
        BOLD = new Font();
        BOLD.setStyle(Font.BOLD);
    }

    private final Invoice invoice;
    private final String title;
    private final String ic;
    private final String bankAccountNumber;
    private final Locale locale;

    public InvoicesPdfExporter(Invoice invoice, String ic, String bankAccountNumber) {
        this.invoice = invoice;
        this.title = "Faktura – daňový doklad #" + invoice.getNumber();
        this.ic = ic;
        this.bankAccountNumber = bankAccountNumber;
        this.locale = LocalDateTimeFormatter.LOCALE;
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
        addEmptyLines(document, 2);
        addTotalPrice(document);
        addEmptyLines(document, 4);
        addIssuedBy(document);
        ColumnText.showTextAligned(pdfWriter.getDirectContent(), Element.ALIGN_LEFT, new Phrase("Vygenerováno " + LocalDateTime.now().format(LocalDateTimeFormatter.ofMediumDateTime())), 30, 30, 0);
        //
        document.close();
    }

    private void addTotalPrice(Document document) {
        Paragraph paragraph = new Paragraph();
        paragraph.add("Celková částka k úhradě: ");
        paragraph.add(new Phrase(String.format(locale, "%.2f CZK", invoice.getTotalPrice()), BOLD));
        document.add(paragraph);
    }

    private void addIssuedBy(Document document) {
        Paragraph paragraph = new Paragraph("Vystavil: " + invoice.getIssuedBy());
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(paragraph);
    }

    private void addMetaData(Document document) {
        document.addTitle("Faktura " + invoice.getNumber());
        document.addCreator("Zaurex management");
    }

    private void addTitle(Document document) {
        Paragraph header = new Paragraph();
        header.add(new Paragraph(title, TITLE));
        document.add(header);
    }

    private void addInfo(Document document) {
        PdfPTable table = new PdfPTable(2);
        formatSmallTable(table);
        //
        table.addCell(getLeftCell("Dodavatel"));
        table.addCell(getLeftCell("Zaurex s.r.o."));
        //
        if (!ic.isBlank()) {
            table.addCell(getLeftCell("IČ"));
            table.addCell(getLeftCell(ic));
        }
        //
        table.addCell(getLeftCell("Datum vystavení"));
        table.addCell(getLeftCell(invoice.getIssuedAt().format(LocalDateTimeFormatter.ofShortDate())));
        //
        table.addCell(getLeftCell("Datum splatnosti"));
        table.addCell(getLeftCell(invoice.getMaturityDate().format(LocalDateTimeFormatter.ofShortDate())));
        //
        table.addCell(getLeftCell("Forma úhrady"));
        table.addCell(getLeftCell(invoice.getPaymentForm().getText()));
        //
        if (invoice.getPaymentForm() == Invoice.PaymentForm.TRANSFER && !bankAccountNumber.isBlank()) {
            table.addCell(getLeftCell("Číslo účtu"));
            table.addCell(getLeftCell(bankAccountNumber));
        }
        //
        document.add(table);
        //
        Invoice.PurchaserInfo purchaserInfo = invoice.getPurchaserInfo();
        if (purchaserInfo != null) {
            addEmptyLines(document, 1);
            //
            PdfPTable table2 = new PdfPTable(2);
            formatSmallTable(table2);
            //
            table2.addCell(getLeftCell("Odběratel"));
            table2.addCell(getLeftCell(purchaserInfo.getPurchaserName()));
            //
            if (purchaserInfo.getCompanyName() != null && !purchaserInfo.getCompanyName().isBlank()) {
                table2.addCell(getLeftCell(""));
                table2.addCell(getLeftCell(purchaserInfo.getCompanyName()));
            }
            //
            if (purchaserInfo.getIc() != null && !purchaserInfo.getIc().isBlank()) {
                table2.addCell(getLeftCell("IČ"));
                table2.addCell(getLeftCell(purchaserInfo.getIc()));
            }
            //
            table2.addCell(getLeftCell("Adresa"));
            table2.addCell(getLeftCell(purchaserInfo.getAddress1()));
            table2.addCell(getLeftCell(""));
            table2.addCell(getLeftCell(purchaserInfo.getAddress2()));
            //
            document.add(table2);
        }
    }

    private void formatSmallTable(PdfPTable table) {
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setWidthPercentage(60);
        table.setTableEvent((table1, width, height, headerRows, rowStart, canvas) -> {
            float[] widths = width[0];
            float x1 = widths[0];
            float x2 = widths[widths.length - 1];
            float y1 = height[0];
            float y2 = height[height.length - 1];
            PdfContentByte cb = canvas[PdfPTable.LINECANVAS];
            cb.rectangle(x1, y1, x2 - x1, y2 - y1 - 3);
            cb.stroke();
            cb.resetRGBColorStroke();
        });
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
            table.addCell(getRightCell(String.format(locale, "%.2f", item.getPricePerOne())));
            table.addCell(getRightCell(String.format(locale, "%.2f", item.getTotalPrice())));
        });
        for (int i = 0; i < 5; i++) {
            PdfPCell cell = getCell(i == 0 ? String.valueOf(invoice.getTotalAmount()) : i == 4 ? String.format(locale, "%.2f CZK", invoice.getTotalPrice()) : "", BOLD, Rectangle.TOP);
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
