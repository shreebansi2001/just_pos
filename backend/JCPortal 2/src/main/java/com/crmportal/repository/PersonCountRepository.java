package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crmportal.entity.PersonCount;

public interface PersonCountRepository extends JpaRepository<PersonCount, Long>{

	List<PersonCount> findAll();

}
