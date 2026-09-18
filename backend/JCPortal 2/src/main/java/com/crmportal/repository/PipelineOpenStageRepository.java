package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.PipelineEntity;
import com.crmportal.entity.PipelineOpenStageEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface PipelineOpenStageRepository extends JpaRepository<PipelineOpenStageEntity, Long> {

	List<PipelineOpenStageEntity> findByPipelineAndIsDeleteFalse(PipelineEntity pipelineEntity);

	Optional<PipelineOpenStageEntity> findByIdAndIsDeleteFalse(Long openStageId);

	List<PipelineOpenStageEntity> findByPipelineIdAndUserIdAndIsDeleteFalse(PipelineEntity pipelineEntity,
			UserMasterEntity userEntity);

//	List<PipelineOpenStageEntity> findByPipeline_IdAndIsDeleteFalse(Long id);

	List<PipelineOpenStageEntity> findByPipelineIdAndUserIdAndIsDeleteFalse(Long pipelineId, Long userId);

	void deleteAllByUserAndPipeline(UserMasterEntity userEntity, PipelineEntity pipelineEntity);

	boolean existsByPipelineAndIsDeleteFalse(PipelineEntity pipelineEntity);
	
}
