package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.MenuItemAllocationConfigEntity;
import com.crmportal.entity.MenuItemMasterEntity;

@Repository
public interface MenuItemAllocationConfigRepository extends JpaRepository<MenuItemAllocationConfigEntity, Long> {

	Optional<MenuItemAllocationConfigEntity> findByIdAndIsDeleteFalse(Long id);

	MenuItemAllocationConfigEntity findByMenuItem(MenuItemMasterEntity entity);

	List<MenuItemAllocationConfigEntity> findAllByMenuItemAndIsDeleteFalse(MenuItemMasterEntity entity);

	Optional<MenuItemAllocationConfigEntity> findByMenuItemIdAndIsDeleteFalse(Long menuItemId);

}
