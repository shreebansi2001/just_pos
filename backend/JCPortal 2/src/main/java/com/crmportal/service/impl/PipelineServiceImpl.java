package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.PipelineCloseStageEntity;
import com.crmportal.entity.PipelineEntity;
import com.crmportal.entity.PipelineOpenStageEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.LeadMasterRepository;
import com.crmportal.repository.PipelineCloseStageRepository;
import com.crmportal.repository.PipelineOpenStageRepository;
import com.crmportal.repository.PipelineRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.PipelineCloseStageRequestDto;
import com.crmportal.request.dto.PipelineOpenStageRequestDto;
import com.crmportal.request.dto.PipelineRequestDto;
import com.crmportal.response.dto.EmployeeLeadPerformanceForReportResponseDto;
import com.crmportal.response.dto.EmployeeLeadPerformanceResponseDto;
import com.crmportal.response.dto.EmployeePerformanceGroupDto;
import com.crmportal.response.dto.LeadDistributionResponseDto;
import com.crmportal.response.dto.LeadPipelineResponseDto;
import com.crmportal.response.dto.PipelineCloseStageResponseDto;
import com.crmportal.response.dto.PipelineOpenStageResponseDto;
import com.crmportal.response.dto.PipelineResponseDto;
import com.crmportal.response.dto.PipelineStageResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.PipelineService;
import com.crmportal.utility.DateMapper;

@Service
public class PipelineServiceImpl implements PipelineService {

	@Autowired
	PipelineRepository pipelineRepository;

	@Autowired
	PipelineOpenStageRepository pipelineOpenStageRepository;

	@Autowired
	PipelineCloseStageRepository pipelineCloseStageRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	DateMapper dateMapperp;

	@Autowired
	LeadMasterRepository leadMasterRepository;

	@Override
	@Transactional
	public PipelineResponseDto createUpdatePipeLine(PipelineRequestDto request) {

		UserMasterEntity userEntity = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User Not Found"));

		PipelineEntity pipelineEntity;

		if (request.getId() == -1) {
			pipelineEntity = new PipelineEntity();
		} else {
			pipelineEntity = pipelineRepository.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new RuntimeException("Pipeline not found with id : " + request.getId()));
		}
		pipelineEntity.setPipelineName(request.getPipelineName());
		pipelineEntity.setUser(userEntity);
		pipelineEntity = pipelineRepository.save(pipelineEntity);

		PipelineEntity finalPipelineEntity = pipelineEntity;

		List<PipelineOpenStageResponseDto> openStageResponse = request.getOpenStages().stream()
				.map(s -> saveOpenStage(s, finalPipelineEntity, userEntity)).collect(Collectors.toList());

		List<PipelineCloseStageResponseDto> closeStageResponse = request.getCloseStages().stream()
				.map(s -> saveCloseStage(s, finalPipelineEntity, userEntity)).collect(Collectors.toList());

		PipelineResponseDto pipelineResponseDto = new PipelineResponseDto();
		pipelineResponseDto.setId(pipelineEntity.getId());
		pipelineResponseDto.setPipelineName(pipelineEntity.getPipelineName());
		pipelineResponseDto.setUserId(pipelineEntity.getUser().getId());
		pipelineResponseDto.setOpenStages(openStageResponse);
		pipelineResponseDto.setCloseStages(closeStageResponse);
		pipelineResponseDto.setCreatedAt(dateTimeFormatted(pipelineEntity.getCreatedAt()));

