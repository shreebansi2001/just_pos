package com.crmportal.config;

import java.util.*;

/**
 * Master catalogue of report modules, groups, and report names.
 *
 * ── HOW TO EXTEND ─────────────────────────────────────────────────────────
 * To add a new module/group/report, simply add entries to REPORT_CATALOGUE.
 * The format is:
 *
 *   moduleName → [ groupName → [ reportName1, reportName2, ... ] ]
 *
 * The service reads this map to build the modal UI response and to know
 * which reports exist when building the GET response.
 * ──────────────────────────────────────────────────────────────────────────
 */
public class ReportModuleConfig {

    private ReportModuleConfig() {}

    /**
     * Ordered map: Module → (Group → Reports list)
     * Change this map to add/remove modules, groups or reports.
     */
    public static final LinkedHashMap<String, LinkedHashMap<String, List<String>>> REPORT_CATALOGUE;

    static {
        REPORT_CATALOGUE = new LinkedHashMap<>();

        // ── Menu Planning ─────────────────────────────────────────────────
        LinkedHashMap<String, List<String>> menuPlanning = new LinkedHashMap<>();
        menuPlanning.put("Exclusive", Arrays.asList(
                "Exclusive Report 1",
                "Exclusive Report 2",
                "Exclusive Report 3"
        ));
        menuPlanning.put("Backoffice", Arrays.asList(
                "Backoffice Report 1",
                "Backoffice Report 2",
                "Backoffice Report 3"
        ));
        REPORT_CATALOGUE.put("Menu Planning", menuPlanning);

        // ── Menu Allocation ───────────────────────────────────────────────
        LinkedHashMap<String, List<String>> menuAllocation = new LinkedHashMap<>();
        menuAllocation.put("Allocation Summary", Arrays.asList(
                "Allocation Report 1",
                "Allocation Report 2"
        ));
        REPORT_CATALOGUE.put("Menu Allocation", menuAllocation);

        // ── Inventory ─────────────────────────────────────────────────────
        LinkedHashMap<String, List<String>> inventory = new LinkedHashMap<>();
        inventory.put("Stock", Arrays.asList(
                "Stock Ledger Report",
                "Stock Summary Report",
                "Purchase Report"
        ));
        inventory.put("SOT", Arrays.asList(
                "SOT Report",
                "Manual PO Report"
        ));
        REPORT_CATALOGUE.put("Inventory", inventory);

        // ── Billing ───────────────────────────────────────────────────────
        LinkedHashMap<String, List<String>> billing = new LinkedHashMap<>();
        billing.put("Invoice", Arrays.asList(
                "Sales Invoice Report",
                "Purchase Invoice Report"
        ));
        REPORT_CATALOGUE.put("Billing", billing);
    }
}