package com.crmportal.response.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor                  // keep no-arg constructor
// REMOVED @AllArgsConstructor      // ← this was auto-generating the wrong constructor count
public class MenuItemForReportResponseDto {

    private Long   id;
    private String nameEnglish;
    private String nameHindi;
    private String nameGujarati;
    private String slogan;
    private String imagePath;
    private String itemNotes;
    private String partyName;       // chef / vendor name
    private String subItem;
    private String subItemHindi;
    private String subItemGujarati;
    private Integer itemSpace;
    private String itemStatus;
    private String itemHeading;
    private Boolean isAddOnItem;

    // ── 7-arg constructor — matches the existing JPQL query (partyName = "" by default)
    public MenuItemForReportResponseDto(Long id, String nameEnglish, String nameHindi,
            String nameGujarati, String slogan, String imagePath, String itemNotes) {
        this.id           = id;
        this.nameEnglish  = nameEnglish;
        this.nameHindi    = nameHindi;
        this.nameGujarati = nameGujarati;
        this.slogan       = slogan;
        this.imagePath    = imagePath;
        this.itemNotes    = itemNotes;
        this.partyName    = "";     // filled later by createMenuItem() via r[47]
        this.subItem      = subItem;
    }

    // ── 8-arg constructor — for future use if needed
    public MenuItemForReportResponseDto(Long id, String nameEnglish, String nameHindi,
            String nameGujarati, String slogan, String imagePath, String itemNotes,
            String partyName) {
        this.id           = id;
        this.nameEnglish  = nameEnglish;
        this.nameHindi    = nameHindi;
        this.nameGujarati = nameGujarati;
        this.slogan       = slogan;
        this.imagePath    = imagePath;
        this.itemNotes    = itemNotes;
        this.partyName    = partyName;
        this.subItem      = subItem;
    }
}