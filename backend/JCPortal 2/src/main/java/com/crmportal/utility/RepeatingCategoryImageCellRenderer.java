package com.crmportal.utility;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;

public class RepeatingCategoryImageCellRenderer extends CellRenderer {

	private ImageData imageData;

	public RepeatingCategoryImageCellRenderer(Cell modelElement, ImageData imageData) {

		super(modelElement);
		this.imageData = imageData;
	}

	@Override
	public IRenderer getNextRenderer() {

		return new RepeatingCategoryImageCellRenderer((Cell) modelElement, imageData);
	}

	@Override
	public void draw(DrawContext drawContext) {

		/*
		 * First let iText calculate and draw the normal cell.
		 */
		super.draw(drawContext);

		if (imageData == null) {
			return;
		}

		/*
		 * Get the ACTUAL cell area on the current page.
		 *
		 * This is the important difference from the previous solution.
		 *
		 * We do NOT use fixedPosition().
		 */
		Rectangle cellArea = getOccupiedAreaBBox();

		if (cellArea == null) {
			return;
		}

		/*
		 * Add some spacing inside the image column.
		 */
		float padding = 10f;

		float availableWidth = cellArea.getWidth() - (padding * 2);

		float availableHeight = cellArea.getHeight() - (padding * 2);

		if (availableWidth <= 0 || availableHeight <= 0) {
			return;
		}

		/*
		 * Keep the original image ratio.
		 *
		 * Your current image is approximately:
		 *
		 * 700 x 800
		 */
		float originalWidth = 700f;
		float originalHeight = 800f;

		float ratio = originalWidth / originalHeight;

		float imageWidth = availableWidth;

		float imageHeight = imageWidth / ratio;

		/*
		 * If height is too large, fit by height.
		 */
		if (imageHeight > availableHeight) {

			imageHeight = availableHeight;

			imageWidth = imageHeight * ratio;
		}

		/*
		 * Center image horizontally inside the cell.
		 */
		float x = cellArea.getLeft() + (cellArea.getWidth() - imageWidth) / 2f;

		/*
		 * Put image at TOP of this cell fragment.
		 *
		 * When iText creates another fragment on the next page, this renderer is called
		 * again and the image is drawn again.
		 */
		float y = cellArea.getTop() - padding - imageHeight;

		Rectangle imageRectangle = new Rectangle(x, y, imageWidth, imageHeight);

		PdfCanvas canvas = drawContext.getCanvas();

		/*
		 * Draw the image INSIDE the cell.
		 */
		canvas.addImageFittedIntoRectangle(imageData, imageRectangle, false);

		/*
		 * Image border.
		 */
		canvas.saveState();

		canvas.rectangle(imageRectangle);

		canvas.setLineWidth(1f);

		canvas.stroke();

		canvas.restoreState();
	}
}