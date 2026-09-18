package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.JournalVoucherDetailEntity;
import com.crmportal.entity.JournalVoucherEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.EventFunctionMenuAllocationRepository;
import com.crmportal.repository.JournalVoucherDetailRepository;
import com.crmportal.repository.JournalVoucherRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.JournalVoucherDetailRequestDto;
import com.crmportal.request.dto.JournalVoucherRequestDto;
import com.crmportal.response.dto.CompanyDetailsResponseDto;
import com.crmportal.response.dto.JournalVoucherDetailResponseDto;
import com.crmportal.response.dto.JournalVoucherResponseDto;
import com.crmportal.service.JournalVoucherService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
@Transactional
public class JournalVoucherServiceImpl implements JournalVoucherService {

    @Autowired
    private JournalVoucherRepository voucherRepository;

    @Autowired
    private JournalVoucherDetailRepository detailRepository;

    @Autowired
    private UserMasterRepository userRepository;

    @Autowired
    private PartyMasterRepository partyRepository;
    
    @Autowired
    private MenuPreparationServiceImpl menuPreparationServiceImpl;

    @Autowired
    private Environment environment;

    @Autowired
    private EventFunctionMenuAllocationRepository menuAllocationRepository;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Add or Update ──────────────────────────────────────────────────────
    @Override
    public JournalVoucherResponseDto addOrUpdate(JournalVoucherRequestDto request) {

        // ── Validate details not null ─────────────────────────────────────
        if (request.getDetails() == null || request.getDetails().size() < 2) {
            throw new RuntimeException(
                    "Minimum 2 entries required for a journal voucher");
        }

        // ── Validate balanced — total CR must equal total DR ──────────────
        BigDecimal totalCr = request.getDetails().stream()
                .filter(d -> "CR".equalsIgnoreCase(d.getCreditDebit()))
                .map(JournalVoucherDetailRequestDto::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDr = request.getDetails().stream()
                .filter(d -> "DR".equalsIgnoreCase(d.getCreditDebit()))
                .map(JournalVoucherDetailRequestDto::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalCr.compareTo(BigDecimal.ZERO) == 0
                || totalDr.compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException(
                    "Voucher must have at least one CR and one DR entry");
        }

        if (totalCr.compareTo(totalDr) != 0) {
            throw new RuntimeException(
                    "Voucher is not balanced. CR total ("
                    + totalCr + ") must equal DR total (" + totalDr + ")");
        }

        // ── Fetch user ────────────────────────────────────────────────────
        UserMasterEntity user = userRepository
                .findByIdAndIsDeleteFalse(request.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "User not found: " + request.getUserId()));

        JournalVoucherEntity voucher;

        boolean isNew = (request.getId() == null
                || request.getId() == 0
                || request.getId() == -1);

        if (isNew) {
            voucher = new JournalVoucherEntity();
            voucher.setVoucherNo(generateVoucherNo(request.getUserId()));
        } else {
            voucher = voucherRepository.findByIdAndIsDeleteFalse(request.getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Voucher not found: " + request.getId()));
            // Soft delete old details
            detailRepository.softDeleteByVoucherId(voucher.getId());
        }

        voucher.setVoucherDate(
                LocalDate.parse(request.getVoucherDate(), formatter));
        voucher.setNarration(request.getNarration());
        voucher.setUser(user);
        voucher.setIsDelete(false);

        voucher = voucherRepository.saveAndFlush(voucher);

        // ── Save new details ──────────────────────────────────────────────
        List<JournalVoucherDetailEntity> detailEntities = new ArrayList<>();

        for (JournalVoucherDetailRequestDto d : request.getDetails()) {

            PartyMasterEntity party = partyRepository
                    .findByIdAndIsDeleteFalse(d.getPartyId())
                    .orElseThrow(() -> new RuntimeException(
                            "Party not found: " + d.getPartyId()));

            JournalVoucherDetailEntity detail =
                    new JournalVoucherDetailEntity();
            detail.setVoucher(voucher);
            detail.setParty(party);
            detail.setAmount(d.getAmount());
            detail.setCreditDebit(d.getCreditDebit().toUpperCase());
            detail.setParticular(d.getParticular());
            detail.setUserId(request.getUserId());
            detail.setIsDelete(false);

            detailEntities.add(detail);
        }

        detailRepository.saveAll(detailEntities);

        return toResponse(voucher, detailEntities);
    }

    // ── Get all by user ────────────────────────────────────────────────────
    @Override
    public List<JournalVoucherResponseDto> getByUser(Long userId) {
        return voucherRepository
                .findByUserIdAndIsDeleteFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(v -> toResponse(v,
                        detailRepository.findByVoucherIdAndIsDeleteFalse(v.getId())))
                .collect(Collectors.toList());
    }

    // ── Get by ID ──────────────────────────────────────────────────────────
    @Override
    public JournalVoucherResponseDto getById(Long id) {
        JournalVoucherEntity voucher = voucherRepository
                .findByIdAndIsDeleteFalse(id)
                .orElseThrow(() -> new RuntimeException(
                        "Voucher not found: " + id));
        List<JournalVoucherDetailEntity> details =
                detailRepository.findByVoucherIdAndIsDeleteFalse(id);
        return toResponse(voucher, details);
    }

    // ── Delete ─────────────────────────────────────────────────────────────
    @Override
    public Boolean delete(Long id) {
        JournalVoucherEntity voucher = voucherRepository
                .findByIdAndIsDeleteFalse(id)
                .orElseThrow(() -> new RuntimeException(
                        "Voucher not found: " + id));
        voucher.setIsDelete(true);
        detailRepository.softDeleteByVoucherId(id);
        voucherRepository.save(voucher);
        return true;
    }

    // ── Generate voucher number ────────────────────────────────────────────
    private String generateVoucherNo(Long userId) {
        String lastVoucherNo =
                voucherRepository.findMaxVoucherNoByUserId(userId);

        long nextNumber = 1L;

        if (lastVoucherNo != null && !lastVoucherNo.trim().isEmpty()) {
            try {
                // Format: JV-001, JV-002, etc.
                String[] parts = lastVoucherNo.split("-");
                nextNumber = Long.parseLong(
                        parts[parts.length - 1]) + 1;
            } catch (NumberFormatException e) {
                nextNumber = 1L;
            }
        }

        return "JV-" + String.format("%03d", nextNumber);
    }

    // ── Entity → Response ──────────────────────────────────────────────────
    private JournalVoucherResponseDto toResponse(
            JournalVoucherEntity voucher,
            List<JournalVoucherDetailEntity> details) {

        JournalVoucherResponseDto dto = new JournalVoucherResponseDto();
        dto.setId(voucher.getId());
        dto.setVoucherNo(voucher.getVoucherNo());
        dto.setVoucherDate(voucher.getVoucherDate() != null
                ? voucher.getVoucherDate().format(formatter) : null);
        dto.setNarration(voucher.getNarration());
        dto.setUserId(voucher.getUser() != null
                ? voucher.getUser().getId() : null);
        dto.setCreatedAt(voucher.getCreatedAt() != null
                ? voucher.getCreatedAt().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null);

        List<JournalVoucherDetailResponseDto> detailDtos = new ArrayList<>();

        for (JournalVoucherDetailEntity d : details) {
            JournalVoucherDetailResponseDto rd =
                    new JournalVoucherDetailResponseDto();
            rd.setId(d.getId());
            rd.setPartyId(d.getParty() != null
                    ? d.getParty().getId() : null);
            rd.setPartyName(d.getParty() != null
                    ? d.getParty().getNameEnglish() : null);
            rd.setAmount(d.getAmount());
            rd.setCreditDebit(d.getCreditDebit());
            rd.setParticular(d.getParticular());
            detailDtos.add(rd);
        }

        dto.setDetails(detailDtos);
        return dto;
    }
    
    @Override
    public byte[] generatePdfReport(Long voucherId, Long userId, Integer isCompanyDetails) {
        try {
            JournalVoucherResponseDto voucher = getById(voucherId);
            if (voucher == null) {
                throw new RuntimeException("Voucher not found with ID: " + voucherId);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer   = new PdfWriter(baos);
            PdfDocument pdf    = new PdfDocument(writer);
            Document document  = new Document(pdf, PageSize.A4);
            document.setMargins(30, 30, 30, 30);

            PdfFont boldFont    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // ── Colors ────────────────────────────────────────────────────────
            Color darkBlue    = new DeviceRgb(13,  71, 116);
            Color blackColor  = new DeviceRgb(0, 0, 0);
            Color tableBlue   = new DeviceRgb(26,  99, 153);
            Color lightBlueBg = new DeviceRgb(235, 245, 255);
            Color borderGray  = new DeviceRgb(200, 210, 220);
            Color labelGray   = new DeviceRgb(100, 120, 140);
            Color altRow      = new DeviceRgb(245, 248, 252);
            Color crColor     = new DeviceRgb(0, 128, 0);   // green for CR
            Color drColor     = new DeviceRgb(200, 0, 0);   // red for DR

            // ══════════════════════════════════════════════════════════════════
            // SECTION 0 — COMPANY HEADER
            // ══════════════════════════════════════════════════════════════════
            if (isCompanyDetails == 1) {
            	
            	CompanyDetailsResponseDto cmpDto = getCompanyDetails(userId);
            	
                float[] companyWidths = {20f, 2f, 78f};
                Table headerTable = new Table(UnitValue.createPercentArray(companyWidths));
                headerTable.setWidth(UnitValue.createPercentValue(100));

                //  Logo (spans 3 rows × 2 cols — exact match to reference)
                try {
                    ImageData logoData = menuPreparationServiceImpl.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
                    
                  //  ImageData logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");

                    Image logo = new Image(logoData);
                    logo.setWidth(100f);
                    logo.setAutoScale(false);
                    logo.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);

                    headerTable.addCell(new Cell(3, 2)
                            .add(logo)
                            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                            .setBorder(Border.NO_BORDER)
                            .setPaddingBottom(10f));

                } catch (Exception e) {
                    headerTable.addCell(new Cell(3, 2).setBorder(Border.NO_BORDER));
                }

				//  Company Name 
                headerTable.addCell(new Cell()
                        .add(new Paragraph(cmpDto.getCompanyName())
                                .setFont(boldFont).setFontSize(14)
                                .setFontColor(blackColor)
                                .setTextAlignment(TextAlignment.LEFT))
                        .setBorder(Border.NO_BORDER)
                        .setPaddingLeft(15f));

                //  Phone
                headerTable.addCell(new Cell()
                        .add(new Paragraph()
                                .add(new Text("Phone : ")
                                        .setFont(boldFont).setFontSize(14)
                                        .setFontColor(blackColor))
                                .add(new Text(cmpDto.getOfficeNo() != null ? cmpDto.getOfficeNo()
                                        : "")
                                        .setFont(regularFont).setFontSize(14)
                                        .setFontColor(blackColor)))
                        .setTextAlignment(TextAlignment.LEFT)
                        .setBorder(Border.NO_BORDER)
                        .setPaddingLeft(15f)
                        .setPaddingBottom(10f));
                
                //  Email
                headerTable.addCell(new Cell()
                        .add(new Paragraph()
                                .add(new Text("Email : ")
                                        .setFont(boldFont).setFontSize(14)
                                        .setFontColor(blackColor))
                                .add(new Text(cmpDto.getCompanyEmail() != null ? cmpDto.getCompanyEmail() : "")
                                        .setFont(regularFont).setFontSize(14)
                                        .setFontColor(blackColor)))
                        .setTextAlignment(TextAlignment.LEFT)
                        .setBorder(Border.NO_BORDER)
                        .setPaddingLeft(15f)
                        .setPaddingBottom(10f));
                

                // ── Empty filler cell (reference has this exact line) ─────────
                headerTable.addCell(new Cell().setBorder(Border.NO_BORDER));

                document.add(headerTable);

                //  Dark blue divider 
                Table companyDivider = new Table(UnitValue.createPercentArray(new float[]{100f}));
                companyDivider.setWidth(UnitValue.createPercentValue(100));
                companyDivider.addCell(new Cell()
                        .setHeight(2f)
                        .setBackgroundColor(darkBlue)
                        .setBorder(Border.NO_BORDER));
                document.add(companyDivider);
                document.add(new Paragraph("").setMarginBottom(8));
            }

            // ══════════════════════════════════════════════════════════════════
            // SECTION 1 — TOP HEADER (Title + Voucher No + Date)
            // ══════════════════════════════════════════════════════════════════
            String printedOn = "Printed on: " +
                    java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));

            float[] topW = {60f, 40f};
            Table topTable = new Table(UnitValue.createPercentArray(topW));
            topTable.setWidth(UnitValue.createPercentValue(100));

            topTable.addCell(new Cell()
                    .add(new Paragraph("Journal Voucher")
                            .setFont(boldFont).setFontSize(22).setFontColor(darkBlue).setMarginBottom(3))
                    .add(new Paragraph(printedOn)
                            .setFont(regularFont).setFontSize(8).setFontColor(labelGray))
                    .setBorder(Border.NO_BORDER).setPaddingBottom(6));

            topTable.addCell(new Cell()
                    .add(new Paragraph(nvl(voucher.getVoucherNo()))
                            .setFont(boldFont).setFontSize(14).setFontColor(darkBlue)
                            .setTextAlignment(TextAlignment.RIGHT).setMarginBottom(2))
                    .add(new Paragraph("Date: " + nvl(voucher.getVoucherDate()))
                            .setFont(regularFont).setFontSize(8).setFontColor(labelGray)
                            .setTextAlignment(TextAlignment.RIGHT))
                    .setBorder(Border.NO_BORDER).setPaddingBottom(6));

            document.add(topTable);

            // Divider
            Table dividerLine = new Table(UnitValue.createPercentArray(new float[]{100f}));
            dividerLine.setWidth(UnitValue.createPercentValue(100));
            dividerLine.addCell(new Cell().setHeight(2f)
                    .setBackgroundColor(darkBlue).setBorder(Border.NO_BORDER));
            document.add(dividerLine);
            document.add(new Paragraph("").setMarginBottom(10));

            // ══════════════════════════════════════════════════════════════════
            // SECTION 2 — NARRATION BAND
            // ══════════════════════════════════════════════════════════════════
            if (voucher.getNarration() != null && !voucher.getNarration().trim().isEmpty()) {
                Table narrationTable = new Table(UnitValue.createPercentArray(new float[]{100f}));
                narrationTable.setWidth(UnitValue.createPercentValue(100));
                narrationTable.addCell(new Cell()
                        .add(new Paragraph()
                                .add(new Text("Narration: ").setFont(boldFont).setFontSize(10).setFontColor(labelGray))
                                .add(new Text(voucher.getNarration()).setFont(regularFont).setFontSize(10).setFontColor(blackColor)))
                        .setBackgroundColor(lightBlueBg)
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.5f))
                        .setPadding(10));
                document.add(narrationTable);
                document.add(new Paragraph("").setMarginBottom(12));
            }

            // ══════════════════════════════════════════════════════════════════
            // SECTION 3 — VOUCHER DETAILS TABLE
            // ══════════════════════════════════════════════════════════════════
            float[] colW = {5f, 40f, 20f, 15f, 20f};
            Table itemTable = new Table(UnitValue.createPercentArray(colW));
            itemTable.setWidth(UnitValue.createPercentValue(100));

            String[] headers = {"#", "Account Name", "Particular", "CR / DR", "Amount"};
            TextAlignment[] aligns = {
                TextAlignment.CENTER, TextAlignment.LEFT,
                TextAlignment.LEFT,   TextAlignment.CENTER, TextAlignment.RIGHT
            };

            for (int i = 0; i < headers.length; i++) {
                itemTable.addHeaderCell(new Cell()
                        .add(new Paragraph(headers[i])
                                .setFont(boldFont).setFontSize(10).setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(tableBlue)
                        .setTextAlignment(aligns[i])
                        .setPaddingTop(7).setPaddingBottom(7)
                        .setBorder(Border.NO_BORDER));
            }

            // ── Calculate totals ──────────────────────────────────────────────
            BigDecimal totalCr = BigDecimal.ZERO;
            BigDecimal totalDr = BigDecimal.ZERO;

            boolean alternate = false;
            int srNo = 1;

            for (JournalVoucherDetailResponseDto d : voucher.getDetails()) {
                Color bg = alternate ? altRow : ColorConstants.WHITE;
                alternate = !alternate;

                boolean isCr = "CR".equalsIgnoreCase(d.getCreditDebit());
                Color cdColor = isCr ? crColor : drColor;

                if (isCr) totalCr = totalCr.add(d.getAmount());
                else       totalDr = totalDr.add(d.getAmount());

                itemTable.addCell(jvCell(String.valueOf(srNo++),       bg, regularFont, TextAlignment.CENTER));
                itemTable.addCell(jvCell(nvl(d.getPartyName()),        bg, regularFont, TextAlignment.LEFT));
                itemTable.addCell(jvCell(nvl(d.getParticular()),       bg, regularFont, TextAlignment.LEFT));

                // CR/DR cell with color
                itemTable.addCell(new Cell()
                        .add(new Paragraph(nvl(d.getCreditDebit()))
                                .setFont(boldFont).setFontSize(9).setFontColor(cdColor))
                        .setBackgroundColor(bg)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(
                                new DeviceRgb(220, 228, 235), 0.3f))
                        .setPaddingTop(5).setPaddingBottom(5));

                itemTable.addCell(jvCell(fmtAmount(d.getAmount()),     bg, regularFont, TextAlignment.RIGHT));
            }

            // ── Totals row ────────────────────────────────────────────────────