		return pipelineResponseDto;
	}

	private PipelineOpenStageResponseDto saveOpenStage(PipelineOpenStageRequestDto stageDto, PipelineEntity pipeline,
			UserMasterEntity userEntity) {

		PipelineOpenStageEntity entity;

		if (stageDto.getId() != null && stageDto.getId() != 0) {
			entity = pipelineOpenStageRepository.findById(stageDto.getId()).orElse(new PipelineOpenStageEntity());
		} else {
			entity = new PipelineOpenStageEntity();
		}

		entity.setStage(stageDto.getStage());
		entity.setPipeline(pipeline);
		entity.setUser(userEntity);

		entity = pipelineOpenStageRepository.save(entity);

		PipelineOpenStageResponseDto response = new PipelineOpenStageResponseDto();
		response.setId(entity.getId());
		response.setStage(entity.getStage());

		return response;
	}

	private PipelineCloseStageResponseDto saveCloseStage(PipelineCloseStageRequestDto stageDto, PipelineEntity pipeline,
			UserMasterEntity userEntity) {

		PipelineCloseStageEntity entity;

		if (stageDto.getId() != null && stageDto.getId() != 0) {
			entity = pipelineCloseStageRepository.findById(stageDto.getId()).orElse(new PipelineCloseStageEntity());
		} else {
			entity = new PipelineCloseStageEntity();
		}

		entity.setStage(stageDto.getStage());
		entity.setPipeline(pipeline);
		entity.setUser(userEntity);

		entity = pipelineCloseStageRepository.save(entity);

		PipelineCloseStageResponseDto response = new PipelineCloseStageResponseDto();
		response.setId(entity.getId());
		response.setStage(entity.getStage());

		return response;
	}

	@Override
	@Transactional
	public Boolean deletePipeLine(Long pipelineId) {
		PipelineEntity pipelineEntity = pipelineRepository.findByIdAndIsDeleteFalse(pipelineId)
				.orElseThrow(() -> new RuntimeException("Pipeline not found with id : " + pipelineId));

		/*
		 * if (leadMasterRepository.existsByPipelineAndIsDeleteFalse(pipelineEntity)) {
		 * throw new
		 * RuntimeException("Pipeline already exists in Lead. Please delete it first.");
		 * }
		 */

		List<PipelineOpenStageEntity> openStageEntities = pipelineOpenStageRepository
				.findByPipelineAndIsDeleteFalse(pipelineEntity);

		List<PipelineCloseStageEntity> closeStageEntities = pipelineCloseStageRepository
				.findByPipelineAndIsDeleteFalse(pipelineEntity);

		/*
		 * List<String> existingOpenStages = openStageEntities.stream() .filter(stage ->
		 * leadMasterRepository.existsByOpenStageAndIsDeleteFalse(stage))
		 * .map(PipelineOpenStageEntity::getStage).collect(Collectors.toList());
		 * 
		 * List<String> existingCloseStages = closeStageEntities.stream() .filter(stage
		 * -> leadMasterRepository.existsByCloseStageAndIsDeleteFalse(stage))
		 * .map(PipelineCloseStageEntity::getStage).collect(Collectors.toList());
		 */

		/*
		 * if (!existingOpenStages.isEmpty() || !existingCloseStages.isEmpty()) {
		 * 
		 * StringBuilder message = new StringBuilder();
		 * 
		 * if (!existingOpenStages.isEmpty()) {
		 * message.append("Open stages already used in Leads: ").append(String.join(", "
		 * , existingOpenStages)); }
		 * 
		 * if (!existingCloseStages.isEmpty()) { if (message.length() > 0) {
		 * message.append(" | "); }
		 * message.append("Close stages already used in Leads: ").append(String.
		 * join(", ", existingCloseStages)); }
		 * 
		 * throw new RuntimeException(message.toString()); }
		 */

		openStageEntities.forEach(stage -> stage.setIsDelete(true));
		pipelineOpenStageRepository.saveAll(openStageEntities);

		closeStageEntities.forEach(stage -> stage.setIsDelete(true));
		pipelineCloseStageRepository.saveAll(closeStageEntities);

		pipelineEntity.setIsDelete(true);
		pipelineRepository.save(pipelineEntity);

		return true;
	}

	@Override
	public List<PipelineResponseDto> getAllPipeLine(Long userId) {

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User Not Found"));
		List<PipelineResponseDto> responseDtos = new ArrayList<>();
		List<PipelineEntity> entities = pipelineRepository.findByIsDeleteFalseAndUser(user);
		List<PipelineOpenStageResponseDto> openStage = new ArrayList<>();
		List<PipelineCloseStageResponseDto> closeStage = new ArrayList<>();

		for (PipelineEntity entity : entities) {
			PipelineResponseDto dto = new PipelineResponseDto();
			dto.setId(entity.getId());
			dto.setPipelineName(entity.getPipelineName());
			dto.setUserId(entity.getUser().getId());
			dto.setCreatedAt(commonService.dateTimeFormatted(entity.getCreatedAt()));

			List<PipelineOpenStageEntity> openStageEntities = pipelineOpenStageRepository
					.findByPipelineAndIsDeleteFalse(entity);
			for (PipelineOpenStageEntity stage : openStageEntities) {
				PipelineOpenStageResponseDto openStageResponseDto = new PipelineOpenStageResponseDto();
				openStageResponseDto.setId(stage.getId());
				openStageResponseDto.setStage(stage.getStage());
				openStage.add(openStageResponseDto);
			}

			List<PipelineCloseStageEntity> closeStageEntities = pipelineCloseStageRepository
					.findByPipelineAndIsDeleteFalse(entity);
			for (PipelineCloseStageEntity stage : closeStageEntities) {
				PipelineCloseStageResponseDto closeStageResponseDto = new PipelineCloseStageResponseDto();
				closeStageResponseDto.setId(stage.getId());
				closeStageResponseDto.setStage(stage.getStage());
				closeStage.add(closeStageResponseDto);
			}

			dto.setOpenStages(openStage);
			dto.setCloseStages(closeStage);

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public Map<String, Object> getStageLeadDataByPipelineId(Long pipelineId, Long memberId, Long userId) {

		List<Object[]> result = pipelineRepository.findLeadDataStagesByPipelineId(pipelineId, memberId, userId);

		Map<String, List<LeadPipelineResponseDto>> openStageLeads = new LinkedHashMap<>();
		Map<String, List<LeadPipelineResponseDto>> closeStageLeads = new LinkedHashMap<>();
		BigDecimal openStageAmount = BigDecimal.ZERO;
		BigDecimal closeStageAmount = BigDecimal.ZERO;
		BigDecimal totlaAmount = BigDecimal.ZERO;
		BigDecimal hotLeadAmount = BigDecimal.ZERO;
		BigDecimal coldLeadAmount = BigDecimal.ZERO;
		BigDecimal lostLeadAmount = BigDecimal.ZERO;
		BigDecimal wonLeadAmount = BigDecimal.ZERO;
		BigDecimal inquiryLeadAmount = BigDecimal.ZERO;
		BigDecimal confirmLeadAmount = BigDecimal.ZERO;
		BigDecimal cancelLeadAmount = BigDecimal.ZERO;

		int totalCount = 0;
		int openStageCount = 0;
		int closeStageCount = 0;
		int hotLeadCount = 0;
		int coldLeadCount = 0;
		int inquiryLeadCount = 0;
		int lostLeadCount = 0;
		int wonLeadCount = 0;
		int confirmLeadCount = 0;
		int cancelLeadCount = 0;

		for (Object[] row : result) {
			int index = 0;

			LeadPipelineResponseDto dto = new LeadPipelineResponseDto();
			dto.setPipelineId(commonService.getLong(row[index++]));
			dto.setStageType(commonService.getString(row[index++]));
			dto.setStageId(commonService.getLong(row[index++]));
			dto.setStage(commonService.getString(row[index++]));
			dto.setLeadId(commonService.getLong(row[index++]));
			dto.setClientName(commonService.getString(row[index++]));
			dto.setEstimateAmount(commonService.getBigDecimal(row[index++]));

			Object followUpObj = row[index++];

			if (followUpObj != null) {
				Timestamp ts = (Timestamp) followUpObj;
				dto.setLeadFollowUpDate(dateMapperp.dateTimeToString(ts.toLocalDateTime()));
			} else {
				dto.setLeadFollowUpDate(null);
			}

			dto.setCity(commonService.getString(row[index++]));
			dto.setLeadAssignId(commonService.getLong(row[index++]));
			dto.setLeadAssignName(commonService.getString(row[index++]) + " " + commonService.getString(row[index++]));
			dto.setLeadCode(commonService.getString(row[index++]));

			Object createdAt = row[index++];
			Object updatedAt = row[index++];

			dto.setPlanName(commonService.getString(row[index++]));

			if (createdAt != null) {
				dto.setLeadCreatedAt(dateMapperp.dateTimeWithSecondToString(((Timestamp) createdAt).toLocalDateTime()));
			}
			if (updatedAt != null) {
				dto.setLeadUpdatedAt(dateMapperp.dateTimeWithSecondToString(((Timestamp) updatedAt).toLocalDateTime()));
			}

			Object closeDateObj = row[index++];
			LocalDateTime closeDate = null;

			if (closeDateObj != null) {
				closeDate = ((Timestamp) closeDateObj).toLocalDateTime();
			}
			dto.setCloseDate(dateMapperp.dateTimeToString(closeDate));

			dto.setDescription(commonService.getString(row[index++]));
			dto.setClientContactNo(commonService.getString(row[index++]));
			dto.setLeadSource(commonService.getString(row[index++]));
			String stageName = dto.getStage();

			if (dto.getStageType().equalsIgnoreCase("open_stage")) {
				openStageLeads.computeIfAbsent(stageName, k -> new ArrayList<>());
				if (dto.getClientName() != null) {
					openStageLeads.get(stageName).add(dto);
					BigDecimal estimateAmount = dto.getEstimateAmount() != null ? dto.getEstimateAmount()
							: BigDecimal.ZERO;
					openStageAmount = openStageAmount.add(estimateAmount);
					totlaAmount = totlaAmount.add(estimateAmount);
					openStageCount++;
					totalCount++;

					if (stageName.equalsIgnoreCase("hot")) {
						hotLeadCount++;
						hotLeadAmount = hotLeadAmount.add(estimateAmount);
					} else if (stageName.equalsIgnoreCase("cold")) {
						coldLeadCount++;
						coldLeadAmount = coldLeadAmount.add(estimateAmount);
					} else if (stageName.equalsIgnoreCase("inquiry")) {
						inquiryLeadCount++;
						inquiryLeadAmount = inquiryLeadAmount.add(estimateAmount);
					}
				}
			} else if (dto.getStageType().equalsIgnoreCase("close_stage")) {
				closeStageLeads.computeIfAbsent(stageName, k -> new ArrayList<>());
				if (dto.getClientName() != null) {
					closeStageLeads.get(stageName).add(dto);
					BigDecimal estimateAmount = dto.getEstimateAmount() != null ? dto.getEstimateAmount()
							: BigDecimal.ZERO;
					closeStageAmount = closeStageAmount.add(estimateAmount);
					totlaAmount = totlaAmount.add(estimateAmount);
					closeStageCount++;
					totalCount++;

					if (stageName.equalsIgnoreCase("lost")) {
						lostLeadCount++;
						lostLeadAmount = lostLeadAmount.add(estimateAmount);
					} else if (stageName.equalsIgnoreCase("won")) {
						wonLeadCount++;
						wonLeadAmount = wonLeadAmount.add(estimateAmount);
					} else if (stageName.equalsIgnoreCase("confirm")) {
						confirmLeadCount++;
						confirmLeadAmount = confirmLeadAmount.add(estimateAmount);
					} else if (stageName.equalsIgnoreCase("cancel")) {
						cancelLeadCount++;
						cancelLeadAmount = cancelLeadAmount.add(estimateAmount);
					}
				}
			}
		}

		Map<String, Object> response = new HashMap<>();
		response.put("open_lead", openStageLeads);
		response.put("close_lead", closeStageLeads);
		response.put("open_lead_count", String.valueOf(openStageCount));
		response.put("close_lead_count", String.valueOf(closeStageCount));
		response.put("total_lead_count", String.valueOf(totalCount));
		response.put("open_lead_amount", openStageAmount.toString());
		response.put("close_lead_amount", closeStageAmount.toString());
		response.put("hot_lead_count", String.valueOf(hotLeadCount));
		response.put("hot_lead_amount", hotLeadAmount.toString());
		response.put("cold_lead_count", String.valueOf(coldLeadCount));
		response.put("cold_lead_amount", coldLeadAmount.toString());
		response.put("lost_lead_count", String.valueOf(lostLeadCount));
		response.put("lost_lead_amount", lostLeadAmount.toString());
		response.put("won_lead_count", String.valueOf(wonLeadCount));
		response.put("won_lead_amount", wonLeadAmount.toString());
		response.put("inquiry_lead_count", String.valueOf(inquiryLeadCount));
		response.put("inquiry_lead_amount", inquiryLeadAmount.toString());
		response.put("confirm_lead_count", String.valueOf(confirmLeadCount));
		response.put("confirm_lead_amount", confirmLeadAmount.toString());
		response.put("cancel_lead_count", String.valueOf(cancelLeadCount));
		response.put("cancel_lead_amount", cancelLeadAmount.toString());
		response.put("total_amount", totlaAmount.toString());

		return response;
	}

	public Map<String, Object> getStageLeadDataByPipelineIdAndStage(Long pipelineId, Long userId, String stage,
			Long memberId) {

		List<Object[]> result = pipelineRepository.findLeadDataStagesByPipelineId(pipelineId, memberId, userId);

		Map<String, List<LeadPipelineResponseDto>> openStageLeads = new LinkedHashMap<>();
		Map<String, List<LeadPipelineResponseDto>> closeStageLeads = new LinkedHashMap<>();
		BigDecimal openStageAmount = BigDecimal.ZERO;
		BigDecimal closeStageAmount = BigDecimal.ZERO;
		BigDecimal totlaAmount = BigDecimal.ZERO;
		BigDecimal hotLeadAmount = BigDecimal.ZERO;
		BigDecimal coldLeadAmount = BigDecimal.ZERO;
		BigDecimal lostLeadAmount = BigDecimal.ZERO;
		BigDecimal wonLeadAmount = BigDecimal.ZERO;
		BigDecimal inquiryLeadAmount = BigDecimal.ZERO;
		BigDecimal confirmLeadAmount = BigDecimal.ZERO;
		BigDecimal cancelLeadAmount = BigDecimal.ZERO;
		
		int totalCount = 0;
		int openStageCount = 0;
		int closeStageCount = 0;
		int hotLeadCount = 0;
		int coldLeadCount = 0;
		int lostLeadCount = 0;
		int wonLeadCount = 0;
		int confirmLeadCount = 0;
		int cancelLeadCount = 0;
		int inquiryLeadCount = 0;
		
		for (Object[] row : result) {
			int index = 0;

			LeadPipelineResponseDto dto = new LeadPipelineResponseDto();
			dto.setPipelineId(commonService.getLong(row[index++]));
			dto.setStageType(commonService.getString(row[index++]));
			dto.setStageId(commonService.getLong(row[index++]));
			dto.setStage(commonService.getString(row[index++]));
			dto.setLeadId(commonService.getLong(row[index++]));
			dto.setClientName(commonService.getString(row[index++]));
			BigDecimal estimateAmount = commonService.getBigDecimal(row[index++]);
			if (estimateAmount == null) {
				estimateAmount = BigDecimal.ZERO;
			}
			dto.setEstimateAmount(estimateAmount);

			Object followUpObj = row[index++];

			if (followUpObj != null) {
				Timestamp ts = (Timestamp) followUpObj;
				dto.setLeadFollowUpDate(dateMapperp.dateTimeToString(ts.toLocalDateTime()));
			} else {
				dto.setLeadFollowUpDate(null);
			}

			dto.setCity(commonService.getString(row[index++]));
			dto.setLeadAssignId(commonService.getLong(row[index++]));
			dto.setLeadAssignName(commonService.getString(row[index++]) + " " + commonService.getString(row[index++]));
			dto.setLeadCode(commonService.getString(row[index++]));

			Object createdAt = row[index++];
			Object updatedAt = row[index++];

			dto.setPlanName(commonService.getString(row[index++]));

			if (createdAt != null) {
				dto.setLeadCreatedAt(dateMapperp.dateTimeWithSecondToString(((Timestamp) createdAt).toLocalDateTime()));
			}
			if (updatedAt != null) {
				dto.setLeadUpdatedAt(dateMapperp.dateTimeWithSecondToString(((Timestamp) updatedAt).toLocalDateTime()));
			}
			Object closeDateObj = row[index++];
			LocalDateTime closeDate = null;

			if (closeDateObj != null) {
				closeDate = ((Timestamp) closeDateObj).toLocalDateTime();
			}
			dto.setCloseDate(dateMapperp.dateTimeToString(closeDate));

			dto.setDescription(commonService.getString(row[index++]));
			dto.setClientContactNo(commonService.getString(row[index++]));
			dto.setLeadSource(commonService.getString(row[index++]));
			String stageName = dto.getStage();

			if (dto.getStageType().equalsIgnoreCase("open_stage")) {
				openStageLeads.computeIfAbsent(stageName, k -> new ArrayList<>());
				if (dto.getClientName() != null && stage.equalsIgnoreCase(stageName)) {
					openStageLeads.get(stageName).add(dto);
					openStageAmount = openStageAmount.add(dto.getEstimateAmount());
					totlaAmount = totlaAmount.add(dto.getEstimateAmount());
					openStageCount++;
					totalCount++;

					if (stageName.equalsIgnoreCase("hot")) {
						hotLeadCount++;
						hotLeadAmount = hotLeadAmount.add(dto.getEstimateAmount());
					} else if (stageName.equalsIgnoreCase("cold")) {
						coldLeadCount++;
						coldLeadAmount = coldLeadAmount.add(dto.getEstimateAmount());
					} else if (stageName.equalsIgnoreCase("inquiry")) {
						inquiryLeadCount++;
						inquiryLeadAmount = inquiryLeadAmount.add(estimateAmount);
					}
				}
			} else if (dto.getStageType().equalsIgnoreCase("close_stage")) {
				closeStageLeads.computeIfAbsent(stageName, k -> new ArrayList<>());
				if (dto.getClientName() != null && stage.equalsIgnoreCase(stageName)) {
					closeStageLeads.get(stageName).add(dto);
					closeStageAmount = closeStageAmount.add(dto.getEstimateAmount());
					totlaAmount = totlaAmount.add(dto.getEstimateAmount());
					closeStageCount++;
					totalCount++;

					if (stageName.equalsIgnoreCase("lost")) {
						lostLeadCount++;
						lostLeadAmount = lostLeadAmount.add(dto.getEstimateAmount());
					} else if (stageName.equalsIgnoreCase("won")) {
						wonLeadCount++;
						wonLeadAmount = wonLeadAmount.add(dto.getEstimateAmount());
					} else if (stageName.equalsIgnoreCase("confirm")) {
						confirmLeadCount++;
						confirmLeadAmount = confirmLeadAmount.add(estimateAmount);
					} else if (stageName.equalsIgnoreCase("cancel")) {
						cancelLeadCount++;
						cancelLeadAmount = cancelLeadAmount.add(estimateAmount);
					}
				}
			}
		}

		Map<String, Object> response = new HashMap<>();
		response.put("open_lead", openStageLeads);
		response.put("close_lead", closeStageLeads);
		response.put("open_lead_count", String.valueOf(openStageCount));
		response.put("close_lead_count", String.valueOf(closeStageCount));
		response.put("total_lead_count", String.valueOf(totalCount));
		response.put("oprn_lead_amount", openStageAmount.toString());
		response.put("close_lead_amount", closeStageAmount.toString());
		response.put("hot_lead_count", String.valueOf(hotLeadCount));
		response.put("hot_lead_amount", hotLeadAmount.toString());
		response.put("cold_lead_count", String.valueOf(coldLeadCount));
		response.put("cold_lead_amount", coldLeadAmount.toString());
		response.put("lost_lead_count", String.valueOf(lostLeadCount));
		response.put("lost_lead_amount", lostLeadAmount.toString());
		response.put("won_lead_count", String.valueOf(wonLeadCount));
		response.put("won_lead_amount", wonLeadAmount.toString());
		response.put("inquiry_lead_count", String.valueOf(inquiryLeadCount));
		response.put("inquiry_lead_amount", inquiryLeadAmount.toString());
		response.put("confirm_lead_count", String.valueOf(confirmLeadCount));
		response.put("confirm_lead_amount", confirmLeadAmount.toString());
		response.put("cancel_lead_count", String.valueOf(cancelLeadCount));
		response.put("cancel_lead_amount", cancelLeadAmount.toString());
		response.put("total_amount", totlaAmount.toString());

		return response;
	}

	@Override
	public Map<String, Object> getStageByPipelineId(Long pipelineId, Long userId) {

		List<Object[]> result = pipelineRepository.findStagesByPipelineId(pipelineId, userId);

		List<PipelineStageResponseDto> openStage = new ArrayList<>();
		List<PipelineStageResponseDto> closeStage = new ArrayList<>();

		for (Object[] row : result) {
			int index = 0;

			PipelineStageResponseDto dto = new PipelineStageResponseDto();
			dto.setStageType(commonService.getString(row[index++]));
			dto.setPipelineId(commonService.getLong(row[index++]));
			dto.setStageId(commonService.getLong(row[index++]));
			dto.setStageName(commonService.getString(row[index++]));

			String stageType = dto.getStageType();

			if (stageType.equalsIgnoreCase("open_stage")) {
				openStage.add(dto);
			} else if (stageType.equalsIgnoreCase("close_stage")) {
				closeStage.add(dto);
			}
		}

		Map<String, Object> response = new HashMap<>();
		response.put("open_stage", openStage);
		response.put("close_close", closeStage);

		return response;
	}

	public String dateTimeFormatted(LocalDateTime dateTime) {
		if (dateTime == null)
			return null;
		return dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
	}

	@Override
	public Map<String, Object> getPerformance(Long userId, String startDate, String endDate, Long memberId) {
		Map<String, Object> map = new HashMap<>();
		int flag = 0;
		if (startDate != null && endDate != null) {
			flag = 1;
		}

		Object[] result = pipelineRepository.getPerformance(userId, startDate, endDate, flag, memberId);
		if (result == null) {
			throw new RuntimeException("Data not found");
		}
		Object[] row = (Object[]) result[0];

		int index = 0;
		map.put("completed_leads", row[index++]);
		map.put("overdue_leads", row[index++]);
		map.put("new_inquiry_leads", row[index++]);
		map.put("in_progress_leads", row[index++]);
		map.put("on_time_delivery", row[index++]);
		map.put("quality_score", row[index++]);

		List<Object[]> result2 = pipelineRepository.getLeadDistribution(userId, startDate, endDate, flag, memberId);

		List<LeadDistributionResponseDto> dtos = new ArrayList<>();

		for (Object[] row2 : result2) {
			int index2 = 0;

			LeadDistributionResponseDto dto = new LeadDistributionResponseDto();

			dto.setLeadAssignId(commonService.getLong(row2[index2++]));
			dto.setAssignName(commonService.getString(row2[index2++]));
			dto.setCompletedLeads(commonService.getInteger(row2[index2++]));
			dto.setPendingLeads(commonService.getInteger(row2[index2++]));
			dto.setTotalLeads(commonService.getInteger(row2[index2++]));

			dtos.add(dto);
		}

		map.put("lead_distribution", dtos);

		return map;
	}

	@Override
	public Map<String, Object> getEmployeePerformance(Long userId, String startDate, String endDate, Long pipelineId,
			Long memberId) {
		Map<String, Object> map = new HashMap<>();
		int flag = 0;
		Long pid = Long.valueOf(0);
		if (startDate != null && endDate != null) {
			flag = 1;
		}

		if (pipelineId != null) {
			pid = pipelineId;
		}

		List<Object[]> result = pipelineRepository.getEmployeePerformanceDistribution(userId, startDate, endDate, flag,
				pid, memberId);

		Map<String, EmployeePerformanceGroupDto> groupedMap = new LinkedHashMap<>();

		for (Object[] row : result) {
			int index = 0;

			EmployeeLeadPerformanceResponseDto dto = new EmployeeLeadPerformanceResponseDto();

			dto.setLeadAssignId(commonService.getLong(row[index++]));
			dto.setUserName(commonService.getString(row[index++]));
			dto.setHotLeads(commonService.getInteger(row[index++]));
			dto.setColdLeads(commonService.getInteger(row[index++]));
			dto.setWonLeads(commonService.getInteger(row[index++]));
			dto.setLostLeads(commonService.getInteger(row[index++]));
			dto.setClientDemoLeads(commonService.getInteger(row[index++]));
			dto.setOnTimeDelivery(commonService.getInteger(row[index++]));
			dto.setTotalLeads(commonService.getInteger(row[index++]));

			String closeDate = commonService.getString(row[index++]);

			Integer wonLeads = dto.getWonLeads() != null ? dto.getWonLeads() : 0;
			Integer totalLeads = dto.getTotalLeads() != null ? dto.getTotalLeads() : 0;

			double qualityScore = 0.0;

			if (totalLeads > 0) {
				qualityScore = (((double) wonLeads / (double) totalLeads) / 0.2) * 100.0;
				qualityScore = Math.round(qualityScore * 100.0) / 100.0;
			}
			dto.setQualityScore(qualityScore);

			// 🔹 Grouping key
			if (!groupedMap.containsKey(closeDate)) {

				EmployeePerformanceGroupDto group = new EmployeePerformanceGroupDto();

				group.setCloseDate(closeDate);
				group.setEmployees(new ArrayList<>());

				groupedMap.put(closeDate, group);
			}

			groupedMap.get(closeDate).getEmployees().add(dto);
		}

		List<EmployeePerformanceGroupDto> finalList = new ArrayList<>(groupedMap.values());

		map.put("employee_performance", finalList);

		return map;
	}

	@Override
	public List<EmployeeLeadPerformanceForReportResponseDto> getEmployeePerformanceForReport(Long employeeId,
			String startDate, String endDate, Long pipelineId, Long userId) {
		Map<String, Object> map = new HashMap<>();
		int flag = 0;
		Long pid = Long.valueOf(0);
		if (startDate != null && endDate != null) {
			flag = 1;
		}

		if (pipelineId != null) {
			pid = pipelineId;
		}

		List<Object[]> result = pipelineRepository.getEmployeePerformanceDistributionDatewise(employeeId, startDate,
				endDate, flag, pid, userId);

		List<EmployeeLeadPerformanceForReportResponseDto> dtos = new ArrayList<>();

		for (Object[] row : result) {
			int index = 0;

			EmployeeLeadPerformanceForReportResponseDto dto = new EmployeeLeadPerformanceForReportResponseDto();

			dto.setLeadAssignId(commonService.getLong(row[index++]));
			dto.setUserName(commonService.getString(row[index++]));
			dto.setHotLeads(commonService.getInteger(row[index++]));
			dto.setColdLeads(commonService.getInteger(row[index++]));
			dto.setWonLeads(commonService.getInteger(row[index++]));
			dto.setLostLeads(commonService.getInteger(row[index++]));
			dto.setClientDemoLeads(commonService.getInteger(row[index++]));
			dto.setOnTimeDelivery(commonService.getInteger(row[index++]));
			dto.setTotalLeads(commonService.getInteger(row[index++]));

			String closeDate = commonService.getString(row[index++]);
			dto.setAssignDate(closeDate);

			Integer wonLeads = dto.getWonLeads() != null ? dto.getWonLeads() : 0;
			Integer totalLeads = dto.getTotalLeads() != null ? dto.getTotalLeads() : 0;

			double qualityScore = 0.0;

			if (totalLeads > 0) {
				qualityScore = ((double) wonLeads / ((double) totalLeads * 0.2)) * 100.0;
				qualityScore = Math.round(qualityScore * 100.0) / 100.0;
			}
			dto.setQualityScore(qualityScore);

			dtos.add(dto);
		}

		return dtos;
	}

	@Override
	public Boolean deleteStage(Long stageId, String stageType) {
		if (stageType.equalsIgnoreCase("close")) {
			PipelineCloseStageEntity closeStageEntity = pipelineCloseStageRepository.findByIdAndIsDeleteFalse(stageId)
					.orElseThrow(() -> new RuntimeException("Stage Not Found"));
			closeStageEntity.setIsDelete(true);
			pipelineCloseStageRepository.save(closeStageEntity);
		} else if (stageType.equalsIgnoreCase("open")) {
			PipelineOpenStageEntity openStageEntity = pipelineOpenStageRepository.findByIdAndIsDeleteFalse(stageId)
					.orElseThrow(() -> new RuntimeException("Stage Not Found"));
			openStageEntity.setIsDelete(true);
			pipelineOpenStageRepository.save(openStageEntity);
		} else {
			throw new RuntimeException("StageType Not Found:- " + stageType);
		}

		return true;
	}
}
