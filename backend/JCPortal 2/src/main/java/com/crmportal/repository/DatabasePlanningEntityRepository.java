package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.DatabasePlanningEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface DatabasePlanningEntityRepository extends JpaRepository<DatabasePlanningEntity, Long> {

	Optional<DatabasePlanningEntity> findById(Long id);

	Optional<DatabasePlanningEntity> findByUser(UserMasterEntity user);

	List<DatabasePlanningEntity> findAll();

	List<DatabasePlanningEntity> findByIdAndDbName(Long id, String dbName);

	List<DatabasePlanningEntity> findByParentDbId(Long parentDbId);

	@Query(value = "SELECT dp.db_planning_id AS dbId, dp.db_name AS dbName, dp.state AS state, dp.uuid AS uuid, dp.instructions AS instructions, dp.is_published AS isPublished, " +
	        "u.user_Id AS userId, u.first_name AS firstName, u.last_name AS lastName, " +
	        "pdp.db_planning_id AS parentDbId, pdp.db_name AS parentDbName " +
	        "FROM db_planning dp " +
	        "JOIN users u ON dp.user_Id = u.user_Id " +
	        "LEFT JOIN db_planning pdp ON dp.parent_db_id = pdp.db_planning_id " +
	        "WHERE dp.user_Id = :id",
	        nativeQuery = true)
	List<Object[]> getDbNameByUser(@Param("id") Long id);

}
