package com.crmportal.pos.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.crmportal.pos.dto.PosDto;
import com.crmportal.pos.dto.PosStatsDto;
import com.crmportal.pos.entity.PosCategoryEntity;
import com.crmportal.pos.entity.PosFloorEntity;
import com.crmportal.pos.entity.PosInvoiceEntity;
import com.crmportal.pos.entity.PosItemEntity;
import com.crmportal.pos.entity.PosKotEntity;
import com.crmportal.pos.entity.PosOrderEntity;
import com.crmportal.pos.entity.PosReservationEntity;
import com.crmportal.pos.entity.PosTableEntity;
import com.crmportal.pos.entity.PosTaxEntity;
import com.crmportal.pos.service.PosService;

@RestController
@RequestMapping("/v1/api/pos")
@CrossOrigin(origins = {"*"}, maxAge = 3600L)
public class PosController {

    @Autowired
    private PosService posService;

    private ResponseEntity<Map<String, Object>> ok(String msg, Object data) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("success", true);
        res.put("msg", msg);
        res.put("data", data);
        return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
    }

    private ResponseEntity<Map<String, Object>> error(String msg) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("success", false);
        res.put("msg", msg);
        return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
    }

    @GetMapping("/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStats() {
        return ok("Stats fetched", posService.getStats());
    }

    // Taxes
    @GetMapping("/taxes")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTaxes() {
        return ok("Taxes fetched", posService.getAllTaxes());
    }

    @PostMapping("/taxes")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveTax(@RequestBody PosTaxEntity tax) {
        return ok("Tax saved", posService.saveTax(tax));
    }

    @DeleteMapping("/taxes/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteTax(@PathVariable("id") Long id) {
        posService.deleteTax(id);
        return ok("Tax deleted", null);
    }

    // Categories
    @GetMapping("/categories")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCategories() {
        return ok("Categories fetched", posService.getAllCategories());
    }

    @PostMapping("/categories")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveCategory(@RequestBody PosCategoryEntity category) {
        return ok("Category saved", posService.saveCategory(category));
    }

    @DeleteMapping("/categories/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable("id") Long id) {
        posService.deleteCategory(id);
        return ok("Category deleted", null);
    }

    // Floors
    @GetMapping("/floors")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFloors() {
        return ok("Floors fetched", posService.getAllFloors());
    }

    @PostMapping("/floors")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveFloor(@RequestBody PosFloorEntity floor) {
        return ok("Floor saved", posService.saveFloor(floor));
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
    public ResponseEntity<Map<String, Object>> getTables() {
        return ok("Tables fetched", posService.getAllTables());
    }

    @PostMapping("/tables")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveTable(@RequestBody PosTableEntity table) {
        return ok("Table saved", posService.saveTable(table));
    }

    @DeleteMapping("/tables/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteTable(@PathVariable("id") Long id) {
        posService.deleteTable(id);
        return ok("Table deleted", null);
    }

    @PutMapping("/tables/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateTableStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        posService.updateTableStatus(id, status);
        return ok("Table status updated", null);
    }

    // Items
    @GetMapping("/items")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getItems() {
        return ok("Items fetched", posService.getAllItems());
    }

    @PostMapping("/items")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveItem(@RequestBody PosItemEntity item) {
        return ok("Item saved", posService.saveItem(item));
    }

    @DeleteMapping("/items/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteItem(@PathVariable("id") Long id) {
        posService.deleteItem(id);
        return ok("Item deleted", null);
    }

    // Orders
    @PostMapping("/orders")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody PosDto.OrderCreateRequest request) {
        return ok("Order created", posService.createOrder(request));
    }

    @GetMapping("/orders")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getActiveOrders() {
        return ok("Orders fetched", posService.getActiveOrders());
    }

    @GetMapping("/orders/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable("id") Long id) {
        PosOrderEntity order = posService.getOrder(id);
        return order != null ? ok("Order fetched", order) : error("Order not found");
    }

    @PutMapping("/orders/{id}/items")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateOrderItems(@PathVariable("id") Long id, @RequestBody List<PosDto.OrderItemUpdate> items) {
        return ok("Order items updated", posService.updateOrderItems(id, items));
    }

    @PutMapping("/orders/{id}/discount")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateOrderDiscount(@PathVariable("id") Long id, @RequestBody PosDto.DiscountUpdate discount) {
        return ok("Discount updated", posService.updateOrderDiscount(id, discount));
    }

    @PostMapping("/orders/{id}/move")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> moveTable(@PathVariable("id") Long id, @RequestBody PosDto.MoveTableRequest request) {
        return ok("Table moved", posService.moveTable(id, request.getNewTableId()));
    }

    @PostMapping("/orders/{id}/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable("id") Long id) {
        posService.cancelOrder(id);
        return ok("Order cancelled", null);
    }

    // KOTs
    @PostMapping("/orders/{id}/kot")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendKot(@PathVariable("id") Long id, @RequestBody List<PosDto.OrderItemUpdate> items) {
        return ok("KOT sent", posService.sendKot(id, items));
    }

    @GetMapping("/kots")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getKots() {
        return ok("KOTs fetched", posService.getActiveKots());
    }

    @PutMapping("/kots/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateKotStatus(@PathVariable("id") Long id, @RequestBody PosDto.KotStatusUpdate request) {
        return ok("KOT updated", posService.updateKotStatus(id, request.getStatus()));
    }

    // Invoices
    @PostMapping("/orders/{id}/invoice")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateInvoice(@PathVariable("id") Long id) {
        return ok("Invoice generated", posService.generateInvoice(id));
    }

    @PostMapping("/invoices/{id}/pay")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> payInvoice(@PathVariable("id") Long id, @RequestBody PosDto.InvoicePaymentRequest request) {
        return ok("Invoice settled", posService.payInvoice(id, request.getPaymentMode()));
    }

    @GetMapping("/invoices")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getInvoices() {
        return ok("Invoices fetched", posService.getAllInvoices());
    }

    // Reservations
    @GetMapping("/reservations")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getReservations(
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "status", required = false) String status) {
        return ok("Reservations fetched", posService.getReservations(date, status));
    }

    @PostMapping("/reservations")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveReservation(@RequestBody PosReservationEntity reservation) {
        return ok("Reservation saved", posService.saveReservation(reservation));
    }

    @PostMapping("/reservations/{id}/seat")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> seatReservation(@PathVariable("id") Long id, @RequestParam("tableId") Long tableId) {
        return ok("Reservation seated", posService.seatReservation(id, tableId));
    }
}
