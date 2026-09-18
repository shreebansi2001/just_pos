package com.crmportal.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.CashAccountEntity;
import com.crmportal.entity.EventAdvancePaymentEntity;
import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventFunctionQuotationEntity;
import com.crmportal.entity.EventFunctionQuotationPaymentEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.TermsAndConditionFeaturesEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserTermsAndConditionEntity;
import com.crmportal.entity.VendorPaymentEntity;
import com.crmportal.mapper.EventAdvancePaymentMapper;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.BanquetHallShiftBookingRepository;
import com.crmportal.repository.CashAccountRepository;
import com.crmportal.repository.EventAdvancePaymentRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventFunctionQuotationPaymentRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.TermsAndConditionFeaturesRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.UserTermsAndConditionRepository;
import com.crmportal.repository.UserUpgradedModuleRepository;
import com.crmportal.repository.VendorPaymentRepository;
import com.crmportal.request.dto.EventAdvancePaymentRequestDto;
import com.crmportal.response.dto.BanquetHallShiftInfoDto;
import com.crmportal.response.dto.EventAdvancePaymentReportResponseDto;
import com.crmportal.response.dto.EventAdvancePaymentResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventAdvancePaymentService;
import com.crmportal.utility.NumberToWordConverter;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.splitting.ISplitCharacters;

@Service
public class EventAdvancePaymentServiceImpl implements EventAdvancePaymentService {

	@Autowired
	EventAdvancePaymentRepository eventAdvancePaymentRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	EventAdvancePaymentMapper eventAdvancePaymentMapper;

	@Autowired
	BankDetailsRepository bankDetailsRepository;

	@Autowired
	CashAccountRepository cashAccountRepository;

	@Autowired
	EventFunctionQuotationServiceImpl eventFunctionQuotationServiceImpl;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	EventFunctionQuotationPaymentRepository eventFunctionQuotationPaymentRepository;

	@Autowired
	UserUpgradedModuleRepository userUpgradedModuleRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	VendorPaymentRepository vendorPaymentRepository;

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;
	
	@Autowired
	UserTermsAndConditionRepository userTermsRepository;
	
	@Autowired
	private TermsAndConditionFeaturesRepository termsAndConditionFeaturesRepository;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;
	
	@Autowired
	Environment environment;
	
	@Autowired
	BanquetHallShiftBookingRepository banquetHallShiftBookingRepository;
	
