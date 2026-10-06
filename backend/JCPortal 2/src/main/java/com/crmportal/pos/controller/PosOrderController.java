package com.crmportal.pos.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.crmportal.pos.dto.PosDto;
import com.crmportal.pos.entity.PosOrderEntity;
import com.crmportal.pos.service.PosService;

@RestController
@RequestMapping("/v1/api/pos")
@CrossOrigin(origins = {"*"}, maxAge = 3600L)
public class PosOrderController extends PosBaseController {

    @Autowired
    private PosService posService;

    @PostMapping("/orders")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestBody PosDto.OrderCreateRequest request) {
        return ok("Order created", posService.createOrder(request, userId));
    }

    @GetMapping("/orders")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getActiveOrders(
            @RequestParam(value = "userId", required = false) Long userId) {
        return ok("Orders fetched", posService.getActiveOrders(userId));
    }

    @GetMapping("/orders/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable("id") Long id) {
        PosOrderEntity order = posService.getOrder(id);
        return order != null ? ok("Order fetched", order) : error("Order not found");
    }

    @PutMapping("/orders/{id}/items")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateOrderItems(
            @PathVariable("id") Long id,
            @RequestBody List<PosDto.OrderItemUpdate> items) {
        return ok("Order items updated", posService.updateOrderItems(id, items));
    }

    @PutMapping("/orders/{id}/discount")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateOrderDiscount(
            @PathVariable("id") Long id,
            @RequestBody PosDto.DiscountUpdate discount) {
        return ok("Discount updated", posService.updateOrderDiscount(id, discount));
    }

    @PostMapping("/orders/{id}/move")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> moveTable(
            @PathVariable("id") Long id,
            @RequestBody PosDto.MoveTableRequest request) {
        return ok("Table moved", posService.moveTable(id, request.getNewTableId()));
    }

    @PostMapping("/orders/{id}/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable("id") Long id) {
        posService.cancelOrder(id);
        return ok("Order cancelled", null);
    }
}
