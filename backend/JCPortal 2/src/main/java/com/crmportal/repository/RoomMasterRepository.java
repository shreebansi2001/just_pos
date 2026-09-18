package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.RoomMaster;

@Repository
public interface RoomMasterRepository extends JpaRepository<RoomMaster, Long> {

    List<RoomMaster> findByUser_IdAndIsDeleteFalseAndIsActiveTrue(Long userId);

}