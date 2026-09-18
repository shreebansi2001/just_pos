package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.CaptainReceipeMasterEntity;
import com.crmportal.entity.MenuItemCaptainReceipeEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.MenuItemRawMaterialEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface MenuItemCaptainReceipeRepository extends JpaRepository<MenuItemCaptainReceipeEntity, Long> {

	Optional<MenuItemCaptainReceipeEntity> findByIdAndIsDeleteFalse(Long id);
	
	List<MenuItemCaptainReceipeEntity> findAllByMenuItem_IdAndIsDeleteFalse(Long menuItemId);
	
	@Query(" "
			+ " SELECT micr " 
			+ " FROM MenuItemCaptainReceipeEntity micr " 
			+ " JOIN FETCH micr.unit " 
			+ " JOIN FETCH micr.captainReceipe "
			+ " JOIN FETCH micr.menuItem " 
			+ " WHERE micr.menuItem.id = :menuItemId " 
			+ " AND micr.isDelete = false")
	List<MenuItemCaptainReceipeEntity> findAllByMenuItemWithRelations(Long menuItemId);

	List<MenuItemCaptainReceipeEntity> findByMenuItem_IdAndIsDeleteFalse(Long id);
	
	Optional<MenuItemCaptainReceipeEntity> findByMenuItemAndCaptainReceipeAndIsDeleteFalse(MenuItemMasterEntity menuItem,
			CaptainReceipeMasterEntity captainReceipe);

	List<MenuItemCaptainReceipeEntity> findAllByMenuItemAndUserAndIsDeleteFalse(MenuItemMasterEntity item,
			UserMasterEntity user);
}
