package com.crmportal.utility;

import com.itextpdf.commons.actions.IEventHandler;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.properties.TextAlignment;

public class PageNumberHandler extends AbstractPdfDocumentEventHandler {

	private final PdfFormXObject totalPagesPlaceholder;

	public PageNumberHandler() {
		this.totalPagesPlaceholder = new PdfFormXObject(new Rectangle(50, 30));
	}

	// ✅ THIS is what your IDE is asking for
	public PdfFormXObject getTotalPagesPlaceholder() {
		return totalPagesPlaceholder;
	}

	@Override
	protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {

	    PdfDocumentEvent docEvent = (PdfDocumentEvent) event;

	    PdfDocument pdf = docEvent.getDocument();
	    PdfPage page = docEvent.getPage();

	    int pageNumber = pdf.getPageNumber(page);
	    Rectangle pageSize = page.getPageSize();

	    PdfCanvas pdfCanvas = new PdfCanvas(
	            page.newContentStreamAfter(),
	            page.getResources(),
	            pdf);

	    Canvas canvas = new Canvas(pdfCanvas, pageSize);

	    float width = pageSize.getWidth();
	    float footerY = pageSize.getBottom() + 25;

	    // ==========================================
	    // A6 PAGE
	    // ==========================================
	    if (width < 350) {

	        // Left Signature
	        canvas.showTextAligned(
	                "Prepared By __________",
	                width * 0.02f,
	                footerY,
	                TextAlignment.LEFT);

	        // Right Signature
	        canvas.showTextAligned(
	                "Submitted By __________",
	                width * 0.98f,
	                footerY,
	                TextAlignment.RIGHT);

	        // Page Number (Second Line)
	        float centerX = width / 2;

	        canvas.showTextAligned(
	                "Page " + pageNumber + " of ",
	                centerX - 5,
	                footerY - 15,
	                TextAlignment.RIGHT);

	        pdfCanvas.addXObjectAt(
	                totalPagesPlaceholder,
	                centerX,
	                footerY - 20);
	    }

	    // ==========================================
	    // A4 / A5 PAGE
	    // ==========================================
	    else {

	        float centerX = width / 2;

	        // Left Signature
	        canvas.showTextAligned(
	                "Prepared By __________",
	                width * 0.02f,
	                footerY,
	                TextAlignment.LEFT);

	        // Page Number
	        canvas.showTextAligned(
	                "Page " + pageNumber + " of ",
	                centerX - 5,
	                footerY,
	                TextAlignment.RIGHT);

	        pdfCanvas.addXObjectAt(
	                totalPagesPlaceholder,
	                centerX,
	                footerY - 5);

	        // Right Signature
	        canvas.showTextAligned(
	                "Submitted By __________",
	                width * 0.98f,
	                footerY,
	                TextAlignment.RIGHT);
	    }

	    canvas.close();
	}
}
