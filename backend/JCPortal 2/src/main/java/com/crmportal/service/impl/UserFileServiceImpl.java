package com.crmportal.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.BanquetHallImageEntity;
import com.crmportal.entity.CatBgSelectionEntity;
import com.crmportal.entity.ConfigurationUtilEntity;
import com.crmportal.entity.DecoreMainCategoryItemImagesMasterEntity;
import com.crmportal.entity.DecoreMainCategoryItemMasterEntity;
import com.crmportal.entity.DecoreMainCategoryMasterEntity;
import com.crmportal.entity.EventFunctionDecorItemImagesEntity;
import com.crmportal.entity.EventFunctionManagerTaskImagesEntity;
import com.crmportal.entity.EventFunctionStaffUploadEntity;
import com.crmportal.entity.EventLabourChecklistEntity;
import com.crmportal.entity.EventLabourChecklistPhotosEntity;
import com.crmportal.entity.ExpenseDetailEntity;
import com.crmportal.entity.ExpenseManagementEntity;
import com.crmportal.entity.FontMasterEntity;
import com.crmportal.entity.InvoiceEntity;
import com.crmportal.entity.LaborHelperEntity;
import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.OfficeExpenseDocEntity;
import com.crmportal.entity.OfficeExpenseEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.PaymentInfo;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.RefundDetailsEntity;
import com.crmportal.entity.SpecialNotesImagesEntity;
import com.crmportal.entity.TemplateMasterEntity;
import com.crmportal.entity.TicketEntity;
import com.crmportal.entity.UserAmcEntity;
import com.crmportal.entity.UserBasicFileEntity;
import com.crmportal.entity.UserDocumentInfoEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.VenueMasterEntity;
import com.crmportal.enums.ModuleName;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.BanquetHallImageRepository;
import com.crmportal.repository.CatBgSelectionRepository;
import com.crmportal.repository.ConfigurationUtilEntityRepository;
import com.crmportal.repository.DecoreMainCategoryItemImagesMasterRepository;
import com.crmportal.repository.DecoreMainCategoryMasterRepository;
import com.crmportal.repository.EventFunctionDecorItemImagesRepository;
import com.crmportal.repository.EventFunctionManagerTaskImagesRepository;
import com.crmportal.repository.EventFunctionStaffUploadRepository;
import com.crmportal.repository.EventLabourCheckListImagesRepository;
import com.crmportal.repository.EventLabourCheckListRepository;
import com.crmportal.repository.ExpenseDetailRepository;
import com.crmportal.repository.ExpenseManagementRepository;
import com.crmportal.repository.FontMasterRepository;
import com.crmportal.repository.InvoiceEntityRepository;
import com.crmportal.repository.LaborHelperRepository;
import com.crmportal.repository.MenuCategoryMasterRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.OfficeExpenseDocRepository;
import com.crmportal.repository.OfficeExpenseRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.PaymentInfoRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.RefundDetailsRepository;
import com.crmportal.repository.SpecialNotesImagesRepository;
import com.crmportal.repository.TemplateMasterRepository;
import com.crmportal.repository.TicketRepository;
import com.crmportal.repository.UserAmcRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserBasicFileRepository;
import com.crmportal.repository.UserDocumentInfoRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.VenueMasterRepository;
import com.crmportal.service.UserFileService;

import net.bytebuddy.implementation.bytecode.Throw;

@Service
public class UserFileServiceImpl implements UserFileService {

	@Autowired
	UserMasterRepository masterRepository;

	@Autowired
	private PartyMasterRepository partyMasterRepository;

	@Autowired
	MenuCategoryMasterRepository menuCategoryMasterRepository;

	@Autowired
	MenuItemMasterRepository menuItemMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	UserBasicDetailsMasterRepository userBasicDetailsMasterRepository;

	@Autowired
	UserDocumentInfoRepository userDocumentInfoRepository;

	@Autowired
	LaborHelperRepository laborHelperRepository;

	@Autowired
	PaymentInfoRepository infoRepository;

	@Autowired
	TicketRepository ticketRepository;

	@Autowired
	TemplateMasterRepository templateMasterRepository;

	@Autowired
	UserAmcRepository amcRepository;

	@Autowired
	RefundDetailsRepository detailsRepository;

	@Autowired
	UserBasicFileRepository basicFileRepository;

