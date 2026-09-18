package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.*;
import com.crmportal.repository.*;
import com.crmportal.response.dto.*;
import com.crmportal.service.DateWiseStockReportService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
//  iText 7 imports (CLEAN)
import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

//  Excel imports
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

@Service
@Transactional
public class DateWiseStockReportServiceImpl implements DateWiseStockReportService {

    @Autowired
    private RawMaterialMasterRepository rawMaterialRepository;

    @Autowired
    private RawMaterialCategoryMasterRepository categoryRepository;

    @Autowired
    private StockTypeRepository stockTypeRepository;

    @Autowired
    private StockLedgerRepository stockLedgerRepository;

    @Autowired
    private UnitMasterRepository unitMasterRepository;
    
    @Autowired
	EventFunctionMenuAllocationRepository menuAllocationRepository;
	
	@Autowired
	Environment environment;
	
	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;
	
	@Autowired
	private UserMasterRepository userRepository;

	@Autowired
	private StoreManageDetailRepository detailRepository;

    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @Override
    public byte[] generatePdfReport(Long categoryId, Long stockTypeId,
            String fromDate, String toDate, Long userId, Integer isCompanyDetails, String search) {

        try {
        	
        	PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        	
            DateWiseStockReportResponseDto data = getReport(
                    categoryId, stockTypeId, fromDate, toDate, userId, 1, 10000, search);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4.rotate());
            

            CompanyDetailsResponseDto cmpDto = getCompanyDetails(userId); 
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Colors
            Color headerColor = new DeviceRgb(44, 62, 80);
            Color altColor = new DeviceRgb(240, 240, 240);
            Color totalColor = new DeviceRgb(220, 220, 220);
            Color redColor = new DeviceRgb(255, 0, 0);
            Color blackColor    = new DeviceRgb(0, 0, 0);
            Color darkBlue    = new DeviceRgb(13,  71,  116);   // header / final amount bg
            Color lightBlueBg = new DeviceRgb(235, 245, 255);   // vendor info band bg
            Color tableHeader = new DeviceRgb(26,  99,  153);   // items table header
            Color altRow      = new DeviceRgb(245, 248, 252);   // alternate row
            Color borderGray  = new DeviceRgb(200, 210, 220);   // subtle border
            Color labelGray   = new DeviceRgb(100, 120, 140);   // small labels
            Color remarksBg   = new DeviceRgb(255, 253, 235);   // remarks yellow tint
            Color remarksBdr  = new DeviceRgb(220, 200, 100);   // remarks border
            
            if (isCompanyDetails == 1) {

                float[] companyWidths = {20f, 2f, 78f};
                Table headerTable = new Table(UnitValue.createPercentArray(companyWidths));
                headerTable.setWidth(UnitValue.createPercentValue(100));

                //  Logo (spans 3 rows × 2 cols — exact match to reference)
                try {
                    ImageData logoData = menuPreparationServiceImpl
                            .loadImageFromResource(
                                    environment.getProperty("app.image.url") + cmpDto.getLogo());

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

            // Title
            document.add(new Paragraph("Stock Order Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(boldFont)
                    .setFontSize(16));

            document.add(new Paragraph("Category Name: " + data.getCategoryName())
                    .setTextAlignment(TextAlignment.CENTER).setFont(boldFont));

            if (fromDate != null) {
            	 document.add(new Paragraph(
                         (fromDate != null ? fromDate : "All") + " TO " +
                         (toDate != null ? toDate : "All"))
                         .setTextAlignment(TextAlignment.CENTER));
			}else {
				 document.add(new Paragraph(
		                    (fromDate != null ? fromDate : " ") + "  " +
		                    (toDate != null ? toDate : " "))
		                    .setTextAlignment(TextAlignment.CENTER));
			}

            document.add(new Paragraph("\n"));

            float[] widths = {3,16,8,6,6,7,6,7,7,5,5,8,8,8};
            Table table = new Table(UnitValue.createPercentArray(widths));
            table.setWidth(UnitValue.createPercentValue(100));

            String[] headers = {"No.","Item Name","Category","OPB","Purchase",
                    "Purchase Return","Sell","Sell Return","Increase","Wastage","Total Stock","Unit","Rate","Amount"};

            for (String h : headers) {
                table.addHeaderCell(new Cell()
                        .add(new Paragraph(h).setFontColor(ColorConstants.WHITE).setFont(boldFont))
                        .setBackgroundColor(headerColor)
                        .setTextAlignment(TextAlignment.CENTER));
            }

            double totalAmount = 0;
            boolean alternate = false;

            for (DateWiseStockRowDto row : data.getItems()) {

                Color bg = alternate ? altColor : ColorConstants.WHITE;
                alternate = !alternate;

                table.addCell(createCell(String.valueOf(row.getSrNo()), bg));
                table.addCell(createCell(row.getItemName(), bg));
                table.addCell(createCell(row.getCategoryName(), bg));

                table.addCell(createRightCell(fmt(row.getOpb()), bg));
                table.addCell(createRightCell(fmt(row.getPurchase()), bg));
                table.addCell(createRightCell(fmt(row.getPurchaseReturn()), bg));
                table.addCell(createRightCell(fmt(row.getSell()), bg));
                table.addCell(createRightCell(fmt(row.getSellReturn()), bg));
                table.addCell(createRightCell(fmt(row.getIncrease()), bg));
                table.addCell(createRightCell(fmt(row.getWastage()), bg));
                table.addCell(new Cell()
                        .add(new Paragraph(fmt(row.getFinalTotal()))
                                .setFontColor(row.getFinalTotal() < 0 ? redColor : ColorConstants.BLACK))
                        .setBackgroundColor(bg)
                        .setTextAlignment(TextAlignment.RIGHT));

                table.addCell(createCell(row.getUnit(), bg));

                table.addCell(createRightCell(
                        row.getRate() != null ? row.getRate().toPlainString() : "0", bg));

                double amt = row.getAmount() != null ? row.getAmount().doubleValue() : 0;

                table.addCell(new Cell()
                        .add(new Paragraph(fmt(amt))
                                .setFontColor(amt < 0 ? redColor : ColorConstants.BLACK))
                        .setBackgroundColor(bg)
                        .setTextAlignment(TextAlignment.RIGHT));

                totalAmount += amt;
            }

            table.addCell(new Cell(1, 11)
                    .add(new Paragraph("Total").setFont(boldFont))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBackgroundColor(totalColor));

            table.addCell(new Cell()
                    .add(new Paragraph(fmt(totalAmount)).setFont(boldFont))
                    .setBackgroundColor(totalColor)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.add(table);
            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ================= Excel =================
    @Override
    public byte[] generateExcelReport(Long categoryId, Long stockTypeId,
            String fromDate, String toDate, Long userId, String search) {
        try {
            DateWiseStockReportResponseDto data = getReport(
                    categoryId, stockTypeId, fromDate, toDate, userId, 1, 10000, search);

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Stock Report");

            int rowNum = 0;

            Row titleRow = sheet.createRow(rowNum++);
            titleRow.createCell(0).setCellValue("Stock Report");
            sheet.addMergedRegion(new CellRangeAddress(0,0,0,11));

            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"No.","Item Name","Category","OPB","Purch.",
                    "P. Return","Sell","S. Return","Increase","Wastage","Total Stock","Unit","Rate","Amount"};

            for (int i=0;i<headers.length;i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            double total = 0;

            for (DateWiseStockRowDto row : data.getItems()) {
                Row r = sheet.createRow(rowNum++);
                r.createCell(0).setCellValue(row.getSrNo());
                r.createCell(1).setCellValue(row.getItemName());
                r.createCell(2).setCellValue(row.getCategoryName());
                r.createCell(3).setCellValue(row.getOpb());
                r.createCell(4).setCellValue(row.getPurchase());
                r.createCell(5).setCellValue(row.getPurchaseReturn());
                r.createCell(6).setCellValue(row.getSell());
                r.createCell(7).setCellValue(row.getSellReturn());
                r.createCell(8).setCellValue(row.getIncrease());
                r.createCell(9).setCellValue(row.getWastage());
                r.createCell(10).setCellValue(row.getFinalTotal());
                r.createCell(11).setCellValue(row.getUnit());
                r.createCell(12).setCellValue(row.getRate()!=null?row.getRate().doubleValue():0);

                double amt = row.getAmount()!=null?row.getAmount().doubleValue():0;
                r.createCell(13).setCellValue(amt);

                total += amt;
            }

            Row totalRow = sheet.createRow(rowNum);
            totalRow.createCell(12).setCellValue("Total");
            totalRow.createCell(13).setCellValue(total);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ================= Helpers =================
    private Cell createCell(String text, Color bg) {
        return new Cell()
                .add(new Paragraph(text != null ? text : ""))
                .setBackgroundColor(bg);
    }

    private Cell createRightCell(String text, Color bg) {
        return new Cell()
                .add(new Paragraph(text != null ? text : ""))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBackgroundColor(bg);
    }

    private String fmt(double val) {
        return String.format("%.2f", val);
    }


    @Override
    public DateWiseStockReportResponseDto getReport(
            Long categoryId,
            Long stockTypeId,
            String fromDate,
            String toDate,
            Long userId,
            Integer pageNo,
            Integer pageSize,
            String search) {

        LocalDate from = (fromDate != null && !fromDate.isEmpty())
                ? LocalDate.parse(fromDate, inputFormatter)
                : LocalDate.of(2000, 1, 1);

        LocalDate to = (toDate != null && !toDate.isEmpty())
                ? LocalDate.parse(toDate, inputFormatter)
                : LocalDate.now();

        // ── Normalize search ──────────────────────────────────────────────────
        String searchTerm = (search != null && !search.trim().isEmpty())
                ? search.trim() : "";

        // ── Get raw material IDs from ledger ──────────────────────────────────
        List<Long> ledgerIds;
		/*
		 * if (stockTypeId != null && stockTypeId != 0 && kitchenTypeId != null &&
		 * kitchenTypeId != 0) {
		 */

       Long selectedStockTypeId = null;
        Long selectedKitchenTypeId = null;

        if (stockTypeId != null && stockTypeId != 0) {

            StockTypeEntity type = stockTypeRepository
                    .findById(stockTypeId)
                    .orElse(null);

            if (type != null) {
                if (type.getMainType() == 0) {
                    selectedStockTypeId = type.getId();
                } else {
                    selectedKitchenTypeId = type.getId();
                }
            }
        }
        ledgerIds = stockLedgerRepository
                .findActiveRawMaterialIdsByDateRangeAndStockTypeAndKitchenType(
                        from,
                        to,
                        selectedStockTypeId,
                        selectedKitchenTypeId,
                        userId);

        List<Long> openingIds = stockLedgerRepository
                .findActiveRawMaterialIdsBeforeDate(
                        from,
                        selectedStockTypeId,
                        selectedKitchenTypeId,
                        userId);
       /* } else if (stockTypeId != null && stockTypeId != 0) {

            ledgerIds = stockLedgerRepository
                    .findActiveRawMaterialIdsByDateRangeAndStockType(
                            from, to, stockTypeId);

        } else if (kitchenTypeId != null && kitchenTypeId != 0) {

            ledgerIds = stockLedgerRepository
                    .findActiveRawMaterialIdsByDateRangeAndKitchenType(
                            from, to, kitchenTypeId);

        } else {

            ledgerIds = stockLedgerRepository
                    .findActiveRawMaterialIdsByDateRange(from, to);
        }*/
        
       
		/*
		 * if (stockTypeId != null && stockTypeId != 0) { ledgerIds =
		 * stockLedgerRepository .findActiveRawMaterialIdsByDateRangeAndStockType(from,
		 * to, stockTypeId); } else { ledgerIds = stockLedgerRepository
		 * .findActiveRawMaterialIdsByDateRange(from, to); }
		 */

        // ── Also get IDs of items with opbStock > 0 ───────────────────────────
		/*
		 * List<Long> opbIds = rawMaterialRepository
		 * .findIdsByUserIdAndOpbStockGreaterThanZero(userId);
		 */

        // ── Merge without duplicates ──────────────────────────────────────────
		/*
		 * Set<Long> mergedSet = new LinkedHashSet<>(ledgerIds);
		 * mergedSet.addAll(opbIds); List<Long> activeIds = new ArrayList<>(mergedSet);
		 */
        
        // Master OPB > 0
        List<Long> opbIds = rawMaterialRepository
                .findIdsByUserIdAndOpbStockGreaterThanZero(userId);

        // Merge all IDs
        Set<Long> mergedSet = new LinkedHashSet<>();
        mergedSet.addAll(ledgerIds);
        mergedSet.addAll(openingIds);
        mergedSet.addAll(opbIds);

        List<Long> activeIds = new ArrayList<>(mergedSet);

        System.out.println("Selected Stock Type : " + selectedStockTypeId);
        System.out.println("Selected Kitchen Type : " + selectedKitchenTypeId);
        System.out.println("Ledger IDs : " + ledgerIds);
        System.out.println("OPB IDs : " + opbIds);
        System.out.println("Merged IDs : " + activeIds);

        // ── Fetch page ────────────────────────────────────────────────────────
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<RawMaterialMasterEntity> page;
        List<RawMaterialMasterEntity> s1 = new ArrayList<>();
        if (activeIds.isEmpty()) {
            page = Page.empty(pageable);

        } else if (categoryId != null && categoryId != 0) {
        	if(!searchTerm.isEmpty()) {
        		page = rawMaterialRepository
                        .findByIdInAndRawMaterialCatIdAndUserIdAndIsDeleteFalseAndNameEnglishContainingIgnoreCase(
                                activeIds, categoryId, userId, searchTerm, pageable);
        	} else {
        		page = rawMaterialRepository
                        .findByIdInAndRawMaterialCatIdAndUserIdAndIsDeleteFalse(
                                activeIds, categoryId, userId, pageable);
        		s1 = rawMaterialRepository.findByIdInAndRawMaterialCatIdAndUserIdAndIsDeleteFalse(activeIds, categoryId, userId);
        	}
            
        } else {
        	if(!searchTerm.isEmpty()) {
        		page = rawMaterialRepository
                        .findByIdInAndUserIdAndIsDeleteFalseAndNameEnglishContainingIgnoreCase(
                                activeIds, userId, searchTerm, pageable);
        	} else {
        		page = rawMaterialRepository
                        .findByIdInAndUserIdAndIsDeleteFalse(
                                activeIds, userId, pageable);
        	}
        }
        System.out.print(s1);
        // ── Category & stock type display names ───────────────────────────────
        String categoryName = (categoryId != null && categoryId != 0)
                ? categoryRepository.findById(categoryId)
                        .map(c -> c.getNameEnglish()).orElse("All")
                : "All";

        String stockTypeName = (stockTypeId != null && stockTypeId != 0)
                ? stockTypeRepository.findById(stockTypeId)
                        .map(s -> s.getNameEnglish()).orElse("All")
                : "All";

        // ── Build rows ────────────────────────────────────────────────────────
        List<DateWiseStockRowDto> items = new ArrayList<>();
        int srNo = ((pageNo - 1) * pageSize) + 1;

        for (RawMaterialMasterEntity rm : page.getContent()) {

            Long rmId = rm.getId();

            // ── Resolve unit conversion once per raw material ─────────────────
            String displayUnitName = rm.getUnit() != null
                    ? rm.getUnit().getNameEnglish() : "-";
            boolean hasParentUnit = false;
            UnitMasterEntity unitForConversion = null;

            if (rm.getUnit() != null) {
                java.util.Optional<UnitMasterEntity> rmUnitOp =
                        unitMasterRepository.findByIdAndIsParentUnitFalse(rm.getUnit().getId());

                if (rmUnitOp.isPresent()
                        && rmUnitOp.get().getParentUnit() != null
                        && rmUnitOp.get().getEquivalentValue() != null
                        && rmUnitOp.get().getEquivalentValue() != 0.0) {

                    hasParentUnit     = true;
                    unitForConversion = rmUnitOp.get();
                    displayUnitName   = rmUnitOp.get().getParentUnit().getNameEnglish();
                }
            }

         // ── Opening Balance — always show ─────────────────────────────────────
          //  double opb = rm.getOpbStock() != null ? rm.getOpbStock().doubleValue() : 0.0;
            LocalDate openingDate = from.minusDays(1);

            double purchaseBefore;

            if (selectedStockTypeId != null) {

                purchaseBefore = stockLedgerRepository
                        .sumQtyInByRawMaterialAndDateLessThanAndRefTypeAndStockType(
                                rmId, from, selectedStockTypeId, "PURCHASE");

            } else if (selectedKitchenTypeId != null) {

                purchaseBefore = stockLedgerRepository
                        .sumQtyInByRawMaterialAndDateLessThanAndRefTypeAndKitchenType(
                                rmId, from, selectedKitchenTypeId, "PURCHASE");

            } else {

                purchaseBefore = stockLedgerRepository
                        .sumQtyInByRawMaterialAndDateLessThanAndRefType(
                                rmId, from, "PURCHASE");
            }
            
            double purchaseReturnBefore;

            if (selectedStockTypeId != null) {

            	purchaseReturnBefore = stockLedgerRepository
                        .sumQtyOutByRawMaterialAndDateLessThanAndRefTypeAndStockType(
                                rmId, from, selectedStockTypeId, "PURCHASE_RETURN");

            } else if (selectedKitchenTypeId != null) {

            	purchaseReturnBefore = stockLedgerRepository
                        .sumQtyOutByRawMaterialAndDateLessThanAndRefTypeAndKitchenType(
                                rmId, from, selectedKitchenTypeId, "PURCHASE_RETURN");

            } else {

            	purchaseReturnBefore = stockLedgerRepository
                        .sumQtyOutByRawMaterialAndDateLessThanAndRefType(
                                rmId, from, "PURCHASE_RETURN");
            }
            
            double sellBefore;

            if (selectedStockTypeId != null) {

                sellBefore = stockLedgerRepository
                        .sumStoreIssueQtyBeforeDateByStockType(
                                rmId, from, selectedStockTypeId);

            } else if (selectedKitchenTypeId != null) {

                sellBefore = stockLedgerRepository
                        .sumStoreIssueQtyBeforeDateByKitchenType(
                                rmId, from, selectedKitchenTypeId);

            } else {

                sellBefore = stockLedgerRepository
                        .sumSellQtyBeforeDate(rmId, from);
            }

            
            double sellReturnBefore ;
            
            if (selectedStockTypeId != null) {

                sellReturnBefore = stockLedgerRepository
                        .sumStoreIssueReturnBeforeDateByStockType(
                                rmId,
                                from,
                                selectedStockTypeId);


            } else if (selectedKitchenTypeId != null) {

                sellReturnBefore = stockLedgerRepository
                        .sumStoreIssueReturnBeforeDateByKitchenType(
                                rmId,
                                from,
                                selectedKitchenTypeId);


            } else {

                sellReturnBefore = stockLedgerRepository
                        .sumSellReturnBeforeDate(rmId, from);
            }

            double increaseBefore =
                    stockLedgerRepository.sumQtyInByRawMaterialAndDateLessThanAndRefType(
                            rmId, from, "INCREASE");

            double wastageBefore =
                    stockLedgerRepository.sumQtyOutByRawMaterialAndDateLessThanAndRefType(
                            rmId, from, "WASTAGE");
            
            double opb  = rm.getOpbStock().doubleValue()
                        + purchaseBefore
                        - purchaseReturnBefore
                        - sellBefore
                        + sellReturnBefore
                        + increaseBefore
                        - wastageBefore;
           
           
            if (hasParentUnit) {
                opb = convertUnitQtyToParentQty(unitForConversion, opb);
            }

            // ── Purchase — only when NO stockType filter ──────────────────────────
            double purchase = 0.0;
            double purchaseReturn = 0.0;

            if (selectedStockTypeId == null || selectedStockTypeId == 0) {
                purchase = stockLedgerRepository
                        .sumQtyInByRawMaterialAndDateRangeAndRefType(rmId, from, to, "PURCHASE");
                if (hasParentUnit) {
                    purchase = convertUnitQtyToParentQty(unitForConversion, purchase);
                }

                purchaseReturn = stockLedgerRepository
                        .sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to, "PURCHASE_RETURN");
                if (hasParentUnit) {
                    purchaseReturn = convertUnitQtyToParentQty(unitForConversion, purchaseReturn);
                }
            }else {
            	purchase = stockLedgerRepository
                        .sumQtyInByRawMaterialAndDateRangeAndRefTypeAndStockType(rmId, from, to, selectedStockTypeId, "PURCHASE");
                if (hasParentUnit) {
                    purchase = convertUnitQtyToParentQty(unitForConversion, purchase);
                }

                purchaseReturn = stockLedgerRepository
                        .sumQtyOutByRawMaterialAndDateRangeAndRefTypeAndStockType(rmId, from, to, selectedStockTypeId, "PURCHASE_RETURN");
                if (hasParentUnit) {
                    purchaseReturn = convertUnitQtyToParentQty(unitForConversion, purchaseReturn);
                }
            }

            // ── Sell ──────────────────────────────────────────────────────────────
            double sell;
            
            if (selectedStockTypeId != null && selectedStockTypeId != 0) {
                // stockType selected → only that stockType's store issue qty
                sell = stockLedgerRepository
                        .sumStoreIssueQtyByStockType(rmId, from, to, selectedStockTypeId);
                sell += stockLedgerRepository
                        .sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to, "SOT_STORE_ISSUE");
            }else if (selectedKitchenTypeId != null && selectedKitchenTypeId != 0) {
                // stockType selected → only that stockType's store issue qty
                sell = stockLedgerRepository
                        .sumStoreIssueQtyByKitchenType(rmId, from, to, selectedKitchenTypeId);
                sell += stockLedgerRepository
                        .sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to, "SOT_STORE_ISSUE");
            } else {
                // no stockType → all sell types
                double storeIssue = stockLedgerRepository
                        .sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to, "STORE_ISSUE");
                double chefRequisition = stockLedgerRepository
                        .sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to, "CHEF_REQUISITION");
                double sotStoreIssue = stockLedgerRepository
                        .sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to, "SOT_STORE_ISSUE");
                sell = storeIssue + chefRequisition + sotStoreIssue;
            }
            if (hasParentUnit) {
                sell = convertUnitQtyToParentQty(unitForConversion, sell);
            }
            
            double sellReturn = 0.0;

            if (selectedStockTypeId != null && selectedStockTypeId != 0) {

                // Stock Type wise
                sellReturn = stockLedgerRepository
                        .sumQtyInByRawMaterialAndDateRangeAndRefTypeAndStockType(
                                rmId, from, to, selectedStockTypeId, "STORE_ISSUE_RETURN");

                sellReturn += stockLedgerRepository
                        .sumQtyInByRawMaterialAndDateRangeAndRefType(
                                rmId, from, to, "SOT_STORE_RETURN");

            } else if (selectedKitchenTypeId != null && selectedKitchenTypeId != 0) {

                // Kitchen Type wise
                sellReturn = stockLedgerRepository
                        .sumQtyInByRawMaterialAndDateRangeAndRefTypeAndKitchenType(
                                rmId, from, to, selectedKitchenTypeId, "STORE_ISSUE_RETURN");

                sellReturn += stockLedgerRepository
                        .sumQtyInByRawMaterialAndDateRangeAndRefType(
                                rmId, from, to, "SOT_STORE_RETURN");

            } else {

                // All
                sellReturn = stockLedgerRepository
                        .sumQtyInByRawMaterialAndDateRangeAndRefType(
                                rmId, from, to, "STORE_ISSUE_RETURN");

                sellReturn += stockLedgerRepository
                        .sumQtyInByRawMaterialAndDateRangeAndRefType(
                                rmId, from, to, "SOT_STORE_RETURN");
            }

            if (hasParentUnit) {
                sellReturn = convertUnitQtyToParentQty(unitForConversion, sellReturn);
            }

            double smIncrease = 0.0;
            double smWastage = 0.0;

            if (selectedStockTypeId == null || selectedStockTypeId == 0) {

                // No stock type filter → all stock types
                smIncrease = stockLedgerRepository
                        .sumQtyInByRawMaterialAndDateRangeAndRefType(
                                rmId, from, to, "INCREASE");

                smWastage = stockLedgerRepository
                        .sumQtyOutByRawMaterialAndDateRangeAndRefType(
                                rmId, from, to, "WASTAGE");

            } else {

                // Stock type selected → only selected stock type
                smIncrease = stockLedgerRepository
                        .sumQtyInByRawMaterialAndDateRangeAndRefTypeAndStockType(
                                rmId, from, to, selectedStockTypeId, "INCREASE");

                smWastage = stockLedgerRepository
                        .sumQtyOutByRawMaterialAndDateRangeAndRefTypeAndStockType(
                                rmId, from, to, selectedStockTypeId, "WASTAGE");
            }

            if (hasParentUnit) {
                smIncrease = convertUnitQtyToParentQty(unitForConversion, smIncrease);
                smWastage = convertUnitQtyToParentQty(unitForConversion, smWastage);
            }

            String remarks = detailRepository.getRemarks(rmId, from, to);           
           

            // ── Final Total ───────────────────────────────────────────────────────
            double finalTotal = opb + purchase - purchaseReturn - sell + sellReturn+ smIncrease - smWastage;
            finalTotal = BigDecimal.valueOf(finalTotal)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();

            // ── Amount ────────────────────────────────────────────────────────
            BigDecimal rate = rm.getSupplierRate() != null
                    ? rm.getSupplierRate() : BigDecimal.ZERO;
            BigDecimal amount = rate.multiply(BigDecimal.valueOf(finalTotal))
                    .setScale(2, RoundingMode.HALF_UP);

            // ── Build row ─────────────────────────────────────────────────────
            DateWiseStockRowDto row = new DateWiseStockRowDto();
            row.setSrNo(srNo++);
            row.setCategoryName(rm.getRawMaterialCat() != null
                    ? rm.getRawMaterialCat().getNameEnglish() : "-");
            row.setItemName(rm.getNameEnglish());
            row.setOpb(opb);
            row.setPurchase(purchase);
            row.setPurchaseReturn(purchaseReturn);
            row.setSell(sell);
            row.setSellReturn(sellReturn);
            row.setIncrease(smIncrease);
            row.setWastage(smWastage);
            row.setFinalTotal(finalTotal);
            row.setUnit(displayUnitName);
            row.setRate(rate);
            row.setAmount(amount);
            row.setRemarks(remarks);

            items.add(row);
        }

        // ── Build response ────────────────────────────────────────────────────
        DateWiseStockReportResponseDto response = new DateWiseStockReportResponseDto();
        response.setFromDate(fromDate != null ? fromDate : "All");
        response.setToDate(toDate != null ? toDate : "All");
        response.setCategoryName(categoryName);
        response.setStockType(stockTypeName);
        response.setItems(items);
        response.setTotalItems(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());

        return response;
    }

    // ── Shared conversion helper ──────────────────────────────────────────────
    private Double convertUnitQtyToParentQty(UnitMasterEntity unit, Double qty) {
        if (qty == null || qty == 0.0) return 0.0;
        return BigDecimal.valueOf(qty)
                .divide(BigDecimal.valueOf(unit.getEquivalentValue()), 2, RoundingMode.HALF_UP)
                .doubleValue();
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