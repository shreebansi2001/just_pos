package com.crmportal.pos.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.crmportal.pos.entity.PosFloorEntity;
import com.crmportal.pos.entity.PosTableEntity;
import com.crmportal.pos.service.PosService;

@RestController
@RequestMapping("/v1/api/pos")
@CrossOrigin(origins = {"*"}, maxAge = 3600L)
public class PosTableController extends PosBaseController {

    @Autowired
    private PosService posService;

    // Floors
    @GetMapping("/floors")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFloors(
            @RequestParam(value = "userId", required = false) Long userId) {
        return ok("Floors fetched", posService.getAllFloors(userId));
    }

    @PostMapping("/floors")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveFloor(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestBody PosFloorEntity floor) {
        return ok("Floor saved", posService.saveFloor(floor, userId));
    }

    @DeleteMapping("/floors/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteFloor(@PathVariable("id") Long id) {
        posService.deleteFloor(id);
        return ok("Floor deleted", null);
    }

    // Tables
    @GetMapping("/tables")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTables(
            @RequestParam(value = "userId", required = false) Long userId) {
        return ok("Tables fetched", posService.getAllTables(userId));
    }

    @PostMapping("/tables")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveTable(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestBody PosTableEntity table) {
        return ok("Table saved", posService.saveTable(table, userId));
    }

    @DeleteMapping("/tables/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteTable(@PathVariable("id") Long id) {
        posService.deleteTable(id);
        return ok("Table deleted", null);
    }

    @PutMapping("/tables/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateTableStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status) {
        posService.updateTableStatus(id, status);
        return ok("Table status updated", null);
    }
}