	@Autowired
	ExpenseManagementRepository expenseManagementRepository;

	@Autowired
	ExpenseDetailRepository expenseDetailRepository;

	@Autowired
	OfficeExpenseRepository officeExpenseRepository;

	@Autowired
	private ConfigurationUtilEntityRepository configurationUtilEntityRepository;

	@Autowired
	RawMaterialMasterRepository rawMaterialMasterRepository;

	@Autowired
	InvoiceEntityRepository invoiceRepository;

	@Autowired
	FontMasterRepository fontMasterRepository;

	@Autowired
	EventLabourCheckListRepository eventLabourCheckListRepository;

	@Autowired
	EventLabourCheckListImagesRepository eventLabourCheckListImagesRepository;

	@Autowired
	BankDetailsRepository bankDetailsRepository;

	@Autowired
	CatBgSelectionRepository catBgSelectionRepository;

	@Autowired
	EventFunctionStaffUploadRepository eventFunctionStaffUploadRepository;

	@Autowired
	DecoreMainCategoryMasterRepository decoreMainCategoryMasterRepository;

	@Autowired
	DecoreMainCategoryItemImagesMasterRepository decoreMainCategoryItemImagesMasterRepository;

	@Autowired
	SpecialNotesImagesRepository specialNotesImagesRepository;

	@Autowired
	EventFunctionManagerTaskImagesRepository eventFunctionManagerTaskImagesRepository;

	@Autowired
	EventFunctionDecorItemImagesRepository eventFunctionDecorItemImagesRepository;

	@Value("${app.image.path}")
	private String uploadRoot;

	@Value("${app.image.url:}")
	private String baseUrl;

	@Autowired
	BanquetHallImageRepository banquetHallImageRepository;

	@Autowired
	OfficeExpenseDocRepository officeExpenseDocRepository;

	@Autowired
	VenueMasterRepository venueMasterRepository;

	private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
			// Images
			".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg", ".tiff",

			// Documents
			".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt", ".rtf", ".csv",

			// Audio
			".mp3", ".wav", ".ogg", ".aac", ".flac", ".m4a",

			// Video
			".mp4", ".avi", ".mkv", ".mov", ".wmv", ".webm",

			// Archives
			".zip", ".rar", ".7z", ".tar", ".gz",

			// Fonts
			".ttf", ".otf", ".woff", ".woff2",

