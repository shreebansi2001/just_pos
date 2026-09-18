package com.crmportal.service.impl;

import java.io.IOException;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

public class RawMaterialHeaderEventHandler extends AbstractPdfDocumentEventHandler {

    private final PdfFont font;
    private final PdfFont fontBold;
    private final ImageData logo;
    private final String clientName;
    private final String mobileNo;
    private final String email;

    public RawMaterialHeaderEventHandler(
            PdfFont font,
            PdfFont fontBold,
            ImageData logo,
            String clientName,
            String mobileNo,
            String email) {

        this.font = font;
        this.fontBold = fontBold;
        this.logo = logo;
        this.clientName = clientName;
        this.mobileNo = mobileNo;
        this.email = email;
    }

    @Override
    protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {

        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfPage page = docEvent.getPage();
        Rectangle pageSize = page.getPageSize();

        PdfCanvas canvas = new PdfCanvas(
                page.newContentStreamBefore(),
                page.getResources(),
                docEvent.getDocument()
        );

        float topY = pageSize.getTop() - 35;
        float logoSize = 75f;

        float logoX = 40;
        float logoY = topY - logoSize;

        PdfImageXObject img = new PdfImageXObject(logo);

        canvas.addXObjectWithTransformationMatrix(
                img,
                logoSize, 0,
                0, logoSize,
                logoX,
                logoY
        );

        float logoCenterY = logoY + (logoSize / 2);

        /* ---------- CENTER TEXT ---------- */
        float textStartX = pageSize.getWidth() / 2 - 140;

        // clientName (already bold)
        canvas.beginText();
        try {
			canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD), 18);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        canvas.moveText(textStartX, logoCenterY + 6);
        canvas.showText(clientName);
        canvas.endText();

        // mobileNo (LABEL BOLD, VALUE NORMAL)
        canvas.beginText();
        canvas.setFontAndSize(fontBold, 13);
        canvas.moveText(textStartX, logoCenterY - 15);
        canvas.showText("Mobile No : ");
        canvas.setFontAndSize(font, 11);
        canvas.showText(mobileNo);
        canvas.endText();

        /* ---------- RIGHT DATE ---------- */
        
        canvas.beginText();
        canvas.setFontAndSize(fontBold, 13);
        canvas.moveText(textStartX, logoCenterY - 30);
        canvas.showText("Email : ");
        canvas.setFontAndSize(font, 11);
        canvas.showText(email);
        canvas.endText();
        
        canvas.release();
    }
}
