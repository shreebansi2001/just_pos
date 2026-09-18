package com.crmportal.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.dto.GstPurchaseReportDTO;
import com.crmportal.dto.GstReportResponseDTO;
import com.crmportal.dto.GstSalesReportDTO;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

@Service
public class GstReportPdfService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DeviceRgb tableBlue  = new DeviceRgb(26, 99, 153);
    private static final DeviceRgb FOOTER_BG      = new DeviceRgb(255, 220, 120);  // slightly darker orange for footer
    private static final DeviceRgb BORDER_COLOR   = new DeviceRgb(180, 180, 180);
    private static final DeviceRgb WHITE           = new DeviceRgb(255, 255, 255);

    // ─── Sales PDF ─────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public byte[] generateSalesPdf(GstReportResponseDTO reportData) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer   = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document  = new Document(pdfDoc, PageSize.A4.rotate());
        document.setMargins(25, 20, 25, 20);

        PdfFont bold   = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // ── Title box ────────────────────────────────────────────────────────
        String title = buildTitle("SALE", reportData.getFromDate(), reportData.getToDate());
        document.add(buildTitleTable(title, bold));

        // ── Data table ───────────────────────────────────────────────────────
        // Sr. | DATE | INV NO | NAME | GST NO | BASIC AMT | CGST | SGST | IGST | TOTAL
        float[] colWidths = {34, 48, 80, 120, 120, 65, 52, 52, 52, 67};
        Table table = new Table(UnitValue.createPercentArray(colWidths))
                .useAllAvailableWidth()
                .setFontSize(8);

        String[] headers = {"Sr.", "DATE", "INV / QT NO", "NAME", "GST NO",
                            "BASIC AMOUNT", "CGST", "SGST", "IGST", "TOTAL AMOUNT"};
        for (String h : headers) {
            table.addHeaderCell(headerCell(h, bold));
        }

        // ── Totals accumulators ──────────────────────────────────────────────
        BigDecimal sumBasic  = BigDecimal.ZERO;
        BigDecimal sumCgst   = BigDecimal.ZERO;
        BigDecimal sumSgst   = BigDecimal.ZERO;
        BigDecimal sumIgst   = BigDecimal.ZERO;
        BigDecimal sumTotal  = BigDecimal.ZERO;

        List<GstSalesReportDTO> rows = (List<GstSalesReportDTO>) reportData.getData();
        for (GstSalesReportDTO row : rows) {
            table.addCell(dataCell(String.valueOf(row.getSrNo()),                               normal, TextAlignment.CENTER));
            table.addCell(dataCell(row.getDate() != null ? row.getDate().format(DATE_FMT) : "", normal, TextAlignment.CENTER));
            table.addCell(dataCell(nvl(row.getInvNo()),                                         normal, TextAlignment.CENTER));
            table.addCell(dataCell(nvl(row.getName()),                                          normal, TextAlignment.LEFT));
            table.addCell(dataCell(nvl(row.getGstNumber()),                                     normal, TextAlignment.LEFT));
            table.addCell(dataCell(fmtNum(row.getBasicAmount()),                                normal, TextAlignment.RIGHT));
            table.addCell(dataCell(fmtNum(row.getCgst()),                                       normal, TextAlignment.RIGHT));
            table.addCell(dataCell(fmtNum(row.getSgst()),                                       normal, TextAlignment.RIGHT));
            table.addCell(dataCell(fmtNum(row.getIgst()),                                       normal, TextAlignment.RIGHT));
            table.addCell(dataCell(fmtNum(row.getTotal()),                                      normal, TextAlignment.RIGHT));

            // Accumulate
            sumBasic  = sumBasic .add(toBD(row.getBasicAmount()));
            sumCgst   = sumCgst  .add(toBD(row.getCgst()));
            sumSgst   = sumSgst  .add(toBD(row.getSgst()));
            sumIgst   = sumIgst  .add(toBD(row.getIgst()));
            sumTotal  = sumTotal .add(toBD(row.getTotal()));
        }

        // ── Footer / Totals row ──────────────────────────────────────────────
        // "TOTAL" label spans non-numeric columns (Sr, Date, InvNo, Name, GstNo) = 5 cols
        table.addCell(footerLabelCell("TOTAL", bold, 5));
        table.addCell(footerValueCell(fmtBD(sumBasic),  bold));
        table.addCell(footerValueCell(fmtBD(sumCgst),   bold));
        table.addCell(footerValueCell(fmtBD(sumSgst),   bold));
        table.addCell(footerValueCell(fmtBD(sumIgst),   bold));
        table.addCell(footerValueCell(fmtBD(sumTotal),  bold));

        document.add(table);
        document.close();
        return baos.toByteArray();
    }

    // ─── Purchase PDF ──────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public byte[] generatePurchasePdf(GstReportResponseDTO reportData) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer   = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document  = new Document(pdfDoc, PageSize.A4.rotate());
        document.setMargins(30, 25, 30, 25);

        PdfFont bold   = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // ── Title ─────────────────────────────────────────────────────────────
        String title = buildTitle("PURCHASE", reportData.getFromDate(), reportData.getToDate());
        document.add(buildTitleTable(title, bold));

        // ── Data table ────────────────────────────────────────────────────────
        // Sr|Date|BillNo|Name|ProductName|Qty|BasicAmt|CGST|SGST|IGST|Total
        float[] colWidths = {28, 52, 45, 120, 120, 28, 68, 38, 38, 38, 60};
        Table table = new Table(UnitValue.createPercentArray(colWidths))
                .useAllAvailableWidth()
                .setFontSize(8);

        String[] headers = {"Sr.", "DATE", "BILL NO", "NAME", "PRODUCT NAME",
                            "QTY", "BASIC AMOUNT", "CGST", "SGST", "IGST", "TOTAL AMOUNT"};
        for (String h : headers) {
            table.addHeaderCell(headerCell(h, bold));
        }

        // ── Totals accumulators ──────────────────────────────────────────────
        double sumBasic   = 0;
        double sumCgst    = 0;
        double sumSgst    = 0;
        double sumIgst    = 0;
        double sumTotal   = 0;

        List<GstPurchaseReportDTO> rows = (List<GstPurchaseReportDTO>) reportData.getData();
        for (GstPurchaseReportDTO row : rows) {
            table.addCell(dataCell(String.valueOf(row.getSrNo()),                               normal, TextAlignment.CENTER));
            table.addCell(dataCell(row.getDate() != null ? row.getDate().format(DATE_FMT) : "", normal, TextAlignment.CENTER));
            table.addCell(dataCell(nvl(row.getBillNo()),                                        normal, TextAlignment.CENTER));
            table.addCell(dataCell(nvl(row.getName()),                                          normal, TextAlignment.LEFT));
            table.addCell(dataCell(nvl(row.getProductName()),                                   normal, TextAlignment.LEFT));
            table.addCell(dataCell(fmtQty(row.getQty()),                                        normal, TextAlignment.RIGHT));
            table.addCell(dataCell(fmtFloat(row.getBasicBillAmt()),                             normal, TextAlignment.RIGHT));
            table.addCell(dataCell(fmtFloat(row.getCgstAmt()),                                  normal, TextAlignment.RIGHT));
            table.addCell(dataCell(fmtFloat(row.getSgstAmt()),                                  normal, TextAlignment.RIGHT));
            table.addCell(dataCell(fmtFloat(row.getIgstAmt()),                                  normal, TextAlignment.RIGHT));
            table.addCell(dataCell(fmtFloat(row.getTotal()),                                    normal, TextAlignment.RIGHT));

            // Accumulate
            sumBasic  += row.getBasicBillAmt();
            sumCgst   += row.getCgstAmt();
            sumSgst   += row.getSgstAmt();
            sumIgst   += row.getIgstAmt();
            sumTotal  += row.getTotal();
        }

        // ── Footer / Totals row ──────────────────────────────────────────────
        // "TOTAL" label spans non-numeric columns (Sr, Date, BillNo, Name, ProductName) = 5 cols
        table.addCell(footerLabelCell("TOTAL", bold, 6));
        table.addCell(footerValueCell(fmt2f(sumBasic),         bold));
        table.addCell(footerValueCell(fmt2f(sumCgst),          bold));
        table.addCell(footerValueCell(fmt2f(sumSgst),          bold));
        table.addCell(footerValueCell(fmt2f(sumIgst),          bold));
        table.addCell(footerValueCell(fmt2f(sumTotal),         bold));

        document.add(table);
        document.close();
        return baos.toByteArray();
    }

    // ─── Shared Helpers ────────────────────────────────────────────────────────

    private Table buildTitleTable(String title, PdfFont bold) {
        Table titleTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                .useAllAvailableWidth()
                .setMarginBottom(8);
        titleTable.addCell(new Cell()
                .add(new Paragraph(title)
                        .setFont(bold)
                        .setFontSize(13)
                        .setTextAlignment(TextAlignment.CENTER))
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setPaddingTop(8)
                .setPaddingBottom(8)
                .setBackgroundColor(WHITE));
        return titleTable;
    }

    private String buildTitle(String type, String fromDate, String toDate) {
        try {
            int fromYear = Integer.parseInt(fromDate.substring(6));
            int toYear   = Integer.parseInt(toDate.substring(6));
            int startFY  = Math.min(fromYear, toYear);
            int endFY    = Math.max(fromYear, toYear);
            if (startFY == endFY) endFY = startFY + 1;
            return "GST REPORT - " + startFY + " - " + endFY + " - " + type.toUpperCase();
        } catch (Exception e) {
            return "GST REPORT - " + type.toUpperCase();
        }
    }

    /** Orange column header cell */
    private Cell headerCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text)
                        .setFont(font)
                        .setFontSize(10)
                        .setFontColor(ColorConstants.WHITE)
                        .setTextAlignment(TextAlignment.CENTER))
                .setBackgroundColor(tableBlue)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(BORDER_COLOR, 0.5f))
                .setPadding(4);
    }

    /** Footer label cell — spans multiple columns, same orange but bold */
    private Cell footerLabelCell(String text, PdfFont bold, int colspan) {
        return new Cell(1, colspan)
                .add(new Paragraph(text)
                        .setFont(bold)
                        .setFontSize(12)
                        .setFontColor(ColorConstants.BLACK)
                        .setTextAlignment(TextAlignment.RIGHT))
                .setBackgroundColor(FOOTER_BG)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(BORDER_COLOR, 0.5f))
                .setPaddingTop(4)
                .setPaddingBottom(4)
                .setPaddingRight(6);
    }

    /** Footer value cell — numeric totals, same footer background */
    private Cell footerValueCell(String text, PdfFont bold) {
        return new Cell()
                .add(new Paragraph(text != null ? text : "0")
                        .setFont(bold)
                        .setFontSize(12)
                        .setFontColor(ColorConstants.BLACK))
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBackgroundColor(FOOTER_BG)
                .setBorder(new SolidBorder(BORDER_COLOR, 0.5f))
                .setPaddingTop(4)
                .setPaddingBottom(4)
                .setPaddingLeft(4)
                .setPaddingRight(4);
    }

    private Cell dataCell(String text, PdfFont font, TextAlignment align) {
        return new Cell()
                .add(new Paragraph(text != null ? text : "")
                        .setFont(font)
                        .setFontSize(10))
                .setTextAlignment(align)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(BORDER_COLOR, 0.3f))
                .setKeepTogether(true)
                .setPaddingTop(3)
                .setPaddingBottom(3)
                .setPaddingLeft(4)
                .setPaddingRight(4);
    }

    // ─── Formatters ────────────────────────────────────────────────────────────

    private String nvl(String s)         { return s != null ? s : ""; }
    private String fmtFloat(float v)     { return String.format("%.2f", v); }
    private String fmt2f(double v)       { return String.format("%.2f", v); }
    private String fmtBD(BigDecimal v)   { return v != null ? String.format("%.2f", v) : "0.00"; }
    private String fmtQty(double v)      { return v == Math.floor(v) ? String.valueOf((int) v) : String.valueOf(v); }

    private BigDecimal toBD(Object n) {
        if (n == null) return BigDecimal.ZERO;
        if (n instanceof BigDecimal)    return (BigDecimal) n;
        if (n instanceof java.math.BigInteger) return new BigDecimal((java.math.BigInteger) n);
        if (n instanceof Double)        return BigDecimal.valueOf((Double) n);
        if (n instanceof Float)         return BigDecimal.valueOf((double)(Float) n);
        try { return new BigDecimal(n.toString()); } catch (Exception e) { return BigDecimal.ZERO; }
    }

    private String fmtNum(Object n) {
        if (n == null) return "0";
        if (n instanceof Float)                return String.format("%.2f", (Float) n);
        if (n instanceof Double)               return String.format("%.2f", (Double) n);
        if (n instanceof BigDecimal)           return String.format("%.2f", ((BigDecimal) n).doubleValue());
        if (n instanceof java.math.BigInteger) return ((java.math.BigInteger) n).toString();
        return n.toString();
    }
}