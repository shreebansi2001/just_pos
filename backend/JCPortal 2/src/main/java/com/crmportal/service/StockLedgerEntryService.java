package com.crmportal.service;

import com.crmportal.entity.*;

public interface StockLedgerEntryService {
    void savePurchase(PurchaseOrderEntity po);
    void savePurchaseReturn(PurchaseOrderReturnEntity por);
    void saveStoreIssue(PurchaseOrderStoreEntity storeIssue);
    void saveStoreIssueReturn(StoreIssueReturnEntity sir);
    void saveChefRequisition(ChefRequisitionEntity cr);
    
    void reversePurchase(Long poId);
    void reversePurchaseReturn(Long porId);
    void reverseStoreIssue(Long poId);
    void reverseStoreIssueReturn(Long sirId);
    void saveStoreManageIncrease(RawMaterialMasterEntity rm,
            Double qty, StoreManageEntity storeManage, Long userId, RawMaterialCategoryMasterEntity rawMaterialCategoryMasterEntity, UnitMasterEntity unitMasterEntity);

    void saveStoreManageWastage(RawMaterialMasterEntity rm,
            Double qty, StoreManageEntity storeManage, Long userId, RawMaterialCategoryMasterEntity rawMaterialCategoryMasterEntity, UnitMasterEntity unitMasterEntity);
}