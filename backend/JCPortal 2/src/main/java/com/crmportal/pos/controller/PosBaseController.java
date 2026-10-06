package com.crmportal.pos.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class PosBaseController {

    protected ResponseEntity<Map<String, Object>> ok(String msg, Object data) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("success", true);
        res.put("msg", msg);
        res.put("data", data);
        return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
    }

    protected ResponseEntity<Map<String, Object>> error(String msg) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("success", false);
        res.put("msg", msg);
        return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
    }
}
