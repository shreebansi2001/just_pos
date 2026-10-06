package com.crmportal.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.service.PosService;

@RestController
@RequestMapping("/v1/api/pos")
@CrossOrigin(origins = {"*"}, maxAge = 3600L)
public class PosDashboardController extends PosBaseController {

    @Autowired
    private PosService posService;

    @GetMapping("/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStats(
            @RequestParam(value = "userId", required = false) Long userId) {
        return ok("Stats fetched", posService.getStats(userId));
    }
}
