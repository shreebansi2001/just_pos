package com.crmportal.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.entity.PosCategoryEntity;
import com.crmportal.entity.PosItemEntity;
import com.crmportal.service.PosService;

@RestController
@RequestMapping("/v1/api/pos")
@CrossOrigin(origins = {"*"}, maxAge = 3600L)
public class PosMenuController extends PosBaseController {

    @Autowired
    private PosService posService;

    // Categories
    @GetMapping("/categories")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCategories(
            @RequestParam(value = "userId", required = false) Long userId) {
        return ok("Categories fetched", posService.getAllCategories(userId));
    }

    @PostMapping("/categories")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveCategory(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestBody PosCategoryEntity category) {
        return ok("Category saved", posService.saveCategory(category, userId));
    }

    @DeleteMapping("/categories/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable("id") Long id) {
        posService.deleteCategory(id);
        return ok("Category deleted", null);
    }

    // Items
    @GetMapping("/items")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getItems(
            @RequestParam(value = "userId", required = false) Long userId) {
        return ok("Items fetched", posService.getAllItems(userId));
    }

    @PostMapping("/items")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveItem(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestBody PosItemEntity item) {
        return ok("Item saved", posService.saveItem(item, userId));
    }

    @DeleteMapping("/items/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteItem(@PathVariable("id") Long id) {
        posService.deleteItem(id);
        return ok("Item deleted", null);
    }
}
