package com.crmportal.pos.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.crmportal.pos.service.PosService;

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
