package com.crmportal.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.MenuItemRawMaterialRateDishCostingEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.response.dto.MenuItemRawMaterialsResponseDto;

@Repository
public interface MenuItemRawMaterialRateDishCostingRepository extends JpaRepository<MenuItemRawMaterialRateDishCostingEntity, Long>{

	void deleteByMenuItemAndUser(MenuItemMasterEntity entity, UserMasterEntity user);

	@Query(
		    value = "SELECT total_rate FROM menuitem_rawmaterial_rate_dishcosting WHERE menu_item_id = :menuItemId",
		    nativeQuery = true
		)
		BigDecimal findTotalRateByMenuItemId(@Param("menuItemId") Long menuItemId);

	MenuItemRawMaterialRateDishCostingEntity findByMenuItemAndIsDeleteFalse(MenuItemMasterEntity entity);

	List<MenuItemRawMaterialRateDishCostingEntity> findByUuid(String oldUuid);

	List<MenuItemRawMaterialRateDishCostingEntity> findByUuidAndIsDeleteFalse(String oldUuid);

	List<MenuItemRawMaterialRateDishCostingEntity> findByMenuItemIdInAndIsDeleteFalse(Set<Long> menuItemIds);

	Optional<MenuItemRawMaterialRateDishCostingEntity> findByMenuItemAndUserAndIsDeleteFalse(MenuItemMasterEntity item,
			UserMasterEntity user);


}
