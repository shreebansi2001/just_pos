package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.MenuSubCategoryMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface MenuSubCategoryMasterRepository extends JpaRepository<MenuSubCategoryMasterEntity, Long>{

	Optional<MenuSubCategoryMasterEntity> findByUserAndNameEnglishIgnoreCaseAndIsDeleteFalse(UserMasterEntity user,
			String nameEnglish);

	Optional<MenuSubCategoryMasterEntity> findByIdAndUserAndIsDeleteFalse(long id, UserMasterEntity user);

	List<MenuSubCategoryMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	List<MenuSubCategoryMasterEntity> findAllByUserAndIsDeleteFalseAndIsActive(UserMasterEntity user, Boolean isActive);

	List<MenuSubCategoryMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(
			String menuSubCategoryName, UserMasterEntity user);

	List<MenuSubCategoryMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndIsActive(
			String menuSubCategoryName, UserMasterEntity user, Boolean isActive);

	Optional<MenuSubCategoryMasterEntity> findByIdAndIsDeleteFalse(Long id);

	List<MenuSubCategoryMasterEntity> findAllByUserAndMenuCategoryAndIsDeleteFalse(UserMasterEntity user,
			MenuCategoryMasterEntity menuCat);

	List<MenuSubCategoryMasterEntity> findAllByUserAndMenuCategoryAndIsDeleteFalseAndIsActive(UserMasterEntity user,
			MenuCategoryMasterEntity menuCat, Boolean isActive);

	List<MenuSubCategoryMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndMenuCategoryAndIsDeleteFalse(
			String menuSubCategoryName, UserMasterEntity user, MenuCategoryMasterEntity menuCat);

	List<MenuSubCategoryMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndMenuCategoryAndIsDeleteFalseAndIsActive(
			String menuSubCategoryName, UserMasterEntity user, MenuCategoryMasterEntity menuCat, Boolean isActive);

}
