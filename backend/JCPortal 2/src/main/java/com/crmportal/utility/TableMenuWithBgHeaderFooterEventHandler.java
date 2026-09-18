package com.crmportal.utility;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

public class TableMenuWithBgHeaderFooterEventHandler extends AbstractPdfDocumentEventHandler {

	private String header;
    private String footer;
    private PdfFont font;

    public TableMenuWithBgHeaderFooterEventHandler(String header, String footer, PdfFont font) {
        this.header = header == null ? "" : header;
        this.footer = footer == null ? "" : footer;
        this.font = font;
    }

    @Override
    protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {
    	PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfPage page = docEvent.getPage();

        int pageNumber = docEvent.getDocument().getPageNumber(page);
        
        if (pageNumber == 1) {
            return;
        }
        
        Rectangle pageSize = page.getPageSize();

        float margin = 20;

        /* -------- HEADER -------- */

        Canvas headerCanvas = new Canvas(
                new PdfCanvas(page),
                new Rectangle(
                        margin,
                        pageSize.getTop() - 60,
                        pageSize.getWidth() - (margin * 2),
                        50
                )
        );

        headerCanvas.add(
                new Paragraph(header)
                        .setFont(font)
                        .setFontSize(14)
                        .setTextAlignment(TextAlignment.CENTER)
        );

        headerCanvas.close();

        /* -------- FOOTER -------- */

        Canvas footerCanvas = new Canvas(
                new PdfCanvas(page),
                new Rectangle(
                        margin,
                        10,
                        pageSize.getWidth() - (margin * 2),
                        50
                )
        );

        footerCanvas.add(
                new Paragraph(footer)
                        .setFont(font)
                        .setFontSize(14)
                        .setTextAlignment(TextAlignment.CENTER)
        );

        footerCanvas.close();
    }
    
}