//            Color totalBg = new DeviceRgb(230, 240, 250);

//            itemTable.addCell(new Cell(1, 3)
//                    .add(new Paragraph("TOTALS").setFont(boldFont).setFontSize(10).setFontColor(darkBlue))
//                    .setBackgroundColor(totalBg)
//                    .setTextAlignment(TextAlignment.RIGHT)
//                    .setPaddingTop(6).setPaddingBottom(6).setPaddingRight(8)
//                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.5f)));

//            itemTable.addCell(new Cell()
//                    .add(new Paragraph()
//                            .add(new Text("CR: ").setFont(boldFont).setFontSize(9).setFontColor(crColor))
//                            .add(new Text(fmtAmount(totalCr)).setFont(regularFont).setFontSize(9).setFontColor(crColor))
//                            .add(new Text("\nDR: ").setFont(boldFont).setFontSize(9).setFontColor(drColor))
//                            .add(new Text(fmtAmount(totalDr)).setFont(regularFont).setFontSize(9).setFontColor(drColor)))
//                    .setBackgroundColor(totalBg)
//                    .setTextAlignment(TextAlignment.CENTER)
//                    .setPaddingTop(4).setPaddingBottom(4)
//                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.5f)));

            // Balanced indicator
//            boolean isBalanced = totalCr.compareTo(totalDr) == 0;
//            Color balancedColor = isBalanced ? crColor : drColor;
//            String balancedText = isBalanced ? "✓ Balanced" : "✗ Not Balanced";

