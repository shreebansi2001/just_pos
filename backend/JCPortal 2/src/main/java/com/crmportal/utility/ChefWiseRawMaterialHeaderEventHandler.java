package com.crmportal.utility;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.renderer.IRenderer;

public class ChefWiseRawMaterialHeaderEventHandler extends AbstractPdfDocumentEventHandler {

	private final Table headerTable;
	private final float topMargin;
	
    public ChefWiseRawMaterialHeaderEventHandler(Table table, float topMargin) {
    	this.headerTable = table;
    	this.topMargin = topMargin;
    }

    @Override
    protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {

        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfPage page = docEvent.getPage();
        Rectangle pageSize = page.getPageSize();
        int pageNumber = docEvent.getDocument().getPageNumber(page);

        PdfCanvas pdfCanvas = new PdfCanvas(
                page.newContentStreamBefore(),
                page.getResources(),
                docEvent.getDocument()
        );

        float width = page.getPageSize().getWidth();
        float height = page.getPageSize().getHeight();
        
        Canvas canvas = new Canvas(pdfCanvas, pageSize);

        IRenderer renderer = headerTable.createRendererSubTree();
        renderer.setParent(new com.itextpdf.layout.Document(docEvent.getDocument()).getRenderer());
        
        LayoutResult result = renderer.layout(
            new LayoutContext(
                new LayoutArea(0, new Rectangle(width - 80, 1000))
            )
        );
        
        float tableHeight = result.getOccupiedArea().getBBox().getHeight();
        
        // Position from top
        float topPosition = height - topMargin - tableHeight;
        
        headerTable.setFixedPosition(
                pageNumber,
                40,  // left margin
                topPosition,
                width - 80  // width (page width - margins)
        );
        
        canvas.add(headerTable);
        canvas.close();
    }

}
