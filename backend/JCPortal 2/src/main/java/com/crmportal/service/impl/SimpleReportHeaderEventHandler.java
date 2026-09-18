package com.crmportal.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.crmportal.response.dto.MenuQuantityReponseDto;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

public class SimpleReportHeaderEventHandler extends AbstractPdfDocumentEventHandler {

    private final MenuQuantityReponseDto eventData;
    private final ImageData logo;
    private final int isUserLogo;
    private final int isUserDetails;
    private final PdfFont font;
    private final PdfFont boldFont;
    private final float headerHeight;
    private final int lang;
    private String cnm = "Customer Name";
    private String mno = "Mobile No.";
    private String enm = "Event Name";
    private String edt = "Event Date";
    private String venue = "Venus";
    
	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

    public SimpleReportHeaderEventHandler(
            MenuQuantityReponseDto eventData,
            ImageData logo,
            int isUserLogo,
            int isUserDetails,
            PdfFont font,
            PdfFont boldFont,
            float headerHeight) {

        this.eventData = eventData;
        this.logo = logo;
        this.isUserLogo = isUserLogo;
        this.isUserDetails = isUserDetails;
        this.font = font;
        this.boldFont = boldFont;
        this.headerHeight = headerHeight;
		this.lang = 0;
    }
    
    public SimpleReportHeaderEventHandler(
            MenuQuantityReponseDto eventData,
            ImageData logo,
            int isUserLogo,
            int isUserDetails,
            PdfFont font,
            PdfFont boldFont,
            float headerHeight, 
            int lang) {

        this.eventData = eventData;
        this.logo = logo;
        this.isUserLogo = isUserLogo;
        this.isUserDetails = isUserDetails;
        this.font = font;
        this.boldFont = boldFont;
        this.headerHeight = headerHeight;
        this.lang = lang;
    }
    
    public SimpleReportHeaderEventHandler(
            MenuQuantityReponseDto eventData,
            ImageData logo,
            int isUserLogo,
            int isUserDetails,
            PdfFont font,
            PdfFont boldFont,
            float headerHeight, 
            int lang, String cnm, String mno, String enm, String edt, String venue ) {

        this.eventData = eventData;
        this.logo = logo;
        this.isUserLogo = isUserLogo;
        this.isUserDetails = isUserDetails;
        this.font = font;
        this.boldFont = boldFont;
        this.headerHeight = headerHeight;
        this.lang = lang;
        this.cnm = cnm;
        this.mno = mno;
        this.enm = enm;
        this.edt = edt;
        this.venue = venue;
    }

    @Override
    protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {
    	
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdf = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        Rectangle pageSize = page.getPageSize();

        PdfCanvas canvas = new PdfCanvas(
                page.newContentStreamBefore(),
                page.getResources(),
                pdf
        );

        float startX = pageSize.getLeft() + 28;
        float startY = pageSize.getTop() - 40;

        /* ---------------- LOGO ---------------- */
        float logoWidth = 120f;  // Increased width
        float logoHeight = 70f;  // Height

        // ONLY ADD LOGO IF isUserLogo == 1 AND logo is not null
        if (isUserDetails == 1 && logo != null) {
            try {
                PdfImageXObject img = new PdfImageXObject(logo);
                canvas.addXObjectWithTransformationMatrix(
                        img,
                        logoWidth, 0,      // Width scaling
                        0, logoHeight,     // Height scaling
                        startX,
                        startY - logoHeight
                );
            } catch (Exception e) {
                System.err.println("Error adding logo: " + e.getMessage());
                // Continue without logo if there's an error
            }
        }

        float textX = startX + (isUserDetails == 1 ? 130 : 0);  // Adjusted for wider logo
        float textY = startY - 15;

        /* ---------------- COMPANY DETAILS ---------------- */
        if (isUserDetails == 1) {
            canvas.beginText();
            try {
				canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD), 18);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            canvas.moveText(textX, textY);
            canvas.showText(eventData.getCompanyName() != null ? eventData.getCompanyName() : "");
            canvas.endText();

            canvas.beginText();
            try {
				canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 13);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            canvas.moveText(textX, textY - 22);
            canvas.showText("Mobile No. : " + (eventData.getOfficeNo() != null ? eventData.getOfficeNo() : ""));
            canvas.endText();

            canvas.beginText();
            try {
				canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 13);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            canvas.moveText(textX, textY - 42);
            canvas.showText("Email : " + (eventData.getCompanyEmail() != null ? eventData.getCompanyEmail() : ""));
            canvas.endText();
        }

        /* ---------------- TITLE ---------------- */
        canvas.beginText();
        try {
			canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD), 18);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        canvas.moveText(pageSize.getWidth() / 2 - 80, startY - 95);
		canvas.showText("Manager Menu File");
        canvas.endText();

        /* ---------------- EVENT DETAILS BOX ---------------- */

        float boxTopY = startY - 115;
        float boxLeftX = pageSize.getLeft() + 20;
        float boxWidth = pageSize.getWidth() - 40;
        float boxHeight = 90;

        canvas.rectangle(
                boxLeftX,
                boxTopY - boxHeight,
                boxWidth,
                boxHeight
        );
        canvas.setLineWidth(1.2f);
        canvas.stroke();

        /* ---------------- EVENT DETAILS CONTENT ---------------- */

        float infoY = boxTopY - 26;
        float labelX = boxLeftX + 12;
        float valueX = labelX + 135;

        float rightLabelX = boxLeftX + boxWidth / 2 + 12;
        float rightValueX = rightLabelX + 135;

        drawLabelValue(canvas, cnm,
                eventData.getPartyName(),
                labelX, valueX, infoY);

        drawLabelValue(canvas, mno,
                eventData.getPartyMobile(),
                rightLabelX, rightValueX, infoY);

        drawLabelValue(canvas, enm,
                eventData.getEventName(),
                labelX, valueX, infoY - 24);

        drawLabelValue(canvas, edt,
                eventData.getEventDate(),
                rightLabelX, rightValueX, infoY - 24);

        drawLabelValue(canvas, venue,
                eventData.getVenueName(),
                labelX, valueX, infoY - 48);

        canvas.release();
    }

    /* -------- EVENT DETAILS FONT INCREASED ONLY -------- */
    private void drawLabelValue(PdfCanvas canvas,
                                String label,
                                String value,
                                float labelX,
                                float valueX,
                                float y) {
    	
    	if (canvas == null || font == null || boldFont == null) {
            return;
        }
    	
        canvas.beginText();
        canvas.setFontAndSize(boldFont, 15);
        canvas.moveText(labelX, y);
        canvas.showText(label + " : ");
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(font, 15);
        canvas.moveText(valueX, y);
        canvas.showText(value != null ? value : "");
        canvas.endText();
    }
}