	@Override
	@Transactional
	public EventAdvancePaymentResponseDto addOrUpdateEventAdvancePayment(EventAdvancePaymentRequestDto request) {
		 System.out.println("banquetId : " + request.getBanquetHallId());
		userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));

		userMasterRepository.findByIdAndIsDeleteFalse(request.getEntryBy())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getEntryBy()));

		EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId())
				.orElseThrow(() -> new RuntimeException("Event not found with id : " + request.getEventId()));

		EventFunctionMasterEntity eventFunctionMasterEntity = eventFunctionMasterRepository.findByIdAndIsDeleteFalse(request.getEventFunctionId())
				.orElseThrow(() -> new RuntimeException("Event function not found with id : " + request.getEventFunctionId()));
		
		EventAdvancePaymentEntity entity;

		if (request.getId() != -1) {
			entity = eventAdvancePaymentRepository.findById(request.getId())
					.orElseThrow(() -> new RuntimeException("Advance Payment not found : " + request.getId()));

			eventAdvancePaymentMapper.updateEntity(entity, request);
			entity.setUpdateAt(LocalDateTime.now());
		} else {
			entity = eventAdvancePaymentMapper.toEntity(request);
			entity.setIsDelete(false);
		}

		BankDetailsEntity bankDetailsEntity = null;
		if (request.getBankId() != null) {
			bankDetailsEntity = bankDetailsRepository.findByIdAndIsDeleteFalse(request.getBankId())
					.orElseThrow(() -> new RuntimeException("Bank Details Not Found with id : " + request.getBankId()));
			entity.setBankId(bankDetailsEntity.getId());
			entity.setCashId(null);
		} else {
			entity.setBankId(null);
		}

		CashAccountEntity cashAccountEntity = null;
		if (request.getCashId() != null) {
			cashAccountEntity = cashAccountRepository.findByIdAndIsDeleteFalse(request.getCashId())
					.orElseThrow(() -> new RuntimeException("Cash Account not found with id : " + request.getCashId()));
			entity.setCashId(cashAccountEntity.getId());
			entity.setBankId(null);
		} else {
			entity.setCashId(null);
		}

		entity = eventAdvancePaymentRepository.save(entity);

		EventFunctionQuotationEntity quotation = eventFunctionQuotationServiceImpl
				.getOrCreateQuotation(eventMasterEntity, false);

		EventFunctionQuotationPaymentEntity quotationPayment = saveOrUpdateQuotationPayment(entity, quotation,
				eventMasterEntity, bankDetailsEntity, cashAccountEntity);

		saveOrUpdateVendorPayment(quotationPayment, entity, eventMasterEntity, bankDetailsEntity, cashAccountEntity);

		EventAdvancePaymentResponseDto response = eventAdvancePaymentMapper.toResponseDto(entity);
		response.setEntryByName(userMasterRepository.getUserNameById(entity.getEntryBy()));

		return response;
	}

	@Override
	public List<EventAdvancePaymentResponseDto> getAllEventAdvancePaymentList(Long eventId) {

		List<EventAdvancePaymentResponseDto> responseList = eventAdvancePaymentMapper.toResponseDtoList(
				eventAdvancePaymentRepository.findByEventIdAndIsDeleteFalseOrderByPaymentDateDesc(eventId));

		responseList.forEach(
				response -> response.setEntryByName(userMasterRepository.getUserNameById(response.getEntryBy())));

		return responseList;
	}

	@Override
	public EventAdvancePaymentResponseDto getEventAdvancePaymentById(Long id) {
		EventAdvancePaymentEntity entity = eventAdvancePaymentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Event Advance Payment not found with id : " + id));

		EventAdvancePaymentResponseDto response = eventAdvancePaymentMapper.toResponseDto(entity);
		response.setEntryByName(userMasterRepository.getUserNameById(entity.getEntryBy()));

		return response;
	}

	@Override
	@Transactional
	public Boolean deleteEventAdvancePaymentById(Long id) {

		EventAdvancePaymentEntity advancePayment = eventAdvancePaymentRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Event Advance Payment not found with id : " + id));

		EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(advancePayment.getEventId())
				.orElseThrow(() -> new RuntimeException("Event not found with id : " + advancePayment.getEventId()));

		EventFunctionQuotationPaymentEntity quotationPayment = eventFunctionQuotationPaymentRepository
				.findByModuleIdAndModuleNameAndEventAndIsDeleteFalse(advancePayment.getId(), "ADVANCE PAYMENT", event)
				.orElse(null);

		if (quotationPayment != null) {
			BankDetailsEntity bank = null;
			if (quotationPayment.getBankId() != null) {
				bank = bankDetailsRepository.findByIdAndIsDeleteFalse(quotationPayment.getBankId()).orElse(null);
			}

			CashAccountEntity cash = null;
			if (quotationPayment.getCashAccountId() != null) {
				cash = cashAccountRepository.findByIdAndIsDeleteFalse(quotationPayment.getCashAccountId()).orElse(null);
			}

			BigDecimal amount = quotationPayment.getAdvancePayment() == null ? BigDecimal.ZERO
					: new BigDecimal(quotationPayment.getAdvancePayment());

			eventFunctionQuotationServiceImpl.updateAccountBalance(bank, cash, BigDecimal.ZERO, amount);

			if (quotationPayment.getVendorCode() != null && !quotationPayment.getVendorCode().trim().isEmpty()) {
				VendorPaymentEntity vendorPayment = vendorPaymentRepository
						.findByInvoiceCodeAndVendorIdAndIsDeleteFalse(quotationPayment.getVendorCode(),
								event.getParty().getId());

				if (vendorPayment != null) {
					vendorPayment.setIsDelete(true);
					vendorPayment.setUpdatedAt(LocalDateTime.now());

					vendorPaymentRepository.save(vendorPayment);
				}
			}

			quotationPayment.setIsDelete(true);
			quotationPayment.setUpdatedAt(LocalDateTime.now());

			eventFunctionQuotationPaymentRepository.save(quotationPayment);
		}

		advancePayment.setIsDelete(true);
		advancePayment.setUpdateAt(LocalDateTime.now());

		eventAdvancePaymentRepository.save(advancePayment);

		return true;
	}

	private EventFunctionQuotationPaymentEntity saveOrUpdateQuotationPayment(EventAdvancePaymentEntity advancePayment,
			EventFunctionQuotationEntity quotation, EventMasterEntity event, BankDetailsEntity bankDetailEntity,
			CashAccountEntity cashAccountEntity) {

		EventFunctionQuotationPaymentEntity quotationPayment = eventFunctionQuotationPaymentRepository
				.findByModuleIdAndModuleNameAndEventAndIsDeleteFalse(advancePayment.getId(), "ADVANCE PAYMENT", event)
				.orElse(new EventFunctionQuotationPaymentEntity());

		boolean isNew = quotationPayment.getId() == null;

		BigInteger oldAdvancePayment = quotationPayment.getAdvancePayment();
		Long oldBankId = quotationPayment.getBankId();
		Long oldCashAccountId = quotationPayment.getCashAccountId();

		quotationPayment.setModuleId(advancePayment.getId());
		quotationPayment.setModuleName("ADVANCE PAYMENT");

		quotationPayment.setAdvancePaymentDate(
				advancePayment.getPaymentDate() != null ? advancePayment.getPaymentDate().atStartOfDay() : null);

		quotationPayment.setAdvancePaymentNotes(advancePayment.getRemark());

		quotationPayment.setAdvancePayment(
				advancePayment.getAmount() != null ? advancePayment.getAmount().toBigInteger() : BigInteger.ZERO);

		quotationPayment.setPaymentMode(advancePayment.getPaymentMode());

		quotationPayment.setBankId(bankDetailEntity == null ? null : bankDetailEntity.getId());

		quotationPayment.setCashAccountId(cashAccountEntity == null ? null : cashAccountEntity.getId());

		quotationPayment.setEventFunctionQuotation(quotation);
		quotationPayment.setEvent(event);

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(advancePayment.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + advancePayment.getUserId()));

		quotationPayment.setUser(user);

		quotationPayment = eventFunctionQuotationPaymentRepository.save(quotationPayment);

		BigDecimal oldAmount = oldAdvancePayment == null ? BigDecimal.ZERO : new BigDecimal(oldAdvancePayment);

		BigDecimal newAmount = advancePayment.getAmount() == null ? BigDecimal.ZERO : advancePayment.getAmount();

		if (isNew) {
			eventFunctionQuotationServiceImpl.updateAccountBalance(bankDetailEntity, cashAccountEntity, newAmount,
					BigDecimal.ZERO);

		} else {

			boolean accountChanged = !Objects.equals(oldBankId, quotationPayment.getBankId())
					|| !Objects.equals(oldCashAccountId, quotationPayment.getCashAccountId());

			if (accountChanged) {
				if (oldBankId != null) {
					BankDetailsEntity oldBank = bankDetailsRepository.findByIdAndIsDeleteFalse(oldBankId).orElse(null);

					eventFunctionQuotationServiceImpl.updateAccountBalance(oldBank, null, BigDecimal.ZERO, oldAmount);
				}

				if (oldCashAccountId != null) {
					CashAccountEntity oldCash = cashAccountRepository.findByIdAndIsDeleteFalse(oldCashAccountId)
							.orElse(null);

					eventFunctionQuotationServiceImpl.updateAccountBalance(null, oldCash, BigDecimal.ZERO, oldAmount);
				}

				eventFunctionQuotationServiceImpl.updateAccountBalance(bankDetailEntity, cashAccountEntity, newAmount,
						BigDecimal.ZERO);
			} else {
				if (newAmount.compareTo(oldAmount) > 0) {
					BigDecimal difference = newAmount.subtract(oldAmount);

					eventFunctionQuotationServiceImpl.updateAccountBalance(bankDetailEntity, cashAccountEntity,
							difference, BigDecimal.ZERO);

				} else if (oldAmount.compareTo(newAmount) > 0) {
					BigDecimal difference = oldAmount.subtract(newAmount);

					eventFunctionQuotationServiceImpl.updateAccountBalance(bankDetailEntity, cashAccountEntity,
							BigDecimal.ZERO, difference);
				}
			}
		}

		return quotationPayment;
	}

	private void saveOrUpdateVendorPayment(EventFunctionQuotationPaymentEntity quotationPayment,
			EventAdvancePaymentEntity advancePayment, EventMasterEntity eventMaster,
			BankDetailsEntity bankDetailsEntity, CashAccountEntity cashAccountEntity) {

		if (!userUpgradedModuleRepository
				.existsByUserIdAndUpgradeModuleIdAndIsActiveTrueAndIsDeleteFalse(advancePayment.getUserId(), 1L)) {
			return;
		}

		VendorPaymentEntity vendorPayment;

		if (quotationPayment.getVendorCode() != null) {

			vendorPayment = vendorPaymentRepository.findByInvoiceCodeAndVendorIdAndIsDeleteFalse(
					quotationPayment.getVendorCode(), eventMaster.getParty().getId());

			vendorPayment.setReceivedAmount(advancePayment.getAmount());
			vendorPayment.setRemarks(advancePayment.getRemark());
			vendorPayment.setPaymentDate(advancePayment.getPaymentDate());
			vendorPayment.setBankId(bankDetailsEntity == null ? null : bankDetailsEntity.getId());
			vendorPayment.setCashAccountId(cashAccountEntity == null ? null : cashAccountEntity.getId());
			vendorPayment.setPaymentMode(advancePayment.getPaymentMode());

		} else {

			UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(advancePayment.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found : " + advancePayment.getUserId()));

			vendorPayment = new VendorPaymentEntity();

			vendorPayment.setEventId(eventMaster.getId());
			vendorPayment.setUser(user);

			vendorPayment.setInvoiceCode(commonService.generateVendorInvoiceCode(user.getId()));

			vendorPayment.setVendorId(eventMaster.getParty().getId());
			vendorPayment.setVendorCat("User");

			vendorPayment.setIsOpb(false);
			vendorPayment.setIsPayable(false);

			vendorPayment.setPaymentDate(advancePayment.getPaymentDate());

			vendorPayment.setReceivedAmount(advancePayment.getAmount());

			vendorPayment.setPayAmount(BigDecimal.ZERO);
			vendorPayment.setSettlementAmount(BigDecimal.ZERO);

			vendorPayment.setReferenceId(advancePayment.getReferenceId());

			vendorPayment.setRemarks(advancePayment.getRemark());

			vendorPayment.setBankId(bankDetailsEntity == null ? null : bankDetailsEntity.getId());

			vendorPayment.setCashAccountId(cashAccountEntity == null ? null : cashAccountEntity.getId());

			vendorPayment.setPaymentMode(advancePayment.getPaymentMode());

			vendorPayment = vendorPaymentRepository.save(vendorPayment);

			quotationPayment.setVendorCode(vendorPayment.getInvoiceCode());

			eventFunctionQuotationPaymentRepository.save(quotationPayment);
		}

		vendorPaymentRepository.save(vendorPayment);
	}

	@Override
	public String generateReport(Long advancePaymentId, Long userId, Long eventId, HttpServletRequest re,Boolean isTermsCond,
			Long eventFunctionId) {
		try {
			PdfFont basicFont = null;
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "", l1 = "",
					l2 = "", note = "", eContact = "", eventFlow = "", function = "", person = "", eTime = "",
					date = "", rate = "", party = "", eventNotes = "", billingNameLabel = "", serviceLabel = "",
					themeLabel = "";
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

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
			billingNameLabel = "Billing Name";
			serviceLabel = "Service";
			themeLabel = "Theme";

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			Object[] result = eventAdvancePaymentRepository.getEventAdvancePaymentReport(advancePaymentId);

			if(result == null || result.length == 0) {
				return "Data not found.";
			}
			
			List<Object[]> shifts = eventAdvancePaymentRepository.findHallShiftsByEventFunctionId(eventFunctionId);
			
			if(shifts == null || shifts.isEmpty()) {
				return "Shifts not found.";
			}
			System.out.println("shifts size : " + shifts.size());
			Object[] row = (Object[]) result[0];
			EventAdvancePaymentReportResponseDto response = mapToEventAdvancePaymentReportDto(row);
			
			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + response.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(outputPath + "/" + menuPreparationServiceImpl.getReportName(menuPreparationServiceImpl.getPartyNameByEventId(eventId),
					"", "advance payment receipt") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, PageSize.A5, false);
			document.setMargins(20, 20, 50, 20);

			// Load all background images upfront
			ImageData mainBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
			ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			Color blackColor = new DeviceRgb(0, 0, 0);

			Cell cell;

			String hostName = response.getPartyName() != null ? response.getPartyName() : "Not Specified";
			String eventDate = response.getEventDate() == "" ? "" : response.getEventDate();
			String eventNo = response.getEventNo() == null ? "" : response.getEventNo();
			String eventName = response.getEventName() == null ? "" : response.getEventName();
			String cmpName = response.getCmpName();
			String cmpPhone = response.getCmpContactNo() != null ? response.getCmpContactNo() : "";
			String cmpAddress = response.getCmpAddress() != null ? response.getCmpAddress() : "";
			String cmpEmail = response.getCmpEmail() != null ? response.getCmpEmail() : "";
			BigDecimal amount = response.getAmount() != null ? response.getAmount() : BigDecimal.ZERO;
			String paymentMode = response.getPaymentMode() != null ? response.getPaymentMode() : "";
			String accountName = response.getPaymentMode() == "CASH" ? response.getCashName() : response.getBankName();
			String referenceId = response.getReferenceId() != null ? response.getReferenceId() : "";
			String remarks = response.getRemark() != null ? response.getRemark() : "";

		    Object[] result1 = shifts.get(0);
		    System.out.println("result1 : " + result1);
		    String venue = result1[0] != null ? result1[0].toString() : "";
		    String shift = result1[1] != null ? result1[1].toString() : "";
		    String session = result1[2] != null ? (String)result1[2] : "";
			
			boolean isFirstFunction = true;
			Table headerTable = new Table(UnitValue.createPercentArray(new float[] {70f, 30f}));

			headerTable.setWidth(UnitValue.createPercentValue(100f));
			headerTable.setFixedLayout();
			
			cell = new Cell().add(new Paragraph("RECEIPT"))
					.setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER)
					.simulateBold()
					.setFontSize(16f)
					.setBorder(Border.NO_BORDER).setPadding(0);
			headerTable.addCell(cell);
			
			ImageData logoData = null;
			logoData = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + response.getLogo());
//			logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");

			Image logo = new Image(logoData);
			logo.scaleToFit(120, 120);
			logo.setAutoScale(false);
			logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
			
			cell = new Cell().add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setBorder(Border.NO_BORDER).setPadding(3f);
			
			headerTable.addCell(cell);
			
			cell = new Cell(1, 2).add(new Paragraph("DATE : " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
					.setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.RIGHT)
					.setFontSize(12f)
					.simulateBold()
					.setBorder(Border.NO_BORDER).setPadding(0);
			headerTable.addCell(cell);
			
			document.add(headerTable);
			
			Table detailsTable = new Table(UnitValue.createPercentArray(new float[] {30f, 70f}));
			detailsTable.setWidth(UnitValue.createPercentValue(100f));
			detailsTable.setFixedLayout();
			System.out.println("venue : " + venue);
			System.out.println("session time : " + shift);
			addRow(detailsTable, "Received with Thanks", hostName, blackColor);
			addRow(detailsTable, "A sum of Rupees", String.valueOf(amount.intValue()), blackColor);
			addRow(detailsTable, "In words", NumberToWordConverter.convert(amount.longValue()), blackColor);
			addRow(detailsTable, "Event No", eventNo, blackColor);
			addRow(detailsTable, "Event Date", eventDate, blackColor);
			addRow(detailsTable, "Venue ", venue + " (" + session + ") " + " ("+shift+") ", blackColor);
			addRow(detailsTable, "Payment Mode", paymentMode, blackColor);
			addRow(detailsTable, "Transaction Id", referenceId, blackColor);
			addRow(detailsTable, "Remarks", remarks, blackColor);
			
			document.add(detailsTable);

			
			if (Boolean.TRUE.equals(isTermsCond)) {

			    UserTermsAndConditionEntity terms =
			            userTermsRepository.findByUserIdAndNameEnglishAndIsDeleteFalseAndIsActiveTrue(
			                    userId, "Advance Receipts");

			    Paragraph title = new Paragraph("TERMS & CONDITIONS FOR BANQUET BOOKINGS")
			            .setFont(basicFont)
			            .simulateBold()
			            .setFontSize(12)
			            .setTextAlignment(TextAlignment.CENTER)
			            .setMarginBottom(5)
			            .setMarginTop(15f);

			    document.add(title);

			    if (terms == null) {

			        document.add(
			                new Paragraph("No Terms & Conditions available.")
			                        .setFont(basicFont)
			                        .setFontSize(11)
			                        .setFontColor(ColorConstants.GRAY)
			        );

			    } else {

			        List<TermsAndConditionFeaturesEntity> features =
			                termsAndConditionFeaturesRepository
			                        .findByUserTermsConditionIdAndIsDeleteFalse(terms.getId());

			        if (features == null || features.isEmpty()) {

			            document.add(
			                    new Paragraph("No Terms & Conditions available.")
			                            .setFont(basicFont)
			                            .setFontSize(11)
			                            .setFontColor(ColorConstants.GRAY)
			            );

			        } else {

			            int index = 1;

			            for (TermsAndConditionFeaturesEntity feature : features) {

			                // Use English description if available
			                String desc = feature.getDescription();

			                // Fallback to Hindi if English is null/empty
			                if (desc == null || desc.trim().isEmpty()) {
			                    desc = feature.getDescriptionHindi();
			                }

			                Paragraph term = new Paragraph(index + ". " + desc)
			                        .setFont(basicFont)
			                        .setFontSize(10)
			                        .setTextAlignment(TextAlignment.JUSTIFIED)
			                        .setMarginBottom(1)
			                        .setKeepTogether(true);

			                document.add(term);

			                index++;
			            }
			        }
			    }
			}
			
			
			/* ================= PAGE NUMBERS ================= */
			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {
			    PdfPage page = pdfDocument.getPage(i);
			    Rectangle pageSize = page.getPageSize();
			    PdfCanvas pdfCanvas = new PdfCanvas(page);
			    Canvas canvas = new Canvas(pdfCanvas, pageSize);

			    // ── Page number (center footer) ──────────────────────────────
			    canvas.showTextAligned(
			        "Page " + i + " of " + totalPages,
			        pageSize.getWidth() / 2,
			        22,
			        TextAlignment.CENTER
			    );

			    if (i == totalPages) {
			        float tableWidth = pageSize.getWidth() - 80; // left+right margin = 80
			        float tableX = 40;  // left margin
			        float tableY = 60;  // height from bottom of page

			        Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
			        signatureTable.setWidth(tableWidth);

			        Cell leftCell = new Cell()
			                .add(new Paragraph("___________________")
			                        .setMarginBottom(0)
			                        .setTextAlignment(TextAlignment.LEFT))
			                .add(new Paragraph("Authorized Signature")
			                        .simulateBold()
			                        .setFontSize(10)
			                        .setMarginTop(0)
			                        .setTextAlignment(TextAlignment.LEFT))
			                .setBorder(Border.NO_BORDER);

			        Cell rightCell = new Cell()
			                .add(new Paragraph("___________________")
			                        .setMarginBottom(0)
			                        .setTextAlignment(TextAlignment.RIGHT))
			                .add(new Paragraph("Guest Signature")
			                        .simulateBold()
			                        .setFontSize(10)
			                        .setMarginTop(0)
			                        .setTextAlignment(TextAlignment.RIGHT))
			                .setBorder(Border.NO_BORDER);

			        signatureTable.addCell(leftCell);
			        signatureTable.addCell(rightCell);

			        // Render table at fixed position using Canvas
			        canvas.add(signatureTable.setFixedPosition(tableX, tableY, tableWidth));
			    }

			    canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + response.getEventNo()
					+ "/" + menuPreparationServiceImpl.getReportName(menuPreparationServiceImpl.getPartyNameByEventId(eventId), 
							"",
							"advance payment receipt")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}
	
	private void addRow(Table table, String label, String value, Color borderColor) {
		if(value != null && value.trim().length() != 0) {
			Cell labelCell = new Cell().add(new Paragraph(label))
					.setBorder(Border.NO_BORDER).setPadding(0)
					.setFontSize(10f)
					.setTextAlignment(TextAlignment.LEFT).simulateBold()
					.setPaddingLeft(5f);
			table.addCell(labelCell);
	
			Cell valueCell = new Cell().add(new Paragraph(value))
					.setBorder(Border.NO_BORDER).setPadding(0).setTextAlignment(TextAlignment.LEFT)
					.setFontSize(10f)
					.setBorderBottom(new SolidBorder(borderColor, 1f));
			table.addCell(valueCell);
		}
	}
	private EventAdvancePaymentReportResponseDto mapToEventAdvancePaymentReportDto(Object[] row) {
		EventAdvancePaymentReportResponseDto dto = new EventAdvancePaymentReportResponseDto();

		dto.setEventId(row[0] != null ? ((Number) row[0]).longValue() : null);
		dto.setEventNo((String) row[1]);
		dto.setEventName((String) row[2]);
		dto.setEventDate((String) row[3]);

		dto.setAdvancePaymentId(row[4] != null ? ((Number) row[4]).longValue() : null);
		dto.setPaymentDate((String) row[5]);

		dto.setAmount((BigDecimal) row[6]);
		dto.setPaymentMode((String) row[7]);

		dto.setEntryBy(row[8] != null ? ((Number) row[8]).longValue() : null);
		dto.setEntryByName((String) row[9]);

		dto.setRemark((String) row[10]);
		dto.setReferenceId((String) row[11]);

		dto.setUserId(row[12] != null ? ((Number) row[12]).longValue() : null);

		dto.setCashId(row[13] != null ? ((Number) row[13]).longValue() : null);
		dto.setCashName((String) row[14]);

		dto.setBankId(row[15] != null ? ((Number) row[15]).longValue() : null);
		dto.setBankName((String) row[16]);

		dto.setLogo(row[17] != null ? (String)row[17] : "");
		dto.setCmpName(row[18] != null ? (String)row[18] : "");
		dto.setCmpEmail(row[19] != null ? (String)row[19] : "");
		dto.setCmpContactNo(row[20] != null ? (String)row[20] : "");
		dto.setCmpAddress(row[21] != null ? (String)row[21] : "");
		
		dto.setVenue(row[22] != null ? (String) row[22] : "");
		dto.setPartyName(row[23] != null ? (String) row[23] : "");
		
		dto.setShift(row[24] != null ? (String) row[24] : "");
		
		return dto;
	}
	
}
