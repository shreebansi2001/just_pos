package com.crmportal.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.IRunBody;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPBdr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.DecoreMainCategoryItemImagesMasterEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.EventTermsAndConditionEntity;
import com.crmportal.entity.EventTermsAndConditionFeaturesEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.BanquetHallShiftBookingRepository;
import com.crmportal.repository.DecoreMainCategoryItemImagesMasterRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EventTermsAndConditionFeaturesRepository;
import com.crmportal.repository.EventTermsAndConditionRepository;
import com.crmportal.repository.MenuPreparationDetailsRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.AdminTemplateModuleRequestDTO;
import com.crmportal.request.dto.ReportMenuPlanningRequestDTO;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;
import com.crmportal.response.dto.BanquetHallShiftInfoDto;
import com.crmportal.response.dto.CostingReportResponseDto;
import com.crmportal.response.dto.DecoreItemReportResponseDto;
import com.crmportal.response.dto.DecoreReportResponseDto;
import com.crmportal.response.dto.EventAdvancePaymentResponseDto;
import com.crmportal.response.dto.EventFunctionRawMaterialPermissionResponseDto;
import com.crmportal.response.dto.EventFunctionReportResponseDto;
import com.crmportal.response.dto.EventReportResponseDto;
import com.crmportal.response.dto.ExtraChargesResponseDto;
import com.crmportal.response.dto.GetEventLaborResponseDto;
import com.crmportal.response.dto.MenuItemForReportResponseDto;
import com.crmportal.response.dto.MenuQuantityReponseDto;
import com.crmportal.response.dto.MenuReportResponseDto;
import com.crmportal.response.dto.OrderSummaryResponseDto;
import com.crmportal.response.dto.RemaingDataResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventAdvancePaymentService;
import com.crmportal.service.ExtraChargesService;
import com.crmportal.service.FollowUpReminderScheduler;
import com.crmportal.service.SimpleReportService;
import com.crmportal.utility.AdobeDocxGenerator;
import com.crmportal.utility.BackgroundEventHandler;
import com.crmportal.utility.PageBorderEventHandler;
import com.crmportal.utility.PageNumberHandler;
import com.crmportal.utility.ShortMenuHeaderEventHandler;
import com.itextpdf.commons.actions.IEventHandler;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DoubleBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.splitting.DefaultSplitCharacters;
import com.itextpdf.layout.splitting.ISplitCharacters;

@Service
public class SimpleReportServiceImpl implements SimpleReportService {

	private final FollowUpReminderScheduler followUpReminderScheduler;

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	MenuItemRawMaterialServiceImpl menuItemRawMaterialServiceImpl;

	@Autowired
	MenuPreparationDetailsRepository menuPreparationDetailsRepository;

	@Autowired
	EventLaborServiceImpl eventLaborServiceImpl;

	@Autowired
	Environment environment;

	@Autowired
	CommonService commonService;

	@Value("${spring.profiles.active}")
	private String activeProfile;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	UserBasicDetailsMasterRepository userBasicDetailsMasterRepository;

	@Autowired
	AdobeDocxGenerator adobeDocxGenerator;

	@Autowired
	EventTermsAndConditionRepository eventTermsAndConditionRepository;

	@Autowired
	EventTermsAndConditionFeaturesRepository eventTermsAndConditionFeaturesRepository;

	@Autowired
	BanquetHallShiftBookingRepository banquetHallShiftBookingRepository;

	@Autowired
	ExtraChargesService extraChargesService;
	
	@Autowired
	EventAdvancePaymentService eventAdvancePaymentService;
	
	@Autowired
	DecoreMainCategoryItemImagesMasterRepository decoreMainCategoryItemImagesMasterRepository;

	SimpleReportServiceImpl(FollowUpReminderScheduler followUpReminderScheduler) {
		this.followUpReminderScheduler = followUpReminderScheduler;
	}

	private String safeText(String value) {
		return value == null ? "" : value;
	}

	private static String fileSafe(String data) {
		return data == null ? "" : data.replaceAll("[^\\p{L}\\p{N}_]", "_");
	}

	public String getReportName(String name, String date, String type) {
		return fileSafe(name) + "-" + fileSafe(date.replace("/", "_") + " (" + type.toUpperCase() + ")");
	}

	public String formatDate(LocalDateTime dateTime) {
		return dateTime == null ? "" : dateTime.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
	}

	public String formatDate(String date) {
		DateTimeFormatter input = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		DateTimeFormatter output = DateTimeFormatter.ofPattern("dd_MM_yyyy");
		if (date == null || date.isEmpty()) {
			return "";
		}
		LocalDate localDate = LocalDate.parse(date, input);
		return localDate.format(output);
	}

	private String getPartyNameByEventId(Long eventId) {
		String party = eventMasterRepository.getPartyByEventId(eventId)
				.orElseThrow(() -> new RuntimeException("Party Details Not Found."));
		return party;
	}

	// =====================================================================================
	// FIXED VERSION of getMenuPlanningSimpleReport1
	//
	// ROOT CAUSE OF THE BORDER BUG:
	// The table has 6 columns. Several "rows" only ever add 3 cells (a
	// label/colon/value
	// triple) when an optional field (billingName, service, theme, remarks...) is
	// absent,
	// and border-top was applied per-triple based on that field's own condition
	// instead
	// of per-row. Because iText auto-wraps cells into rows purely by column count,
	// a
	// missing triple causes the NEXT logical row's cells to slide into the same
	// visual
	// row -> half a row gets a border-top and the other half doesn't (or worse, two
	// unrelated rows get merged onto one line).
	//
	// FIX:
	// - Every logical row is now built through addPair()/addFullWidthPair(), which
//	     ALWAYS emits a complete 6-cell row (padding with invisible NO_BORDER cells when
//	     the right-hand pair is absent).
	// - The border for a row is decided ONCE, based on "is this the first row of a
//	     bordered block", and passed in explicitly - never inferred per-field.
	// =====================================================================================

	@Override
	public String getMenuPlanningSimpleReport1(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails,
			Integer isTermsCondition, Integer isExtraCharges) {
		try {
			PdfFont basicFont = null;
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "", l1 = "",
					l2 = "", note = "", eContact = "", eventFlow = "", function = "", person = "", eTime = "",
					date = "", rate = "", party = "", eventNotes = "", billingNameLabel = "", serviceLabel = "",
					referenceLabel = "", cordinatorPersonLabel = "", themeLabel = "", pkgPrice = "",
					permissableLabel = "", notPermissableLabel = "";
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			if (lang == 1) {
				// Hindi
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				customerName = "नाम";
				customerPhone = "मोबाइल नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "दिनांक";
				fNotes = "नोट्स";
				eVenue = "आयोजन स्थान";
				function = "कार्यक्रम";
				person = "मेम्बर्स";
				eTime = "समय";
				eContact = "संपर्क नंबर";
				l1 = "व्यक्तियों की संख्या:";
				eventFlow = "कार्यक्रम का संचालन क्रम";
				note = "नोट";
				date = "दिनांक";
				rate = "रेट";
				party = "पार्टी का नाम";
				eventNotes = "नोट्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				referenceLabel = "संदर्भ";
				cordinatorPersonLabel = "समन्वयक व्यक्ति";
				pkgPrice = "पैकेज मूल्य";
				permissableLabel = "अनुमत्य सामग्री";
				notPermissableLabel = "अस्वीकृत सामग्री";
				y = 512;
			} else if (lang == 2) {
				// Gujarati / regional variants
				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Regular.ttf");
					customerName = "வாடிக்கையாளர் பெயர்";
					customerPhone = "மொபைல் எண்";
					eName = "நிகழ்ச்சி பெயர்";
					eDate = "நிகழ்ச்சி தேதி";
					fNotes = "உணவு விவரம்";
					eVenue = "நிகழ்வு இடம்";
					function = "நிகழ்ச்சி";
					person = "நபர்";
					eTime = "நேரம்";
					eContact = "தொடர்பு எண்";
					l1 = "நபர்களின் எண்ணிக்கை:";
					eventFlow = "நிகழ்ச்சி நடைபெறும் வரிசை";
					note = "குறிப்பு";
					date = "தேதி";
					rate = "விலை";
					party = "கட்சியின் பெயர்";
					eventNotes = "நிகழ்வு குறிப்புகள்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					referenceLabel = "குறிப்பு";
					cordinatorPersonLabel = "ஒருங்கிணைப்பாளர்";
					pkgPrice = "தொகுப்பு விலை";
					permissableLabel = "அனுமதிக்கப்பட்ட பொருட்கள்";
					notPermissableLabel = "அனுமதிக்கப்படாத பொருட்கள்";
				} else if (language.equalsIgnoreCase("Telugu")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					customerName = "కస్టమర్ పేరు";
					customerPhone = "మొబైల్ నంబర్";
					eName = "కార్యక్రమ పేరు";
					eDate = "కార్యక్రమ తేదీ";
					fNotes = "భోజన వివరాలు";
					eVenue = "కార్యక్రమ స్థలం";
					function = "కార్యక్రమం";
					person = "వ్యక్తి";
					eTime = "సమయం";
					eContact = "సంప్రదింపు నంబర్";
					l1 = "వ్యక్తుల సంఖ్య:";
					eventFlow = "కార్యక్రమ నిర్వహణ క్రమం";
					note = "గమనిక";
					date = "తేదీ";
					rate = "రేటు";
					party = "పార్టీ పేరు";
					eventNotes = "ఈవెంట్ గమనికలు";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					referenceLabel = "సూచన";
					cordinatorPersonLabel = "సమన్వయకర్త";
					pkgPrice = "ప్యాకేజ్ ధర";
					permissableLabel = "అనుమతించదగిన వస్తువులు";
					notPermissableLabel = "అనుమతించని వస్తువులు";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					customerName = "ഉപഭോക്താവിന്റെ പേര്";
					customerPhone = "മൊബൈൽ നമ്പർ";
					eName = "പരിപാടിയുടെ പേര്";
					eDate = "പരിപാടിയുടെ തീയതി";
					fNotes = "ഭക്ഷണ വിശദാംശങ്ങൾ";
					eVenue = "പരിപാടി സ്ഥലം";
					function = "പരിപാടി";
					person = "വ്യക്തി";
					eTime = "സമയം";
					eContact = "ബന്ധപ്പെടാനുള്ള നമ്പർ";
					l1 = "വ്യക്തികളുടെ എണ്ണം:";
					eventFlow = "പരിപാടി നടത്തിപ്പ് ക്രമം";
					note = "കുറിപ്പ്";
					date = "തീയതി";
					rate = "നിരക്ക്";
					party = "പാർട്ടി പേര്";
					eventNotes = "ഇവന്റ് കുറിപ്പുകൾ";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					referenceLabel = "റഫറൻസ്";
					cordinatorPersonLabel = "കോഓർഡിനേറ്റർ";
					pkgPrice = "പാക്കേജ് വില";
					permissableLabel = "അനുവദനീയമായ ഇനങ്ങൾ";
					notPermissableLabel = "അനുവദനീയമല്ലാത്ത ഇനങ്ങൾ";
				} else if (language.equalsIgnoreCase("Marathi")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
					customerName = "ग्राहकाचे नाव";
					customerPhone = "मोबाईल नंबर";
					eName = "कार्यक्रमाचे नाव";
					eDate = "कार्यक्रमाची दिनांक";
					fNotes = "भोजन तपशील";
					eVenue = "आयोजन स्थळ";
					function = "कार्यक्रम";
					person = "व्यक्ती";
					eTime = "वेळ";
					eContact = "संपर्क नंबर";
					l1 = "व्यक्तींची संख्या:";
					eventFlow = "कार्यक्रमाचा संचालन क्रम";
					note = "टीप";
					date = "दिनांक";
					rate = "रेट";
					party = "पार्टीचे नाव";
					eventNotes = "कार्यक्रम नोंदी";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					referenceLabel = "संदर्भ";
					cordinatorPersonLabel = "समन्वयक व्यक्ती";
					pkgPrice = "पॅकेज किंमत";
					permissableLabel = "अनुमत्य वस्तू";
					notPermissableLabel = "अस्वीकृत वस्तू";
				} else {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					customerName = "ગ્રાહકનું નામ";
					customerPhone = "મોબાઇલ નંબર";
					eName = "કાર્યક્રમનું નામ";
					eDate = "કાર્યક્રમની તારીખ";
					fNotes = "ભોજન વિગતો";
					eVenue = "આયોજન સ્થળ";
					function = "કાર્યક્રમ";
					person = "વ્યક્તિ";
					eTime = "સમય";
					eContact = "સંપર્ક નંબર";
					eventFlow = "કાર્યક્રમનું સંચાલન ક્રમ";
					l1 = "વ્યક્તિઓની સંખ્યા:";
					note = "નોંધ";
					date = "તારીખ";
					rate = "રેટ";
					party = "પાર્ટીનું નામ";
					eventNotes = "કાર્યક્રમની નોંધ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					referenceLabel = "રેફરન્સ";
					cordinatorPersonLabel = "સંકલનકર્તા";
					pkgPrice = "પેકેજ કિંમત";
					permissableLabel = "માન્ય સામગ્રી";
					notPermissableLabel = "અમાન્ય સામગ્રી";
				}
				y = 510;
			} else {
				// English
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				customerName = "Customer Name";
				customerPhone = "Mobile No.";
				eName = "Event Name";
				eDate = "Event Date";
				fNotes = "Food Note";
				eVenue = "Venue";
				function = "Function";
				person = "Persons";
				eTime = "TIMING";
				eContact = "CONTACT NO";
				eventFlow = "FLOW OF EVENT";
				l1 = "OF PERSONS:";
				note = "Note";
				date = "Date";
				rate = "Rate";
				party = "PARTY NAME";
				eventNotes = "Event Notes";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";
				referenceLabel = "Reference";
				cordinatorPersonLabel = "Coordinator Person";
				pkgPrice = "Package Price";
				permissableLabel = "Permissable Items";
				notPermissableLabel = "Not Permissable Items";
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);

			if (eventDto == null) {
				return "";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

			if (!outputPath.exists()) {
				outputPath.mkdirs();
			}
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "back office report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 30, 50, 30);

			ImageData mainBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
			ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			String watermarkPath = environment.getProperty("app.image.url") + eventDto.getLogo();

			if (isCompanyDetails == 1) {
				pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(watermarkPath));
			}

			Color redColor = new DeviceRgb(255, 0, 0);
			Color blackColor = new DeviceRgb(0, 0, 0);

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;
			String reference = eventDto.getReference() != null ? eventDto.getReference() : "";
			String cordinatoreName = eventDto.getCordinationPersonName() != null ? eventDto.getCordinationPersonName()
					: "";
			String cordinatoreNo = eventDto.getCordinationPersonContactno() != null
					? eventDto.getCordinationPersonContactno()
					: "";
			String venue;
			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}
			String foodNotesName = eventDto.getFoodType();
			String foodNotes;
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone() != null ? eventDto.getCmpPhone() : "";
			String cmpAddress = eventDto.getCmpAddress() != null ? eventDto.getCmpAddress() : "";
			String cmpEmail = eventDto.getEmail() != null ? eventDto.getEmail() : "";
			String remarks = eventDto.getRemark() != null ? eventDto.getRemark() : "";

			boolean isFirstFunction = true;
			Table eventTable;
			Table catItemTable;

			String billingName, service, theme;
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
			}

			// ---- border constants used consistently everywhere below ----
			final Border NONE = Border.NO_BORDER;
			final Border BLOCK_TOP = new DoubleBorder(3f); // top border of the whole party-details block
			final Border ROW_TOP = new SolidBorder(1f); // divider border between sections

			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				eventTable = new Table(UnitValue.createPercentArray(new float[] { 32f, 1.5f, 32f, 32f, 1.5f, 32f }));
				eventTable.setWidth(UnitValue.createPercentValue(100));
				eventTable.setMarginBottom(-2f);
				eventTable.setBorder(Border.NO_BORDER);
				eventTable.setBorderBottom(new SolidBorder(1f));
				eventTable.setMarginTop(10f);

				if (isFirstFunction) {
					if (isCompanyDetails == 1) {
						ImageData logoData = menuPreparationServiceImpl
								.loadImageFromResource(environment.getProperty("app.image.url") + eventDto.getLogo());
						Image logo = new Image(logoData);
						logo.setWidth(UnitValue.createPercentValue(100f));
						logo.setAutoScale(false);
						logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

						eventTable.addCell(new Cell(6, 1).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setBorder(NONE).setPadding(3f));

						eventTable.addCell(new Cell(1, 5)
								.add(new Paragraph(cmpName.toUpperCase()).setFont(basicFont).setFontSize(12)
										.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
								.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(NONE));

						eventTable.addCell(new Cell(1, 5)
								.add(new Paragraph(cmpAddress).setFont(basicFont).setFontSize(10)
										.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
								.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(NONE));

						eventTable.addCell(new Cell(1, 5)
								.add(new Paragraph().add(new Text("Mobile No : ").simulateBold())
										.add(new Text(cmpPhone)).setFont(basicFont).setFontSize(10)
										.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
								.setPaddingBottom(0).setPaddingTop(0).setBorder(NONE).setPaddingLeft(10f));

						eventTable.addCell(new Cell(1, 5)
								.add(new Paragraph().add(new Text("Email : ").simulateBold()).add(new Text(cmpEmail))
										.setFont(basicFont).setFontSize(10).setFontColor(blackColor)
										.setTextAlignment(TextAlignment.LEFT))
								.setPaddingBottom(0).setPaddingTop(0).setBorder(NONE).setPaddingLeft(10f));

						eventTable.addCell(new Cell(1, 5).setBorder(NONE).setHeight(5f));
						eventTable.addCell(new Cell(1, 5).setBorder(NONE).setHeight(5f));
						
					}

					// =========================================================
					// PARTY DETAILS BLOCK - every row is a complete 6-cell row.
					// Only the FIRST row of whichever block actually renders
					// gets BLOCK_TOP; every other row in the same block gets NONE.
					// =========================================================
					boolean hasBilling = billingName != null && billingName.trim().length() != 0;
					boolean partyBlockRendered = isPartyDetails == 1;

					if (partyBlockRendered) {
						// Row 1: Customer Name | Billing Name (optional)
						addPair(eventTable, basicFont, blackColor, customerName, hostName,
								hasBilling ? billingNameLabel : null, hasBilling ? billingName : null, BLOCK_TOP);

						// Row 2: Mobile No | (nothing — kept as its own row, no border, interior row)
						addPair(eventTable, basicFont, blackColor, customerPhone, mobileNo, null, null, NONE);
					}

					// Row: Event Name | Event Date
					// Gets BLOCK_TOP only if the party-details block above didn't render
					// (i.e. this is now the first row of the block).
					addPair(eventTable, basicFont, blackColor, eName, eventName.toUpperCase(), eDate, eventDate,
							partyBlockRendered ? NONE : BLOCK_TOP);

					// Row: Food Note (label + value spans 4 cols, red, bold) — always interior, no
					// border
					String foodNoteValue = foodNotes.trim().isEmpty() ? foodNotesName
							: foodNotesName + " (" + foodNotes + ")";
					addFullWidthPair(eventTable, basicFont, fNotes, foodNoteValue, redColor, blackColor, NONE, true);

					// Row: Venue (spans 4 cols) — always interior, no border
					addFullWidthPair(eventTable, basicFont, eVenue, venue.toUpperCase(), blackColor, blackColor, NONE, true);

					// Row: Service (optional, own row)
					if (service != null && service.trim().length() != 0) {
						addPair(eventTable, basicFont, blackColor, serviceLabel, service, null, null, NONE);
					}

					// Row: Theme (optional, own row)
					if (theme != null && theme.trim().length() != 0) {
						addPair(eventTable, basicFont, blackColor, themeLabel, theme, null, null, NONE);
					}

					// Row: Remarks / Event Notes (optional, spans 4 cols)
					if (remarks != null && remarks.trim().length() != 0) {
						addFullWidthPair(eventTable, basicFont, eventNotes, remarks, blackColor, blackColor, NONE, true);
					}

					if (reference != null && reference.trim().length() != 0) {
						addFullWidthPair(eventTable, basicFont, referenceLabel, reference, blackColor, blackColor,
								NONE, true);
					}

					String cordName = cordinatoreName == null ? "" : cordinatoreName.trim();
					String cordNo = cordinatoreNo == null ? "" : cordinatoreNo.trim();

					if (!cordName.isEmpty() || !cordNo.isEmpty()) {
						String full = cordName.isEmpty() ? cordNo
								: cordNo.isEmpty() ? cordName : cordName + " - " + cordNo;

						addFullWidthPair(eventTable, basicFont, cordinatorPersonLabel, full, blackColor, blackColor,
								NONE, true);
					}
					
					EventFunctionRawMaterialPermissionResponseDto permissionResponseDto = menuPreparationServiceImpl
							.getEventFunctionPermissionRawMaterial(eventId, eventFunctionId, userid);

					boolean hasPermissables = permissionResponseDto.getPermissables() != null
							&& !permissionResponseDto.getPermissables().isEmpty();

					boolean hasNotPermissables = permissionResponseDto.getNotPermissables() != null
							&& !permissionResponseDto.getNotPermissables().isEmpty();

					if (hasPermissables || hasNotPermissables) {
						if (hasPermissables) {
							String permissables = permissionResponseDto.getPermissables().stream()
									.map(item -> menuPreparationServiceImpl.getRawMaterialNameByLang(item, lang))
									.collect(Collectors.joining(", "));
							
							addFullWidthPair(eventTable, basicFont, permissableLabel, permissables, blackColor, blackColor,
									NONE, false);
						}

						if (hasNotPermissables) {
							String notPermissables = permissionResponseDto.getNotPermissables().stream()
									.map(item -> menuPreparationServiceImpl.getRawMaterialNameByLang(item, lang))
									.collect(Collectors.joining(", "));

							addFullWidthPair(eventTable, basicFont, notPermissableLabel, notPermissables, blackColor, blackColor,
									NONE, false);
						}
					}
				}

				if (!isFirstFunction) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}

				// =========================================================
				// PER-FUNCTION BLOCK - same rule: first row of the block gets
				// ROW_TOP (SolidBorder), every subsequent row in the block is NONE.
				// =========================================================
				String functionName = safeText(eventFunctionMasterResponseDto.getFunctionName().toUpperCase());

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, eventFunctionMasterResponseDto.getFunctionId());

				String functionVenue;
				String functionNotes;
				if (lang == 1) {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenueHindi() != null
							? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: "";
					functionNotes = eventFunctionMasterResponseDto.getNotesHindi() != null
							? eventFunctionMasterResponseDto.getNotesHindi()
							: "";
				} else if (lang == 2) {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenueGujarati() != null
							? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
							: "";
					functionNotes = eventFunctionMasterResponseDto.getNotesGujarati() != null
							? eventFunctionMasterResponseDto.getNotesGujarati()
							: "";
				} else {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenue() != null
							? eventFunctionMasterResponseDto.getFunctionVenue()
							: "";
					functionNotes = eventFunctionMasterResponseDto.getNotesEnglish() != null
							? eventFunctionMasterResponseDto.getNotesEnglish()
							: "";
				}
				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				}

				// Row 1 of the function block: Function | Venue -> gets ROW_TOP (top divider
				// line)
				addPair(eventTable, basicFont, blackColor, function, functionName, person,
						safeText(eventFunctionMasterResponseDto.getPax().toString()), ROW_TOP);

				// Row 2: Persons | Date -> interior row, no border
//				addPair(eventTable, basicFont, blackColor, person,
//						safeText(eventFunctionMasterResponseDto.getPax().toString()), date, null, NONE);
				// (Date value is rendered as a full-width row below since it spans 4 columns
				// in the original layout - handled next.)

				String eventStartTime = eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp().split(" ")[1].substring(0, 5) + " "
								+ eventFunctionMasterResponseDto.getFunctionStartTimestamp().split(" ")[2]
						: "";
				String eventEndTime = eventFunctionMasterResponseDto.getFunctionEndTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionEndTimestamp().split(" ")[1].substring(0, 5) + " "
								+ eventFunctionMasterResponseDto.getFunctionEndTimestamp().split(" ")[2]
						: "";
				String dateTimeValue = safeText(
						eventFunctionMasterResponseDto.getFunctionStartTimestamp().split(" ")[0]) + " "
						+ safeText(eventStartTime).toUpperCase() + " To " + safeText(eventEndTime).toUpperCase();

				// Row 3: Date value spans remaining columns -> interior row, no border
				addFullWidthPair(eventTable, basicFont, date, dateTimeValue, blackColor, blackColor, NONE, true);

				// Row 4: Function Notes label + value spans 4 cols -> interior row, no border
				addFullWidthPair(eventTable, basicFont, lang == 0 ? "Function Notes" : eventNotes,
						safeText(functionNotes), blackColor, blackColor, NONE, true);

				String price;
				if (eventFunctionMasterResponseDto.getIsPackage()) {
					price = eventFunctionMasterResponseDto.getPackagePrice().toString();
				} else {
					price = eventFunctionMasterResponseDto.getRate().toString();
				}

				addFullWidthPair(eventTable, basicFont, pkgPrice, price, blackColor, blackColor, NONE, true);

				document.add(eventTable);

				// ---- Menu items ----
				for (MenuReportResponseDto menu : eventFunctionMasterResponseDto.getMenuCategories()) {
					catItemTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
					catItemTable.setWidth(UnitValue.createPercentValue(100));
					catItemTable.setBorder(Border.NO_BORDER);
					catItemTable.setMarginTop(8f);
					catItemTable.setKeepTogether(false);
					catItemTable.setBorderTop(new SolidBorder(1f));
					catItemTable.setBorderBottom(new SolidBorder(1f));

					ISplitCharacters splitAll = new ISplitCharacters() {
						@Override
						public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
							return true;
						}
					};

					Paragraph p = new Paragraph(
							menu.getNameEnglish() != null ? menu.getNameEnglish().toUpperCase() : "").setFont(basicFont)
							.setFontSize(16f).setFontColor(blackColor).setSplitCharacters(splitAll).setMarginLeft(5f)
							.setPadding(0).simulateBold();

					Cell catCell = new Cell().add(p).setBorder(Border.NO_BORDER)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);

					if (isCategoryInstruction == 1 && menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
						catCell.add(new Paragraph(
								menuPreparationServiceImpl.formatText(" ( " + menu.getMenuNotes() + " )", lang))
								.setSplitCharacters(splitAll).setFont(basicFont).setFontSize(12f)
								.setFontColor(blackColor).setMarginLeft(6).setMarginTop(2f)
								.setTextAlignment(TextAlignment.CENTER));
					}

					catItemTable.addCell(catCell);

					Cell itemsCell = new Cell().setBorder(Border.NO_BORDER);
					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						itemsCell.add(new Paragraph(
								"• " + (item.getNameEnglish() != null ? item.getNameEnglish().toUpperCase() : ""))
								.setFont(basicFont).setFontSize(14f).setFontColor(blackColor).setMarginLeft(12)
								.setPadding(0).setTextAlignment(TextAlignment.LEFT));

						if (isItemInstruction == 1 && item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
							itemsCell.add(new Paragraph(
									menuPreparationServiceImpl.formatText(" ( " + item.getItemNotes() + " ) ", lang))
									.setFont(basicFont).setSplitCharacters(splitAll).setFontSize(12)
									.setFixedLeading(13f).setFontColor(blackColor).setMarginLeft(18).setMarginTop(2f)
									.setMarginBottom(0f).setPadding(0f).setTextAlignment(TextAlignment.LEFT));
						}
					}
					catItemTable.addCell(itemsCell.setKeepTogether(true));
					document.add(catItemTable);
				}
				isFirstFunction = false;
			}

			if (isExtraCharges != null && isExtraCharges == 1) {

				ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
						userid);

				if (extraCharges != null && extraCharges.getHeadings() != null
						&& !extraCharges.getHeadings().isEmpty()) {

					DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
					DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					// ── Main Title: "-: EXTRA CHARGE :-" ─────────────────────────────
					Paragraph extraTitle = new Paragraph("-: EXTRA CHARGE :-").setFont(basicFont).setFontSize(18)
							.setFontColor(blackColor).setTextAlignment(TextAlignment.CENTER).setUnderline()
							.simulateBold().setMarginBottom(15f);
					document.add(extraTitle);

					// ── Loop each heading ─────────────────────────────────────────────
					for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges.getHeadings()) {

						// Heading title: HEADING NAME
						Paragraph headingTitle = new Paragraph(heading.getHeadingName().toUpperCase())
								.setFont(basicFont).setFontSize(14).setFontColor(blackColor).simulateBold()
								.setUnderline().setTextAlignment(TextAlignment.LEFT).setMarginTop(12f)
								.setMarginBottom(6f);
						document.add(headingTitle);

						// ── Table: DATE | START TIME | END TIME | SESSION | QTY | RATE | TOTAL ──
						float[] colWidths = { 15f, 17f, 15f, 13f, 12f, 12f, 16f };
						Table chargeTable = new Table(UnitValue.createPercentArray(colWidths));
						chargeTable.setWidth(UnitValue.createPercentValue(100));
						chargeTable.setBorder(new SolidBorder(blackColor, 1f));

						// ── Header Row ────────────────────────────────────────────────
						String[] colHeaders = { "DATE", "START TIME", "END TIME", "SESSION", "QTY", "RATE", "TOTAL" };
						for (String h : colHeaders) {
							chargeTable.addHeaderCell(new Cell()
									.add(new Paragraph(h).setFont(basicFont).setFontSize(11).setFontColor(blackColor)
											.simulateBold().setUnderline())
									.setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(blackColor, 0.8f))
									.setPaddingTop(5f).setPaddingBottom(5f));
						}

						// ── Data Rows ─────────────────────────────────────────────────
						if (heading.getRows() != null && !heading.getRows().isEmpty()) {
							for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {

								chargeTable.addCell(dataCell(
										row.getChargeDate() != null ? row.getChargeDate().format(dateFormatter) : "",
										basicFont, blackColor, 10, blackColor));

								chargeTable.addCell(dataCell(row.getChargeStartTime() != null
										? row.getChargeStartTime().format(timeFormatter)
										: "", basicFont, blackColor, 10, blackColor));

								chargeTable.addCell(dataCell(
										row.getChargeEndTime() != null ? row.getChargeEndTime().format(timeFormatter)
												: "",
										basicFont, blackColor, 10, blackColor));

								chargeTable.addCell(
										dataCell(row.getSession() != null ? String.valueOf(row.getSession()) : "0",
												basicFont, blackColor, 10, blackColor));

								chargeTable.addCell(dataCell(
										row.getPersonItem() != null ? String.valueOf(row.getPersonItem()) : "0",
										basicFont, blackColor, 10, blackColor));

								chargeTable
										.addCell(dataCell(row.getRate() != null ? row.getRate().toPlainString() : "0",
												basicFont, blackColor, 10, blackColor));

								chargeTable
										.addCell(dataCell(row.getTotal() != null ? row.getTotal().toPlainString() : "0",
												basicFont, blackColor, 10, blackColor));
							}
						} else {
							// Empty rows for visual layout (like the sample image)
							for (int i = 0; i < 7; i++) {
								for (int j = 0; j < 7; j++) {
									chargeTable.addCell(dataCell("", basicFont, blackColor, 10, blackColor));
								}
							}
						}

						document.add(chargeTable);

						// ── Heading Total ─────────────────────────────────────────────
						Paragraph headingTotalPara = new Paragraph("Total: "
								+ (heading.getHeadingTotal() != null ? heading.getHeadingTotal().toPlainString() : "0"))
								.setFont(basicFont).setFontSize(12).setFontColor(blackColor).simulateBold()
								.setTextAlignment(TextAlignment.RIGHT).setMarginTop(4f);
						document.add(headingTotalPara);
					}

					// ── Grand Total ───────────────────────────────────────────────────
					Paragraph grandTotalPara = new Paragraph("Grand Total: "
							+ (extraCharges.getGrandTotal() != null ? extraCharges.getGrandTotal().toPlainString()
									: "0"))
							.setFont(basicFont).setFontSize(14).setFontColor(blackColor).simulateBold()
							.setTextAlignment(TextAlignment.RIGHT).setMarginTop(10f).setUnderline();
					document.add(grandTotalPara);
				}
			}

			if (isTermsCondition == 1) {
				Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

				if (terms != null && terms.isPresent()) {
					EventTermsAndConditionEntity eventTermsEntity = terms.get();
					List<EventTermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

					if (features != null && !features.isEmpty()) {
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

						Paragraph termsTitle = new Paragraph("Special Instruction").simulateBold().setFont(basicFont)
								.setFontSize(16).setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f)
								.setFontColor(blackColor);

						document.add(termsTitle);

						ISplitCharacters breakAll = new ISplitCharacters() {
							@Override
							public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
								return true; // break anywhere
							}
						};

						int index = 1;
						for (EventTermsAndConditionFeaturesEntity feature : features) {
							String desc;

							if (lang == 1) {
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(basicFont).setFontSize(12)
									.setWidth(UnitValue.createPercentValue(90f))
									.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
									.setSplitCharacters((text, glyphPos) -> true).setFontColor(blackColor);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			// ================= PAGE NUMBERS =================
			int totalPages = pdfDocument.getNumberOfPages();
			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());
				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 22,
						TextAlignment.CENTER);
				canvas.close();
			}

			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo() + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"back office report")
					+ ".pdf";

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	private Cell dataCell(String value, PdfFont font, Color fontColor, int fontSize, Color borderColor) {
		return new Cell()
				.add(new Paragraph(value == null ? "" : value).setFont(font).setFontSize(fontSize)
						.setFontColor(fontColor))
				.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setBorder(new SolidBorder(borderColor, 0.8f)).setPaddingTop(4f).setPaddingBottom(4f);
	}

	private void addPair(Table table, PdfFont font, Color color, String label1, String value1, String label2,
			String value2, Border border) {

		table.addCell(
				new Cell().add(new Paragraph(label1).setFont(font).setFontSize(14).setFontColor(color).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(border));

		table.addCell(new Cell().add(new Paragraph(":").setFont(font).setFontSize(14).setFontColor(color)).setPadding(0)
				.setBorder(Border.NO_BORDER).setBorderTop(border));

		table.addCell(new Cell()
				.add(new Paragraph(value1 == null ? "" : value1).setFont(font).setFontSize(14).setFontColor(color))
				.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(border));

		if (label2 != null) {
			table.addCell(new Cell()
					.add(new Paragraph(label2).setFont(font).setFontSize(14).setFontColor(color).simulateBold())
					.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(border));

			table.addCell(new Cell().add(new Paragraph(":").setFont(font).setFontSize(14).setFontColor(color))
					.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(border));

			table.addCell(new Cell()
					.add(new Paragraph(value2 == null ? "" : value2).setFont(font).setFontSize(14).setFontColor(color))
					.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(border));
		} else {
			// Pad so the row is complete; keep the SAME border so a visible top line
			// (if any) runs unbroken across the full row width.
			for (int i = 0; i < 3; i++) {
				table.addCell(new Cell().setBorder(Border.NO_BORDER).setBorderTop(border));
			}
		}
	}

	private void addFullWidthPair(Table table, PdfFont font, String label, String value, Color valueColor,
			Color labelColor, Border border, Boolean isValueBold) {

		table.addCell(new Cell()
				.add(new Paragraph(label).setFont(font).setFontSize(14).setFontColor(labelColor)
						.setTextAlignment(TextAlignment.LEFT).simulateBold())
				.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(border));

		table.addCell(new Cell()
				.add(new Paragraph(":").setFont(font).setFontSize(14).setFontColor(labelColor)
						.setTextAlignment(TextAlignment.LEFT))
				.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(border));

		Cell cell = new Cell(1, 4)
				.add(new Paragraph(value == null ? "" : value).setFont(font).setFontSize(14).setFontColor(valueColor)
						.setTextAlignment(TextAlignment.LEFT))
				.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(border);
		if(isValueBold) {
			cell.simulateBold();
		}
		table.addCell(cell);
	}

	private void addRow(Table table, String label, String value, Style labelStyle, Style valueStyle, Color borderColor,
			Boolean isSetBorder, String borderType) {
		Cell labelCell = new Cell().add(new Paragraph(safeText(label)).addStyle(labelStyle)).setBorder(Border.NO_BORDER)
				.setPadding(0).setTextAlignment(TextAlignment.CENTER).simulateBold();
		if (isSetBorder) {
			if (borderType.equalsIgnoreCase("BOTTOM")) {
				labelCell.setBorderBottom(new SolidBorder(borderColor, 1f));
			} else if (borderType.equalsIgnoreCase("TOP")) {
				labelCell.setBorderTop(new SolidBorder(borderColor, 1f));
			}
		}
		table.addCell(labelCell);

		Cell middleCell = new Cell().add(new Paragraph(safeText(":")).addStyle(valueStyle)).setBorder(Border.NO_BORDER)
				.setPadding(0).setTextAlignment(TextAlignment.CENTER);
		if (isSetBorder) {
			if (borderType.equalsIgnoreCase("BOTTOM")) {
				middleCell.setBorderBottom(new SolidBorder(borderColor, 1f));
			} else if (borderType.equalsIgnoreCase("TOP")) {
				middleCell.setBorderTop(new SolidBorder(borderColor, 1f));
			}
		}
		table.addCell(middleCell);

		Cell valueCell = new Cell().add(new Paragraph(safeText(value)).addStyle(valueStyle)).setBorder(Border.NO_BORDER)
				.setPadding(0).setTextAlignment(TextAlignment.CENTER);
		if (isSetBorder) {
			if (borderType.equalsIgnoreCase("BOTTOM")) {
				valueCell.setBorderBottom(new SolidBorder(borderColor, 1f));
			} else if (borderType.equalsIgnoreCase("TOP")) {
				valueCell.setBorderTop(new SolidBorder(borderColor, 1f));
			}
		}
		table.addCell(valueCell);
	}

	private void addFullRow(Table table, String label, String value, Style labelStyle, Style valueStyle,
			Color borderColor) {
		Cell labelCell = new Cell().add(new Paragraph(safeText(label)).addStyle(labelStyle)).setBorder(Border.NO_BORDER)
				.setPadding(0).setTextAlignment(TextAlignment.CENTER).simulateBold();
		table.addCell(labelCell);

		Cell middleCell = new Cell().add(new Paragraph(safeText(":")).addStyle(valueStyle)).setBorder(Border.NO_BORDER)
				.setPadding(0).setTextAlignment(TextAlignment.CENTER);
		table.addCell(middleCell);

		Cell valueCell = new Cell(1, 4).add(new Paragraph(safeText(value)).addStyle(valueStyle))
				.setBorder(Border.NO_BORDER).setPadding(0).setTextAlignment(TextAlignment.CENTER);
		table.addCell(valueCell);

	}

	private void addLabelRow(Table table, String label, Style labelStyle, Color borderColor) {
		Cell labelCell = new Cell(1, 6)
				.add(new Paragraph(safeText(label)).setTextAlignment(TextAlignment.CENTER).setFontSize(16f)
						.addStyle(labelStyle))
				.setBorder(Border.NO_BORDER).setPadding(0).simulateBold().setBorder(Border.NO_BORDER)
				.setBorderTop(new SolidBorder(borderColor, 1f)).setBorderBottom(new SolidBorder(borderColor, 1f));
		table.addCell(labelCell);
	}

	@Override
	public String getMenuPlanningSimpleReport10(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails) {
		try {
			PdfFont basicFont = null;
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "", l1 = "",
					l2 = "", note = "", eContact = "", eventFlow = "", function = "", person = "", eTime = "",
					date = "", rate = "", party = "", eventNotes = "", billingNameLabel = "", serviceLabel = "",
					themeLabel = "", remarks = "";
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				customerName = "नाम";
				customerPhone = "मोबाइल नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "दिनांक";
				fNotes = "नोट्स";
				eVenue = "आयोजन स्थान";
				function = "कार्यक्रम";
				person = "मेम्बर्स";
				eTime = "समय";
				eContact = "संपर्क नंबर";
				l1 = "व्यक्तियों की संख्या:";
				eventFlow = "कार्यक्रम का संचालन क्रम";
				note = "नोट";
				date = "दिनांक";
				rate = "रेट";
				party = "पार्टी का नाम";
				eventNotes = "नोट्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				remarks = "रिमार्क्स";
				y = 512;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Regular.ttf");

					// Tamil
					customerName = "வாடிக்கையாளர் பெயர்";
					customerPhone = "மொபைல் எண்";
					eName = "நிகழ்ச்சி பெயர்";
					eDate = "நிகழ்ச்சி தேதி";
					fNotes = "உணவு விவரம்";
					eVenue = "நிகழ்வு இடம்";
					function = "நிகழ்ச்சி";
					person = "நபர்";
					eTime = "நேரம்";
					eContact = "தொடர்பு எண்";
					l1 = "நபர்களின் எண்ணிக்கை:";
					eventFlow = "நிகழ்ச்சி நடைபெறும் வரிசை";
					note = "குறிப்பு";
					date = "தேதி";
					rate = "விலை";
					party = "கட்சியின் பெயர்";
					eventNotes = "நிகழ்வு குறிப்புகள்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					remarks = "ரிமார்க்ஸ்";
				} else if (language.equalsIgnoreCase("Telugu")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Regular.ttf");

					// Telugu
					customerName = "కస్టమర్ పేరు";
					customerPhone = "మొబైల్ నంబర్";
					eName = "కార్యక్రమ పేరు";
					eDate = "కార్యక్రమ తేదీ";
					fNotes = "భోజన వివరాలు";
					eVenue = "కార్యక్రమ స్థలం";
					function = "కార్యక్రమం";
					person = "వ్యక్తి";
					eTime = "సమయం";
					eContact = "సంప్రదింపు నంబర్";
					l1 = "వ్యక్తుల సంఖ్య:";
					eventFlow = "కార్యక్రమ నిర్వహణ క్రమం";
					note = "గమనిక";
					date = "తేదీ";
					rate = "రేటు";
					party = "పార్టీ పేరు";
					eventNotes = "ఈవెంట్ గమనికలు";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					remarks = "రిమార్క్స్";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Regular.ttf");

					// Malayalam
					customerName = "ഉപഭോക്താവിന്റെ പേര്";
					customerPhone = "മൊബൈൽ നമ്പർ";
					eName = "പരിപാടിയുടെ പേര്";
					eDate = "പരിപാടിയുടെ തീയതി";
					fNotes = "ഭക്ഷണ വിശദാംശങ്ങൾ";
					eVenue = "പരിപാടി സ്ഥലം";
					function = "പരിപാടി";
					person = "വ്യക്തി";
					eTime = "സമയം";
					eContact = "ബന്ധപ്പെടാനുള്ള നമ്പർ";
					l1 = "വ്യക്തികളുടെ എണ്ണം:";
					eventFlow = "പരിപാടി നടത്തിപ്പ് ക്രമം";
					note = "കുറിപ്പ്";
					date = "തീയതി";
					rate = "നിരക്ക്";
					party = "പാർട്ടി പേര്";
					eventNotes = "ഇവന്റ് കുറിപ്പുകൾ";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					remarks = "റിമാർക്സ്";
				} else if (language.equalsIgnoreCase("Marathi")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");

					// Marathi
					customerName = "ग्राहकाचे नाव";
					customerPhone = "मोबाईल नंबर";
					eName = "कार्यक्रमाचे नाव";
					eDate = "कार्यक्रमाची दिनांक";
					fNotes = "भोजन तपशील";
					eVenue = "आयोजन स्थळ";
					function = "कार्यक्रम";
					person = "व्यक्ती";
					eTime = "वेळ";
					eContact = "संपर्क नंबर";
					l1 = "व्यक्तींची संख्या:";
					eventFlow = "कार्यक्रमाचा संचालन क्रम";
					note = "टीप";
					date = "दिनांक";
					rate = "रेट";
					party = "पार्टीचे नाव";
					eventNotes = "कार्यक्रम नोंदी";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					remarks = "रिमार्क्स";
				} else {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");

					customerName = "ગ્રાહકનું નામ";
					customerPhone = "મોબાઇલ નંબર";
					eName = "કાર્યક્રમનું નામ";
					eDate = "કાર્યક્રમની તારીખ";
					fNotes = "ભોજન વિગતો";
					eVenue = "આયોજન સ્થળ";
					function = "કાર્યક્રમ";
					person = "વ્યક્તિ";
					eTime = "સમય";
					eContact = "સંપર્ક નંબર";
					eventFlow = "કાર્યક્રમનું સંચાલન ક્રમ";
					l1 = "વ્યક્તિઓની સંખ્યા:";
					note = "નોંધ";
					date = "તારીખ";
					rate = "રેટ";
					party = "પાર્ટીનું નામ";
					eventNotes = "કાર્યક્રમની નોંધ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					remarks = "રિમાર્ક્સ";
				}
				System.out.println("Gujarati font loaded successfully");
				y = 510;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				System.out.println("English font loaded successfully");
				customerName = "Customer Name";
				customerPhone = "Mobile No.";
				eName = "Event Name";
				eDate = "Event Date";
				fNotes = "Food Note";
				eVenue = "Venue";
				function = "Function";
				person = "Persons";
				eTime = "Timing";
				eContact = "CONTACT NO";
				eventFlow = "FLOW OF EVENT";
				l1 = "OF PERSONS:";
				note = "Note";
				date = "Date";
				rate = "Rate";
				party = "Party Name";
				eventNotes = "Event Notes";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";
				remarks = "Remarks";
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);

			if (eventDto == null) {
				return "";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "back office report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(30, 30, 50, 30);

			// Load all background images upfront
			ImageData mainBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
			ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			String watermarkPath;

			watermarkPath = environment.getProperty("app.image.url") + eventDto.getLogo();
//			watermarkPath = "/flipbook/pages/logo.png";

			if (isCompanyDetails == 1) {
				pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(watermarkPath));
			}

			Color blackColor = new DeviceRgb(0, 0, 0);

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			String venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			String foodNotesName = eventDto.getFoodType();
			String foodNotes = eventDto.getFoodNotes();
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone() != null ? eventDto.getCmpPhone() : "";
			String cmpAddress = eventDto.getCmpAddress() != null ? eventDto.getCmpAddress() : "";
			String cmpEmail = eventDto.getEmail() != null ? eventDto.getEmail() : "";
			String remarksValue = eventDto.getRemark() != null ? eventDto.getRemark() : "";
			String eventTime = eventDto.getEventTime() != null ? eventDto.getEventTime() : "";

			boolean isFirstFunction = true;

			// Create main table with 2 columns for the header layout
			Table catItemTable = null;

			String billingName = "";
			String service = "";
			String theme = "";
			String foodPref = eventDto.getFoodType();
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
				System.out.println(service);
			}

			Table eventTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 2f, 26f, 20f, 2f, 20f }));
			eventTable.setWidth(UnitValue.createPercentValue(100f));
			eventTable.setFixedLayout();
			eventTable.setBorderBottom(new SolidBorder(blackColor, 1f));
			eventTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

			// Common Styles
			Style labelStyle = new Style().setFont(basicFont).setFontColor(blackColor).setFontSize(14)
					.setTextAlignment(TextAlignment.LEFT);

			Style valueStyle = new Style().setFont(basicFont).setFontColor(blackColor).setFontSize(14)
					.setTextAlignment(TextAlignment.LEFT);

			ImageData logoData = null;
			logoData = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + eventDto.getLogo());
//			logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");

			Image logo = new Image(logoData);

			// Resize & align
			logo.scaleToFit(120, 120);
			logo.setAutoScale(false);
			logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

			cell = new Cell(6, 1).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER)
					.setPadding(3f);

			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph(cmpName.toUpperCase()).setFont(basicFont).setFontSize(12)
							.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph(cmpAddress).setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph().add(new Text("Mobile No : ").simulateBold()).add(new Text(cmpPhone))
							.setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph().add(new Text("Email : ").simulateBold()).add(new Text(cmpEmail))
							.setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			eventTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));
			eventTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));

			addRow(eventTable, eDate, eventDate, labelStyle, valueStyle, blackColor, true, "BOTTOM");
			addRow(eventTable, eTime, eventTime, labelStyle, valueStyle, blackColor, true, "BOTTOM");

			addFullRow(eventTable, party, hostName, labelStyle, valueStyle, blackColor);

			if (billingName != null && !billingName.trim().isEmpty()) {
				addFullRow(eventTable, billingNameLabel, billingName, labelStyle, valueStyle, blackColor);
			}

			addFullRow(eventTable, customerPhone, mobileNo, labelStyle, valueStyle, blackColor);
			addFullRow(eventTable, eVenue, venue, labelStyle, valueStyle, blackColor);
			addFullRow(eventTable, eName, eventName, labelStyle, valueStyle, blackColor);
			addFullRow(eventTable, person, pax, labelStyle, valueStyle, blackColor);

			if (remarks != null && remarks.trim().length() != 0) {
				addFullRow(eventTable, remarks, remarksValue, labelStyle, valueStyle, blackColor);
			}

			if (fNotes != null && fNotes.trim().length() != 0) {
				addFullRow(eventTable, fNotes,
						foodNotesName.toString() + (foodNotes.trim().isEmpty() ? "" : " (" + foodNotes + ")"),
						labelStyle, valueStyle, blackColor);
			}

			if (service.trim().length() != 0) {
				addFullRow(eventTable, serviceLabel, service, labelStyle, valueStyle, blackColor);
			}

			if (theme.trim().length() != 0) {
				addFullRow(eventTable, themeLabel, theme, labelStyle, valueStyle, blackColor);
			}

			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				if (!isFirstFunction) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					eventTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 2f, 26f, 20f, 2f, 20f }));
					eventTable.setWidth(UnitValue.createPercentValue(100f));
					eventTable.setFixedLayout();
					eventTable.setBorderBottom(new SolidBorder(blackColor, 1f));
					eventTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
				}

				String functionVenue = "";
				String functionNotes = "";
				if (lang == 1) {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenueHindi() != null
							? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: "";
					functionNotes = eventFunctionMasterResponseDto.getNotesHindi() != null
							? eventFunctionMasterResponseDto.getNotesHindi()
							: "";
				} else if (lang == 2) {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenueGujarati() != null
							? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
							: "";
					functionNotes = eventFunctionMasterResponseDto.getNotesGujarati() != null
							? eventFunctionMasterResponseDto.getNotesGujarati()
							: "";
				} else {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenue() != null
							? eventFunctionMasterResponseDto.getFunctionVenue()
							: "";
					functionNotes = eventFunctionMasterResponseDto.getNotesEnglish() != null
							? eventFunctionMasterResponseDto.getNotesEnglish()
							: "";
				}

				String eventStartTime = eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp().split(" ")[1].substring(0, 5) + " "
								+ eventFunctionMasterResponseDto.getFunctionStartTimestamp().split(" ")[2]
						: "";

				String eventEndTime = eventFunctionMasterResponseDto.getFunctionEndTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionEndTimestamp().split(" ")[1].substring(0, 5) + " "
								+ eventFunctionMasterResponseDto.getFunctionEndTimestamp().split(" ")[2]
						: "";

				addRow(eventTable, function, eventFunctionMasterResponseDto.getFunctionName().toUpperCase(), labelStyle,
						valueStyle, blackColor, true, "TOP");

				addRow(eventTable, person, eventFunctionMasterResponseDto.getPax().toString(), labelStyle, valueStyle,
						blackColor, true, "TOP");

				addFullRow(eventTable, eVenue, functionVenue, labelStyle, valueStyle, blackColor);

				addFullRow(eventTable, date,
						safeText(eventFunctionMasterResponseDto.getFunctionStartTimestamp().split(" ")[0]) + " "
								+ safeText(eventStartTime).toUpperCase() + " To "
								+ safeText(eventEndTime).toUpperCase(),
						labelStyle, valueStyle, blackColor);
				addFullRow(eventTable, lang == 0 ? "Function Notes" : eventNotes, functionNotes, labelStyle, valueStyle,
						blackColor);

				addLabelRow(eventTable, "MENU", labelStyle, blackColor);
				document.add(eventTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				// Add menu content
				for (MenuReportResponseDto menu : menuReportResponseDtos) {

					catItemTable = new Table(UnitValue.createPercentArray(new float[] { 100f }));
					catItemTable.setWidth(UnitValue.createPercentValue(100));
					catItemTable.setBorder(Border.NO_BORDER);
					catItemTable.setMarginTop(8f);
					catItemTable.setKeepTogether(false);

					Paragraph p = new Paragraph(
							menu.getNameEnglish() != null ? menu.getNameEnglish().toUpperCase() : "").setFont(basicFont)
							.setFontSize(16f).setFontColor(blackColor).setSplitCharacters(new ISplitCharacters() {

								@Override
								public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
									return true;
								}
							}).setMarginLeft(0).setPadding(0).simulateBold().setUnderline();

					cell = new Cell().add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);

					// Category Instructions
					if (isCategoryInstruction == 1) {
						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
							cell.add(new Paragraph(
									menuPreparationServiceImpl.formatText(" ( " + menu.getMenuNotes() + " )", lang))
									.setSplitCharacters(new ISplitCharacters() {

										@Override
										public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
											return true;
										}
									}).setFont(basicFont).setFontSize(12f).setFontColor(blackColor).setMarginLeft(0)
									.setMarginTop(2f).setTextAlignment(TextAlignment.CENTER));
						}
					}

					catItemTable.addCell(cell);
					cell = new Cell().setBorder(Border.NO_BORDER);

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						Paragraph p1 = new Paragraph(
								item.getNameEnglish() != null ? item.getNameEnglish().toUpperCase() : "")
								.setFont(basicFont).setFontSize(14f).setFontColor(blackColor).setMarginLeft(0)
								.setPadding(0).setPaddingTop(5f).setTextAlignment(TextAlignment.CENTER);

						cell.add(p1);

						// Item Instructions
						if (isItemInstruction == 1) {
							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
								cell.add(new Paragraph(menuPreparationServiceImpl
										.formatText(" ( " + item.getItemNotes() + " ) ", lang)).setFont(basicFont)
										.setSplitCharacters(new ISplitCharacters() {

											@Override
											public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
												return true;
											}
										}).setFontSize(12).setFixedLeading(13f).setFontColor(blackColor)
										.setMarginLeft(0).setMarginTop(2f).setMarginBottom(0f).setPadding(0f)
										.setTextAlignment(TextAlignment.CENTER));
							}
						}
					}
					catItemTable.addCell(cell.setKeepTogether(true));
					document.add(catItemTable);
				}
				isFirstFunction = false;
			}

			/* ================= PAGE NUMBERS ================= */
			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 22,
						TextAlignment.CENTER);

				canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"back office report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String getMenuPlanningSimpleReport10Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, int intValue, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails) {

		getMenuPlanningSimpleReport10(eventId, eventFunctionId, isCategorySlogan, isCategoryInstruction, intValue,
				isItemSlogan, isItemInstruction, isCompanyDetails, re, lang, userid, isPartyDetails);

		try {
			EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);

			if (event == null) {
				return null;
			}

			String rootPath = re.getSession().getServletContext().getRealPath("/");

			String pdfFileName = getReportName(getPartyNameByEventId(eventId),
					formatDate(event.getEventStartDateTime()), "back office report") + ".pdf";

			String pdfPath = rootPath + "resources/tempDownload/" + event.getEventNo() + "/" + pdfFileName;

			return adobeDocxGenerator.convertPdfToDocxAndGetUrl(new File(pdfPath), event.getEventNo());

		} catch (Exception e) {
			throw new RuntimeException("Failed to convert PDF to DOCX", e);
		}
	}

	public String getMenuPlanningWithChefReport(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails) {
		try {
			PdfFont basicFont = null;
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "", l1 = "",
					l2 = "", note = "", eContact = "", eventFlow = "", function = "", person = "", eTime = "",
					date = "", rate = "", party = "", chefLabel = "";
			menuPreparationServiceImpl.loadLicense();

			if (lang == 1) {
				// Hindi
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				customerName = "ग्राहक का नाम";
				customerPhone = "मोबाइल नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "कार्यक्रम की तिथि";
				fNotes = "भोजन विवरण";
				eVenue = "आयोजन स्थल";
				function = "कार्यक्रम";
				person = "व्यक्ति";
				eTime = "समय";
				eContact = "संपर्क नंबर";
				l1 = "व्यक्तियों की संख्या:";
				eventFlow = "कार्यक्रम का संचालन क्रम";
				note = "नोट";
				date = "दिनांक";
				rate = "रेट";
				party = "पार्टी का नाम";
				chefLabel = "शेफ / विक्रेता";
			} else if (lang == 2) {
				// Gujarati
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				customerName = "ગ્રાહકનું નામ";
				customerPhone = "મોબાઇલ નંબર";
				eName = "કાર્યક્રમનું નામ";
				eDate = "કાર્યક્રમની તારીખ";
				fNotes = "ભોજન વિગતો";
				eVenue = "આયોજન સ્થળ";
				function = "કાર્યક્રમ";
				person = "વ્યક્તિ";
				eTime = "સમય";
				eContact = "સંપર્ક નંબર";
				eventFlow = "કાર્યક્રમનું સંચાલન ક્રમ";
				l1 = "વ્યક્તિઓની સંખ્યા:";
				note = "નોંધ";
				date = "તારીખ";
				rate = "રેટ";
				party = "પાર્ટીનું નામ";
				chefLabel = "શેફ / વેન્ડર";
			} else {
				// English
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				customerName = "Customer Name";
				customerPhone = "Mobile No.";
				eName = "Event Name";
				eDate = "Event Date";
				fNotes = "Food Note";
				eVenue = "Venue";
				function = "Function";
				person = "Persons";
				eTime = "TIMING";
				eContact = "CONTACT NO";
				eventFlow = "FLOW OF EVENT";
				l1 = "OF PERSONS:";
				note = "Note";
				date = "Date";
				rate = "Rate";
				party = "PARTY NAME";
				chefLabel = "Chef / Vendor";
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);

			if (eventDto == null) {
				return "";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");
			if (!outputPath.exists()) {
				outputPath.mkdirs();
			}

			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "chef report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 30, 50, 30);

			ImageData mainBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
			ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");
			// String watermarkPath = "/flipbook/pages/logo.png";
			String watermarkPath = environment.getProperty("app.image.url") + eventDto.getLogo();

			if (isCompanyDetails == 1) {
				pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(watermarkPath));
			}

			Color redColor = new DeviceRgb(255, 0, 0);
			Color blackColor = new DeviceRgb(0, 0, 0);
			Color brownColor = new DeviceRgb(168, 104, 50);

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp().isEmpty() ? "" : eventDto.getEventStartTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			String foodNotes = eventDto.getFoodNotes();
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone() != null ? eventDto.getCmpPhone() : "";
			String cmpAddress = eventDto.getCmpAddress() != null ? eventDto.getCmpAddress() : "";
			String cmpEmail = eventDto.getEmail() != null ? eventDto.getEmail() : "";

			boolean isFirstFunction = true;

			for (EventFunctionReportResponseDto eventFunctionDto : eventDto.getFunctions()) {

				// ── Header table (same as original) ─────────────────────────────
				Table eventTable = new Table(
						UnitValue.createPercentArray(new float[] { 32f, 1.5f, 32f, 32f, 1.5f, 32f }));
				eventTable.setWidth(UnitValue.createPercentValue(100));
				eventTable.setMarginBottom(-2f);
				eventTable.setBorder(Border.NO_BORDER);
				eventTable.setBorderBottom(new SolidBorder(1f));
				eventTable.setMarginTop(10f);

				if (isFirstFunction) {
					// ImageData logoData =
					// menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");
					ImageData logoData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + eventDto.getLogo());
					Image logo = new Image(logoData);
					logo.setWidth(UnitValue.createPercentValue(100f));
					logo.setAutoScale(false);
					logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

					cell = new Cell(6, 1).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setBorder(Border.NO_BORDER).setPadding(3f);
					if (isCompanyDetails == 1)
						eventTable.addCell(cell);

					cell = new Cell(1, 5)
							.add(new Paragraph(cmpName.toUpperCase()).setFont(basicFont).setFontSize(12)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
					if (isCompanyDetails == 1)
						eventTable.addCell(cell);

					cell = new Cell(1, 5)
							.add(new Paragraph(cmpAddress).setFont(basicFont).setFontSize(10).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
					if (isCompanyDetails == 1)
						eventTable.addCell(cell);

					cell = new Cell(1, 5)
							.add(new Paragraph().add(new Text("Mobile No : ").simulateBold()).add(new Text(cmpPhone))
									.setFont(basicFont).setFontSize(10).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT))
							.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
					if (isCompanyDetails == 1)
						eventTable.addCell(cell);

					cell = new Cell(1, 5)
							.add(new Paragraph().add(new Text("Email : ").simulateBold()).add(new Text(cmpEmail))
									.setFont(basicFont).setFontSize(10).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT))
							.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
					if (isCompanyDetails == 1)
						eventTable.addCell(cell);

					eventTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));
					eventTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));

					if (isPartyDetails == 1) {
						addLabelValueRow(eventTable, basicFont, blackColor, customerName, hostName, customerPhone,
								mobileNo, new DoubleBorder(3f));
					}

					addLabelValueRow(eventTable, basicFont, blackColor, eName, eventName.toUpperCase(), eDate,
							eventDate, null);
					addFoodNoteRow(eventTable, basicFont, blackColor, redColor, fNotes, foodNotes);
					addVenueRow(eventTable, basicFont, blackColor, eVenue, venue.toUpperCase());
				}

				if (!isFirstFunction) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}

				// Customer + venue row
				addLabelValueRow(eventTable, basicFont, blackColor, customerName, hostName, eVenue,
						eventFunctionDto.getFunctionVenue() != null ? eventFunctionDto.getFunctionVenue() : "",
						new SolidBorder(1f));

				// Function + persons row
				addLabelValueRow(eventTable, basicFont, blackColor, function,
						safeText(eventFunctionDto.getFunctionName().toUpperCase()), person,
						safeText(eventFunctionDto.getPax().toString()), null);

				// Date + time row
				String startTime = eventFunctionDto.getFunctionStartTimestamp() != null
						? eventFunctionDto.getFunctionStartTimestamp().split(" ")[1].substring(0, 5) + " "
								+ eventFunctionDto.getFunctionStartTimestamp().split(" ")[2]
						: "";
				String endTime = eventFunctionDto.getFunctionEndTimestamp() != null
						? eventFunctionDto.getFunctionEndTimestamp().split(" ")[1].substring(0, 5) + " "
								+ eventFunctionDto.getFunctionEndTimestamp().split(" ")[2]
						: "";

				cell = new Cell().add(
						new Paragraph(date).setFont(basicFont).setFontSize(14).setFontColor(blackColor).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);
				cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor))
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);
				cell = new Cell(1, 4)
						.add(new Paragraph(safeText(eventFunctionDto.getFunctionStartTimestamp().split(" ")[0]) + " "
								+ safeText(startTime).toUpperCase() + " To " + safeText(endTime).toUpperCase())
								.setFont(basicFont).setFontSize(14).setFontColor(blackColor))
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				document.add(eventTable);

				// ── Menu table with Chef column ──────────────────────────────────
				// 3 columns: Category (25%) | Items (50%) | Chef/Vendor (25%)
				for (MenuReportResponseDto menu : eventFunctionDto.getMenuCategories()) {

					// 2 cols: Category name (25%) | Inner table with Item+Chef (75%)
					Table catItemTable = new Table(UnitValue.createPercentArray(new float[] { 25f, 75f }));
					catItemTable.setWidth(UnitValue.createPercentValue(100));
					catItemTable.setBorder(Border.NO_BORDER);
					catItemTable.setMarginTop(8f);
					catItemTable.setKeepTogether(false);
					catItemTable.setBorderTop(new SolidBorder(1f));
					catItemTable.setBorderBottom(new SolidBorder(1f));

					// ── Col 1: Category name ─────────────────────────────────────
					Paragraph catPara = new Paragraph(
							menu.getNameEnglish() != null ? menu.getNameEnglish().toUpperCase() : "").setFont(basicFont)
							.setFontSize(16f).setFontColor(blackColor).setSplitCharacters((text, glyphPos) -> true)
							.setMarginLeft(5f).setPadding(0).simulateBold();

					Cell catCell = new Cell().add(catPara).setBorder(Border.NO_BORDER)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
					catPara.setProperty(Property.SPLIT_CHARACTERS, new DefaultSplitCharacters());

					if (isCategoryInstruction == 1 && menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
						catCell.add(new Paragraph(
								menuPreparationServiceImpl.formatText(" ( " + menu.getMenuNotes() + " )", lang))
								.setSplitCharacters((text, glyphPos) -> true).setFont(basicFont).setFontSize(12f)
								.setFontColor(blackColor).setMarginLeft(6).setMarginTop(2f)
								.setTextAlignment(TextAlignment.CENTER));
					}
					catItemTable.addCell(catCell);

					// ── Col 2 & 3: Nested table — each item row aligned with its chef name ──
					// Inner table: Item Name (65%) | Chef Name (35%) per row
					Table innerTable = new Table(UnitValue.createPercentArray(new float[] { 65f, 35f }))
							.useAllAvailableWidth().setBorder(Border.NO_BORDER);

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

						// Inner Col 1: Item name
						Cell innerItemCell = new Cell().setBorder(Border.NO_BORDER)
								.setVerticalAlignment(VerticalAlignment.MIDDLE).setPaddingTop(3f).setPaddingBottom(3f)
								.setPaddingLeft(12f);

						innerItemCell.add(
								new Paragraph(item.getNameEnglish() != null ? item.getNameEnglish().toUpperCase() : "")
										.setFont(basicFont).setSplitCharacters((text, glyphPos) -> true)
										.setFontSize(14f).setFontColor(blackColor).setPadding(0)
										.setTextAlignment(TextAlignment.LEFT));
						innerItemCell.setProperty(Property.SPLIT_CHARACTERS, new DefaultSplitCharacters());

						if (isItemInstruction == 1 && item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
							innerItemCell.add(new Paragraph(
									menuPreparationServiceImpl.formatText(" ( " + item.getItemNotes() + " ) ", lang))
									.setFont(basicFont).setSplitCharacters((text, glyphPos) -> true).setFontSize(12)
									.setFixedLeading(13f).setFontColor(blackColor).setMarginLeft(6).setMarginTop(2f)
									.setMarginBottom(0f).setPadding(0f).setTextAlignment(TextAlignment.LEFT));
						}

						// Inner Col 2: Chef name — same row as item, perfectly aligned
						String vendorName = (item.getPartyName() != null && !item.getPartyName().trim().isEmpty())
								? item.getPartyName()
								: "";

						Cell innerChefCell = new Cell().setBorder(Border.NO_BORDER)
								.setVerticalAlignment(VerticalAlignment.MIDDLE).setPaddingTop(3f).setPaddingBottom(3f);

						innerChefCell.add(
								new Paragraph(vendorName).setFont(basicFont).setFontSize(13f).setFontColor(brownColor)
										.simulateBold().setPadding(0).setTextAlignment(TextAlignment.LEFT));
						innerChefCell.setProperty(Property.SPLIT_CHARACTERS, new DefaultSplitCharacters());

						innerTable.addCell(innerItemCell);
						innerTable.addCell(innerChefCell);
					}

					// Single cell wrapping the inner table — spans col 2 of catItemTable
					catItemTable.addCell(
							new Cell().add(innerTable).setBorder(Border.NO_BORDER).setPadding(0).setKeepTogether(true));
					document.add(catItemTable);
				}

				isFirstFunction = false;
			}

			// ── Page numbers ─────────────────────────────────────────────────────
			int totalPages = pdfDocument.getNumberOfPages();
			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());
				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 22,
						TextAlignment.CENTER);
				canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"chef report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Chef Report", e);
		}
	}

	// ─── Private helpers (add to same class if not already present) ──────────────

	/** Label : Value | Label : Value — single row across 6 columns */
	private void addLabelValueRow(Table table, PdfFont font, Color color, String label1, String value1, String label2,
			String value2, Border topBorder) {
		Cell c;
		c = new Cell().add(new Paragraph(label1).setFont(font).setFontSize(14).setFontColor(color).simulateBold())
				.setPadding(0).setBorder(Border.NO_BORDER);
		if (topBorder != null)
			c.setBorderTop(topBorder);
		table.addCell(c);

		c = new Cell().add(new Paragraph(":").setFont(font).setFontSize(14).setFontColor(color)).setPadding(0)
				.setBorder(Border.NO_BORDER);
		if (topBorder != null)
			c.setBorderTop(topBorder);
		table.addCell(c);

		c = new Cell().add(new Paragraph(value1).setFont(font).setFontSize(14).setFontColor(color)).setPadding(0)
				.setBorder(Border.NO_BORDER);
		if (topBorder != null)
			c.setBorderTop(topBorder);
		table.addCell(c);

		c = new Cell().add(new Paragraph(label2).setFont(font).setFontSize(14).setFontColor(color).simulateBold())
				.setPadding(0).setBorder(Border.NO_BORDER);
		if (topBorder != null)
			c.setBorderTop(topBorder);
		table.addCell(c);

		c = new Cell().add(new Paragraph(":").setFont(font).setFontSize(14).setFontColor(color)).setPadding(0)
				.setBorder(Border.NO_BORDER);
		if (topBorder != null)
			c.setBorderTop(topBorder);
		table.addCell(c);

		c = new Cell().add(new Paragraph(value2).setFont(font).setFontSize(14).setFontColor(color)).setPadding(0)
				.setBorder(Border.NO_BORDER);
		if (topBorder != null)
			c.setBorderTop(topBorder);
		table.addCell(c);
	}

	/** Food Note row — value spans 4 cols with red bold text */
	private void addFoodNoteRow(Table table, PdfFont font, Color black, Color red, String label, String value) {
		table.addCell(
				new Cell().add(new Paragraph(label).setFont(font).setFontSize(14).setFontColor(black).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER));
		table.addCell(new Cell().add(new Paragraph(":").setFont(font).setFontSize(14).setFontColor(black)).setPadding(0)
				.setBorder(Border.NO_BORDER));
		table.addCell(
				new Cell(1, 4).add(new Paragraph(value).setFont(font).setFontSize(14).setFontColor(red).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER));
	}

	/** Venue row — value spans 4 cols */
	private void addVenueRow(Table table, PdfFont font, Color color, String label, String value) {
		table.addCell(
				new Cell().add(new Paragraph(label).setFont(font).setFontSize(14).setFontColor(color).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER));
		table.addCell(new Cell().add(new Paragraph(":").setFont(font).setFontSize(14).setFontColor(color)).setPadding(0)
				.setBorder(Border.NO_BORDER));
		table.addCell(new Cell(1, 4).add(new Paragraph(value).setFont(font).setFontSize(14).setFontColor(color))
				.setPadding(0).setBorder(Border.NO_BORDER));
	}

	@Override
	public String getMenuPlanningSimpleReport2(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid) {
		try {
			PdfFont basicFont = null;
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "", l1 = "",
					l2 = "", note = "";
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 500;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				customerName = "ग्राहक का नाम";
				customerPhone = "मोबाइल नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "दिनांक";
				fNotes = "भोजन विवरण";
				eVenue = "आयोजन स्थल";
				l1 = "व्यक्तियों के लिए";
				l2 = "बजे";
				note = "नोट";
				y = 497;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				System.out.println("Gujarati font loaded successfully");
				customerName = "ગ્રાહકનું નામ";
				customerPhone = "મોબાઇલ નંબર";
				eName = "કાર્યક્રમનું નામ";
				eDate = "કાર્યક્રમની તારીખ";
				fNotes = "ભોજન વિગતો";
				eVenue = "આયોજન સ્થળ";
				l1 = "વ્યક્તિઓ માટે";
				l2 = "વાગ્યે";
				note = "નોંધ";
				y = 495;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				System.out.println("English font loaded successfully");
				customerName = "Party Name";
				customerPhone = "Mobile No.";
				eName = "Event Name";
				eDate = "Event Date";
				fNotes = "Food Note";
				eVenue = "Venue";
				l1 = "Pax at";
				l2 = "for";
				note = "Note";
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);

			if (eventDto == null) {
				return "";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			File pdfFile = new File(outputPath + "/"
					+ getReportName(getPartyNameByEventId(eventId),
							formatDate(LocalDateTime.parse(eventDto.getEventStartTimestamp())), "simple report")
					+ ".pdf");
			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(40, 35, 50, 20);

			// Load all background images upfront
			ImageData mainBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
			ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setPageBackground(1, mainBgData); // Page 1: Main
			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			// Define colors
			Color detailsColor = new DeviceRgb(61, 55, 107);
			Color fnColor = new DeviceRgb(88, 23, 0);
			Color insColor = new DeviceRgb(104, 104, 104);
			Color cmpColor = new DeviceRgb(3, 50, 100);

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			String venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = eventDto.getFoodNotes();
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();
			String logo = eventDto.getLogo();

			if (foodNotes != null && !foodNotes.isEmpty()) {
				foodNotesName.append("( ");
				foodNotesName.append(foodNotes);
				foodNotesName.append(" )");
			}

			if (isCompanyDetails == 1) {
				String watermarkPath = environment.getProperty("app.image.url") + eventDto.getLogo();
				pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(watermarkPath));
			}

//			ImageData logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
//			Image img = new Image(logoData);
			Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));
			img.setWidth(190);
			img.setHorizontalAlignment(HorizontalAlignment.CENTER);
			img.setMarginTop(30);
			img.setMarginBottom(5);

			if (isCompanyDetails == 1) {
				document.add(img);
			}

			// Party Name
			Paragraph hostNameContentLabel = new Paragraph(customerName + ": ").setFont(basicFont)
					.setFontColor(ColorConstants.DARK_GRAY).setFontSize(14).setFixedPosition(x - 100, y, 400); // x, y,
																												// width
			document.add(hostNameContentLabel);

			Paragraph hostNameContent = new Paragraph(safeText(hostName)).setFont(basicFont).setFontColor(detailsColor)
					.setFontSize(14).setFixedPosition(x, y, 400); // x, y, width
			document.add(hostNameContent);

			// Mobile No.
			Paragraph mobileNoContentLabel = new Paragraph(customerPhone + ": ").setFont(basicFont)
					.setFontColor(ColorConstants.DARK_GRAY).setFontSize(14).setFixedPosition(x - 100, y -= 30, 400);
			document.add(mobileNoContentLabel);
			Paragraph mobileNoContent = new Paragraph(safeText(mobileNo)).setFont(basicFont).setFontColor(detailsColor)
					.setFontSize(14).setFixedPosition(x, y, 400);
			document.add(mobileNoContent);

			// Event Date
			Paragraph eventDateContentLabel = new Paragraph(eDate + ": ").setFont(basicFont)
					.setFontColor(ColorConstants.DARK_GRAY).setFontSize(14).setFixedPosition(x - 100, y -= 30, 400);
			document.add(eventDateContentLabel);
			Paragraph eventDateContent = new Paragraph(safeText(eventDate)).setFont(basicFont)
					.setFontColor(detailsColor).setFontSize(14).setFixedPosition(x, y, 400);
			document.add(eventDateContent);

			// Event Name
			Paragraph eventNameContentLabel = new Paragraph(eName + ": ").setFont(basicFont)
					.setFontColor(ColorConstants.DARK_GRAY).setFontSize(14).setFixedPosition(x - 100, y -= 27, 400);
			document.add(eventNameContentLabel);
			Paragraph eventNameContent = new Paragraph(safeText(eventName)).setFont(basicFont)
					.setFontColor(detailsColor).setFontSize(14).setFixedPosition(x, y, 400);
			document.add(eventNameContent);

			// Pax
//			Paragraph paxContent = new Paragraph(safeText(pax)).setFont(basicFont).setFontColor(detailsColor)
//					.setFontSize(14).setFixedPosition(x, y -= 30, 400);
//			document.add(paxContent);

			// Venue
			Paragraph venueContentLabel = new Paragraph(eVenue + ": ").setFont(basicFont)
					.setFontColor(ColorConstants.DARK_GRAY).setFontSize(14).setFixedPosition(x - 100, y -= 28, 400);
			document.add(venueContentLabel);
			Paragraph venueContent = new Paragraph(safeText(venue)).setFont(basicFont).setFontColor(detailsColor)
					.setFontSize(14).setFixedPosition(x, y, 400);
			document.add(venueContent);

			// Food
			Paragraph foodContentLabel = new Paragraph(fNotes + ": ").setFont(basicFont)
					.setFontColor(ColorConstants.DARK_GRAY).setFontSize(14).setFixedPosition(x - 100, y -= 28, 400);
			document.add(foodContentLabel);
			Paragraph foodContent = new Paragraph(safeText(foodNotesName.toString())).setFont(basicFont)
					.setFontColor(detailsColor).setFontSize(14).setFixedPosition(x, y, 400);
			document.add(foodContent);

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			Paragraph cmpNameContent = new Paragraph(safeText(cmpName.toString()))
					.setFont(PdfFontFactory.createFont(StandardFonts.TIMES_BOLD)).setFontColor(cmpColor).setFontSize(14)
					.setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 85, pageWidth);
			document.add(cmpNameContent);

			// ============ MENU CONTENT PAGES ============
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				// Always start each function on a new page
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				// Create main table with 2 columns for the header layout
				float[] columnWidths = { 100f };
				Table headerTable = new Table(UnitValue.createPercentArray(columnWidths));
				headerTable.setWidth(UnitValue.createPercentValue(100));
				headerTable.setBorder(Border.NO_BORDER);
				headerTable.setMarginTop(15f);

				// === ROW 1: Name (spans both columns) ===
				String functionTitle = menuPreparationServiceImpl
						.formatText(eventFunctionMasterResponseDto.getFunctionName(), lang);
				String funpax = eventFunctionMasterResponseDto.getPax() != null
						? eventFunctionMasterResponseDto.getPax().toString()
						: "0";
				String startTimeText = (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
						: "TBD");
				String date = startTimeText.split(" ")[0];
				String time = startTimeText.split(" ")[1] + " " + startTimeText.split(" ")[2];

				StringBuilder fnTitle = new StringBuilder();
				if (lang == 1 || lang == 2) {
					fnTitle.append(time);
					fnTitle.append(" " + l2 + " ");
					fnTitle.append(funpax);
					fnTitle.append(" " + l1 + " ");
					fnTitle.append(functionTitle);
				} else {
					fnTitle.append(functionTitle);
					fnTitle.append(" " + l2 + " ");
					fnTitle.append(funpax);
					fnTitle.append(" " + l1 + " ");
					fnTitle.append(time);
				}

				Paragraph funPara = new Paragraph().add(new com.itextpdf.layout.element.Text(fnTitle.toString())
						.setFont(basicFont).setFontColor(fnColor).setFontSize(22).setUnderline());
				Cell funCell = new Cell().add(funPara);
				funCell.setBorder(Border.NO_BORDER);
				funCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(funCell);

				document.add(headerTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				// Add menu content
				for (MenuReportResponseDto menu : menuReportResponseDtos) {

					Div menuContent = new Div();
					menuContent.setKeepTogether(true);

					Paragraph p = new Paragraph(menuPreparationServiceImpl.formatText(menu.getNameEnglish(), lang))
							.setFont(basicFont).setFontSize(20).setFixedLeading(21f).setFontColor(fnColor)
							.setTextAlignment(TextAlignment.CENTER).setUnderline();

					Table table = new Table(1).setWidth(UnitValue.createPercentValue(100));
					table.setBorder(Border.NO_BORDER);
					table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

					menuContent.add(table).setMarginTop(8f);

					// Category Slogan
					if (isCategorySlogan == 1) {
						if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
							menuContent.add(new Paragraph(menuPreparationServiceImpl.formatText(menu.getSlogan(), lang))
									.setFont(basicFont).setFontSize(12f).setMultipliedLeading(1.1f).setMarginBottom(2f)
									.setFontColor(insColor).setMarginLeft(6).setTextAlignment(TextAlignment.CENTER));
						}
					}

					// Category Instructions
					if (isCategoryInstruction == 1) {
						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
							menuContent
									.add(new Paragraph(menuPreparationServiceImpl.formatText(menu.getMenuNotes(), lang))
											.setFont(basicFont).setFontSize(12f).setMultipliedLeading(1.1f)
											.setFontColor(insColor).setMarginLeft(6)
											.setTextAlignment(TextAlignment.CENTER));
						}
					}

					if (isCategoryImage == 1) {
						// Category Image
						if (isCategoryImage != null && isCategoryImage == 1) {
							if (menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
								try {
									img = new Image(ImageDataFactory
											.create(environment.getProperty("app.image.url") + menu.getImagePath()));

									img.setAutoScale(true);
									img.setHorizontalAlignment(HorizontalAlignment.CENTER);
									img.setMarginTop(5);
									img.setMarginBottom(5);

									menuContent.add(img);
								} catch (Exception e) {
									// Skip if image loading fails
								}
							}
						}
					}

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

						menuContent
								.add(new Paragraph(menuPreparationServiceImpl.formatText(item.getNameEnglish(), lang))
										.setFont(basicFont).setFontSize(15f).setFixedLeading(16f)
										.setFontColor(detailsColor).setMarginLeft(12)
										.setTextAlignment(TextAlignment.CENTER));

						if (isItemSlogan == 1) {
							if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
								menuContent.add(
										new Paragraph(menuPreparationServiceImpl.formatText(item.getSlogan(), lang))
												.setFont(basicFont).setFontSize(12f).setMultipliedLeading(1.1f)
												.setFontColor(insColor).setMarginLeft(18).setMarginTop(-4f)
												.setMarginBottom(2f).setPadding(0f)
												.setTextAlignment(TextAlignment.CENTER));
							}
						}

						// Item Instructions
						if (isItemInstruction == 1) {
							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
								menuContent.add(
										new Paragraph(menuPreparationServiceImpl.formatText(item.getItemNotes(), lang))
												.setFont(basicFont).setFontSize(12f).setMultipliedLeading(1.1f)
												.setFontColor(insColor).setMarginLeft(18).setMarginTop(-4f)
												.setMarginBottom(0f).setPadding(0f)
												.setTextAlignment(TextAlignment.CENTER));
							}
						}

					}

					document.add(menuContent);
				}
			}

			if (isCompanyDetails == 1) {

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				// ============ LAST PAGE ============

				img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));

				img.setWidth(320);
				img.setHorizontalAlignment(HorizontalAlignment.CENTER);
				img.setMarginTop(85);
				img.setMarginBottom(5);

				document.add(img);

				// Set the background for the last page BEFORE creating it
				int lastPageNum = pdfDocument.getNumberOfPages() + 1;
				bgHandler.setPageBackground(lastPageNum, watermarkBgData);

				Paragraph moreinfoContent = new Paragraph("FOR MORE INFO CONTACT US")
						.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontColor(cmpColor)
						.setFontSize(24).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y += 55, pageWidth);
				document.add(moreinfoContent);

				ImageData lineData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/line.png");
				Image line = new Image(lineData);

				line.scaleToFit(120, 120);

				float lineWidth = line.getImageScaledWidth();
				float lineX = (pageWidth - lineWidth) / 2;

				float lineY = y - 40; // distance below text

				line.setFixedPosition(lineX, lineY);

				document.add(line);

				Paragraph usrNameContent = new Paragraph(
						userFirstName + " " + userLastName + ": " + countryCode + " " + cmpPhone.toString())
						.setFont(PdfFontFactory.createFont(StandardFonts.TIMES_BOLD)).setFontColor(cmpColor)
						.setFontSize(22).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 45, pageWidth);
				document.add(usrNameContent);

				Paragraph emailContent = new Paragraph("Email: " + email)
						.setFont(PdfFontFactory.createFont(StandardFonts.TIMES_BOLD)).setFontColor(cmpColor)
						.setFontSize(22).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 30, pageWidth);
				document.add(emailContent);

				Paragraph cmpAddressContent = new Paragraph(cmpAddress.toString())
						.setFont(PdfFontFactory.createFont(StandardFonts.TIMES_BOLD)).setFontColor(cmpColor)
						.setFontSize(22).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 30, pageWidth);
				document.add(cmpAddressContent);

			}
			/* ================= PAGE NUMBERS ================= */
			document.flush();

			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 22,
						TextAlignment.CENTER);

				canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/"
					+ getReportName(getPartyNameByEventId(eventId),
							formatDate(LocalDateTime.parse(eventDto.getEventStartTimestamp())), "simple report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String getMenuPlanningSimpleReport3(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isUserLogo, Integer isUserDetails, HttpServletRequest re, int lang, Long userid,
			Integer isPartyDetails) {
		try {
			PdfFont basicFont = null;
			PdfFont basicFontBold = null;
			menuPreparationServiceImpl.loadLicense();

			if (lang == 1) {
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				basicFontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
			} else if (lang == 2) {
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				basicFontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Regular.ttf");
					basicFontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Bold.ttf");
				} else if (language.equalsIgnoreCase("Telugu")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					basicFontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Bold.ttf");
				} else if (language.equalsIgnoreCase("Malayalam")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					basicFontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Bold.ttf");
				} else if (language.equalsIgnoreCase("Marathi")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
					basicFontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				} else {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					basicFontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				}
			} else {
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
//				basicFontBold = menuPreparationServiceImpl.loadFont("/fonts/timesbd.ttf");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				basicFontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			}

			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);

			if (eventDto == null)
				return "";

			String cmpName = eventDto.getCmpName();
			String cmpAddress = eventDto.getCmpAddress();
			String cmpPhone = eventDto.getCmpPhone();

			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");
			if (!outputPath.exists())
				outputPath.mkdirs();

			File pdfFile = new File(outputPath + "/"
					+ getReportName(getPartyNameByEventId(eventId),
							formatDate(LocalDateTime.parse(eventDto.getEventStartTimestamp())), "back office report")
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);

			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(25, 35, 25, 35);

			Color black = ColorConstants.BLACK;
			Color gray = new DeviceRgb(90, 90, 90);
			Color navyBlue = new DeviceRgb(30, 30, 100);
			Color deepRose = new DeviceRgb(140, 20, 60);

			/* ================= HEADER ================= */
			float[] infoColumnWidths = { 150f, 440f };

			Table infoTable = new Table(infoColumnWidths);
			infoTable.setWidth(UnitValue.createPercentValue(100));
			infoTable.setMarginBottom(30);

			System.err.println("is UserLogo--->" + isUserLogo);

			if (isUserLogo == 1) {
				String watermarkPath = null;
				watermarkPath = environment.getProperty("app.image.url") + eventDto.getLogo();
//				watermarkPath = "/flipbook/pages/Blankpage.png";

				pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(watermarkPath));
			}

			if (isUserLogo == 1 && eventDto.getLogo() != null) {
				ImageData logoData = null;
				logoData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + eventDto.getLogo()); // e.g.
//				logoData = menuPreparationServiceImpl
//						.loadImageFromResource("/flipbook/pages/logo.png"); // e.g.
				Image logo = new Image(logoData);

				// Resize & align
				logo.setWidth(120);
				logo.setAutoScale(false);
				logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

				// Add to document
				infoTable.addCell(new Cell(3, 1).add(logo).setBorder(Border.NO_BORDER).setPaddingBottom(10f));

				if (isUserDetails == 0) {
					infoTable.addCell(new Cell().setBorder(Border.NO_BORDER));
					infoTable.addCell(new Cell().setBorder(Border.NO_BORDER));
					infoTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				}
			}

			if (isUserDetails == 1) {
				Cell cell;

				if (isUserLogo == 0) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(new Paragraph(safeText(cmpName)).setFont(basicFont).setFontSize(15).setFontColor(black)
						.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
				infoTable.addCell(cell);

				if (isUserLogo == 0) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(new Paragraph(safeText(cmpPhone)).setFont(basicFont).setFontSize(15).setFontColor(black)
						.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);

				infoTable.addCell(cell);

				if (isUserLogo == 0) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(new Paragraph(safeText(cmpAddress)).setFont(basicFont).setFontSize(15).setFontColor(black)
						.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER).setPaddingBottom(10f);

				infoTable.addCell(cell);
			}
			infoTable.addCell(new Cell().add(new Paragraph("Party Name").setFont(basicFontBold).setFontSize(14))
					.setBorder(Border.NO_BORDER).setPaddingTop(2).setPaddingBottom(1).setPaddingLeft(0)
					.setPaddingRight(0));

			infoTable.addCell(new Cell()
					.add(new Paragraph(":- " + safeText(eventDto.getPartyName())).setFont(basicFontBold)
							.setFontSize(14))
					.setBorder(Border.NO_BORDER).setPaddingTop(2).setPaddingBottom(1).setPaddingLeft(0)
					.setPaddingRight(0));

			infoTable.addCell(new Cell().add(new Paragraph("Date").setFont(basicFontBold).setFontSize(14))
					.setBorder(Border.NO_BORDER).setPaddingTop(2).setPaddingBottom(1).setPaddingLeft(0)
					.setPaddingRight(0));

			infoTable.addCell(new Cell()
					.add(new Paragraph(":- " + safeText(eventDto.getEventStartTimestamp())).setFont(basicFontBold)
							.setFontSize(14))
					.setBorder(Border.NO_BORDER).setPaddingTop(2).setPaddingBottom(1).setPaddingLeft(0)
					.setPaddingRight(0));

			infoTable.addCell(new Cell().add(new Paragraph("Event").setFont(basicFontBold).setFontSize(14))
					.setBorder(Border.NO_BORDER));

			infoTable.addCell(new Cell().add(
					new Paragraph(":- " + safeText(eventDto.getEventName())).setFont(basicFontBold).setFontSize(14))
					.setBorder(Border.NO_BORDER));

			infoTable.addCell(new Cell().add(new Paragraph("Venue").setFont(basicFontBold).setFontSize(14))
					.setBorder(Border.NO_BORDER).setPaddingTop(2).setPaddingBottom(1).setPaddingLeft(0)
					.setPaddingRight(0));

			infoTable.addCell(new Cell()
					.add(new Paragraph(":- " + safeText(eventDto.getVenue())).setFont(basicFontBold).setFontSize(14))
					.setBorder(Border.NO_BORDER).setPaddingTop(2).setPaddingBottom(1).setPaddingLeft(0)
					.setPaddingRight(0));

			document.add(infoTable);

			/* ================= MENU CONTENT ================= */

			boolean firstFunction = true;

			for (EventFunctionReportResponseDto functionDto : eventDto.getFunctions()) {

				String startTime = functionDto.getFunctionStartTimestamp();
				String functionDate = "";
				String functionTime = "";

				if (startTime != null && startTime.contains(" ")) {
					String[] split = startTime.split(" ");
					functionDate = split[0];
					functionTime = split[1] + " " + split[2];
				}

				String functionName = menuPreparationServiceImpl.formatText(functionDto.getFunctionName(), lang);

				String paxText = functionDto.getPax() != null ? functionDto.getPax() + " Members" : "0 Members";

				if (!firstFunction) {
					document.add(new Paragraph("").setMarginTop(10));
				}
				firstFunction = false;

				/* ========= FUNCTION INFO (COLUMN BASED) ========= */

				Table functionInfoTable = new Table(infoColumnWidths);
				functionInfoTable.setWidth(UnitValue.createPercentValue(100));
				functionInfoTable.setMarginBottom(4);

				functionInfoTable.addCell(new Cell().add(new Paragraph("Date").setFont(basicFontBold).setFontSize(14))
						.setBorder(Border.NO_BORDER).setPaddingTop(5).setPaddingBottom(1).setPaddingLeft(0)
						.setPaddingRight(0));

				functionInfoTable.addCell(new Cell()
						.add(new Paragraph(":- " + safeText(functionDate)).setFont(basicFontBold).setFontSize(14))
						.setBorder(Border.NO_BORDER).setPaddingTop(5).setPaddingBottom(1).setPaddingLeft(0)
						.setPaddingRight(0));

				functionInfoTable.addCell(
						new Cell().add(new Paragraph(safeText(functionName)).setFont(basicFontBold).setFontSize(14))
								.setBorder(Border.NO_BORDER).setPaddingTop(2).setPaddingBottom(1).setPaddingLeft(0)
								.setPaddingRight(0));

				functionInfoTable.addCell(new Cell()
						.add(new Paragraph(":- " + safeText(eventDto.getFoodType()) + " (" + safeText(paxText)
								+ ") (Time " + safeText(functionTime) + ")").setFont(basicFontBold).setFontSize(14))
						.setBorder(Border.NO_BORDER).setPaddingTop(2).setPaddingBottom(1).setPaddingLeft(0)
						.setPaddingRight(0));

				document.add(functionInfoTable);

				/* ========= SMALL GAP BEFORE MENU START ========= */
				document.add(new Paragraph("").setMarginBottom(4));

				/* ========= MENU TABLE ========= */

				Table menuTable = new Table(infoColumnWidths);
				menuTable.setWidth(UnitValue.createPercentValue(100));
				menuTable.setMarginTop(0);
				menuTable.setMarginBottom(0);

				for (MenuReportResponseDto menu : functionDto.getMenuCategories()) {

					String categoryName = menuPreparationServiceImpl.formatText(menu.getNameEnglish(), lang);

					StringBuilder itemsBuilder = new StringBuilder();
					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						String itemName = menuPreparationServiceImpl.formatText(item.getNameEnglish(), lang);
						itemsBuilder.append(itemName).append(", ");
					}

					if (itemsBuilder.length() > 2) {
						itemsBuilder.setLength(itemsBuilder.length() - 2);
					}

					Paragraph categoryPara = new Paragraph(categoryName).setFont(basicFontBold).setFontSize(14)
							.setFontColor(deepRose).setTextAlignment(TextAlignment.LEFT).setMargin(0)
							.setMultipliedLeading(1.05f);

					Cell categoryCell = new Cell().add(categoryPara).setBorder(Border.NO_BORDER).setPaddingRight(6)
							.setPaddingTop(1).setPaddingBottom(1);

					/* ----- ITEMS CELL (:- STARTS ITEMS) ----- */
					Paragraph itemsPara = new Paragraph(":- " + itemsBuilder.toString()).setFont(basicFontBold)
							.setFontSize(14).setMargin(0).setFontColor(navyBlue).setMultipliedLeading(1.05f);

					Cell itemsCell = new Cell().add(itemsPara).setBorder(Border.NO_BORDER).setPaddingTop(3)
							.setPaddingBottom(3);

					menuTable.addCell(categoryCell);
					menuTable.addCell(itemsCell);
				}

				document.add(menuTable);
			}

//			document.add(new Paragraph("\nPage 1 of 1").setFont(basicFont).setFontSize(9).setFontColor(gray)
//					.setTextAlignment(TextAlignment.CENTER));

			document.close();

			/* ================= RETURN URL ================= */

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/"
					+ getReportName(getPartyNameByEventId(eventId),
							formatDate(LocalDateTime.parse(eventDto.getEventStartTimestamp())), "back office report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String getMenuPlanningSimpleReport4(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid) {
		try {
			PdfFont basicFont = null;
			String eName = "", eDate = "", fNotes = "", eVenue = "";
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				eName = "कार्यक्रम का नाम";
				eDate = "दिनांक";
				fNotes = "भोजन विवरण";
				eVenue = "आयोजन स्थल";
				y = 512;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				System.out.println("Gujarati font loaded successfully");
				eName = "કાર્યક્રમનું નામ";
				eDate = "કાર્યક્રમની તારીખ";
				fNotes = "ભોજન વિગતો";
				eVenue = "આયોજન સ્થળ";
				y = 510;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				System.out.println("English font loaded successfully");
				eName = "TYPE OF EVENTS";
				eDate = "DATE";
				fNotes = "FOOD STATUS";
				eVenue = "VENUE";
			}

			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);
			if (eventDto == null) {
				return "";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			File pdfFile = new File(outputPath + "/"
					+ getReportName(getPartyNameByEventId(eventId),
							formatDate(LocalDateTime.parse(eventDto.getEventStartTimestamp())), "simple report")
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 50, 50, 50);

			// Load all background images upfront
			ImageData mainBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
			ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			if (isCompanyDetails == 1) {
				String watermarkPath = environment.getProperty("app.image.url") + eventDto.getLogo();
				pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(watermarkPath));
			}

			Color mainColor = new DeviceRgb(10, 43, 115);

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			String venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = eventDto.getFoodNotes();
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();

			Table eventTable = new Table(UnitValue.createPercentArray(new float[] { 32f, 1.5f, 32f, 32f, 1.5f, 32f }));
			eventTable.setWidth(UnitValue.createPercentValue(100));
			eventTable.setBorder(Border.NO_BORDER);
			eventTable.setBorderBottom(new SolidBorder(mainColor, 1f));
//			eventTable.setMarginTop(15f);

			ImageData logoData = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + eventDto.getLogo()); // e.g.

//			ImageData logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");

			Image logo = new Image(logoData);

			// Resize & align
			logo.setWidth(100);
			logo.setAutoScale(false);
			logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

			cell = new Cell(3, 1).add(logo).setBorder(Border.NO_BORDER).setPaddingBottom(10f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE);
			// Add to document
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph(safeText(cmpName)).setFont(basicFont).setFontSize(12).setFontColor(mainColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPaddingLeft(10f).setPaddingTop(0).setPaddingBottom(0).setBorder(Border.NO_BORDER);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph(safeText(cmpPhone)).setFont(basicFont).setFontSize(12).setFontColor(mainColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPaddingLeft(10f).setPaddingTop(0).setPaddingBottom(0).setBorder(Border.NO_BORDER);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph(safeText(cmpAddress)).setFont(basicFont).setFontSize(14).setFontColor(mainColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPaddingLeft(10f).setPaddingTop(0).setPaddingBottom(0).setBorder(Border.NO_BORDER)
					.setPaddingBottom(10f);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			Border topBorder = new SolidBorder(mainColor, 1f);

			cell = new Cell().add(new Paragraph(eDate).setFont(basicFont).simulateBold().setFontSize(12)
					.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER)
					.setBorderTop(topBorder);
			eventTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(":").setFont(basicFont).simulateBold().setFontSize(12).setFontColor(mainColor))
					.setBorder(Border.NO_BORDER).setBorderTop(topBorder);
			eventTable.addCell(cell);

			cell = new Cell(1, 4)
					.add(new Paragraph(safeText(eventDate)).setFont(basicFont).simulateBold().setFontSize(12)
							.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER).setBorderTop(topBorder);
			eventTable.addCell(cell);

			cell = new Cell().add(new Paragraph(eVenue).setFont(basicFont).simulateBold().setFontSize(12)
					.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).simulateBold().setFontSize(12)
					.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell(1, 4).add(new Paragraph(safeText(venue)).setFont(basicFont).simulateBold().setFontSize(12)
					.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell().add(new Paragraph(eName).setFont(basicFont).simulateBold().setFontSize(12)
					.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).simulateBold().setFontSize(12)
					.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell(1, 4).add(new Paragraph(safeText(eventName)).setFont(basicFont).simulateBold()
					.setFontSize(12).setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

//			cell = new Cell().add(new Paragraph("NO OF PERSONS").setFont(basicFont).simulateBold().setFontSize(12)
//					.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
//			eventTable.addCell(cell);

//			cell = new Cell().add(new Paragraph(":").setFont(basicFont).simulateBold().setFontSize(12)
//					.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
//			eventTable.addCell(cell);

//			cell = new Cell(1, 4).add(new Paragraph(safeText(pax)).setFont(basicFont).simulateBold().setFontSize(12)
//					.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
//			eventTable.addCell(cell);

			cell = new Cell().add(new Paragraph(fNotes).setFont(basicFont).simulateBold().setFontSize(12)
					.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).simulateBold().setFontSize(12)
					.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell(1, 4).add(new Paragraph(safeText(foodNotes)).setFont(basicFont).simulateBold()
					.setFontSize(12).setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			document.add(eventTable);

			// ============ MENU CONTENT PAGES ============
			boolean isFirstFunction = true;
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				// Always start each function on a new page
				if (!isFirstFunction) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}

				// Create main table with 2 columns for the header layout
				float[] columnWidths = { 100f };
				Table headerTable = new Table(UnitValue.createPercentArray(columnWidths));
				headerTable.setWidth(UnitValue.createPercentValue(100));
				headerTable.setBorder(Border.NO_BORDER);
				headerTable.setMarginTop(15f);

				// === ROW 1: Name (spans both columns) ===
				String functionTitle = menuPreparationServiceImpl
						.formatText(eventFunctionMasterResponseDto.getFunctionName(), lang);
				String funpax = eventFunctionMasterResponseDto.getPax() != null
						? eventFunctionMasterResponseDto.getPax().toString()
						: "0";
				String startTimeText = (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
						: "TBD");
				String date = startTimeText.split(" ")[0];
				String time = startTimeText.split(" ")[1] + " " + startTimeText.split(" ")[2];

				StringBuilder fnTitle = new StringBuilder();
				fnTitle.append(functionTitle);
				fnTitle.append(" for ");
				fnTitle.append(funpax);
				fnTitle.append(" Pax at ");
				fnTitle.append(time);

				Paragraph funPara = new Paragraph().add(new com.itextpdf.layout.element.Text(fnTitle.toString())
						.setFont(basicFont).setFontColor(mainColor).setFontSize(22).setUnderline().simulateBold());
				Cell funCell = new Cell().add(funPara);
				funCell.setBorder(Border.NO_BORDER);
				funCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(funCell);

				document.add(headerTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				// Add menu content
				for (MenuReportResponseDto menu : menuReportResponseDtos) {

					Div menuContent = new Div();
					menuContent.setKeepTogether(true);
					Paragraph p = new Paragraph(menuPreparationServiceImpl.formatText(menu.getNameEnglish(), lang))
							.setFont(basicFont).setFontSize(20).setFixedLeading(21f).setFontColor(mainColor)
							.setTextAlignment(TextAlignment.LEFT).setUnderline().setMarginLeft(5f).simulateBold();

					Table table = new Table(1).setWidth(UnitValue.createPercentValue(100));
					table.setBorder(Border.NO_BORDER);
					table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

					menuContent.add(table).setMarginTop(8f);

					// Category Slogan
					if (isCategorySlogan == 1) {
						if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
							menuContent.add(new Paragraph(menuPreparationServiceImpl.formatText(menu.getSlogan(), lang))
									.setFont(basicFont).setFontSize(12f).setFixedLeading(16f).setFontColor(mainColor)
									.setMarginLeft(6).setTextAlignment(TextAlignment.LEFT));
						}
					}

					// Category Instructions
					if (isCategoryInstruction == 1) {
						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
							menuContent
									.add(new Paragraph(menuPreparationServiceImpl.formatText(menu.getMenuNotes(), lang))
											.setFont(basicFont).setFontSize(12f).setFixedLeading(16f)
											.setFontColor(mainColor).setMarginLeft(6)
											.setTextAlignment(TextAlignment.LEFT));
						}
					}

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						menuContent
								.add(new Paragraph(menuPreparationServiceImpl.formatText(item.getNameEnglish(), lang))
										.setFont(basicFont).setFontSize(15f).setFixedLeading(16f)
										.setFontColor(mainColor).setMarginLeft(12)
										.setTextAlignment(TextAlignment.LEFT));

						if (isItemSlogan == 1) {
							if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
								menuContent.add(
										new Paragraph(menuPreparationServiceImpl.formatText(item.getSlogan(), lang))
												.setFont(basicFont).setFontSize(12f).setFixedLeading(13f)
												.setFontColor(mainColor).setMarginLeft(18).setMarginTop(-4f)
												.setMarginBottom(0f).setPadding(0f)
												.setTextAlignment(TextAlignment.LEFT));
							}
						}

						// Item Instructions
						if (isItemInstruction == 1) {
							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
								menuContent.add(
										new Paragraph(menuPreparationServiceImpl.formatText(item.getItemNotes(), lang))
												.setFont(basicFont).setFontSize(12f).setFixedLeading(13f)
												.setFontColor(mainColor).setMarginLeft(18).setMarginTop(-4f)
												.setMarginBottom(0f).setPadding(0f)
												.setTextAlignment(TextAlignment.LEFT));
							}
						}

					}

					document.add(menuContent);
				}
				isFirstFunction = false;
			}

			document.flush();

			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 22,
						TextAlignment.CENTER);

				canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/"
					+ getReportName(getPartyNameByEventId(eventId),
							formatDate(LocalDateTime.parse(eventDto.getEventStartTimestamp())), "simple report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	private Cell blankCell(float minHeight) {
		return new Cell().add(new Paragraph("\u00A0")) // non-breaking space
				.setMinHeight(minHeight).setBorder(Border.NO_BORDER);
	}

	@Override
	public String getMenuPlanningSimpleReport5(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isUserLogo, Integer isUserDetails, HttpServletRequest re, int lang, Long userid,
			Integer isPartyDetails, Integer isTermsCondition, Integer isExtraCharges, Integer isAdvancePayment) {

		try {
			/* ================= LICENSE ================= */
			menuPreparationServiceImpl.loadLicense();

			/* ================= FONTS ================= */
			PdfFont fontRegular;
			PdfFont fontBold;
			PdfFont cmpFont;
			PdfFont cmpFontBold;
			String function = "", person = "", date = "", eTime = "";
			String cnm = "Customer Name";
			String mno = "Mobile No.";
			String enm = "Event Name";
			String edt = "Event Date";
			String venue = "Venue";
			String eventNotes = "Event Notes";
			String billingNameLabel = "Billing Name";
			String foodNotesLabel = "Food Note";
			String serviceLabel = "Service";
			String themeLabel = "Theme";
			String note = "Note";
			String pckPrice = "Package Price";
			String price = "Price";
			float itemLine = 0.8f;
			float catLine = 0.9f;

			if (lang == 1) {
				fontRegular = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				function = "कार्यक्रम";
				person = "मेंबर्स";
				eTime = "समय";
				date = "दिनांक";
				cnm = "ग्राहक का नाम";
				mno = "मोबाइल नंबर";
				enm = "कार्यक्रम का नाम";
				edt = "दिनांक";
				venue = "आयोजन स्थान";
				eventNotes = "कार्यक्रम की नोट";
				itemLine = 0.9f;
				billingNameLabel = "बिलिंग नाम";
				foodNotesLabel = "नोट्स";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				note = "नोट";
				price = "मूल्य";
				pckPrice = "पैकेज मूल्य";
				catLine = 1f;
			} else if (lang == 2) {

				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {
					fontRegular = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Regular.ttf");
					fontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Bold.ttf");

					function = "நிகழ்ச்சி";
					person = "நபர்";
					eTime = "நேரம்";
					date = "தேதி";
					cnm = "வாடிக்கையாளர் பெயர்";
					mno = "மொபைல் எண்";
					enm = "நிகழ்ச்சி பெயர்";
					edt = "நிகழ்ச்சி தேதி";
					venue = "நிகழ்வு இடம்";
					eventNotes = "நிகழ்வு குறிப்புகள்";
					billingNameLabel = "பில்லிங் பெயர்";
					foodNotesLabel = "உணவு குறிப்புகள்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					note = "குறிப்பு";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
				} else if (language.equalsIgnoreCase("Telugu")) {
					fontRegular = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					fontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					function = "కార్యక్రమం";
					person = "వ్యక్తి";
					eTime = "సమయం";
					date = "తేదీ";
					cnm = "కస్టమర్ పేరు";
					mno = "మొబైల్ నంబర్";
					enm = "కార్యక్రమ పేరు";
					edt = "కార్యక్రమ తేదీ";
					venue = "కార్యక్రమ స్థలం";
					eventNotes = "ఈవెంట్ గమనికలు";
					billingNameLabel = "బిల్లింగ్ పేరు";
					foodNotesLabel = "ఆహార గమనికలు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					note = "గమనిక";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					fontRegular = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					fontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					function = "പരിപാടി";
					person = "വ്യക്തി";
					eTime = "സമയം";
					date = "തീയതി";
					cnm = "ഉപഭോക്താവിന്റെ പേര്";
					mno = "മൊബൈൽ നമ്പർ";
					enm = "പരിപാടിയുടെ പേര്";
					edt = "പരിപാടിയുടെ തീയതി";
					venue = "പരിപാടി സ്ഥലം";
					eventNotes = "ഇവന്റ് കുറിപ്പുകൾ";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					foodNotesLabel = "ഭക്ഷണ കുറിപ്പുകൾ";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					note = "കുറിപ്പ്";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
				} else if (language.equalsIgnoreCase("Marathi")) {
					fontRegular = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
					fontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");

					function = "कार्यक्रम";
					person = "व्यक्ती";
					eTime = "वेळ";
					date = "दिनांक";
					cnm = "ग्राहकाचे नाव";
					mno = "मोबाईल नंबर";
					enm = "कार्यक्रमाचे नाव";
					edt = "कार्यक्रमाची दिनांक";
					venue = "आयोजन स्थळ";
					eventNotes = "कार्यक्रम नोंदी";
					billingNameLabel = "बिलिंग नाव";
					foodNotesLabel = "अन्न नोंदी";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					note = "नोंदी";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
				} else {
					fontRegular = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					fontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					function = "કાર્યક્રમ";
					person = "વ્યક્તિ";
					eTime = "સમય";
					date = "તારીખ";
					cnm = "ગ્રાહકનું નામ";
					mno = "મોબાઇલ નંબર";
					enm = "કાર્યક્રમનું નામ";
					edt = "કાર્યક્રમની તારીખ";
					venue = "આયોજન સ્થળ";
					eventNotes = "કાર્યક્રમની નોંધ";
					billingNameLabel = "બિલિંગ નામ";
					foodNotesLabel = "ભોજન નોંધ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					note = "નોંધ";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
				}
			} else {
				fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				function = "Function";
				person = "Persons";
				eTime = "Time";
				date = "Date";
				itemLine = 1f;
				catLine = 1.1f;
			}
			cmpFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
			cmpFontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			Color blackColor = ColorConstants.BLACK;
			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);

			if (eventDto == null)
				return "";

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);

			if (eventData == null) {
				throw new RuntimeException("MenuQuantityReponseDto is null for eventId=" + eventId);
			}

			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

			File dir = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");
			if (!dir.exists())
				dir.mkdirs();

			PdfWriter writer = new PdfWriter(new File(dir, "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "back office report") + ".pdf"));

			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, PageSize.A4);

			if (isUserDetails == 1) {
				String watermarkPath = environment.getProperty("app.image.url") + eventDto.getLogo();
				pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(watermarkPath));
			}

			float headerHeight = 210f;
			document.setMargins(headerHeight + 40, 28, 40, 28);

			String logoUrl = eventDto.getLogo();
			ImageData tempLogo = null;
			System.out.println("logoUrl : " + logoUrl);
			if (isUserDetails == 1 && logoUrl != null && !logoUrl.isEmpty()) {
				tempLogo = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + logoUrl);

			}

			final ImageData logoImage = tempLogo;
			final String cnm2 = cnm;
			final String mno2 = mno;
			final String enm2 = enm;
			final String edt2 = edt;
			final String venue2 = venue;
			final String enotes = eventNotes;
			final String billName = billingNameLabel;
			final String fNotesLabel = foodNotesLabel;
			final String sLabel = serviceLabel;
			final String thLabel = themeLabel;

			Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 100f }))
					.setBorder(Border.NO_BORDER);

			headerTable.setWidth(UnitValue.createPercentValue(100f));

			Table companyTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 80f }));

			companyTable.setWidth(UnitValue.createPercentValue(100f));

			Cell logoCell = new Cell().setBorder(Border.NO_BORDER);

			if (isUserDetails == 1 && logoImage != null) {

				try {

					Image logo = new Image(logoImage);
					logo.setWidth(100);
					logo.setHeight(60);

					logoCell.add(logo);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			companyTable.addCell(logoCell);

			Cell companyCell = new Cell().setBorder(Border.NO_BORDER);

			if (isUserDetails == 1) {

				companyCell.add(new Paragraph(eventData.getCompanyName() != null ? eventData.getCompanyName() : "")
						.setFont(cmpFontBold).setFontSize(18));

				companyCell.add(new Paragraph(
						"Mobile No. : " + (eventData.getOfficeNo() != null ? eventData.getOfficeNo() : ""))
						.setFont(cmpFont).setFontSize(13));

				companyCell.add(new Paragraph(
						"Email : " + (eventData.getCompanyEmail() != null ? eventData.getCompanyEmail() : ""))
						.setFont(cmpFont).setFontSize(13));
			}

			companyTable.addCell(companyCell);

			headerTable.addCell(new Cell().setBorder(Border.NO_BORDER).setPaddingBottom(5).add(companyTable));

			String billingName = "";
			String foodNotes = "";
			String service = "";
			String theme = "";
			String foodName = eventDto.getFoodType();

			if (lang == 1) {

				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";

				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";

				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";

				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";

			} else if (lang == 2) {

				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";

				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";

				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";

				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";

			} else {

				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";

				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";

				service = eventDto.getService() != null ? eventDto.getService() : "";

				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
			}

			Table eventTable = new Table(UnitValue.createPercentArray(new float[] { 23f, 27f, 23f, 27f }));

			eventTable.setWidth(UnitValue.createPercentValue(100f));
			eventTable.setFixedLayout();
			eventTable.setBorder(new SolidBorder(1f));

			/* PARTY DETAILS */
			if (isPartyDetails == 1) {

				eventTable.addCell(new Cell().add(new Paragraph(cnm2 + " :").setFont(fontBold).setFontSize(14))
						.setBorder(Border.NO_BORDER));

				eventTable.addCell(
						new Cell().add(new Paragraph(eventData.getPartyName() != null ? eventData.getPartyName() : "")
								.setFont(fontRegular).setFontSize(14)).setBorder(Border.NO_BORDER));

				eventTable.addCell(new Cell().add(new Paragraph(billName + " :").setFont(fontBold).setFontSize(14))
						.setBorder(Border.NO_BORDER));

				eventTable.addCell(new Cell().add(new Paragraph(billingName).setFont(fontRegular).setFontSize(14))
						.setBorder(Border.NO_BORDER));

				eventTable.addCell(new Cell().add(new Paragraph(mno2 + " :").setFont(fontBold).setFontSize(14))
						.setBorder(Border.NO_BORDER));

				eventTable.addCell(new Cell()
						.add(new Paragraph(eventData.getPartyMobile() != null ? eventData.getPartyMobile() : "")
								.setFont(fontRegular).setFontSize(14))
						.setBorder(Border.NO_BORDER));
			}

			/* EVENT NAME */
			eventTable.addCell(new Cell().add(new Paragraph(enm2 + " :").setFont(fontBold).setFontSize(14))
					.setBorder(Border.NO_BORDER));

			eventTable.addCell(
					new Cell().add(new Paragraph(eventData.getEventName() != null ? eventData.getEventName() : "")
							.setFont(fontRegular).setFontSize(14)).setBorder(Border.NO_BORDER));

			/* EVENT DATE */
			eventTable.addCell(new Cell().add(new Paragraph(edt2 + " :").setFont(fontBold).setFontSize(14))
					.setBorder(Border.NO_BORDER));

			eventTable.addCell(
					new Cell().add(new Paragraph(eventData.getEventDate() != null ? eventData.getEventDate() : "")
							.setFont(fontRegular).setFontSize(14)).setBorder(Border.NO_BORDER));

			String eventVenue = eventData.getVenueName() != null ? eventData.getVenueName() : "";

			if (eventVenue.trim().length() != 0) {
				/* VENUE */
				eventTable.addCell(new Cell().add(new Paragraph(venue2 + " :").setFont(fontBold).setFontSize(14))
						.setBorder(Border.NO_BORDER));

				eventTable.addCell(new Cell().add(new Paragraph(eventVenue).setFont(fontRegular).setFontSize(14))
						.setBorder(Border.NO_BORDER));
			}

			if (service.trim().length() != 0) {
				/* SERVICE */
				eventTable.addCell(new Cell().add(new Paragraph(sLabel + " :").setFont(fontBold).setFontSize(14))
						.setBorder(Border.NO_BORDER));

				eventTable.addCell(new Cell().add(new Paragraph(service).setFont(fontRegular).setFontSize(14))
						.setBorder(Border.NO_BORDER));
			}

			if (theme.trim().length() != 0) {
				/* THEME */
				eventTable.addCell(new Cell().add(new Paragraph(thLabel + " :").setFont(fontBold).setFontSize(14))
						.setBorder(Border.NO_BORDER));

				eventTable.addCell(new Cell().add(new Paragraph(theme).setFont(fontRegular).setFontSize(14))
						.setBorder(Border.NO_BORDER));
			}

			String eventRemark = eventDto.getRemark() != null ? eventDto.getRemark() : "";

			if (eventRemark.trim().length() != 0) {
				/* REMARKS */
				eventTable.addCell(new Cell().add(new Paragraph(enotes + " :").setFont(fontBold).setFontSize(14))
						.setBorder(Border.NO_BORDER));

				eventTable.addCell(new Cell(1, 3).add(new Paragraph(eventRemark).setFont(fontRegular).setFontSize(14))
						.setBorder(Border.NO_BORDER));
			}

			String foodNote = foodNotes.trim().isEmpty() ? foodName : foodName + " (" + foodNotes + ")";

			if (foodNote != null && foodNote.trim().length() != 0) {
				/* FOOD NOTES */
				eventTable.addCell(new Cell().add(new Paragraph(fNotesLabel + " :").setFont(fontBold).setFontSize(14))
						.setBorder(Border.NO_BORDER));

				eventTable
						.addCell(
								new Cell(1, 3)
										.add(new Paragraph(foodNote).setFont(fontRegular)
												.setFontColor(new DeviceRgb(255, 0, 0)).setFontSize(14))
										.setBorder(Border.NO_BORDER));
			}

			headerTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(eventTable));

			ShortMenuHeaderEventHandler fullHeaderHandler = new ShortMenuHeaderEventHandler(headerTable, 20f);
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, fullHeaderHandler);

			/* ================= 3 COLUMN LAYOUT ================= */
			float gutter = 5f;
			float leftMargin = document.getLeftMargin();
			float rightMargin = document.getRightMargin();
			float topMargin = 20;
			float top = PageSize.A4.getTop() - topMargin - calculateTableHeight(headerTable, pdfDocument);
			float bottom = PageSize.A4.getBottom() + 60;
			float height = top - bottom;
			System.out.println("left margin : " + leftMargin);
			System.out.println("Right margin : " + rightMargin);
			float usableWidth = PageSize.A4.getWidth() - leftMargin - rightMargin;
			float columnWidth = ((usableWidth - (gutter * 2)) / 3) - 4;

			Rectangle[] columns = new Rectangle[] { new Rectangle(leftMargin, bottom, columnWidth, height),
					new Rectangle(leftMargin + columnWidth + 12, bottom, columnWidth, height),
					new Rectangle(leftMargin + (columnWidth * 2) + 24, bottom, columnWidth, height) };

			document.setRenderer(new ColumnDocumentRenderer(document, columns));

			/* ================= CONTENT ================= */
			for (EventFunctionReportResponseDto functionDto : eventDto.getFunctions()) {
				String functionNote = "";
				String functionVenue = "";
				if (lang == 1) {
					functionNote = functionDto.getNotesHindi() != null ? functionDto.getNotesHindi() : "";
					functionVenue = functionDto.getFunctionVenueHindi() != null ? functionDto.getFunctionVenueHindi()
							: "";
				} else if (lang == 2) {
					functionNote = functionDto.getNotesGujarati() != null ? functionDto.getNotesGujarati() : "";
					functionVenue = functionDto.getFunctionVenueGujarati() != null
							? functionDto.getFunctionVenueGujarati()
							: "";
				} else {
					functionNote = functionDto.getNotesEnglish() != null ? functionDto.getNotesEnglish() : "";
					functionVenue = functionDto.getFunctionVenue() != null ? functionDto.getFunctionVenue() : "";
				}
				
				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, functionDto.getFunctionId());
				
				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", ")).toUpperCase();
				}
				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

				DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

				LocalDateTime start = LocalDateTime.parse(functionDto.getFunctionStartTimestamp(), inputFormatter);
				LocalDateTime end = LocalDateTime.parse(functionDto.getFunctionEndTimestamp(), inputFormatter);

				String dateTime = start.format(timeFormatter) + " to " + end.format(timeFormatter);

				Div data = new Div().setKeepTogether(true);

				String label;
				String value;

				if (functionDto.getIsPackage()) {
					label = pckPrice;
					value = functionDto.getPackagePrice().toString();
				} else {
					label = price;
					value = functionDto.getRate().toString();
				}
				data.add(new Paragraph().add(new Text(function + ": ").setFont(fontBold)).setPadding(0)
						.setMultipliedLeading(catLine)
						.add(new Text(functionDto.getFunctionName().toUpperCase()).setFont(fontRegular)).setPadding(0)
						.setMultipliedLeading(catLine).add(new Text("\n" + person + ": ").setFont(fontBold))
						.setPadding(0).setMultipliedLeading(catLine)
						.add(new Text(functionDto.getPax().toString()).setFont(fontRegular)).setPadding(0)
						.setMultipliedLeading(catLine).add(new Text("\n" + date + ": ").setFont(fontBold)).setPadding(0)
						.setMultipliedLeading(catLine)
						.add(new Text(functionDto.getFunctionStartTimestamp().split(" ")[0]).setFont(fontRegular))
						.setMultipliedLeading(catLine).setPadding(0)
						.add(new Text("\n" + eTime + ": ").setFont(fontBold)).setMultipliedLeading(catLine)
						.add(new Text(dateTime).setFont(fontRegular)).setPadding(0).setFont(fontBold)
						.setMultipliedLeading(catLine).setBorder(new SolidBorder(1f)).setFontSize(13).setPadding(3f)
						.add(new Text("\n" + venue + ": ").setFont(fontBold)).setMultipliedLeading(catLine)
						.add(new Text(functionVenue).setFont(fontRegular))
						.add(new Text("\n" + label + ": ").setFont(fontBold)).setMultipliedLeading(catLine)
						.add(new Text(value).setFont(fontRegular)).add(new Text("\n" + note + ": ").setFont(fontBold))
						.setMultipliedLeading(catLine).add(new Text(functionNote).setFont(fontRegular)).setPadding(0)
						.setFont(fontBold).setMultipliedLeading(catLine).setBorder(new SolidBorder(1f)).setFontSize(13)
						.setPadding(3f).setMarginBottom(5).setMultipliedLeading(catLine));

				document.add(data);

				for (MenuReportResponseDto menu : functionDto.getMenuCategories()) {

					Div categoryBlock = new Div().setKeepTogether(true).setMarginBottom(5).setMarginTop(5);

					String menuName = menu.getNameEnglish() != null ? menu.getNameEnglish().toUpperCase() : "";

					categoryBlock.add(new Paragraph(menuName).setFont(fontBold).setFontSize(15).setUnderline()
							.setMarginTop(3).setMarginBottom(0).setMultipliedLeading(catLine));

					if (isCategoryInstruction == 1 && menu.getMenuNotes() != null
							&& !menu.getMenuNotes().trim().isEmpty()) {

						categoryBlock.add(new Paragraph("(" + menu.getMenuNotes().trim() + ")").setFont(fontBold)
								.setFontSize(12).setMargin(0).setMultipliedLeading(catLine));
					}

					Div itemContainer = new Div();

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

						Div row = new Div();

						String itemName = item.getNameEnglish() != null ? item.getNameEnglish().toUpperCase() : "";

						row.add(new Paragraph("•  " + itemName).setFont(fontRegular).setFontSize(15).setMargin(0)
								.setMultipliedLeading(itemLine));

						if (isItemInstruction == 1 && item.getItemNotes() != null
								&& !item.getItemNotes().trim().isEmpty()) {

							row.add(new Paragraph("(" + item.getItemNotes().trim() + ")").setFont(fontRegular)
									.setFontSize(12).setMarginLeft(12).setMarginTop(0).setMultipliedLeading(itemLine));
						}

						itemContainer.add(row);
					}

					categoryBlock.add(itemContainer);

					document.add(categoryBlock);
				}
			}

			boolean extraChargesPrinted = false;

			boolean hasExtraCharges = isExtraCharges != null && isExtraCharges == 1;

			boolean hasTermsCondition = isTermsCondition != null && isTermsCondition == 1;
			
			boolean hasAdvancePayment = isAdvancePayment != null && isAdvancePayment == 1;
			
			if (hasExtraCharges || hasTermsCondition || hasAdvancePayment) {

				pdfDocument.removeEventHandler(fullHeaderHandler);

//				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				document.setMargins(40, // top
						28, // right
						40, // bottom
						28 // left
				);

				document.setRenderer(new DocumentRenderer(document));

				document.add(new AreaBreak(AreaBreakType.LAST_PAGE));
			}

			if (hasExtraCharges) {

				ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
						userid);

				if (extraCharges != null && extraCharges.getHeadings() != null
						&& !extraCharges.getHeadings().isEmpty()) {

					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					
					DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

					DateTimeFormatter chargeTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

					Paragraph extraTitle = new Paragraph("-: EXTRA CHARGE :-").setFont(fontBold).setFontSize(18)
							.setTextAlignment(TextAlignment.CENTER).setUnderline().setMarginTop(0).setMarginBottom(15);

					document.add(extraTitle);

					for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges.getHeadings()) {

						Paragraph headingTitle = new Paragraph(
								heading.getHeadingName() != null ? heading.getHeadingName().toUpperCase() : "")
								.setFont(fontBold).setFontSize(14).setUnderline().setMarginTop(10).setMarginBottom(6);

						document.add(headingTitle);

						float[] colWidths = { 15f, // DATE
								17f, // START TIME
								15f, // END TIME
								13f, // SESSION
								12f, // QTY
								12f, // RATE
								16f // TOTAL
						};

						Table chargeTable = new Table(UnitValue.createPercentArray(colWidths));

						chargeTable.setWidth(UnitValue.createPercentValue(100));

						String[] headers = { "DATE", "START TIME", "END TIME", "SESSION", "QTY", "RATE", "TOTAL" };

						for (String header : headers) {

							Cell headerCell = new Cell().add(new Paragraph(header).setFont(fontBold).setFontSize(10))
									.setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(blackColor, 0.8f))
									.setPadding(4);

							chargeTable.addHeaderCell(headerCell);
						}

						if (heading.getRows() != null && !heading.getRows().isEmpty()) {

							for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {

								chargeTable.addCell(new Cell().add(new Paragraph(
										row.getChargeDate() != null ? row.getChargeDate().format(dateFormatter) : "")
										.setFont(fontRegular).setFontSize(10)).setTextAlignment(TextAlignment.CENTER));

								chargeTable.addCell(new Cell()
										.add(new Paragraph(row.getChargeStartTime() != null
												? row.getChargeStartTime().format(chargeTimeFormatter)
												: "").setFont(fontRegular).setFontSize(10))
										.setTextAlignment(TextAlignment.CENTER));

								chargeTable.addCell(new Cell()
										.add(new Paragraph(row.getChargeEndTime() != null
												? row.getChargeEndTime().format(chargeTimeFormatter)
												: "").setFont(fontRegular).setFontSize(10))
										.setTextAlignment(TextAlignment.CENTER));

								chargeTable.addCell(new Cell().add(
										new Paragraph(row.getSession() != null ? String.valueOf(row.getSession()) : "0")
												.setFont(fontRegular).setFontSize(10))
										.setTextAlignment(TextAlignment.CENTER));

								chargeTable.addCell(new Cell()
										.add(new Paragraph(
												row.getPersonItem() != null ? String.valueOf(row.getPersonItem()) : "0")
												.setFont(fontRegular).setFontSize(10))
										.setTextAlignment(TextAlignment.CENTER));

								chargeTable.addCell(new Cell()
										.add(new Paragraph(row.getRate() != null ? row.getRate().toPlainString() : "0")
												.setFont(fontRegular).setFontSize(10))
										.setTextAlignment(TextAlignment.RIGHT));

								chargeTable.addCell(new Cell().add(
										new Paragraph(row.getTotal() != null ? row.getTotal().toPlainString() : "0")
												.setFont(fontRegular).setFontSize(10))
										.setTextAlignment(TextAlignment.RIGHT));
							}
						}

						document.add(chargeTable);

						Paragraph headingTotal = new Paragraph("Total: "
								+ (heading.getHeadingTotal() != null ? heading.getHeadingTotal().toPlainString() : "0"))
								.setFont(fontBold).setFontSize(12).setTextAlignment(TextAlignment.RIGHT)
								.setMarginTop(4);

						document.add(headingTotal);
					}

					Paragraph grandTotal = new Paragraph("Grand Total: "
							+ (extraCharges.getGrandTotal() != null ? extraCharges.getGrandTotal().toPlainString()
									: "0"))
							.setFont(fontBold).setFontSize(14).setTextAlignment(TextAlignment.RIGHT).setUnderline()
							.setMarginTop(10);

					document.add(grandTotal);

					extraChargesPrinted = true;
				}
			}
			
			Boolean advancePaymentPrinted = false;
			if (hasAdvancePayment) {

				List<EventAdvancePaymentResponseDto> responseDto = eventAdvancePaymentService
						.getAllEventAdvancePaymentList(eventId);

				if (responseDto != null && !responseDto.isEmpty()) {

					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					/*
					 * Create one container for the complete Advance Payment section.
					 *
					 * This prevents: Title -> one column/page Table -> another column/page Grand
					 * Total -> another column/page
					 */
					Div advancePaymentContent = new Div();

					advancePaymentContent.setKeepTogether(true);
					advancePaymentContent.setWidth(UnitValue.createPercentValue(100f));
					advancePaymentContent.setMarginTop(0);
					advancePaymentContent.setMarginBottom(0);

					/*
					 * ============================== ADVANCE PAYMENT TITLE
					 * ==============================
					 */
					Paragraph advancePaymentTitle = new Paragraph("-: ADVANCE PAYMENT :-").setFont(fontBold)
							.setFontSize(18).setTextAlignment(TextAlignment.CENTER).setUnderline().setMarginTop(0)
							.setMarginBottom(15);

					advancePaymentContent.add(advancePaymentTitle);

					/*
					 * ============================== PAYMENT TABLE ==============================
					 *
					 * Total width = 3 + 29 + 20 + 15 + 20 + 13 = 100
					 */
					Table paymentTable = new Table(
							UnitValue.createPercentArray(new float[] { 5f, 27f, 20f, 15f, 20f, 13f }));

					paymentTable.setWidth(UnitValue.createPercentValue(100f));
					paymentTable.setFixedLayout();

					/*
					 * Table border
					 */
					paymentTable.setBorder(new SolidBorder(blackColor, 1f));

					/*
					 * ============================== TABLE HEADERS ==============================
					 */
					String[] headers = { "No.", "Receiver Name", "Payment Mode", "Payment Date", "Remark", "Amount" };

					for (String header : headers) {

						Cell headerCell = new Cell();

						Paragraph headerParagraph = new Paragraph(header).setFont(fontBold).setFontSize(10)
								.setTextAlignment(TextAlignment.CENTER).setMargin(0);

						headerCell.add(headerParagraph);

						headerCell.setTextAlignment(TextAlignment.CENTER);

						headerCell.setVerticalAlignment(VerticalAlignment.MIDDLE);

						headerCell.setBorder(new SolidBorder(blackColor, 0.8f));

						headerCell.setPadding(4);

						paymentTable.addHeaderCell(headerCell);
					}

					/*
					 * ============================== PAYMENT DATA ==============================
					 */
					int i = 0;

					BigDecimal grandTotal = BigDecimal.ZERO;

					for (EventAdvancePaymentResponseDto row : responseDto) {

						/*
						 * ------------------------------ No. ------------------------------
						 */
						Cell noCell = new Cell();

						noCell.add(new Paragraph(String.valueOf(++i)).setFont(fontRegular).setFontSize(10).setMargin(0)
								.setTextAlignment(TextAlignment.CENTER));

						noCell.setTextAlignment(TextAlignment.CENTER);
						noCell.setVerticalAlignment(VerticalAlignment.MIDDLE);

						noCell.setBorder(new SolidBorder(blackColor, 0.8f));

						noCell.setPadding(3);

						paymentTable.addCell(noCell);

						/*
						 * ------------------------------ Receiver Name ------------------------------
						 */
						Cell receiverCell = new Cell();

						receiverCell.add(new Paragraph(row.getEntryByName() != null ? row.getEntryByName() : "-")
								.setFont(fontRegular).setFontSize(10).setMargin(0)
								.setTextAlignment(TextAlignment.CENTER));

						receiverCell.setTextAlignment(TextAlignment.CENTER);
						receiverCell.setVerticalAlignment(VerticalAlignment.MIDDLE);

						receiverCell.setBorder(new SolidBorder(blackColor, 0.8f));

						receiverCell.setPadding(3);

						paymentTable.addCell(receiverCell);

						/*
						 * ------------------------------ Payment Mode ------------------------------
						 */
						Cell paymentModeCell = new Cell();

						paymentModeCell.add(new Paragraph(row.getPaymentMode() != null ? row.getPaymentMode() : "-")
								.setFont(fontRegular).setFontSize(10).setMargin(0)
								.setTextAlignment(TextAlignment.CENTER));

						paymentModeCell.setTextAlignment(TextAlignment.CENTER);

						paymentModeCell.setVerticalAlignment(VerticalAlignment.MIDDLE);

						paymentModeCell.setBorder(new SolidBorder(blackColor, 0.8f));

						paymentModeCell.setPadding(3);

						paymentTable.addCell(paymentModeCell);

						/*
						 * ------------------------------ Payment Date ------------------------------
						 */
						Cell paymentDateCell = new Cell();

						paymentDateCell.add(new Paragraph(row.getPaymentDate() != null ? row.getPaymentDate() : "-")
								.setFont(fontRegular).setFontSize(10).setMargin(0)
								.setTextAlignment(TextAlignment.CENTER));

						paymentDateCell.setTextAlignment(TextAlignment.CENTER);

						paymentDateCell.setVerticalAlignment(VerticalAlignment.MIDDLE);

						paymentDateCell.setBorder(new SolidBorder(blackColor, 0.8f));

						paymentDateCell.setPadding(3);

						paymentTable.addCell(paymentDateCell);

						/*
						 * ------------------------------ Remark ------------------------------
						 */
						Cell remarkCell = new Cell();

						remarkCell
								.add(new Paragraph(row.getRemark() != null ? row.getRemark() : "-").setFont(fontRegular)
										.setFontSize(10).setMargin(0).setTextAlignment(TextAlignment.CENTER));

						remarkCell.setTextAlignment(TextAlignment.CENTER);

						remarkCell.setVerticalAlignment(VerticalAlignment.MIDDLE);

						remarkCell.setBorder(new SolidBorder(blackColor, 0.8f));

						remarkCell.setPadding(3);

						paymentTable.addCell(remarkCell);

						/*
						 * ------------------------------ Amount ------------------------------
						 */
						BigDecimal amount = row.getAmount() != null ? row.getAmount() : BigDecimal.ZERO;

						Cell amountCell = new Cell();

						amountCell.add(new Paragraph(amount.toString()).setFont(fontRegular).setFontSize(10)
								.setMargin(0).setTextAlignment(TextAlignment.RIGHT));

						amountCell.setTextAlignment(TextAlignment.RIGHT);

						amountCell.setVerticalAlignment(VerticalAlignment.MIDDLE);

						amountCell.setBorder(new SolidBorder(blackColor, 0.8f));

						amountCell.setPadding(3);

						paymentTable.addCell(amountCell);

						/*
						 * Add to Grand Total
						 */
						grandTotal = grandTotal.add(amount);
					}

					/*
					 * Add table to the single container
					 */
					advancePaymentContent.add(paymentTable);

					/*
					 * ============================== GRAND TOTAL ==============================
					 */
					Paragraph grandTotalPara = new Paragraph("Grand Total: " + grandTotal.toString()).setFont(fontBold)
							.setFontSize(14).setTextAlignment(TextAlignment.RIGHT).setUnderline().setMarginTop(10)
							.setMarginBottom(0);

					advancePaymentContent.add(grandTotalPara);

					/*
					 * ============================== ADD COMPLETE SECTION
					 * ==============================
					 *
					 * Important: Do NOT add title/table/total separately. Add the complete Div as
					 * one element.
					 */
					document.add(advancePaymentContent);

					/*
					 * Mark advance payment as printed
					 */
					advancePaymentPrinted = true;
				}
			}
			
			if (hasTermsCondition) {

				Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

				if (terms.isPresent()) {

					EventTermsAndConditionEntity eventTermsEntity = terms.get();

					List<EventTermsAndConditionFeaturesEntity> features = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

					if (features != null && !features.isEmpty()) {

						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

						Paragraph termsTitle = new Paragraph("Special Instruction").setFont(fontBold).setFontSize(16)
								.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginBottom(15);

						document.add(termsTitle);

						int index = 1;

						for (EventTermsAndConditionFeaturesEntity feature : features) {

							String description;

							if (lang == 1) {

								description = feature.getDescriptionHindi();

							} else if (lang == 2) {

								description = feature.getDescriptionGujarati();

							} else {

								description = feature.getDescription();
							}

							if (description == null) {
								description = "";
							}

							Paragraph term = new Paragraph(index + ". " + description).setFont(fontRegular)
									.setFontSize(12).setWidth(UnitValue.createPercentValue(100)).setMarginTop(0)
									.setMarginBottom(5);

							document.add(term);

							index++;
						}
					}
				}
			}
			/* ================= PAGE NUMBERS ================= */
			document.flush();

			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);

				if (page == null || page.isFlushed()) {
					continue;
				}

				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 7,
						TextAlignment.CENTER);

				canvas.close();
			}

			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo() + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"back office report")
					+ ".pdf";

		} catch (Exception e) {
			throw new RuntimeException("Failed to generate menu report", e);
		}
	}

	private float calculateTableHeight(Table table, PdfDocument pdfDoc) {
		IRenderer renderer = table.createRendererSubTree();
		renderer.setParent(new Document(pdfDoc).getRenderer());

		LayoutResult result = renderer
				.layout(new LayoutContext(new LayoutArea(0, new Rectangle(PageSize.A4.getWidth() - 80, 1000 // large
																											// height
				))));

		return result.getOccupiedArea().getBBox().getHeight();
	}

	private Cell textCell(String text, boolean bold) {
		PdfFont font;
		try {
			font = bold ? PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
					: PdfFontFactory.createFont(StandardFonts.HELVETICA);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return new Cell().add(new Paragraph(text).setFont(font).setFontSize(14f)).setBorder(Border.NO_BORDER)
				.setTextAlignment(TextAlignment.LEFT);
	}

	private Cell labelCell(String text) {
		return textCell(text, true).setPaddingLeft(10);
	}

	private Cell valueCell(String text) {
		return textCell(text, false);
	}

	private Cell colonCell() {
		PdfFont font;
		try {
			font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return new Cell().add(new Paragraph(":").setFont(font).setFontSize(14f)).setBorder(Border.NO_BORDER)
				.setTextAlignment(TextAlignment.LEFT);
	}

	@Override
	public String getMenuPlanningSimpleReport6(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
	        Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
	        Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails,
	        Integer isTermsCondition, Integer isExtraCharges) {
	    try {
	        PdfFont basicFont = null;
	        PdfFont itemFont = null;
	        PdfFont boldFont = null;
	        String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "", l1 = "",
	                l2 = "", note = "", eventNotes = "", billingNameLabel = "", serviceLabel = "", themeLabel = "";
	        menuPreparationServiceImpl.loadLicense();

	        int x = 185;
	        int y = 500;

	        if (lang == 1) {
	            // Hindi
	            basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
	            itemFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
	            boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
	            customerName = "नाम";
	            customerPhone = "मोबाइल नंबर";
	            eName = "कार्यक्रम का नाम";
	            eDate = "दिनांक";
	            fNotes = "नोट्स";
	            eVenue = "आयोजन स्थान";
	            l1 = "मेम्बर्स:";
	            note = "नोट";
	            eventNotes = "कार्यक्रम की नोट";
	            billingNameLabel = "बिलिंग नाम";
	            serviceLabel = "सर्विस";
	            themeLabel = "थीम";
	            y = 512;
	        } else if (lang == 2) {
	            // Gujarati
	            String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

	            if (language.equalsIgnoreCase("Tamil")) {
	                basicFont = itemFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Regular.ttf");
	                boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Bold.ttf");
	                customerName = "வாடிக்கையாளர் பெயர்";
	                customerPhone = "மொபைல் எண்";
	                eName = "நிகழ்ச்சி பெயர்";
	                eDate = "நிகழ்ச்சி தேதி";
	                fNotes = "உணவு விவரம்";
	                eVenue = "நிகழ்வு இடம்";
	                l1 = "நபர்களின் எண்ணிக்கை:";
	                note = "குறிப்பு";
	                eventNotes = "நிகழ்வு குறிப்புகள்";
	                billingNameLabel = "பில்லிங் பெயர்";
	                serviceLabel = "சர்வீஸ்";
	                themeLabel = "தீம்";
	            } else if (language.equalsIgnoreCase("Telugu")) {
	                basicFont = itemFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Regular.ttf");
	                boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Bold.ttf");
	                customerName = "కస్టమర్ పేరు";
	                customerPhone = "మొబైల్ నంబర్";
	                eName = "కార్యక్రమ పేరు";
	                eDate = "కార్యక్రమ తేదీ";
	                fNotes = "భోజన వివరాలు";
	                eVenue = "కార్యక్రమ స్థలం";
	                l1 = "వ్యక్తుల సంఖ్య:";
	                note = "గమనిక";
	                eventNotes = "ఈవెంట్ గమనికలు";
	                billingNameLabel = "బిల్లింగ్ పేరు";
	                serviceLabel = "సర్వీస్";
	                themeLabel = "థీమ్";
	            } else if (language.equalsIgnoreCase("Malayalam")) {
	                basicFont = itemFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
	                boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Bold.ttf");
	                customerName = "ഉപഭോക്താവിന്റെ പേര്";
	                customerPhone = "മൊബൈൽ നമ്പർ";
	                eName = "പരിപാടിയുടെ പേര്";
	                eDate = "പരിപാടിയുടെ തീയതി";
	                fNotes = "ഭക്ഷണ വിശദാംശങ്ങൾ";
	                eVenue = "പരിപാടി സ്ഥലം";
	                l1 = "വ്യക്തികളുടെ എണ്ണം:";
	                note = "കുറിപ്പ്";
	                eventNotes = "പരിപാടി രേഖകൾ";
	                billingNameLabel = "ബില്ലിംഗ് പേര്";
	                serviceLabel = "സർവീസ്";
	                themeLabel = "തീം";
	            } else if (language.equalsIgnoreCase("Marathi")) {
	                basicFont = itemFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
	                boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
	                customerName = "ग्राहकाचे नाव";
	                customerPhone = "मोबाईल नंबर";
	                eName = "कार्यक्रमाचे नाव";
	                eDate = "कार्यक्रमाची दिनांक";
	                fNotes = "भोजन तपशील";
	                eVenue = "आयोजन स्थळ";
	                l1 = "व्यक्तींची संख्या:";
	                note = "टीप";
	                eventNotes = "कार्यक्रम नोंदी";
	                billingNameLabel = "बिलिंग नाव";
	                serviceLabel = "सर्व्हिस";
	                themeLabel = "थीम";
	            } else {
	                basicFont = itemFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
	                boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
	                customerName = "ગ્રાહકનું નામ";
	                customerPhone = "મોબાઇલ નંબર";
	                eName = "કાર્યક્રમનું નામ";
	                eDate = "કાર્યક્રમની તારીખ";
	                fNotes = "ભોજન વિગતો";
	                eVenue = "આયોજન સ્થળ";
	                l1 = "વ્યક્તિઓની સંખ્યા:";
	                note = "નોંધ";
	                eventNotes = "કાર્યક્રમની નોંધ";
	                billingNameLabel = "બિલિંગ નામ";
	                serviceLabel = "સર્વિસ";
	                themeLabel = "થીમ";
	            }
	            y = 510;
	        } else {
	            // English
	            basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	            itemFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
	            boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
	            customerName = "Customer Name";
	            customerPhone = "Mobile No.";
	            eName = "Event Name";
	            eDate = "Event Date";
	            fNotes = "Food Note";
	            eVenue = "Venue";
	            l1 = "PERSONS:";
	            note = "Note";
	            eventNotes = "Event Notes";
	            billingNameLabel = "Billing Name";
	            serviceLabel = "Service";
	            themeLabel = "Theme";
	        }

	        PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

	        EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
	                eventFunctionId, lang, userid);

	        if (eventDto == null) {
	            return "";
	        }

	        Date now = new Date();
	        String rootPath = re.getSession().getServletContext().getRealPath("/");
	        String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
	        File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

	        if (!outputPath.exists()) {
	            outputPath.mkdirs();
	        }

	        File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
	                formatDate(eventDto.getEventStartTimestamp()), "back office report") + ".pdf");

	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
	        document.setMargins(40, 20, 50, 20);

	        Color detailsColor = new DeviceRgb(0, 153, 0);
	        Color fnColor = new DeviceRgb(88, 23, 0);
	        Color insColor = new DeviceRgb(104, 104, 104);
	        Color cmpColor = new DeviceRgb(3, 50, 100);
	        Color blackColor = new DeviceRgb(0, 0, 0);

	        Cell cell;

	        String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
	        String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
	        String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
	        String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
	        String pax = eventDto.getPax();
	        String venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
	        StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
	        String foodNotes = "";
	        String cmpName = eventDto.getCmpName();
	        String cmpPhone = eventDto.getCmpPhone();
	        String cmpAddress = eventDto.getCmpAddress();
	        String userFirstName = eventDto.getUserFirstName();
	        String userLastName = eventDto.getUserLastName();
	        String countryCode = eventDto.getCountrycode();
	        String email = eventDto.getEmail();
	        String logo = eventDto.getLogo();
	        String remarks = eventDto.getRemark() != null ? eventDto.getRemark() : "";

	        String billingName = "";
	        String service = "";
	        String theme = "";
	        String functionNote = "";
	        if (lang == 1) {
	            billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
	            foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
	            service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
	            theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
	        } else if (lang == 2) {
	            billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
	            foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
	            service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
	            theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
	        } else {
	            billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
	            foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
	            service = eventDto.getService() != null ? eventDto.getService() : "";
	            theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
	        }

	        if (foodNotes != null && !foodNotes.isEmpty()) {
	            foodNotesName.append("( ");
	            foodNotesName.append(foodNotes);
	            foodNotesName.append(" )");
	        }

	        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new PageBorderEventHandler());

	        if (isCompanyDetails == 1) {
	            String watermarkPath = environment.getProperty("app.image.url") + eventDto.getLogo();
	            pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(watermarkPath));
	        }

	        float[] columnWidth = { 25f, 10f, 2f, 63f };
	        Table cmptbl = new Table(UnitValue.createPercentArray(columnWidth));
	        cmptbl.setWidth(UnitValue.createPercentValue(100));
	        cmptbl.setMarginBottom(0f);
	        cmptbl.setMarginTop(-20f);

	        ImageData imgData = menuPreparationServiceImpl
	                .loadImageFromResource(environment.getProperty("app.image.url") + logo);
	        Image img = new Image(imgData);
	        img.setWidth(UnitValue.createPercentValue(90));
	        img.setHorizontalAlignment(HorizontalAlignment.CENTER);

	        Cell dataCell = new Cell(4, 1).setPadding(5f).setBorder(Border.NO_BORDER).add(img);
	        cmptbl.addCell(dataCell);

	        Paragraph cmpNameContent = new Paragraph(safeText(cmpName.toUpperCase()))
	                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18);
	        dataCell = new Cell(1, 3).setPadding(0).setBorder(Border.NO_BORDER).add(cmpNameContent);
	        cmptbl.addCell(dataCell);

	        Paragraph cmpAddressContent = new Paragraph(safeText(cmpAddress)).setFont(basicFont).setFontSize(12);
	        dataCell = new Cell(1, 3).setPadding(0).setBorder(Border.NO_BORDER).add(cmpAddressContent);
	        cmptbl.addCell(dataCell);

	        Paragraph cmplabel = new Paragraph("Mobile")
	                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12);
	        dataCell = new Cell().setPadding(0).setBorder(Border.NO_BORDER).add(cmplabel);
	        cmptbl.addCell(dataCell);

	        Paragraph cmpseperator = new Paragraph(":").setFont(basicFont).setFontSize(12);
	        dataCell = new Cell().setPadding(0).setBorder(Border.NO_BORDER).add(cmpseperator);
	        cmptbl.addCell(dataCell);

	        Paragraph cmpMobileContent = new Paragraph(cmpPhone).setFont(basicFont).setFontSize(12);
	        dataCell = new Cell().setPadding(0).setBorder(Border.NO_BORDER).add(cmpMobileContent);
	        cmptbl.addCell(dataCell);

	        cmplabel = new Paragraph("Email").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
	                .setFontSize(12);
	        dataCell = new Cell().setPadding(0).setBorder(Border.NO_BORDER).add(cmplabel);
	        cmptbl.addCell(dataCell);

	        cmpseperator = new Paragraph(":").setFont(basicFont).setFontSize(12);
	        dataCell = new Cell().setPadding(0).setBorder(Border.NO_BORDER).add(cmpseperator);
	        cmptbl.addCell(dataCell);

	        Paragraph cmpEmailContent = new Paragraph(email).setFont(basicFont).setFontSize(12);
	        dataCell = new Cell().setPadding(0).setBorder(Border.NO_BORDER).add(cmpEmailContent);
	        cmptbl.addCell(dataCell);

	        if (isCompanyDetails == 1) {
	            document.add(cmptbl);
	        }

	        Paragraph head = new Paragraph().add(new Text("Kitchen Menu File")
	                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(20));
	        head.setWidth(UnitValue.createPercentValue(100));
	        head.setTextAlignment(TextAlignment.CENTER);
	        document.add(head);

	        float[] columnWidth2 = { 23f, 2f, 30f, 22f, 2f, 21f };
	        Table tbl = new Table(UnitValue.createPercentArray(columnWidth2));
	        tbl.setWidth(UnitValue.createPercentValue(100));
	        tbl.setBorder(new SolidBorder(1f));
	        tbl.setMarginTop(-8f);

	        Paragraph label = new Paragraph();
	        Paragraph seperator = new Paragraph();
	        Paragraph hostNameContent = new Paragraph();

	        if (isPartyDetails == 1) {
	            label = new Paragraph(safeText(customerName)).setFont(boldFont).setFontSize(14);
	            seperator = new Paragraph(":").setFont(basicFont).setFontSize(14);
	            hostNameContent = new Paragraph(safeText(hostName.toUpperCase())).setFont(basicFont).setFontSize(14);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(label);
	            tbl.addCell(dataCell);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(seperator);
	            tbl.addCell(dataCell);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(hostNameContent);
	            tbl.addCell(dataCell);

	            label = new Paragraph(safeText(billingNameLabel)).setFont(boldFont).setFontSize(14);
	            seperator = new Paragraph(":").setFont(basicFont).setFontSize(14);
	            hostNameContent = new Paragraph(safeText(billingName.toUpperCase())).setFont(basicFont).setFontSize(14);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(label);
	            tbl.addCell(dataCell);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(seperator);
	            tbl.addCell(dataCell);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(hostNameContent);
	            tbl.addCell(dataCell);

	            label = new Paragraph(customerPhone).setFont(boldFont).setFontSize(14);
	            seperator = new Paragraph(":").setFont(basicFont).setFontSize(14);
	            Paragraph mobileNoContent = new Paragraph(safeText(mobileNo)).setFont(basicFont).setFontSize(14);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(label);
	            tbl.addCell(dataCell);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(seperator);
	            tbl.addCell(dataCell);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(mobileNoContent);
	            tbl.addCell(dataCell);
	        }

	        label = new Paragraph(eName).setFont(boldFont).setFontSize(14);
	        seperator = new Paragraph(":").setFont(basicFont).setFontSize(14);
	        Paragraph eventNameContent = new Paragraph(safeText(eventName.toUpperCase())).setFont(basicFont)
	                .setFontSize(14);
	        dataCell = new Cell().setBorder(Border.NO_BORDER).add(label);
	        tbl.addCell(dataCell);
	        dataCell = new Cell().setBorder(Border.NO_BORDER).add(seperator);
	        tbl.addCell(dataCell);
	        dataCell = new Cell().setBorder(Border.NO_BORDER).add(eventNameContent);
	        tbl.addCell(dataCell);

	        label = new Paragraph(eDate).setFont(boldFont).setFontSize(14);
	        seperator = new Paragraph(":").setFont(basicFont).setFontSize(14);
	        Paragraph eventDateContent = new Paragraph(safeText(eventDate)).setFont(basicFont).setFontSize(14);
	        dataCell = new Cell().setBorder(Border.NO_BORDER).add(label);
	        tbl.addCell(dataCell);
	        dataCell = new Cell().setBorder(Border.NO_BORDER).add(seperator);
	        tbl.addCell(dataCell);
	        dataCell = new Cell().setBorder(Border.NO_BORDER).add(eventDateContent);
	        tbl.addCell(dataCell);

	        label = new Paragraph(eVenue).setFont(boldFont).setFontSize(14);
	        seperator = new Paragraph(":").setFont(basicFont).setFontSize(14);
	        Paragraph venueContent = new Paragraph(safeText(venue.toUpperCase())).setFont(basicFont).setFontSize(14);
	        dataCell = new Cell().setBorder(Border.NO_BORDER).add(label);
	        tbl.addCell(dataCell);
	        dataCell = new Cell().setBorder(Border.NO_BORDER).add(seperator);
	        tbl.addCell(dataCell);
	        dataCell = new Cell().setBorder(Border.NO_BORDER).add(venueContent);
	        tbl.addCell(dataCell);

	        if (foodNotesName != null && foodNotesName.toString().trim().length() != 0) {
	            label = new Paragraph(fNotes).setFont(boldFont).setFontSize(14);
	            seperator = new Paragraph(":").setFont(basicFont).setFontSize(14);
	            Paragraph foodContent = new Paragraph(safeText(foodNotesName.toString().toUpperCase()))
	                    .setFont(basicFont).setFontSize(14);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(label);
	            tbl.addCell(dataCell);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(seperator);
	            tbl.addCell(dataCell);
	            dataCell = new Cell(1, 4).setBorder(Border.NO_BORDER).add(foodContent);
	            tbl.addCell(dataCell);
	        }

	        if (service != null && service.trim().length() != 0) {
	            label = new Paragraph(serviceLabel).setFont(boldFont).setFontSize(14);
	            seperator = new Paragraph(":").setFont(basicFont).setFontSize(14);
	            Paragraph serviceContent = new Paragraph(safeText(service.toUpperCase())).setFont(basicFont)
	                    .setFontSize(14);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(label);
	            tbl.addCell(dataCell);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(seperator);
	            tbl.addCell(dataCell);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(serviceContent);
	            tbl.addCell(dataCell);
	        }

	        if (theme != null && theme.trim().length() != 0) {
	            label = new Paragraph(themeLabel).setFont(boldFont).setFontSize(14);
	            seperator = new Paragraph(":").setFont(basicFont).setFontSize(14);
	            Paragraph themeContent = new Paragraph(safeText(theme.toUpperCase())).setFont(basicFont)
	                    .setFontSize(14);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(label);
	            tbl.addCell(dataCell);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(seperator);
	            tbl.addCell(dataCell);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(themeContent);
	            tbl.addCell(dataCell);
	        }

	        if (remarks != null && remarks.trim().length() != 0) {
	            label = new Paragraph(eventNotes).setFont(boldFont).setFontSize(14);
	            seperator = new Paragraph(":").setFont(basicFont).setFontSize(14);
	            Paragraph eventNotesPara = new Paragraph(safeText(remarks.toUpperCase())).setFont(basicFont)
	                    .setFontSize(14);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(label);
	            tbl.addCell(dataCell);
	            dataCell = new Cell().setBorder(Border.NO_BORDER).add(seperator);
	            tbl.addCell(dataCell);
	            dataCell = new Cell(1, 4).setBorder(Border.NO_BORDER).add(eventNotesPara);
	            tbl.addCell(dataCell);
	        }

	        document.add(tbl);

	        String fnname = eventDto.getFunctions().get(0).getFunctionName();

	        // ============ MENU CONTENT PAGES ============
	        // Each function's content is wrapped in its own single-column Table.
	        // Party details are added via addHeaderCell() so iText automatically
	        // repeats them at the top of every page this function's table spans.
	        boolean isFirstFunction = true; // tracks the first function group (shares page 1 with the main `tbl`)
	        for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {

	            if (!fnname.equalsIgnoreCase(eventFunctionMasterResponseDto.getFunctionName())) {
	                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
	            }

	            // ===== one table per function, with a repeating party-details header =====
	            Table functionTable = new Table(UnitValue.createPercentArray(new float[] { 100f }));
	            functionTable.setWidth(UnitValue.createPercentValue(100));
	            functionTable.setBorder(Border.NO_BORDER);

	            if (isPartyDetails == 1) {
	                // 6 columns: Customer Name | : | value | Billing Name | : | value
	                float[] partyColWidths = { 23f, 2f, 30f, 22f, 2f, 21f };
	                Table functionPartyTable = new Table(UnitValue.createPercentArray(partyColWidths));
	                functionPartyTable.setWidth(UnitValue.createPercentValue(100));
	                functionPartyTable.setBorder(new SolidBorder(1f));
	                functionPartyTable.setMarginBottom(6f);

	                // Row 1: Customer Name : value    Billing Name : value
	                label = new Paragraph(safeText(customerName)).setFont(boldFont).setFontSize(14);
	                seperator = new Paragraph(":").setFont(basicFont).setFontSize(14);
	                hostNameContent = new Paragraph(safeText(hostName.toUpperCase())).setFont(basicFont).setFontSize(14);
	                functionPartyTable.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(4f).add(label));
	                functionPartyTable.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(4f).add(seperator));
	                functionPartyTable.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(4f).add(hostNameContent));

	                Paragraph billingLabel = new Paragraph(safeText(billingNameLabel)).setFont(boldFont).setFontSize(14);
	                Paragraph billingSeperator = new Paragraph(":").setFont(basicFont).setFontSize(14);
	                Paragraph billingContent = new Paragraph(safeText(billingName.toUpperCase())).setFont(basicFont).setFontSize(14);
	                functionPartyTable.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(4f).add(billingLabel));
	                functionPartyTable.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(4f).add(billingSeperator));
	                functionPartyTable.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(4f).add(billingContent));

	                // Row 2: Mobile No. : value   (spans remaining columns so it doesn't get cut off)
	                label = new Paragraph(customerPhone).setFont(boldFont).setFontSize(14);
	                seperator = new Paragraph(":").setFont(basicFont).setFontSize(14);
	                Paragraph mobileNoContent = new Paragraph(safeText(mobileNo)).setFont(basicFont).setFontSize(14);
	                functionPartyTable.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(4f).add(label));
	                functionPartyTable.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(4f).add(seperator));
	                functionPartyTable.addCell(new Cell(1, 4).setBorder(Border.NO_BORDER).setPadding(4f).add(mobileNoContent));

	                Cell partyHeaderCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0).add(functionPartyTable);

	                functionTable.addHeaderCell(partyHeaderCell);

	                if (isFirstFunction) {
	                    functionTable.setSkipFirstHeader(true);
	                }
	            }
	            // ===== END party details header =====

	            String fnNote = "";
	            if (lang == 1) {
	                fnNote = eventFunctionMasterResponseDto.getNotesHindi() != null
	                        ? eventFunctionMasterResponseDto.getNotesHindi()
	                        : "";
	            } else if (lang == 2) {
	                fnNote = eventFunctionMasterResponseDto.getNotesGujarati() != null
	                        ? eventFunctionMasterResponseDto.getNotesGujarati()
	                        : "";
	            } else {
	                fnNote = eventFunctionMasterResponseDto.getNotesEnglish() != null
	                        ? eventFunctionMasterResponseDto.getNotesEnglish()
	                        : "";
	            }

	            float[] columnWidths = { 100f };
	            Table headerTable = new Table(UnitValue.createPercentArray(columnWidths));
	            headerTable.setWidth(UnitValue.createPercentValue(100));
	            headerTable.setBorder(Border.NO_BORDER);
	            headerTable.setMarginTop(15f);
	            headerTable.setMarginBottom(-10f);

	            String functionTitle = menuPreparationServiceImpl
	                    .formatText(eventFunctionMasterResponseDto.getFunctionName(), lang);
	            String funpax = eventFunctionMasterResponseDto.getPax() != null
	                    ? eventFunctionMasterResponseDto.getPax().toString()
	                    : "0";
	            String startTimeText = (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
	                    ? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
	                    : "TBD");
	            String date = startTimeText.split(" ")[0];
	            String time = startTimeText.split(" ")[1] + " " + startTimeText.split(" ")[2];

	            StringBuilder fnTitle = new StringBuilder();
	            StringBuilder fnTitle2 = new StringBuilder();
	            fnTitle.append(functionTitle.toUpperCase());
	            fnTitle2.append(l1 + " " + funpax);

	            Paragraph funPara = new Paragraph().add(new Text(fnTitle.toString()).setFont(boldFont)
	                    .setFontColor(detailsColor).setFontSize(15f).setUnderline());
	            Cell funCell = new Cell().add(funPara);
	            funCell.setBorder(Border.NO_BORDER);
	            funCell.setTextAlignment(TextAlignment.CENTER);
	            headerTable.addCell(funCell);

	            funPara = new Paragraph()
	                    .add(new Text(eDate + ": ").setFont(boldFont).setFontColor(detailsColor).setFontSize(14f))
	                    .add(new Text(date + " " + time.toUpperCase()).setFont(boldFont).setFontColor(detailsColor)
	                            .setFontSize(14f));
	            funCell = new Cell().add(funPara);
	            funCell.setBorder(Border.NO_BORDER);
	            funCell.setTextAlignment(TextAlignment.CENTER);
	            headerTable.addCell(funCell);

	            funPara = new Paragraph().add(
	                    new Text(fnTitle2.toString()).setFont(boldFont).setFontColor(detailsColor).setFontSize(14f));
	            funCell = new Cell().add(funPara);
	            funCell.setBorder(Border.NO_BORDER);
	            funCell.setTextAlignment(TextAlignment.CENTER);
	            headerTable.addCell(funCell);

	            if (fnNote.trim().length() != 0) {
	                funPara = new Paragraph()
	                        .add(new Text(note + ": ").setFont(boldFont).setFontColor(detailsColor).setFontSize(14f))
	                        .add(new Text(fnNote).setFont(basicFont).setFontColor(detailsColor).setFontSize(14f));
	                funCell = new Cell().add(funPara);
	                funCell.setBorder(Border.NO_BORDER);
	                funCell.setTextAlignment(TextAlignment.CENTER);
	                headerTable.addCell(funCell);
	            }

	            functionTable.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(0).add(headerTable));

	            List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

	            for (MenuReportResponseDto menu : menuReportResponseDtos) {

	                Div menuContent = new Div();
	                menuContent.setKeepTogether(true);

	                Paragraph p = new Paragraph(
	                        menuPreparationServiceImpl.formatText(menu.getNameEnglish(), lang).toUpperCase())
	                        .setFont(boldFont).setFontSize(20).setFixedLeading(21f)
	                        .setTextAlignment(TextAlignment.CENTER).setUnderline();

	                Table table = new Table(1).setWidth(UnitValue.createPercentValue(100));
	                table.setBorder(Border.NO_BORDER);
	                table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

	                menuContent.add(table).setMarginTop(8f);

	                if (isCategoryInstruction == 1) {
	                    if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
	                        menuContent
	                                .add(new Paragraph(menuPreparationServiceImpl.formatText(menu.getMenuNotes(), lang))
	                                        .setFont(basicFont).setFontSize(14f).setMultipliedLeading(1.1f)
	                                        .setFontColor(insColor).setMarginLeft(6)
	                                        .setTextAlignment(TextAlignment.CENTER));
	                    }
	                }

	                for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

	                    menuContent.add(new Paragraph(
	                            menuPreparationServiceImpl.formatText(item.getNameEnglish(), lang).toUpperCase())
	                            .setFont(itemFont).setFontSize(18f).setFixedLeading(16f).setMarginLeft(12)
	                            .setTextAlignment(TextAlignment.CENTER));

	                    if (isItemInstruction == 1) {
	                        if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
	                            menuContent.add(
	                                    new Paragraph(menuPreparationServiceImpl.formatText(item.getItemNotes(), lang))
	                                            .setFont(basicFont).setFontSize(14f).setMultipliedLeading(1.1f)
	                                            .setFontColor(insColor).setMarginLeft(18).setMarginTop(-4f)
	                                            .setMarginBottom(0f).setPadding(0f)
	                                            .setTextAlignment(TextAlignment.CENTER));
	                        }
	                    }
	                }

	                functionTable.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(0).add(menuContent));
	            }

	            document.add(functionTable);
	            isFirstFunction = false;
	        }

	        if (isExtraCharges != null && isExtraCharges == 1) {

	            ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
	                    userid);

	            if (extraCharges != null && extraCharges.getHeadings() != null
	                    && !extraCharges.getHeadings().isEmpty()) {

	                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

	                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

	                Paragraph extraTitle = new Paragraph("-: EXTRA CHARGE :-").setFont(basicFont).setFontSize(18)
	                        .setFontColor(blackColor).setTextAlignment(TextAlignment.CENTER).setUnderline()
	                        .simulateBold().setMarginBottom(15f);
	                document.add(extraTitle);

	                for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges.getHeadings()) {

	                    Paragraph headingTitle = new Paragraph(heading.getHeadingName().toUpperCase())
	                            .setFont(basicFont).setFontSize(14).setFontColor(blackColor).simulateBold()
	                            .setUnderline().setTextAlignment(TextAlignment.LEFT).setMarginTop(12f)
	                            .setMarginBottom(6f);
	                    document.add(headingTitle);

	                    float[] colWidths = { 15f, 17f, 15f, 13f, 12f, 12f, 16f };
	                    Table chargeTable = new Table(UnitValue.createPercentArray(colWidths));
	                    chargeTable.setWidth(UnitValue.createPercentValue(100));
	                    chargeTable.setBorder(new SolidBorder(blackColor, 1f));

	                    String[] colHeaders = { "DATE", "START TIME", "END TIME", "SESSION", "QTY", "RATE", "TOTAL" };
	                    for (String h : colHeaders) {
	                        chargeTable.addHeaderCell(new Cell()
	                                .add(new Paragraph(h).setFont(basicFont).setFontSize(11).setFontColor(blackColor)
	                                        .simulateBold().setUnderline())
	                                .setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(blackColor, 0.8f))
	                                .setPaddingTop(5f).setPaddingBottom(5f));
	                    }

	                    if (heading.getRows() != null && !heading.getRows().isEmpty()) {
	                        for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {

	                            chargeTable.addCell(dataCell(
	                                    row.getChargeDate() != null ? row.getChargeDate().format(dateFormatter) : "",
	                                    basicFont, blackColor, 10, blackColor));

	                            chargeTable.addCell(dataCell(row.getChargeStartTime() != null
	                                    ? row.getChargeStartTime().format(timeFormatter)
	                                    : "", basicFont, blackColor, 10, blackColor));

	                            chargeTable.addCell(dataCell(
	                                    row.getChargeEndTime() != null ? row.getChargeEndTime().format(timeFormatter)
	                                            : "",
	                                    basicFont, blackColor, 10, blackColor));

	                            chargeTable.addCell(
	                                    dataCell(row.getSession() != null ? String.valueOf(row.getSession()) : "0",
	                                            basicFont, blackColor, 10, blackColor));

	                            chargeTable.addCell(dataCell(
	                                    row.getPersonItem() != null ? String.valueOf(row.getPersonItem()) : "0",
	                                    basicFont, blackColor, 10, blackColor));

	                            chargeTable
	                                    .addCell(dataCell(row.getRate() != null ? row.getRate().toPlainString() : "0",
	                                            basicFont, blackColor, 10, blackColor));

	                            chargeTable
	                                    .addCell(dataCell(row.getTotal() != null ? row.getTotal().toPlainString() : "0",
	                                            basicFont, blackColor, 10, blackColor));
	                        }
	                    } else {
	                        for (int i = 0; i < 7; i++) {
	                            for (int j = 0; j < 7; j++) {
	                                chargeTable.addCell(dataCell("", basicFont, blackColor, 10, blackColor));
	                            }
	                        }
	                    }

	                    document.add(chargeTable);

	                    Paragraph headingTotalPara = new Paragraph("Total: "
	                            + (heading.getHeadingTotal() != null ? heading.getHeadingTotal().toPlainString() : "0"))
	                            .setFont(basicFont).setFontSize(12).setFontColor(blackColor).simulateBold()
	                            .setTextAlignment(TextAlignment.RIGHT).setMarginTop(4f);
	                    document.add(headingTotalPara);
	                }

	                Paragraph grandTotalPara = new Paragraph("Grand Total: "
	                        + (extraCharges.getGrandTotal() != null ? extraCharges.getGrandTotal().toPlainString()
	                                : "0"))
	                        .setFont(basicFont).setFontSize(14).setFontColor(blackColor).simulateBold()
	                        .setTextAlignment(TextAlignment.RIGHT).setMarginTop(10f).setUnderline();
	                document.add(grandTotalPara);
	            }
	        }

	        if (isTermsCondition == 1) {
	            Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
	                    .findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

	            if (terms != null && terms.isPresent()) {
	                EventTermsAndConditionEntity eventTermsEntity = terms.get();
	                List<EventTermsAndConditionFeaturesEntity> features = eventTermsAndConditionFeaturesRepository
	                        .findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

	                if (features != null && !features.isEmpty()) {
	                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

	                    Paragraph termsTitle = new Paragraph("Special Instruction").simulateBold().setFont(basicFont)
	                            .setFontSize(16).setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f)
	                            .setFontColor(blackColor);

	                    document.add(termsTitle);

	                    ISplitCharacters breakAll = new ISplitCharacters() {
	                        @Override
	                        public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
	                            return true;
	                        }
	                    };

	                    int index = 1;
	                    for (EventTermsAndConditionFeaturesEntity feature : features) {
	                        String desc;
	                        if (lang == 1) {
	                            desc = feature.getDescriptionHindi();
	                        } else if (lang == 2) {
	                            desc = feature.getDescriptionGujarati();
	                        } else {
	                            desc = feature.getDescription();
	                        }

	                        Paragraph term = new Paragraph(index + ". " + desc).setFont(basicFont).setFontSize(12)
	                                .setWidth(UnitValue.createPercentValue(90f))
	                                .setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
	                                .setSplitCharacters((text, glyphPos) -> true).setFontColor(blackColor);

	                        term.setSplitCharacters(breakAll);

	                        document.add(term);
	                        index++;
	                    }
	                }
	            }
	        }

	        document.flush();

	        int totalPages = pdfDocument.getNumberOfPages();
	        for (int i = 1; i <= totalPages; i++) {
	            PdfPage page = pdfDocument.getPage(i);
	            Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());
	            canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 2,
	                    TextAlignment.CENTER);
	            canvas.close();
	        }

	        document.close();

	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
	                + "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
	                        "back office report")
	                + ".pdf";
	        return fullUrl;

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate Event Menu Report", e);
	    }
	}
	
	@Override
	public String getMenuPlanningSimpleReport7(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails, ReportMenuPlanningRequestDTO req) {
		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;

			PdfFont prefFont = null;
			PdfFont prefboldFont = null;

			menuPreparationServiceImpl.loadLicense();

			Object result = menuPreparationDetailsRepository.getLanguageSpecification(userid);

			if (result == null) {
				return null;
			}

			Object[] langRow = (Object[]) result;
			String defaultLanguage = langRow[0].toString();
			String preferedLanguage = langRow[1].toString();

//			String defaultLanguage = "en";
//			String preferedLanguage = "gu";
			
			// English
			String customerName = "Customer Name :";
			String eName = "Event Name :";
			String bookingDate = "Booking Date :";
			String address = "Venue :";
			String mobileNoLabel = "Mobile No. :";
			String foodNote = "Food Note :";
			String peopleLabel = "People";
			String menusLabel = "Menu(s)";
			String dateTimeLabel = "Date & Time";
			String eventNotes = "Event Notes";
			String serviceLabel = "Service :";
			String themeLabel = "Theme :";
			String note = "Note";

			if (defaultLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				customerName = "ग्राहक का नाम :";
				eName = "कार्यक्रम का नाम :";
				bookingDate = "बुकिंग की दिनांक :";
				address = "स्थान :";
				mobileNoLabel = "मोबाइल नंबर :";
				foodNote = "भोजन नोट :";
				peopleLabel = "लोग";
				menusLabel = "मेनू(स)";
				dateTimeLabel = "दिनांक और समय";
				eventNotes = "कार्यक्रम की नोट";
				serviceLabel = "सर्विस :";
				themeLabel = "थीम :";
				note = "नोट : ";
				lang = 1;
			} else if (defaultLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				customerName = "ગ્રાહકનું નામ :";
				eName = "ઇવેન્ટનું નામ :";
				bookingDate = "બુકિંગની તારીખ :";
				address = "સ્થળ :";
				mobileNoLabel = "મોબાઇલ નંબર :";
				foodNote = "ભોજન નોંધ :";
				peopleLabel = "લોકો";
				menusLabel = "મેનૂ(ઓ)";
				dateTimeLabel = "તારીખ અને સમય";
				eventNotes = "ઇવેન્ટની નોંધ";
				serviceLabel = "સર્વિસ :";
				themeLabel = "થીમ :";
				note = "નોંધ : ";
				lang = 2;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				lang = 0;
			}

			if (preferedLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
			} else if (preferedLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
			} else {
				// English
				System.out.println("Loading English font...");
				prefFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				prefboldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			PdfFont labelFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
			PdfFont itemFont = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid, defaultLanguage, preferedLanguage);

			if (eventDto == null) {
				return "Data not found";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "back office report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(25, 20, 25, 20);

			Color blackColor = new DeviceRgb(0, 0, 0);
			
			// ============ PAGE 1: Event Detail Page ============
			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			String venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();
			String remarks = eventDto.getRemark() != null ? eventDto.getRemark() : "";
			String service = eventDto.getService() != null ? eventDto.getService() : "";
			String theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			String logo = eventDto.getLogo();

			if (foodNotes != null && !foodNotes.isEmpty()) {
				foodNotesName.append("( ");
				foodNotesName.append(foodNotes);
				foodNotesName.append(" )");
			}

			if (isCompanyDetails == 1) {
				String watermarkPath = environment.getProperty("app.image.url") + eventDto.getLogo();
//				String watermarkPath = "/flipbook/pages/krishnai_logo.png";
				pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(watermarkPath));
			}

//			ImageData imgData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
//			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler("/flipbook/pages/krishnai_logo.png"));
//			Image img = new Image(imgData);

			Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));
			img.setWidth(190);
			img.setHorizontalAlignment(HorizontalAlignment.CENTER);
			img.setMarginTop(80);
			img.setMarginBottom(5);

			if (isCompanyDetails == 1) {
				document.add(img);
			}

			// ============ PAGE 1: Front Page (Main) ============
			Paragraph p = new Paragraph("Event Information")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18f)
					.setTextAlignment(TextAlignment.CENTER); // x, y,
			if (isCompanyDetails == 1) {
				p.setMarginTop(60f);
			} else {
				p.setMarginTop(270f);
			}
			document.add(p);

			Color lineColor = new DeviceRgb(179, 179, 179);

			Table eventDetail = new Table(UnitValue.createPercentArray(new float[] { 30f, 70f }), false);
			eventDetail.setWidth(UnitValue.createPercentValue(70f));
			eventDetail.setHorizontalAlignment(HorizontalAlignment.CENTER);
			eventDetail.setMarginTop(0f);
//			eventDetail.setBorder(new SolidBorder(lineColor, 1f));
			eventDetail.setBorderTop(new SolidBorder(lineColor, 1f));
			eventDetail.setBorderBottom(new SolidBorder(lineColor, 1f));

			Cell label = new Cell();
			Cell value = new Cell();

			// Customer Name

			if (isPartyDetails == 1) {
				label = new Cell()
						.add(new Paragraph(customerName).setFont(basicFont).setFontSize(13f)
								.setTextAlignment(TextAlignment.RIGHT))
						.setBorder(Border.NO_BORDER).setPadding(4f).setBorderBottom(new SolidBorder(lineColor, 1f));
				eventDetail.addCell(label);

				value = new Cell()
						.add(new Paragraph(safeText(hostName).toUpperCase()).setFont(basicFont).setFontSize(13f)
								.setTextAlignment(TextAlignment.LEFT))
						.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(lineColor, 1f));
				eventDetail.addCell(value);
			}
			// Event Name
			label = new Cell()
					.add(new Paragraph(eName).setFont(basicFont).setFontSize(13f).setTextAlignment(TextAlignment.RIGHT))
					.setPadding(4f).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(lineColor, 1f));
			eventDetail.addCell(label);

			value = new Cell()
					.add(new Paragraph(safeText(eventName).toUpperCase()).setFont(basicFont).setFontSize(13f)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(4f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
					.setBorderBottom(new SolidBorder(lineColor, 1f));
			eventDetail.addCell(value);

			// Event Date
			label = new Cell()
					.add(new Paragraph(bookingDate).setFont(basicFont).setFontSize(13f)
							.setTextAlignment(TextAlignment.RIGHT))
					.setPadding(3f).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(lineColor, 1f)); // x,
																													// y,
																													// width
			eventDetail.addCell(label);

			value = new Cell()
					.add(new Paragraph(safeText(eventDate).toUpperCase()).setFont(basicFont).setFontSize(13f)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
					.setBorderBottom(new SolidBorder(lineColor, 1f));
			eventDetail.addCell(value);

			// Venue
			label = new Cell()
					.add(new Paragraph(address).setFont(basicFont).setFontSize(13f)
							.setTextAlignment(TextAlignment.RIGHT))
					.setPadding(3f).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(lineColor, 1f)); // x,
																													// y,
																													// width
			eventDetail.addCell(label);

			value = new Cell()
					.add(new Paragraph(safeText(venue).toUpperCase()).setFont(basicFont).setFontSize(13f)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
					.setBorderBottom(new SolidBorder(lineColor, 1f));
			eventDetail.addCell(value);

			// Mobile No.

			if (isPartyDetails == 1) {
				label = new Cell()
						.add(new Paragraph(mobileNoLabel).setFont(basicFont).setFontSize(13f)
								.setTextAlignment(TextAlignment.RIGHT))
						.setPadding(3f).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(lineColor, 1f)); // x,
																														// y,
																														// width
				eventDetail.addCell(label);

				value = new Cell()
						.add(new Paragraph(safeText(mobileNo)).setFont(basicFont).setFontSize(14f)
								.setTextAlignment(TextAlignment.LEFT))
						.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(lineColor, 1f));
				eventDetail.addCell(value);

			}

			if (foodNotesName != null && foodNotesName.toString().trim().length() != 0) {
				// Food
				label = new Cell()
						.add(new Paragraph(foodNote).setFont(basicFont).setFontSize(13f)
								.setTextAlignment(TextAlignment.RIGHT))
						.setPadding(3f).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(lineColor, 1f)); // x,
				eventDetail.addCell(label);

				value = new Cell()
						.add(new Paragraph(safeText(foodNotesName.toString()).toUpperCase()).setFont(basicFont)
								.setFontSize(13f).setTextAlignment(TextAlignment.LEFT))
						.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(lineColor, 1f));
				eventDetail.addCell(value);
			}

			if (remarks != null && remarks.trim().length() != 0) {
				// Remarks
				label = new Cell()
						.add(new Paragraph(eventNotes).setFont(basicFont).setFontSize(13f)
								.setTextAlignment(TextAlignment.RIGHT))
						.setPadding(3f).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(lineColor, 1f)); // x,
				eventDetail.addCell(label);

				value = new Cell()
						.add(new Paragraph(safeText(remarks.toString()).toUpperCase()).setFont(basicFont)
								.setFontSize(13f).setTextAlignment(TextAlignment.LEFT))
						.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(lineColor, 1f));
				eventDetail.addCell(value);
			}

			if (service != null && service.trim().length() != 0) {
				// Service
				label = new Cell()
						.add(new Paragraph(serviceLabel).setFont(basicFont).setFontSize(13f)
								.setTextAlignment(TextAlignment.RIGHT))
						.setPadding(3f).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(lineColor, 1f)); // x,
				eventDetail.addCell(label);

				value = new Cell()
						.add(new Paragraph(safeText(service.toString()).toUpperCase()).setFont(basicFont)
								.setFontSize(13f).setTextAlignment(TextAlignment.LEFT))
						.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(lineColor, 1f));
				eventDetail.addCell(value);
			}

			if (theme != null && theme.trim().length() != 0) {
				// Theme
				label = new Cell().add(new Paragraph(themeLabel).setFont(basicFont).setFontSize(13f)
						.setTextAlignment(TextAlignment.RIGHT)).setPadding(3f).setBorder(Border.NO_BORDER); // x,
				eventDetail.addCell(label);

				value = new Cell()
						.add(new Paragraph(safeText(theme.toString()).toUpperCase()).setFont(basicFont).setFontSize(13f)
								.setTextAlignment(TextAlignment.LEFT))
						.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER);
				eventDetail.addCell(value);
			}

			document.add(eventDetail);

			Table cmpDetail = new Table(UnitValue.createPercentArray(new float[] { 100f }), false);
			cmpDetail.setWidth(UnitValue.createPercentValue(70f));
			cmpDetail.setHorizontalAlignment(HorizontalAlignment.CENTER);
			cmpDetail.setMarginTop(220f);
//			eventDetail.setBorder(new SolidBorder(lineColor, 1f));

			value = new Cell()
					.add(new Paragraph(safeText(cmpName).toUpperCase()).setPadding(0).setMargin(0).setFont(itemFont)
							.setMultipliedLeading(1f).setFontSize(10f).setTextAlignment(TextAlignment.CENTER))
					.setBorder(Border.NO_BORDER).setPadding(0);
			cmpDetail.addCell(value);

//			value = new Cell().add(new Paragraph(safeText("Premium Wedding & Event Catering Services").toUpperCase())
//					.setPadding(0).setMargin(0).setFont(itemFont).setMultipliedLeading(1f).setFontSize(10f)
//					.setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER).setPadding(0);
//			cmpDetail.addCell(value);

			value = new Cell()
					.add(new Paragraph(safeText(cmpAddress).toUpperCase()).setPadding(0).setMargin(0).setFont(itemFont)
							.setMultipliedLeading(1f).setFontSize(10f).setTextAlignment(TextAlignment.CENTER))
					.setBorder(Border.NO_BORDER).setPadding(0);
			cmpDetail.addCell(value);

			value = new Cell().add(new Paragraph("Mobile no. : " + countryCode + " " + cmpPhone + " | Email:  " + email)
					.setPadding(0).setMargin(0).setFont(itemFont).setMultipliedLeading(1f).setFontSize(10f)
					.setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER).setPadding(0);
			cmpDetail.addCell(value);

			if (isCompanyDetails == 1) {
				document.add(cmpDetail);
			}

			String fn_name = "";

			// ============ MENU CONTENT PAGES ============
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				// Always start each function on a new page
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				String functionNote = "";
				if (lang == 1) {
					functionNote = eventFunctionMasterResponseDto.getNotesHindi() != null
							? eventFunctionMasterResponseDto.getNotesHindi()
							: "";
				} else if (lang == 2) {
					functionNote = eventFunctionMasterResponseDto.getNotesGujarati() != null
							? eventFunctionMasterResponseDto.getNotesGujarati()
							: "";
				} else {
					functionNote = eventFunctionMasterResponseDto.getNotesEnglish() != null
							? eventFunctionMasterResponseDto.getNotesEnglish()
							: "";
				}

				Paragraph heading = new Paragraph().add(new Text("Menu Information"))
						.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18f)
						.setWidth(UnitValue.createPercentValue(100)).setTextAlignment(TextAlignment.CENTER);

				document.add(heading);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();
				int menus = 0;
				for (MenuReportResponseDto menu : menuReportResponseDtos) {
					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						menus++;
					}
				}

				Table funTable = new Table(UnitValue.createPercentArray(new float[] { 60f, 40f }), false);
				funTable.setWidth(UnitValue.createPercentValue(100f));
				funTable.setBorderBottom(new SolidBorder(ColorConstants.GRAY, 1f));
				funTable.setMarginBottom(20f);
				funTable.setMarginTop(0f);

				String dateTime = eventFunctionMasterResponseDto.getFunctionStartTimestamp().toUpperCase() + " to "
						+ eventFunctionMasterResponseDto.getFunctionEndTimestamp().toUpperCase().substring(11);

				value = new Cell()
						.add(new Paragraph(safeText(eventFunctionMasterResponseDto.getFunctionName()) + " | "
								+ safeText(eventFunctionMasterResponseDto.getPax().toString()) + " " + peopleLabel
								+ " | " + menusLabel + " " + menus).setFont(boldFont).setFontSize(14f)
								.setTextAlignment(TextAlignment.LEFT))
						.setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(3f).setBorder(Border.NO_BORDER);
				funTable.addCell(value);

				label = new Cell().add(new Paragraph().add(new Text(dateTimeLabel + " : ").setFont(boldFont))
						.add(new Text("\n" + dateTime).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
						.setFontSize(14f).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER);
				funTable.addCell(label);

				if (functionNote.trim().length() != 0) {
					label = new Cell(1, 2)
							.add(new Paragraph().add(new Text(note + " : ").setFont(boldFont))
									.add(new Text("\n" + functionNote)
											.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
									.setFontSize(14f).setTextAlignment(TextAlignment.LEFT))
							.setBorder(Border.NO_BORDER);
					funTable.addCell(label);
				}

				document.add(funTable);

				Table table = new Table(1);
				table.setBorder(Border.NO_BORDER);
				table.setWidth(UnitValue.createPercentValue(100));

				// Add menu content
				for (MenuReportResponseDto menu : menuReportResponseDtos) {
					Div div = new Div();
					div.setKeepTogether(true);
					div.setBorder(Border.NO_BORDER);
					div.setPaddingTop(10f);

					Paragraph p1 = new Paragraph().add(new Text(menu.getNameHindi() + " (").setFont(basicFont))
							.add(new Text(menu.getNameGujarati()).setFont(prefFont))
							.add(new Text(") ").setFont(basicFont)).setFontSize(16f).setFontColor(ColorConstants.RED)
							.setTextAlignment(TextAlignment.CENTER).setPadding(0).setFixedLeading(16f);
					div.add(p1).setBorder(Border.NO_BORDER);

					String text = "";
					// Category Slogan
//					if (isCategorySlogan != null && isCategorySlogan == 1) {
//						System.out.println("slogan : " + menu.getSlogan());
//						if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
//							text = " \"" + menuPreparationServiceImpl.formatText(menu.getSlogan(), lang) + "\"";
//							div.add(new Paragraph(text).setFont(basicFont).setFontSize(13f)
//									.setTextAlignment(TextAlignment.CENTER).setFixedLeading(13f))
//									.setBorder(Border.NO_BORDER);
//						}
//					}

					// Category Instructions
					if (isCategoryInstruction != null && isCategoryInstruction == 1) {
						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
							text = "  (" + menuPreparationServiceImpl.formatText(menu.getMenuNotes(), lang) + ") ";
							div.add(new Paragraph(text).setFont(basicFont).setFontSize(13f)
									.setTextAlignment(TextAlignment.CENTER).setFixedLeading(13f))
									.setBorder(Border.NO_BORDER);
						}
					}
					div.add(new Paragraph("").setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 1f)));

//					if (isCategoryImage == 1) {
//						// Category Image
//						if (isCategoryImage != null && isCategoryImage == 1) {
//							if (menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
//								try {
//									img = new Image(ImageDataFactory
//											.create(environment.getProperty("app.image.url") + menu.getImagePath()));
//
//									img.setAutoScale(true);
//									img.setHorizontalAlignment(HorizontalAlignment.CENTER);
//									img.setMarginTop(5);
//									img.setMarginBottom(5);
//
//									div.add(img).setBorder(Border.NO_BORDER);
//								} catch (Exception e) {
//									// Skip if image loading fails
//								}
//							}
//						}
//					}

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

						div.add(new Paragraph().add(new Text(item.getNameHindi() + " (").setFont(basicFont))
								.add(new Text(item.getNameGujarati()).setFont(prefFont))
								.add(new Text(") ").setFont(basicFont)).setFontSize(14f)
								.setTextAlignment(TextAlignment.CENTER).setMarginTop(3f).setFixedLeading(14f))
								.setBorder(Border.NO_BORDER);

//						if (isItemSlogan != null && isItemSlogan == 1) {
//							if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
//								text = " \"" + menuPreparationServiceImpl.formatText(item.getSlogan(), lang) + "\"";
//								div.add(new Paragraph(text).setFont(basicFont).setFontSize(13f)
//										.setTextAlignment(TextAlignment.CENTER).setFixedLeading(13f))
//										.setBorder(Border.NO_BORDER);
//
//							}
//						}

						// Item Instructions
						if (isItemInstruction != null && isItemInstruction == 1) {
							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
								text = "  (" + menuPreparationServiceImpl.formatText(item.getItemNotes(), lang) + ") ";
								div.add(new Paragraph(text).setFont(basicFont).setFontSize(13f)
										.setTextAlignment(TextAlignment.CENTER).setFixedLeading(13f))
										.setBorder(Border.NO_BORDER);

							}
						}
					}
					table.addCell(new Cell().add(div).setBorder(Border.NO_BORDER).setPadding(0));
				}
				document.add(table);
			}

			Integer isExtraCharges = req.getIsExtraCharges() != null ? req.getIsExtraCharges() : 0;

			if (isExtraCharges != null && isExtraCharges == 1) {

				ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
						userid);

				if (extraCharges != null && extraCharges.getHeadings() != null
						&& !extraCharges.getHeadings().isEmpty()) {

					DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
					DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					// ── Main Title: "-: EXTRA CHARGE :-" ─────────────────────────────
					Paragraph extraTitle = new Paragraph("-: EXTRA CHARGE :-").setFont(basicFont).setFontSize(18)
							.setFontColor(blackColor).setTextAlignment(TextAlignment.CENTER).setUnderline()
							.simulateBold().setMarginBottom(15f);
					document.add(extraTitle);

					// ── Loop each heading ─────────────────────────────────────────────
					for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges.getHeadings()) {

						// Heading title: HEADING NAME
						Paragraph headingTitle = new Paragraph(heading.getHeadingName().toUpperCase())
								.setFont(basicFont).setFontSize(14).setFontColor(blackColor).simulateBold()
								.setUnderline().setTextAlignment(TextAlignment.LEFT).setMarginTop(12f)
								.setMarginBottom(6f);
						document.add(headingTitle);

						// ── Table: DATE | START TIME | END TIME | SESSION | QTY | RATE | TOTAL ──
						float[] colWidths = { 15f, 17f, 15f, 13f, 12f, 12f, 16f };
						Table chargeTable = new Table(UnitValue.createPercentArray(colWidths));
						chargeTable.setWidth(UnitValue.createPercentValue(100));
						chargeTable.setBorder(new SolidBorder(blackColor, 1f));

						// ── Header Row ────────────────────────────────────────────────
						String[] colHeaders = { "DATE", "START TIME", "END TIME", "SESSION", "QTY", "RATE", "TOTAL" };
						for (String h : colHeaders) {
							chargeTable.addHeaderCell(new Cell()
									.add(new Paragraph(h).setFont(basicFont).setFontSize(11).setFontColor(blackColor)
											.simulateBold().setUnderline())
									.setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(blackColor, 0.8f))
									.setPaddingTop(5f).setPaddingBottom(5f));
						}

						// ── Data Rows ─────────────────────────────────────────────────
						if (heading.getRows() != null && !heading.getRows().isEmpty()) {
							for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {

								chargeTable.addCell(dataCell(
										row.getChargeDate() != null ? row.getChargeDate().format(dateFormatter) : "",
										basicFont, blackColor, 10, blackColor));

								chargeTable.addCell(dataCell(row.getChargeStartTime() != null
										? row.getChargeStartTime().format(timeFormatter)
										: "", basicFont, blackColor, 10, blackColor));

								chargeTable.addCell(dataCell(
										row.getChargeEndTime() != null ? row.getChargeEndTime().format(timeFormatter)
												: "",
										basicFont, blackColor, 10, blackColor));

								chargeTable.addCell(
										dataCell(row.getSession() != null ? String.valueOf(row.getSession()) : "0",
												basicFont, blackColor, 10, blackColor));

								chargeTable.addCell(dataCell(
										row.getPersonItem() != null ? String.valueOf(row.getPersonItem()) : "0",
										basicFont, blackColor, 10, blackColor));

								chargeTable
										.addCell(dataCell(row.getRate() != null ? row.getRate().toPlainString() : "0",
												basicFont, blackColor, 10, blackColor));

								chargeTable
										.addCell(dataCell(row.getTotal() != null ? row.getTotal().toPlainString() : "0",
												basicFont, blackColor, 10, blackColor));
							}
						} else {
							// Empty rows for visual layout (like the sample image)
							for (int i = 0; i < 7; i++) {
								for (int j = 0; j < 7; j++) {
									chargeTable.addCell(dataCell("", basicFont, blackColor, 10, blackColor));
								}
							}
						}

						document.add(chargeTable);

						// ── Heading Total ─────────────────────────────────────────────
						Paragraph headingTotalPara = new Paragraph("Total: "
								+ (heading.getHeadingTotal() != null ? heading.getHeadingTotal().toPlainString() : "0"))
								.setFont(basicFont).setFontSize(12).setFontColor(blackColor).simulateBold()
								.setTextAlignment(TextAlignment.RIGHT).setMarginTop(4f);
						document.add(headingTotalPara);
					}

					// ── Grand Total ───────────────────────────────────────────────────
					Paragraph grandTotalPara = new Paragraph("Grand Total: "
							+ (extraCharges.getGrandTotal() != null ? extraCharges.getGrandTotal().toPlainString()
									: "0"))
							.setFont(basicFont).setFontSize(14).setFontColor(blackColor).simulateBold()
							.setTextAlignment(TextAlignment.RIGHT).setMarginTop(10f).setUnderline();
					document.add(grandTotalPara);
				}
			}

			Integer isTermsCondition = req.getIsTermsCond() != null ? req.getIsTermsCond() : 0;
			
			if (isTermsCondition == 1) {
				Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

				if (terms != null && terms.isPresent()) {
					EventTermsAndConditionEntity eventTermsEntity = terms.get();
					List<EventTermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

					if (features != null && !features.isEmpty()) {
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

						Paragraph termsTitle = new Paragraph("Special Instruction").simulateBold().setFont(basicFont)
								.setFontSize(16).setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f)
								.setFontColor(blackColor);

						document.add(termsTitle);

						ISplitCharacters breakAll = new ISplitCharacters() {
							@Override
							public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
								return true; // break anywhere
							}
						};

						int index = 1;
						for (EventTermsAndConditionFeaturesEntity feature : features) {
							String desc;

							if (lang == 1) {
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(basicFont).setFontSize(12)
									.setWidth(UnitValue.createPercentValue(90f))
									.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
									.setSplitCharacters((text, glyphPos) -> true).setFontColor(blackColor);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}
			
			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 10,
						TextAlignment.CENTER);

				canvas.close();
			}
			
			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"back office report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	private String getLogo(Long userId) {
		/* ================= HEADER ================= */
		Optional<UserMasterEntity> userLst = userMasterRepository.findByIdAndIsDeleteFalse(userId);
		if (!userLst.isPresent() || userLst.get().getLogo() == null || "".equals(userLst.get().getLogo())) {
			throw new RuntimeException("User Logo not found");
		}
		String logoimg = environment.getProperty("app.image.url") + userLst.get().getLogo();
//        String logoimg = "/flipbook/sample_watermark.png";
		System.out.println("get Logo : " + logoimg);
		return logoimg;
	}

	@Override
	public String remaingDataReport(Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid) {
		// TODO Auto-generated method stub
		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			String labour = "Labour";
			String dateTimeLabel = "Date & Time";
			String shiftName = "Shift";
			String persons = "Persons";
			String note = "Note";
			String customerName = "Customer Name";
			String function = "Function";
			String venueName = "Venue";

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				labour = "श्रमिक";
				dateTimeLabel = "दिनांक एवं समय";
				shiftName = "कार्य शिफ्ट";
				persons = "व्यक्ति";
				note = "टिप्पणी";
				customerName = "ग्राहक का नाम";
				function = "कार्यक्रम";
				venueName = "स्थल";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				labour = "મજૂર";
				dateTimeLabel = "તારીખ અને સમય";
				shiftName = "કાર્ય શિફ્ટ";
				persons = "વ્યક્તિઓ";
				note = "નોંધ";
				customerName = "ગ્રાહકનું નામ";
				function = "કાર્યક્રમ";
				venueName = "સ્થળ";
			} else {
				// English
				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
			}

			GetEventLaborResponseDto cmpData = new GetEventLaborResponseDto();
			String eventNo = "";
			cmpData = eventLaborServiceImpl.getCmpData(userid, lang);

			Long uId = cmpData.getUserId();
			String cmpName = cmpData.getCompanyName() == null || cmpData.getCompanyName().isEmpty() ? ""
					: cmpData.getCompanyName();
			String cmpEmail = cmpData.getCompanyEmail() == null || cmpData.getCompanyEmail().isEmpty() ? ""
					: cmpData.getCompanyEmail();
			String cmpCountryCode = cmpData.getCountryCode() == null || cmpData.getCountryCode().isEmpty() ? ""
					: cmpData.getCountryCode();
			String cmpMobile = cmpData.getCompanyMobile() == null || cmpData.getCompanyMobile().isEmpty() ? ""
					: cmpData.getCompanyMobile();
			String logo = cmpData.getLogo() == null || cmpData.getLogo().isEmpty() ? "" : cmpData.getLogo();

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + userid);

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			File pdfFile = new File(outputPath + "/remaing_data_" + datetimeforfile + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(20, 20, 20, 20);

			float[] columnWidthHead = { 25f, 15f, 2f, 57f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));
			cmpHead.setMarginBottom(10f);

//			ImageData logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");
//			Image img = new Image(logoData);
			Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));
			img.setWidth(120);
			img.setHorizontalAlignment(HorizontalAlignment.LEFT);

			Cell cmp = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setPadding(0).setPaddingLeft(10f)
					.setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			Paragraph cmpPara = new Paragraph().add(
					new Text(cmpName).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			cmp = new Cell(1, 3).add(cmpPara).setPadding(0).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text("Mobile No.").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(new Text(cmpCountryCode + " " + cmpMobile)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text("Email").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text(cmpEmail).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			if (isCompanyDetails == 1) {
				document.add(cmpHead);
			}

			List<RemaingDataResponseDto> dataResponseDtos = menuPreparationServiceImpl
					.getItemOfRemaingRawMaterial(userid, lang);

			Paragraph heading = new Paragraph().add(new Text("Menu Items (Recipy Remaing)")).setFont(boldFont)
					.setFontSize(16).setWidth(UnitValue.createPercentValue(100)).setTextAlignment(TextAlignment.CENTER)
					.setPadding(0).setMarginLeft(5f).setVerticalAlignment(VerticalAlignment.MIDDLE);

			document.add(heading);

			Paragraph dataPara = null;
			Cell dataCell = null;
			float[] cw = { 33f, 33f, 33f };
			Table data = new Table(UnitValue.createPercentArray(cw));
			data.setWidth(UnitValue.createPercentValue(100));

			for (RemaingDataResponseDto dto : dataResponseDtos) {
				String itemName = dto.getItemName() != null ? dto.getItemName() : "";
				dataPara = new Paragraph().add(new Text(itemName).setFont(basicFont).setFontSize(14f))
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(5f).setMargin(0);
				dataCell = new Cell().add(dataPara);
				data.addCell(dataCell);
			}

			document.add(data);

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			dataResponseDtos = menuPreparationServiceImpl.getItemOfRemaingRecipyRate(userid, lang);

			Map<Long, Map<String, List<RemaingDataResponseDto>>> groupedData = new LinkedHashMap<>();

			for (RemaingDataResponseDto dto : dataResponseDtos) {
				Long menuItemId = dto.getMenuItemId();
				String itemName = dto.getItemName() != null ? dto.getItemName() : "Unknown Item";

				groupedData.putIfAbsent(menuItemId, new LinkedHashMap<>());
				groupedData.get(menuItemId).putIfAbsent(itemName, new ArrayList<>());
				groupedData.get(menuItemId).get(itemName).add(dto);
			}

			heading = new Paragraph().add(new Text("Menu Items (Recipe Rate Remaing)")).setFont(boldFont)
					.setFontSize(16).setWidth(UnitValue.createPercentValue(100)).setTextAlignment(TextAlignment.CENTER)
					.setPadding(0).setMarginLeft(5f).setVerticalAlignment(VerticalAlignment.MIDDLE);

			document.add(heading);

			dataPara = null;
			dataCell = null;
			Div itemWithRawMaterial = null;
			data = new Table(UnitValue.createPercentArray(cw));
			data.setWidth(UnitValue.createPercentValue(100));

			for (Map.Entry<Long, Map<String, List<RemaingDataResponseDto>>> menuEntry : groupedData.entrySet()) {
				for (Map.Entry<String, List<RemaingDataResponseDto>> itemEntry : menuEntry.getValue().entrySet()) {
					String item = itemEntry.getKey();
					List<RemaingDataResponseDto> dtos = itemEntry.getValue();
					itemWithRawMaterial = new Div().setKeepTogether(true);
					dataPara = new Paragraph().add(new Text(item).setFont(basicFont).setFontSize(14f))
							.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(5f).setMargin(0);
					for (RemaingDataResponseDto dto : dtos) {
						Paragraph rawMaterialItem = new Paragraph()
								.add(new Text("• " + dto.getRawMaterialName()).setFont(basicFont).setFontSize(12f))
								.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f).setMargin(0);

						dataPara.add(rawMaterialItem);
					}
					itemWithRawMaterial.add(dataPara);
					dataCell = new Cell().add(itemWithRawMaterial);
					data.addCell(dataCell);
				}
			}

			document.add(data);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			dataResponseDtos = menuPreparationServiceImpl.getItemOfRemaingSlogan(userid, lang);

			heading = new Paragraph().add(new Text("Menu Items (Slogan Remaing)")).setFont(boldFont).setFontSize(16)
					.setWidth(UnitValue.createPercentValue(100)).setTextAlignment(TextAlignment.CENTER).setPadding(0)
					.setMarginLeft(5f).setVerticalAlignment(VerticalAlignment.MIDDLE);

			document.add(heading);

			dataPara = null;
			dataCell = null;
			data = new Table(UnitValue.createPercentArray(cw));
			data.setWidth(UnitValue.createPercentValue(100));

			for (RemaingDataResponseDto dto : dataResponseDtos) {
				String itemName = dto.getItemName() != null ? dto.getItemName() : "";
				dataPara = new Paragraph().add(new Text(itemName).setFont(basicFont).setFontSize(14f))
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(5f).setMargin(0);
				dataCell = new Cell().add(dataPara);
				data.addCell(dataCell);
			}

			document.add(data);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			dataResponseDtos = menuPreparationServiceImpl.getItemOfRemaingImage(userid, lang);

			heading = new Paragraph().add(new Text("Menu Items (Image Remaing)")).setFont(boldFont).setFontSize(16)
					.setWidth(UnitValue.createPercentValue(100)).setTextAlignment(TextAlignment.CENTER).setPadding(0)
					.setMarginLeft(5f).setVerticalAlignment(VerticalAlignment.MIDDLE);

			document.add(heading);

			dataPara = null;
			dataCell = null;
			data = new Table(UnitValue.createPercentArray(cw));
			data.setWidth(UnitValue.createPercentValue(100));

			for (RemaingDataResponseDto dto : dataResponseDtos) {
				String itemName = dto.getItemName() != null ? dto.getItemName() : "";
				dataPara = new Paragraph().add(new Text(itemName).setFont(basicFont).setFontSize(14f))
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(5f).setMargin(0);
				dataCell = new Cell().add(dataPara);
				data.addCell(dataCell);
			}

			document.add(data);

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + userid + "/remaing_data_"
					+ datetimeforfile + ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate report");
		}
	}

	@Override
	public String dishCountingReport(Long eventId, Integer isCompanyDetails, HttpServletRequest re, int lang,
			Long userid, Integer isPartyDetails) {
		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			String labour = "Labour";
			String shiftName = "Shift";
			String function = "Function";
			String venueName = "Venue";
			String customerName = "Customer Name";
			String mobileLabel = "Mobile No";
			String dateTimeLabel = "Date & Time";
			String note = "Note";
			String timeLable = "Time";
			String persons = "Persons";
			String dishCounting = "Dish Counting";
			String extraDish = "Extra Dish";
			String testing = "Testing";
			String sign = "Signature";
			String feedbackForm = "Feedback Form";
			String managerSign = "Manager Signature";
			String nameLabel = "Name";
			String reportHeading = "Dish Counting Report";

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				labour = "श्रमिक";
				dateTimeLabel = "दिनांक एवं समय";
				timeLable = "समय";
				shiftName = "कार्य शिफ्ट";
				persons = "व्यक्ति";
				note = "नोट";
				customerName = "ग्राहक का नाम";
				mobileLabel = "मोबाइल नंबर";
				function = "कार्यक्रम";
				venueName = "स्थल";
				dishCounting = "डिश काउंटिंग";
				extraDish = "एक्स्ट्रा डिश";
				testing = "टेस्टिंग";
				sign = "हस्ताक्षर";
				feedbackForm = "फीडबैक फॉर्म";
				managerSign = "मैनेजर हस्ताक्षर";
				nameLabel = "नाम";
				reportHeading = "डिश काउंटिंग रिपोर्ट";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				labour = "મજૂર";
				dateTimeLabel = "તારીખ અને સમય";
				mobileLabel = "મોબાઇલ નંબર";
				shiftName = "કાર્ય શિફ્ટ";
				persons = "વ્યક્તિઓ";
				note = "નોંધ";
				customerName = "ગ્રાહકનું નામ";
				function = "કાર્યક્રમ";
				venueName = "સ્થળ";
				dishCounting = "ડિશ કાઉન્ટિંગ";
				timeLable = "સમય";
				extraDish = "એક્સ્ટ્રા ડિશ";
				testing = "ટેસ્ટિંગ";
				sign = "હસ્તાક્ષર";
				feedbackForm = "ફીડબેક ફોર્મ";
				managerSign = "મેનેજર હસ્તાક્ષર";
				nameLabel = "નામ";
				reportHeading = "ડિશ કાઉન્ટિંગ રિપોર્ટ";
			} else {
				// English
				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
			}

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);
			String eventNo = eventData.getEventNo();

			String cmpName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();
			String cmpEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();
			String cmpCountryCode = eventData.getCountryCode() == null || eventData.getCountryCode().isEmpty() ? ""
					: eventData.getCountryCode();
			String cmpMobile = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();
			String logo = eventData.getLogo() == null || eventData.getLogo().isEmpty() ? "" : eventData.getLogo();

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventData.getEventStartTime()), "dish counting report") + ".pdf");

//			File pdfFile = new File(outputPath + "/dish_costing_" + datetimeforfile + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);

			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 20, 30, 20);

			float[] columnWidthHead = { 25f, 15f, 2f, 57f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));
			cmpHead.setMarginBottom(10f);

//			ImageData logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");
//			Image img = new Image(logoData);
			Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));
			img.setWidth(120);
			img.setHorizontalAlignment(HorizontalAlignment.LEFT);

			Cell cmp = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setPadding(0).setPaddingLeft(10f)
					.setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			Paragraph cmpPara = new Paragraph().add(
					new Text(cmpName).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			cmp = new Cell(1, 3).add(cmpPara).setPadding(0).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text("Mobile No.").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMargin(0).setMultipliedLeading(0.9f);
			cmp = new Cell().add(cmpPara).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMargin(0).setMultipliedLeading(0.9f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpCountryCode + " " + cmpMobile)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMargin(0).setMultipliedLeading(0.9f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text("Email").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMargin(0).setMultipliedLeading(0.9f);
			cmp = new Cell().add(cmpPara).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMargin(0).setMultipliedLeading(0.9f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpEmail).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMargin(0).setMultipliedLeading(0.9f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			List<CostingReportResponseDto> allFunctions = menuItemRawMaterialServiceImpl
					.getAllFunctionDetailsForCosting(eventId, lang);

			for (int i = 0; i < allFunctions.size(); i++) {
				if (isCompanyDetails == 1) {
					document.add(cmpHead);
				}

				CostingReportResponseDto dto = allFunctions.get(i);

				Paragraph heading = new Paragraph().add(new Text(reportHeading).setFont(boldFont).setFontSize(18));
				heading.setHorizontalAlignment(HorizontalAlignment.CENTER);
				heading.setMargin(1f).setMultipliedLeading(1f);
				heading.setTextAlignment(TextAlignment.CENTER);
				heading.setWidth(UnitValue.createPercentValue(100));
				document.add(heading);

				// Create table with 4 columns
				float[] columnWidths = { 120f, 240f, 90f, 150f };
				Table table = new Table(UnitValue.createPointArray(columnWidths));
				table.setWidth(UnitValue.createPercentValue(100));
				table.setBorder(new SolidBorder(1f));
				table.setMarginBottom(15f);

				table.addCell(new Cell(1, 4)
						.add(new Paragraph(dto.getFunctionName()).setFont(boldFont).setFontSize(16).setMargin(0)
								.setMultipliedLeading(1f))
						.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(5)
						.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER));

				if (isPartyDetails == 1) {
					table.addCell(createLabelCell(customerName, boldFont));
					table.addCell(createValueCell(": " + dto.getPartyName(), basicFont));
					table.addCell(createLabelCell(mobileLabel, boldFont));
					table.addCell(createValueCell(": +91 " + dto.getPartyMobileNo(), basicFont));
				}
				table.addCell(createLabelCell(dateTimeLabel, boldFont));
				table.addCell(createValueCell(
						": " + dto.getFunctionStartTime() + " to" + dto.getFunctionEndTime().substring(10), basicFont));
				table.addCell(createLabelCell(persons, boldFont));
				table.addCell(createValueCell(": " + dto.getFunctionPerson().toString(), basicFont));

				table.addCell(createLabelCell(venueName, boldFont));

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, dto.getEventFunctionId());
				String functionVenue = "";
				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				} else {
					functionVenue = dto.getVenueName();
				}
				table.addCell(createValueCell(": " + functionVenue, basicFont));

				document.add(table);

				addSignatureForm(document, boldFont, basicFont, nameLabel, mobileLabel, dishCounting, extraDish,
						testing, timeLable, note, sign, feedbackForm);

//				addSignatureFooter(document, basicFont, sign, managerSign, lang);

				if (i < allFunctions.size() - 1) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
			}

			document.flush();
			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {

				PdfPage page = pdfDocument.getPage(i);
				Rectangle pageSize = page.getPageSize();

				PdfCanvas pdfCanvas = new PdfCanvas(page);
				Canvas canvas = new Canvas(pdfCanvas, pageSize);

				// Page Number
				canvas.showTextAligned("Page " + i + " of " + totalPages, pageSize.getWidth() / 2, 30,
						TextAlignment.CENTER);

				// Signature Table
				Table signatureTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
				signatureTable.setWidth(UnitValue.createPercentValue(100));

				Cell signatureCell = new Cell()
						.add(new Paragraph(sign + ": __________________").setFont(basicFont).setFontSize(10))
						.setBorder(Border.NO_BORDER).setPadding(0);

				Cell managerSignatureCell = new Cell().add(new Paragraph(managerSign + ": _______________")
						.setFont(basicFont).setFontSize(10).setTextAlignment(TextAlignment.RIGHT))
						.setBorder(Border.NO_BORDER).setPadding(0);

				signatureTable.addCell(signatureCell);
				signatureTable.addCell(managerSignatureCell);

				float y = 30;

				signatureTable.setFixedPosition(i, 36, y, pageSize.getWidth() - 72);

				canvas.add(signatureTable);

				canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventStartTime()),
							"dish counting report")
					+ ".pdf";

//			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + userid + "/dish_costing_"
//					+ datetimeforfile + ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed generate report.", e);
		}
	}

	@Override
	public String dishCountingReportSinglePage(Long eventId, Integer isCompanyDetails, HttpServletRequest re, int lang,
			Long userid, Long eventFunctionId, Integer isPartyDetails) {
		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			String labour = "Labour";
			String shiftName = "Shift";
			String function = "Function";
			String venueName = "Venue";
			String customerName = "Customer Name";
			String mobileLabel = "Mobile No";
			String dateTimeLabel = "Date & Time";
			String note = "Note";
			String timeLable = "Time";
			String persons = "Total Persons";
			String dishCounting = "Dish Counting";
			String extraDish = "Extra Dish";
			String testing = "Testing";
			String sign = "Signature";
			String feedbackForm = "Feedback Form";
			String managerSign = "Manager Signature";
			String nameLabel = "Name";
			String reportHeading = "Dish Counting Report";

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				labour = "श्रमिक";
				dateTimeLabel = "दिनांक एवं समय";
				timeLable = "समय";
				shiftName = "कार्य शिफ्ट";
				persons = "कुल व्यक्ति";
				note = "नोट";
				customerName = "ग्राहक का नाम";
				mobileLabel = "मोबाइल नंबर";
				function = "कार्यक्रम";
				venueName = "स्थल";
				dishCounting = "डिश काउंटिंग";
				extraDish = "एक्स्ट्रा डिश";
				testing = "टेस्टिंग";
				sign = "हस्ताक्षर";
				feedbackForm = "फीडबैक फॉर्म";
				managerSign = "मैनेजर हस्ताक्षर";
				nameLabel = "नाम";
				reportHeading = "डिश काउंटिंग रिपोर्ट";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				labour = "મજૂર";
				dateTimeLabel = "તારીખ અને સમય";
				mobileLabel = "મોબાઇલ નંબર";
				shiftName = "કાર્ય શિફ્ટ";
				persons = "કુલ વ્યક્તિઓ";
				note = "નોંધ";
				customerName = "ગ્રાહકનું નામ";
				function = "કાર્યક્રમ";
				venueName = "સ્થળ";
				dishCounting = "ડિશ કાઉન્ટિંગ";
				timeLable = "સમય";
				extraDish = "એક્સ્ટ્રા ડિશ";
				testing = "ટેસ્ટિંગ";
				sign = "હસ્તાક્ષર";
				feedbackForm = "ફીડબેક ફોર્મ";
				managerSign = "મેનેજર હસ્તાક્ષર";
				nameLabel = "નામ";
				reportHeading = "ડિશ કાઉન્ટિંગ રિપોર્ટ";
			} else {
				// English
				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
			}

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);
			String eventNo = eventData.getEventNo();

			String cmpName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();
			String cmpEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();
			String cmpCountryCode = eventData.getCountryCode() == null || eventData.getCountryCode().isEmpty() ? ""
					: eventData.getCountryCode();
			String cmpMobile = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();
			String logo = eventData.getLogo() == null || eventData.getLogo().isEmpty() ? "" : eventData.getLogo();

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventData.getEventStartTime()), "dish counting report") + ".pdf");

//			File pdfFile = new File(outputPath + "/dish_costing_" + datetimeforfile + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			PageNumberHandler pageHandler = new PageNumberHandler();

			Document document = new Document(pdfDocument);
			document.setMargins(20, 20, 30, 20);

			float[] columnWidthHead = { 25f, 15f, 2f, 57f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));
			cmpHead.setMarginBottom(10f);

//			ImageData logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");
//			Image img = new Image(logoData);
			Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));
			img.setWidth(120);
			img.setHorizontalAlignment(HorizontalAlignment.LEFT);

			Cell cmp = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setPadding(0).setPaddingLeft(10f)
					.setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			Paragraph cmpPara = new Paragraph().add(
					new Text(cmpName).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			cmp = new Cell(1, 3).add(cmpPara).setPadding(0).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text("Mobile No.").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMargin(0).setMultipliedLeading(0.9f);
			cmp = new Cell().add(cmpPara).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMargin(0).setMultipliedLeading(0.9f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpCountryCode + " " + cmpMobile)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMargin(0).setMultipliedLeading(0.9f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text("Email").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMargin(0).setMultipliedLeading(0.9f);
			cmp = new Cell().add(cmpPara).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMargin(0).setMultipliedLeading(0.9f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpEmail).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMargin(0).setMultipliedLeading(0.9f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			List<CostingReportResponseDto> allFunctions = menuItemRawMaterialServiceImpl
					.getAllFunctionDetailsForCosting(eventId, lang);

			if (isCompanyDetails == 1) {
				document.add(cmpHead);
			}

			List<CostingReportResponseDto> filteredFunctions = allFunctions.stream()
					.filter(f -> eventFunctionId == -1 || f.getEventFunctionId().equals(eventFunctionId))
					.collect(Collectors.toList());

			String functions = filteredFunctions.stream().map(f -> f.getFunctionName() + " - " + f.getFunctionPerson())
					.collect(Collectors.joining(", "));

			BigDecimal totalPax = filteredFunctions.stream().map(f -> f.getFunctionPerson()).reduce(BigDecimal.ZERO,
					BigDecimal::add);

			CostingReportResponseDto dto = allFunctions.get(0);

			Paragraph heading = new Paragraph().add(new Text(reportHeading).setFont(boldFont).setFontSize(18));
			heading.setHorizontalAlignment(HorizontalAlignment.CENTER);
			heading.setMargin(1f).setMultipliedLeading(1f);
			heading.setTextAlignment(TextAlignment.CENTER);
			heading.setWidth(UnitValue.createPercentValue(100));
			document.add(heading);

			// Create table with 4 columns
			float[] columnWidths = { 120f, 240f, 90f, 150f };
			Table table = new Table(UnitValue.createPointArray(columnWidths));
			table.setWidth(UnitValue.createPercentValue(100));
			table.setBorder(new SolidBorder(1f));
			table.setMarginBottom(15f);

			table.addCell(new Cell(1, 4)
					.add(new Paragraph(functions).setFont(boldFont).setFontSize(16).setMargin(0)
							.setMultipliedLeading(1f))
					.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(5)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER));
			if (isPartyDetails == 1) {
				table.addCell(createLabelCell(customerName, boldFont));
				table.addCell(createValueCell(": " + dto.getPartyName(), basicFont));
				table.addCell(createLabelCell(mobileLabel, boldFont));
				table.addCell(createValueCell(": +91 " + dto.getPartyMobileNo(), basicFont));
			}
			table.addCell(createLabelCell(persons, boldFont));
			table.addCell(createValueCell(": " + totalPax, basicFont));

			document.add(table);

			addSignatureForm(document, boldFont, basicFont, nameLabel, mobileLabel, dishCounting, extraDish, testing,
					timeLable, note, sign, feedbackForm);

//			addSignatureFooter(document, basicFont, sign, managerSign, lang);

			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {

				PdfPage page = pdfDocument.getPage(i);
				Rectangle pageSize = page.getPageSize();

				PdfCanvas pdfCanvas = new PdfCanvas(page);
				Canvas canvas = new Canvas(pdfCanvas, pageSize);

				// Page Number
				canvas.showTextAligned("Page " + i + " of " + totalPages, pageSize.getWidth() / 2, 30,
						TextAlignment.CENTER);

				// Signature Table
				Table signatureTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
				signatureTable.setWidth(UnitValue.createPercentValue(100));

				Cell signatureCell = new Cell()
						.add(new Paragraph(sign + ": __________________").setFont(basicFont).setFontSize(10))
						.setBorder(Border.NO_BORDER).setPadding(0);

				Cell managerSignatureCell = new Cell().add(new Paragraph(managerSign + ": _______________")
						.setFont(basicFont).setFontSize(10).setTextAlignment(TextAlignment.RIGHT))
						.setBorder(Border.NO_BORDER).setPadding(0);

				signatureTable.addCell(signatureCell);
				signatureTable.addCell(managerSignatureCell);

				float y = 30;

				signatureTable.setFixedPosition(i, 36, y, pageSize.getWidth() - 72);

				canvas.add(signatureTable);

				canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventStartTime()),
							"dish counting report")
					+ ".pdf";

//			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + userid + "/dish_costing_"
//					+ datetimeforfile + ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed generate report.", e);
		}
	}

	private static Cell createLabelCell(String text, PdfFont font) {
		return new Cell().add(new Paragraph(text).setFont(font).setFontSize(12).setMargin(0).setMultipliedLeading(0.9f))
				.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(5)
				.setVerticalAlignment(VerticalAlignment.MIDDLE);
	}

	private static Cell createValueCell(String text, PdfFont font) {
		return new Cell().add(new Paragraph(text).setFont(font).setFontSize(12).setMargin(0).setMultipliedLeading(0.9f))
				.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(5)
				.setVerticalAlignment(VerticalAlignment.MIDDLE);
	}

	private static void addSignatureForm(Document document, PdfFont boldFont, PdfFont regularFont, String nameLabel,
			String mobileLabel, String dishCounting, String extraDish, String testing, String timeLable, String note,
			String sign, String feedbackForm) throws Exception {

		// Name and Mobile No. header
		Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
		headerTable.setWidth(UnitValue.createPercentValue(100));
		headerTable.setMarginBottom(10f);

		Cell nameCell = new Cell().add(
				new Paragraph(nameLabel + ": _______________________________").setFont(regularFont).setFontSize(12))
				.setBorder(Border.NO_BORDER).setPadding(0);

		Cell mobileCell = new Cell().add(
				new Paragraph(mobileLabel + ": _______________________________").setFont(regularFont).setFontSize(12))
				.setBorder(Border.NO_BORDER).setPadding(0);

		headerTable.addCell(nameCell);
		headerTable.addCell(mobileCell);
		document.add(headerTable);

		// Dish Counting Table
		addSectionTable(document, dishCounting, 1, boldFont, regularFont, timeLable, note, sign);

		// Extra Dish Table
		addSectionTable2(document, extraDish, 3, boldFont, regularFont, timeLable, note, sign);

		// Tasting Table
		addSectionTable(document, testing, 1, boldFont, regularFont, timeLable, note, sign);

		// Feedback Form
		document.add(new Paragraph(feedbackForm + " :").setFont(boldFont).setFontSize(14).setMarginTop(15f)
				.setMarginBottom(5f));

		// Add feedback lines
		for (int i = 0; i < 6; i++) {
			Table lineTable = new Table(1);
			lineTable.setWidth(UnitValue.createPercentValue(100));

			Cell lineCell = new Cell().add(new Paragraph(" ").setFontSize(10)).setBorder(Border.NO_BORDER)
					.setBorderBottom(new SolidBorder(0.5f)).setHeight(17f).setPadding(0);

			lineTable.addCell(lineCell);
			document.add(lineTable);
		}
	}

	private static void addSectionTable(Document document, String sectionName, int dataRows, PdfFont boldFont,
			PdfFont regularFont, String timeLable, String note, String sign) {

		float[] columnWidths = { 25f, 25f, 25f, 25f };
		Table table = new Table(UnitValue.createPercentArray(columnWidths));
		table.setWidth(UnitValue.createPercentValue(100));
		table.setMarginBottom(10f);

		// Header row
		table.addCell(createHeaderCell(sectionName, boldFont));
		table.addCell(createHeaderCell(timeLable, boldFont));
		table.addCell(createHeaderCell(note, boldFont));
		table.addCell(createHeaderCell(sign, boldFont));

		for (int i = 0; i < dataRows; i++) {
			table.addCell(createDataCell2(regularFont));
			table.addCell(createDataCell2(regularFont));
			table.addCell(createDataCell2(regularFont));
			table.addCell(createDataCell2(regularFont));
		}

		document.add(table);
	}

	private static void addSectionTable2(Document document, String sectionName, int dataRows, PdfFont boldFont,
			PdfFont regularFont, String timeLable, String note, String sign) {

		float[] columnWidths = { 25f, 25f, 25f, 25f };
		Table table = new Table(UnitValue.createPercentArray(columnWidths));
		table.setWidth(UnitValue.createPercentValue(100));
		table.setMarginBottom(10f);

		// Header row
		table.addCell(createHeaderCell(sectionName, boldFont));
		table.addCell(createHeaderCell(timeLable, boldFont));
		table.addCell(createHeaderCell(note, boldFont));
		table.addCell(createHeaderCell(sign, boldFont));

		for (int i = 0; i < dataRows; i++) {
			table.addCell(createDataCell(regularFont));
			table.addCell(createDataCell(regularFont));
			table.addCell(createDataCell(regularFont));
			table.addCell(createDataCell(regularFont));
		}

		document.add(table);
	}

	private static Cell createHeaderCell(String text, PdfFont font) {
		return new Cell().add(new Paragraph(text).setFont(font).setFontSize(12).setTextAlignment(TextAlignment.CENTER))
				.setBorder(new SolidBorder(1f)).setTextAlignment(TextAlignment.CENTER);
	}

	private static Cell createDataCell(PdfFont font) {
		return new Cell().add(new Paragraph(" ").setFont(font).setFontSize(10)).setBorder(new SolidBorder(1f))
				.setHeight(9f).setPadding(5);
	}

	private static Cell createDataCell2(PdfFont font) {
		return new Cell().add(new Paragraph(" ").setFont(font).setFontSize(10)).setBorder(new SolidBorder(1f))
				.setHeight(25f).setPadding(5);
	}

	private void addSignatureFooter(Document document, PdfFont regularFont, String sign, String managerSign, int lang) {
		// Add some space before footer
		document.add(new Paragraph("\n"));

		// Create signature table
		Table signatureTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
		signatureTable.setWidth(UnitValue.createPercentValue(100));
		if (lang == 0) {
			signatureTable.setMarginTop(145f);
		} else if (lang == 1) {
			signatureTable.setMarginTop(110f);
		} else {
			signatureTable.setMarginTop(80f);
		}

		Cell signatureCell = new Cell()
				.add(new Paragraph(sign + ": _______________").setFont(regularFont).setFontSize(10))
				.setBorder(Border.NO_BORDER).setPadding(0);

		Cell managerSignatureCell = new Cell().add(new Paragraph(managerSign + ": _______________").setFont(regularFont)
				.setFontSize(10).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER).setPadding(0);

		signatureTable.addCell(signatureCell);
		signatureTable.addCell(managerSignatureCell);

		document.add(signatureTable);
	}

	@Override
	public String orderSummaryReport(String startDate, String endDate, List<Integer> eventStatus,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, List<Long> managerIds, Long partyId,
			Integer isContactNoVisible) {

		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();
			String eventDateLabel, eventNameLabel, functionNameLabel, paxLabel, functionVenueLabel, partyNameLabel,
					managerNameLabel, statusLabel, eventNoLabel, partyMobileLabel;
			eventDateLabel = "Event Date";
			eventNameLabel = "Event Name";
			functionNameLabel = "Function Name";
			paxLabel = "Pax";
			functionVenueLabel = "Function Venue";
			partyNameLabel = "Party Name";
			managerNameLabel = "Manager Name";
			statusLabel = "Status";
			eventNoLabel = "Booking Code";
			partyMobileLabel = "Party Contact No.";

			int headFontSize = 13;
			int fontSize = 10;
			float lineHeight = 1.1f;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				eventDateLabel = "कार्यक्रम तिथि";
				eventNameLabel = "कार्यक्रम का नाम";
				functionNameLabel = "समारोह का नाम";
				paxLabel = "संख्या";
				functionVenueLabel = "समारोह स्थल";
				partyNameLabel = "ग्राहक का नाम";
				managerNameLabel = "प्रबंधक का नाम";
				statusLabel = "स्थिति";
				eventNoLabel = "बुकिंग कोड";
				partyMobileLabel = "ग्राहक का नंबर";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				eventDateLabel = "કાર્યક્રમ તારીખ";
				eventNameLabel = "કાર્યક્રમનું નામ";
				functionNameLabel = "સમારોહનું નામ";
				paxLabel = "સંખ્યા";
				functionVenueLabel = "સમારોહ સ્થળ";
				partyNameLabel = "ગ્રાહકનું નામ";
				managerNameLabel = "મેનેજરનું નામ";
				statusLabel = "સ્થિતિ";
				eventNoLabel = "બુકિંગ કોડ";
				partyMobileLabel = "ગ્રાહકનો નંબર";
				fontSize = 11;
				headFontSize = 14;
				lineHeight = 0.9f;

			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/orderSummaryReport");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			File pdfFile = new File(outputPath + "/" + "FROM " + formatDate(startDate) + " TO " + formatDate(endDate)
					+ "_order_summary_report" + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, PageSize.A4.rotate());
			document.setMargins(40, 35, 50, 20);

			float[] columnWidthHead = { 20f, 18f, 2f, 57f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));

			MenuQuantityReponseDto cmpData = menuItemRawMaterialServiceImpl.getCmpData(userid);

			String cmpName = cmpData != null ? commonService.getString(cmpData.getCompanyName()) : "";
			String cmpCountryCode = cmpData != null ? commonService.getString(cmpData.getCountryCode()) : "";
			String cmpMobile = cmpData != null ? commonService.getString(cmpData.getOfficeNo()) : "";
			String cmpEmail = cmpData != null ? commonService.getString(cmpData.getCompanyEmail()) : "";
			String logo = cmpData != null ? commonService.getString(cmpData.getLogo()) : "";
			String companyAddress = cmpData != null ? commonService.getString(cmpData.getCompanyAddress()) : "";

//			ImageData imgData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
//			Image img = new Image(imgData);
			Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));
			img.setWidth(100);
			img.setHorizontalAlignment(HorizontalAlignment.LEFT);

			Cell cmp = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			Paragraph cmpPara = new Paragraph().add(
					new Text(cmpName).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18))
					.setMultipliedLeading(1f);
			cmp = new Cell(1, 3).add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(new Text("Mobile No.")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpCountryCode + " " + cmpMobile)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text("Email").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpEmail).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER).setMarginBottom(3f);
			cmpHead.addCell(cmp);

			if (isCompanyDetails == 1) {
				cmpHead.setMarginBottom(7f);
				document.add(cmpHead);
			}

			LocalDate firstDate = commonService.dateFormatted(startDate);
			LocalDate lastDate = commonService.dateFormatted(endDate);

			Paragraph para = new Paragraph().add(new Text("DATEWISE ORDER SUMMARY"))
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(16)
					.setTextAlignment(TextAlignment.CENTER);

			document.add(para);

			float[] cw2 = { 50, 50 };
			Table dTable = new Table(UnitValue.createPercentArray(cw2));
			dTable.setWidth(UnitValue.createPercentValue(100));
			dTable.setMarginBottom(7f);

			para = new Paragraph().add(new Text("From Date: " + startDate))
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(13)
					.setTextAlignment(TextAlignment.CENTER);
			Cell cell = new Cell().add(para).setBorder(Border.NO_BORDER);
			dTable.addCell(cell);

			para = new Paragraph().add(new Text("To Date: " + endDate))
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(13)
					.setTextAlignment(TextAlignment.CENTER);
			cell = new Cell().add(para).setBorder(Border.NO_BORDER);
			dTable.addCell(cell);

			document.add(dTable);

			List<OrderSummaryResponseDto> summaryResponseDtos = menuPreparationServiceImpl
					.getDatewiseOrderSummaryReport(firstDate, lastDate, userid, lang, eventStatus, managerIds, partyId,
							"type1");

			// --- Header Table (separate, so it doesn't get pulled with data) ---
			float[] cw = isContactNoVisible == 1 ? new float[] { 8, 8, 9, 18, 5, 12, 11, 11, 11, 7 }
					: new float[] { 8, 7, 8, 23, 5, 15, 12, 12, 9 };

			Table headerTable = new Table(UnitValue.createPercentArray(cw));
			headerTable.setWidth(UnitValue.createPercentValue(100));
			headerTable.setFixedLayout();

			String[] headers = isContactNoVisible == 1
					? new String[] { eventDateLabel, eventNoLabel, eventNameLabel, functionNameLabel, paxLabel,
							functionVenueLabel, partyNameLabel, partyMobileLabel, managerNameLabel, statusLabel }
					: new String[] { eventDateLabel, eventNoLabel, eventNameLabel, functionNameLabel, paxLabel,
							functionVenueLabel, partyNameLabel, managerNameLabel, statusLabel };
			for (String header : headers) {
				para = new Paragraph().add(new Text(header)).setFont(boldFont).setFontSize(headFontSize)
						.setTextAlignment(TextAlignment.CENTER);
				cell = new Cell().add(para);
				headerTable.addCell(cell);
			}
			document.add(headerTable);

			// --- Group DTOs ---
			Map<String, List<OrderSummaryResponseDto>> groupedMap = new LinkedHashMap<>();
			for (OrderSummaryResponseDto dto : summaryResponseDtos) {
				String key = dto.getEventDate() + "||" + dto.getEventName() + "||" + dto.getPartyName() + "||"
						+ dto.getManagerName() + "||" + dto.getStatus();
				groupedMap.computeIfAbsent(key, k -> new ArrayList<>()).add(dto);
			}

			// --- One mini-table per group, kept together on same page ---
			for (Map.Entry<String, List<OrderSummaryResponseDto>> entry : groupedMap.entrySet()) {
				List<OrderSummaryResponseDto> group = entry.getValue();

				String eventDate = group.get(0).getEventDate() != null ? group.get(0).getEventDate() : "";
				String eventName = group.get(0).getEventName() != null ? group.get(0).getEventName() : "";
				String partyName = group.get(0).getPartyName() != null ? group.get(0).getPartyName() : "";
				String managerName = group.get(0).getManagerName() != null ? group.get(0).getManagerName() : "";
				String status = group.get(0).getStatus() != null ? group.get(0).getStatus() : "";
				String eventNo = group.get(0).getEventNo() != null ? group.get(0).getEventNo() : "";
				String partyContactNo = group.get(0).getMobileNo() != null ? group.get(0).getMobileNo() : "";

				// Create a mini-table for this group
				Table groupTable = new Table(UnitValue.createPercentArray(cw));
				groupTable.setWidth(UnitValue.createPercentValue(100));
				groupTable.setKeepTogether(true); // <-- KEY: never split this group across pages
				groupTable.setFixedLayout();

				for (int i = 0; i < group.size(); i++) {
					OrderSummaryResponseDto dto = group.get(i);

					String functionName = dto.getFunctionName() != null ? dto.getFunctionName() : "";
					String functionPax = dto.getFunctionPax() != null ? dto.getFunctionPax() : "";
					String functionVenue = dto.getFunctionVenue() != null ? dto.getFunctionVenue() : "";

					// -- Event Date --

					if (i == 0) {
						para = new Paragraph().add(new Text(eventDate)).setFont(basicFont).setFontSize(fontSize)
								.setPaddingLeft(3f).setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(lineHeight);
						cell = new Cell(group.size(), 1).add(para).setVerticalAlignment(VerticalAlignment.MIDDLE);
						groupTable.addCell(cell);
					}

					if (i == 0) {
						para = new Paragraph().add(new Text(eventNo)).setFont(basicFont).setFontSize(fontSize)
								.setPaddingLeft(3f).setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(lineHeight);
						cell = new Cell(group.size(), 1).add(para).setVerticalAlignment(VerticalAlignment.MIDDLE);
						groupTable.addCell(cell);
					}
					// -- Event Name --
					if (i == 0) {
						para = new Paragraph().add(new Text(eventName)).setFont(basicFont).setFontSize(fontSize)
								.setPaddingLeft(3f).setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(lineHeight);
						cell = new Cell(group.size(), 1).add(para).setVerticalAlignment(VerticalAlignment.MIDDLE);
						groupTable.addCell(cell);
					}

					// -- Function Name --
					para = new Paragraph().add(new Text(functionName)).setFont(basicFont).setFontSize(fontSize)
							.setPaddingLeft(3f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(lineHeight);
					cell = new Cell().add(para);
					groupTable.addCell(cell);

					// -- Pax --
					para = new Paragraph().add(new Text(functionPax)).setFont(basicFont).setFontSize(fontSize)
							.setPaddingLeft(3f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(lineHeight);
					cell = new Cell().add(para);
					groupTable.addCell(cell);

					// -- Function Venue --
					para = new Paragraph().add(new Text(functionVenue)).setFont(basicFont).setFontSize(fontSize)
							.setPaddingLeft(3f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(lineHeight);
					cell = new Cell().add(para);
					groupTable.addCell(cell);

					// -- Party Name --
					if (i == 0) {
						para = new Paragraph().add(new Text(partyName)).setFont(basicFont).setFontSize(fontSize)
								.setPaddingLeft(3f).setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(lineHeight);
						cell = new Cell(group.size(), 1).add(para).setVerticalAlignment(VerticalAlignment.MIDDLE);
						groupTable.addCell(cell);
					}

					// -- Party Contact No --
					if (isContactNoVisible == 1 && i == 0) {
						para = new Paragraph().add(new Text(partyContactNo)).setFont(basicFont).setFontSize(fontSize)
								.setPaddingLeft(3f).setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(lineHeight);
						cell = new Cell(group.size(), 1).add(para).setVerticalAlignment(VerticalAlignment.MIDDLE);
						groupTable.addCell(cell);
					}

					// -- Manager Name --
					if (i == 0) {
						para = new Paragraph().add(new Text(managerName)).setFont(basicFont).setFontSize(fontSize)
								.setPaddingLeft(3f).setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(lineHeight);
						cell = new Cell(group.size(), 1).add(para).setVerticalAlignment(VerticalAlignment.MIDDLE);
						groupTable.addCell(cell);
					}

					// -- Status --
					if (i == 0) {
						para = new Paragraph().add(new Text(status)).setFont(basicFont).setFontSize(fontSize)
								.setPaddingLeft(3f).setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(lineHeight);
						cell = new Cell(group.size(), 1).add(para).setVerticalAlignment(VerticalAlignment.MIDDLE);
						groupTable.addCell(cell);
					}
				}

				document.add(groupTable);
			}

			document.close();

			String scheme = re.getScheme();
			String serverName = re.getServerName();
			int serverPort = re.getServerPort();
			String contextPath = re.getContextPath();

			String fullUrl;
			if ((scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443)) {
				fullUrl = "https://" + serverName + contextPath + "/api/download/pdf/orderSummaryReport" + "/" + "FROM "
						+ formatDate(startDate) + " TO " + formatDate(endDate) + "_order_summary_report" + ".pdf";
			} else {
				fullUrl = "https://" + serverName + ":" + serverPort + contextPath
						+ "/api/download/pdf/orderSummaryReport" + "/" + "FROM " + formatDate(startDate) + " TO "
						+ formatDate(endDate) + "_order_summary_report" + ".pdf";
			}

			fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/orderSummaryReport" + "/" + "FROM "
					+ formatDate(startDate) + " TO " + formatDate(endDate) + "_order_summary_report" + ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report generation failed.");
		}
	}

	@Override
	public String getMenuPlanningSimpleReport8(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, int intValue, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails,
			AdminTemplateModuleResponseDto adminTemplate) {

		try {
			PdfFont basicFont = null;
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "", l1 = "",
					l2 = "", note = "", eContact = "", eventFlow = "", function = "", person = "", eTime = "",
					date = "", rate = "", party = "", eventNotes = "", serviceLabel = "", themeLabel = "",
					altMobileLabel = "", refLabel = "";
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				customerName = "ग्राहक का नाम";
				customerPhone = "मोबाइल नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "दिनांक";
				fNotes = "नोट्स";
				eVenue = "आयोजन स्थान";
				function = "कार्यक्रम";
				person = "मेंबर्स";
				eTime = "समय";
				eContact = "संपर्क नंबर";
				l1 = "व्यक्तियों की संख्या:";
				eventFlow = "कार्यक्रम का संचालन क्रम";
				note = "नोट";
				date = "दिनांक";
				rate = "रेट";
				party = "पार्टी का नाम";
				eventNotes = "कार्यक्रम की नोट";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				altMobileLabel = "दूसरा मोबाइल नंबर";
				refLabel = "रेफरेंस";
				y = 512;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Regular.ttf");

					// Tamil
					customerName = "வாடிக்கையாளர் பெயர்";
					customerPhone = "மொபைல் எண்";
					eName = "நிகழ்ச்சி பெயர்";
					eDate = "நிகழ்ச்சி தேதி";
					fNotes = "உணவு விவரம்";
					eVenue = "நிகழ்வு இடம்";
					function = "நிகழ்ச்சி";
					person = "நபர்";
					eTime = "நேரம்";
					eContact = "தொடர்பு எண்";
					l1 = "நபர்களின் எண்ணிக்கை:";
					eventFlow = "நிகழ்ச்சி நடைபெறும் வரிசை";
					note = "குறிப்பு";
					date = "தேதி";
					rate = "விலை";
					party = "கட்சியின் பெயர்";
					eventNotes = "நிகழ்வு குறிப்புகள்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					altMobileLabel = "மாற்று கைபேசி எண்";
					refLabel = "பரிந்துரை";
				} else if (language.equalsIgnoreCase("Telugu")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Regular.ttf");

					// Telugu
					customerName = "కస్టమర్ పేరు";
					customerPhone = "మొబైల్ నంబర్";
					eName = "కార్యక్రమ పేరు";
					eDate = "కార్యక్రమ తేదీ";
					fNotes = "భోజన వివరాలు";
					eVenue = "కార్యక్రమ స్థలం";
					function = "కార్యక్రమం";
					person = "వ్యక్తి";
					eTime = "సమయం";
					eContact = "సంప్రదింపు నంబర్";
					l1 = "వ్యక్తుల సంఖ్య:";
					eventFlow = "కార్యక్రమ నిర్వహణ క్రమం";
					note = "గమనిక";
					date = "తేదీ";
					rate = "రేటు";
					party = "పార్టీ పేరు";
					eventNotes = "ఈవెంట్ గమనికలు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					altMobileLabel = "ప్రత్యామ్నాయ ఫోన్ నంబర్";
					refLabel = "రిఫరెన్స్";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Regular.ttf");

					// Malayalam
					customerName = "ഉപഭോക്താവിന്റെ പേര്";
					customerPhone = "മൊബൈൽ നമ്പർ";
					eName = "പരിപാടിയുടെ പേര്";
					eDate = "പരിപാടിയുടെ തീയതി";
					fNotes = "ഭക്ഷണ വിശദാംശങ്ങൾ";
					eVenue = "പരിപാടി സ്ഥലം";
					function = "പരിപാടി";
					person = "വ്യക്തി";
					eTime = "സമയം";
					eContact = "ബന്ധപ്പെടാനുള്ള നമ്പർ";
					l1 = "വ്യക്തികളുടെ എണ്ണം:";
					eventFlow = "പരിപാടി നടത്തിപ്പ് ക്രമം";
					note = "കുറിപ്പ്";
					date = "തീയതി";
					rate = "നിരക്ക്";
					party = "പാർട്ടി പേര്";
					eventNotes = "ഇവന്റ് കുറിപ്പുകൾ";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					altMobileLabel = "മറ്റൊരു മൊബൈൽ നമ്പർ";
					refLabel = "റഫറൻസ്";
				} else if (language.equalsIgnoreCase("Marathi")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");

					// Marathi
					customerName = "ग्राहकाचे नाव";
					customerPhone = "मोबाईल नंबर";
					eName = "कार्यक्रमाचे नाव";
					eDate = "कार्यक्रमाची दिनांक";
					fNotes = "भोजन तपशील";
					eVenue = "आयोजन स्थळ";
					function = "कार्यक्रम";
					person = "व्यक्ती";
					eTime = "वेळ";
					eContact = "संपर्क नंबर";
					l1 = "व्यक्तींची संख्या:";
					eventFlow = "कार्यक्रमाचा संचालन क्रम";
					note = "टीप";
					date = "दिनांक";
					rate = "रेट";
					party = "पार्टीचे नाव";
					eventNotes = "कार्यक्रम नोंदी";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					altMobileLabel = "दुसरा मोबाईल नंबर";
					refLabel = "रेफरन्स";
				} else {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");

					customerName = "ગ્રાહકનું નામ";
					customerPhone = "મોબાઇલ નંબર";
					eName = "કાર્યક્રમનું નામ";
					eDate = "કાર્યક્રમની તારીખ";
					fNotes = "ભોજન વિગતો";
					eVenue = "આયોજન સ્થળ";
					function = "કાર્યક્રમ";
					person = "વ્યક્તિ";
					eTime = "સમય";
					eContact = "સંપર્ક નંબર";
					eventFlow = "કાર્યક્રમનું સંચાલન ક્રમ";
					l1 = "વ્યક્તિઓની સંખ્યા:";
					note = "નોંધ";
					date = "તારીખ";
					rate = "રેટ";
					party = "પાર્ટીનું નામ";
					eventNotes = "કાર્યક્રમની નોંધ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					altMobileLabel = "બીજો મોબાઇલ નંબર";
					refLabel = "રેફરન્સ";
				}
				System.out.println("Gujarati font loaded successfully");
				y = 510;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				System.out.println("English font loaded successfully");
				customerName = "Customer Name";
				customerPhone = "Mobile No.";
				eName = "Event Name";
				eDate = "Event Date";
				fNotes = "Food Note";
				eVenue = "Venue";
				function = "Function";
				person = "Persons";
				eTime = "TIMING";
				eContact = "CONTACT NO";
				eventFlow = "FLOW OF EVENT";
				l1 = "OF PERSONS:";
				note = "Note";
				date = "Date";
				rate = "Rate";
				party = "PARTY NAME";
				eventNotes = "Event Notes";
				serviceLabel = "Service";
				themeLabel = "Theme";
				altMobileLabel = "Alt. Mobile";
				refLabel = "Ref.";
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);

			if (eventDto == null) {
				return "";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "back office report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 30, 50, 30);

			// Load all background images upfront
//			ImageData mainBgData = menuPreparationServiceImpl
//					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
//			ImageData watermarkBgData = menuPreparationServiceImpl
//					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			Color redColor = new DeviceRgb(255, 0, 0);
			Color blackColor = new DeviceRgb(0, 0, 0);

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "";
			String altMobileNo = eventDto.getAltMobileNo() != null ? eventDto.getAltMobileNo() : "";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			String venue = "";
			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone() != null ? eventDto.getCmpPhone() : "";
			String cmpAddress = eventDto.getCmpAddress() != null ? eventDto.getCmpAddress() : "";
			String cmpEmail = eventDto.getEmail() != null ? eventDto.getEmail() : "";
			String remarks = eventDto.getRemark() != null ? eventDto.getRemark() : "";
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;
			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}
			String reference = eventDto.getReference() != null ? eventDto.getReference() : "";
			
			boolean isFirstFunction = true;
			Table eventTable = null;

			ImageData tnc1 = null;
			if (adminTemplate.getTemplateMaster().getFrontPage() != null) {
				tnc1 = menuPreparationServiceImpl.loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
//				tnc1 = menuPreparationServiceImpl.loadImageFromResource(adminTemplate.getTemplateMaster().getFrontPage());
				System.out.println("Page1:- " + environment.getProperty("app.image.url")
						+ adminTemplate.getTemplateMaster().getFrontPage());
			}

			ImageData tnc2 = null;
			if (adminTemplate.getTemplateMaster().getSecondFrontPage() != null) {
				tnc2 = menuPreparationServiceImpl.loadImageFromResource(environment.getProperty("app.image.url")
						+ adminTemplate.getTemplateMaster().getSecondFrontPage());
//				tnc2 = menuPreparationServiceImpl.loadImageFromResource(adminTemplate.getTemplateMaster().getSecondFrontPage());
				System.out.println("Page1:- " + environment.getProperty("app.image.url")
						+ adminTemplate.getTemplateMaster().getSecondFrontPage());
			}

			// Create main table with 2 columns for the header layout
			Table catItemTable = null;

			String service = "";
			String theme = "";
			if (lang == 1) {
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
			}

			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				if (!isFirstFunction) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}

				String functionVenue = "";
				String functionNote = "";

				if (lang == 1) {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenueHindi() != null
							? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: "";
					functionNote = eventFunctionMasterResponseDto.getNotesHindi() != null
							? eventFunctionMasterResponseDto.getNotesHindi()
							: "";
				} else if (lang == 2) {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenueGujarati() != null
							? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
							: "";
					functionNote = eventFunctionMasterResponseDto.getNotesGujarati() != null
							? eventFunctionMasterResponseDto.getNotesGujarati()
							: "";
				} else {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenue() != null
							? eventFunctionMasterResponseDto.getFunctionVenue()
							: "";
					functionNote = eventFunctionMasterResponseDto.getNotesEnglish() != null
							? eventFunctionMasterResponseDto.getNotesEnglish()
							: "";
				}

				eventTable = new Table(UnitValue.createPercentArray(new float[] { 32f, 1.5f, 32f, 32f, 1.5f, 32f }));
				eventTable.setWidth(UnitValue.createPercentValue(100));
				eventTable.setMarginBottom(-2f);
				eventTable.setBorder(Border.NO_BORDER);
				eventTable.setMarginTop(10f);

				ImageData logoData = null;
				logoData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + eventDto.getLogo());
//				logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");

				Image logo = new Image(logoData);

				// Resize & align
//				logo.setWidth(UnitValue.createPercentValue(100f));
				logo.scaleToFit(130f, 130f);
				logo.setAutoScale(false);
				logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

				Color headingColor = new DeviceRgb(115, 99, 67);

				cell = new Cell(7, 1).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setBorder(Border.NO_BORDER).setPadding(3f);

				if (isCompanyDetails == 1 && isFirstFunction) {
					eventTable.addCell(cell);
				}

				if (isFirstFunction) {
					ImageData vegSymbol = menuPreparationServiceImpl
							.loadImageFromResource("/flipbook/pages/veg_symbol.png");
					Image veg = new Image(vegSymbol);

					veg.scaleToFit(20f, 20f);
					veg.setAutoScale(false);
					veg.setHorizontalAlignment(HorizontalAlignment.RIGHT);

					cell = new Cell(1, 5).add(veg).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setBorder(Border.NO_BORDER).setPadding(3f);

					if (isCompanyDetails == 1) {
						eventTable.addCell(cell);
					}

					cell = new Cell(1, 5)
							.add(new Paragraph(cmpName.toUpperCase()).setFont(basicFont).setFontSize(20)
									.setFontColor(headingColor).setTextAlignment(TextAlignment.CENTER).simulateBold())
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
					if (isCompanyDetails == 1) {
						eventTable.addCell(cell);
					}

					cell = new Cell(1, 5)
							.add(new Paragraph(cmpAddress).setFont(basicFont).setFontSize(8).setFontColor(headingColor)
									.setTextAlignment(TextAlignment.CENTER))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
					if (isCompanyDetails == 1) {
						eventTable.addCell(cell);
					}

					cell = new Cell(1, 5)
							.add(new Paragraph().add(new Text("Email : ")).add(new Text(cmpEmail)).setFont(basicFont)
									.setFontSize(10).setFontColor(headingColor).setTextAlignment(TextAlignment.CENTER))
							.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
					if (isCompanyDetails == 1) {
						eventTable.addCell(cell);
					}

					cell = new Cell(1, 5)
							.add(new Paragraph().add(new Text("Mobile No : ")).add(new Text(cmpPhone))
									.setFont(basicFont).setFontSize(10).setFontColor(headingColor)
									.setTextAlignment(TextAlignment.CENTER))
							.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
					if (isCompanyDetails == 1) {
						eventTable.addCell(cell);
					}

					eventTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));
					eventTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));

				}

				if (isPartyDetails == 1) {
					cell = new Cell()
							.add(new Paragraph(customerName).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f));
					eventTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT))
							.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f));
					eventTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(hostName).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT))
							.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(new SolidBorder(1f));
					eventTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(customerPhone).setFont(basicFont).setFontSize(14)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(new SolidBorder(1f));
					eventTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT))
							.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(new SolidBorder(1f));
					eventTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(mobileNo).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT))
							.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(new SolidBorder(1f));
					eventTable.addCell(cell);
					
					if(altMobileNo != null && altMobileNo.trim().length() != 0) {
						cell = new Cell()
								.add(new Paragraph(altMobileLabel).setFont(basicFont).setFontSize(14)
										.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
								.setBorder(Border.NO_BORDER).setPadding(0);
						eventTable.addCell(cell);
		
						cell = new Cell()
								.add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
										.setTextAlignment(TextAlignment.LEFT))
								.setBorder(Border.NO_BORDER).setPadding(0);
						eventTable.addCell(cell);
		
						cell = new Cell()
								.add(new Paragraph(altMobileNo).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
										.setTextAlignment(TextAlignment.LEFT))
								.setBorder(Border.NO_BORDER).setPadding(0);
						eventTable.addCell(cell);
					}
				}
				cell = new Cell()
						.add(new Paragraph(eName).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
						.setTextAlignment(TextAlignment.LEFT)).setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(eventName.toUpperCase()).setFont(basicFont).setFontSize(14)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(eDate).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
						.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell().add(new Paragraph(eventDate).setFont(basicFont).setFontSize(14)
						.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT)).setPadding(0)
						.setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				if (foodNotes != null && foodNotes.trim().length() != 0) {
					cell = new Cell()
							.add(new Paragraph(fNotes).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setPadding(0).setBorder(Border.NO_BORDER);
					eventTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT)).setPadding(0).setBorder(Border.NO_BORDER);
					eventTable.addCell(cell);

					cell = new Cell(1, 4)
							.add(new Paragraph(foodNotes).setFont(basicFont).setFontSize(14).setFontColor(redColor)
									.setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setPadding(0).setBorder(Border.NO_BORDER);
					eventTable.addCell(cell);
				}

				cell = new Cell()
						.add(new Paragraph(eVenue).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
						.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell(1, 4)
						.add(new Paragraph(venue.toUpperCase()).setFont(basicFont).setPadding(0).setFontSize(14)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				if (reference != null && reference.trim().length() != 0) {
					cell = new Cell()
							.add(new Paragraph(refLabel).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setPadding(0).setBorder(Border.NO_BORDER);
					eventTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
					eventTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(reference.toUpperCase()).setFont(basicFont).setPadding(0).setFontSize(14)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
							.setBorder(Border.NO_BORDER);
					eventTable.addCell(cell);
				}
				
				if (service != null && service.trim().length() != 0) {
					cell = new Cell()
							.add(new Paragraph(serviceLabel).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setPadding(0).setBorder(Border.NO_BORDER);
					eventTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
					eventTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(service.toUpperCase()).setFont(basicFont).setPadding(0).setFontSize(14)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
							.setBorder(Border.NO_BORDER);
					eventTable.addCell(cell);
				}

				if (theme != null && theme.trim().length() != 0) {
					cell = new Cell()
							.add(new Paragraph(themeLabel).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setPadding(0).setBorder(Border.NO_BORDER);
					eventTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
					eventTable.addCell(cell);

					cell = new Cell(1, 4)
							.add(new Paragraph(theme.toUpperCase()).setFont(basicFont).setPadding(0).setFontSize(14)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
							.setBorder(Border.NO_BORDER);
					eventTable.addCell(cell);
				}

				if (remarks != null && remarks.trim().length() != 0) {
					cell = new Cell()
							.add(new Paragraph(eventNotes).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setPadding(0).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(1f));
					eventTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
									.setPadding(0).setTextAlignment(TextAlignment.LEFT))
							.setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(1f));
					eventTable.addCell(cell);

					cell = new Cell(1, 4)
							.add(new Paragraph(remarks).setFont(basicFont).setPadding(0).setFontSize(14)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
							.setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(1f));
					eventTable.addCell(cell);
				}

				/* Function Details */
				cell = new Cell(1, 6).setBorder(Border.NO_BORDER).setMinHeight(15f);
				eventTable.addCell(cell);

				cell = new Cell(1, 6)
						.add(new Paragraph(safeText(eventFunctionMasterResponseDto.getFunctionName().toUpperCase()))
								.setFont(basicFont).setFontSize(16).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell(1, 6).add(
						new Paragraph(person + " : " + safeText(eventFunctionMasterResponseDto.getPax().toString()))
								.setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER).setPadding(0);
				eventTable.addCell(cell);

				cell = new Cell(1, 6)
						.add(new Paragraph(eVenue + " : " + safeText(functionVenue)).setFont(basicFont).setFontSize(14)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell(1, 6)
						.add(new Paragraph(note + " : " + safeText(functionNote)).setFont(basicFont).setFontSize(14)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
						.setPadding(0).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(1f));
				eventTable.addCell(cell);

				String eventStartTime = eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp().split(" ")[1].substring(0, 5) + " "
								+ eventFunctionMasterResponseDto.getFunctionStartTimestamp().split(" ")[2]
						: "";

				String eventEndTime = eventFunctionMasterResponseDto.getFunctionEndTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionEndTimestamp().split(" ")[1].substring(0, 5) + " "
								+ eventFunctionMasterResponseDto.getFunctionEndTimestamp().split(" ")[2]
						: "";

				cell = new Cell(1, 6)
						.add(new Paragraph(date + " : "
								+ safeText(eventFunctionMasterResponseDto.getFunctionStartTimestamp().split(" ")[0])
								+ " " + safeText(eventStartTime).toUpperCase()).setFont(basicFont).setFontSize(14)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.CENTER))
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell(1, 6)
						.add(new Paragraph(foodNotesName.toString().toUpperCase()).setFont(basicFont).setFontSize(14)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.CENTER))
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				document.add(eventTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				// Add menu content
				for (MenuReportResponseDto menu : menuReportResponseDtos) {
					String subCat = "";
					if (lang == 1) {
						subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
					} else if (lang == 2) {
						subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
					} else {
						subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
					}

					catItemTable = new Table(UnitValue.createPercentArray(new float[] { 100f }));
					catItemTable.setWidth(UnitValue.createPercentValue(100));
					catItemTable.setBorder(Border.NO_BORDER);
					catItemTable.setMarginTop(8f);
					catItemTable.setKeepTogether(true);

					// ── Category Name ─────────────────────────────────────────────────
					Paragraph p = new Paragraph(
							menu.getNameEnglish() != null ? menu.getNameEnglish().toUpperCase() : "").setFont(basicFont)
							.setFontSize(16f).setFontColor(blackColor).setSplitCharacters(new ISplitCharacters() {
								@Override
								public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
									return true;
								}
							}).setMarginLeft(5f).setPadding(0).simulateBold();

					cell = new Cell().add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.LEFT).setUnderline();

					// ── Sub Cat — shown after category name ───────────────────────────
					if (subCat.trim().length() != 0) {
						cell.add(new Paragraph(menuPreparationServiceImpl.formatText(subCat, lang)).setFont(basicFont)
								.setFontSize(12f).setFontColor(blackColor).setMarginLeft(5f).setMarginTop(2f)
								.setPadding(0).setSplitCharacters(new ISplitCharacters() {
									@Override
									public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
										return true;
									}
								}).setTextAlignment(TextAlignment.LEFT));
					}

					// ── Category Instructions ─────────────────────────────────────────
					if (isCategoryInstruction == 1) {
						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
							cell.add(new Paragraph(
									menuPreparationServiceImpl.formatText(" ( " + menu.getMenuNotes() + " )", lang))
									.setSplitCharacters(new ISplitCharacters() {
										@Override
										public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
											return true;
										}
									}).setFont(basicFont).setFontSize(12f).setFontColor(blackColor).setMarginLeft(6)
									.setMarginTop(2f).setTextAlignment(TextAlignment.CENTER));
						}
					}

					catItemTable.addCell(cell);
					cell = new Cell().setBorder(Border.NO_BORDER);

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						String subItem = "";
						String itemHeading = item.getItemHeading() != null ? item.getItemHeading() : "";
						
						if (lang == 1) {
							subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
						} else if (lang == 2) {
							subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
						} else {
							subItem = item.getSubItem() != null ? item.getSubItem() : "";
						}

						if(itemHeading != null && itemHeading.trim().length() != 0) {
							p = new Paragraph(itemHeading)
									.setFont(basicFont).setFontSize(16f).setFontColor(blackColor)
									.setSplitCharacters(new ISplitCharacters() {
										@Override
										public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
											return true;
										}
									}).setMarginLeft(5f).setPadding(0).simulateBold().setMarginTop(10f);
							cell.add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE)
									.setTextAlignment(TextAlignment.LEFT).setUnderline();
						}
						
						// ── Item Name ─────────────────────────────────────────────────
						Paragraph p1 = new Paragraph(
								"• " + (item.getNameEnglish() != null ? toTitleCase(item.getNameEnglish()) : ""))
								.setFont(basicFont).setFontSize(14f).setFontColor(blackColor).setMarginLeft(12)
								.setPadding(0).setTextAlignment(TextAlignment.LEFT);

						cell.add(p1);

						// ── Sub Item — shown after item name ──────────────────────────
						if (subItem.trim().length() != 0) {
							cell.add(new Paragraph(menuPreparationServiceImpl.formatText(subItem, lang))
									.setFont(basicFont).setFontSize(12f).setFontColor(blackColor).setMarginLeft(18f)
									.setMarginTop(2f).setMarginBottom(0f).setPadding(0)
									.setSplitCharacters(new ISplitCharacters() {
										@Override
										public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
											return true;
										}
									}).setTextAlignment(TextAlignment.LEFT));
						}

						// ── Item Instructions ─────────────────────────────────────────
						if (isItemInstruction == 1) {
							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
								cell.add(new Paragraph(menuPreparationServiceImpl
										.formatText(" ( " + item.getItemNotes() + " ) ", lang)).setFont(basicFont)
										.setSplitCharacters(new ISplitCharacters() {
											@Override
											public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
												return true;
											}
										}).setFontSize(12).setFixedLeading(13f).setFontColor(blackColor)
										.setMarginLeft(18).setMarginTop(2f).setMarginBottom(0f).setPadding(0f)
										.setTextAlignment(TextAlignment.LEFT));
							}
						}
					}

					catItemTable.addCell(cell.setKeepTogether(true));
					document.add(catItemTable);
				}
				isFirstFunction = false;
			}

			/* ================= PAGE NUMBERS ================= */
			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 22,
						TextAlignment.CENTER);

				canvas.close();
			}

			int lastPageNum = pdfDocument.getNumberOfPages() + 1;
			Paragraph invisibleContent;

			if (tnc1 != null) {
				bgHandler.setPageBackground(lastPageNum, tnc1);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);
				document.add(invisibleContent);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}

			if (tnc2 != null) {
				bgHandler.setPageBackground(lastPageNum, tnc2);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);
				document.add(invisibleContent);
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"back office report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}

	}

	@Override
	public String getMenuPlanningSimpleReport9(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, int intValue, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails,
			AdminTemplateModuleResponseDto adminTemplate, Integer isHalfPax, Integer isFunctionNextPage) {

		try {
			PdfFont basicFont = null;
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "", l1 = "",
					l2 = "", note = "", eContact = "", eventFlow = "", function = "", person = "", eTime = "",
					date = "", rate = "", party = "", eventNotes = "", serviceLabel = "", themeLabel = "",
					billingNameLabel = "";
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansDevanagari-Regular.ttf");
				System.out.println("Hindi font loaded successfully");
				customerName = "नाम";
				customerPhone = "नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "दिनांक";
				fNotes = "नोट्स";
				eVenue = "स्थान";
				function = "कार्यक्रम";
				person = "मेम्बर्स";
				eTime = "समय";
				eContact = "संपर्क नंबर";
				l1 = "व्यक्तियों की संख्या:";
				eventFlow = "कार्यक्रम का संचालन क्रम";
				note = "नोट";
				date = "दिनांक";
				rate = "रेट";
				party = "पार्टी का नाम";
				eventNotes = "कार्यक्रम की नोट";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				billingNameLabel = "बिलिंग नाम";
				y = 512;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Regular.ttf");

					// Tamil
					customerName = "வாடிக்கையாளர் பெயர்";
					customerPhone = "மொபைல் எண்";
					eName = "நிகழ்ச்சி பெயர்";
					eDate = "நிகழ்ச்சி தேதி";
					fNotes = "உணவு விவரம்";
					eVenue = "நிகழ்வு இடம்";
					function = "நிகழ்ச்சி";
					person = "நபர்";
					eTime = "நேரம்";
					eContact = "தொடர்பு எண்";
					l1 = "நபர்களின் எண்ணிக்கை:";
					eventFlow = "நிகழ்ச்சி நடைபெறும் வரிசை";
					note = "குறிப்பு";
					date = "தேதி";
					rate = "விலை";
					party = "கட்சியின் பெயர்";
					eventNotes = "நிகழ்வு குறிப்புகள்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					billingNameLabel = "பில்லிங் பெயர்";
				} else if (language.equalsIgnoreCase("Telugu")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Regular.ttf");

					// Telugu
					customerName = "కస్టమర్ పేరు";
					customerPhone = "మొబైల్ నంబర్";
					eName = "కార్యక్రమ పేరు";
					eDate = "కార్యక్రమ తేదీ";
					fNotes = "భోజన వివరాలు";
					eVenue = "కార్యక్రమ స్థలం";
					function = "కార్యక్రమం";
					person = "వ్యక్తి";
					eTime = "సమయం";
					eContact = "సంప్రదింపు నంబర్";
					l1 = "వ్యక్తుల సంఖ్య:";
					eventFlow = "కార్యక్రమ నిర్వహణ క్రమం";
					note = "గమనిక";
					date = "తేదీ";
					rate = "రేటు";
					party = "పార్టీ పేరు";
					eventNotes = "ఈవెంట్ గమనికలు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					billingNameLabel = "బిల్లింగ్ పేరు";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Regular.ttf");

					// Malayalam
					customerName = "ഉപഭോക്താവിന്റെ പേര്";
					customerPhone = "മൊബൈൽ നമ്പർ";
					eName = "പരിപാടിയുടെ പേര്";
					eDate = "പരിപാടിയുടെ തീയതി";
					fNotes = "ഭക്ഷണ വിശദാംശങ്ങൾ";
					eVenue = "പരിപാടി സ്ഥലം";
					function = "പരിപാടി";
					person = "വ്യക്തി";
					eTime = "സമയം";
					eContact = "ബന്ധപ്പെടാനുള്ള നമ്പർ";
					l1 = "വ്യക്തികളുടെ എണ്ണം:";
					eventFlow = "പരിപാടി നടത്തിപ്പ് ക്രമം";
					note = "കുറിപ്പ്";
					date = "തീയതി";
					rate = "നിരക്ക്";
					party = "പാർട്ടി പേര്";
					eventNotes = "ഇവന്റ് കുറിപ്പുകൾ";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
				} else if (language.equalsIgnoreCase("Marathi")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");

					// Marathi
					customerName = "ग्राहकाचे नाव";
					customerPhone = "मोबाईल नंबर";
					eName = "कार्यक्रमाचे नाव";
					eDate = "कार्यक्रमाची दिनांक";
					fNotes = "भोजन तपशील";
					eVenue = "आयोजन स्थळ";
					function = "कार्यक्रम";
					person = "व्यक्ती";
					eTime = "वेळ";
					eContact = "संपर्क नंबर";
					l1 = "व्यक्तींची संख्या:";
					eventFlow = "कार्यक्रमाचा संचालन क्रम";
					note = "टीप";
					date = "दिनांक";
					rate = "रेट";
					party = "पार्टीचे नाव";
					eventNotes = "कार्यक्रम नोंदी";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					billingNameLabel = "बिलिंग नाव";
				} else {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");

					customerName = "ગ્રાહકનું નામ";
					customerPhone = "મોબાઇલ નંબર";
					eName = "કાર્યક્રમનું નામ";
					eDate = "કાર્યક્રમની તારીખ";
					fNotes = "ભોજન વિગતો";
					eVenue = "આયોજન સ્થળ";
					function = "કાર્યક્રમ";
					person = "વ્યક્તિ";
					eTime = "સમય";
					eContact = "સંપર્ક નંબર";
					eventFlow = "કાર્યક્રમનું સંચાલન ક્રમ";
					l1 = "વ્યક્તિઓની સંખ્યા:";
					note = "નોંધ";
					date = "તારીખ";
					rate = "રેટ";
					party = "પાર્ટીનું નામ";
					eventNotes = "કાર્યક્રમની નોંધ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					billingNameLabel = "બિલિંગ નામ";
				}
				System.out.println("Gujarati font loaded successfully");
				y = 510;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				System.out.println("English font loaded successfully");
				customerName = "Party Name";
				customerPhone = "Contact No.";
				eName = "Event Name";
				eDate = "Date";
				fNotes = "Food Note";
				eVenue = "Venue";
				function = "Function";
				person = "Persons";
				eTime = "Timing";
				eContact = "CONTACT NO";
				eventFlow = "FLOW OF EVENT";
				l1 = "OF PERSONS:";
				note = "Note";
				date = "Date";
				rate = "Rate";
				party = "PARTY NAME";
				eventNotes = "Event Notes";
				serviceLabel = "Service";
				themeLabel = "Theme";
				billingNameLabel = "Billing Name";
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
			PdfFont basicFont3 = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);

			if (eventDto == null) {
				return "";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "back office report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 30, 50, 30);

			// Load all background images upfront
//			ImageData mainBgData = menuPreparationServiceImpl
//					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
//			ImageData watermarkBgData = menuPreparationServiceImpl
//					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			Color redColor = new DeviceRgb(255, 0, 0);
			Color blackColor = new DeviceRgb(0, 0, 0);

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;
			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}

			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone() != null ? eventDto.getCmpPhone() : "";
			String cmpAddress = eventDto.getCmpAddress() != null ? eventDto.getCmpAddress() : "";
			String cmpEmail = eventDto.getEmail() != null ? eventDto.getEmail() : "";
			String remarks = eventDto.getRemark() != null && !eventDto.getRemark().trim().isEmpty()
					? " (" + eventDto.getRemark() + ")"
					: "";

			boolean isFirstFunction = true;

			// Create main table with 2 columns for the header layout
			Table catItemTable = null;

			String service = "";
			String theme = "";
			String billingName = "";
			if (lang == 1) {
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
			} else if (lang == 2) {
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
			} else {
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
			}

			Table eventTable = new Table(UnitValue.createPercentArray(new float[] { 32f, 1.5f, 32f, 32f, 1.5f, 32f }));
			eventTable.setWidth(UnitValue.createPercentValue(100));
			eventTable.setMarginBottom(-2f);
			eventTable.setBorderBottom(new SolidBorder(1f));
			eventTable.setMarginTop(10f);

			ImageData logoData = null;
			logoData = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + eventDto.getLogo());
//			logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");

			Image logo = new Image(logoData);

			// Resize & align
			logo.setWidth(UnitValue.createPercentValue(100f));
			logo.setAutoScale(false);
			logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

			cell = new Cell(6, 1).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER)
					.setPadding(3f);

			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph(cmpName.toUpperCase()).setFont(basicFont2).setFontSize(12)
							.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph(cmpAddress).setFont(basicFont2).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph().add(new Text("Mobile No : ").simulateBold()).add(new Text(cmpPhone))
							.setFont(basicFont2).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph().add(new Text("Email : ").simulateBold()).add(new Text(cmpEmail))
							.setFont(basicFont2).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			eventTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));
			eventTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));

			if (isPartyDetails == 1) {
				cell = new Cell()
						.add(new Paragraph(customerName).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f));
				eventTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f));
				eventTable.addCell(cell);

				cell = new Cell(1, 4)
						.add(new Paragraph(hostName).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(new SolidBorder(1f));
				eventTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(customerPhone).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell(1, 4)
						.add(new Paragraph(mobileNo).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(billingNameLabel).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
						.setTextAlignment(TextAlignment.LEFT)).setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell(1, 4).add(new Paragraph(billingName).setFont(basicFont).setFontSize(14)
						.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER)
						.setPadding(0);
				eventTable.addCell(cell);
			}

			cell = new Cell()
					.add(new Paragraph(eDate).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setPadding(0).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell(1, 4).add(new Paragraph(eventDate).setFont(basicFont).setFontSize(14)
					.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT)).setPadding(0)
					.setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(eVenue).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setPadding(0).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
					.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER).setPadding(0);
			eventTable.addCell(cell);

			cell = new Cell(1, 4).add(new Paragraph(venue.toUpperCase()).setFont(basicFont).setFontSize(14)
					.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT)).setPadding(0)
					.setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			if (foodNotes.trim().length() != 0) {
				cell = new Cell()
						.add(new Paragraph(fNotes).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
						.setTextAlignment(TextAlignment.LEFT)).setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell(1, 4)
						.add(new Paragraph(foodNotes).setFont(basicFont).setFontSize(14).setFontColor(redColor)
								.setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);
			}

			cell = new Cell()
					.add(new Paragraph(eventNotes).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setPadding(0).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell(1, 4).add(new Paragraph(foodNotesName + remarks).setFont(basicFont).setPadding(0)
					.setFontSize(14).setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			if (service.trim().length() != 0) {
				cell = new Cell()
						.add(new Paragraph(serviceLabel).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
						.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(service.toUpperCase()).setFont(basicFont).setPadding(0).setFontSize(14)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);
			}

			if (theme.trim().length() != 0) {
				cell = new Cell()
						.add(new Paragraph(themeLabel).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
						.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell(1, 4)
						.add(new Paragraph(theme.toUpperCase()).setFont(basicFont).setPadding(0).setFontSize(14)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);
			}

			document.add(eventTable);

			Table fnTable = null;

			for (EventFunctionReportResponseDto dto : eventDto.getFunctions()) {

				if (!isFirstFunction && isFunctionNextPage == 1) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}

				fnTable = new Table(UnitValue.createPercentArray(new float[] { 70f, 30f }))
						.setWidth(UnitValue.createPercentValue(100)).setBorder(Border.NO_BORDER).setMargin(0)
						.setPadding(0).setKeepTogether(true);

				String functionVenue = "";
				String functionNote = "";
				if (lang == 1) {
					functionVenue = dto.getFunctionVenueHindi() != null ? dto.getFunctionVenueHindi() : "";
					functionNote = dto.getNotesHindi() != null ? dto.getNotesHindi() : "";
				} else if (lang == 2) {
					functionVenue = dto.getFunctionVenueGujarati() != null ? dto.getFunctionVenueGujarati() : "";
					functionNote = dto.getNotesGujarati() != null ? dto.getNotesGujarati() : "";
				} else {
					functionVenue = dto.getFunctionVenue() != null ? dto.getFunctionVenue() : "";
					functionNote = dto.getNotesEnglish() != null ? dto.getNotesEnglish() : "";
				}

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, dto.getFunctionId());
				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				}

				/* ================= FUNCTION NAME + PAX (ROW 1) ================= */

				Cell leftCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0).setMargin(0);

				Paragraph fnName = new Paragraph(safeText(dto.getFunctionName().toUpperCase())).setFont(basicFont)
						.setFontSize(16).simulateBold().setMarginTop(10).setPadding(0);

				leftCell.add(fnName);

				Cell rightCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0).setMargin(10)
						.setTextAlignment(TextAlignment.RIGHT);

				Integer fnPax = dto.getPax() != null ? (isHalfPax == 1 ? dto.getPax() / 2 : dto.getPax()) : 0;

				Paragraph paxPara = new Paragraph(person + " : " + fnPax).setFont(basicFont).setFontSize(14)
						.simulateBold().setMarginTop(10).setPadding(0);

				rightCell.add(paxPara);

				fnTable.addCell(leftCell);
				fnTable.addCell(rightCell);

				/* ================= ROW 2: DATE + TIME ================= */

				String startDate = dto.getFunctionStartTimestamp() != null
						? dto.getFunctionStartTimestamp().split(" ")[0]
						: "";

				String eventStartTime = dto.getFunctionStartTimestamp() != null
						? dto.getFunctionStartTimestamp().split(" ")[1].substring(0, 5) + " "
								+ dto.getFunctionStartTimestamp().split(" ")[2]
						: "";

				String eventEndTime = dto.getFunctionEndTimestamp() != null
						? dto.getFunctionEndTimestamp().split(" ")[1].substring(0, 5) + " "
								+ dto.getFunctionEndTimestamp().split(" ")[2]
						: "";

				Cell dateCell = new Cell(1, 2).setBorder(Border.NO_BORDER).setPadding(0).setMargin(0);

				Paragraph datePara = new Paragraph()
						.add(new Text(date + " / " + eTime + " : ").setFont(basicFont).setFontSize(14))
						.add(new Text(startDate.toUpperCase() + "  " + eventStartTime.toUpperCase()).setFont(basicFont3)
								.setFontSize(14))
						.setMargin(3).setPadding(0).setFixedLeading(14);

				dateCell.add(datePara);

				fnTable.addCell(dateCell);

				/* ================= ROW 3: TIME RANGE ================= */

				Cell timeCell = new Cell(1, 2).setBorder(Border.NO_BORDER).setPadding(0).setMargin(0);

				Paragraph timePara = new Paragraph(
						"(" + eventStartTime.toUpperCase() + " To " + eventEndTime.toUpperCase() + ")")
						.setFont(basicFont3).setFontSize(13).setMargin(3).setPadding(0).setFixedLeading(13);

				timeCell.add(timePara);

				fnTable.addCell(timeCell);

				/* ================= ROW 4: NOTE ================= */

				if (functionNote != null && !functionNote.trim().isEmpty()) {

					Cell noteCell = new Cell(1, 2).setBorder(Border.NO_BORDER).setPadding(0).setMargin(0);

					Paragraph notePara = new Paragraph("(" + functionNote + ")").setFont(basicFont).setFontSize(13)
							.setMargin(3).setPadding(0).setFixedLeading(13);

					noteCell.add(notePara);

					fnTable.addCell(noteCell);
				}

				/* ================= ADD FUNCTION BLOCK ================= */

				document.add(fnTable);

				isFirstFunction = false;

				/* ================= MENU PART (UNCHANGED) ================= */
				List<MenuReportResponseDto> menuReportResponseDtos = dto.getMenuCategories();

				PdfFont symbolFont = menuPreparationServiceImpl.loadFont("/fonts/DejaVuSans.ttf");

				for (MenuReportResponseDto menu : menuReportResponseDtos) {

					catItemTable = new Table(UnitValue.createPercentArray(new float[] { 100f }));
					catItemTable.setWidth(UnitValue.createPercentValue(100));
					catItemTable.setBorder(Border.NO_BORDER);
					catItemTable.setMarginTop(8f);
					catItemTable.setKeepTogether(true);

					Paragraph p = new Paragraph().add(new Text("\u274F ").setFont(symbolFont))
							.add(new Text(menu.getNameEnglish() != null ? menu.getNameEnglish().toUpperCase() : "")
									.setFont(basicFont))
							.setFontSize(16f).simulateBold();

					cell = new Cell().add(p).setBorder(Border.NO_BORDER).setPaddingLeft(10f);

					catItemTable.addCell(cell);

					Cell itemCell = new Cell().setBorder(Border.NO_BORDER);

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

						Paragraph p1 = new Paragraph().add(new Text("\u25A1 ").setFont(symbolFont))
								.add(new Text(item.getNameEnglish() != null ? item.getNameEnglish().toUpperCase() : "")
										.setFont(basicFont))
								.setFontSize(14f).setMarginLeft(30f);

						itemCell.add(p1);
					}

					catItemTable.addCell(itemCell);
					document.add(catItemTable);
				}
			}

			/* ================= PAGE NUMBERS ================= */
			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 22,
						TextAlignment.CENTER);

				canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"back office report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}

	}

	public String getMenuPlanningSimpleReport9Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, int intValue, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails,
			AdminTemplateModuleResponseDto adminTemplate, Integer isHalfPax, Integer isFunctionNextPage) {

		try {
			// -----------------------------------------------------------
			// 1. LANGUAGE LABELS (same branching as PDF method)
			// -----------------------------------------------------------
			String customerName = "", customerPhone = "", eDate = "", fNotes = "", eVenue = "", note = "",
					eventFlow = "", person = "", eTime = "", date = "", party = "", eventNotes = "", serviceLabel = "",
					themeLabel = "", billingNameLabel = "";

			String fontFamily;

			if (lang == 1) {
				// Hindi
				fontFamily = "Nirmala UI";
				customerName = "नाम";
				customerPhone = "नंबर";
				eDate = "दिनांक";
				fNotes = "नोट्स";
				eVenue = "स्थान";
				person = "मेम्बर्स";
				eTime = "समय";
				eventFlow = "कार्यक्रम का संचालन क्रम";
				note = "नोट";
				date = "दिनांक";
				party = "पार्टी का नाम";
				eventNotes = "कार्यक्रम की नोट";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				billingNameLabel = "बिलिंग नाम";
			} else if (lang == 2) {
				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {
					fontFamily = "Nirmala UI";
					customerName = "வாடிக்கையாளர் பெயர்";
					customerPhone = "மொபைல் எண்";
					eDate = "நிகழ்ச்சி தேதி";
					fNotes = "உணவு விவரம்";
					eVenue = "நிகழ்வு இடம்";
					person = "நபர்";
					eTime = "நேரம்";
					eventFlow = "நிகழ்ச்சி நடைபெறும் வரிசை";
					note = "குறிப்பு";
					date = "தேதி";
					party = "கட்சியின் பெயர்";
					eventNotes = "நிகழ்வு குறிப்புகள்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					billingNameLabel = "பில்லிங் பெயர்";
				} else if (language.equalsIgnoreCase("Telugu")) {
					fontFamily = "Nirmala UI";
					customerName = "కస్టమర్ పేరు";
					customerPhone = "మొబైల్ నంబర్";
					eDate = "కార్యక్రమ తేదీ";
					fNotes = "భోజన వివరాలు";
					eVenue = "కార్యక్రమ స్థలం";
					person = "వ్యక్తి";
					eTime = "సమయం";
					eventFlow = "కార్యక్రమ నిర్వహణ క్రమం";
					note = "గమనిక";
					date = "తేదీ";
					party = "పార్టీ పేరు";
					eventNotes = "ఈవెంట్ గమనికలు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					billingNameLabel = "బిల్లింగ్ పేరు";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					fontFamily = "Nirmala UI";
					customerName = "ഉപഭോക്താവിന്റെ പേര്";
					customerPhone = "മൊബൈൽ നമ്പർ";
					eDate = "പരിപാടിയുടെ തീയതി";
					fNotes = "ഭക്ഷണ വിശദാംശങ്ങൾ";
					eVenue = "പരിപാടി സ്ഥലം";
					person = "വ്യക്തി";
					eTime = "സമയം";
					eventFlow = "പരിപാടി നടത്തിപ്പ് ക്രമം";
					note = "കുറിപ്പ്";
					date = "തീയതി";
					party = "പാർട്ടി പേര്";
					eventNotes = "ഇവന്റ് കുറിപ്പുകൾ";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
				} else if (language.equalsIgnoreCase("Marathi")) {
					fontFamily = "Nirmala UI";
					customerName = "ग्राहकाचे नाव";
					customerPhone = "मोबाईल नंबर";
					eDate = "कार्यक्रमाची दिनांक";
					fNotes = "भोजन तपशील";
					eVenue = "आयोजन स्थळ";
					person = "व्यक्ती";
					eTime = "वेळ";
					eventFlow = "कार्यक्रमाचा संचालन क्रम";
					note = "टीप";
					date = "दिनांक";
					party = "पार्टीचे नाव";
					eventNotes = "कार्यक्रम नोंदी";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					billingNameLabel = "बिलिंग नाव";
				} else {
					fontFamily = "Nirmala UI";
					customerName = "ગ્રાહકનું નામ";
					customerPhone = "મોબાઇલ નંબર";
					eDate = "કાર્યક્રમની તારીખ";
					fNotes = "ભોજન વિગતો";
					eVenue = "આયોજન સ્થળ";
					person = "વ્યક્તિ";
					eTime = "સમય";
					eventFlow = "કાર્યક્રમનું સંચાલન ક્રમ";
					note = "નોંધ";
					date = "તારીખ";
					party = "પાર્ટીનું નામ";
					eventNotes = "કાર્યક્રમની નોંધ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					billingNameLabel = "બિલિંગ નામ";
				}
			} else {
				// English
				fontFamily = "Arial";
				customerName = "Party Name";
				customerPhone = "Contact No.";
				eDate = "Date";
				fNotes = "Food Note";
				eVenue = "Venue";
				person = "Persons";
				eTime = "Timing";
				eventFlow = "FLOW OF EVENT";
				note = "Note";
				date = "Date";
				party = "PARTY NAME";
				eventNotes = "Event Notes";
				serviceLabel = "Service";
				themeLabel = "Theme";
				billingNameLabel = "Billing Name";
			}

			// -----------------------------------------------------------
			// FETCH DATA (identical call to your PDF method)
			// -----------------------------------------------------------
			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);

			if (eventDto == null) {
				return "";
			}

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() == null ? "" : eventDto.getEventStartTimestamp();
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;
			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}
			String foodType = eventDto.getFoodType() == null ? "" : eventDto.getFoodType();
			String cmpName = eventDto.getCmpName() == null ? "" : eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone() != null ? eventDto.getCmpPhone() : "";
			String cmpAddress = eventDto.getCmpAddress() != null ? eventDto.getCmpAddress() : "";
			String cmpEmail = eventDto.getEmail() != null ? eventDto.getEmail() : "";
			String remarks = eventDto.getRemark() != null && !eventDto.getRemark().trim().isEmpty()
					? " (" + eventDto.getRemark() + ")"
					: "";

			String foodNotes, service, theme, billingName;
			if (lang == 1) {
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
			} else if (lang == 2) {
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
			} else {
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
			}

			// -----------------------------------------------------------
			// OUTPUT FILE PATH (same convention as PDF method)
			// -----------------------------------------------------------
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");
			if (!outputPath.exists()) {
				outputPath.mkdirs();
			}
			String reportName = getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "back office report");
			File docxFile = new File(outputPath, reportName + ".docx");

			// -----------------------------------------------------------
			// BUILD THE DOCUMENT
			// -----------------------------------------------------------

			XWPFDocument document = new XWPFDocument();

			CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
			CTPageMar pageMar = sectPr.addNewPgMar();
			pageMar.setTop(BigInteger.valueOf(400));
			pageMar.setBottom(BigInteger.valueOf(700));
			pageMar.setLeft(BigInteger.valueOf(500));
			pageMar.setRight(BigInteger.valueOf(500));

			// ---- 2. COMPANY HEADER BLOCK ----
			if (isCompanyDetails != null && isCompanyDetails == 1) {
				XWPFTable headerTable = document.createTable(1, 2);
				headerTable.setWidth("100%");
				CTTblPr tblPr = headerTable.getCTTbl().getTblPr();

				if (tblPr == null) {
					tblPr = headerTable.getCTTbl().addNewTblPr();
				}

				CTTblLayoutType layout = tblPr.getTblLayout();

				if (layout == null) {
					layout = tblPr.addNewTblLayout();
				}

				layout.setType(STTblLayoutType.FIXED);
				removeTableBorders(headerTable);

				XWPFTableCell logoCell = headerTable.getRow(0).getCell(0);
				logoCell.removeParagraph(0);
				XWPFParagraph logoPara = logoCell.addParagraph();
				logoPara.setAlignment(ParagraphAlignment.CENTER);
				try {
					String logoPath = environment.getProperty("app.image.url") + eventDto.getLogo();

					com.itextpdf.io.image.ImageData logoImageData = menuPreparationServiceImpl
							.loadImageFromResource(logoPath);
					byte[] logoBytes = (logoImageData != null) ? logoImageData.getData() : null;
					if (logoBytes == null) {
						logoBytes = readBytesFromResourcePath(logoPath);
					}
					if (logoBytes != null && logoBytes.length > 0) {
						int pictureType = guessPoiPictureType(logoPath);
						XWPFRun logoRun = logoPara.createRun();
						logoRun.addPicture(new ByteArrayInputStream(logoBytes), pictureType,
								"logo." + guessExtension(logoPath), Units.toEMU(80), Units.toEMU(80));
					}
				} catch (Exception imgEx) {
					System.out.println("Logo could not be loaded for docx: " + imgEx.getMessage());
				}

				XWPFTableCell infoCell = headerTable.getRow(0).getCell(1);
				if (!logoCell.getParagraphs().isEmpty()) {
					logoCell.removeParagraph(0);
				}

				addStyledParagraph(infoCell.addParagraph(), cmpName.toUpperCase(), "Nirmala UI", 12, false,
						ParagraphAlignment.LEFT);
				addStyledParagraph(infoCell.addParagraph(), cmpAddress, "Nirmala UI", 10, false,
						ParagraphAlignment.LEFT);

				XWPFParagraph phonePara = infoCell.addParagraph();
				addLabelValueRun(phonePara, "Mobile No : ", cmpPhone, "Nirmala UI", 10);

				XWPFParagraph emailPara = infoCell.addParagraph();
				addLabelValueRun(emailPara, "Email : ", cmpEmail, "Nirmala UI", 10);
			}

			addHorizontalLine(document);
			// ---- 3 & 4. PARTY DETAILS + EVENT DETAILS BLOCK ----
			XWPFTable detailsTable = document.createTable(1, 2);
			detailsTable.setWidth("100%");
			removeTableBorders(detailsTable);
			detailsTable.removeRow(0);
			XWPFTableRow detailsRow = detailsTable.getRow(0);

			if (detailsRow != null) {

				if (detailsRow.getCell(0) != null && !detailsRow.getCell(0).getParagraphs().isEmpty()) {
					detailsRow.getCell(0).removeParagraph(0);
				}

				if (detailsRow.getCell(1) != null && !detailsRow.getCell(1).getParagraphs().isEmpty()) {
					detailsRow.getCell(1).removeParagraph(0);
				}
			}

			if (isPartyDetails != null && isPartyDetails == 1) {
				addLabelValueRow(detailsTable, customerName, hostName, fontFamily);
				addLabelValueRow(detailsTable, customerPhone, mobileNo, fontFamily);
				addLabelValueRow(detailsTable, billingNameLabel, billingName, fontFamily);
			}

			addLabelValueRow(detailsTable, eDate, eventDate, fontFamily);
			addLabelValueRow(detailsTable, eVenue, venue.toUpperCase(), fontFamily);

			if (foodNotes.trim().length() != 0) {
				addLabelValueRow(detailsTable, fNotes, foodNotes, fontFamily, true /* red */);
			}

			addLabelValueRow(detailsTable, eventNotes, foodType + remarks, fontFamily);

			if (service.trim().length() != 0) {
				addLabelValueRow(detailsTable, serviceLabel, service.toUpperCase(), fontFamily);
			}
			if (theme.trim().length() != 0) {
				addLabelValueRow(detailsTable, themeLabel, theme.toUpperCase(), fontFamily);
			}
			addHorizontalLine(document);
			// ---- 5, 6, 7. PER-FUNCTION LOOP: function header, menu categories, items ----
			boolean isFirstFunction = true;
			List<EventFunctionReportResponseDto> functions = eventDto.getFunctions();

			for (EventFunctionReportResponseDto fn : functions) {

				if (!isFirstFunction && isFunctionNextPage != null && isFunctionNextPage == 1) {

					XWPFParagraph pageBreak = document.createParagraph();
					pageBreak.setPageBreak(true);
				}

				String functionNote;

				if (lang == 1) {
					functionNote = fn.getNotesHindi() != null ? fn.getNotesHindi() : "";
				} else if (lang == 2) {
					functionNote = fn.getNotesGujarati() != null ? fn.getNotesGujarati() : "";
				} else {
					functionNote = fn.getNotesEnglish() != null ? fn.getNotesEnglish() : "";
				}

				Integer fnPax = fn.getPax() != null
						? (isHalfPax != null && isHalfPax == 1 ? fn.getPax() / 2 : fn.getPax())
						: 0;

				String fnDateOnly = "";
				String startTime = "";
				String endTime = "";
				String functionName = safeText(fn.getFunctionName()).toUpperCase();
				if (fn.getFunctionStartTimestamp() != null) {

					String[] parts = fn.getFunctionStartTimestamp().split(" ");

					fnDateOnly = parts.length > 0 ? parts[0] : "";

					if (parts.length >= 3) {
						startTime = parts[1] + " " + parts[2];
					}
				}

				if (fn.getFunctionEndTimestamp() != null) {

					String[] parts = fn.getFunctionEndTimestamp().split(" ");

					if (parts.length >= 3) {
						endTime = parts[1] + " " + parts[2];
					}
				}

				XWPFParagraph functionSpace = document.createParagraph();
				functionSpace.setSpacingAfter(30);
				/*
				 * DINNER MEMBERS : 40
				 */
				XWPFTable headerTable = document.createTable(1, 2);
				headerTable.setWidth("100%");
				removeTableBorders(headerTable);

				XWPFTableRow row = headerTable.getRow(0);

				// Left
				XWPFParagraph p1 = row.getCell(0).getParagraphs().get(0);
				p1.setAlignment(ParagraphAlignment.LEFT);

				XWPFRun r1 = p1.createRun();
				setRunStyle(r1, fontFamily, 14, true);
				r1.setText(functionName);
				r1.setUnderline(UnderlinePatterns.SINGLE);

				// Right
				XWPFParagraph p2 = row.getCell(1).getParagraphs().get(0);
				p2.setAlignment(ParagraphAlignment.RIGHT);

				XWPFRun r2 = p2.createRun();
				setRunStyle(r2, fontFamily, 14, true);
				r2.setText(person + " : " + fnPax);
				removeTableGap(headerTable);
				/*
				 * DATE TIME
				 */
				XWPFTable dateTimeTable = document.createTable(1, 2);
				dateTimeTable.setWidth("100%");
				removeTableBorders(dateTimeTable);

				XWPFTableRow dtRow = dateTimeTable.getRow(0);

				// DATE
				XWPFTableCell dateCell = dtRow.getCell(0);
				dateCell.removeParagraph(0);

				XWPFParagraph datePara = document.createParagraph();
				datePara.setAlignment(ParagraphAlignment.LEFT);

				XWPFRun dateRun = datePara.createRun();
				setRunStyle(dateRun, fontFamily, 13, true);

				dateRun.setText(date + " / " + eTime + " : " + fnDateOnly + "  " + startTime);

				XWPFParagraph durationPara = document.createParagraph();
				durationPara.setAlignment(ParagraphAlignment.LEFT);

				XWPFRun durationRun = durationPara.createRun();
				setRunStyle(durationRun, fontFamily, 13, true);

				durationRun.setText("(" + startTime + " To " + endTime + ")");
				removeTableGap(dateTimeTable);

				/*
				 * FUNCTION NOTE
				 */
				if (!functionNote.trim().isEmpty()) {

					XWPFParagraph notePara = document.createParagraph();
					notePara.setAlignment(ParagraphAlignment.LEFT);

					XWPFRun noteRun = notePara.createRun();
					setRunStyle(noteRun, fontFamily, 13, true);

					noteRun.setText(functionNote.toUpperCase());
				}

				/*
				 * MENU CATEGORIES
				 */
				List<MenuReportResponseDto> categories = fn.getMenuCategories();

				for (MenuReportResponseDto menu : categories) {

					XWPFParagraph catPara = document.createParagraph();

					catPara.setSpacingBefore(160);

					XWPFRun catBullet = catPara.createRun();

					setRunStyle(catBullet, "DejaVu Sans", 16, false);

					catBullet.setText("\u274F ");

					XWPFRun catText = catPara.createRun();

					setRunStyle(catText, fontFamily, 16, true);

					catText.setText(menu.getNameEnglish() != null ? menu.getNameEnglish().toUpperCase() : "");
					catText.setUnderline(UnderlinePatterns.SINGLE);
					if (isCategoryInstruction != null && isCategoryInstruction == 1 && menu.getMenuNotes() != null
							&& !menu.getMenuNotes().isEmpty()) {

						XWPFRun catNoteRun = catPara.createRun();

						setRunStyle(catNoteRun, fontFamily, 12, false);

						catNoteRun.setText(
								" ( " + menuPreparationServiceImpl.formatText(menu.getMenuNotes(), lang) + " )");
					}

					/*
					 * MENU ITEMS
					 */
					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

						XWPFParagraph itemPara = document.createParagraph();

						itemPara.setIndentationLeft(400);

						XWPFRun itemBullet = itemPara.createRun();

						setRunStyle(itemBullet, "DejaVu Sans", 14, false);

						itemBullet.setText("\u25A1 ");

						XWPFRun itemText = itemPara.createRun();

						setRunStyle(itemText, fontFamily, 14, false);

						itemText.setText(item.getNameEnglish() != null ? item.getNameEnglish().toUpperCase() : "");

						if (isItemInstruction != null && isItemInstruction == 1 && item.getItemNotes() != null
								&& !item.getItemNotes().isEmpty()) {

							XWPFRun itemNoteRun = itemPara.createRun();

							setRunStyle(itemNoteRun, fontFamily, 12, false);

							itemNoteRun.setText(
									" ( " + menuPreparationServiceImpl.formatText(item.getItemNotes(), lang) + " ) ");
						}
					}
				}

				isFirstFunction = false;
			}

			// ---- 8. PAGE NUMBERS (footer, auto-updating PAGE/NUMPAGES fields) ----
			addPageNumberFooter(document, fontFamily);

			// -----------------------------------------------------------
			// WRITE FILE
			// -----------------------------------------------------------
			try (FileOutputStream out = new FileOutputStream(docxFile)) {
				document.write(out);
			}
			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/docx/" + eventDto.getEventNo()
					+ "/" + reportName + ".docx";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report (DOCX)", e);
		}
	}

	private void addHorizontalLine(XWPFDocument document) {

		XWPFParagraph p = document.createParagraph();

		CTPPr pPr = p.getCTP().isSetPPr() ? p.getCTP().getPPr() : p.getCTP().addNewPPr();

		CTPBdr borders = pPr.isSetPBdr() ? pPr.getPBdr() : pPr.addNewPBdr();

		CTBorder bottom = borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom();

		bottom.setVal(STBorder.SINGLE);
		bottom.setSz(BigInteger.valueOf(8));
		bottom.setColor("000000");
	}

	// ================================================================
	// HELPER METHODS
	// ================================================================

	private void removeTableGap(XWPFTable table) {
		for (XWPFTableRow row : table.getRows()) {
			for (XWPFTableCell cell : row.getTableCells()) {
				for (XWPFParagraph p : cell.getParagraphs()) {
					p.setSpacingBefore(0);
					p.setSpacingAfter(0);
				}
			}
		}
	}

	/**
	 * Reads raw bytes directly from a resource path. Used as a fallback when
	 * ImageData.getData() returns null (happens for some image types where iText
	 * defers byte population).
	 */
	private byte[] readBytesFromResourcePath(String path) {
		try {
			File f = new File(path);
			if (f.exists()) {
				return java.nio.file.Files.readAllBytes(f.toPath());
			}
			try (InputStream is = getClass().getResourceAsStream(path)) {
				if (is != null) {
					return IOUtils.toByteArray(is);
				}
			}
		} catch (Exception e) {
			System.out.println("readBytesFromResourcePath failed for " + path + ": " + e.getMessage());
		}
		return null;
	}

	/** Maps a file extension to the POI XWPFDocument.PICTURE_TYPE_* constant. */
	private int guessPoiPictureType(String path) {
		String ext = guessExtension(path).toLowerCase();
		switch (ext) {
		case "jpg":
		case "jpeg":
			return XWPFDocument.PICTURE_TYPE_JPEG;
		case "gif":
			return XWPFDocument.PICTURE_TYPE_GIF;
		case "bmp":
			return XWPFDocument.PICTURE_TYPE_BMP;
		case "png":
		default:
			return XWPFDocument.PICTURE_TYPE_PNG;
		}
	}

	/** Extracts the file extension from a path, defaulting to "png". */
	private String guessExtension(String path) {
		if (path == null)
			return "png";
		int dot = path.lastIndexOf('.');
		if (dot == -1 || dot == path.length() - 1)
			return "png";
		String ext = path.substring(dot + 1);
		int q = ext.indexOf('?');
		return q == -1 ? ext : ext.substring(0, q);
	}

	/**
	 * Removes visible borders from a table (mirrors Border.NO_BORDER in the PDF
	 * method).
	 */
	private void removeTableBorders(XWPFTable table) {

		CTTblPr tblPr = table.getCTTbl().getTblPr();

		if (tblPr == null) {
			tblPr = table.getCTTbl().addNewTblPr();
		}

		CTTblBorders borders = tblPr.getTblBorders();

		if (borders == null) {
			borders = tblPr.addNewTblBorders();
		}

		borders.setTop(CTBorder.Factory.newInstance());
		borders.getTop().setVal(STBorder.NONE);

		borders.setBottom(CTBorder.Factory.newInstance());
		borders.getBottom().setVal(STBorder.NONE);

		borders.setLeft(CTBorder.Factory.newInstance());
		borders.getLeft().setVal(STBorder.NONE);

		borders.setRight(CTBorder.Factory.newInstance());
		borders.getRight().setVal(STBorder.NONE);

		borders.setInsideH(CTBorder.Factory.newInstance());
		borders.getInsideH().setVal(STBorder.NONE);

		borders.setInsideV(CTBorder.Factory.newInstance());
		borders.getInsideV().setVal(STBorder.NONE);
	}

	/**
	 * Adds a label/value row, e.g. "Date : 15/06/2026", as one table row with 2
	 * cells.
	 */
	private void addLabelValueRow(XWPFTable table, String label, String value, String fontFamily) {
		addLabelValueRow(table, label, value, fontFamily, false);
	}

	private void addLabelValueRow(XWPFTable table, String label, String value, String fontFamily, boolean redValue) {

		XWPFTableRow row = table.createRow();

		while (row.getTableCells().size() < 2) {
			row.createCell();
		}

		XWPFTableCell labelCell = row.getCell(0);

		if (!labelCell.getParagraphs().isEmpty()) {
			labelCell.removeParagraph(0);
		}

		addStyledParagraph(labelCell.addParagraph(), label, fontFamily, 14, false, ParagraphAlignment.LEFT);

		XWPFTableCell valueCell = row.getCell(1);

		if (!valueCell.getParagraphs().isEmpty()) {
			valueCell.removeParagraph(0);
		}

		XWPFParagraph valuePara = valueCell.addParagraph();

		XWPFRun colonRun = valuePara.createRun();
		setRunStyle(colonRun, fontFamily, 14, false);
		colonRun.setText(": ");

		XWPFRun valueRun = valuePara.createRun();
		setRunStyle(valueRun, fontFamily, 14, false);

		if (redValue) {
			valueRun.setColor("FF0000");
		}

		valueRun.setText(value == null ? "" : value);
	}

	private void addStyledParagraph(XWPFParagraph para, String text, String fontFamily, int sizePt, boolean bold,
			ParagraphAlignment align) {
		para.setAlignment(align);
		XWPFRun run = para.createRun();
		setRunStyle(run, fontFamily, sizePt, bold);
		run.setText(text);
	}

	private void addLabelValueRun(XWPFParagraph para, String label, String value, String fontFamily, int sizePt) {
		XWPFRun labelRun = para.createRun();
		setRunStyle(labelRun, fontFamily, sizePt, false);
		labelRun.setText(label);
		XWPFRun valueRun = para.createRun();
		setRunStyle(valueRun, fontFamily, sizePt, false);
		valueRun.setText(value);
	}

	private void setRunStyle(XWPFRun run, String fontFamily, int sizePt, boolean bold) {

		run.setFontSize(sizePt);
		run.setBold(bold);

		try {
			run.setFontFamily(fontFamily);

			try {
				run.setFontFamily(fontFamily, XWPFRun.FontCharRange.cs);
			} catch (Exception ignored) {
			}

			try {
				run.setFontFamily(fontFamily, XWPFRun.FontCharRange.eastAsia);
			} catch (Exception ignored) {
			}

		} catch (Exception e) {
			System.out.println("Could not set font '" + fontFamily + "': " + e.getMessage());
		}
	}

	/**
	 * Adds a footer with "Page X of Y" using live Word fields (PAGE / NUMPAGES).
	 */
	private void addPageNumberFooter(XWPFDocument document, String fontFamily) throws Exception {
		XWPFFooter footer = document.createFooter(HeaderFooterType.DEFAULT);
		XWPFParagraph footerPara = footer.getParagraphArray(0) != null ? footer.getParagraphArray(0)
				: footer.createParagraph();
		footerPara.setAlignment(ParagraphAlignment.CENTER);

		XWPFRun prefixRun = footerPara.createRun();
		setRunStyle(prefixRun, fontFamily, 9, false);
		prefixRun.setText("Page ");

		addFieldRun(footerPara, "PAGE", fontFamily);

		XWPFRun ofRun = footerPara.createRun();
		setRunStyle(ofRun, fontFamily, 9, false);
		ofRun.setText(" of ");

		addFieldRun(footerPara, "NUMPAGES", fontFamily);
	}

	/**
	 * Inserts a live Word field (e.g. PAGE, NUMPAGES) using raw XML, since POI has
	 * no high-level API for it.
	 */
	private void addFieldRun(XWPFParagraph paragraph, String fieldName, String fontFamily) {
		CTSimpleField field = paragraph.getCTP().addNewFldSimple();
		field.setInstr(" " + fieldName + " ");
		CTR fieldR = field.addNewR();
		XWPFRun fieldRun = new XWPFRun(fieldR, (IRunBody) paragraph);
		setRunStyle(fieldRun, fontFamily, 9, false);
	}

	/**
	 * Title-cases a string ("hello world" -> "Hello World"). Unused by the main
	 * method; kept as a utility.
	 */
	public static String toTitleCase(String input) {
		if (input == null || input.trim().isEmpty()) {
			return input;
		}
		String[] words = input.trim().toLowerCase().split("\\s+");
		StringBuilder result = new StringBuilder();
		for (String word : words) {
			result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
		}
		return result.toString().trim();
	}

	// ----------------------------------------------------------------

	@Override
	public String getMenuPlanningSimpleReport11(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails) {
		try {
			PdfFont basicFont = null;
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "", l1 = "",
					l2 = "", note = "", eContact = "", eventFlow = "", function = "", person = "", eTime = "",
					date = "", rate = "", party = "", eventNotes = "", billingNameLabel = "", serviceLabel = "",
					themeLabel = "", remarks = "";
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				customerName = "नाम";
				customerPhone = "मोबाइल नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "दिनांक";
				fNotes = "नोट्स";
				eVenue = "आयोजन स्थान";
				function = "कार्यक्रम";
				person = "मेम्बर्स";
				eTime = "समय";
				eContact = "संपर्क नंबर";
				l1 = "व्यक्तियों की संख्या:";
				eventFlow = "कार्यक्रम का संचालन क्रम";
				note = "नोट";
				date = "दिनांक";
				rate = "रेट";
				party = "पार्टी का नाम";
				eventNotes = "नोट्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				remarks = "रिमार्क्स";
				y = 512;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Regular.ttf");

					// Tamil
					customerName = "வாடிக்கையாளர் பெயர்";
					customerPhone = "மொபைல் எண்";
					eName = "நிகழ்ச்சி பெயர்";
					eDate = "நிகழ்ச்சி தேதி";
					fNotes = "உணவு விவரம்";
					eVenue = "நிகழ்வு இடம்";
					function = "நிகழ்ச்சி";
					person = "நபர்";
					eTime = "நேரம்";
					eContact = "தொடர்பு எண்";
					l1 = "நபர்களின் எண்ணிக்கை:";
					eventFlow = "நிகழ்ச்சி நடைபெறும் வரிசை";
					note = "குறிப்பு";
					date = "தேதி";
					rate = "விலை";
					party = "கட்சியின் பெயர்";
					eventNotes = "நிகழ்வு குறிப்புகள்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					remarks = "ரிமார்க்ஸ்";
				} else if (language.equalsIgnoreCase("Telugu")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Regular.ttf");

					// Telugu
					customerName = "కస్టమర్ పేరు";
					customerPhone = "మొబైల్ నంబర్";
					eName = "కార్యక్రమ పేరు";
					eDate = "కార్యక్రమ తేదీ";
					fNotes = "భోజన వివరాలు";
					eVenue = "కార్యక్రమ స్థలం";
					function = "కార్యక్రమం";
					person = "వ్యక్తి";
					eTime = "సమయం";
					eContact = "సంప్రదింపు నంబర్";
					l1 = "వ్యక్తుల సంఖ్య:";
					eventFlow = "కార్యక్రమ నిర్వహణ క్రమం";
					note = "గమనిక";
					date = "తేదీ";
					rate = "రేటు";
					party = "పార్టీ పేరు";
					eventNotes = "ఈవెంట్ గమనికలు";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					remarks = "రిమార్క్స్";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Regular.ttf");

					// Malayalam
					customerName = "ഉപഭോക്താവിന്റെ പേര്";
					customerPhone = "മൊബൈൽ നമ്പർ";
					eName = "പരിപാടിയുടെ പേര്";
					eDate = "പരിപാടിയുടെ തീയതി";
					fNotes = "ഭക്ഷണ വിശദാംശങ്ങൾ";
					eVenue = "പരിപാടി സ്ഥലം";
					function = "പരിപാടി";
					person = "വ്യക്തി";
					eTime = "സമയം";
					eContact = "ബന്ധപ്പെടാനുള്ള നമ്പർ";
					l1 = "വ്യക്തികളുടെ എണ്ണം:";
					eventFlow = "പരിപാടി നടത്തിപ്പ് ക്രമം";
					note = "കുറിപ്പ്";
					date = "തീയതി";
					rate = "നിരക്ക്";
					party = "പാർട്ടി പേര്";
					eventNotes = "ഇവന്റ് കുറിപ്പുകൾ";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					remarks = "റിമാർക്സ്";
				} else if (language.equalsIgnoreCase("Marathi")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");

					// Marathi
					customerName = "ग्राहकाचे नाव";
					customerPhone = "मोबाईल नंबर";
					eName = "कार्यक्रमाचे नाव";
					eDate = "कार्यक्रमाची दिनांक";
					fNotes = "भोजन तपशील";
					eVenue = "आयोजन स्थळ";
					function = "कार्यक्रम";
					person = "व्यक्ती";
					eTime = "वेळ";
					eContact = "संपर्क नंबर";
					l1 = "व्यक्तींची संख्या:";
					eventFlow = "कार्यक्रमाचा संचालन क्रम";
					note = "टीप";
					date = "दिनांक";
					rate = "रेट";
					party = "पार्टीचे नाव";
					eventNotes = "कार्यक्रम नोंदी";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					remarks = "रिमार्क्स";
				} else {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");

					customerName = "ગ્રાહકનું નામ";
					customerPhone = "મોબાઇલ નંબર";
					eName = "કાર્યક્રમનું નામ";
					eDate = "કાર્યક્રમની તારીખ";
					fNotes = "ભોજન વિગતો";
					eVenue = "આયોજન સ્થળ";
					function = "કાર્યક્રમ";
					person = "વ્યક્તિ";
					eTime = "સમય";
					eContact = "સંપર્ક નંબર";
					eventFlow = "કાર્યક્રમનું સંચાલન ક્રમ";
					l1 = "વ્યક્તિઓની સંખ્યા:";
					note = "નોંધ";
					date = "તારીખ";
					rate = "રેટ";
					party = "પાર્ટીનું નામ";
					eventNotes = "કાર્યક્રમની નોંધ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					remarks = "રિમાર્ક્સ";
				}
				System.out.println("Gujarati font loaded successfully");
				y = 510;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				System.out.println("English font loaded successfully");
				customerName = "Customer Name";
				customerPhone = "Mobile No.";
				eName = "Event Name";
				eDate = "Event Date";
				fNotes = "Food Note";
				eVenue = "Venue";
				function = "Function";
				person = "Persons";
				eTime = "Timing";
				eContact = "CONTACT NO";
				eventFlow = "FLOW OF EVENT";
				l1 = "OF PERSONS:";
				note = "Note";
				date = "Date";
				rate = "Rate";
				party = "Party Name";
				eventNotes = "Event Notes";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";
				remarks = "Remarks";
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);

			if (eventDto == null) {
				return "";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "back office report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(30, 30, 50, 30);

			// Load all background images upfront
			ImageData mainBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
			ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			String watermarkPath;

			watermarkPath = environment.getProperty("app.image.url") + eventDto.getLogo();
//			watermarkPath = "/flipbook/pages/logo.png";

			if (isCompanyDetails == 1) {
				pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(watermarkPath));
			}

			Color blackColor = new DeviceRgb(0, 0, 0);

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			String venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			String foodNotesName = eventDto.getFoodType();
			String foodNotes = eventDto.getFoodNotes();
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone() != null ? eventDto.getCmpPhone() : "";
			String cmpAddress = eventDto.getCmpAddress() != null ? eventDto.getCmpAddress() : "";
			String cmpEmail = eventDto.getEmail() != null ? eventDto.getEmail() : "";
			String remarksValue = eventDto.getRemark() != null ? eventDto.getRemark() : "";
			String eventTime = eventDto.getEventTime() != null ? eventDto.getEventTime() : "";

			boolean isFirstFunction = true;

			// Create main table with 2 columns for the header layout
			Table catItemTable = null;

			String billingName = "";
			String service = "";
			String theme = "";
			String foodPref = eventDto.getFoodType();
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
				System.out.println(service);
			}

			Table eventTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 2f, 26f, 20f, 2f, 20f }));
			eventTable.setWidth(UnitValue.createPercentValue(100f));
			eventTable.setFixedLayout();
			eventTable.setBorderBottom(new SolidBorder(blackColor, 1f));
			eventTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

			// Common Styles
			Style labelStyle = new Style().setFont(basicFont).setFontColor(blackColor).setFontSize(14)
					.setTextAlignment(TextAlignment.LEFT);

			Style valueStyle = new Style().setFont(basicFont).setFontColor(blackColor).setFontSize(14)
					.setTextAlignment(TextAlignment.LEFT);

			ImageData logoData = null;
			logoData = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + eventDto.getLogo());
//			logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");

			Image logo = new Image(logoData);

			// Resize & align
			logo.scaleToFit(120, 120);
			logo.setAutoScale(false);
			logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

			cell = new Cell(6, 1).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER)
					.setPadding(3f);

			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph(cmpName.toUpperCase()).setFont(basicFont).setFontSize(12)
							.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph(cmpAddress).setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph().add(new Text("Mobile No : ").simulateBold()).add(new Text(cmpPhone))
							.setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph().add(new Text("Email : ").simulateBold()).add(new Text(cmpEmail))
							.setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			eventTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));
			eventTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));

			addRow(eventTable, eDate, eventDate, labelStyle, valueStyle, blackColor, true, "BOTTOM");
			addRow(eventTable, eTime, eventTime, labelStyle, valueStyle, blackColor, true, "BOTTOM");

			addFullRow(eventTable, party, hostName, labelStyle, valueStyle, blackColor);

			if (billingName != null && !billingName.trim().isEmpty()) {
				addFullRow(eventTable, billingNameLabel, billingName, labelStyle, valueStyle, blackColor);
			}

			addFullRow(eventTable, customerPhone, mobileNo, labelStyle, valueStyle, blackColor);
			addFullRow(eventTable, eVenue, venue, labelStyle, valueStyle, blackColor);
			addFullRow(eventTable, eName, eventName, labelStyle, valueStyle, blackColor);
			addFullRow(eventTable, person, pax, labelStyle, valueStyle, blackColor);
			addFullRow(eventTable, remarks, remarksValue, labelStyle, valueStyle, blackColor);
			addFullRow(eventTable, fNotes,
					foodNotesName.toString() + (foodNotes.trim().isEmpty() ? "" : " (" + foodNotes + ")"), labelStyle,
					valueStyle, blackColor);

			if (service.trim().length() != 0) {
				addFullRow(eventTable, serviceLabel, service, labelStyle, valueStyle, blackColor);
			}

			if (theme.trim().length() != 0) {
				addFullRow(eventTable, themeLabel, theme, labelStyle, valueStyle, blackColor);
			}

			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				if (!isFirstFunction) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					eventTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 2f, 26f, 20f, 2f, 20f }));
					eventTable.setWidth(UnitValue.createPercentValue(100f));
					eventTable.setFixedLayout();
					eventTable.setBorderBottom(new SolidBorder(blackColor, 1f));
					eventTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
				}

				String functionVenue = "";
				String functionNotes = "";
				if (lang == 1) {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenueHindi() != null
							? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: "";
					functionNotes = eventFunctionMasterResponseDto.getNotesHindi() != null
							? eventFunctionMasterResponseDto.getNotesHindi()
							: "";
				} else if (lang == 2) {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenueGujarati() != null
							? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
							: "";
					functionNotes = eventFunctionMasterResponseDto.getNotesGujarati() != null
							? eventFunctionMasterResponseDto.getNotesGujarati()
							: "";
				} else {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenue() != null
							? eventFunctionMasterResponseDto.getFunctionVenue()
							: "";
					functionNotes = eventFunctionMasterResponseDto.getNotesEnglish() != null
							? eventFunctionMasterResponseDto.getNotesEnglish()
							: "";
				}

				String eventStartTime = eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp().split(" ")[1].substring(0, 5) + " "
								+ eventFunctionMasterResponseDto.getFunctionStartTimestamp().split(" ")[2]
						: "";

				String eventEndTime = eventFunctionMasterResponseDto.getFunctionEndTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionEndTimestamp().split(" ")[1].substring(0, 5) + " "
								+ eventFunctionMasterResponseDto.getFunctionEndTimestamp().split(" ")[2]
						: "";

				addRow(eventTable, function, eventFunctionMasterResponseDto.getFunctionName().toUpperCase(), labelStyle,
						valueStyle, blackColor, true, "TOP");

				addRow(eventTable, person, eventFunctionMasterResponseDto.getPax().toString(), labelStyle, valueStyle,
						blackColor, true, "TOP");

				addFullRow(eventTable, eVenue, functionVenue, labelStyle, valueStyle, blackColor);

				addFullRow(eventTable, date,
						safeText(eventFunctionMasterResponseDto.getFunctionStartTimestamp().split(" ")[0]) + " "
								+ safeText(eventStartTime).toUpperCase() + " To "
								+ safeText(eventEndTime).toUpperCase(),
						labelStyle, valueStyle, blackColor);
				addFullRow(eventTable, lang == 0 ? "Function Notes" : eventNotes, functionNotes, labelStyle, valueStyle,
						blackColor);

				addLabelRow(eventTable, "MENU", labelStyle, blackColor);
				document.add(eventTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				// Add menu content
				for (MenuReportResponseDto menu : menuReportResponseDtos) {

					catItemTable = new Table(UnitValue.createPercentArray(new float[] { 100f }));
					catItemTable.setWidth(UnitValue.createPercentValue(100));
					catItemTable.setBorder(Border.NO_BORDER);
					catItemTable.setMarginTop(8f);
					catItemTable.setKeepTogether(true);

					Paragraph p = new Paragraph(
							menu.getNameEnglish() != null ? menu.getNameEnglish().toUpperCase() : "").setFont(basicFont)
							.setFontSize(16f).setFontColor(blackColor).setSplitCharacters(new ISplitCharacters() {

								@Override
								public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
									return true;
								}
							}).setMarginLeft(0).setPadding(0).simulateBold().setUnderline();

					cell = new Cell().add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);

					// Category Instructions
					if (isCategoryInstruction == 1) {
						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
							cell.add(new Paragraph(
									menuPreparationServiceImpl.formatText(" ( " + menu.getMenuNotes() + " )", lang))
									.setSplitCharacters(new ISplitCharacters() {

										@Override
										public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
											return true;
										}
									}).setFont(basicFont).setFontSize(12f).setFontColor(blackColor).setMarginLeft(0)
									.setMarginTop(2f).setTextAlignment(TextAlignment.CENTER));
						}
					}

					catItemTable.addCell(cell);
					cell = new Cell().setBorder(Border.NO_BORDER);

					StringBuilder sb = new StringBuilder();
					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						if (item.getNameEnglish().toUpperCase() != null) {
							if (sb.length() > 0) {
								sb.append(" / ");
							}
							sb.append(item.getNameEnglish().toUpperCase());
						}
//						// Item Instructions
//						if (isItemInstruction == 1) {
//							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
//								cell.add(new Paragraph(menuPreparationServiceImpl
//										.formatText(" ( " + item.getItemNotes() + " ) ", lang)).setFont(basicFont)
//										.setSplitCharacters(new ISplitCharacters() {
//
//											@Override
//											public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
//												return true;
//											}
//										}).setFontSize(12).setFixedLeading(13f).setFontColor(blackColor)
//										.setMarginLeft(0).setMarginTop(2f).setMarginBottom(0f).setPadding(0f)
//										.setTextAlignment(TextAlignment.CENTER));
//							}
//						}
					}
					String items = sb.toString();
					Paragraph p1 = new Paragraph(items).setFont(basicFont).setFontSize(14f).setFontColor(blackColor)
							.setMarginLeft(0).setPadding(0).setPaddingTop(5f).setTextAlignment(TextAlignment.CENTER);
					cell.add(p1);
					catItemTable.addCell(cell);
					document.add(catItemTable);
				}
				isFirstFunction = false;
			}

			/* ================= PAGE NUMBERS ================= */
			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 22,
						TextAlignment.CENTER);

				canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"back office report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String getMenuPlanningSimpleReport11Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails) {

		getMenuPlanningSimpleReport11(eventId, eventFunctionId, isCategorySlogan, isCategoryInstruction,
				isCategoryImage, isItemSlogan, isItemInstruction, isCompanyDetails, re, lang, userid, isPartyDetails);

		try {

			EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);

			if (event == null) {
				return null;
			}

			String rootPath = re.getSession().getServletContext().getRealPath("/");

			String pdfFileName = getReportName(getPartyNameByEventId(eventId),
					formatDate(event.getEventStartDateTime()), "back office report") + ".pdf";

			String pdfPath = rootPath + "resources/tempDownload/" + event.getEventNo() + "/" + pdfFileName;

			return adobeDocxGenerator.convertPdfToDocxAndGetUrl(new File(pdfPath), event.getEventNo());

		} catch (Exception e) {
			throw new RuntimeException("Failed to convert PDF to DOCX", e);
		}
	}
	/*
	 * @Override public String getMenuPlanningSimpleReport1Docx(Long eventId, Long
	 * eventFunctionId, Integer isCategorySlogan, Integer isCategoryInstruction,
	 * Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
	 * Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid,
	 * Integer isPartyDetails) { try { menuPreparationServiceImpl.loadLicense();
	 * 
	 * // ── 1. Language labels ──────────────────────────────────────── String
	 * fontName, customerName, customerPhone, eName, eDate, fNotes, eVenue,
	 * function, person, eTime, eContact, eventFlow, l1, note, date, rate, party,
	 * eventNotes, billingNameLabel, serviceLabel, themeLabel;
	 * 
	 * if (lang == 1) { fontName = "Nirmala UI"; customerName = "नाम"; customerPhone
	 * = "मोबाइल नंबर"; eName = "कार्यक्रम का नाम"; eDate = "दिनांक"; fNotes =
	 * "नोट्स"; eVenue = "आयोजन स्थान"; function = "कार्यक्रम"; person = "मेम्बर्स";
	 * eTime = "समय"; eContact = "संपर्क नंबर"; l1 = "व्यक्तियों की संख्या:";
	 * eventFlow = "कार्यक्रम का संचालन क्रम"; note = "नोट"; date = "दिनांक"; rate =
	 * "रेट"; party = "पार्टी का नाम"; eventNotes = "नोट्स"; billingNameLabel =
	 * "बिलिंग नाम"; serviceLabel = "सर्विस"; themeLabel = "थीम";
	 * 
	 * } else if (lang == 2) { String language =
	 * userBasicDetailsMasterRepository.getLangByUserId(userid); if
	 * (language.equalsIgnoreCase("Tamil")) { fontName = "Latha"; customerName =
	 * "வாடிக்கையாளர் பெயர்"; customerPhone = "மொபைல் எண்"; eName =
	 * "நிகழ்ச்சி பெயர்"; eDate = "நிகழ்ச்சி தேதி"; fNotes = "உணவு விவரம்"; eVenue =
	 * "நிகழ்வு இடம்"; function = "நிகழ்ச்சி"; person = "நபர்"; eTime = "நேரம்";
	 * eContact = "தொடர்பு எண்"; l1 = "நபர்களின் எண்ணிக்கை:"; eventFlow =
	 * "நிகழ்ச்சி நடைபெறும் வரிசை"; note = "குறிப்பு"; date = "தேதி"; rate = "விலை";
	 * party = "கட்சியின் பெயர்"; eventNotes = "நிகழ்வு குறிப்புகள்";
	 * billingNameLabel = "பில்லிங் பெயர்"; serviceLabel = "சர்வீஸ்"; themeLabel =
	 * "தீம்"; } else if (language.equalsIgnoreCase("Telugu")) { fontName =
	 * "Gautami"; customerName = "కస్టమర్ పేరు"; customerPhone = "మొబైల్ నంబర్";
	 * eName = "కార్యక్రమ పేరు"; eDate = "కార్యక్రమ తేదీ"; fNotes = "భోజన వివరాలు";
	 * eVenue = "కార్యక్రమ స్థలం"; function = "కార్యక్రమం"; person = "వ్యక్తి";
	 * eTime = "సమయం"; eContact = "సంప్రదింపు నంబర్"; l1 = "వ్యక్తుల సంఖ్య:";
	 * eventFlow = "కార్యక్రమ నిర్వహణ క్రమం"; note = "గమనిక"; date = "తేదీ"; rate =
	 * "రేటు"; party = "పార్టీ పేరు"; eventNotes = "ఈవెంట్ గమనికలు";
	 * billingNameLabel = "బిల్లింగ్ పేరు"; serviceLabel = "సర్వీస్"; themeLabel =
	 * "థీమ్"; } else if (language.equalsIgnoreCase("Malayalam")) { fontName =
	 * "Kartika"; customerName = "ഉപഭോക്താവിന്റെ പേര്"; customerPhone =
	 * "മൊബൈൽ നമ്പർ"; eName = "പരിപാടിയുടെ പേര്"; eDate = "പരിപാടിയുടെ തീയതി";
	 * fNotes = "ഭക്ഷണ വിശദാംശങ്ങൾ"; eVenue = "പരിപാടി സ്ഥലം"; function = "പരിപാടി";
	 * person = "വ്യക്തി"; eTime = "സമയം"; eContact = "ബന്ധപ്പെടാനുള്ള നമ്പർ"; l1 =
	 * "വ്യക്തികളുടെ എണ്ണം:"; eventFlow = "പരിപാടി നടത്തിപ്പ് ക്രമം"; note =
	 * "കുറിപ്പ്"; date = "തീയതി"; rate = "നിരക്ക്"; party = "പാർട്ടി പേര്";
	 * eventNotes = "ഇവന്റ് കുറിപ്പുകൾ"; billingNameLabel = "ബില്ലിംഗ് പേര്";
	 * serviceLabel = "സർവീസ്"; themeLabel = "തീം"; } else if
	 * (language.equalsIgnoreCase("Marathi")) { fontName = "Nirmala UI";
	 * customerName = "ग्राहकाचे नाव"; customerPhone = "मोबाईल नंबर"; eName =
	 * "कार्यक्रमाचे नाव"; eDate = "कार्यक्रमाची दिनांक"; fNotes = "भोजन तपशील";
	 * eVenue = "आयोजन स्थळ"; function = "कार्यक्रम"; person = "व्यक्ती"; eTime =
	 * "वेळ"; eContact = "संपर्क नंबर"; l1 = "व्यक्तींची संख्या:"; eventFlow =
	 * "कार्यक्रमाचा संचालन क्रम"; note = "टीप"; date = "दिनांक"; rate = "रेट";
	 * party = "पार्टीचे नाव"; eventNotes = "कार्यक्रम नोंदी"; billingNameLabel =
	 * "बिलिंग नाव"; serviceLabel = "सर्व्हिस"; themeLabel = "थीम"; } else {
	 * fontName = "Shruti"; customerName = "ગ્રાહકનું નામ"; customerPhone =
	 * "મોબાઇલ નંબર"; eName = "કાર્યક્રમનું નામ"; eDate = "કાર્યક્રમની તારીખ";
	 * fNotes = "ભોજન વિગતો"; eVenue = "આયોજન સ્થળ"; function = "કાર્યક્રમ"; person
	 * = "વ્યક્તિ"; eTime = "સમય"; eContact = "સંપર્ક નંબર"; l1 =
	 * "વ્યક્તિઓની સંખ્યા:"; eventFlow = "કાર્યક્રમનું સંચાલન ક્રમ"; note = "નોંધ";
	 * date = "તારીખ"; rate = "રેટ"; party = "પાર્ટીનું નામ"; eventNotes =
	 * "કાર્યક્રમની નોંધ"; billingNameLabel = "બિલિંગ નામ"; serviceLabel = "સર્વિસ";
	 * themeLabel = "થીમ"; } } else { fontName = "Arial"; customerName =
	 * "Customer Name"; customerPhone = "Mobile No."; eName = "Event Name"; eDate =
	 * "Event Date"; fNotes = "Food Note"; eVenue = "Venue"; function = "Function";
	 * person = "Persons"; eTime = "TIMING"; eContact = "CONTACT NO"; eventFlow =
	 * "FLOW OF EVENT"; l1 = "OF PERSONS:"; note = "Note"; date = "Date"; rate =
	 * "Rate"; party = "PARTY NAME"; eventNotes = "Event Notes"; billingNameLabel =
	 * "Billing Name"; serviceLabel = "Service"; themeLabel = "Theme"; }
	 * 
	 * // ── 2. Fetch data ─────────────────────────────────────────────
	 * EventReportResponseDto eventDto =
	 * menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
	 * eventFunctionId, lang, userid); if (eventDto == null) return "";
	 * 
	 * String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() :
	 * "Not Specified"; String mobileNo = eventDto.getMobileNo() != null ?
	 * eventDto.getMobileNo() : "Not Specified"; String eventDate =
	 * eventDto.getEventStartTimestamp() != null ? eventDto.getEventStartTimestamp()
	 * : ""; String eventName = eventDto.getEventName() != null ?
	 * eventDto.getEventName() : ""; String venue = eventDto.getVenue() != null ?
	 * eventDto.getVenue() : ""; String foodNotesName = eventDto.getFoodType();
	 * String cmpName = eventDto.getCmpName(); String cmpPhone =
	 * eventDto.getCmpPhone() != null ? eventDto.getCmpPhone() : ""; String
	 * cmpAddress = eventDto.getCmpAddress() != null ? eventDto.getCmpAddress() :
	 * ""; String cmpEmail = eventDto.getEmail() != null ? eventDto.getEmail() : "";
	 * String remarks = eventDto.getRemark() != null ? eventDto.getRemark() : "";
	 * 
	 * String billingName, foodNotes, service, theme; if (lang == 1) { billingName =
	 * nvl(eventDto.getBillingNameHindi()); foodNotes =
	 * nvl(eventDto.getFoodNotesHindi()); service = nvl(eventDto.getServiceHindi());
	 * theme = nvl(eventDto.getThemeHindi()); } else if (lang == 2) { billingName =
	 * nvl(eventDto.getBillingNameGujarati()); foodNotes =
	 * nvl(eventDto.getFoodNotesGujarati()); service =
	 * nvl(eventDto.getServiceGujarati()); theme = nvl(eventDto.getThemeGujarati());
	 * } else { billingName = nvl(eventDto.getBillingNameEnglish()); foodNotes =
	 * nvl(eventDto.getFoodNotes()); service = nvl(eventDto.getService()); theme =
	 * nvl(eventDto.getTheme()); }
	 * 
	 * String foodDisplay = foodNotes.trim().isEmpty() ? foodNotesName :
	 * foodNotesName + " (" + foodNotes + ")";
	 * 
	 * // ── 3. Output path ──────────────────────────────────────────── String
	 * rootPath = re.getSession().getServletContext().getRealPath("/"); File
	 * outputPath = new File(rootPath + "resources/tempDownload/" +
	 * eventDto.getEventNo() + "/"); if (!outputPath.exists()) outputPath.mkdirs();
	 * String baseName = getReportName(getPartyNameByEventId(eventId),
	 * formatDate(eventDto.getEventStartTimestamp()), "back office report"); File
	 * docxFile = new File(outputPath, baseName + ".docx");
	 * 
	 * // ── 4. Create document ────────────────────────────────────────
	 * XWPFDocument doc = new XWPFDocument();
	 * 
	 * // Page margins (twips: 720 = 0.5", 600 ≈ 0.42") CTBody body =
	 * doc.getDocument().getBody(); CTSectPr sectPr = body.isSetSectPr() ?
	 * body.getSectPr() : body.addNewSectPr(); CTPageMar mar = sectPr.isSetPgMar() ?
	 * sectPr.getPgMar() : sectPr.addNewPgMar();
	 * mar.setTop(BigInteger.valueOf(720)); mar.setBottom(BigInteger.valueOf(1008));
	 * mar.setLeft(BigInteger.valueOf(600)); mar.setRight(BigInteger.valueOf(600));
	 * 
	 * // A4 page size CTPageSz pgSz = sectPr.isSetPgSz() ? sectPr.getPgSz() :
	 * sectPr.addNewPgSz(); pgSz.setW(BigInteger.valueOf(11906));
	 * pgSz.setH(BigInteger.valueOf(16838));
	 * 
	 * // Font sizes (half-points) final int FS_CMP = 24; // 12 pt – company header
	 * final int FS_LABEL = 26; // 13 pt – info grid labels/values final int FS_CAT
	 * = 28; // 14 pt – category names final int FS_ITEM = 26; // 13 pt – menu items
	 * final int FS_NOTE = 22; // 11 pt – item sub-notes
	 * 
	 * // Column widths (twips). Content width = 11906 - 600 - 600 = 10706 final int
	 * TW = 10706; final int CL = 1750; // label col final int CC = 120; // colon
	 * col final int CV = 2400; // value col final int CVWIDE = TW - CL - CC - CV -
	 * CL - CC; // remaining (~2166)
	 * 
	 * boolean isFirstFunction = true;
	 * 
	 * for (EventFunctionReportResponseDto fn : eventDto.getFunctions()) {
	 * 
	 * // Page break between functions if (!isFirstFunction) { XWPFParagraph pb =
	 * doc.createParagraph(); pb.setPageBreak(true); }
	 * 
	 * // ── 4a. Company header ───────────────────────────────────── if
	 * (isFirstFunction && isCompanyDetails == 1) { // Company name – centred bold
	 * XWPFParagraph pCmp = doc.createParagraph();
	 * pCmp.setAlignment(ParagraphAlignment.CENTER); XWPFRun rCmp =
	 * pCmp.createRun(); rCmp.setText(cmpName.toUpperCase());
	 * rCmp.setFontFamily(fontName); rCmp.setFontSize(FS_CMP / 2 + 2);
	 * rCmp.setBold(true);
	 * 
	 * // If logo is set, embed it before the name line inside a table if
	 * (eventDto.getLogo() != null && !eventDto.getLogo().isEmpty()) { // Remove the
	 * paragraph we just added – we'll use a table instead
	 * doc.removeBodyElement(doc.getBodyElements().size() - 1);
	 * 
	 * XWPFTable logoTable = doc.createTable(1, 2); setTableWidth(logoTable, TW);
	 * removeAllBorders(logoTable);
	 * 
	 * // Logo cell XWPFTableCell logoCell = logoTable.getRow(0).getCell(0);
	 * setCellWidth(logoCell, (int) (TW * 0.15)); try { String logoUrl =
	 * environment.getProperty("app.image.url") + eventDto.getLogo(); ImageData
	 * logoData = menuPreparationServiceImpl.loadImageFromResource(logoUrl);
	 * 
	 * // Extract raw bytes from ImageData and wrap back into InputStream byte[]
	 * logoBytes = logoData.getData(); InputStream logoStream = new
	 * ByteArrayInputStream(logoBytes);
	 * 
	 * XWPFParagraph logoPara = logoCell.getParagraphs().get(0);
	 * logoPara.setAlignment(ParagraphAlignment.CENTER); XWPFRun logoRun =
	 * logoPara.createRun(); logoRun.addPicture(logoStream,
	 * XWPFDocument.PICTURE_TYPE_PNG, "logo.png", Units.toEMU(80), Units.toEMU(60));
	 * } catch (Exception ignored) {
	 * 
	 * }
	 * 
	 * // Info cell XWPFTableCell infoCell = logoTable.getRow(0).getCell(1);
	 * setCellWidth(infoCell, (int) (TW * 0.85));
	 * 
	 * XWPFParagraph pName = infoCell.getParagraphs().get(0); XWPFRun rName =
	 * pName.createRun(); rName.setText(cmpName.toUpperCase());
	 * rName.setFontFamily(fontName); rName.setFontSize(FS_CMP / 2 + 2);
	 * rName.setBold(true);
	 * 
	 * addLabelValuePara(infoCell, "Mobile No : ", cmpPhone, fontName, FS_CMP);
	 * addLabelValuePara(infoCell, "Email : ", cmpEmail, fontName, FS_CMP); } else {
	 * // No logo – plain centred paragraphs addCentredLabelValuePara(doc,
	 * "Mobile No : ", cmpPhone, fontName, FS_CMP); addCentredLabelValuePara(doc,
	 * "Email : ", cmpEmail, fontName, FS_CMP); }
	 * 
	 * // Spacer doc.createParagraph(); }
	 * 
	 * // ── 4b. Event-info grid (double-top-border on first row) ─── if
	 * (isFirstFunction) { // 6-column table: [L|CO|V|L|CO|CVWIDE] int[] colWidths =
	 * { CL, CC, CV, CL, CC, CVWIDE }; XWPFTable infoTable = doc.createTable(0, 6);
	 * setTableWidth(infoTable, TW); setColWidths(infoTable, colWidths);
	 * removeAllBorders(infoTable);
	 * 
	 * if (isPartyDetails == 1) { addInfo6Row(infoTable, fontName, FS_LABEL,
	 * colWidths, customerName, hostName, billingNameLabel, billingName,
	 * BorderType.DOUBLE, "000000", "000000"); addInfo6Row(infoTable, fontName,
	 * FS_LABEL, colWidths, customerPhone, mobileNo, eName, eventName.toUpperCase(),
	 * BorderType.NONE, "000000", "000000"); addInfo6Row(infoTable, fontName,
	 * FS_LABEL, colWidths, eDate, eventDate, fNotes, foodDisplay, BorderType.NONE,
	 * "000000", "FF0000"); } else { addInfo6Row(infoTable, fontName, FS_LABEL,
	 * colWidths, eName, eventName.toUpperCase(), eDate, eventDate,
	 * BorderType.DOUBLE, "000000", "000000"); addInfo6Row(infoTable, fontName,
	 * FS_LABEL, colWidths, fNotes, foodDisplay, "", "", BorderType.NONE, "FF0000",
	 * "000000"); } // Venue – value spans 4 cols addInfoWideRow(infoTable,
	 * fontName, FS_LABEL, colWidths, eVenue, venue.toUpperCase(), BorderType.NONE,
	 * "000000", BorderType.NONE); // Service + Theme addInfo6Row(infoTable,
	 * fontName, FS_LABEL, colWidths, serviceLabel, service, themeLabel, theme,
	 * BorderType.NONE, "000000", "000000"); // Remarks – value spans 4 cols
	 * addInfoWideRow(infoTable, fontName, FS_LABEL, colWidths, eventNotes, remarks,
	 * BorderType.NONE, "000000", BorderType.NONE); }
	 * 
	 * // ── 4c. Function block (single-top-border on first row) ──── String
	 * functionVenue, functionNotes; if (lang == 1) { functionVenue =
	 * nvl(fn.getFunctionVenueHindi()); functionNotes = nvl(fn.getNotesHindi()); }
	 * else if (lang == 2) { functionVenue = nvl(fn.getFunctionVenueGujarati());
	 * functionNotes = nvl(fn.getNotesGujarati()); } else { functionVenue =
	 * nvl(fn.getFunctionVenue()); functionNotes = nvl(fn.getNotesEnglish()); }
	 * 
	 * String startTime = "", endTime = ""; if (fn.getFunctionStartTimestamp() !=
	 * null) { String[] parts = fn.getFunctionStartTimestamp().split(" "); startTime
	 * = parts.length >= 3 ? parts[1].substring(0, 5) + " " + parts[2] : ""; } if
	 * (fn.getFunctionEndTimestamp() != null) { String[] parts =
	 * fn.getFunctionEndTimestamp().split(" "); endTime = parts.length >= 3 ?
	 * parts[1].substring(0, 5) + " " + parts[2] : ""; } String fnDateDisplay =
	 * (fn.getFunctionStartTimestamp() != null ?
	 * fn.getFunctionStartTimestamp().split(" ")[0] : "") + " " +
	 * startTime.toUpperCase() + " To " + endTime.toUpperCase();
	 * 
	 * int[] colWidths = { CL, CC, CV, CL, CC, CVWIDE }; XWPFTable fnTable =
	 * doc.createTable(0, 6); setTableWidth(fnTable, TW); setColWidths(fnTable,
	 * colWidths); removeAllBorders(fnTable);
	 * 
	 * // Row 1: कार्यक्रम | venue — single top border addInfo6Row(fnTable,
	 * fontName, FS_LABEL, colWidths, function,
	 * safeText(fn.getFunctionName().toUpperCase()), eVenue,
	 * safeText(functionVenue), BorderType.SINGLE, "000000", "000000");
	 * 
	 * // Row 2: persons | date+time addInfo6Row(fnTable, fontName, FS_LABEL,
	 * colWidths, person, safeText(fn.getPax().toString()), date,
	 * safeText(fnDateDisplay), BorderType.NONE, "000000", "000000");
	 * 
	 * // Row 3: function notes — wide String fnNotesLabel = (lang == 0) ?
	 * "Function Notes" : eventNotes; addInfoWideRow(fnTable, fontName, FS_LABEL,
	 * colWidths, fnNotesLabel, safeText(functionNotes), BorderType.NONE, "000000",
	 * BorderType.SINGLE);
	 * 
	 * // ── 4d. Menu categories ──────────────────────────────────── for
	 * (MenuReportResponseDto menu : fn.getMenuCategories()) { // Spacer before each
	 * category table XWPFParagraph sp = doc.createParagraph();
	 * sp.setSpacingAfter(0);
	 * 
	 * int catW = (int) (TW * 0.45); int itemsW = TW - catW;
	 * 
	 * XWPFTable catTable = doc.createTable(1, 2); setTableWidth(catTable, TW); //
	 * catTable.getCTTbl().getTblPr().getTblBorders().getTop().setVal(STBorder.NONE)
	 * ; // will be set // per-cell below removeAllBorders(catTable); // Category
	 * name cell (left, centred, bold) XWPFTableCell catCell =
	 * catTable.getRow(0).getCell(0); setCellWidth(catCell, catW);
	 * setCellTopBottomBorder(catCell, true, true);
	 * 
	 * XWPFParagraph catPara = catCell.getParagraphs().get(0);
	 * 
	 * catPara.setAlignment(ParagraphAlignment.CENTER); // Horizontal center
	 * catPara.setVerticalAlignment(
	 * org.apache.poi.xwpf.usermodel.TextAlignment.CENTER ); // Vertical text
	 * alignment in paragraph
	 * 
	 * catPara.setSpacingBefore(80); catPara.setSpacingAfter(80);
	 * 
	 * XWPFRun catRun = catPara.createRun();
	 * 
	 * String catDisplayName = getCategoryDisplayName(menu, lang);
	 * catRun.setText(catDisplayName.toUpperCase()); catRun.setFontFamily(fontName);
	 * catRun.setFontSize(FS_CAT / 2); catRun.setBold(true);
	 * 
	 * // Category instruction (optional, smaller, centred) if
	 * (isCategoryInstruction == 1 && menu.getMenuNotes() != null &&
	 * !menu.getMenuNotes().isEmpty()) { XWPFParagraph instrPara =
	 * catCell.addParagraph(); instrPara.setAlignment(ParagraphAlignment.CENTER);
	 * XWPFRun instrRun = instrPara.createRun(); instrRun.setText( " ( " +
	 * menuPreparationServiceImpl.formatText(menu.getMenuNotes(), lang) + " )");
	 * instrRun.setFontFamily(fontName); instrRun.setFontSize(FS_NOTE / 2); }
	 * 
	 * // Items cell (right) XWPFTableCell itemsCell =
	 * catTable.getRow(0).getCell(1); setCellWidth(itemsCell, itemsW);
	 * setCellTopBottomBorder(itemsCell, true, true);
	 * 
	 * boolean firstItem = true; for (MenuItemForReportResponseDto item :
	 * menu.getMenuItems()) { XWPFParagraph itemPara = firstItem ?
	 * itemsCell.getParagraphs().get(0) : itemsCell.addParagraph(); firstItem =
	 * false; itemPara.setAlignment(ParagraphAlignment.LEFT);
	 * itemPara.setSpacingBefore(20); itemPara.setSpacingAfter(20);
	 * 
	 * String itemDisplayName = getItemDisplayName(item, lang); XWPFRun itemRun =
	 * itemPara.createRun(); itemRun.setText("\u2022 " + (itemDisplayName != null ?
	 * itemDisplayName.toUpperCase() : "")); itemRun.setFontFamily(fontName);
	 * itemRun.setFontSize(FS_ITEM / 2);
	 * 
	 * // Item instruction (optional, indented, smaller) if (isItemInstruction == 1)
	 * { String itemNotes = getItemNotes(item, lang); if (itemNotes != null &&
	 * !itemNotes.isEmpty()) { XWPFParagraph notePara = itemsCell.addParagraph();
	 * notePara.setIndentationLeft(200); notePara.setSpacingBefore(0);
	 * notePara.setSpacingAfter(20); XWPFRun noteRun = notePara.createRun();
	 * noteRun.setText("( " + menuPreparationServiceImpl.formatText(itemNotes, lang)
	 * + " )"); noteRun.setFontFamily(fontName); noteRun.setFontSize(FS_NOTE / 2); }
	 * } } }
	 * 
	 * isFirstFunction = false; }
	 * 
	 * // ── 5. Footer: "Page X of Y" ────────────────────────────────── XWPFFooter
	 * footer = doc.createFooter(HeaderFooterType.DEFAULT); XWPFParagraph fp =
	 * footer.createParagraph(); fp.setAlignment(ParagraphAlignment.CENTER);
	 * addPageNumberField(fp, fontName, FS_CMP);
	 * 
	 * // ── 6. Write file ───────────────────────────────────────────── try
	 * (FileOutputStream fos = new FileOutputStream(docxFile)) { doc.write(fos); }
	 * doc.close();
	 * 
	 * return environment.getProperty("ws_image_path") + "/api/download/docx/" +
	 * eventDto.getEventNo() + "/" + baseName + ".docx";
	 * 
	 * } catch (Exception e) { e.printStackTrace(); throw new
	 * RuntimeException("Failed to generate Event Menu Report (DOCX)", e); } }
	 * 
	 * // ════════════════════════════════════════════════════════════════ // Helper
	 * enums / types //
	 * ════════════════════════════════════════════════════════════════ private enum
	 * BorderType { NONE, SINGLE, DOUBLE }
	 * 
	 * // ════════════════════════════════════════════════════════════════ // Helper
	 * methods // ════════════════════════════════════════════════════════════════
	 * 
	 * // Null-safe empty string. private String nvl(String s) { return s != null ?
	 * s : ""; }
	 * 
	 * // Set table total width (twips). private void setTableWidth(XWPFTable table,
	 * int widthTwips) { CTTblPr tblPr = getOrCreateTblPr(table); CTTblWidth tblW =
	 * tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
	 * tblW.setW(BigInteger.valueOf(widthTwips)); tblW.setType(STTblWidth.DXA); }
	 * 
	 * // Set cell preferred width (twips). private void setCellWidth(XWPFTableCell
	 * cell, int widthTwips) { CTTcPr tcPr = getOrCreateTcPr(cell); CTTblWidth tcW =
	 * tcPr.isSetTcW() ? tcPr.getTcW() : tcPr.addNewTcW();
	 * tcW.setW(BigInteger.valueOf(widthTwips)); tcW.setType(STTblWidth.DXA); }
	 * 
	 * private void setTblBorder(CTBorder b, STBorder.Enum val) { b.setVal(val);
	 * b.setSz(BigInteger.ZERO); b.setSpace(BigInteger.ZERO); b.setColor("auto"); }
	 * 
	 * private void setCellTopBottomBorder(XWPFTableCell cell, boolean top, boolean
	 * bottom) { CTTcPr tcPr = getOrCreateTcPr(cell); CTTcBorders borders =
	 * tcPr.isSetTcBorders() ? tcPr.getTcBorders() : tcPr.addNewTcBorders();
	 * 
	 * // TOP: single line if (top) { CTBorder t = borders.isSetTop() ?
	 * borders.getTop() : borders.addNewTop(); t.setVal(STBorder.SINGLE);
	 * t.setSz(BigInteger.valueOf(4)); t.setSpace(BigInteger.ZERO);
	 * t.setColor("000000"); } // BOTTOM: double line (matches PDF) if (bottom) {
	 * CTBorder bt = borders.isSetBottom() ? borders.getBottom() :
	 * borders.addNewBottom(); bt.setVal(STBorder.SINGLE);
	 * bt.setSz(BigInteger.valueOf(6)); bt.setSpace(BigInteger.ZERO);
	 * bt.setColor("000000"); } // Suppress left/right for (CTBorder side : new
	 * CTBorder[] { borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft(),
	 * borders.isSetRight() ? borders.getRight() : borders.addNewRight() }) {
	 * side.setVal(STBorder.NONE); side.setSz(BigInteger.ZERO);
	 * side.setColor("auto"); } }
	 * 
	 * // Apply top border to a single cell based on BorderType. private void
	 * applyCellTopBorder(XWPFTableCell cell, BorderType bt) { if (bt ==
	 * BorderType.NONE) return; CTTcPr tcPr = getOrCreateTcPr(cell); CTTcBorders
	 * borders = tcPr.isSetTcBorders() ? tcPr.getTcBorders() :
	 * tcPr.addNewTcBorders(); CTBorder top = borders.isSetTop() ? borders.getTop()
	 * : borders.addNewTop(); top.setVal(bt == BorderType.DOUBLE ? STBorder.DOUBLE :
	 * STBorder.SINGLE); top.setSz(BigInteger.valueOf(bt == BorderType.DOUBLE ? 6 :
	 * 4)); top.setSpace(BigInteger.ZERO); top.setColor("000000"); }
	 * 
	 * // Apply top border to a single cell based on BorderType. private void
	 * applyCellBottomBorder(XWPFTableCell cell, BorderType bt) { if (bt ==
	 * BorderType.NONE) return; CTTcPr tcPr = getOrCreateTcPr(cell); CTTcBorders
	 * borders = tcPr.isSetTcBorders() ? tcPr.getTcBorders() :
	 * tcPr.addNewTcBorders(); CTBorder bottom = borders.isSetBottom() ?
	 * borders.getBottom() : borders.addNewBottom(); bottom.setVal(bt ==
	 * BorderType.DOUBLE ? STBorder.DOUBLE : STBorder.SINGLE);
	 * bottom.setSz(BigInteger.valueOf(bt == BorderType.DOUBLE ? 6 : 4));
	 * bottom.setSpace(BigInteger.ZERO); bottom.setColor("000000"); }
	 * 
	 * // Suppress all borders on a single cell. private void
	 * suppressCellBorders(XWPFTableCell cell) { CTTcPr tcPr =
	 * getOrCreateTcPr(cell); CTTcBorders borders = tcPr.isSetTcBorders() ?
	 * tcPr.getTcBorders() : tcPr.addNewTcBorders(); for (CTBorder b : new
	 * CTBorder[] { borders.isSetTop() ? borders.getTop() : borders.addNewTop(),
	 * borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom(),
	 * borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft(),
	 * borders.isSetRight() ? borders.getRight() : borders.addNewRight() }) {
	 * b.setVal(STBorder.NONE); b.setSz(BigInteger.ZERO); b.setColor("auto"); } }
	 * 
	 * private void addInfo6Row(XWPFTable table, String font, int fs, int[]
	 * colWidths, String l1, String v1, String l2, String v2, BorderType topBorder,
	 * String v1Color, String v2Color) {
	 * 
	 * XWPFTableRow row = table.createRow(); // Ensure exactly 6 cells while
	 * (row.getTableCells().size() < 6) row.addNewTableCell();
	 * 
	 * String[] labels = { l1, "", v1, l2, "", v2 }; boolean[] bolds = { true,
	 * false, false, true, false, false }; String[] colors = { "000000", "000000",
	 * v1Color, "000000", "000000", v2Color }; String[] colons = { "", ":", "", "",
	 * ":", "" };
	 * 
	 * for (int i = 0; i < 6; i++) { XWPFTableCell cell = row.getCell(i);
	 * suppressCellBorders(cell); if (topBorder != BorderType.NONE)
	 * applyCellTopBorder(cell, topBorder); setCellWidth(cell, colWidths[i]);
	 * setCellMargins(cell, 40, 40, 60, 60);
	 * 
	 * XWPFParagraph p = cell.getParagraphs().isEmpty() ? cell.addParagraph() :
	 * cell.getParagraphs().get(0); String text = (i == 1 || i == 4) ? ":" :
	 * labels[i]; XWPFRun r = p.createRun(); r.setText(text); r.setFontFamily(font);
	 * r.setFontSize(fs / 2); r.setBold(bolds[i]); r.setColor(colors[i]); } }
	 * 
	 * private void addInfoWideRow(XWPFTable table, String font, int fs, int[]
	 * colWidths, String label, String value, BorderType topBorder, String
	 * valueColor, BorderType bottomBorder) {
	 * 
	 * XWPFTableRow row = table.createRow(); // We need only 3 cells; last one spans
	 * columns 3-6 while (row.getTableCells().size() > 3) {
	 * row.getCtRow().removeTc(row.getTableCells().size() - 1); } while
	 * (row.getTableCells().size() < 3) row.addNewTableCell();
	 * 
	 * // Col widths for the 3 logical cells int wLabel = colWidths[0]; int wColon =
	 * colWidths[1]; int wValue = colWidths[2] + colWidths[3] + colWidths[4] +
	 * colWidths[5];
	 * 
	 * String[] texts = { label, ":", value }; boolean[] bolds = { true, false,
	 * false }; String[] colors = { "000000", "000000", valueColor }; int[] widths =
	 * { wLabel, wColon, wValue }; int[] spans = { 1, 1, 4 };
	 * 
	 * for (int i = 0; i < 3; i++) { XWPFTableCell cell = row.getCell(i);
	 * suppressCellBorders(cell); if (topBorder != BorderType.NONE)
	 * applyCellTopBorder(cell, topBorder);
	 * 
	 * if (bottomBorder != BorderType.NONE) applyCellBottomBorder(cell,
	 * bottomBorder);
	 * 
	 * setCellWidth(cell, widths[i]); setCellMargins(cell, 40, 40, 60, 60);
	 * 
	 * // Apply column span for the value cell if (spans[i] > 1) { CTTcPr tcPr =
	 * getOrCreateTcPr(cell); CTDecimalNumber span = tcPr.isSetGridSpan() ?
	 * tcPr.getGridSpan() : tcPr.addNewGridSpan();
	 * span.setVal(BigInteger.valueOf(spans[i])); }
	 * 
	 * XWPFParagraph p = cell.getParagraphs().isEmpty() ? cell.addParagraph() :
	 * cell.getParagraphs().get(0); XWPFRun r = p.createRun(); r.setText(texts[i]);
	 * r.setFontFamily(font); r.setFontSize(fs / 2); r.setBold(bolds[i]);
	 * r.setColor(colors[i]); } }
	 * 
	 * // Set cell internal margins (twips). private void
	 * setCellMargins(XWPFTableCell cell, int top, int bottom, int left, int right)
	 * { CTTcPr tcPr = getOrCreateTcPr(cell); CTTcMar mar = tcPr.isSetTcMar() ?
	 * tcPr.getTcMar() : tcPr.addNewTcMar(); setMarSide(mar.isSetTop() ?
	 * mar.getTop() : mar.addNewTop(), top); setMarSide(mar.isSetBottom() ?
	 * mar.getBottom() : mar.addNewBottom(), bottom); setMarSide(mar.isSetLeft() ?
	 * mar.getLeft() : mar.addNewLeft(), left); setMarSide(mar.isSetRight() ?
	 * mar.getRight() : mar.addNewRight(), right); }
	 * 
	 * private void setMarSide(CTTblWidth w, int val) {
	 * w.setW(BigInteger.valueOf(val)); w.setType(STTblWidth.DXA); }
	 * 
	 * // Add a "label (bold) + value" paragraph to a cell – used for company info.
	 * private void addLabelValuePara(XWPFTableCell cell, String label, String
	 * value, String font, int fs) { XWPFParagraph p = cell.addParagraph(); XWPFRun
	 * rl = p.createRun(); rl.setText(label); rl.setBold(true);
	 * rl.setFontFamily(font); rl.setFontSize(fs / 2); XWPFRun rv = p.createRun();
	 * rv.setText(value); rv.setFontFamily(font); rv.setFontSize(fs / 2); }
	 * 
	 * private void addCentredLabelValuePara(XWPFDocument doc, String label, String
	 * value, String font, int fs) { XWPFParagraph p = doc.createParagraph();
	 * p.setAlignment(ParagraphAlignment.CENTER); XWPFRun rl = p.createRun();
	 * rl.setText(label); rl.setBold(true); rl.setFontFamily(font);
	 * rl.setFontSize(fs / 2); XWPFRun rv = p.createRun(); rv.setText(value);
	 * rv.setFontFamily(font); rv.setFontSize(fs / 2); }
	 * 
	 * 
	 * private void addPageNumberField(XWPFParagraph para, String font, int fs) { //
	 * "Page " XWPFRun r1 = para.createRun(); r1.setText("Page ");
	 * r1.setFontFamily(font); r1.setFontSize(fs / 2);
	 * 
	 * // PAGE field
	 * para.getCTP().addNewR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);
	 * XWPFRun instrPage = para.createRun();
	 * instrPage.getCTR().addNewInstrText().setStringValue(" PAGE ");
	 * para.getCTP().addNewR().addNewFldChar().setFldCharType(STFldCharType.END);
	 * 
	 * // " of " XWPFRun r2 = para.createRun(); r2.setText(" of ");
	 * r2.setFontFamily(font); r2.setFontSize(fs / 2);
	 * 
	 * // NUMPAGES field
	 * para.getCTP().addNewR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);
	 * XWPFRun instrTotal = para.createRun();
	 * instrTotal.getCTR().addNewInstrText().setStringValue(" NUMPAGES ");
	 * para.getCTP().addNewR().addNewFldChar().setFldCharType(STFldCharType.END); }
	 * 
	 * private String getCategoryDisplayName(MenuReportResponseDto menu, int lang) {
	 * return nvl(menu.getNameEnglish()); }
	 * 
	 * private String getItemDisplayName(MenuItemForReportResponseDto item, int
	 * lang) { return nvl(item.getNameEnglish()); }
	 * 
	 * private String getItemNotes(MenuItemForReportResponseDto item, int lang) {
	 * 
	 * return nvl(item.getItemNotes()); }
	 * 
	 * private CTTcPr getOrCreateTcPr(XWPFTableCell c) { CTTcPr tcPr =
	 * c.getCTTc().getTcPr(); return tcPr != null ? tcPr : c.getCTTc().addNewTcPr();
	 * }
	 * 
	 * private void setColWidths(XWPFTable table, int[] widths) { CTTbl ctTbl =
	 * table.getCTTbl(); // Remove existing tblGrid using XmlCursor (works across
	 * all POI versions) CTTblGrid existingGrid = ctTbl.getTblGrid(); if
	 * (existingGrid != null) { XmlCursor cursor = existingGrid.newCursor();
	 * cursor.removeXml(); cursor.dispose(); } CTTblGrid grid =
	 * ctTbl.addNewTblGrid(); for (int w : widths) { CTTblGridCol col =
	 * grid.addNewGridCol(); col.setW(BigInteger.valueOf(w)); } }
	 * 
	 * private void removeAllBorders(XWPFTable table) { CTTblPr tblPr =
	 * getOrCreateTblPr(table); CTTblBorders b = tblPr.getTblBorders() != null ?
	 * tblPr.getTblBorders() : tblPr.addNewTblBorders();
	 * 
	 * CTBorder[] sides = { b.getTop() != null ? b.getTop() : b.addNewTop(),
	 * b.getBottom() != null ? b.getBottom() : b.addNewBottom(), b.getLeft() != null
	 * ? b.getLeft() : b.addNewLeft(), b.getRight() != null ? b.getRight() :
	 * b.addNewRight(), b.getInsideH() != null ? b.getInsideH() : b.addNewInsideH(),
	 * b.getInsideV() != null ? b.getInsideV() : b.addNewInsideV(), }; for (CTBorder
	 * side : sides) { side.setVal(STBorder.NONE); side.setSz(BigInteger.ZERO);
	 * side.setSpace(BigInteger.ZERO); side.setColor("auto"); } }
	 * 
	 * private CTTblPr getOrCreateTblPr(XWPFTable t) { CTTbl ctTbl = t.getCTTbl();
	 * // getTblPr() returns null if not set (no isSet check needed) CTTblPr tblPr =
	 * ctTbl.getTblPr(); return tblPr != null ? tblPr : ctTbl.addNewTblPr(); }
	 */

	@Override
	public String getMenuPlanningSimpleReport12(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, int intValue, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyLogo, Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid,
			Integer isPartyDetails, ReportMenuPlanningRequestDTO request) {
		try {
			/* ================= LICENSE ================= */
			menuPreparationServiceImpl.loadLicense();

			/* ================= FONTS ================= */
			PdfFont fontRegular;
			PdfFont fontBold;
			PdfFont cmpFont;
			PdfFont cmpFontBold;
			String function = "", person = "", date = "", eTime = "";
			String cnm = "Customer Name";
			String mno = "Mobile No.";
			String enm = "Event Name";
			String edt = "Event Date";
			String venue = "Venue";
			String eventNotes = "Event Notes";
			String billingNameLabel = "Billing Name";
			String foodNotesLabel = "Food Note";
			String serviceLabel = "Service";
			String themeLabel = "Theme";
			String note = "Note";
			float itemLine = 0.8f;
			float catLine = 0.9f;

			if (lang == 1) {
				fontRegular = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				function = "कार्यक्रम";
				person = "मेंबर्स";
				eTime = "समय";
				date = "दिनांक";
				cnm = "ग्राहक का नाम";
				mno = "मोबाइल नंबर";
				enm = "कार्यक्रम का नाम";
				edt = "दिनांक";
				venue = "आयोजन स्थान";
				eventNotes = "कार्यक्रम की नोट";
				itemLine = 0.9f;
				billingNameLabel = "बिलिंग नाम";
				foodNotesLabel = "नोट्स";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				note = "नोट";
				catLine = 1f;
			} else if (lang == 2) {

				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {
					fontRegular = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Regular.ttf");
					fontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Bold.ttf");

					// Tamil
					function = "நிகழ்ச்சி";
					person = "நபர்";
					eTime = "நேரம்";
					date = "தேதி";
					cnm = "வாடிக்கையாளர் பெயர்";
					mno = "மொபைல் எண்";
					enm = "நிகழ்ச்சி பெயர்";
					edt = "நிகழ்ச்சி தேதி";
					venue = "நிகழ்வு இடம்";
					eventNotes = "நிகழ்வு குறிப்புகள்";
					billingNameLabel = "பில்லிங் பெயர்";
					foodNotesLabel = "உணவு குறிப்புகள்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					note = "குறிப்பு";
				} else if (language.equalsIgnoreCase("Telugu")) {
					fontRegular = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					fontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					// Telugu
					function = "కార్యక్రమం";
					person = "వ్యక్తి";
					eTime = "సమయం";
					date = "తేదీ";
					cnm = "కస్టమర్ పేరు";
					mno = "మొబైల్ నంబర్";
					enm = "కార్యక్రమ పేరు";
					edt = "కార్యక్రమ తేదీ";
					venue = "కార్యక్రమ స్థలం";
					eventNotes = "ఈవెంట్ గమనికలు";
					billingNameLabel = "బిల్లింగ్ పేరు";
					foodNotesLabel = "ఆహార గమనికలు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					note = "గమనిక";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					fontRegular = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					fontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					// Malayalam
					function = "പരിപാടി";
					person = "വ്യക്തി";
					eTime = "സമയം";
					date = "തീയതി";
					cnm = "ഉപഭോക്താവിന്റെ പേര്";
					mno = "മൊബൈൽ നമ്പർ";
					enm = "പരിപാടിയുടെ പേര്";
					edt = "പരിപാടിയുടെ തീയതി";
					venue = "പരിപാടി സ്ഥലം";
					eventNotes = "ഇവന്റ് കുറിപ്പുകൾ";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					foodNotesLabel = "ഭക്ഷണ കുറിപ്പുകൾ";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					note = "കുറിപ്പ്";
				} else if (language.equalsIgnoreCase("Marathi")) {
					fontRegular = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
					fontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");

					// Marathi
					function = "कार्यक्रम";
					person = "व्यक्ती";
					eTime = "वेळ";
					date = "दिनांक";
					cnm = "ग्राहकाचे नाव";
					mno = "मोबाईल नंबर";
					enm = "कार्यक्रमाचे नाव";
					edt = "कार्यक्रमाची दिनांक";
					venue = "आयोजन स्थळ";
					eventNotes = "कार्यक्रम नोंदी";
					billingNameLabel = "बिलिंग नाव";
					foodNotesLabel = "अन्न नोंदी";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					note = "नोंदी";
				} else {
					fontRegular = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					fontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					function = "કાર્યક્રમ";
					person = "વ્યક્તિ";
					eTime = "સમય";
					date = "તારીખ";
					cnm = "ગ્રાહકનું નામ";
					mno = "મોબાઇલ નંબર";
					enm = "કાર્યક્રમનું નામ";
					edt = "કાર્યક્રમની તારીખ";
					venue = "આયોજન સ્થળ";
					eventNotes = "કાર્યક્રમની નોંધ";
					billingNameLabel = "બિલિંગ નામ";
					foodNotesLabel = "ભોજન નોંધ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					note = "નોંધ";
				}
			} else {
//	            fontRegular = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
//	            fontBold = menuPreparationServiceImpl.loadFont("/fonts/timesbd.ttf");
				fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				function = "Function";
				person = "Persons";
				eTime = "Time";
				date = "Date";
				itemLine = 1f;
				catLine = 1.1f;
			}
			cmpFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
			cmpFontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			/* ================= DATA ================= */
			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);

			if (eventDto == null)
				return "";

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);

			if (eventData == null) {
				throw new RuntimeException("MenuQuantityReponseDto is null for eventId=" + eventId);
			}

			/* ================= FILE SETUP ================= */
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

			File dir = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");
			if (!dir.exists())
				dir.mkdirs();

			PdfWriter writer = new PdfWriter(new File(dir, "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "back office report") + ".pdf"));

			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, PageSize.A4);

			Integer catFontSize = request.getCatFontSize() != null && request.getCatFontSize() > 0
					? request.getCatFontSize()
					: 14;
			Integer itemFontSize = request.getItemFontSize() != null && request.getItemFontSize() > 0
					? request.getItemFontSize()
					: 12;
			Integer sloganFontSize = request.getSloganFontSize() != null && request.getSloganFontSize() > 0
					? request.getSloganFontSize()
					: 10;

			if (isCompanyDetails == 1) {
				String watermarkPath = environment.getProperty("app.image.url") + eventDto.getLogo();
//				String watermarkPath = "/flipbook/pages/krishnai_logo.png";
				pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(watermarkPath));
			}

			/* ================= MARGINS (CRITICAL) ================= */
			float headerHeight = 210f;
			document.setMargins(headerHeight + 40, 28, 40, 28);

			/* ================= LOGO ================= */
			String logoUrl = eventDto.getLogo();
//			String logoUrl = "/flipbook/pages/krishnai_logo.png";
			ImageData tempLogo = null;
			System.out.println("logoUrl : " + logoUrl);
			if (isCompanyDetails == 1 && logoUrl != null && !logoUrl.isEmpty()) {
//				tempLogo = menuPreparationServiceImpl.loadImageFromResource(logoUrl);
				tempLogo = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + logoUrl);

			}

			final ImageData logoImage = tempLogo;
			final String cnm2 = cnm;
			final String mno2 = mno;
			final String enm2 = enm;
			final String edt2 = edt;
			final String venue2 = venue;
			final String enotes = eventNotes;
			final String billName = billingNameLabel;
			final String fNotesLabel = foodNotesLabel;
			final String sLabel = serviceLabel;
			final String thLabel = themeLabel;
//	        logoImage = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");

			Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 100f }))
					.setBorder(Border.NO_BORDER);

//			headerTable.setFixedLayout();
			headerTable.setWidth(UnitValue.createPercentValue(100f));

			Table companyTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 80f }));
			companyTable.setWidth(UnitValue.createPercentValue(100f));

			Cell logoCell = new Cell().setBorder(Border.NO_BORDER);

			if (isCompanyDetails == 1 && logoImage != null) {

				try {

					Image logo = new Image(logoImage);
					logo.setWidth(100);
					logo.setHeight(60);

					logoCell.add(logo);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			companyTable.addCell(logoCell);

			Cell companyCell = new Cell().setBorder(Border.NO_BORDER);

			if (isCompanyDetails == 1) {
				companyCell.add(new Paragraph(eventData.getCompanyName() != null ? eventData.getCompanyName() : "")
						.setFont(cmpFontBold).setFontSize(18));

				companyCell.add(new Paragraph(
						"Mobile No. : " + (eventData.getOfficeNo() != null ? eventData.getOfficeNo() : ""))
						.setFont(cmpFont).setFontSize(13));

				companyCell.add(new Paragraph(
						"Email : " + (eventData.getCompanyEmail() != null ? eventData.getCompanyEmail() : ""))
						.setFont(cmpFont).setFontSize(13));
			}

			companyTable.addCell(companyCell);

			headerTable.addCell(new Cell().setBorder(Border.NO_BORDER).setPaddingBottom(5).add(companyTable));

			String billingName = "";
			String foodNotes = "";
			String service = "";
			String theme = "";
			String foodName = eventDto.getFoodType();

			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
			}

			Table eventTable = new Table(UnitValue.createPercentArray(new float[] { 23f, 27f, 23f, 27f }));

			eventTable.setWidth(UnitValue.createPercentValue(100f));
			eventTable.setFixedLayout();
			eventTable.setBorder(new SolidBorder(1f));

			/* PARTY DETAILS */
			if (isPartyDetails == 1) {
				eventTable.addCell(new Cell().add(new Paragraph(cnm2 + " :").setFont(fontBold).setFontSize(catFontSize))
						.setBorder(Border.NO_BORDER));

				eventTable.addCell(
						new Cell().add(new Paragraph(eventData.getPartyName() != null ? eventData.getPartyName() : "")
								.setFont(fontRegular).setFontSize(itemFontSize)).setBorder(Border.NO_BORDER));

				if (billingName != null && billingName.trim().length() != 0) {
					eventTable.addCell(
							new Cell().add(new Paragraph(billName + " :").setFont(fontBold).setFontSize(catFontSize))
									.setBorder(Border.NO_BORDER));

					eventTable.addCell(
							new Cell().add(new Paragraph(billingName).setFont(fontRegular).setFontSize(itemFontSize))
									.setBorder(Border.NO_BORDER));
				}
				eventTable.addCell(new Cell().add(new Paragraph(mno2 + " :").setFont(fontBold).setFontSize(catFontSize))
						.setBorder(Border.NO_BORDER));

				eventTable.addCell(new Cell()
						.add(new Paragraph(eventData.getPartyMobile() != null ? eventData.getPartyMobile() : "")
								.setFont(fontRegular).setFontSize(itemFontSize))
						.setBorder(Border.NO_BORDER));
			}

			/* EVENT NAME */
			eventTable.addCell(new Cell().add(new Paragraph(enm2 + " :").setFont(fontBold).setFontSize(catFontSize))
					.setBorder(Border.NO_BORDER));

			eventTable
					.addCell(
							new Cell()
									.add(new Paragraph(eventData.getEventName() != null ? eventData.getEventName() : "")
											.setFont(fontRegular).setFontSize(itemFontSize))
									.setBorder(Border.NO_BORDER));

			/* EVENT DATE */
			eventTable.addCell(new Cell().add(new Paragraph(edt2 + " :").setFont(fontBold).setFontSize(catFontSize))
					.setBorder(Border.NO_BORDER));

			eventTable
					.addCell(
							new Cell()
									.add(new Paragraph(eventData.getEventDate() != null ? eventData.getEventDate() : "")
											.setFont(fontRegular).setFontSize(itemFontSize))
									.setBorder(Border.NO_BORDER));

			String eventVenue = eventData.getVenueName() != null ? eventData.getVenueName() : "";

			if (eventVenue.trim().length() != 0) {
				/* VENUE */
				eventTable
						.addCell(new Cell().add(new Paragraph(venue2 + " :").setFont(fontBold).setFontSize(catFontSize))
								.setBorder(Border.NO_BORDER));

				eventTable.addCell(
						new Cell().add(new Paragraph(eventVenue).setFont(fontRegular).setFontSize(itemFontSize))
								.setBorder(Border.NO_BORDER));
			}

			if (service.trim().length() != 0) {
				/* SERVICE */
				eventTable
						.addCell(new Cell().add(new Paragraph(sLabel + " :").setFont(fontBold).setFontSize(catFontSize))
								.setBorder(Border.NO_BORDER));

				eventTable.addCell(new Cell().add(new Paragraph(service).setFont(fontRegular).setFontSize(itemFontSize))
						.setBorder(Border.NO_BORDER));
			}

			if (theme.trim().length() != 0) {
				/* THEME */
				eventTable.addCell(
						new Cell().add(new Paragraph(thLabel + " :").setFont(fontBold).setFontSize(catFontSize))
								.setBorder(Border.NO_BORDER));

				eventTable.addCell(new Cell().add(new Paragraph(theme).setFont(fontRegular).setFontSize(itemFontSize))
						.setBorder(Border.NO_BORDER));
			}

			String eventRemark = eventDto.getRemark() != null ? eventDto.getRemark() : "";

			if (eventRemark.trim().length() != 0) {
				/* REMARKS */
				eventTable
						.addCell(new Cell().add(new Paragraph(enotes + " :").setFont(fontBold).setFontSize(catFontSize))
								.setBorder(Border.NO_BORDER));

				eventTable.addCell(
						new Cell(1, 3).add(new Paragraph(eventRemark).setFont(fontRegular).setFontSize(itemFontSize))
								.setBorder(Border.NO_BORDER));
			}

			String foodNote = foodNotes.trim().isEmpty() ? foodName : foodName + " (" + foodNotes + ")";

			if (foodNote != null && foodNote.trim().length() != 0) {
				/* FOOD NOTES */
				eventTable.addCell(
						new Cell().add(new Paragraph(fNotesLabel + " :").setFont(fontBold).setFontSize(catFontSize))
								.setBorder(Border.NO_BORDER));

				eventTable
						.addCell(
								new Cell(1, 3)
										.add(new Paragraph(foodNote).setFont(fontRegular)
												.setFontColor(new DeviceRgb(255, 0, 0)).setFontSize(itemFontSize))
										.setBorder(Border.NO_BORDER));
			}

			headerTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(eventTable));

			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, new ShortMenuHeaderEventHandler(headerTable, 20f));

			/* ================= 3 COLUMN LAYOUT ================= */
			float gutter = 5f;
			float leftMargin = document.getLeftMargin();
			float rightMargin = document.getRightMargin();
			float topMargin = 20;
			float top = PageSize.A4.getTop() - topMargin - calculateTableHeight(headerTable, pdfDocument);
			float bottom = PageSize.A4.getBottom() + 60;
			float height = top - bottom;
			float usableWidth = PageSize.A4.getWidth() - leftMargin - rightMargin;
			float columnWidth = ((usableWidth - (gutter * 2)) / 3) - 4;

			Rectangle[] columns = new Rectangle[] { new Rectangle(leftMargin, bottom, columnWidth, height),
					new Rectangle(leftMargin + columnWidth + 12, bottom, columnWidth, height),
					new Rectangle(leftMargin + (columnWidth * 2) + 24, bottom, columnWidth, height) };

			document.setRenderer(new ColumnDocumentRenderer(document, columns));

			/* ================= CONTENT ================= */
			for (EventFunctionReportResponseDto functionDto : eventDto.getFunctions()) {
				String functionNote = "";
				if (lang == 1) {
					functionNote = functionDto.getNotesHindi() != null ? functionDto.getNotesHindi() : "";
				} else if (lang == 2) {
					functionNote = functionDto.getNotesGujarati() != null ? functionDto.getNotesGujarati() : "";
				} else {
					functionNote = functionDto.getNotesEnglish() != null ? functionDto.getNotesEnglish() : "";
				}
				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

				DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

				LocalDateTime start = LocalDateTime.parse(functionDto.getFunctionStartTimestamp(), inputFormatter);
				LocalDateTime end = LocalDateTime.parse(functionDto.getFunctionEndTimestamp(), inputFormatter);

				String dateTime = start.format(timeFormatter) + " to " + end.format(timeFormatter);

				Div data = new Div().setKeepTogether(true);

				data.add(new Paragraph().add(new Text(function + ": ").setFont(fontBold)).setPadding(0)
						.setMultipliedLeading(catLine)
						.add(new Text(functionDto.getFunctionName().toUpperCase()).setFont(fontRegular)).setPadding(0)
						.setMultipliedLeading(catLine).add(new Text("\n" + person + ": ").setFont(fontBold))
						.setPadding(0).setMultipliedLeading(catLine)
						.add(new Text(functionDto.getPax().toString()).setFont(fontRegular)).setPadding(0)
						.setMultipliedLeading(catLine)
//						.add(new Text("\n" + date + ": ").setFont(fontBold)).setPadding(0)
//						.setMultipliedLeading(catLine)
//						.add(new Text(functionDto.getFunctionStartTimestamp().split(" ")[0]).setFont(fontRegular))
//						.setMultipliedLeading(catLine).setPadding(0)
						.add(new Text("\n" + eTime + ": ").setFont(fontBold)).setMultipliedLeading(catLine)
						.add(new Text(dateTime).setFont(fontRegular)).setPadding(0).setFont(fontBold)
						.setMultipliedLeading(catLine).setBorder(new SolidBorder(1f)).setFontSize(13).setPadding(3f)
						.add(new Text("\n" + note + ": ").setFont(fontBold)).setMultipliedLeading(catLine)
						.add(new Text(functionNote).setFont(fontRegular)).setPadding(0).setFont(fontBold)
						.setMultipliedLeading(catLine).setBorder(Border.NO_BORDER).setFontSize(catFontSize)
						.setPadding(3f).setMarginBottom(5).setMultipliedLeading(catLine));

				document.add(data);

				for (MenuReportResponseDto menu : functionDto.getMenuCategories()) {
					Div categoryBlock = new Div().setKeepTogether(true).setMarginBottom(5).setMarginTop(5);

					String menuName = menu.getNameEnglish() != null ? menu.getNameEnglish().toUpperCase() : "";

					categoryBlock.add(new Paragraph(menuName).setFont(fontBold).setFontSize(catFontSize).setUnderline()
							.setMarginTop(3).setMarginBottom(0).setMultipliedLeading(catLine));

					if (isCategoryInstruction == 1 && menu.getMenuNotes() != null
							&& !menu.getMenuNotes().trim().isEmpty()) {
						categoryBlock.add(new Paragraph("(" + menu.getMenuNotes().trim() + ")").setFont(fontBold)
								.setFontSize(sloganFontSize).setMargin(0).setMultipliedLeading(catLine));
					}

					Div itemContainer = new Div();

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						Div row = new Div();

						String itemName = item.getNameEnglish() != null ? item.getNameEnglish().toUpperCase() : "";

						row.add(new Paragraph("•  " + itemName).setFont(fontRegular).setFontSize(itemFontSize)
								.setMargin(0).setMultipliedLeading(itemLine));

						if (isItemInstruction == 1 && item.getItemNotes() != null
								&& !item.getItemNotes().trim().isEmpty()) {
							row.add(new Paragraph("(" + item.getItemNotes().trim() + ")").setFont(fontRegular)
									.setFontSize(sloganFontSize).setMarginLeft(12).setMarginTop(0)
									.setMultipliedLeading(itemLine));
						}

						itemContainer.add(row);
					}

					categoryBlock.add(itemContainer);

					document.add(categoryBlock);
				}
			}

			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);

				if (page == null || page.isFlushed()) {
					continue;
				}

				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 7,
						TextAlignment.CENTER);

				canvas.close();
			}

			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo() + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"back office report")
					+ ".pdf";

		} catch (Exception e) {
			throw new RuntimeException("Failed to generate menu report", e);
		}
	}

	@Override
	public String getMenuPlanningSimpleReport13(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails,
			Integer isFunctionNextPage, Integer isHalfPax) {

		try {
			PdfFont basicFont = null;
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "", l1 = "",
					l2 = "", note = "", eContact = "", eventFlow = "", function = "", person = "", eTime = "",
					date = "", rate = "", party = "", eventNotes = "", serviceLabel = "", themeLabel = "",
					billingNameLabel = "";
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansDevanagari-Regular.ttf");
				System.out.println("Hindi font loaded successfully");
				customerName = "नाम";
				customerPhone = "नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "दिनांक";
				fNotes = "नोट्स";
				eVenue = "स्थान";
				function = "कार्यक्रम";
				person = "मेम्बर्स";
				eTime = "समय";
				eContact = "संपर्क नंबर";
				l1 = "व्यक्तियों की संख्या:";
				eventFlow = "कार्यक्रम का संचालन क्रम";
				note = "नोट";
				date = "दिनांक";
				rate = "रेट";
				party = "पार्टी का नाम";
				eventNotes = "कार्यक्रम की नोट";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				billingNameLabel = "बिलिंग नाम";
				y = 512;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Regular.ttf");

					// Tamil
					customerName = "வாடிக்கையாளர் பெயர்";
					customerPhone = "மொபைல் எண்";
					eName = "நிகழ்ச்சி பெயர்";
					eDate = "நிகழ்ச்சி தேதி";
					fNotes = "உணவு விவரம்";
					eVenue = "நிகழ்வு இடம்";
					function = "நிகழ்ச்சி";
					person = "நபர்";
					eTime = "நேரம்";
					eContact = "தொடர்பு எண்";
					l1 = "நபர்களின் எண்ணிக்கை:";
					eventFlow = "நிகழ்ச்சி நடைபெறும் வரிசை";
					note = "குறிப்பு";
					date = "தேதி";
					rate = "விலை";
					party = "கட்சியின் பெயர்";
					eventNotes = "நிகழ்வு குறிப்புகள்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					billingNameLabel = "பில்லிங் பெயர்";
				} else if (language.equalsIgnoreCase("Telugu")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Regular.ttf");

					// Telugu
					customerName = "కస్టమర్ పేరు";
					customerPhone = "మొబైల్ నంబర్";
					eName = "కార్యక్రమ పేరు";
					eDate = "కార్యక్రమ తేదీ";
					fNotes = "భోజన వివరాలు";
					eVenue = "కార్యక్రమ స్థలం";
					function = "కార్యక్రమం";
					person = "వ్యక్తి";
					eTime = "సమయం";
					eContact = "సంప్రదింపు నంబర్";
					l1 = "వ్యక్తుల సంఖ్య:";
					eventFlow = "కార్యక్రమ నిర్వహణ క్రమం";
					note = "గమనిక";
					date = "తేదీ";
					rate = "రేటు";
					party = "పార్టీ పేరు";
					eventNotes = "ఈవెంట్ గమనికలు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					billingNameLabel = "బిల్లింగ్ పేరు";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Regular.ttf");

					// Malayalam
					customerName = "ഉപഭോക്താവിന്റെ പേര്";
					customerPhone = "മൊബൈൽ നമ്പർ";
					eName = "പരിപാടിയുടെ പേര്";
					eDate = "പരിപാടിയുടെ തീയതി";
					fNotes = "ഭക്ഷണ വിശദാംശങ്ങൾ";
					eVenue = "പരിപാടി സ്ഥലം";
					function = "പരിപാടി";
					person = "വ്യക്തി";
					eTime = "സമയം";
					eContact = "ബന്ധപ്പെടാനുള്ള നമ്പർ";
					l1 = "വ്യക്തികളുടെ എണ്ണം:";
					eventFlow = "പരിപാടി നടത്തിപ്പ് ക്രമം";
					note = "കുറിപ്പ്";
					date = "തീയതി";
					rate = "നിരക്ക്";
					party = "പാർട്ടി പേര്";
					eventNotes = "ഇവന്റ് കുറിപ്പുകൾ";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
				} else if (language.equalsIgnoreCase("Marathi")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");

					// Marathi
					customerName = "ग्राहकाचे नाव";
					customerPhone = "मोबाईल नंबर";
					eName = "कार्यक्रमाचे नाव";
					eDate = "कार्यक्रमाची दिनांक";
					fNotes = "भोजन तपशील";
					eVenue = "आयोजन स्थळ";
					function = "कार्यक्रम";
					person = "व्यक्ती";
					eTime = "वेळ";
					eContact = "संपर्क नंबर";
					l1 = "व्यक्तींची संख्या:";
					eventFlow = "कार्यक्रमाचा संचालन क्रम";
					note = "टीप";
					date = "दिनांक";
					rate = "रेट";
					party = "पार्टीचे नाव";
					eventNotes = "कार्यक्रम नोंदी";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					billingNameLabel = "बिलिंग नाव";
				} else {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");

					customerName = "ગ્રાહકનું નામ";
					customerPhone = "મોબાઇલ નંબર";
					eName = "કાર્યક્રમનું નામ";
					eDate = "કાર્યક્રમની તારીખ";
					fNotes = "ભોજન વિગતો";
					eVenue = "આયોજન સ્થળ";
					function = "કાર્યક્રમ";
					person = "વ્યક્તિ";
					eTime = "સમય";
					eContact = "સંપર્ક નંબર";
					eventFlow = "કાર્યક્રમનું સંચાલન ક્રમ";
					l1 = "વ્યક્તિઓની સંખ્યા:";
					note = "નોંધ";
					date = "તારીખ";
					rate = "રેટ";
					party = "પાર્ટીનું નામ";
					eventNotes = "કાર્યક્રમની નોંધ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					billingNameLabel = "બિલિંગ નામ";
				}
				System.out.println("Gujarati font loaded successfully");
				y = 510;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				System.out.println("English font loaded successfully");
				customerName = "Party Name";
				customerPhone = "Contact No.";
				eName = "Event Name";
				eDate = "Date";
				fNotes = "Food Note";
				eVenue = "Venue";
				function = "Function";
				person = "Persons";
				eTime = "Timing";
				eContact = "CONTACT NO";
				eventFlow = "FLOW OF EVENT";
				l1 = "OF PERSONS:";
				note = "Note";
				date = "Date";
				rate = "Rate";
				party = "PARTY NAME";
				eventNotes = "Event Notes";
				serviceLabel = "Service";
				themeLabel = "Theme";
				billingNameLabel = "Billing Name";
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
			PdfFont basicFont3 = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);

			if (eventDto == null) {
				return "";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "back office report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 30, 50, 30);

			// Load all background images upfront
//			ImageData mainBgData = menuPreparationServiceImpl
//					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
//			ImageData watermarkBgData = menuPreparationServiceImpl
//					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			Color redColor = new DeviceRgb(255, 0, 0);
			Color blackColor = new DeviceRgb(0, 0, 0);

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;
			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}

			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone() != null ? eventDto.getCmpPhone() : "";
			String cmpAddress = eventDto.getCmpAddress() != null ? eventDto.getCmpAddress() : "";
			String cmpEmail = eventDto.getEmail() != null ? eventDto.getEmail() : "";
			String remarks = eventDto.getRemark() != null && !eventDto.getRemark().trim().isEmpty()
					? " (" + eventDto.getRemark() + ")"
					: "";

			boolean isFirstFunction = true;

			// Create main table with 2 columns for the header layout
			Table catItemTable = null;

			String service = "";
			String theme = "";
			String billingName = "";
			if (lang == 1) {
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
			} else if (lang == 2) {
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
			} else {
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
			}

			Table eventTable = new Table(UnitValue.createPercentArray(new float[] { 32f, 1.5f, 32f, 32f, 1.5f, 32f }));
			eventTable.setWidth(UnitValue.createPercentValue(100));
			eventTable.setMarginBottom(-2f);
			eventTable.setBorderBottom(new SolidBorder(1f));
			eventTable.setMarginTop(10f);

			ImageData logoData = null;
			logoData = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + eventDto.getLogo());
//			logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");

			Image logo = new Image(logoData);

			// Resize & align
			logo.setWidth(UnitValue.createPercentValue(100f));
			logo.setAutoScale(false);
			logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

			cell = new Cell(6, 1).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER)
					.setPadding(3f);

			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph(cmpName.toUpperCase()).setFont(basicFont2).setFontSize(12)
							.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph(cmpAddress).setFont(basicFont2).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph().add(new Text("Mobile No : ").simulateBold()).add(new Text(cmpPhone))
							.setFont(basicFont2).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			cell = new Cell(1, 5)
					.add(new Paragraph().add(new Text("Email : ").simulateBold()).add(new Text(cmpEmail))
							.setFont(basicFont2).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			if (isCompanyDetails == 1) {
				eventTable.addCell(cell);
			}

			eventTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));
			eventTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));

			cell = new Cell()
					.add(new Paragraph(customerName).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f));
			eventTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f));
			eventTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(hostName).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(new SolidBorder(1f));
			eventTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(customerPhone).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(Border.NO_BORDER)
					.setBorderTop(new SolidBorder(1f));
			eventTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(Border.NO_BORDER)
					.setBorderTop(new SolidBorder(1f));
			eventTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(mobileNo).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(Border.NO_BORDER)
					.setBorderTop(new SolidBorder(1f));
			eventTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(eVenue).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setPadding(0).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
					.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER).setPadding(0);
			eventTable.addCell(cell);

			cell = new Cell(1, 4).add(new Paragraph(venue.toUpperCase()).setFont(basicFont).setFontSize(14)
					.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT)).setPadding(0)
					.setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(eventNotes).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setPadding(0).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell(1, 4).add(new Paragraph(foodNotesName + remarks).setFont(basicFont).setPadding(0)
					.setFontSize(14).setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			document.add(eventTable);

			Table fnTable = null;

			for (EventFunctionReportResponseDto dto : eventDto.getFunctions()) {

				if (!isFirstFunction && isFunctionNextPage == 1) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}

				fnTable = new Table(UnitValue.createPercentArray(new float[] { 70f, 30f }))
						.setWidth(UnitValue.createPercentValue(100)).setBorder(Border.NO_BORDER).setMargin(0)
						.setPadding(0).setKeepTogether(true).setMarginTop(10f);

				String functionVenue = "";
				String functionNote = "";
				if (lang == 1) {
					functionVenue = dto.getFunctionVenueHindi() != null ? dto.getFunctionVenueHindi() : "";
					functionNote = dto.getNotesHindi() != null ? dto.getNotesHindi() : "";
				} else if (lang == 2) {
					functionVenue = dto.getFunctionVenueGujarati() != null ? dto.getFunctionVenueGujarati() : "";
					functionNote = dto.getNotesGujarati() != null ? dto.getNotesGujarati() : "";
				} else {
					functionVenue = dto.getFunctionVenue() != null ? dto.getFunctionVenue() : "";
					functionNote = dto.getNotesEnglish() != null ? dto.getNotesEnglish() : "";
				}

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, dto.getFunctionId());
				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				}

				/* ================= FUNCTION NAME + PAX (ROW 1) ================= */

				Cell leftCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0).setMargin(0);

				Paragraph fnName = new Paragraph(safeText(dto.getFunctionName().toUpperCase())).setFont(basicFont)
						.setFontSize(16).simulateBold().setMarginTop(10).setPadding(0);

				leftCell.add(fnName.setTextAlignment(TextAlignment.CENTER));

				Cell rightCell = new Cell().setPadding(0).setMargin(10).setTextAlignment(TextAlignment.LEFT);

				Integer fnPax = dto.getPax() != null ? (isHalfPax == 1 ? dto.getPax() / 2 : dto.getPax()) : 0;

				Paragraph paxPara = new Paragraph(person + " : " + fnPax).setFont(basicFont).setFontSize(14)
						.simulateBold().setMargin(0).setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0)
						.setPaddingLeft(5f);

				rightCell.add(paxPara);

				fnTable.addCell(leftCell);
				fnTable.addCell(rightCell);

				String startDate = dto.getFunctionStartTimestamp() != null
						? dto.getFunctionStartTimestamp().split(" ")[0]
						: "";

				String eventStartTime = dto.getFunctionStartTimestamp() != null
						? dto.getFunctionStartTimestamp().split(" ")[1].substring(0, 5) + " "
								+ dto.getFunctionStartTimestamp().split(" ")[2]
						: "";

				String eventEndTime = dto.getFunctionEndTimestamp() != null
						? dto.getFunctionEndTimestamp().split(" ")[1].substring(0, 5) + " "
								+ dto.getFunctionEndTimestamp().split(" ")[2]
						: "";

				rightCell = new Cell().setPadding(0).setMargin(10).setTextAlignment(TextAlignment.LEFT);

				Paragraph timePara = new Paragraph(eTime + " : " + eventStartTime.toUpperCase()).setFont(basicFont)
						.setFontSize(14).simulateBold().setMargin(0).setPadding(0).setPaddingLeft(5f);

				rightCell.add(timePara);

				/* ================= ROW 2: DATE + TIME ================= */
				Cell dateCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0).setMargin(0);

				Paragraph datePara = new Paragraph().add(new Text(date + " : ").setFont(basicFont).setFontSize(14))
						.add(new Text(startDate.toUpperCase()).setFont(basicFont3).setFontSize(14)).setMargin(3)
						.setPadding(0).setFixedLeading(14);

				dateCell.add(datePara);

				Paragraph funVenuePara = new Paragraph()
						.add(new Text(eVenue + " : ").setFont(basicFont).setFontSize(14))
						.add(new Text(functionVenue).setFont(basicFont3).setFontSize(14)).setMargin(3).setPadding(0)
						.setFixedLeading(14);

				dateCell.add(funVenuePara);

				fnTable.addCell(dateCell);
				fnTable.addCell(rightCell);

				/* ================= ADD FUNCTION BLOCK ================= */

				document.add(fnTable);

				isFirstFunction = false;

				/* ================= MENU PART (UNCHANGED) ================= */
				List<MenuReportResponseDto> menuReportResponseDtos = dto.getMenuCategories();

				PdfFont symbolFont = menuPreparationServiceImpl.loadFont("/fonts/DejaVuSans.ttf");

				for (MenuReportResponseDto menu : menuReportResponseDtos) {

					catItemTable = new Table(UnitValue.createPercentArray(new float[] { 100f }));
					catItemTable.setWidth(UnitValue.createPercentValue(100));
					catItemTable.setBorder(Border.NO_BORDER);
					catItemTable.setMarginTop(8f);
					catItemTable.setKeepTogether(true);

					Paragraph p = new Paragraph().add(new Text("\u274F ").setFont(symbolFont))
							.add(new Text(menu.getNameEnglish() != null ? menu.getNameEnglish().toUpperCase() : "")
									.setFont(basicFont))
							.setFontSize(16f).simulateBold();

					cell = new Cell().add(p).setBorder(Border.NO_BORDER).setPaddingLeft(10f);

					catItemTable.addCell(cell);

					Cell itemCell = new Cell().setBorder(Border.NO_BORDER);

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

						Paragraph p1 = new Paragraph().add(new Text("\u25A1 ").setFont(symbolFont))
								.add(new Text(item.getNameEnglish() != null ? item.getNameEnglish().toUpperCase() : "")
										.setFont(basicFont))
								.setFontSize(14f).setMarginLeft(30f);

						itemCell.add(p1);
					}

					catItemTable.addCell(itemCell);
					document.add(catItemTable);
				}
			}

			/* ================= PAGE NUMBERS ================= */
			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 22,
						TextAlignment.CENTER);

				canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"back office report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	public String getMenuPlanningSimpleReport13Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, int intValue, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails,
			Integer isFunctionNextPage, Integer isHalfPax) {

		try {
			// -----------------------------------------------------------
			// 1. LANGUAGE LABELS (same branching as PDF method)
			// -----------------------------------------------------------
			String customerName = "", customerPhone = "", eDate = "", fNotes = "", eVenue = "", note = "",
					eventFlow = "", person = "", eTime = "", date = "", party = "", eventNotes = "", serviceLabel = "",
					themeLabel = "", billingNameLabel = "";

			String fontFamily;

			if (lang == 1) {
				// Hindi
				fontFamily = "Nirmala UI";
				customerName = "नाम";
				customerPhone = "नंबर";
				eDate = "दिनांक";
				fNotes = "नोट्स";
				eVenue = "स्थान";
				person = "मेम्बर्स";
				eTime = "समय";
				eventFlow = "कार्यक्रम का संचालन क्रम";
				note = "नोट";
				date = "दिनांक";
				party = "पार्टी का नाम";
				eventNotes = "कार्यक्रम की नोट";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				billingNameLabel = "बिलिंग नाम";
			} else if (lang == 2) {
				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {
					fontFamily = "Nirmala UI";
					customerName = "வாடிக்கையாளர் பெயர்";
					customerPhone = "மொபைல் எண்";
					eDate = "நிகழ்ச்சி தேதி";
					fNotes = "உணவு விவரம்";
					eVenue = "நிகழ்வு இடம்";
					person = "நபர்";
					eTime = "நேரம்";
					eventFlow = "நிகழ்ச்சி நடைபெறும் வரிசை";
					note = "குறிப்பு";
					date = "தேதி";
					party = "கட்சியின் பெயர்";
					eventNotes = "நிகழ்வு குறிப்புகள்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					billingNameLabel = "பில்லிங் பெயர்";
				} else if (language.equalsIgnoreCase("Telugu")) {
					fontFamily = "Nirmala UI";
					customerName = "కస్టమర్ పేరు";
					customerPhone = "మొబైల్ నంబర్";
					eDate = "కార్యక్రమ తేదీ";
					fNotes = "భోజన వివరాలు";
					eVenue = "కార్యక్రమ స్థలం";
					person = "వ్యక్తి";
					eTime = "సమయం";
					eventFlow = "కార్యక్రమ నిర్వహణ క్రమం";
					note = "గమనిక";
					date = "తేదీ";
					party = "పార్టీ పేరు";
					eventNotes = "ఈవెంట్ గమనికలు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					billingNameLabel = "బిల్లింగ్ పేరు";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					fontFamily = "Nirmala UI";
					customerName = "ഉപഭോക്താവിന്റെ പേര്";
					customerPhone = "മൊബൈൽ നമ്പർ";
					eDate = "പരിപാടിയുടെ തീയതി";
					fNotes = "ഭക്ഷണ വിശദാംശങ്ങൾ";
					eVenue = "പരിപാടി സ്ഥലം";
					person = "വ്യക്തി";
					eTime = "സമയം";
					eventFlow = "പരിപാടി നടത്തിപ്പ് ക്രമം";
					note = "കുറിപ്പ്";
					date = "തീയതി";
					party = "പാർട്ടി പേര്";
					eventNotes = "ഇവന്റ് കുറിപ്പുകൾ";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
				} else if (language.equalsIgnoreCase("Marathi")) {
					fontFamily = "Nirmala UI";
					customerName = "ग्राहकाचे नाव";
					customerPhone = "मोबाईल नंबर";
					eDate = "कार्यक्रमाची दिनांक";
					fNotes = "भोजन तपशील";
					eVenue = "आयोजन स्थळ";
					person = "व्यक्ती";
					eTime = "वेळ";
					eventFlow = "कार्यक्रमाचा संचालन क्रम";
					note = "टीप";
					date = "दिनांक";
					party = "पार्टीचे नाव";
					eventNotes = "कार्यक्रम नोंदी";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					billingNameLabel = "बिलिंग नाव";
				} else {
					fontFamily = "Nirmala UI";
					customerName = "ગ્રાહકનું નામ";
					customerPhone = "મોબાઇલ નંબર";
					eDate = "કાર્યક્રમની તારીખ";
					fNotes = "ભોજન વિગતો";
					eVenue = "આયોજન સ્થળ";
					person = "વ્યક્તિ";
					eTime = "સમય";
					eventFlow = "કાર્યક્રમનું સંચાલન ક્રમ";
					note = "નોંધ";
					date = "તારીખ";
					party = "પાર્ટીનું નામ";
					eventNotes = "કાર્યક્રમની નોંધ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					billingNameLabel = "બિલિંગ નામ";
				}
			} else {
				// English
				fontFamily = "Arial";
				customerName = "Party Name";
				customerPhone = "Contact No.";
				eDate = "Date";
				fNotes = "Food Note";
				eVenue = "Venue";
				person = "Persons";
				eTime = "Timing";
				eventFlow = "FLOW OF EVENT";
				note = "Note";
				date = "Date";
				party = "PARTY NAME";
				eventNotes = "Event Notes";
				serviceLabel = "Service";
				themeLabel = "Theme";
				billingNameLabel = "Billing Name";
			}

			// -----------------------------------------------------------
			// FETCH DATA (identical call to your PDF method)
			// -----------------------------------------------------------
			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);

			if (eventDto == null) {
				return "";
			}

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() == null ? "" : eventDto.getEventStartTimestamp();
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;
			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}
			String foodType = eventDto.getFoodType() == null ? "" : eventDto.getFoodType();
			String cmpName = eventDto.getCmpName() == null ? "" : eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone() != null ? eventDto.getCmpPhone() : "";
			String cmpAddress = eventDto.getCmpAddress() != null ? eventDto.getCmpAddress() : "";
			String cmpEmail = eventDto.getEmail() != null ? eventDto.getEmail() : "";
			String remarks = eventDto.getRemark() != null && !eventDto.getRemark().trim().isEmpty()
					? " (" + eventDto.getRemark() + ")"
					: "";

			String foodNotes, service, theme, billingName;
			if (lang == 1) {
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
			} else if (lang == 2) {
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
			} else {
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
			}

			// -----------------------------------------------------------
			// OUTPUT FILE PATH (same convention as PDF method)
			// -----------------------------------------------------------
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");
			if (!outputPath.exists()) {
				outputPath.mkdirs();
			}
			String reportName = getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "back office report");
			File docxFile = new File(outputPath, reportName + ".docx");

			// -----------------------------------------------------------
			// BUILD THE DOCUMENT
			// -----------------------------------------------------------

			XWPFDocument document = new XWPFDocument();

			CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
			CTPageMar pageMar = sectPr.addNewPgMar();
			pageMar.setTop(BigInteger.valueOf(400));
			pageMar.setBottom(BigInteger.valueOf(700));
			pageMar.setLeft(BigInteger.valueOf(500));
			pageMar.setRight(BigInteger.valueOf(500));

			// ---- 2. COMPANY HEADER BLOCK ----
			if (isCompanyDetails != null && isCompanyDetails == 1) {
				XWPFTable headerTable = document.createTable(1, 2);
				headerTable.setWidth("100%");
				CTTblPr tblPr = headerTable.getCTTbl().getTblPr();

				if (tblPr == null) {
					tblPr = headerTable.getCTTbl().addNewTblPr();
				}

				CTTblLayoutType layout = tblPr.getTblLayout();

				if (layout == null) {
					layout = tblPr.addNewTblLayout();
				}

				layout.setType(STTblLayoutType.FIXED);
				removeTableBorders(headerTable);

				XWPFTableCell logoCell = headerTable.getRow(0).getCell(0);
				logoCell.removeParagraph(0);
				XWPFParagraph logoPara = logoCell.addParagraph();
				logoPara.setAlignment(ParagraphAlignment.CENTER);
				try {
					String logoPath = environment.getProperty("app.image.url") + eventDto.getLogo();

					com.itextpdf.io.image.ImageData logoImageData = menuPreparationServiceImpl
							.loadImageFromResource(logoPath);
					byte[] logoBytes = (logoImageData != null) ? logoImageData.getData() : null;
					if (logoBytes == null) {
						logoBytes = readBytesFromResourcePath(logoPath);
					}
					if (logoBytes != null && logoBytes.length > 0) {
						int pictureType = guessPoiPictureType(logoPath);
						XWPFRun logoRun = logoPara.createRun();
						logoRun.addPicture(new ByteArrayInputStream(logoBytes), pictureType,
								"logo." + guessExtension(logoPath), Units.toEMU(80), Units.toEMU(80));
					}
				} catch (Exception imgEx) {
					System.out.println("Logo could not be loaded for docx: " + imgEx.getMessage());
				}

				XWPFTableCell infoCell = headerTable.getRow(0).getCell(1);
				if (!logoCell.getParagraphs().isEmpty()) {
					logoCell.removeParagraph(0);
				}

				addStyledParagraph(infoCell.addParagraph(), cmpName.toUpperCase(), "Nirmala UI", 12, false,
						ParagraphAlignment.LEFT);
				addStyledParagraph(infoCell.addParagraph(), cmpAddress, "Nirmala UI", 10, false,
						ParagraphAlignment.LEFT);

				XWPFParagraph phonePara = infoCell.addParagraph();
				addLabelValueRun(phonePara, "Mobile No : ", cmpPhone, "Nirmala UI", 10);

				XWPFParagraph emailPara = infoCell.addParagraph();
				addLabelValueRun(emailPara, "Email : ", cmpEmail, "Nirmala UI", 10);
			}

			addHorizontalLine(document);
			// ---- 3 & 4. PARTY DETAILS + EVENT DETAILS BLOCK ----
			XWPFTable detailsTable = document.createTable(1, 2);
			detailsTable.setWidth("100%");
			removeTableBorders(detailsTable);
			detailsTable.removeRow(0);
			XWPFTableRow detailsRow = detailsTable.getRow(0);

			if (detailsRow != null) {

				if (detailsRow.getCell(0) != null && !detailsRow.getCell(0).getParagraphs().isEmpty()) {
					detailsRow.getCell(0).removeParagraph(0);
				}

				if (detailsRow.getCell(1) != null && !detailsRow.getCell(1).getParagraphs().isEmpty()) {
					detailsRow.getCell(1).removeParagraph(0);
				}
			}

			addLabelValueRow(detailsTable, customerName, hostName, fontFamily);
			addLabelValueRow(detailsTable, customerPhone, mobileNo, fontFamily);

			addLabelValueRow(detailsTable, eVenue, venue.toUpperCase(), fontFamily);

			addLabelValueRow(detailsTable, eventNotes, foodType + remarks, fontFamily);

			addHorizontalLine(document);
			// ---- 5, 6, 7. PER-FUNCTION LOOP: function header, menu categories, items ----
			boolean isFirstFunction = true;
			List<EventFunctionReportResponseDto> functions = eventDto.getFunctions();

			for (EventFunctionReportResponseDto fn : functions) {

				if (!isFirstFunction && isFunctionNextPage != null && isFunctionNextPage == 1) {

					XWPFParagraph pageBreak = document.createParagraph();
					pageBreak.setPageBreak(true);
				}

				String functionVenue = "";
				String functionNote = "";
				if (lang == 1) {
					functionVenue = fn.getFunctionVenueHindi() != null ? fn.getFunctionVenueHindi() : "";
					functionNote = fn.getNotesHindi() != null ? fn.getNotesHindi() : "";
				} else if (lang == 2) {
					functionVenue = fn.getFunctionVenueGujarati() != null ? fn.getFunctionVenueGujarati() : "";
					functionNote = fn.getNotesGujarati() != null ? fn.getNotesGujarati() : "";
				} else {
					functionVenue = fn.getFunctionVenue() != null ? fn.getFunctionVenue() : "";
					functionNote = fn.getNotesEnglish() != null ? fn.getNotesEnglish() : "";
				}

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, fn.getFunctionId());
				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				}

				Integer fnPax = fn.getPax() != null
						? (isHalfPax != null && isHalfPax == 1 ? fn.getPax() / 2 : fn.getPax())
						: 0;

				String functionName = safeText(fn.getFunctionName()).toUpperCase();

				XWPFParagraph functionSpace = document.createParagraph();
				functionSpace.setSpacingAfter(30);

				String startDate = fn.getFunctionStartTimestamp() != null ? fn.getFunctionStartTimestamp().split(" ")[0]
						: "";

				String eventStartTime = fn.getFunctionStartTimestamp() != null
						? fn.getFunctionStartTimestamp().split(" ")[1].substring(0, 5) + " "
								+ fn.getFunctionStartTimestamp().split(" ")[2]
						: "";

				String eventEndTime = fn.getFunctionEndTimestamp() != null
						? fn.getFunctionEndTimestamp().split(" ")[1].substring(0, 5) + " "
								+ fn.getFunctionEndTimestamp().split(" ")[2]
						: "";

				XWPFTable functionTable = document.createTable(3, 2);
				functionTable.setWidth("100%");
				removeTableBorders(functionTable);

				// set borders
				CTTblPr tblPr = functionTable.getCTTbl().getTblPr();
				if (tblPr == null) {
					tblPr = functionTable.getCTTbl().addNewTblPr();
				}

				// ADD THIS HERE
				CTTblLayoutType tblLayout = tblPr.isSetTblLayout() ? tblPr.getTblLayout() : tblPr.addNewTblLayout();
				tblLayout.setType(STTblLayoutType.FIXED);

				setCellBorder(functionTable.getRow(0).getCell(1));
				setCellBorder(functionTable.getRow(1).getCell(1));

				setCellWidth(functionTable.getRow(0).getCell(0), 3500);
				setCellWidth(functionTable.getRow(0).getCell(1), 1500);
				setCellWidth(functionTable.getRow(1).getCell(0), 3500);
				setCellWidth(functionTable.getRow(1).getCell(1), 1500);
				setCellWidth(functionTable.getRow(2).getCell(0), 3500);
				setCellWidth(functionTable.getRow(2).getCell(1), 1500);
				/* 1st row */
				XWPFTableRow row0 = functionTable.getRow(0);

				XWPFParagraph para0_0 = row0.getCell(0).getParagraphs().get(0);
				para0_0.setAlignment(ParagraphAlignment.CENTER);

				XWPFRun run0_0 = para0_0.createRun();
				setRunStyle(run0_0, fontFamily, 14, true);
				run0_0.setText(functionName);

				XWPFParagraph para0_1 = row0.getCell(1).getParagraphs().get(0);
				para0_1.setAlignment(ParagraphAlignment.LEFT);

				XWPFRun run0_1 = para0_1.createRun();
				setRunStyle(run0_1, fontFamily, 14, true);
				run0_1.setText(person + " : " + fnPax);

				/* 2nd row */
				XWPFTableRow row1 = functionTable.getRow(1);

				XWPFParagraph para1_0 = row1.getCell(0).getParagraphs().get(0);
				para1_0.setAlignment(ParagraphAlignment.LEFT);

				XWPFRun run1_0 = para1_0.createRun();
				setRunStyle(run1_0, fontFamily, 14, true);
				run1_0.setText(date + " : " + startDate.toUpperCase());

				XWPFParagraph para1_1 = row1.getCell(1).getParagraphs().get(0);
				para1_1.setAlignment(ParagraphAlignment.LEFT);

				XWPFRun run1_1 = para1_1.createRun();
				setRunStyle(run1_1, fontFamily, 14, true);
				run1_1.setText(eTime + " : " + eventStartTime.toUpperCase());

				/* 3rd row */
				XWPFTableRow row2 = functionTable.getRow(2);

				XWPFParagraph para2_0 = row2.getCell(0).getParagraphs().get(0);
				para2_0.setAlignment(ParagraphAlignment.LEFT);

				XWPFRun run2_0 = para2_0.createRun();
				setRunStyle(run2_0, fontFamily, 14, true);
				run2_0.setText(eVenue + " : " + functionVenue);

				row2.getCell(1).removeParagraph(0);
				row2.getCell(1).addParagraph();
				/*
				 * MENU CATEGORIES
				 */
				List<MenuReportResponseDto> categories = fn.getMenuCategories();

				for (MenuReportResponseDto menu : categories) {

					XWPFParagraph catPara = document.createParagraph();

					catPara.setSpacingBefore(160);

					XWPFRun catBullet = catPara.createRun();

					setRunStyle(catBullet, "DejaVu Sans", 16, false);

					catBullet.setText("\u274F ");

					XWPFRun catText = catPara.createRun();

					setRunStyle(catText, fontFamily, 16, true);

					catText.setText(menu.getNameEnglish() != null ? menu.getNameEnglish().toUpperCase() : "");
					catText.setUnderline(UnderlinePatterns.SINGLE);
					if (isCategoryInstruction != null && isCategoryInstruction == 1 && menu.getMenuNotes() != null
							&& !menu.getMenuNotes().isEmpty()) {

						XWPFRun catNoteRun = catPara.createRun();

						setRunStyle(catNoteRun, fontFamily, 12, false);

						catNoteRun.setText(
								" ( " + menuPreparationServiceImpl.formatText(menu.getMenuNotes(), lang) + " )");
					}

					/*
					 * MENU ITEMS
					 */
					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

						XWPFParagraph itemPara = document.createParagraph();

						itemPara.setIndentationLeft(400);

						XWPFRun itemBullet = itemPara.createRun();

						setRunStyle(itemBullet, "DejaVu Sans", 14, false);

						itemBullet.setText("\u25A1 ");

						XWPFRun itemText = itemPara.createRun();

						setRunStyle(itemText, fontFamily, 14, false);

						itemText.setText(item.getNameEnglish() != null ? item.getNameEnglish().toUpperCase() : "");

						if (isItemInstruction != null && isItemInstruction == 1 && item.getItemNotes() != null
								&& !item.getItemNotes().isEmpty()) {

							XWPFRun itemNoteRun = itemPara.createRun();

							setRunStyle(itemNoteRun, fontFamily, 12, false);

							itemNoteRun.setText(
									" ( " + menuPreparationServiceImpl.formatText(item.getItemNotes(), lang) + " ) ");
						}
					}
				}

				isFirstFunction = false;
			}

			// ---- 8. PAGE NUMBERS (footer, auto-updating PAGE/NUMPAGES fields) ----
			addPageNumberFooter(document, fontFamily);

			// -----------------------------------------------------------
			// WRITE FILE
			// -----------------------------------------------------------
			try (FileOutputStream out = new FileOutputStream(docxFile)) {
				document.write(out);
			}
			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/docx/" + eventDto.getEventNo()
					+ "/" + reportName + ".docx";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report (DOCX)", e);
		}
	}

	private void setCellBorder(XWPFTableCell cell) {

		CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();

		CTTcBorders borders = tcPr.isSetTcBorders() ? tcPr.getTcBorders() : tcPr.addNewTcBorders();

		CTBorder border;

		border = borders.isSetTop() ? borders.getTop() : borders.addNewTop();
		border.setVal(STBorder.SINGLE);
		border.setSz(BigInteger.valueOf(8));

		border = borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom();
		border.setVal(STBorder.SINGLE);
		border.setSz(BigInteger.valueOf(8));

		border = borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft();
		border.setVal(STBorder.SINGLE);
		border.setSz(BigInteger.valueOf(8));

		border = borders.isSetRight() ? borders.getRight() : borders.addNewRight();
		border.setVal(STBorder.SINGLE);
		border.setSz(BigInteger.valueOf(8));
	}

	private void setCellWidth(XWPFTableCell cell, int widthPct) {
		CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();

		CTTblWidth tcW = tcPr.isSetTcW() ? tcPr.getTcW() : tcPr.addNewTcW();

		tcW.setType(STTblWidth.PCT);
		tcW.setW(BigInteger.valueOf(widthPct));
	}

	@Override
	public String orderSummaryReport2(String startDate, String endDate, List<Integer> eventStatus,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, List<Long> managerIds,
			Long partyId) {

		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();
			String eventDateLabel, eventNameLabel, functionNameLabel, paxLabel, functionVenueLabel, partyNameLabel,
					managerNameLabel, statusLabel, eventNoLabel, mobileLabel, sessionLabel, inquiryDateLabel,
					createdByLabel, LastChangedByLabel;

			eventDateLabel = "EVENT DATE";
			eventNameLabel = "OCCASION";
			functionNameLabel = "FUNCTION NAME";
			paxLabel = "GUESTS";
			functionVenueLabel = "VENUE";
			partyNameLabel = "CUSTOMER NAME";
			managerNameLabel = "MANAGER NAME";
			statusLabel = "STATUS";
			eventNoLabel = "ORD NO.";
			mobileLabel = "MOBILE NO";
			sessionLabel = "SESSION \n  DATE / TIME ";
			inquiryDateLabel = "INQUIRY DATE";
			createdByLabel = "CREATED BY";
			LastChangedByLabel = "LAST CHANGED BY";
			int headFontSize = 13;
			int fontSize = 10;
			float lineHeight = 1.1f;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				eventDateLabel = "कार्यक्रम की तिथि";
				eventNameLabel = "कार्यक्रम का प्रकार";
				functionNameLabel = "समारोह का नाम";
				paxLabel = "अतिथियों की संख्या";
				functionVenueLabel = "समारोह स्थल";
				partyNameLabel = "ग्राहक का नाम";
				managerNameLabel = "प्रबंधक का नाम";
				statusLabel = "स्थिति";
				eventNoLabel = "ऑर्डर आईडी";
				mobileLabel = "मोबाइल नंबर";
				sessionLabel = "सत्र \n ( तिथि / समय )";
				inquiryDateLabel = "पूछताछ तिथि";
				createdByLabel = "निर्माता";
				LastChangedByLabel = "अंतिम संशोधनकर्ता";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");

				eventDateLabel = "કાર્યક્રમ તારીખ";
				eventNameLabel = "કાર્યક્રમનો પ્રકાર"; // Type Of Event
				functionNameLabel = "સમારોહનું નામ";
				paxLabel = "મહેમાનોની સંખ્યા"; // Better than "સંખ્યા"
				functionVenueLabel = "સમારોહ સ્થળ";
				partyNameLabel = "ગ્રાહકનું નામ";
				managerNameLabel = "મેનેજરનું નામ";
				statusLabel = "સ્થિતિ";
				eventNoLabel = "ઓર્ડર આઈડી"; // Since English is "Order Id"
				mobileLabel = "મોબાઇલ નંબર";
				sessionLabel = "સેશન \n ( તારીખ / સમય )";
				inquiryDateLabel = "પૂછપરછ તારીખ"; // Better Gujarati than "ઇન્ક્વૈરી"
				createdByLabel = "બનાવનાર";
				LastChangedByLabel = "છેલ્લે સુધારો કરનાર";

				fontSize = 11;
				headFontSize = 14;
				lineHeight = 0.9f;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/orderSummaryReport");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			File pdfFile = new File(outputPath + "/" + "FROM " + formatDate(startDate) + " TO " + formatDate(endDate)
					+ "_order_summary_report" + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, PageSize.A3.rotate());
			document.setMargins(40, 35, 50, 20);

			float[] columnWidthHead = { 20f, 18f, 2f, 57f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));

			MenuQuantityReponseDto cmpData = menuItemRawMaterialServiceImpl.getCmpData(userid);

			String cmpName = cmpData != null ? commonService.getString(cmpData.getCompanyName()) : "";
			String cmpCountryCode = cmpData != null ? commonService.getString(cmpData.getCountryCode()) : "";
			String cmpMobile = cmpData != null ? commonService.getString(cmpData.getOfficeNo()) : "";
			String cmpEmail = cmpData != null ? commonService.getString(cmpData.getCompanyEmail()) : "";
			String logo = cmpData != null ? commonService.getString(cmpData.getLogo()) : "";
			String companyAddress = cmpData != null ? commonService.getString(cmpData.getCompanyAddress()) : "";

//			ImageData imgData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
//			Image img = new Image(imgData);
			Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));
			img.setWidth(100);
			img.setHorizontalAlignment(HorizontalAlignment.LEFT);

			Cell cmp = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			Paragraph cmpPara = new Paragraph().add(
					new Text(cmpName).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18))
					.setMultipliedLeading(1f);
			cmp = new Cell(1, 3).add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(new Text("MOBILE NO.")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpCountryCode + " " + cmpMobile)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text("EMAIL").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpEmail).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER).setMarginBottom(3f);
			cmpHead.addCell(cmp);

			if (isCompanyDetails == 1) {
				cmpHead.setMarginBottom(7f);
				document.add(cmpHead);
			}

			LocalDate firstDate = commonService.dateFormatted(startDate);
			LocalDate lastDate = commonService.dateFormatted(endDate);

			Paragraph para = new Paragraph().add(new Text("DATEWISE ORDER SUMMARY"))
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(16)
					.setTextAlignment(TextAlignment.CENTER);

			document.add(para);

			float[] cw2 = { 50, 50 };
			Table dTable = new Table(UnitValue.createPercentArray(cw2));
			dTable.setWidth(UnitValue.createPercentValue(100));
			dTable.setMarginBottom(7f);

			para = new Paragraph().add(new Text("From Date: " + startDate))
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(13)
					.setTextAlignment(TextAlignment.CENTER);
			Cell cell = new Cell().add(para).setBorder(Border.NO_BORDER);
			dTable.addCell(cell);

			para = new Paragraph().add(new Text("To Date: " + endDate))
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(13)
					.setTextAlignment(TextAlignment.CENTER);
			cell = new Cell().add(para).setBorder(Border.NO_BORDER);
			dTable.addCell(cell);

			document.add(dTable);

			List<OrderSummaryResponseDto> summaryResponseDtos = menuPreparationServiceImpl
					.getDatewiseOrderSummaryReport(firstDate, lastDate, userid, lang, eventStatus, managerIds, partyId,
							"type2");

			// --- Header Table (separate, so it doesn't get pulled with data) ---
			float[] cw = { 7, // Order Id
					13, // Customer Name
					9, // Mobile No
					12, // Type Of Event
					8, // Session
					13, // Venue
					6, // Guest Pax
					8, // Status
					10, // Inquiry Date
					7, // Created By
					7 // Last Changed By
			};
			Table headerTable = new Table(UnitValue.createPercentArray(cw));
			headerTable.setWidth(UnitValue.createPercentValue(100));

			String[] headers = { eventNoLabel, partyNameLabel, mobileLabel, eventNameLabel, sessionLabel,
					functionVenueLabel, paxLabel, statusLabel, inquiryDateLabel, createdByLabel, LastChangedByLabel };
			for (String header : headers) {
				para = new Paragraph().add(new Text(header)).setFont(boldFont).setFontSize(headFontSize)
						.setTextAlignment(TextAlignment.CENTER);
				cell = new Cell().add(para);
				headerTable.addCell(cell);
			}
			document.add(headerTable);

			// --- Group DTOs ---
			Map<String, List<OrderSummaryResponseDto>> groupedMap = new LinkedHashMap<>();
			for (OrderSummaryResponseDto dto : summaryResponseDtos) {
				String key = dto.getEventDate() + "||" + dto.getEventName() + "||" + dto.getPartyName() + "||"
						+ dto.getManagerName() + "||" + dto.getStatus();
				groupedMap.computeIfAbsent(key, k -> new ArrayList<>()).add(dto);
			}
			// --- One mini-table per group, kept together on same page ---
			for (Map.Entry<String, List<OrderSummaryResponseDto>> entry : groupedMap.entrySet()) {
				List<OrderSummaryResponseDto> group = entry.getValue();

				String eventNo = group.get(0).getEventNo() == null ? "" : group.get(0).getEventNo();
				String partyName = group.get(0).getPartyName() == null ? "" : group.get(0).getPartyName();
				String mobile = group.get(0).getMobileNo() == null ? "" : group.get(0).getMobileNo();
				String eventName = group.get(0).getEventName() == null ? "" : group.get(0).getEventName();
				String status = group.get(0).getStatus() == null ? "" : group.get(0).getStatus();
				String inquiryDate = group.get(0).getInquiryDate() == null ? "" : group.get(0).getInquiryDate();
				String createdBy = group.get(0).getManagerName() == null ? "" : group.get(0).getManagerName();
				String lastChangedBy = group.get(0).getLastUpdatedBy() == null ? "" : group.get(0).getLastUpdatedBy();
				String eventDate = group.get(0).getEventDate() != null ? group.get(0).getEventDate() : "";
				// Create a mini-table for this group
				Table groupTable = new Table(UnitValue.createPercentArray(cw));
				groupTable.setWidth(UnitValue.createPercentValue(100));
				groupTable.setKeepTogether(true); // <-- KEY: never split this group across pages
				groupTable.setFixedLayout();

				for (int i = 0; i < group.size(); i++) {
					OrderSummaryResponseDto dto = group.get(i);

					String functionName = dto.getFunctionName() != null ? dto.getFunctionName() : "";
					String functionPax = dto.getFunctionPax() != null ? dto.getFunctionPax() : "";
					String functionVenue = dto.getFunctionVenue() != null ? dto.getFunctionVenue() : "";
					String session = dto.getSession() == null ? "" : dto.getSession();

					// Order Id
					if (i == 0) {
						para = new Paragraph(new Text(upper(eventNo))).setFont(basicFont).setFontSize(fontSize)
								.setMultipliedLeading(lineHeight);
						cell = new Cell(group.size(), 1).add(para).setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setTextAlignment(TextAlignment.CENTER);
						groupTable.addCell(cell);
					}

					// Customer Name
					if (i == 0) {
						para = new Paragraph(new Text(upper(partyName))).setFont(basicFont).setFontSize(fontSize)
								.setMultipliedLeading(lineHeight);
						cell = new Cell(group.size(), 1).add(para).setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setTextAlignment(TextAlignment.CENTER);
						groupTable.addCell(cell);
					}

					// Mobile
					if (i == 0) {
						para = new Paragraph(new Text(upper(mobile))).setFont(basicFont).setFontSize(fontSize)
								.setMultipliedLeading(lineHeight);
						cell = new Cell(group.size(), 1).add(para).setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setTextAlignment(TextAlignment.CENTER);
						groupTable.addCell(cell);
					}

					// Type Of Event
					if (i == 0) {
						para = new Paragraph(new Text(upper(eventName))).setFont(basicFont).setFontSize(fontSize)
								.setMultipliedLeading(lineHeight);
						cell = new Cell(group.size(), 1).add(para).setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setTextAlignment(TextAlignment.CENTER);
						groupTable.addCell(cell);
					}

					// Function Name
//					para = new Paragraph(new Text(functionName)).setFont(basicFont).setFontSize(fontSize)
//							.setMultipliedLeading(lineHeight);
//					groupTable.addCell(new Cell().add(para));

					// Session
					para = new Paragraph(new Text(upper(functionName) + " \n " + upper(session))).setFont(basicFont)
							.setFontSize(fontSize).setMultipliedLeading(lineHeight);
					groupTable.addCell(new Cell().add(para).setTextAlignment(TextAlignment.CENTER));

					// Venue
					para = new Paragraph(new Text(upper(functionVenue))).setFont(basicFont).setFontSize(fontSize)
							.setMultipliedLeading(lineHeight);
					groupTable.addCell(new Cell().add(para).setTextAlignment(TextAlignment.CENTER));

					// Guest Pax
					para = new Paragraph(new Text(upper(functionPax))).setFont(basicFont).setFontSize(fontSize)
							.setMultipliedLeading(lineHeight);
					groupTable.addCell(new Cell().add(para).setTextAlignment(TextAlignment.CENTER));

					// Status
					if (i == 0) {
						para = new Paragraph(new Text(upper(status))).setFont(basicFont).setFontSize(fontSize)
								.setMultipliedLeading(lineHeight);
						cell = new Cell(group.size(), 1).add(para).setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setTextAlignment(TextAlignment.CENTER);
						groupTable.addCell(cell);
					}

					// Inquiry Date
					if (i == 0) {
						para = new Paragraph(new Text(upper(inquiryDate))).setFont(basicFont).setFontSize(fontSize)
								.setMultipliedLeading(lineHeight);
						cell = new Cell(group.size(), 1).add(para).setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setTextAlignment(TextAlignment.CENTER);
						groupTable.addCell(cell);
					}

					// Created By
					if (i == 0) {
						para = new Paragraph(new Text(upper(createdBy))).setFont(basicFont).setFontSize(fontSize)
								.setMultipliedLeading(lineHeight);
						cell = new Cell(group.size(), 1).add(para).setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setTextAlignment(TextAlignment.CENTER);
						groupTable.addCell(cell);
					}

					// Last Changed By
					if (i == 0) {
						para = new Paragraph(new Text(upper(lastChangedBy))).setFont(basicFont).setFontSize(fontSize)
								.setMultipliedLeading(lineHeight);
						cell = new Cell(group.size(), 1).add(para).setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setTextAlignment(TextAlignment.CENTER);
						groupTable.addCell(cell);
					}

				}

				document.add(groupTable);
			}

			document.close();

			String scheme = re.getScheme();
			String serverName = re.getServerName();
			int serverPort = re.getServerPort();
			String contextPath = re.getContextPath();

			String fullUrl;
			if ((scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443)) {
				fullUrl = "https://" + serverName + contextPath + "/api/download/pdf/orderSummaryReport" + "/" + "FROM "
						+ formatDate(startDate) + " TO " + formatDate(endDate) + "_order_summary_report" + ".pdf";
			} else {
				fullUrl = "https://" + serverName + ":" + serverPort + contextPath
						+ "/api/download/pdf/orderSummaryReport" + "/" + "FROM " + formatDate(startDate) + " TO "
						+ formatDate(endDate) + "_order_summary_report" + ".pdf";
			}

			fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/orderSummaryReport" + "/" + "FROM "
					+ formatDate(startDate) + " TO " + formatDate(endDate) + "_order_summary_report" + ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report generation failed.");
		}
	}

	private String upper(String value) {
		return value == null ? "" : value.toUpperCase();
	}

	@Override
	public String getMenuPlanningSimpleReport14(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, int isCatImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails,
			AdminTemplateModuleResponseDto adminTemplate, Integer isSignatureVisible, Integer isAddDecoration,
			Integer isTermsCond, Integer isNotes) {
		try {
			PdfFont basicFont = null;
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "", l1 = "",
					l2 = "", note = "", eContact = "", eventFlow = "", function = "", person = "", eTime = "",
					date = "", rate = "", party = "", eventNotes = "", billingNameLabel = "", serviceLabel = "",
					themeLabel = "", authorizePersonLabel = "", emailLabel = "", addressLabel = "";
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;
			if (lang == 1) {
				// Hindi
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				customerName = "नाम";
				customerPhone = "मोबाइल नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "दिनांक";
				fNotes = "नोट्स";
				eVenue = "आयोजन स्थान";
				function = "कार्यक्रम";
				person = "मेम्बर्स";
				eTime = "समय";
				eContact = "संपर्क नंबर";
				l1 = "व्यक्तियों की संख्या:";
				eventFlow = "कार्यक्रम का संचालन क्रम";
				note = "नोट";
				date = "दिनांक";
				rate = "रेट";
				party = "पार्टी का नाम";
				eventNotes = "नोट्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				authorizePersonLabel = "अधिकृत व्यक्ति";
				emailLabel = "ईमेल";
				addressLabel = "निवास स्थान";
				y = 512;
			} else if (lang == 2) {
				// Gujarati / regional variants
				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Regular.ttf");
					customerName = "வாடிக்கையாளர் பெயர்";
					customerPhone = "மொபைல் எண்";
					eName = "நிகழ்ச்சி பெயர்";
					eDate = "நிகழ்ச்சி தேதி";
					fNotes = "உணவு விவரம்";
					eVenue = "நிகழ்வு இடம்";
					function = "நிகழ்ச்சி";
					person = "நபர்";
					eTime = "நேரம்";
					eContact = "தொடர்பு எண்";
					l1 = "நபர்களின் எண்ணிக்கை:";
					eventFlow = "நிகழ்ச்சி நடைபெறும் வரிசை";
					note = "குறிப்பு";
					date = "தேதி";
					rate = "விலை";
					party = "கட்சியின் பெயர்";
					eventNotes = "நிகழ்வு குறிப்புகள்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					authorizePersonLabel = "அங்கீகரிக்கப்பட்ட நபர்";
					emailLabel = "மின்னஞ்சல்";
					addressLabel = "வசிப்பிடம்";
				} else if (language.equalsIgnoreCase("Telugu")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					customerName = "కస్టమర్ పేరు";
					customerPhone = "మొబైల్ నంబర్";
					eName = "కార్యక్రమ పేరు";
					eDate = "కార్యక్రమ తేదీ";
					fNotes = "భోజన వివరాలు";
					eVenue = "కార్యక్రమ స్థలం";
					function = "కార్యక్రమం";
					person = "వ్యక్తి";
					eTime = "సమయం";
					eContact = "సంప్రదింపు నంబర్";
					l1 = "వ్యక్తుల సంఖ్య:";
					eventFlow = "కార్యక్రమ నిర్వహణ క్రమం";
					note = "గమనిక";
					date = "తేదీ";
					rate = "రేటు";
					party = "పార్టీ పేరు";
					eventNotes = "ఈవెంట్ గమనికలు";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					authorizePersonLabel = "అధీకృత వ్యక్తి";
					emailLabel = "ఇమెయిల్";
					addressLabel = "నివాస స్థలం";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					customerName = "ഉപഭോക്താവിന്റെ പേര്";
					customerPhone = "മൊബൈൽ നമ്പർ";
					eName = "പരിപാടിയുടെ പേര്";
					eDate = "പരിപാടിയുടെ തീയതി";
					fNotes = "ഭക്ഷണ വിശദാംശങ്ങൾ";
					eVenue = "പരിപാടി സ്ഥലം";
					function = "പരിപാടി";
					person = "വ്യക്തി";
					eTime = "സമയം";
					eContact = "ബന്ധപ്പെടാനുള്ള നമ്പർ";
					l1 = "വ്യക്തികളുടെ എണ്ണം:";
					eventFlow = "പരിപാടി നടത്തിപ്പ് ക്രമം";
					note = "കുറിപ്പ്";
					date = "തീയതി";
					rate = "നിരക്ക്";
					party = "പാർട്ടി പേര്";
					eventNotes = "ഇവന്റ് കുറിപ്പുകൾ";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					emailLabel = "ഇമെയിൽ";
					authorizePersonLabel = "അംഗീകൃത വ്യക്തി";
					addressLabel = "താമസസ്ഥലം";
				} else if (language.equalsIgnoreCase("Marathi")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
					customerName = "ग्राहकाचे नाव";
					customerPhone = "मोबाईल नंबर";
					eName = "कार्यक्रमाचे नाव";
					eDate = "कार्यक्रमाची दिनांक";
					fNotes = "भोजन तपशील";
					eVenue = "आयोजन स्थळ";
					function = "कार्यक्रम";
					person = "व्यक्ती";
					eTime = "वेळ";
					eContact = "संपर्क नंबर";
					l1 = "व्यक्तींची संख्या:";
					eventFlow = "कार्यक्रमाचा संचालन क्रम";
					note = "टीप";
					date = "दिनांक";
					rate = "रेट";
					party = "पार्टीचे नाव";
					eventNotes = "कार्यक्रम नोंदी";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					emailLabel = "ईमेल";
					authorizePersonLabel = "अधिकृत व्यक्ती";
					addressLabel = "निवासस्थान";
				} else {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					customerName = "ગ્રાહકનું નામ";
					customerPhone = "મોબાઇલ નંબર";
					eName = "કાર્યક્રમનું નામ";
					eDate = "કાર્યક્રમની તારીખ";
					fNotes = "ભોજન વિગતો";
					eVenue = "આયોજન સ્થળ";
					function = "કાર્યક્રમ";
					person = "વ્યક્તિ";
					eTime = "સમય";
					eContact = "સંપર્ક નંબર";
					eventFlow = "કાર્યક્રમનું સંચાલન ક્રમ";
					l1 = "વ્યક્તિઓની સંખ્યા:";
					note = "નોંધ";
					date = "તારીખ";
					rate = "રેટ";
					party = "પાર્ટીનું નામ";
					eventNotes = "કાર્યક્રમની નોંધ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					emailLabel = "ઈમેલ";
					authorizePersonLabel = "અધિકૃત વ્યક્તિ";
					addressLabel = "રહેઠાણનું સ્થળ";
				}
				y = 510;
			} else {
				// English
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				customerName = "Customer Name";
				customerPhone = "Mobile No.";
				eName = "Event Name";
				eDate = "Event Date";
				fNotes = "Food Note";
				eVenue = "Venue";
				function = "Function Name";
				person = "Persons";
				eTime = "Event Time";
				eContact = "CONTACT NO";
				eventFlow = "FLOW OF EVENT";
				l1 = "OF PERSONS:";
				note = "Note";
				date = "Date";
				rate = "Rate";
				party = "PARTY NAME";
				eventNotes = "Event Notes";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";
				authorizePersonLabel = "Authorized Person";
				emailLabel = "Email";
				addressLabel = "Address";
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);

			if (eventDto == null) {
				return "Data not found.";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

			if (!outputPath.exists()) {
				outputPath.mkdirs();
			}
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "back office report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 30, 50, 30);

			ImageData mainBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
			ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			Color redColor = new DeviceRgb(255, 0, 0);
			Color blackColor = new DeviceRgb(0, 0, 0);
			Color labelColor = new DeviceRgb(147, 91, 57);

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "-";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventTime = eventDto.getEventTime() != null ? eventDto.getEventTime() : "";
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;
			String venue;
			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}
			String foodNotesName = eventDto.getFoodType();
			String foodNotes;
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone() != null ? eventDto.getCmpPhone() : "";
			String cmpAddress = eventDto.getCmpAddress() != null ? eventDto.getCmpAddress() : "";
			String cmpEmail = eventDto.getEmail() != null ? eventDto.getEmail() : "";
			String remarks = eventDto.getRemark() != null ? eventDto.getRemark() : "";
			String manager = eventDto.getEventManagerName() != null ? eventDto.getEventManagerName() : "";
			String email = eventDto.getEmail() != null ? eventDto.getEmail() : "";
			String partyAddress = eventDto.getPartyAddress() != null ? eventDto.getPartyAddress() : "";
			
			boolean isFirstFunction = true;
			Table eventTable;
			Table catItemTable;

			String billingName, service, theme;
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
			}

			// ---- border constants used consistently everywhere below ----
			final Border NONE = Border.NO_BORDER;
			final Border BLOCK_TOP = new DoubleBorder(3f); // top border of the whole party-details block
			final Border ROW_TOP = new SolidBorder(1f); // divider border between sections
			
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				if (!isFirstFunction) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, eventFunctionMasterResponseDto.getFunctionId());

				String functionVenue;
				String functionNotes;
				if (lang == 1) {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenueHindi() != null
							? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: "";
					functionNotes = eventFunctionMasterResponseDto.getNotesHindi() != null
							? eventFunctionMasterResponseDto.getNotesHindi()
							: "";
				} else if (lang == 2) {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenueGujarati() != null
							? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
							: "";
					functionNotes = eventFunctionMasterResponseDto.getNotesGujarati() != null
							? eventFunctionMasterResponseDto.getNotesGujarati()
							: "";
				} else {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenue() != null
							? eventFunctionMasterResponseDto.getFunctionVenue()
							: "";
					functionNotes = eventFunctionMasterResponseDto.getNotesEnglish() != null
							? eventFunctionMasterResponseDto.getNotesEnglish()
							: "";
				}

				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				}
				eventTable = new Table(UnitValue.createPercentArray(new float[] { 25f, 1.5f, 39f, 25f, 1.5f, 39f }));
				eventTable.setWidth(UnitValue.createPercentValue(100));
				eventTable.setMarginBottom(-2f);
				eventTable.setBorder(Border.NO_BORDER);
				eventTable.setMarginTop(10f);

				if (isFirstFunction && isCompanyDetails == 1) {
					ImageData logoData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + eventDto.getLogo());
					Image logo = new Image(logoData);
					logo.setWidth(UnitValue.createPercentValue(100f));
					logo.setAutoScale(false);
					logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

					eventTable.addCell(new Cell(6, 1).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setBorder(NONE).setPadding(3f));

					eventTable.addCell(new Cell(1, 5)
							.add(new Paragraph(cmpName.toUpperCase()).setFont(basicFont).setFontSize(12)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(NONE));

					eventTable.addCell(new Cell(1, 5)
							.add(new Paragraph(cmpAddress).setFont(basicFont).setFontSize(10).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(NONE));

					eventTable.addCell(new Cell(1, 5)
							.add(new Paragraph().add(new Text("Mobile No : ").simulateBold()).add(new Text(cmpPhone))
									.setFont(basicFont).setFontSize(10).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT))
							.setPaddingBottom(0).setPaddingTop(0).setBorder(NONE).setPaddingLeft(10f));

					eventTable.addCell(new Cell(1, 5)
							.add(new Paragraph().add(new Text("Email : ").simulateBold()).add(new Text(cmpEmail))
									.setFont(basicFont).setFontSize(10).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT))
							.setPaddingBottom(0).setPaddingTop(0).setBorder(NONE).setPaddingLeft(10f));

					eventTable.addCell(new Cell(1, 5).setBorder(NONE).setHeight(5f));
					eventTable.addCell(new Cell(1, 5).setBorder(NONE).setHeight(5f));
				}

				createHeaderCell(eventTable, eDate, 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, true, true, new DoubleBorder(blackColor, 3f), null, null,
						null);
				createHeaderCell(eventTable, ":", 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, true, new DoubleBorder(blackColor, 3f), null, null,
						null);
				createHeaderCell(eventTable, eventDate, 11f, blackColor, null, 4, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, true, new DoubleBorder(blackColor, 3f), null, null,
						null);

				createHeaderCell(eventTable, eName, 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, true, true, null, null, null, null);
				createHeaderCell(eventTable, ":", 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, true, null, null, null, null);
				createHeaderCell(eventTable, eventName.toUpperCase(), 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, true, null, null, null, null);

				createHeaderCell(eventTable, eVenue, 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, true, false, null, null, null, null);
				createHeaderCell(eventTable, ":", 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, false, null, null, null, null);
				createHeaderCell(eventTable, venue.toUpperCase(), 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, false, null, null, null, null);

				String eventStartTime = eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp().split(" ")[1].substring(0, 5) + " "
								+ eventFunctionMasterResponseDto.getFunctionStartTimestamp().split(" ")[2]
						: "";
				String eventEndTime = eventFunctionMasterResponseDto.getFunctionEndTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionEndTimestamp().split(" ")[1].substring(0, 5) + " "
								+ eventFunctionMasterResponseDto.getFunctionEndTimestamp().split(" ")[2]
						: "";
				String dateTimeValue = safeText(eventStartTime).toUpperCase() + " To "
						+ safeText(eventEndTime).toUpperCase();

				createHeaderCell(eventTable, eTime, 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, true, false, null, null, null, null);
				createHeaderCell(eventTable, ":", 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, false, null, null, null, null);
				createHeaderCell(eventTable, dateTimeValue, 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, false, null, null, null, null);

				createHeaderCell(eventTable, authorizePersonLabel, 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, true, false, null, null, null, null);
				createHeaderCell(eventTable, ":", 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, false, null, null, null, null);
				createHeaderCell(eventTable, manager, 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, false, null, null, null, null);

				String functionName = safeText(eventFunctionMasterResponseDto.getFunctionName().toUpperCase());

				createHeaderCell(eventTable, function, 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, true, false, null, null, null, null);
				createHeaderCell(eventTable, ":", 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, false, null, null, null, null);
				createHeaderCell(eventTable, functionName, 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, false, null, null, null, null);

//				addPair(eventTable, basicFont, blackColor, function, functionName, eVenue, safeText(functionVenue),
//						ROW_TOP);

				createHeaderCell(eventTable, person, 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, true, false, null, null, null, null);
				createHeaderCell(eventTable, ":", 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, false, null, null, null, null);
				createHeaderCell(eventTable, eventFunctionMasterResponseDto.getPax().toString(), 11f, blackColor, null,
						1, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, basicFont, false, false, null, null, null,
						null);

//				addPair(eventTable, basicFont, blackColor, person,
//						safeText(eventFunctionMasterResponseDto.getPax().toString()), date, null, NONE);

				createHeaderCell(eventTable, customerName, 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, true, false, null, null, null, null);
				createHeaderCell(eventTable, ":", 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, false, null, null, null, null);
				createHeaderCell(eventTable, hostName, 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, false, null, null, null, null);

//				addPair(eventTable, basicFont, blackColor, customerName, hostName,
//						hasBilling ? billingNameLabel : null, hasBilling ? billingName : null, BLOCK_TOP);

				createHeaderCell(eventTable, customerPhone, 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, true, true, null, null, null, null);
				createHeaderCell(eventTable, ":", 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, true, null, null, null, null);
				createHeaderCell(eventTable, mobileNo, 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, true, null, null, null, null);

//				addPair(eventTable, basicFont, blackColor, customerPhone, mobileNo, null, null, NONE);

				createHeaderCell(eventTable, addressLabel, 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, true, true, null, null, new DoubleBorder(blackColor, 3f),
						null);
				createHeaderCell(eventTable, ":", 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, true, null, null, new DoubleBorder(blackColor, 3f),
						null);
				createHeaderCell(eventTable, partyAddress.toUpperCase(), 11f, blackColor, null, 1, 1,
						TextAlignment.LEFT, VerticalAlignment.MIDDLE, basicFont, false, true, null, null,
						new DoubleBorder(blackColor, 3f), null);
				
				createHeaderCell(eventTable, emailLabel, 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, true, true, null, null, new DoubleBorder(blackColor, 3f),
						null);
				createHeaderCell(eventTable, ":", 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
						VerticalAlignment.MIDDLE, basicFont, false, true, null, null, new DoubleBorder(blackColor, 3f),
						null);
				createHeaderCell(eventTable, email, 11f, blackColor, null, 1, 1,
						TextAlignment.LEFT, VerticalAlignment.MIDDLE, basicFont, false, true, null, null,
						new DoubleBorder(blackColor, 3f), null);

//				createHeaderCell(eventTable, eVenue, 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
//						VerticalAlignment.MIDDLE, basicFont, true, false, null);
//				createHeaderCell(eventTable, ":", 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
//						VerticalAlignment.MIDDLE, basicFont, false, false, null);
//				createHeaderCell(eventTable, venue.toUpperCase(), 11f, labelColor, null, 1, 1, TextAlignment.LEFT,
//						VerticalAlignment.MIDDLE, basicFont, false, false, null);

//				addPair(eventTable, basicFont, blackColor, eName, eventName.toUpperCase(), eDate, eventDate,NONE);
//
//				String foodNoteValue = foodNotes.trim().isEmpty() ? foodNotesName
//						: foodNotesName + " (" + foodNotes + ")";
//				addFullWidthPair(eventTable, basicFont, fNotes, foodNoteValue, redColor, blackColor, NONE);
//
//				addFullWidthPair(eventTable, basicFont, eVenue, venue.toUpperCase(), blackColor, blackColor, NONE);
//
//				addPair(eventTable, basicFont, blackColor, serviceLabel, service, null, null, NONE);
//
//				addPair(eventTable, basicFont, blackColor, themeLabel, theme, null, null, NONE);
//
//				addFullWidthPair(eventTable, basicFont, eventNotes, remarks, blackColor, blackColor, NONE);

//				addFullWidthPair(eventTable, basicFont, lang == 0 ? "Function Notes" : eventNotes,
//						safeText(functionNotes), blackColor, blackColor, NONE);

				document.add(eventTable);

				// ---- Menu items ----
				for (MenuReportResponseDto menu : eventFunctionMasterResponseDto.getMenuCategories()) {
					catItemTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
					catItemTable.setWidth(UnitValue.createPercentValue(100));
					catItemTable.setBorder(Border.NO_BORDER);
					catItemTable.setMarginTop(15f);
					catItemTable.setKeepTogether(false);
					catItemTable.setBorderTop(new SolidBorder(labelColor, 1f));
					catItemTable.setBorderBottom(new SolidBorder(labelColor, 1f));

					ISplitCharacters splitAll = new ISplitCharacters() {
						@Override
						public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
							return true;
						}
					};

					Paragraph p = new Paragraph(
							menu.getNameEnglish() != null ? menu.getNameEnglish().toUpperCase() : "").setFont(basicFont)
							.setFontSize(14f).setFontColor(blackColor).setSplitCharacters(splitAll).setMarginLeft(5f)
							.setPadding(0);

					Cell catCell = new Cell().add(p).setBorder(Border.NO_BORDER)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);

					if (isCategoryInstruction == 1 && menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
						catCell.add(new Paragraph(
								menuPreparationServiceImpl.formatText(" ( " + menu.getMenuNotes() + " )", lang))
								.setSplitCharacters(splitAll).setFont(basicFont).setFontSize(12f)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.CENTER));
					}

					catItemTable.addCell(catCell);

					Cell itemsCell = new Cell().setBorder(Border.NO_BORDER);
					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						itemsCell.add(
								new Paragraph(item.getNameEnglish() != null ? item.getNameEnglish().toUpperCase() : "")
										.setFont(basicFont).setFontSize(12f).setFontColor(blackColor).setMarginLeft(12)
										.setPadding(0).setTextAlignment(TextAlignment.LEFT));

						if (isItemInstruction == 1 && item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
							itemsCell.add(new Paragraph(
									menuPreparationServiceImpl.formatText(" ( " + item.getItemNotes() + " ) ", lang))
									.setFont(basicFont).setSplitCharacters(splitAll).setFontSize(12)
									.setFixedLeading(12f).setFontColor(blackColor).setMarginLeft(18).setMarginBottom(0f)
									.setPadding(0f).setTextAlignment(TextAlignment.LEFT));
						}
					}
					catItemTable.addCell(itemsCell.setKeepTogether(true));
					document.add(catItemTable);
				}
				isFirstFunction = false;
			}

			Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
					.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

			isTermsCond = isTermsCond != null ? isTermsCond : 0;
			if (isTermsCond == 1 && terms != null && terms.isPresent()) {
				EventTermsAndConditionEntity eventTermsEntity = terms.get();
				List<EventTermsAndConditionFeaturesEntity> features = new ArrayList<>();
				features = eventTermsAndConditionFeaturesRepository
						.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

				if (features != null && !features.isEmpty()) {
					Paragraph termsTitle = new Paragraph("Special Instruction").setFont(basicFont).setFontSize(16)
							.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f).setFontColor(blackColor);

					document.add(termsTitle);

					ISplitCharacters breakAll = new ISplitCharacters() {
						@Override
						public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
							return true; // break anywhere
						}
					};

					int index = 1;
					for (EventTermsAndConditionFeaturesEntity feature : features) {
						String desc;

						if (lang == 1) {
							desc = feature.getDescriptionHindi();
						} else if (lang == 2) {
							desc = feature.getDescriptionGujarati();
						} else {
							desc = feature.getDescription();
						}

						Paragraph term = new Paragraph(index + ". " + desc).setFont(basicFont).setFontSize(12)
								.setWidth(UnitValue.createPercentValue(90f))
								.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
								.setSplitCharacters((text, glyphPos) -> true).setFontColor(blackColor);

						term.setSplitCharacters(breakAll);

						document.add(term);
						index++;
					}
				}
			}

//			if (isAddDecoration == 1) {
//				for (EventFunctionReportResponseDto fn : eventDto.getFunctions()) {
//
//					List<DecoreReportResponseDto> decoreCategories = fn.getDecoreCategories();
//
//					if (decoreCategories == null || decoreCategories.isEmpty()) {
//						continue;
//					}
//
//					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//
//					String functionVenue = "";
//					List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
//							.findBanquetByEventFunctionId(eventId, fn.getFunctionId());
//					if (!banquets.isEmpty()) {
//						functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
//								.collect(Collectors.joining(", ")).toUpperCase();
//					} else {
//						functionVenue = lang == 1 ? fn.getFunctionVenueHindi()
//								: lang == 2 ? fn.getFunctionVenueGujarati() : fn.getFunctionVenue().toUpperCase();
//					}
//
//					// TYPE OF EVENT
//					String functionName = fn.getFunctionName() != null ? fn.getFunctionName().toUpperCase() : "";
//
//					// ---------------- Header info block ----------------
//					Table header = new Table(UnitValue.createPercentArray(new float[] { 30f, 2f, 30f, 15f, 2f, 21f }));
//
//					header.setWidth(UnitValue.createPercentValue(100f));
//					header.setFixedLayout();
//					header.setHorizontalAlignment(HorizontalAlignment.CENTER);
//					header.setBorderBottom(new SolidBorder(blackColor, 1f));
//
//					createHeaderCell(header, eDate, 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null,
//							new SolidBorder(blackColor, 1f), null);
//					createHeaderCell(header, ":", 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null,
//							new SolidBorder(blackColor, 1f), null);
//					createHeaderCell(header, eventTime.toUpperCase(), 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null,
//							new SolidBorder(blackColor, 1f), null);
//
//					createHeaderCell(header, eDate, 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null,
//							new SolidBorder(blackColor, 1f), null);
//					createHeaderCell(header, ":", 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null,
//							new SolidBorder(blackColor, 1f), null);
//					createHeaderCell(header, eventTime.toUpperCase(), 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null,
//							new SolidBorder(blackColor, 1f), null);
//
//					createHeaderCell(header, customerName, 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null, null, null);
//					createHeaderCell(header, ":", 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null, null, null);
//					createHeaderCell(header, hostName.toUpperCase(), 12f, blackColor, null, 4, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null, null, null);
//
//					createHeaderCell(header, customerPhone, 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null, null, null);
//					createHeaderCell(header, ":", 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null, null, null);
//					createHeaderCell(header, mobileNo.toUpperCase(), 12f, blackColor, null, 4, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null, null, null);
//
//					createHeaderCell(header, eVenue, 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null, null, null);
//					createHeaderCell(header, ":", 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null, null, null);
//					createHeaderCell(header, functionVenue.toUpperCase(), 12f, blackColor, null, 4, 1,
//							TextAlignment.LEFT, VerticalAlignment.MIDDLE, basicFont, true, true, null, null, null,
//							null);
//
//					createHeaderCell(header, function, 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, true, true, null, null, null, null);
//					createHeaderCell(header, ":", 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null, null, null);
//					createHeaderCell(header, functionName.toUpperCase(), 12f, blackColor, null, 4, 1,
//							TextAlignment.LEFT, VerticalAlignment.MIDDLE, basicFont, true, true, null, null, null,
//							null);
//
//					createHeaderCell(header, person, 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null, null, null);
//					createHeaderCell(header, ":", 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, false, true, null, null, null, null);
//					createHeaderCell(header, fn.getPax().toString(), 12f, blackColor, null, 4, 1, TextAlignment.LEFT,
//							VerticalAlignment.MIDDLE, basicFont, true, true, null, null, null, null);
//
//					document.add(header);
//
//					// ---------------- Decoration title ----------------
//					Paragraph sectionTitle = new Paragraph("Decoration").setFont(basicFont).setFontSize(16)
//							.setFontColor(blackColor).simulateBold().setTextAlignment(TextAlignment.CENTER)
//							.setMarginTop(6f).setMarginBottom(6f);
//
//					document.add(sectionTitle);
//
//					document.add(new LineSeparator(new SolidLine(1f)).setMarginBottom(10f));
//
//					// ---------------- Decoration items (NO BULLETS) ----------------
//					// Color navyColor = new DeviceRgb(24, 55, 95);
//
//					for (DecoreReportResponseDto category : decoreCategories) {
//
//						List<DecoreItemReportResponseDto> items = category.getDecoreItems();
//
//						if (items == null || items.isEmpty()) {
//							continue;
//						}
//
//						for (DecoreItemReportResponseDto item : items) {
//
//							String itemName = item.getNameEnglish() != null ? item.getNameEnglish() : "";
//
//							String subName = item.getSubItem() != null ? item.getSubItem() : "";
//							Integer itemSpace = item.getItemSpace() != null ? item.getItemSpace() : 0;
//							BigDecimal price = item.getPrice();
//
//							Integer itemQty = item.getItemQty();
//
//							Paragraph headerPara = new Paragraph().setFont(basicFont).setFontSize(16)
//									.setFontColor(blackColor).setMarginBottom(6f);
//
//							if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
//
//								headerPara.add(new Text("\u2022 " + itemName + ": "));
//
//								if (!subName.isEmpty()) {
//									headerPara.add(new Text(subName));
//								}
//								BigDecimal price1 = price != null ? price : BigDecimal.ZERO;
//								int qty = itemQty != null ? itemQty : 0;
//								BigDecimal totalAmount = price1.multiply(BigDecimal.valueOf(qty));
//
//								String postLabel = "";
//								if (qty > 1) {
//									postLabel = " /- + Tax Each";
//								} else {
//									postLabel = " /- + Tax";
//								}
//
//								headerPara.add(new Text("  @Rs. " + totalAmount + postLabel));
//
//							} else if (!subName.isEmpty()) {
//
//								headerPara.add(new Text("\u2022 " + itemName + ": "));
//								headerPara.add(new Text(subName));
//
//							} else {
//
//								headerPara.add(new Text("\u2022 " + itemName));
//							}
//
//							document.add(headerPara);
//
//							if (isItemInstruction == 1 && item.getDecoreItemNotes() != null
//									&& !item.getDecoreItemNotes().trim().isEmpty()) {
//
//								for (String line : item.getDecoreItemNotes().split("\\r?\\n")) {
//
//									if (!line.trim().isEmpty()) {
//
//										Paragraph notePara = new Paragraph(
//												"\u2022 " + menuPreparationServiceImpl.formatText(line.trim(), lang))
//												.setFont(basicFont).setFontSize(15).setFontColor(blackColor)
//												.setMarginLeft(50f).setMarginBottom(3f);
//
//										document.add(notePara);
//									}
//								}
//							}
//
//							// -------- Image --------
//							String imgPath = item.getImagePath();
//
//							if (isCatImage == 1 && imgPath != null && !imgPath.trim().isEmpty()) {
//
//								try {
//
//									ImageData imgData = menuPreparationServiceImpl
//											.loadImageFromResource(environment.getProperty("app.image.url") + imgPath);
//
//									// ImageData imgData = menuPreparationServiceImpl
//									// .loadImageFromResource("/flipbook/pages/krishna_front_4.png");
//
//									Image img = new Image(imgData);
//
//									img.scaleToFit(200, 300);
//									img.setHorizontalAlignment(HorizontalAlignment.CENTER);
//									img.setMarginTop(8f);
//									img.setMarginBottom(14f);
//
//									document.add(img);
//
//								} catch (Exception imgEx) {
//
//									System.out.println("Could not load image for decor item: " + imgPath);
//								}
//							}
//							for (int i = 0; i < itemSpace; i++) {
//								document.add(new Paragraph("\n"));
//							}
//							// Space between decoration items
//							document.add(new Paragraph().setMarginBottom(10f));
//						}
//					}
//				} // End of function loop
//			}
			
//			createHeaderCell(header, eDate, 12f, blackColor, null, 1, 1, TextAlignment.LEFT,
//			VerticalAlignment.MIDDLE, basicFont, false, true, null, null,
//			new SolidBorder(blackColor, 1f), null);
			
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			if (isAddDecoration == 1) {
				List<DecoreReportResponseDto> decoreReportResponseDtos = eventDto.getFunctions().stream().flatMap(
						fn -> Optional.ofNullable(fn.getDecoreCategories()).orElse(Collections.emptyList()).stream())
						.collect(Collectors.toList());

				if (decoreReportResponseDtos != null && !decoreReportResponseDtos.isEmpty()) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					document.add(new Paragraph("Setup Details:").setFont(basicFont).setFontSize(22f)
							.simulateBold().setUnderline());

					for (DecoreReportResponseDto decore : decoreReportResponseDtos) {
						Div decoreContent = new Div();
						decoreContent.setKeepTogether(false);
						String text = "";
						for (DecoreItemReportResponseDto item : decore.getDecoreItems()) {
							List<DecoreMainCategoryItemImagesMasterEntity> imagesMasterEntities = decoreMainCategoryItemImagesMasterRepository
									.findAllByDecoreItem_IdAndIsDeleteFalse(item.getId());
							Integer itemQty = item.getItemQty();
							Integer itemSpace = item.getItemSpace() != null ? item.getItemSpace() : 0;
							BigDecimal itemPrice = item.getPrice();

							String subItem = "";
							if (lang == 1) {
								subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
							} else if (lang == 2) {
								subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
							} else {
								subItem = item.getSubItem() != null ? item.getSubItem() : "";
							}

							BigDecimal price1 = itemPrice != null ? itemPrice : BigDecimal.ZERO;
							int qty = itemQty != null ? itemQty : 0;
							BigDecimal totalAmount = price1.multiply(BigDecimal.valueOf(qty));

							String postLabel = "";
							if (qty > 1) {
								postLabel = " /- + Tax Each";
							} else {
								postLabel = " /- + Tax";
							}
							String text1 = "\u2022 " + menuPreparationServiceImpl.formatText(item.getNameEnglish(), lang)
									+ (subItem.trim().length() != 0 ? " " + subItem : "")
									+ (totalAmount.compareTo(BigDecimal.ZERO) != 0
											? " @RS." + totalAmount.intValue() + postLabel
											: "");

							decoreContent.add(new Paragraph(text1).setFont(basicFont)
									.setFontSize(19).setFixedLeading(20f)
									.setTextAlignment(TextAlignment.LEFT).setFontColor(blackColor).setMarginLeft(20f)
									.setMarginTop(15f));

							// Item Instructions
							if (isItemInstruction != null && isItemInstruction == 1) {
								if (item.getDecoreItemNotes() != null && !item.getDecoreItemNotes().isEmpty()) {
									text = menuPreparationServiceImpl.formatText(item.getDecoreItemNotes(), lang);
									decoreContent.add(new Paragraph(text).setFont(basicFont)
											.setFontSize(19).setMultipliedLeading(1.2f)
											.setFontColor(blackColor).setMarginLeft(0f).setMarginTop(0)
											.setTextAlignment(TextAlignment.LEFT).setMarginBottom(0).setPadding(0f)
											.setPaddingLeft(130f));
								}
							}

							if (imagesMasterEntities != null && !imagesMasterEntities.isEmpty()) {
								try {
									Image img = new Image(ImageDataFactory.create(
											environment.getProperty("app.image.url") + decore.getImagePath()));
//									Image img = new Image(loadImageFromResource("/flipbook/pages/logo.png"));

									img.setAutoScale(false);
									img.scaleToFit(300, 200);
									img.setHorizontalAlignment(HorizontalAlignment.CENTER);
									img.setMarginTop(5);
									img.setMarginBottom(5);

									decoreContent.add(img);
								} catch (Exception e) {
									// Skip if image loading fails
								}
							}

							for (int i = 0; i < itemSpace; i++) {
								decoreContent.add(new Paragraph("\n"));
							}
						}

						document.add(decoreContent);
					}
				}
			}

			System.out.println("4");
			int totalPages = pdfDocument.getNumberOfPages();
			ImageData tncPage = null;
			if (adminTemplate.getTemplateMaster().getCatBgPage() != null
					&& adminTemplate.getTemplateMaster().getCatBgPage().trim().length() != 0) {
				tncPage = menuPreparationServiceImpl.loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getCatBgPage());
			}
//			ImageData tncPage = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/amoncar_terms.png");

			// ============ Terms And Condition PAGE ============
			if (isNotes == 1 && tncPage != null) {
				bgHandler.setPageBackground(totalPages + 1, tncPage);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				Paragraph invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);
				totalPages = totalPages + 1;
			}

			// ================= PAGE NUMBERS =================
			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());
				// Decrease font size
				canvas.setFont(basicFont);
				canvas.setFontSize(9);

				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 22,
						TextAlignment.CENTER);

				if (isSignatureVisible == 1) {
					canvas.showTextAligned("Client Signature ___________________", page.getPageSize().getWidth() - 36,
							22, TextAlignment.RIGHT);
				}
				canvas.close();
			}
						
			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo() + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"back office report")
					+ ".pdf";

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	private void createHeaderCell(Table table, String label, float fontSize, Color fontColor, Color bgColor,
			Integer colSpan, Integer rowSpan, TextAlignment textAlignment, VerticalAlignment verticalAlignment,
			PdfFont font, Boolean isBold, Boolean isBorder, Border topBorder, Border rightBorder, Border bottomBorder,
			Border leftBorder) {
		Cell cell = new Cell(rowSpan, colSpan).add(new Paragraph(label).setFont(font).setFontSize(fontSize)
				.setFontColor(fontColor).setTextAlignment(textAlignment).setVerticalAlignment(verticalAlignment));

		if (isBold) {
			cell.simulateBold();
		}

		if (bgColor != null) {
			cell.setBackgroundColor(bgColor);
		}

		cell.setBorderTop(topBorder != null ? topBorder : Border.NO_BORDER);
		cell.setBorderRight(rightBorder != null ? rightBorder : Border.NO_BORDER);
		cell.setBorderBottom(bottomBorder != null ? bottomBorder : Border.NO_BORDER);
		cell.setBorderLeft(leftBorder != null ? leftBorder : Border.NO_BORDER);

		table.addCell(cell);
	}

	@Override
	public String orderSummaryReport3(String startDate, String endDate, List<Integer> eventStatus,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, List<Long> managerIds,
			Long partyId) {

		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();
			String eventDateLabel, eventNameLabel, functionNameLabel, paxLabel, partyNameLabel, managerNameLabel,
					statusLabel, totalLabel, transportationLabel, grandTotalLabel;
			eventDateLabel = "Event Date";
			eventNameLabel = "Event Name";
			functionNameLabel = "Function Name";
			paxLabel = "Pax";
			partyNameLabel = "Party Name";
			managerNameLabel = "Manager Name";
			statusLabel = "Status";
			totalLabel = "Total";
			transportationLabel = "Transportation";
			grandTotalLabel = "Grand Total";

			int headFontSize = 13;
			int fontSize = 10;
			float lineHeight = 1.1f;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				eventDateLabel = "कार्यक्रम तिथि";
				eventNameLabel = "कार्यक्रम का नाम";
				functionNameLabel = "समारोह का नाम";
				paxLabel = "संख्या";
				partyNameLabel = "ग्राहक का नाम";
				managerNameLabel = "प्रबंधक का नाम";
				statusLabel = "स्थिति";
				totalLabel = "टोटल";
				transportationLabel = "ट्रांसपोर्टेशन";
				grandTotalLabel = "ग्रांड टोटल";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				eventDateLabel = "કાર્યક્રમ તારીખ";
				eventNameLabel = "કાર્યક્રમનું નામ";
				functionNameLabel = "સમારોહનું નામ";
				paxLabel = "સંખ્યા";
				partyNameLabel = "ગ્રાહકનું નામ";
				managerNameLabel = "મેનેજરનું નામ";
				statusLabel = "સ્થિતિ";
				totalLabel = "ટોટલ";
				transportationLabel = "ટ્રાન્સપોટેસન";
				grandTotalLabel = "ગ્રાન્ડ ટોટલ";
				fontSize = 11;
				headFontSize = 14;
				lineHeight = 0.9f;

			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/orderSummaryReport");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			File pdfFile = new File(outputPath + "/" + "FROM " + formatDate(startDate) + " TO " + formatDate(endDate)
					+ "_order_summary_report.pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, PageSize.A4.rotate());
			document.setMargins(40, 35, 50, 20);

			float[] columnWidthHead = { 20f, 18f, 2f, 57f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));

			MenuQuantityReponseDto cmpData = menuItemRawMaterialServiceImpl.getCmpData(userid);

			String cmpName = cmpData != null ? commonService.getString(cmpData.getCompanyName()) : "";
			String cmpCountryCode = cmpData != null ? commonService.getString(cmpData.getCountryCode()) : "";
			String cmpMobile = cmpData != null ? commonService.getString(cmpData.getOfficeNo()) : "";
			String cmpEmail = cmpData != null ? commonService.getString(cmpData.getCompanyEmail()) : "";
			String logo = cmpData != null ? commonService.getString(cmpData.getLogo()) : "";
			String companyAddress = cmpData != null ? commonService.getString(cmpData.getCompanyAddress()) : "";

//			ImageData imgData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
//			Image img = new Image(imgData);
			Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));
			img.setWidth(100);
			img.setHorizontalAlignment(HorizontalAlignment.LEFT);

			Cell cmp = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			Paragraph cmpPara = new Paragraph().add(
					new Text(cmpName).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18))
					.setMultipliedLeading(1f);
			cmp = new Cell(1, 3).add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(new Text("Mobile No.")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpCountryCode + " " + cmpMobile)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text("Email").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpEmail).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER).setMarginBottom(3f);
			cmpHead.addCell(cmp);

			if (isCompanyDetails == 1) {
				cmpHead.setMarginBottom(7f);
				document.add(cmpHead);
			}

			LocalDate firstDate = commonService.dateFormatted(startDate);
			LocalDate lastDate = commonService.dateFormatted(endDate);

			Paragraph para = new Paragraph().add(new Text("DATEWISE ORDER SUMMARY"))
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(16)
					.setTextAlignment(TextAlignment.CENTER);

			document.add(para);

			float[] cw2 = { 50, 50 };
			Table dTable = new Table(UnitValue.createPercentArray(cw2));
			dTable.setWidth(UnitValue.createPercentValue(100));
			dTable.setMarginBottom(7f);

			para = new Paragraph().add(new Text("From Date: " + startDate))
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(13)
					.setTextAlignment(TextAlignment.CENTER);
			Cell cell = new Cell().add(para).setBorder(Border.NO_BORDER);
			dTable.addCell(cell);

			para = new Paragraph().add(new Text("To Date: " + endDate))
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(13)
					.setTextAlignment(TextAlignment.CENTER);
			cell = new Cell().add(para).setBorder(Border.NO_BORDER);
			dTable.addCell(cell);

			document.add(dTable);

			List<OrderSummaryResponseDto> summaryResponseDtos = menuPreparationServiceImpl
					.getDatewiseOrderSummaryReport(firstDate, lastDate, userid, lang, eventStatus, managerIds, partyId,
							"type3");

			// --- Header Table (separate, so it doesn't get pulled with data) ---
			float[] cw = new float[] { 10, // Event Date
					18, // Event Name
					18, // Function Name
					6, // Pax
					18, // Party Name
					15, // Manager Name
					10, // Status
					10, // Total
					12, // Transportation
					12 // Grand Total
			};

			Table headerTable = new Table(UnitValue.createPercentArray(cw));
			headerTable.setWidth(UnitValue.createPercentValue(100));
			headerTable.setFixedLayout();

			String[] headers = new String[] { eventDateLabel, eventNameLabel, functionNameLabel, paxLabel,
					partyNameLabel, managerNameLabel, statusLabel, totalLabel, transportationLabel, grandTotalLabel };
			for (String header : headers) {
				para = new Paragraph().add(new Text(header)).setFont(boldFont).setFontSize(headFontSize)
						.setTextAlignment(TextAlignment.CENTER);
				cell = new Cell().add(para);
				headerTable.addCell(cell);
			}
			document.add(headerTable);

			// --- Group DTOs ---
			Map<String, List<OrderSummaryResponseDto>> groupedMap = new LinkedHashMap<>();
			for (OrderSummaryResponseDto dto : summaryResponseDtos) {
				String key = dto.getEventDate() + "||" + dto.getEventName() + "||" + dto.getPartyName() + "||"
						+ dto.getManagerName() + "||" + dto.getStatus();
				groupedMap.computeIfAbsent(key, k -> new ArrayList<>()).add(dto);
			}

			// --- One mini-table per group, kept together on same page ---
			for (Map.Entry<String, List<OrderSummaryResponseDto>> entry : groupedMap.entrySet()) {
				List<OrderSummaryResponseDto> group = entry.getValue();

				String eventDate = group.get(0).getEventDate() != null ? group.get(0).getEventDate() : "";
				String eventName = group.get(0).getEventName() != null ? group.get(0).getEventName() : "";
				String partyName = group.get(0).getPartyName() != null ? group.get(0).getPartyName() : "";
				String managerName = group.get(0).getManagerName() != null ? group.get(0).getManagerName() : "";
				String status = group.get(0).getStatus() != null ? group.get(0).getStatus() : "";
				String eventNo = group.get(0).getEventNo() != null ? group.get(0).getEventNo() : "";
				String partyContactNo = group.get(0).getMobileNo() != null ? group.get(0).getMobileNo() : "";
				Integer subTotal = group.get(0).getSubTotal() != null ? group.get(0).getSubTotal() : 0;
				Integer transportation = group.get(0).getTransportation() != null ? group.get(0).getTransportation()
						: 0;
				Integer grandTotal = group.get(0).getGrandTotal() != null ? group.get(0).getGrandTotal() : 0;

				// Create a mini-table for this group
				Table groupTable = new Table(UnitValue.createPercentArray(cw));
				groupTable.setWidth(UnitValue.createPercentValue(100));
				groupTable.setKeepTogether(true); // <-- KEY: never split this group across pages
				groupTable.setFixedLayout();

				for (int i = 0; i < group.size(); i++) {

					OrderSummaryResponseDto dto = group.get(i);

					String functionName = dto.getFunctionName() != null ? dto.getFunctionName() : "";
					String functionPax = dto.getFunctionPax() != null ? dto.getFunctionPax() : "";

					// Event Date
					if (i == 0) {
						groupTable.addCell(new Cell(group.size(), 1)
								.add(new Paragraph(eventDate).setFont(basicFont).setFontSize(fontSize))
								.setVerticalAlignment(VerticalAlignment.MIDDLE));
					}

					// Event Name
					if (i == 0) {
						groupTable.addCell(new Cell(group.size(), 1)
								.add(new Paragraph(eventName).setFont(basicFont).setFontSize(fontSize))
								.setVerticalAlignment(VerticalAlignment.MIDDLE));
					}

					// Function Name
					groupTable.addCell(
							new Cell().add(new Paragraph(functionName).setFont(basicFont).setFontSize(fontSize)));

					// Pax
					groupTable.addCell(
							new Cell().add(new Paragraph(functionPax).setFont(basicFont).setFontSize(fontSize)));

					// Party Name
					if (i == 0) {
						groupTable.addCell(new Cell(group.size(), 1)
								.add(new Paragraph(partyName).setFont(basicFont).setFontSize(fontSize))
								.setVerticalAlignment(VerticalAlignment.MIDDLE));
					}

					// Manager Name
					if (i == 0) {
						groupTable.addCell(new Cell(group.size(), 1)
								.add(new Paragraph(managerName).setFont(basicFont).setFontSize(fontSize))
								.setVerticalAlignment(VerticalAlignment.MIDDLE));
					}

					// Status
					if (i == 0) {
						groupTable.addCell(new Cell(group.size(), 1)
								.add(new Paragraph(status).setFont(basicFont).setFontSize(fontSize))
								.setVerticalAlignment(VerticalAlignment.MIDDLE));
					}

					// Total
					if (i == 0) {
						groupTable.addCell(new Cell(group.size(), 1)
								.add(new Paragraph(String.valueOf(subTotal)).setFont(basicFont).setFontSize(fontSize))
								.setTextAlignment(TextAlignment.RIGHT).setVerticalAlignment(VerticalAlignment.MIDDLE));
					}

					// Transportation
					if (i == 0) {
						groupTable.addCell(new Cell(group.size(), 1)
								.add(new Paragraph(String.valueOf(transportation)).setFont(basicFont)
										.setFontSize(fontSize))
								.setTextAlignment(TextAlignment.RIGHT).setVerticalAlignment(VerticalAlignment.MIDDLE));
					}

					// Grand Total
					if (i == 0) {
						groupTable.addCell(new Cell(group.size(), 1)
								.add(new Paragraph(String.valueOf(grandTotal)).setFont(basicFont).setFontSize(fontSize))
								.setTextAlignment(TextAlignment.RIGHT).setVerticalAlignment(VerticalAlignment.MIDDLE));
					}
				}

				document.add(groupTable);
			}

			document.close();

			String scheme = re.getScheme();
			String serverName = re.getServerName();
			int serverPort = re.getServerPort();
			String contextPath = re.getContextPath();

			String fullUrl;
			if ((scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443)) {
				fullUrl = "https://" + serverName + contextPath + "/api/download/pdf/orderSummaryReport" + "/" + "FROM "
						+ formatDate(startDate) + " TO " + formatDate(endDate) + "_order_summary_report" + ".pdf";
			} else {
				fullUrl = "https://" + serverName + ":" + serverPort + contextPath
						+ "/api/download/pdf/orderSummaryReport" + "/" + "FROM " + formatDate(startDate) + " TO "
						+ formatDate(endDate) + "_order_summary_report" + ".pdf";
			}

			fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/orderSummaryReport" + "/" + "FROM "
					+ formatDate(startDate) + " TO " + formatDate(endDate) + "_order_summary_report" + ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report generation failed.");
		}
	}
	
	@Override
	public String getMenuPlanningSimpleReport15(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, int categoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid,
			AdminTemplateModuleResponseDto adminTemplate) {
		try {
			PdfFont basicFont = null;
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "", l1 = "",
					l2 = "", note = "", eContact = "", eventFlow = "", function = "", person = "", eTime = "",
					date = "", rate = "", party = "", eventNotes = "", serviceLabel = "", themeLabel = "",
					altMobileLabel = "", refLabel = "";
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				customerName = "ग्राहक का नाम";
				customerPhone = "मोबाइल नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "दिनांक";
				fNotes = "नोट्स";
				eVenue = "आयोजन स्थान";
				function = "कार्यक्रम";
				person = "मेंबर्स";
				eTime = "समय";
				eContact = "संपर्क नंबर";
				l1 = "व्यक्तियों की संख्या:";
				eventFlow = "कार्यक्रम का संचालन क्रम";
				note = "नोट";
				date = "दिनांक";
				rate = "रेट";
				party = "पार्टी का नाम";
				eventNotes = "कार्यक्रम की नोट";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				altMobileLabel = "दूसरा मोबाइल नंबर";
				refLabel = "रेफरेंस";
				y = 512;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Regular.ttf");

					// Tamil
					customerName = "வாடிக்கையாளர் பெயர்";
					customerPhone = "மொபைல் எண்";
					eName = "நிகழ்ச்சி பெயர்";
					eDate = "நிகழ்ச்சி தேதி";
					fNotes = "உணவு விவரம்";
					eVenue = "நிகழ்வு இடம்";
					function = "நிகழ்ச்சி";
					person = "நபர்";
					eTime = "நேரம்";
					eContact = "தொடர்பு எண்";
					l1 = "நபர்களின் எண்ணிக்கை:";
					eventFlow = "நிகழ்ச்சி நடைபெறும் வரிசை";
					note = "குறிப்பு";
					date = "தேதி";
					rate = "விலை";
					party = "கட்சியின் பெயர்";
					eventNotes = "நிகழ்வு குறிப்புகள்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					altMobileLabel = "மாற்று கைபேசி எண்";
					refLabel = "பரிந்துரை";
				} else if (language.equalsIgnoreCase("Telugu")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Regular.ttf");

					// Telugu
					customerName = "కస్టమర్ పేరు";
					customerPhone = "మొబైల్ నంబర్";
					eName = "కార్యక్రమ పేరు";
					eDate = "కార్యక్రమ తేదీ";
					fNotes = "భోజన వివరాలు";
					eVenue = "కార్యక్రమ స్థలం";
					function = "కార్యక్రమం";
					person = "వ్యక్తి";
					eTime = "సమయం";
					eContact = "సంప్రదింపు నంబర్";
					l1 = "వ్యక్తుల సంఖ్య:";
					eventFlow = "కార్యక్రమ నిర్వహణ క్రమం";
					note = "గమనిక";
					date = "తేదీ";
					rate = "రేటు";
					party = "పార్టీ పేరు";
					eventNotes = "ఈవెంట్ గమనికలు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					altMobileLabel = "ప్రత్యామ్నాయ ఫోన్ నంబర్";
					refLabel = "రిఫరెన్స్";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Regular.ttf");

					// Malayalam
					customerName = "ഉപഭോക്താവിന്റെ പേര്";
					customerPhone = "മൊബൈൽ നമ്പർ";
					eName = "പരിപാടിയുടെ പേര്";
					eDate = "പരിപാടിയുടെ തീയതി";
					fNotes = "ഭക്ഷണ വിശദാംശങ്ങൾ";
					eVenue = "പരിപാടി സ്ഥലം";
					function = "പരിപാടി";
					person = "വ്യക്തി";
					eTime = "സമയം";
					eContact = "ബന്ധപ്പെടാനുള്ള നമ്പർ";
					l1 = "വ്യക്തികളുടെ എണ്ണം:";
					eventFlow = "പരിപാടി നടത്തിപ്പ് ക്രമം";
					note = "കുറിപ്പ്";
					date = "തീയതി";
					rate = "നിരക്ക്";
					party = "പാർട്ടി പേര്";
					eventNotes = "ഇവന്റ് കുറിപ്പുകൾ";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					altMobileLabel = "മറ്റൊരു മൊബൈൽ നമ്പർ";
					refLabel = "റഫറൻസ്";
				} else if (language.equalsIgnoreCase("Marathi")) {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");

					// Marathi
					customerName = "ग्राहकाचे नाव";
					customerPhone = "मोबाईल नंबर";
					eName = "कार्यक्रमाचे नाव";
					eDate = "कार्यक्रमाची दिनांक";
					fNotes = "भोजन तपशील";
					eVenue = "आयोजन स्थळ";
					function = "कार्यक्रम";
					person = "व्यक्ती";
					eTime = "वेळ";
					eContact = "संपर्क नंबर";
					l1 = "व्यक्तींची संख्या:";
					eventFlow = "कार्यक्रमाचा संचालन क्रम";
					note = "टीप";
					date = "दिनांक";
					rate = "रेट";
					party = "पार्टीचे नाव";
					eventNotes = "कार्यक्रम नोंदी";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					altMobileLabel = "दुसरा मोबाईल नंबर";
					refLabel = "रेफरन्स";
				} else {
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");

					customerName = "ગ્રાહકનું નામ";
					customerPhone = "મોબાઇલ નંબર";
					eName = "કાર્યક્રમનું નામ";
					eDate = "કાર્યક્રમની તારીખ";
					fNotes = "ભોજન વિગતો";
					eVenue = "આયોજન સ્થળ";
					function = "કાર્યક્રમ";
					person = "વ્યક્તિ";
					eTime = "સમય";
					eContact = "સંપર્ક નંબર";
					eventFlow = "કાર્યક્રમનું સંચાલન ક્રમ";
					l1 = "વ્યક્તિઓની સંખ્યા:";
					note = "નોંધ";
					date = "તારીખ";
					rate = "રેટ";
					party = "પાર્ટીનું નામ";
					eventNotes = "કાર્યક્રમની નોંધ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					altMobileLabel = "બીજો મોબાઇલ નંબર";
					refLabel = "રેફરન્સ";
				}
				System.out.println("Gujarati font loaded successfully");
				y = 510;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				System.out.println("English font loaded successfully");
				customerName = "Customer Name";
				customerPhone = "Mobile No.";
				eName = "Event Name";
				eDate = "Event Date";
				fNotes = "Food Note";
				eVenue = "Venue";
				function = "Function";
				person = "Persons";
				eTime = "Timing";
				eContact = "CONTACT NO";
				eventFlow = "FLOW OF EVENT";
				l1 = "OF PERSONS:";
				note = "Note";
				date = "Date";
				rate = "Rate";
				party = "PARTY NAME";
				eventNotes = "Event Notes";
				serviceLabel = "Service";
				themeLabel = "Theme";
				altMobileLabel = "Alt. Mobile";
				refLabel = "Ref.";
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);

			if (eventDto == null) {
				return "";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "back office report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 20, 20, 70);

			// Load all background images upfront
//			ImageData mainBgData = menuPreparationServiceImpl
//					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
//			ImageData watermarkBgData = menuPreparationServiceImpl
//					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			Color redColor = new DeviceRgb(255, 0, 0);
			Color blackColor = new DeviceRgb(0, 0, 0);

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "";
			String altMobileNo = eventDto.getAltMobileNo() != null ? eventDto.getAltMobileNo() : "";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			String venue = "";
			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone() != null ? eventDto.getCmpPhone() : "";
			String cmpAddress = eventDto.getCmpAddress() != null ? eventDto.getCmpAddress() : "";
			String cmpEmail = eventDto.getEmail() != null ? eventDto.getEmail() : "";
			String remarks = eventDto.getRemark() != null ? eventDto.getRemark() : "";
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;
			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}
			String reference = eventDto.getReference() != null ? eventDto.getReference() : "";
			
			boolean isFirstFunction = true;
			Table eventTable = null;

			// Create main table with 2 columns for the header layout
			Table catItemTable = null;

			String service = "";
			String theme = "";
			if (lang == 1) {
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
			}

			eventTable = new Table(UnitValue.createPercentArray(new float[] { 32f, 1.5f, 32f, 32f, 1.5f, 32f }));
			eventTable.setWidth(UnitValue.createPercentValue(100));
			eventTable.setMarginBottom(-2f);
			eventTable.setBorder(Border.NO_BORDER);
			eventTable.setBorderBottom(new SolidBorder(blackColor, 1f));

//			ImageData logoData = null;
//			logoData = menuPreparationServiceImpl
//					.loadImageFromResource(environment.getProperty("app.image.url") + eventDto.getLogo());
//			logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");

//			Image logo = new Image(logoData);

			// Resize & align
//			logo.setWidth(UnitValue.createPercentValue(100f));
//			logo.scaleToFit(130f, 130f);
//			logo.setAutoScale(false);
//			logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

//			Color headingColor = new DeviceRgb(115, 99, 67);

//			cell = new Cell(6, 1).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE)
//					.setBorder(Border.NO_BORDER).setPadding(3f);
//
//			if (isCompanyDetails == 1) {
//				eventTable.addCell(cell);
//			}
//
//			cell = new Cell(1, 5)
//					.add(new Paragraph(cmpName.toUpperCase()).setFont(basicFont).setFontSize(20)
//							.setFontColor(headingColor).setTextAlignment(TextAlignment.CENTER).simulateBold())
//					.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
//			if (isCompanyDetails == 1) {
//				eventTable.addCell(cell);
//			}
//
//			cell = new Cell(1, 5)
//					.add(new Paragraph(cmpAddress).setFont(basicFont).setFontSize(8).setFontColor(headingColor)
//							.setTextAlignment(TextAlignment.CENTER))
//					.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
//			if (isCompanyDetails == 1) {
//				eventTable.addCell(cell);
//			}
//
//			cell = new Cell(1, 5)
//					.add(new Paragraph().add(new Text("Email : ")).add(new Text(cmpEmail)).setFont(basicFont)
//							.setFontSize(10).setFontColor(headingColor).setTextAlignment(TextAlignment.CENTER))
//					.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
//			if (isCompanyDetails == 1) {
//				eventTable.addCell(cell);
//			}
//
//			cell = new Cell(1, 5)
//					.add(new Paragraph().add(new Text("Mobile No : ")).add(new Text(cmpPhone))
//							.setFont(basicFont).setFontSize(10).setFontColor(headingColor)
//							.setTextAlignment(TextAlignment.CENTER))
//					.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
//			if (isCompanyDetails == 1) {
//				eventTable.addCell(cell);
//			}

			cell = new Cell(1, 6)
					.add(new Paragraph(cmpName.toUpperCase()).setFont(basicFont).setFontSize(20)
							.setFontColor(blackColor).setTextAlignment(TextAlignment.CENTER).simulateBold())
					.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);
			
			eventTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));
			eventTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));

			cell = new Cell()
					.add(new Paragraph(customerName).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f));
			eventTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f));
			eventTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(hostName).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(new SolidBorder(1f));
			eventTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(customerPhone).setFont(basicFont).setFontSize(14)
							.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(new SolidBorder(1f));
			eventTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(new SolidBorder(1f));
			eventTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(mobileNo).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(new SolidBorder(1f));
			eventTable.addCell(cell);
			
			if(altMobileNo != null && altMobileNo.trim().length() != 0) {
				cell = new Cell()
						.add(new Paragraph(altMobileLabel).setFont(basicFont).setFontSize(14)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setBorder(Border.NO_BORDER).setPadding(0);
				eventTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER).setPadding(0);
				eventTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(altMobileNo).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER).setPadding(0);
				eventTable.addCell(cell);
			}
			
			cell = new Cell()
					.add(new Paragraph(eName).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setPadding(0).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
					.setTextAlignment(TextAlignment.LEFT)).setPadding(0).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(eventName.toUpperCase()).setFont(basicFont).setFontSize(14)
							.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(eDate).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setPadding(0).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell().add(new Paragraph(eventDate).setFont(basicFont).setFontSize(14)
					.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT)).setPadding(0)
					.setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			if (foodNotes != null && foodNotes.trim().length() != 0) {
				cell = new Cell()
						.add(new Paragraph(fNotes).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
						.setTextAlignment(TextAlignment.LEFT)).setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell(1, 4)
						.add(new Paragraph(foodNotes).setFont(basicFont).setFontSize(14).setFontColor(redColor)
								.setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);
			}

			cell = new Cell()
					.add(new Paragraph(eVenue).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setPadding(0).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			cell = new Cell(1, 4)
					.add(new Paragraph(venue.toUpperCase()).setFont(basicFont).setPadding(0).setFontSize(14)
							.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			eventTable.addCell(cell);

			if (reference != null && reference.trim().length() != 0) {
				cell = new Cell()
						.add(new Paragraph(refLabel).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
						.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(reference.toUpperCase()).setFont(basicFont).setPadding(0).setFontSize(14)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);
			}
			
			if (service != null && service.trim().length() != 0) {
				cell = new Cell()
						.add(new Paragraph(serviceLabel).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
						.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(service.toUpperCase()).setFont(basicFont).setPadding(0).setFontSize(14)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);
			}

			if (theme != null && theme.trim().length() != 0) {
				cell = new Cell()
						.add(new Paragraph(themeLabel).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
						.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);

				cell = new Cell(1, 4)
						.add(new Paragraph(theme.toUpperCase()).setFont(basicFont).setPadding(0).setFontSize(14)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				eventTable.addCell(cell);
			}

			if (remarks != null && remarks.trim().length() != 0) {
				cell = new Cell()
						.add(new Paragraph(eventNotes).setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPadding(0).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(1f));
				eventTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(blackColor)
								.setPadding(0).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(1f));
				eventTable.addCell(cell);

				cell = new Cell(1, 4)
						.add(new Paragraph(remarks).setFont(basicFont).setPadding(0).setFontSize(14)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(1f));
				eventTable.addCell(cell);
			}
			
			document.add(eventTable);

			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				if (!isFirstFunction) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
				isFirstFunction = false;
				
				String functionVenue = "";
				String functionNote = "";

				if (lang == 1) {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenueHindi() != null
							? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: "";
					functionNote = eventFunctionMasterResponseDto.getNotesHindi() != null
							? eventFunctionMasterResponseDto.getNotesHindi()
							: "";
				} else if (lang == 2) {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenueGujarati() != null
							? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
							: "";
					functionNote = eventFunctionMasterResponseDto.getNotesGujarati() != null
							? eventFunctionMasterResponseDto.getNotesGujarati()
							: "";
				} else {
					functionVenue = eventFunctionMasterResponseDto.getFunctionVenue() != null
							? eventFunctionMasterResponseDto.getFunctionVenue()
							: "";
					functionNote = eventFunctionMasterResponseDto.getNotesEnglish() != null
							? eventFunctionMasterResponseDto.getNotesEnglish()
							: "";
				}

				/* Function Details */
				Table fnTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
				fnTable.setWidth(UnitValue.createPercentValue(100f));
				fnTable.setBorder(Border.NO_BORDER);
				fnTable.setMarginTop(10f);
				
				cell = new Cell()
						.add(new Paragraph(eventFunctionMasterResponseDto.getFunctionName() + " MENU for "
								+ eventFunctionMasterResponseDto.getPax() + " Persons ").setFont(basicFont)
								.setFontSize(16).setFontColor(blackColor).simulateBold().setTextAlignment(TextAlignment.LEFT))
						.setPadding(0).setBorder(Border.NO_BORDER);
				fnTable.addCell(cell);

				String eventStartTime = eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp().split(" ")[1].substring(0, 5) + " "
								+ eventFunctionMasterResponseDto.getFunctionStartTimestamp().split(" ")[2]
						: "";

				String eventEndTime = eventFunctionMasterResponseDto.getFunctionEndTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionEndTimestamp().split(" ")[1].substring(0, 5) + " "
								+ eventFunctionMasterResponseDto.getFunctionEndTimestamp().split(" ")[2]
						: "";

				cell = new Cell()
						.add(new Paragraph(eTime + " : " + safeText(eventStartTime).toUpperCase()).setFont(basicFont).setFontSize(14)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.RIGHT))
						.setPadding(0).setBorder(Border.NO_BORDER);
				fnTable.addCell(cell);

				document.add(fnTable);
				
				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				// ---- Menu items ----
				for (MenuReportResponseDto menu : menuReportResponseDtos) {
					catItemTable = new Table(UnitValue.createPercentArray(new float[] { 25f, 75f }));
					catItemTable.setWidth(UnitValue.createPercentValue(100));
					catItemTable.setBorder(Border.NO_BORDER);
					catItemTable.setMarginTop(8f);
					catItemTable.setKeepTogether(false);
					catItemTable.setBorderTop(new SolidBorder(1f));
					catItemTable.setBorderBottom(new SolidBorder(1f));

					ISplitCharacters splitAll = new ISplitCharacters() {
						@Override
						public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
							return true;
						}
					};

					Paragraph p = new Paragraph(
							menu.getNameEnglish() != null ? menu.getNameEnglish().toUpperCase() : "").setFont(basicFont)
							.setFontSize(11f).setFontColor(blackColor).setSplitCharacters(splitAll).setMarginLeft(5f)
							.setPadding(0);

					Cell catCell = new Cell().add(p).setVerticalAlignment(VerticalAlignment.TOP)
							.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingTop(0);

					if (isCategoryInstruction == 1 && menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
						catCell.add(new Paragraph(
								menuPreparationServiceImpl.formatText(" ( " + menu.getMenuNotes() + " )", lang))
								.setSplitCharacters(splitAll).setFont(basicFont).setFontSize(8f)
								.setFontColor(blackColor).setMarginLeft(6).setMarginTop(2f)
								.setTextAlignment(TextAlignment.CENTER));
					}

					catItemTable.addCell(catCell).setBorder(Border.NO_BORDER);

					Cell itemsCell = new Cell();
					Boolean isFirst = true;
					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						itemsCell.add(new Paragraph(
								item.getNameEnglish() != null ? item.getNameEnglish().toUpperCase() : "")
								.setFont(basicFont).setFontSize(10f).setFontColor(blackColor).setMarginLeft(10)
								.setPadding(0).setTextAlignment(TextAlignment.LEFT).setPaddingTop(isFirst ? 0f : 7f));

						if (isItemInstruction == 1 && item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
							itemsCell.add(new Paragraph(
									menuPreparationServiceImpl.formatText(" ( " + item.getItemNotes() + " ) ", lang))
									.setFont(basicFont).setSplitCharacters(splitAll).setFontSize(8f)
									.setFixedLeading(8f).setFontColor(blackColor).setMarginLeft(18).setMarginTop(2f)
									.setMarginBottom(0f).setPadding(0f).setTextAlignment(TextAlignment.LEFT));
						}
						isFirst = false;
					}
					catItemTable.addCell(itemsCell.setKeepTogether(true).setBorder(Border.NO_BORDER));
					document.add(catItemTable);
				}
			}

			/* ================= PAGE NUMBERS ================= */
			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 22,
						TextAlignment.CENTER);

				canvas.close();
			}

			int lastPageNum = pdfDocument.getNumberOfPages() + 1;
			Paragraph invisibleContent;

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"back office report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}
}