//            itemTable.addCell(new Cell()
//                    .add(new Paragraph(balancedText)
//                            .setFont(boldFont).setFontSize(9).setFontColor(balancedColor))
//                    .setBackgroundColor(totalBg)
//                    .setTextAlignment(TextAlignment.RIGHT)
//                    .setPaddingTop(6).setPaddingBottom(6).setPaddingRight(8)
//                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.5f)));

            document.add(itemTable);
            document.add(new Paragraph("").setMarginBottom(16));

            // ══════════════════════════════════════════════════════════════════
            // SECTION 4 — FOOTER
            // ══════════════════════════════════════════════════════════════════
//            com.itextpdf.layout.borders.Border topBorder =
//                    new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.5f);
//
//            Table footer = new Table(UnitValue.createPercentArray(new float[]{60f, 40f}));
//            footer.setWidth(UnitValue.createPercentValue(100));
//
//            footer.addCell(new Cell()
//                    .add(new Paragraph(nvl(voucher.getVoucherNo())
//                            + "  |  Journal Voucher  |  Date: "
//                            + nvl(voucher.getVoucherDate()))
//                            .setFont(regularFont).setFontSize(8).setFontColor(labelGray))
//                    .setBorder(topBorder)
//                    .setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
//                    .setBorderBottom(Border.NO_BORDER).setPaddingTop(6));
//
//            footer.addCell(new Cell()
//                    .add(new Paragraph("Total Entries: " + voucher.getDetails().size())
//                            .setFont(regularFont).setFontSize(8).setFontColor(labelGray)
//                            .setTextAlignment(TextAlignment.RIGHT))
//                    .setBorder(topBorder)
//                    .setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
//                    .setBorderBottom(Border.NO_BORDER).setPaddingTop(6));
//
//            document.add(footer);
            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Cell jvCell(String text, Color bg, PdfFont font, TextAlignment align) {
        return new Cell()
                .add(new Paragraph(text != null ? text : "").setFont(font).setFontSize(9))
                .setBackgroundColor(bg)
                .setTextAlignment(align)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(
                        new DeviceRgb(220, 228, 235), 0.3f))
                .setPaddingTop(5).setPaddingBottom(5);
    }

    private String fmtAmount(BigDecimal val) {
        return val != null
                ? String.format("%.2f", val) : "0.00";
    }

    private String nvl(String val) {
        return (val != null && !val.isEmpty()) ? val : "-";
    }
    
    public CompanyDetailsResponseDto getCompanyDetails(Long userId) {
		UserMasterEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));
		Optional<Object[]> op = menuAllocationRepository.getCompanyDetailsByUserId(userId);

		if (!op.isPresent()) {
			return new CompanyDetailsResponseDto();
		} else {
			Object[] row = op.get();

			if (row.length == 1 && row[0] instanceof Object[]) {
				row = (Object[]) row[0];
			}

			CompanyDetailsResponseDto dto = new CompanyDetailsResponseDto();
			dto.setCompanyName(row[0] != null ? row[0].toString() : "");
			dto.setCountryCode(row[1] != null ? row[1].toString() : "");
			dto.setCompanyEmail(row[2] != null ? row[2].toString() : "");
			dto.setOfficeNo(row[3] != null ? row[3].toString() : "");
			dto.setAddress(row[4] != null ? row[4].toString() : "");
			dto.setLogo(row[5] != null ? row[5].toString() : "");
			return dto;
		}
	}
}