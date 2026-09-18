package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.MenuPreparationEntity;
import com.crmportal.entity.MenuPreparationHistoryEntity;

@Repository
public interface MenuPreparationHistoryRepository extends JpaRepository<MenuPreparationHistoryEntity, Long>{

	List<MenuPreparationHistoryEntity> findAllByMenuPreparation(MenuPreparationEntity menuPreparationEntity);

}
