package com.crmportal.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;

public class ImageWatermarkEventHandler extends AbstractPdfDocumentEventHandler {

	private final ImageData watermarkImage;

	public ImageData loadImageFromResource(String resourcePath) throws IOException {
		InputStream inputStream;

		// Case 1: Remote URL
		if (resourcePath.startsWith("http://") || resourcePath.startsWith("https://")) {
			URL url = new URL(resourcePath);
			inputStream = url.openStream();

			// Case 2: Classpath resource
		} else {
			inputStream = getClass().getResourceAsStream(resourcePath);

			// Try file system as fallback
			if (inputStream == null) {
				File file = new File(resourcePath);
				if (file.exists()) {
					inputStream = new FileInputStream(file);
				}
			}
		}

		if (inputStream == null) {
			throw new FileNotFoundException("Image not found: " + resourcePath);
		}

		try (InputStream is = inputStream) {
			return ImageDataFactory.create(IOUtils.toByteArray(is));
		}
	}

	public ImageWatermarkEventHandler(String imagePath) throws IOException {
		this.watermarkImage = loadImageFromResource(imagePath);
	}

	@Override
	protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {

		PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
		PdfDocument pdfDoc = docEvent.getDocument();
		PdfPage page = docEvent.getPage();

		Rectangle pageSize = page.getPageSize();

		PdfCanvas canvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDoc);

		PdfExtGState gs = new PdfExtGState();
		gs.setFillOpacity(0.10f); // watermark transparency

		canvas.saveState();
		canvas.setExtGState(gs);

		float imgWidth = 420;
		float imgHeight = 420;

		float x = (pageSize.getWidth() - imgWidth) / 2;
		float y = (pageSize.getHeight() - imgHeight) / 2;

		canvas.addImageFittedIntoRectangle(watermarkImage, new Rectangle(x, y, imgWidth, imgHeight), false);

		canvas.restoreState();
	}
}
