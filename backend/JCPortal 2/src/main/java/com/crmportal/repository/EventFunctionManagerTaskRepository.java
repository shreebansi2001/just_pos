package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionManagerTaskEntity;
import com.crmportal.enums.TaskType;

@Repository
public interface EventFunctionManagerTaskRepository extends JpaRepository<EventFunctionManagerTaskEntity, Long> {

	Optional<EventFunctionManagerTaskEntity> findByIdAndIsDeleteFalse(Long id);

	@Query(value = "SELECT eft.eventfunction_managertask_id, " +
	        "mt.manager_task_id, mt.name, mt.description, " +
	        "eft.manager_id, eft.eventfunction_id, eft.event_id, " +
	        "eft.remarks, eft.latitude, eft.longitude, " +
	        "eft.status, eft.completed_at, mt.priority, mt.type " +
	        " FROM manager_task mt " +
	        " JOIN eventfunction_managertask eft " +
	        "ON mt.manager_task_id = eft.manager_task_id " +
	        "AND eft.eventfunction_id = :eventFunctionId " +
	        "AND eft.manager_id = :managerId " +
	        "AND eft.is_delete = FALSE " +
	        "WHERE (:type IS NULL OR mt.type = :type) " +
	        "AND mt.is_delete = FALSE " +
	        "ORDER BY mt.sequence",
	        nativeQuery = true)
	List<Object[]> getEventFunctionManagerTask(
	        @Param("eventFunctionId") Long eventFunctionId,
	        @Param("managerId") Long managerId,
	        @Param("type") String type);

	@Query(value = "SELECT " +
	        "COUNT(*) AS totalTasks, " +
	        "SUM(CASE WHEN eft.status = 'PENDING' THEN 1 ELSE 0 END) AS totalPending, " +
	        "SUM(CASE WHEN eft.status = 'INPROGRESS' THEN 1 ELSE 0 END) AS totalInProgress, " +
	        "SUM(CASE WHEN eft.status = 'COMPLETED' THEN 1 ELSE 0 END) AS totalCompleted, " +
	        "SUM(CASE WHEN eft.status = 'CANCEL' THEN 1 ELSE 0 END) AS totalCancel " +
	        "FROM eventfunction_managertask eft " +
	        "JOIN manager_task mt ON mt.manager_task_id = eft.manager_task_id " +
	        "WHERE eft.manager_id = :managerId " +
	        "AND eft.eventfunction_id = :eventFunctionId " +
	        "AND eft.is_delete = FALSE " +
	        "AND mt.is_delete = FALSE " +
	        "AND (:type IS NULL OR mt.type = :type)",
	        nativeQuery = true)
	List<Object[]> getManagerTaskSummary(
	        @Param("managerId") Long managerId,
	        @Param("eventFunctionId") Long eventFunctionId,
	        @Param("type") String type);

	List<EventFunctionManagerTaskEntity> findAllByEventIdAndIsDeleteFalse(Long eventId);
}
