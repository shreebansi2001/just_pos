package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.PipelineEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface PipelineRepository extends JpaRepository<PipelineEntity, Long> {

	Optional<PipelineEntity> findByIdAndIsDeleteFalse(Long id);

	List<PipelineEntity> findByIsDeleteFalse();

	@Query(value = "SELECT p.id AS pipeline_id, 'open_stage' AS stage_type, ps.id AS stage_id, ps.stage, "
			+ " lm.lead_id, " + " lm.client_name, " + " lm.estimate_amount, " + " lm.lead_follow_up_date, "
			+ " c.name, " + " lm.lead_assign_id, " + " u.first_name, " + " u.last_name, " + " lm.lead_code, "
			+ " lm.created_at, " + " lm.updated_at, " + " pl.name as plan_name," + " lm.close_date,"
			+ " lm.description, " + " lm.contact_number, lsm.source_name " + "FROM pipeline p "
			+ "JOIN pipeline_open_stages ps ON ps.pipeline_id = p.id "
			+ "LEFT JOIN lead_master lm ON lm.pipeline_id = p.id JOIN lead_source_master lsm ON lm.lead_source_id = lsm.lead_source_id " + " AND lm.open_stage_id = ps.id  "
			+ " AND lm.is_delete = FALSE "
			+ " AND (:memberId = -1 OR lm.lead_assign_id = :memberId) AND lm.user_id = :userId "
			+ "LEFT JOIN users u ON u.user_id = lm.lead_assign_id " + " LEFT JOIN cities c on c.city_id = lm.city_id "
			+ " LEFT JOIN plans pl on pl.plan_id = lm.plan_id " + "WHERE p.id = :pipelineId "
			+ "AND p.is_delete = FALSE " + "AND ps.is_delete = FALSE " + "AND p.user_id = :userId "
			+ "AND ps.user_id = :userId " + "UNION ALL "
			+ "SELECT p.id AS pipeline_id, 'close_stage' AS stage_type, ps.id AS stage_id, ps.stage, " + " lm.lead_id, "
			+ " lm.client_name, " + " lm.estimate_amount, " + " lm.lead_follow_up_date, " + " c.name, "
			+ " lm.lead_assign_id, " + " u.first_name, " + " u.last_name, " + " lm.lead_code, " + " lm.created_at, "
			+ " lm.updated_at, " + " pl.name as plan_name, " + " lm.close_date," + " lm.description, "
			+ " lm.contact_number , lsm.source_name " + "FROM pipeline p " + "JOIN pipeline_close_stages ps ON ps.pipeline_id = p.id "
			+ "LEFT JOIN lead_master lm ON lm.pipeline_id = p.id JOIN lead_source_master lsm ON lm.lead_source_id = lsm.lead_source_id  " + " AND lm.close_stage_id = ps.id  "
			+ " AND lm.is_delete = FALSE "
			+ " AND (:memberId = -1 OR lm.lead_assign_id = :memberId) AND lm.user_id = :userId "
			+ "LEFT JOIN users u ON u.user_id = lm.lead_assign_id " + " LEFT JOIN cities c on c.city_id = lm.city_id "
			+ " LEFT JOIN plans pl on pl.plan_id = lm.plan_id " + "WHERE p.id = :pipelineId "
			+ "AND p.is_delete = FALSE " + "AND ps.is_delete = FALSE " + "AND p.user_id = :userId "
			+ "AND ps.user_id = :userId ", nativeQuery = true)
	List<Object[]> findLeadDataStagesByPipelineId(Long pipelineId, Long memberId, Long userId);

	@Query(value = "SELECT 'open_stage' AS stage_type, p.id AS pipelineId, ps.id AS stage_id, ps.stage "
			+ "FROM pipeline p " + "JOIN pipeline_open_stages ps ON ps.pipeline_id = p.id "
			+ "WHERE p.id = :pipelineId AND p.is_delete = FALSE AND ps.is_delete = FALSE AND p.user_id " + "UNION ALL "
			+ "SELECT 'close_stage' AS stage_type, p.id AS pipelineId, ps.id AS stage_id, ps.stage "
			+ "FROM pipeline p " + "JOIN pipeline_close_stages ps ON ps.pipeline_id = p.id "
			+ "WHERE p.id = :pipelineId AND p.is_delete = FALSE AND ps.is_delete = FALSE AND p.user_id = :userId ", nativeQuery = true)
	List<Object[]> findStagesByPipelineId(Long pipelineId, Long userId);

	@Query(value = "SELECT " + " COUNT(DISTINCT CASE WHEN pcs.id IS NOT NULL AND lm.close_stage_id IS NOT NULL "
			+ " AND lm.is_delete = FALSE AND pcs.is_delete = FALSE THEN lm.lead_id END) AS completed_leads, "

			+ " COUNT(DISTINCT CASE WHEN pos.id IS NOT NULL AND lm.open_stage_id IS NOT NULL "
			+ " AND lm.actual_close_date IS NULL AND CURRENT_DATE > DATE(lm.close_date) AND lm.is_delete = FALSE "
			+ " AND pos.is_delete = FALSE THEN lm.lead_id END) AS overdue_leads, "

			+ " COUNT(DISTINCT CASE WHEN pos.id IS NOT NULL AND lm.open_stage_id IS NOT NULL "
			+ " AND LOWER(pos.stage) = 'new inquiry' AND lm.is_delete = FALSE "
			+ " AND pos.is_delete = FALSE THEN lm.lead_id END) AS new_inquiry_leads, "

			+ " COUNT(DISTINCT CASE WHEN pos.id IS NOT NULL AND lm.open_stage_id IS NOT NULL "
			+ " AND LOWER(pos.stage) != 'new inquiry' AND lm.is_delete = FALSE "
			+ " AND pos.is_delete = FALSE THEN lm.lead_id END) AS in_progress_leads, "

			+ " COUNT(DISTINCT CASE WHEN pcs.id IS NOT NULL AND lm.close_stage_id IS NOT NULL "
			+ " AND lm.actual_close_date IS NOT NULL AND lm.close_date IS NOT NULL AND DATE(lm.close_date) <= DATE(actual_close_date) AND lm.is_delete = FALSE "
			+ " AND pcs.is_delete = FALSE THEN lm.lead_id END) AS on_time_delivery, "

			+ " COUNT(DISTINCT CASE WHEN pcs.id IS NOT NULL AND lm.close_stage_id IS NOT NULL "
			+ " AND LOWER(pcs.stage) = 'won' AND lm.is_delete = FALSE "
			+ " AND pcs.is_delete = FALSE THEN lm.lead_id END) AS quality_score "

			+ " FROM lead_master lm " + " LEFT JOIN pipeline p ON p.id = lm.pipeline_id "
			+ " LEFT JOIN pipeline_close_stages pcs ON pcs.pipeline_id = p.id " + " AND lm.close_stage_id = pcs.id "
			+ " LEFT JOIN pipeline_open_stages pos ON pos.pipeline_id = p.id " + " AND lm.open_stage_id = pos.id "

			+ " WHERE (:memberId = -1 OR lm.lead_assign_id = :memberId) AND lm.user_id = :userId "
			+ " AND (:flag = 0 OR (DATE(lm.created_at) BETWEEN :startDate AND :endDate)) ", nativeQuery = true)
	Object[] getPerformance(Long userId, String startDate, String endDate, int flag, Long memberId);

	@Query(value = " SELECT t.lead_assign_id, t.user_name, t.completed_leads, t.pending_leads, (t.completed_leads + t.pending_leads) AS total_leads "
			+ " FROM ( " + " SELECT lm.lead_assign_id, CONCAT(u.first_name, ' ', u.last_name) AS user_name,  "
			+ " COUNT(CASE WHEN pcs.id IS NOT NULL AND lm.is_delete = FALSE AND pcs.is_delete = FALSE THEN pcs.id END) AS completed_leads, "
			+ " COUNT(CASE WHEN pos.id IS NOT NULL AND lm.is_delete = FALSE AND pos.is_delete = FALSE THEN pos.id END) AS pending_leads "
			+ " FROM lead_master lm " + " LEFT JOIN `pipeline` p ON p.id = lm.pipeline_id "
			+ " LEFT JOIN `pipeline_close_stages` pcs ON pcs.pipeline_id = p.id AND pcs.id = lm.close_stage_id "
			+ " LEFT JOIN `pipeline_open_stages` pos ON pos.pipeline_id = p.id AND pos.id = lm.open_stage_id "
			+ " LEFT JOIN users u ON u.user_id = lm.lead_assign_id "
			+ " WHERE (:memberId = -1 OR lm.lead_assign_id = :memberId) AND lm.user_id = :userId "
			+ " AND (:flag = 0 OR (DATE(lm.created_at) BETWEEN :startDate AND :endDate)) "
			+ " GROUP BY lm.lead_assign_id " + " ) AS t ", nativeQuery = true)
	List<Object[]> getLeadDistribution(Long userId, String startDate, String endDate, int flag,Long memberId);

	@Query(value = "SELECT lm.lead_assign_id, CONCAT(u.first_name, ' ', u.last_name) AS user_name, "
			+ " COUNT(CASE WHEN pos.id IS NOT NULL AND lm.is_delete = FALSE AND pos.is_delete = FALSE AND LOWER(pos.stage) = 'hot' THEN pos.id END) AS hot_leads, "
			+ " COUNT(CASE WHEN pos.id IS NOT NULL AND lm.is_delete = FALSE AND pos.is_delete = FALSE AND LOWER(pos.stage) = 'cold' THEN pos.id END) AS cold_leads, "
			+ " COUNT(CASE WHEN pcs.id IS NOT NULL AND lm.is_delete = FALSE AND pcs.is_delete = FALSE AND LOWER(pcs.stage) = 'won' THEN pcs.id END) AS won_leads, "
			+ " COUNT(CASE WHEN pcs.id IS NOT NULL AND lm.is_delete = FALSE AND pcs.is_delete = FALSE AND LOWER(pcs.stage) = 'lost' THEN pcs.id END) AS lost_leads, "
			+ " COUNT(CASE WHEN pos.id IS NOT NULL AND lm.is_delete = FALSE AND pos.is_delete = FALSE AND LOWER(pos.stage) = 'client demo' THEN pos.id END) AS client_demo_leads, "
			+ " COUNT(CASE WHEN pcs.id IS NOT NULL AND lm.is_delete = FALSE AND pcs.is_delete = FALSE AND actual_close_date IS NOT NULL AND lm.close_date IS NOT NULL AND DATE(lm.close_date) <= DATE(actual_close_date) THEN pcs.id END) AS on_time_delivery, "
			+ " COUNT(CASE WHEN pos.id IS NOT NULL AND lm.is_delete = FALSE AND pos.is_delete = FALSE THEN pos.id END) AS total_leads, "
			+ " DATE(lm.close_date) AS close_date " + " FROM lead_master lm "
			+ " LEFT JOIN `pipeline` p ON p.id = lm.pipeline_id "
			+ " LEFT JOIN `pipeline_close_stages` pcs ON pcs.pipeline_id = p.id AND pcs.id = lm.close_stage_id "
			+ " LEFT JOIN `pipeline_open_stages` pos ON pos.pipeline_id = p.id AND pos.id = lm.open_stage_id "
			+ " LEFT JOIN users u ON u.user_id = lm.lead_assign_id "
			+ " WHERE (:memberId = -1 OR lm.lead_assign_id = :memberId) "
			+ " AND (:flag = 0 OR (DATE(lm.created_at) BETWEEN :startDate AND :endDate)) "
			+ "   AND (:pipelineId = 0 OR p.id = :pipelineId) AND lm.user_id = :userId "
			+ " GROUP BY DATE(lm.close_date), lm.lead_assign_id, u.first_name, u.last_name ", nativeQuery = true)
	List<Object[]> getEmployeePerformanceDistribution(Long userId, String startDate, String endDate, int flag,
			Long pipelineId,Long memberId);

	@Query(value = "SELECT lm.lead_assign_id, CONCAT(u.first_name, ' ', u.last_name) AS user_name, "
			+ " COUNT(CASE WHEN pos.id IS NOT NULL AND lm.is_delete = FALSE AND pos.is_delete = FALSE AND LOWER(pos.stage) = 'hot' THEN pos.id END) AS hot_leads, "
			+ " COUNT(CASE WHEN pos.id IS NOT NULL AND lm.is_delete = FALSE AND pos.is_delete = FALSE AND LOWER(pos.stage) = 'cold' THEN pos.id END) AS cold_leads, "
			+ " COUNT(CASE WHEN pcs.id IS NOT NULL AND lm.is_delete = FALSE AND pcs.is_delete = FALSE AND LOWER(pcs.stage) = 'won' THEN pcs.id END) AS won_leads, "
			+ " COUNT(CASE WHEN pcs.id IS NOT NULL AND lm.is_delete = FALSE AND pcs.is_delete = FALSE AND LOWER(pcs.stage) = 'lost' THEN pcs.id END) AS lost_leads, "
			+ " COUNT(CASE WHEN pos.id IS NOT NULL AND lm.is_delete = FALSE AND pos.is_delete = FALSE AND LOWER(pos.stage) = 'client demo' THEN pos.id END) AS client_demo_leads, "
			+ " COUNT(CASE WHEN pcs.id IS NOT NULL AND lm.is_delete = FALSE AND pcs.is_delete = FALSE AND actual_close_date IS NOT NULL AND lm.close_date IS NOT NULL AND DATE(lm.close_date) <= DATE(actual_close_date) THEN pcs.id END) AS on_time_delivery, "
			+ " COUNT(CASE WHEN pos.id IS NOT NULL AND lm.is_delete = FALSE AND pos.is_delete = FALSE THEN pos.id END) AS total_leads, "
			+ " DATE(lm.close_date) AS close_date " + " FROM lead_master lm "
			+ " LEFT JOIN `pipeline` p ON p.id = lm.pipeline_id "
			+ " LEFT JOIN `pipeline_close_stages` pcs ON pcs.pipeline_id = p.id AND pcs.id = lm.close_stage_id "
			+ " LEFT JOIN `pipeline_open_stages` pos ON pos.pipeline_id = p.id AND pos.id = lm.open_stage_id "
			+ " LEFT JOIN users u ON u.user_id = lm.lead_assign_id "
			+ " WHERE (:employeeId = 1 OR lm.lead_assign_id = :employeeId) "
			+ " AND (:flag = 0 OR (DATE(lm.close_date) BETWEEN :startDate AND :endDate)) "
			+ "   AND (:pipelineId = 0 OR p.id = :pipelineId) AND lm.user_Id = :userId "
			+ " GROUP BY DATE(lm.close_date), lm.lead_assign_id, u.first_name, u.last_name ", nativeQuery = true)
	List<Object[]> getEmployeePerformanceDistributionDatewise(Long employeeId, String startDate, String endDate, int flag,
			Long pipelineId, Long userId);

	List<PipelineEntity> findByIsDeleteFalseAndUser(UserMasterEntity user);

}
