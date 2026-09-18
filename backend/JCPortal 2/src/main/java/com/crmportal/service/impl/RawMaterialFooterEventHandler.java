package com.crmportal.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;

public class RawMaterialFooterEventHandler extends AbstractPdfDocumentEventHandler {

    private final PdfFont font;

    public RawMaterialFooterEventHandler(PdfFont font) {
        this.font = font;
    }

    @Override
    protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {

        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        Rectangle pageSize = page.getPageSize();

        PdfCanvas canvas = new PdfCanvas(
                page.newContentStreamAfter(),
                page.getResources(),
                pdfDoc
        );

        float footerY = pageSize.getBottom() + 30;

        int pageNumber = pdfDoc.getPageNumber(page);
        int totalPages = pdfDoc.getNumberOfPages();

        String dateTime =
                new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());

        /* ---------- LEFT : SIGNATURE ---------- */
        canvas.beginText();
        canvas.setFontAndSize(font, 9);
        canvas.moveText(pageSize.getLeft() + 40, footerY);
        canvas.showText("Signature: ____________________");
        canvas.endText();

        /* ---------- CENTER : PAGE NUMBER ---------- */
        canvas.beginText();
        canvas.setFontAndSize(font, 9);
        canvas.moveText(pageSize.getWidth() / 2 - 35, footerY);
        canvas.showText("Page " + pageNumber + " of " + totalPages);
        canvas.endText();

        /* ---------- RIGHT : DATE ---------- */
        canvas.beginText();
        canvas.setFontAndSize(font, 9);
        canvas.moveText(pageSize.getRight() - 170, footerY);
        canvas.showText(dateTime);
        canvas.endText();

        canvas.release();
    }
}
