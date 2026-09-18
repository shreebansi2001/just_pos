package com.crmportal.utility;

import com.itextpdf.commons.actions.IEventHandler;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;

public class PageBorderEventHandler extends AbstractPdfDocumentEventHandler {

    private float borderWidth;
    private Color borderColor;
    private float marginLeft;
    private float marginRight;
    private float marginTop;
    private float marginBottom;

    /**
     * Default constructor with standard border
     */
    public PageBorderEventHandler() {
        this.borderWidth = 2f;  // Increased from 1.5f to make it more visible
        this.borderColor = new DeviceRgb(0, 0, 0);  // Explicit black color
        this.marginLeft = 10f;
        this.marginRight = 10f;
        this.marginTop = 10f;
        this.marginBottom = 20f;
    }
    
    public PageBorderEventHandler(float borderWidth, Color borderColor, float margin) {
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
        this.marginLeft = margin;
        this.marginRight = margin;
        this.marginTop = margin;
        this.marginBottom = margin;
    }

    public PageBorderEventHandler(float borderWidth, Color borderColor, float marginLeft, float marginRight, 
            float marginTop, float marginBottom) {
		this.borderWidth = borderWidth;
		this.borderColor = borderColor;
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.marginTop = marginTop;
		this.marginBottom = marginBottom;
}
    
    /**
     * Constructor with custom border width and color
     * @param borderWidth Width of the border line
     * @param borderColor Color of the border
     * @param margin Distance from page edge
     */

    @Override
    public void onAcceptedEvent(AbstractPdfDocumentEvent event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();

        Rectangle pageSize = page.getPageSize();
        
        // Use newContentStreamAfter instead of Before to draw on top
        PdfCanvas canvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDoc);

        // Calculate border rectangle
        float x = marginLeft;
        float y = marginBottom;
        float width = pageSize.getWidth() - marginLeft - marginRight;
        float height = pageSize.getHeight() - marginTop - marginBottom;

        // Draw the border with explicit settings
        canvas.saveState();
        canvas.setStrokeColor(borderColor);
        canvas.setLineWidth(borderWidth);
        canvas.rectangle(x, y, width, height);
        canvas.stroke();
        canvas.restoreState();
        
        canvas.release();
    }
}