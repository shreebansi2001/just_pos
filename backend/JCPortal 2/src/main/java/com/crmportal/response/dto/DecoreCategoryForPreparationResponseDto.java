package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecoreCategoryForPreparationResponseDto {

    private Long decoreCategoryId;
    private String decoreCategoryName;
    private Integer decoreCatSortOrder;
    private String decoreCatSlogan;
    private String decoreCatNotes;

    private String decoreCategoryNameHindi;
    private String decoreCategoryNameGujarati;

    private String decoreNotesHindi;
    private String decoreNotesGujarati;

    private String startTime;

    private Boolean isDecoreCatAddons;

    private Long catImgId;
    private Long bgImgId;

    private Integer catSpace;
    private Integer anyItem;

    private String subCat;
    private String subCatHindi;
    private String subCatGujarati;
}