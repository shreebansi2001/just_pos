package com.crmportal.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.ContactCategoryMasterEntity;
import com.crmportal.entity.EventLaborHelperEntity;
import com.crmportal.entity.LaborHelperEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.enums.ModuleName;
import com.crmportal.repository.ContactCategoryMasterRepository;
import com.crmportal.repository.EventLaborHelperRepository;
import com.crmportal.repository.LaborHelperRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.request.dto.LaborHelperRequestDto;
import com.crmportal.response.dto.EventLaborHelperSelectionResponseDto;
import com.crmportal.response.dto.LaborHelperResponseDto;
import com.crmportal.service.LaborHelperService;
import com.crmportal.service.UserFileService; // Existing service reference

import java.io.ByteArrayOutputStream;
import java.io.File;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.borders.Border;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

@Service
public class LaborHelperServiceImpl implements LaborHelperService {

    @Autowired
    private LaborHelperRepository laborHelperRepository;

    @Autowired
    private PartyMasterRepository partyMasterRepository;

    @Autowired
    private ContactCategoryMasterRepository contactCategoryMasterRepository;

    @Autowired
    private UserFileService userFileService;
    
    @Autowired
    private EventLaborHelperRepository eventLaborHelperRepository;

    @Override
    @Transactional
    public LaborHelperResponseDto addOrUpdateLaborHelper(LaborHelperRequestDto request, Long id) {
        LaborHelperEntity entity;

        if (id != null && id > 0) {
            entity = laborHelperRepository.findByIdAndIsDeleteFalse(id)
                    .orElseThrow(() -> new RuntimeException("Labor Helper record not found for ID: " + id));
        } else {
            entity = new LaborHelperEntity();
            entity.setIsDelete(false);
        }

        entity.setName(request.getName());
        entity.setPhonenumber(request.getPhonenumber());
        entity.setAadharcard(request.getAadharcard());
        entity.setPancard(request.getPancard());

        if (request.getPartyId() != null) {
            PartyMasterEntity party = partyMasterRepository.findById(request.getPartyId())
                    .orElseThrow(() -> new RuntimeException("Party Master not found for ID: " + request.getPartyId()));
            entity.setContact(party);
        }

        if (request.getContactCategoryId() != null) {
            ContactCategoryMasterEntity category = contactCategoryMasterRepository.findById(request.getContactCategoryId())
                    .orElseThrow(() -> new RuntimeException("Contact Category Master not found for ID: " + request.getContactCategoryId()));
            entity.setContactCategory(category);
        }

        LaborHelperEntity savedEntity = laborHelperRepository.save(entity);

        // Process File Uploads (Supports up to 5 document types)
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            request.getFiles().forEach(file -> {
                if (file.getFile() != null && !file.getFile().isEmpty()) {
                    try {
                        userFileService.storeFile(
                        		request.getUserId(),
                        		ModuleName.LABOR_HELPER.toString(),
                        		entity.getId(),
                                file.getFileType(),
                                file.getFile()
                        );
                    } catch (IOException e) {
                        throw new RuntimeException("File upload failed: " + e.getMessage(), e);
                    }
                }
            });
        }

