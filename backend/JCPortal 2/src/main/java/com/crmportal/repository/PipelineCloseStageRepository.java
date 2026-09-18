package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.PipelineCloseStageEntity;
import com.crmportal.entity.PipelineEntity;
import com.crmportal.entity.PipelineOpenStageEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface PipelineCloseStageRepository extends JpaRepository<PipelineCloseStageEntity, Long> {

	List<PipelineCloseStageEntity> findByPipelineAndIsDeleteFalse(PipelineEntity pipelineEntity);

	Optional<PipelineCloseStageEntity> findByIdAndIsDeleteFalse(Long closeStageId);

	List<PipelineCloseStageEntity> findByPipelineIdAndUserIdAndIsDeleteFalse(PipelineEntity pipelineEntity,
			UserMasterEntity userEntity);

//	List<PipelineCloseStageEntity> findByPipelineAndIsDeleteFalse(Long id);

//	Iterable<PipelineCloseStageEntity> findByPipelineIdAndUserIdAndIsDeleteFalse(Long pipelineId, Long userId);

	void deleteAllByUserAndPipeline(UserMasterEntity userEntity, PipelineEntity pipelineEntity);

	boolean existsByPipelineAndIsDeleteFalse(PipelineEntity pipelineEntity);
}
