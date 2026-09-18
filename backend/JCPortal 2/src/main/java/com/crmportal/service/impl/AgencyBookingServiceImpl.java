package com.crmportal.service.impl;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EventRawMaterialRepository;
import com.crmportal.service.AgencyBookingService;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

@Service
public class AgencyBookingServiceImpl implements AgencyBookingService {

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;
	
	@Autowired
	EventMasterRepository eventMasterRepository;
	
	@Autowired
	Environment environment;
	
	@Autowired
	EventRawMaterialRepository eventRawMaterialRepository;
	
	@Override
	public String generateAgencyBookingReport(Long eventId, Long userId, HttpServletRequest request, Integer lang) {
		try {

			menuPreparationServiceImpl.loadLicense();

			PdfFont basicFont = null;
			PdfFont boldFont = null;
			PdfFont arialBold = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");
			
			if (lang == 1) {
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
			} else if (lang == 2) {
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
			} else {
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");
				System.out.println("English font loaded successfully");
			}

			EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
					.orElseThrow(() -> new RuntimeException("Event not found with id : " + eventId));

			String rootPath = request.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/" + event.getEventNo() + "/");

			if (!outputPath.exists()) {
				outputPath.mkdirs();
			}

			File pdfFile = new File(outputPath, "agencybooking_report.pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, PageSize.A4);

			document.setMargins(20, 20, 20, 20);

			Paragraph title = new Paragraph("Agency Booking Report").setFont(arialBold).setFontSize(24).simulateBold()
					.setUnderline().setTextAlignment(TextAlignment.CENTER).setMarginBottom(20);

			document.add(title);

			float[] widths = { 40f, 30f, 30f};

			Table table = new Table(UnitValue.createPercentArray(widths));
			table.setWidth(UnitValue.createPercentValue(100));

			String itemLabel = "";
			String orderLabel = "";
			String mobileNoLabel = "";

			if (lang == 1) {
			    itemLabel = "वस्तु";
			    orderLabel = "ऑर्डर";
			    mobileNoLabel = "मोबाइल नंबर";
			} else if (lang == 2) {
			    itemLabel = "વસ્તુ";
			    orderLabel = "ઓર્ડર";
			    mobileNoLabel = "મોબાઇલ નંબર";
			} else {
			    itemLabel = "Item";
			    orderLabel = "Order";
			    mobileNoLabel = "Mobile No.";
			}
			
			String[] headers = { itemLabel, orderLabel, mobileNoLabel };

			for (String header : headers) {

				Cell cell = new Cell().add(new Paragraph(header).setFont(basicFont).simulateBold().setFontSize(11))
						.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setPadding(4);

				table.addHeaderCell(cell);
			}

			List<String> data = eventRawMaterialRepository.getAgencyBookingData(userId, lang);
			
			for (String st : data) {
				table.addCell(
						new Cell().add(new Paragraph(st.toUpperCase()).setFont(basicFont)).setTextAlignment(TextAlignment.LEFT)
								.setVerticalAlignment(VerticalAlignment.MIDDLE).setHeight(24).setPaddingLeft(3f));

				table.addCell(new Cell().add(new Paragraph("")).setHeight(24));

				table.addCell(new Cell().add(new Paragraph("")).setHeight(24));
			}

			document.add(table);

			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/pdf/" + event.getEventNo()
					+ "/agencybooking_report.pdf";

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Attendance Report", e);
		}
	}
}
