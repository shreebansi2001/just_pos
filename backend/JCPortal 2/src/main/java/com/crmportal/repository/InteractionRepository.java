package com.crmportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.InteractionEntity;

@Repository
public interface InteractionRepository extends JpaRepository<InteractionEntity, Long> {

}
