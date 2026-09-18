package com.crmportal.service;

import java.util.List;
import java.util.Map;

import com.crmportal.entity.UserMasterEntity;
import com.crmportal.request.dto.AssignDbRequest;
import com.crmportal.request.dto.DBExcelRequestDto;

public interface DatabasePlanningService {
	Long saveDatabasePlanningEntity(DBExcelRequestDto dbExcelRequestDto, String uuid, Map<String, List<String>> errorMap);

	Map<String, Object> getAllDatabasePlanningEntities();

	Map<String, Object> getByDbPlanningIdAndDbName(String dbPlanningId);
	
	Map<String, Object> getByDbPlanningId(String parentDbId);
	
	Map<String, Object> assignDbToUser(AssignDbRequest assignDbRequest);
	
	String getOrCreateUserUuid(UserMasterEntity userMasterEntity);
	
	void deleteDbPlanningEntity(Long dbPlanningId);
}
