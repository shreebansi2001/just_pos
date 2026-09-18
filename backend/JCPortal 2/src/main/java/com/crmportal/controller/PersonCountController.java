package com.crmportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.crmportal.entity.PersonCount;
import com.crmportal.repository.PersonCountRepository;
import com.crmportal.response.dto.PersonCountResponse;
import com.crmportal.service.PersonCountService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PersonCountController {

    @Autowired
    private PersonCountService service;

    @Autowired
    private PersonCountRepository repository;

    @GetMapping("/live")
    public PersonCountResponse liveCount() {

        return service.fetchFromCamera();
    }

    @GetMapping("/history")
    public List<PersonCount> history() {

        return repository.findAll();
    }
}