package com.crmportal.service.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.AccountEntryEntity;
import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.VendorPaymentEntity;
import com.crmportal.enums.EntryType;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.BankTransactionAccountRepository;
import com.crmportal.repository.BankTransactionVendorRepository;
import com.crmportal.response.dto.BankTransactionDTO;
import com.crmportal.response.dto.BankTransactionReportDTO;
import com.crmportal.service.BankTransactionReportService;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.font.*;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.borders.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class BankTransactionReportServiceImpl
        implements BankTransactionReportService {

    @Autowired
    private BankDetailsRepository bankRepo;

    @Autowired
    private BankTransactionVendorRepository vendorRepo;

    @Autowired
    private BankTransactionAccountRepository accountRepo;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Get Report Data ───────────────────────────────────────────────────
    @Override
    public BankTransactionReportDTO getReport(
            Long bankId, LocalDate fromDate, LocalDate toDate) {

        BankDetailsEntity bank = bankRepo.findByIdAndIsDeleteFalse(bankId)
                .orElseThrow(() -> new RuntimeException(
                        "Bank not found with id: " + bankId));

        List<BankTransactionDTO> transactions = new ArrayList<>();
        int srNo = 1;

        // ── From vendor_payments ──────────────────────────────────────────
        List<VendorPaymentEntity> vendorList =
                vendorRepo.findByBankAndDateRange(bankId, fromDate, toDate);

        for (VendorPaymentEntity v : vendorList) {
            BankTransactionDTO dto = new BankTransactionDTO();
            dto.setSrNo(srNo++);
            dto.setDate(v.getPaymentDate());
            dto.setSource("VENDOR_PAYMENT");
            dto.setVoucherNo(v.getInvoiceCode());
            dto.setContactName(v.getVendorCat());
            dto.setPaymentMode(v.getPaymentMode());
            dto.setNotes(v.getRemarks());
            dto.setReferenceNo(v.getReferenceId());
            dto.setDebit(v.getPayAmount() != null
                    ? v.getPayAmount() : BigDecimal.ZERO);
            dto.setCredit(v.getReceivedAmount() != null
                    ? v.getReceivedAmount() : BigDecimal.ZERO);
            transactions.add(dto);
        }

        // ── From account_entry ────────────────────────────────────────────
        List<AccountEntryEntity> accountList =
                accountRepo.findByBankAndDateRange(bankId, fromDate, toDate);

        for (AccountEntryEntity a : accountList) {
            BankTransactionDTO dto = new BankTransactionDTO();
            dto.setSrNo(srNo++);
            dto.setDate(a.getDate());
            dto.setSource("ACCOUNT_ENTRY");
            dto.setVoucherNo(a.getVoucherNo());
            dto.setContactName(a.getAccountContactName());
            dto.setPaymentMode(a.getPaymentMode() != null
                    ? a.getPaymentMode().name() : "");
            dto.setNotes(a.getNotes());
            dto.setReferenceNo(a.getReferenceNo());

            if (EntryType.CREDIT.equals(a.getEntryType())) {
                dto.setCredit(a.getAmount() != null
                        ? a.getAmount() : BigDecimal.ZERO);
                dto.setDebit(BigDecimal.ZERO);
            } else {
                dto.setDebit(a.getAmount() != null
                        ? a.getAmount() : BigDecimal.ZERO);
                dto.setCredit(BigDecimal.ZERO);
            }
            transactions.add(dto);
        }

        // ── Sort by date ──────────────────────────────────────────────────
        transactions.sort(Comparator.comparing(
                BankTransactionDTO::getDate,
                Comparator.nullsLast(Comparator.naturalOrder())));

        for (int i = 0; i < transactions.size(); i++) {
            transactions.get(i).setSrNo(i + 1);
        }

        // ── Totals ────────────────────────────────────────────────────────
        BigDecimal totalCredit = transactions.stream()
                .map(t -> t.getCredit() != null ? t.getCredit() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebit = transactions.stream()
                .map(t -> t.getDebit() != null ? t.getDebit() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal openingBalance = bank.getOpeningBalance() != null
                ? bank.getOpeningBalance() : BigDecimal.ZERO;

        BigDecimal closingBalance = openingBalance
                .add(totalCredit).subtract(totalDebit);

        // ── Build response ────────────────────────────────────────────────
        BankTransactionReportDTO report = new BankTransactionReportDTO();
        report.setBankName(bank.getBankName());
        report.setBranchName(bank.getBranchName());
        report.setAccountNo(bank.getAccountNo());
        report.setAccountHolderName(bank.getAccountHolderName());
        report.setFromDate(fromDate != null ? fromDate.format(FMT) : "");
        report.setToDate(toDate   != null ? toDate.format(FMT)   : "");
        report.setTotalCredit(totalCredit);
        report.setTotalDebit(totalDebit);
        report.setOpeningBalance(openingBalance);
        report.setClosingBalance(closingBalance);
        report.setTransactions(transactions);

        return report;
    }

    // ── Generate PDF ──────────────────────────────────────────────────────
    @Override
    public byte[] generatePdf(
            Long bankId, LocalDate fromDate, LocalDate toDate) {

        BankTransactionReportDTO report = getReport(bankId, fromDate, toDate);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter   writer   = new PdfWriter(baos);
            PdfDocument pdf      = new PdfDocument(writer);
            Document    document = new Document(pdf, PageSize.A4.rotate());
            document.setMargins(25, 25, 25, 25);

            PdfFont boldFont    = PdfFontFactory.createFont(
                    StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(
                    StandardFonts.HELVETICA);

            Color darkBlue    = new DeviceRgb(13,  71, 116);
            Color tableBlue   = new DeviceRgb(26,  99, 153);
            Color lightBlue   = new DeviceRgb(235, 245, 255);
            Color borderGray  = new DeviceRgb(200, 210, 220);
            Color labelGray   = new DeviceRgb(100, 120, 140);
            Color altRow      = new DeviceRgb(245, 248, 252);
            Color creditGreen = new DeviceRgb(0,  128,   0);
            Color debitRed    = new DeviceRgb(200,   0,   0);
            Color cardBg      = new DeviceRgb(245, 248, 252);

            // ── Title ─────────────────────────────────────────────────────
            document.add(new Paragraph("Bank Transaction Report")
                    .setFont(boldFont).setFontSize(18)
                    .setFontColor(darkBlue)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(4));

            // ── Divider ───────────────────────────────────────────────────
            Table div = new Table(
                    UnitValue.createPercentArray(new float[]{100f}));
            div.setWidth(UnitValue.createPercentValue(100));
            div.addCell(new Cell().setHeight(2f)
                    .setBackgroundColor(darkBlue)
                    .setBorder(Border.NO_BORDER));
            document.add(div);
            document.add(new Paragraph("").setMarginBottom(8));

            // ── Info Band ─────────────────────────────────────────────────
            float[] infoW = {20f, 30f, 20f, 30f};
            Table infoTable = new Table(
                    UnitValue.createPercentArray(infoW));
            infoTable.setWidth(UnitValue.createPercentValue(100));

            addInfoCell(infoTable, "Bank Name",
                    report.getBankName(),            lightBlue, labelGray, boldFont, regularFont, borderGray);
            addInfoCell(infoTable, "Account No",
                    report.getAccountNo(),           lightBlue, labelGray, boldFont, regularFont, borderGray);
            addInfoCell(infoTable, "Branch",
                    report.getBranchName(),          lightBlue, labelGray, boldFont, regularFont, borderGray);
            addInfoCell(infoTable, "Account Holder",
                    report.getAccountHolderName(),   lightBlue, labelGray, boldFont, regularFont, borderGray);
            addInfoCell(infoTable, "From Date",
                    nvl(report.getFromDate()),       lightBlue, labelGray, boldFont, regularFont, borderGray);
            addInfoCell(infoTable, "To Date",
                    nvl(report.getToDate()),         lightBlue, labelGray, boldFont, regularFont, borderGray);
            addInfoCell(infoTable, "Opening Balance",
                    fmt(report.getOpeningBalance()), lightBlue, labelGray, boldFont, regularFont, borderGray);
            addInfoCell(infoTable, "Closing Balance",
                    fmt(report.getClosingBalance()), lightBlue, labelGray, boldFont, regularFont, borderGray);

            document.add(infoTable);
            document.add(new Paragraph("").setMarginBottom(10));

            // ── Transactions Table ────────────────────────────────────────
            float[] colW = {4f, 9f, 12f, 15f, 12f, 12f, 18f, 9f, 9f};
            Table txTable = new Table(
                    UnitValue.createPercentArray(colW));
            txTable.setWidth(UnitValue.createPercentValue(100));

            String[] headers = {
                "#", "Date", "Source", "Contact",
                "Voucher No", "Ref No", "Notes",
                "Credit", "Debit"
            };
            TextAlignment[] aligns = {
                TextAlignment.CENTER, TextAlignment.CENTER,
                TextAlignment.LEFT,   TextAlignment.LEFT,
                TextAlignment.LEFT,   TextAlignment.LEFT,
                TextAlignment.LEFT,   TextAlignment.RIGHT,
                TextAlignment.RIGHT
            };

            for (int i = 0; i < headers.length; i++) {
                txTable.addHeaderCell(new Cell()
                        .add(new Paragraph(headers[i])
                                .setFont(boldFont).setFontSize(8)
                                .setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(tableBlue)
                        .setTextAlignment(aligns[i])
                        .setPaddingTop(5).setPaddingBottom(5)
                        .setBorder(new SolidBorder(borderGray, 0.3f)));
            }

            boolean alt = false;
            for (BankTransactionDTO tx : report.getTransactions()) {
                Color bg = alt ? altRow : ColorConstants.WHITE;
                alt = !alt;

                txTable.addCell(dCell(
                        String.valueOf(tx.getSrNo()),
                        bg, regularFont, TextAlignment.CENTER, borderGray));
                txTable.addCell(dCell(
                        tx.getDate() != null
                                ? tx.getDate().format(DATE_FMT) : "-",
                        bg, regularFont, TextAlignment.CENTER, borderGray));
                txTable.addCell(dCell(
                        nvl(tx.getSource()),
                        bg, regularFont, TextAlignment.LEFT, borderGray));
                txTable.addCell(dCell(
                        nvl(tx.getContactName()),
                        bg, regularFont, TextAlignment.LEFT, borderGray));
                txTable.addCell(dCell(
                        nvl(tx.getVoucherNo()),
                        bg, regularFont, TextAlignment.LEFT, borderGray));
                txTable.addCell(dCell(
                        nvl(tx.getReferenceNo()),
                        bg, regularFont, TextAlignment.LEFT, borderGray));
                txTable.addCell(dCell(
                        nvl(tx.getNotes()),
                        bg, regularFont, TextAlignment.LEFT, borderGray));

                // Credit — green
                txTable.addCell(new Cell()
                        .add(new Paragraph(fmt(tx.getCredit()))
                                .setFont(regularFont).setFontSize(8)
                                .setFontColor(tx.getCredit() != null
                                        && tx.getCredit().signum() > 0
                                        ? creditGreen : labelGray))
                        .setBackgroundColor(bg)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setBorder(new SolidBorder(borderGray, 0.3f))
                        .setPaddingTop(3).setPaddingBottom(3).setPaddingRight(4));

                // Debit — red
                txTable.addCell(new Cell()
                        .add(new Paragraph(fmt(tx.getDebit()))
                                .setFont(regularFont).setFontSize(8)
                                .setFontColor(tx.getDebit() != null
                                        && tx.getDebit().signum() > 0
                                        ? debitRed : labelGray))
                        .setBackgroundColor(bg)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setBorder(new SolidBorder(borderGray, 0.3f))
                        .setPaddingTop(3).setPaddingBottom(3).setPaddingRight(4));
            }

            // ── Total Row ─────────────────────────────────────────────────
            Color totalBg = new DeviceRgb(13, 71, 116);
            txTable.addCell(new Cell(1, 7)
                    .add(new Paragraph("TOTAL")
                            .setFont(boldFont).setFontSize(9)
                            .setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(totalBg)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorder(new SolidBorder(borderGray, 0.3f))
                    .setPaddingRight(8).setPaddingTop(5).setPaddingBottom(5));

            txTable.addCell(new Cell()
                    .add(new Paragraph(fmt(report.getTotalCredit()))
                            .setFont(boldFont).setFontSize(9)
                            .setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(totalBg)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorder(new SolidBorder(borderGray, 0.3f))
                    .setPaddingTop(5).setPaddingBottom(5).setPaddingRight(4));

            txTable.addCell(new Cell()
                    .add(new Paragraph(fmt(report.getTotalDebit()))
                            .setFont(boldFont).setFontSize(9)
                            .setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(totalBg)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorder(new SolidBorder(borderGray, 0.3f))
                    .setPaddingTop(5).setPaddingBottom(5).setPaddingRight(4));

            document.add(txTable);

            // ── Summary Cards ─────────────────────────────────────────────
            document.add(new Paragraph("").setMarginBottom(10));

            float[] cardW = {24f, 1f, 24f, 1f, 24f, 1f, 25f};
            Table cardTable = new Table(
                    UnitValue.createPercentArray(cardW));
            cardTable.setWidth(UnitValue.createPercentValue(100));

            cardTable.addCell(buildCard(
                    "OPENING BALANCE", fmt(report.getOpeningBalance()),
                    cardBg, darkBlue,    labelGray, boldFont, regularFont, borderGray));
            cardTable.addCell(new Cell().setBorder(Border.NO_BORDER));
            cardTable.addCell(buildCard(
                    "TOTAL CREDIT",    fmt(report.getTotalCredit()),
                    cardBg, creditGreen, labelGray, boldFont, regularFont, borderGray));
            cardTable.addCell(new Cell().setBorder(Border.NO_BORDER));
            cardTable.addCell(buildCard(
                    "TOTAL DEBIT",     fmt(report.getTotalDebit()),
                    cardBg, debitRed,   labelGray, boldFont, regularFont, borderGray));
            cardTable.addCell(new Cell().setBorder(Border.NO_BORDER));
            cardTable.addCell(buildCard(
                    "CLOSING BALANCE", fmt(report.getClosingBalance()),
                    cardBg, darkBlue,   labelGray, boldFont, regularFont, borderGray));

            document.add(cardTable);
            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to generate bank transaction PDF", e);
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private void addInfoCell(Table table, String label, String value,
            Color bg, Color labelColor, PdfFont boldFont,
            PdfFont regularFont, Color borderColor) {
        table.addCell(new Cell()
                .add(new Paragraph(label)
                        .setFont(regularFont).setFontSize(7)
                        .setFontColor(labelColor).setMarginBottom(2))
                .add(new Paragraph(nvl(value))
                        .setFont(boldFont).setFontSize(9)
                        .setFontColor(ColorConstants.BLACK))
                .setBackgroundColor(bg).setPadding(8)
                .setBorder(new SolidBorder(borderColor, 0.5f)));
    }

    private Cell dCell(String text, Color bg, PdfFont font,
            TextAlignment align, Color borderColor) {
        return new Cell()
                .add(new Paragraph(nvl(text))
                        .setFont(font).setFontSize(8))
                .setBackgroundColor(bg)
                .setTextAlignment(align)
                .setBorder(new SolidBorder(borderColor, 0.3f))
                .setPaddingTop(3).setPaddingBottom(3)
                .setPaddingLeft(3).setPaddingRight(3);
    }

    private Cell buildCard(String label, String value,
            Color cardBg, Color valueColor, Color labelColor,
            PdfFont boldFont, PdfFont regularFont, Color borderColor) {
        return new Cell()
                .add(new Paragraph(label)
                        .setFont(regularFont).setFontSize(7)
                        .setFontColor(labelColor).setMarginBottom(4))
                .add(new Paragraph(value)
                        .setFont(boldFont).setFontSize(16)
                        .setFontColor(valueColor))
                .setBackgroundColor(cardBg)
                .setBorder(new SolidBorder(borderColor, 0.8f))
                .setPadding(10);
    }

    private String nvl(String s) {
        return s != null && !s.isEmpty() ? s : "-";
    }

    private String fmt(BigDecimal val) {
        return val != null ? String.format("%.2f", val) : "0.00";
    }
}