			// Misc
			".json", ".xml");

	private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
			// Images
			"image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp", "image/svg+xml", "image/tiff",

			// Documents
			"application/pdf", "application/msword",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.ms-excel",
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-powerpoint",
			"application/vnd.openxmlformats-officedocument.presentationml.presentation", "text/plain", "text/csv",
			"application/rtf",

			// Audio
			"audio/mpeg", "audio/wav", "audio/ogg", "audio/aac", "audio/flac", "audio/mp4",

			// Video
			"video/mp4", "video/x-msvideo", "video/x-matroska", "video/quicktime", "video/x-ms-wmv", "video/webm",

			// Archives
			"application/zip", "application/x-rar-compressed", "application/x-7z-compressed", "application/x-tar",
			"application/gzip",

			// Fonts
			"font/ttf", "font/otf", "font/woff", "font/woff2",

			// Misc
			"application/json", "application/xml", "text/xml",

			// Generic fallback (use carefully)
			"application/octet-stream", "multipart/form-data");

	@Transactional
	public Map<String, Object> storeFile(Long userId, String moduleName, Long moduleRecordId, String fileType,
			MultipartFile file) throws IOException {

		Map<String, Object> response = new HashMap<>();
		Path newTargetLocation = null;

		try {
			validateFile(file);
			validateModuleName(moduleName);
			if (uploadRoot.startsWith("http://") || uploadRoot.startsWith("https://")) {
				throw new IllegalStateException(
						"app.image.path must be a local file system path, not a URL. Current value: " + uploadRoot);
			}

			UserMasterEntity user = null;
			EventLabourChecklistEntity checklistEntity;
			if (moduleName.equalsIgnoreCase("CHECKLIST")) {
				checklistEntity = eventLabourCheckListRepository.findByIdAndIsDeleteFalse(userId)
						.orElseThrow(() -> new RuntimeException("Event Labour CheckList not found with id: " + userId));
				user = userMasterRepository.findByIdAndIsDeleteFalse(checklistEntity.getUserId())
						.orElseThrow(() -> new Exception("User not found with ID: " + checklistEntity.getUserId()));
			} else {
				user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
						.orElseThrow(() -> new Exception("User not found with ID: " + userId));
			}

			String safeEmail = sanitize(user.getEmail());

			// USE ENUM PROPERLY
			ModuleName module = ModuleName.fromString(moduleName);
			String moduleDir = module.name();

			Path directory = Paths.get(uploadRoot, safeEmail, moduleDir).normalize();

			if (!directory.startsWith(Paths.get(uploadRoot).normalize())) {
				throw new IOException("Invalid upload path");
			}

			Files.createDirectories(directory);

			String originalFileName = file.getOriginalFilename();
			String extension = getFileExtension(originalFileName);
			validateFileType(extension, file.getContentType());

			String safeFileName = UUID.randomUUID().toString() + extension;
			newTargetLocation = directory.resolve(safeFileName);

			String dbPath = "/jcupload/" + safeEmail + "/" + moduleDir + "/" + safeFileName;

			// COPY FILE ONCE
			Files.copy(file.getInputStream(), newTargetLocation, StandardCopyOption.REPLACE_EXISTING);

			// SWITCH ON ENUM (NOT STRING)
			switch (module) {

			case PARTY:
				PartyMasterEntity party = partyMasterRepository.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new Exception("Party Master not found"));
				deleteOldFile(party.getDocPath());
				party.setDocPath(dbPath);
				partyMasterRepository.save(party);
				break;

			case MENUCATEGORY:
				MenuCategoryMasterEntity menuCat = menuCategoryMasterRepository.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new Exception("Menu Category not found"));
				deleteOldFile(menuCat.getImagePath());
				menuCat.setImagePath(dbPath);
				menuCategoryMasterRepository.save(menuCat);
				break;

			case MENUITEM:
				MenuItemMasterEntity menuItem = menuItemMasterRepository.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new Exception("Menu Item not found"));
				deleteOldFile(menuItem.getImagePath());
				menuItem.setImagePath(dbPath);
				menuItemMasterRepository.save(menuItem);
				break;

			case USERLOGO:
				UserMasterEntity u = userMasterRepository.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new Exception("User not found"));
				deleteOldFile(u.getLogo());
				u.setLogo(dbPath);
				userMasterRepository.save(u);
				break;

			case TICKET:
				TicketEntity ticket = ticketRepository.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new Exception("Ticket not found"));
				deleteOldFile(ticket.getDocumentpath());
				ticket.setDocumentpath(dbPath);
				ticketRepository.save(ticket);
				break;

			case UTILITY:
				ConfigurationUtilEntity utility = configurationUtilEntityRepository
						.findByIdAndIsDeleteFalse(moduleRecordId).orElseThrow(() -> new Exception("Utility not found"));
				deleteOldFile(utility.getBgImage());
				utility.setBgImage(dbPath);
				configurationUtilEntityRepository.save(utility);
				break;

			case RAWMATERIAL:
				RawMaterialMasterEntity entity = rawMaterialMasterRepository.findByIdAndIsDeleteFalse(moduleRecordId);
				if (entity == null) {
					throw new RuntimeException("Rawmaterial not found");
				}
				deleteOldFile(entity.getFile());
				entity.setFile(dbPath);
				rawMaterialMasterRepository.save(entity);
				break;

			case REPORTPAGE:
				TemplateMasterEntity template = templateMasterRepository.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new RuntimeException("Template not found"));
				switch (fileType.toUpperCase()) {
				case "NAMEPLATE_PAGE":
					deleteOldFile(template.getNamePlateBg());
					template.setNamePlateBg(dbPath);
					break;
				case "NAMEPLATE_COVER_PAGE":
					deleteOldFile(template.getNamePlateCoverBg());
					template.setNamePlateCoverBg(dbPath);
					break;
				case "FRONT_PAGE":
					deleteOldFile(template.getFrontPage());
					template.setFrontPage(dbPath);
					break;
				case "DETAILS_PAGE":
					deleteOldFile(template.getSecondFrontPage());
					template.setSecondFrontPage(dbPath);
					break;
				case "WATERMARK":
					deleteOldFile(template.getWatermark());
					template.setWatermark(dbPath);
					break;
				case "LAST_PAGE":
					deleteOldFile(template.getLastMainPage());
					template.setLastMainPage(dbPath);
					break;
				case "CATEGORY_BG_PAGE":
					deleteOldFile(template.getCatBgPage());
					template.setCatBgPage(dbPath);
					break;
				case "EXTRA_PAGE":
					deleteOldFile(template.getExtraPage());
					template.setExtraPage(dbPath);
					break;
				case "OTHER":
					deleteOldFile(template.getDummyPdf());
					template.setDummyPdf(dbPath);
					break;
				default:
					throw new IllegalArgumentException("Invalid fileType: " + fileType);
				}
				templateMasterRepository.save(template);
				break;

			case USERBASICFILE:
				UserBasicFileEntity basicFile;
				if (moduleRecordId == 0) {
					basicFile = new UserBasicFileEntity();
					basicFile.setUserId(userId);
					basicFile.setModuleType(fileType);
				} else {
					basicFile = basicFileRepository.findByIdAndIsDeleteFalse(moduleRecordId)
							.orElseThrow(() -> new RuntimeException("UserBasicFile not found"));
					deleteOldFile(basicFile.getFile());
				}
				basicFile.setFile(dbPath);
				basicFileRepository.save(basicFile);
				break;

			case LABOR_HELPER:
				LaborHelperEntity laborhelper;
				laborhelper = laborHelperRepository.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new RuntimeException("Labor Helper not found"));

				if (fileType != null) {
					switch (fileType.toUpperCase()) {
					case "AADHAR_FRONT":
						deleteOldFile(laborhelper.getAadharcarddocpathfront());
						laborhelper.setAadharcarddocpathfront(dbPath);
						break;
					case "AADHAR_BACK":
						deleteOldFile(laborhelper.getAadharcarddocpathback());
						laborhelper.setAadharcarddocpathback(dbPath);
						break;
					case "PAN_CARD":
						deleteOldFile(laborhelper.getPancarddocpath());
						laborhelper.setPancarddocpath(dbPath);
						break;
					case "PROFILE_PIC":
						deleteOldFile(laborhelper.getPhoto());
						laborhelper.setPhoto(dbPath);
						break;
					case "DRIVING_LICENCE":
						deleteOldFile(laborhelper.getDrivinglicense());
						laborhelper.setDrivinglicense(dbPath);
						break;
					default:
						throw new IllegalArgumentException("Invalid fileType: " + fileType);
					}
					laborHelperRepository.save(laborhelper);
				}
				break;

			case USERDOCUMENT:
				UserDocumentInfoEntity doc = userDocumentInfoRepository.findByIdAndIsDeleteFalse(moduleRecordId);
				if (doc == null)
					throw new RuntimeException("User Document not found");
				deleteOldFile(doc.getDocPath());
				doc.setDocPath(dbPath);
				userDocumentInfoRepository.save(doc);
				break;

			case CHECKLIST:
				EventLabourChecklistPhotosEntity checkImage;
				if (moduleRecordId == -1 || moduleRecordId == null || moduleRecordId == 0) {
					checkImage = new EventLabourChecklistPhotosEntity();
				} else {
					checkImage = eventLabourCheckListImagesRepository.findByIdAndIsDeleteFalse(moduleRecordId);
					if (checkImage == null)
						throw new RuntimeException("CheckList Image not found");
					deleteOldFile(checkImage.getImagePath());
				}
				checkImage.setImagePath(dbPath);
				checkImage.setEventLabourCheckListId(userId);
				checkImage.setIsDelete(false);
				eventLabourCheckListImagesRepository.save(checkImage);
				break;

			case USERDOWNPAYMENT:
				PaymentInfo p = infoRepository.findByIdAndIsDeleteFalse(moduleRecordId);
				if (p == null)
					throw new RuntimeException("User Down Payment not found");
				deleteOldFile(p.getDocPath());
				p.setDocPath(dbPath);
				infoRepository.save(p);
				break;

			case USERAMCFILE:
				UserAmcEntity ua = amcRepository.findByIdAndIsDeleteFalse(moduleRecordId);
				if (ua == null)
					throw new RuntimeException("User AMC not found");
				deleteOldFile(ua.getFile());
				ua.setFile(dbPath);
				amcRepository.save(ua);
				break;

			case REFUNDDETAILFILE:
				RefundDetailsEntity rd = detailsRepository.findByIdAndIsDeleteFalse(moduleRecordId);
				if (rd == null)
					throw new RuntimeException("Refund not found");
				deleteOldFile(rd.getFile());
				rd.setFile(dbPath);
				detailsRepository.save(rd);
				break;

			case USEREXPENSE:
				ExpenseManagementEntity ex = expenseManagementRepository.findByExpenseIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new RuntimeException("User not found with id : " + moduleRecordId));
				deleteOldFile(ex.getDocPath());
				ex.setDocPath(dbPath);
				expenseManagementRepository.save(ex);
				break;

			case TRIPEXPENSEFILE:
				ExpenseDetailEntity ed = expenseDetailRepository.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new RuntimeException("User not found with id : " + moduleRecordId));
				deleteOldFile(ed.getDocPath());
				ed.setDocPath(dbPath);
				expenseDetailRepository.save(ed);
				break;

			case OFFICEEXPENSEFILE:
				OfficeExpenseDocEntity oed = officeExpenseDocRepository.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new RuntimeException("File not found with id : " + moduleRecordId));
				deleteOldFile(oed.getDocPath());
				oed.setDocPath(dbPath);
				officeExpenseDocRepository.save(oed);
				break;

			case SUPERADMIN_INVOICE:
				InvoiceEntity invoice = invoiceRepository.findByInvoiceIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new RuntimeException("Invoice not found with id : " + moduleRecordId));
				deleteOldFile(invoice.getDocPath());
				invoice.setDocPath(dbPath);
				invoiceRepository.save(invoice);
				break;

			case FONTS:
				FontMasterEntity font = fontMasterRepository.findByFontIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new RuntimeException("Font not found with id : " + moduleRecordId));
				deleteOldFile(font.getFontPath());
				font.setFontPath(dbPath);
				fontMasterRepository.save(font);
				break;

			case QRCODE:
				BankDetailsEntity bankDetails = bankDetailsRepository.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new RuntimeException("Bank details not found with id : " + moduleRecordId));
				deleteOldFile(bankDetails.getQrCode());
				bankDetails.setQrCode(dbPath);
				bankDetailsRepository.save(bankDetails);
				break;

			case CATEGORY_BG:
				CatBgSelectionEntity catBg = catBgSelectionRepository.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new RuntimeException("Cat BG not found"));

				deleteOldFile(catBg.getImagePath());
				catBg.setImagePath(dbPath);
				catBgSelectionRepository.save(catBg);
				break;

			case GROUDIMAGE:
				EventFunctionStaffUploadEntity uploadEntity = eventFunctionStaffUploadRepository
						.findById(moduleRecordId).orElseThrow(() -> new RuntimeException("Staff Upload not found"));
				uploadEntity.setFilePath(dbPath);
				eventFunctionStaffUploadRepository.save(uploadEntity);
				break;

			case BANQUETHALL:
				BanquetHallImageEntity img = banquetHallImageRepository.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new RuntimeException("Hall Images not found"));
				deleteOldFile(img.getImagePath());
				img.setImagePath(dbPath);
				banquetHallImageRepository.save(img);
				break;

			case DECOREMAINCATEGORY:
				DecoreMainCategoryMasterEntity masterEntity = decoreMainCategoryMasterRepository
						.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new RuntimeException("Hall Images not found"));
				deleteOldFile(masterEntity.getImagePath());
				masterEntity.setImagePath(dbPath);
				decoreMainCategoryMasterRepository.save(masterEntity);
				break;

			case DECOREMAINCATEGORYITEM:
				DecoreMainCategoryItemImagesMasterEntity masterimageEntity = decoreMainCategoryItemImagesMasterRepository
						.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new RuntimeException("Hall Images not found"));
				deleteOldFile(masterimageEntity.getImagePath());
				masterimageEntity.setImagePath(dbPath);
				decoreMainCategoryItemImagesMasterRepository.save(masterimageEntity);
				break;

			case SPECIALNOTES:
				SpecialNotesImagesEntity imageEntity = specialNotesImagesRepository
						.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new RuntimeException("Image not found"));
				imageEntity.setImagePath(dbPath);
				specialNotesImagesRepository.save(imageEntity);
				break;

			case MANAGERTASK:
				EventFunctionManagerTaskImagesEntity imagesEntity = eventFunctionManagerTaskImagesRepository
						.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new RuntimeException("Image not found"));
				imagesEntity.setImagePath(dbPath);
				eventFunctionManagerTaskImagesRepository.save(imagesEntity);
				break;
			case EVENTFUNCTIONDECOREMAINCATEGORYITEM:
				EventFunctionDecorItemImagesEntity decorItemImagesEntity = eventFunctionDecorItemImagesRepository
						.findByIdAndIsDeleteFalse(moduleRecordId)
						.orElseThrow(() -> new RuntimeException("Image not found"));
				decorItemImagesEntity.setImagePath(dbPath);
				eventFunctionDecorItemImagesRepository.save(decorItemImagesEntity);
				break;
			case EVENT_IMG:
				VenueMasterEntity venueMasterEntity = venueMasterRepository.findByIdAndIsDeleteFalse(moduleRecordId);
				if (venueMasterEntity == null) {
					throw new RuntimeException("Venue not found with id : " + moduleRecordId);
				}
				venueMasterEntity.setImgPath(dbPath);
				venueMasterRepository.save(venueMasterEntity);
				break;
			default:
				throw new IllegalArgumentException("Invalid moduleName: " + moduleName);
			}

			response.put("success", true);
			response.put("msg", "File uploaded and updated successfully.");
			response.put("fullPath", baseUrl + dbPath);
			response.put("fileName", originalFileName);
			response.put("fileSize", file.getSize());

			return response;

		} catch (Exception e) {
			if (newTargetLocation != null) {
				Files.deleteIfExists(newTargetLocation);
			}

			response.put("success", false);
			response.put("msg", "File upload failed");
			return response;
		}
	}

	private String sanitize(String s) {
		if (s == null)
			return "unknown";
		return s.replaceAll("[^A-Za-z0-9@._-]", "_");
	}

	private String getExtension(String name) {
		if (name == null)
			return "";
		int idx = name.lastIndexOf('.');
		return (idx == -1) ? "" : name.substring(idx).toLowerCase();
	}

	private void validateFile(MultipartFile file) throws IOException {
		if (file == null) {
			throw new IOException("File is null");
		}
		if (file.isEmpty()) {
			throw new IOException("File is empty");
		}

		String fileName = file.getOriginalFilename();
		if (fileName == null || fileName.trim().isEmpty()) {
			throw new IOException("Invalid file name");
		}

		if (file.getSize() > 10 * 1024 * 1024) {
			throw new IOException("File size exceeds 10MB limit");
		}

		if (fileName.contains("\0")) {
			throw new IOException("Invalid filename contains null bytes");
		}
	}

	private void validateModuleName(String moduleName) {
		if (moduleName == null || moduleName.trim().isEmpty()) {
			throw new IllegalArgumentException("Module name is required");
		}
	}

	private String getFileExtension(String fileName) {
		if (fileName == null || !fileName.contains(".")) {
			return "";
		}
		return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
	}

	private void validateFileType(String extension, String mimeType) throws IOException {
		if (extension.isEmpty()) {
			throw new IOException("File must have an extension");
		}

		if (!ALLOWED_EXTENSIONS.contains(extension)) {
			throw new IOException("File type not allowed. Allowed types: " + String.join(", ", ALLOWED_EXTENSIONS));
		}

		if (mimeType != null && !ALLOWED_MIME_TYPES.contains(mimeType)) {
			throw new IOException("MIME type not allowed: " + mimeType);
		}
	}

	public void deleteOldFile(String oldFilePath) {
		if (oldFilePath != null && (oldFilePath.startsWith("/uploads/") || oldFilePath.startsWith("/jcupload/"))) {
			try {
				System.out.println("world");
				String relativePath = oldFilePath.replaceFirst("^/(uploads|jcupload)/", "");
				Path oldFile = Paths.get(uploadRoot, relativePath);
				System.out.println("old path : " + oldFile);
				if (Files.exists(oldFile)) {
					Files.delete(oldFile);
				}
			} catch (IOException e) {
				System.err.println("Failed to delete old file: " + oldFilePath + ", Error: " + e.getMessage());
			}
		}
	}

}
