package com.crmportal.utility;

import java.util.HashMap;
import java.util.Map;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;

public class BackgroundEventHandler extends AbstractPdfDocumentEventHandler {

    private Map<Integer, ImageData> pageBackgrounds = new HashMap<>();
    private ImageData defaultBackground;

    // ── NEW: active background (applies from a page onwards until cleared) ──
    private ImageData activeBackground     = null;
    private int       activeBackgroundFrom = -1;

    public BackgroundEventHandler() {
    }

    public void setDefaultBackground(ImageData imageData) {
        this.defaultBackground = imageData;
    }

    public void setPageBackground(int pageNumber, ImageData imageData) {
        pageBackgrounds.put(pageNumber, imageData);
    }

    // ── NEW methods ─────────────────────────────────────────────────────────

    /**
     * Set a background that applies from the given page number onwards,
     * overriding the default, until clearActiveBackground() is called.
     * Used for multi-page category content in type11 report.
     */
    public void setActiveBackground(int fromPage, ImageData imageData) {
        this.activeBackground     = imageData;
        this.activeBackgroundFrom = fromPage;
    }

    /**
     * Stop using the active background — revert to default.
     * Call this before starting each new category.
     */
    public void clearActiveBackground() {
        this.activeBackground     = null;
        this.activeBackgroundFrom = -1;
    }

    // ────────────────────────────────────────────────────────────────────────

    @Override
    protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        int pageNum = pdfDoc.getPageNumber(page);

        ImageData bgImage;

        // Priority 1: specific page override (existing behavior — unchanged)
        if (pageBackgrounds.containsKey(pageNum)) {
            bgImage = pageBackgrounds.get(pageNum);
        }
        // Priority 2: active range background (NEW — for multi-page categories)
        else if (activeBackground != null && pageNum >= activeBackgroundFrom) {
            bgImage = activeBackground;
        }
        // Priority 3: default background (existing behavior — unchanged)
        else {
            bgImage = defaultBackground;
        }

        if (bgImage != null) {
            PdfCanvas canvas = new PdfCanvas(
                    page.newContentStreamBefore(),
                    page.getResources(), pdfDoc);
            Rectangle pageSize = page.getPageSize();
            canvas.addImageFittedIntoRectangle(bgImage, pageSize, false);
            canvas.release();
        }
    }
}