        return mapToResponseDto(savedEntity);
    }

    @Override
    public List<LaborHelperResponseDto> getAllLaborHelpersByUserId(Long userId) {
        // Retrieves non-deleted helper records linked to parties owned by the given userId
        List<LaborHelperEntity> entities = laborHelperRepository.findByContact_User_IdAndIsDeleteFalse(userId);
        return entities.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<LaborHelperResponseDto> getAllLaborHelpersByPartyId(Long partyId) {
        List<LaborHelperEntity> entities = laborHelperRepository.findByContact_IdAndIsDeleteFalse(partyId);
        return entities.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<LaborHelperResponseDto> getAllLaborHelpersByContactCategoryId(Long contactCategoryId) {
        List<LaborHelperEntity> entities = laborHelperRepository.findByContactCategory_IdAndIsDeleteFalse(contactCategoryId);
        return entities.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Override
    public LaborHelperResponseDto getLaborHelperById(Long id) {
        LaborHelperEntity entity = laborHelperRepository.findByIdAndIsDeleteFalse(id)
                .orElseThrow(() -> new RuntimeException("Labor Helper record not found for ID: " + id));
        return mapToResponseDto(entity);
    }

    @Override
    @Transactional
    public boolean deleteLaborHelperById(Long id) {
        LaborHelperEntity entity = laborHelperRepository.findByIdAndIsDeleteFalse(id)
                .orElseThrow(() -> new RuntimeException("Labor Helper record not found for ID: " + id));
        entity.setIsDelete(true);
        laborHelperRepository.save(entity);
        return true;
    }

    private LaborHelperResponseDto mapToResponseDto(LaborHelperEntity entity) {
        LaborHelperResponseDto dto = new LaborHelperResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPhonenumber(entity.getPhonenumber());
        dto.setAadharcard(entity.getAadharcard());
        dto.setPancard(entity.getPancard());
        dto.setAadharcarddocpathfront(entity.getAadharcarddocpathfront());
        dto.setAadharcarddocpathback(entity.getAadharcarddocpathback());
        dto.setPancarddocpath(entity.getPancarddocpath());
        dto.setPhoto(entity.getPhoto());
        dto.setDrivinglicense(entity.getDrivinglicense());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setIsDelete(entity.getIsDelete());

        if (entity.getContact() != null) {
            dto.setPartyId(entity.getContact().getId());
            dto.setPartyName(entity.getContact().getNameEnglish());
        }
        if (entity.getContactCategory() != null) {
            dto.setContactCategoryId(entity.getContactCategory().getId());
            dto.setContactCategoryName(entity.getContactCategory().getNameEnglish());
        }
        return dto;
    }
    
    @Override
    public List<EventLaborHelperSelectionResponseDto> getAllLaborHelpersByPartyAndEventDetails(
            Long partyId, Long eventId, Long eventFunctionId) {

        // 1. Fetch all active helpers belonging to the party
        List<LaborHelperEntity> allPartyHelpers = laborHelperRepository.findByContact_IdAndIsDeleteFalse(partyId);

        // 2. Fetch mapped helper entries for this specific event context
        List<EventLaborHelperEntity> mappedEntities = eventLaborHelperRepository
                .findByEvent_idAndEvent_function_idAndContact_Id(eventId, eventFunctionId, partyId);

        // 3. Collect assigned labor helper IDs into a set
        Set<Long> selectedHelperIds = mappedEntities.stream()
                .map(e -> e.getLaborhelper().getId())
                .collect(Collectors.toSet());

        // 4. Map directly to EventLaborHelperSelectionResponseDto
        return allPartyHelpers.stream().map(helper -> {
            EventLaborHelperSelectionResponseDto dto = new EventLaborHelperSelectionResponseDto();
            dto.setId(helper.getId());
            dto.setName(helper.getName());
            dto.setPhonenumber(helper.getPhonenumber());
            dto.setAadharcard(helper.getAadharcard());
            dto.setPancard(helper.getPancard());
            dto.setAadharcarddocpathfront(helper.getAadharcarddocpathfront());
            dto.setAadharcarddocpathback(helper.getAadharcarddocpathback());
            dto.setPancarddocpath(helper.getPancarddocpath());
            dto.setPhoto(helper.getPhoto());
            dto.setDrivinglicense(helper.getDrivinglicense());
            dto.setCreatedAt(helper.getCreatedAt());
            dto.setIsDelete(helper.getIsDelete());

            if (helper.getContact() != null) {
                dto.setPartyId(helper.getContact().getId());
                dto.setPartyName(helper.getContact().getNameEnglish());
            }
            if (helper.getContactCategory() != null) {
                dto.setContactCategoryId(helper.getContactCategory().getId());
                dto.setContactCategoryName(helper.getContactCategory().getNameEnglish());
            }

            dto.setIsSelected(selectedHelperIds.contains(helper.getId()));
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public byte[] generateLaborHelperReport(Long id, Long userId) {
    	try {
	        LaborHelperEntity labor = laborHelperRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Labor Helper not found"));
	
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfDocument pdf = new PdfDocument(new PdfWriter(baos));
	        Document document = new Document(pdf, PageSize.A4);
	        document.setMargins(25, 25, 25, 25);
	
	        PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
	
	        document.add(new Paragraph("GHATI PARTY")
	                .setFont(boldFont)
	                .setFontSize(20)
	                .setUnderline()
	                .setTextAlignment(TextAlignment.CENTER)
	                .setMarginBottom(35));
	
	        Table mainTable = new Table(UnitValue.createPercentArray(new float[]{22f, 3f, 45f, 30f}))
	                .setWidth(UnitValue.createPercentValue(100));
	
	        addInfoRow(mainTable, "Name", labor.getName(), normalFont);
	        addInfoRow(mainTable, "Adhar Card No.", labor.getAadharcard(), normalFont);
	        addInfoRow(mainTable, "Phone No.", labor.getPhonenumber(), normalFont);
	        addInfoRow(mainTable, "Category",
	                labor.getContactCategory() != null ? labor.getContactCategory().getNameEnglish() : "", normalFont);
	        addInfoRow(mainTable, "Main Account",
	                labor.getContact() != null ? labor.getContact().getNameEnglish() : "", normalFont);
	
	        Cell photoCell = new Cell(5, 1)
	                .setTextAlignment(TextAlignment.CENTER)
	                .setVerticalAlignment(VerticalAlignment.MIDDLE);
	
	        if (labor.getPhoto() != null && !labor.getPhoto().trim().isEmpty()) {
	            Image photo = getImage(labor.getPhoto(), 130, 160);
	            if (photo != null) photoCell.add(photo);
	        }
	
	        mainTable.addCell(photoCell);
	        document.add(mainTable);
	
	        document.add(new Paragraph("UPLOADED DOCUMENTS")
	                .setFont(boldFont)
	                .setFontSize(15)
	                .setTextAlignment(TextAlignment.CENTER)
	                .setMarginTop(30)
	                .setMarginBottom(15));
	
	        Table documentTable = new Table(UnitValue.createPercentArray(new float[]{33.33f, 33.33f, 33.33f}))
	                .setWidth(UnitValue.createPercentValue(100));
	
	        addDocumentImage(documentTable, "Aadhaar Card Front", labor.getAadharcarddocpathfront(), normalFont);
	        addDocumentImage(documentTable, "Aadhaar Card Back", labor.getAadharcarddocpathback(), normalFont);
	        addDocumentImage(documentTable, "PAN Card", labor.getPancarddocpath(), normalFont);
	        addDocumentImage(documentTable, "Driving License", labor.getDrivinglicense(), normalFont);
	
	        document.add(documentTable);
	        document.close();
	        return baos.toByteArray();
	    } catch (Exception e) {
	        throw new RuntimeException(
	                "Failed to generate Labor Helper Report", e
	        );
	    }
    }
    
    private void addInfoRow(Table table, String label, String value, PdfFont font) {

        table.addCell(new Cell()
                .add(new Paragraph(label)
                        .setFont(font)
                        .setFontSize(10)));

        table.addCell(new Cell()
                .add(new Paragraph(":")
                        .setFont(font)
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.CENTER)));

        table.addCell(new Cell()
                .add(new Paragraph(value != null ? value : "")
                        .setFont(font)
                        .setFontSize(10)));

        table.addCell(new Cell().add(new Paragraph("")));
    }
    
    private void addDocumentImage(
            Table table,
            String title,
            String path,
            PdfFont font) {

        Cell cell = new Cell()
                .setPadding(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        cell.add(new Paragraph(title)
                .setFont(font)
                .setFontSize(10)
                .setMarginBottom(8));

        if (path != null && !path.trim().isEmpty()) {

            Image image = getImage(path, 150, 120);

            if (image != null) {
                cell.add(image);
            } else {
                cell.add(new Paragraph("Document not available")
                        .setFont(font)
                        .setFontSize(8));
            }

        } else {
            cell.add(new Paragraph("Document not uploaded")
                    .setFont(font)
                    .setFontSize(8));
        }

        table.addCell(cell);
    }
    
    private Image getImage(String path, float width, float height) {

        try {

            File file = new File(path);

            if (!file.exists()) {
                return null;
            }

            ImageData imageData = ImageDataFactory.create(file.getAbsolutePath());

            return new Image(imageData)
                    .setWidth(width)
                    .setHeight(height)
                    .setAutoScale(false);

        } catch (Exception e) {
            return null;
        }
    }
}