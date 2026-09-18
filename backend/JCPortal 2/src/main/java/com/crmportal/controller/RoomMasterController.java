package com.crmportal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmportal.request.dto.RoomMasterRequestDto;
import com.crmportal.response.dto.RoomMasterResponseDto;
import com.crmportal.service.RoomMasterService;

@RestController
@RequestMapping("/v1/api/room")
@CrossOrigin(origins = "*", maxAge = 3600L)
public class RoomMasterController {

    @Autowired
    private RoomMasterService roomMasterService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addRoom(
            @Valid @RequestBody RoomMasterRequestDto request) {

        Map<String, Object> response = new HashMap<>();

        try {

            RoomMasterResponseDto responseDto =
                    roomMasterService.addOrUpdateRoom(request, -1L);

            response.put("success", responseDto != null);
            response.put("msg", responseDto != null
                    ? "Room Created Successfully"
                    : "Room Creation Failed");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateRoom(
            @Valid @RequestBody RoomMasterRequestDto request,
            @RequestParam("id") Long id) {

        Map<String, Object> response = new HashMap<>();

        try {

            RoomMasterResponseDto responseDto =
                    roomMasterService.addOrUpdateRoom(request, id);

            response.put("success", responseDto != null);
            response.put("msg", responseDto != null
                    ? "Room Updated Successfully"
                    : "Room Update Failed");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping("/getallactivebyuserid")
    public ResponseEntity<Map<String, Object>> getAllActiveRooms(
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {

            List<RoomMasterResponseDto> rooms =
                    roomMasterService.getAllActiveRoomsByUserId(userId);

            if (rooms.isEmpty()) {

                response.put("success", false);
                response.put("msg", "No Rooms Found");

            } else {

                Map<String, Object> roomData = new HashMap<>();

                roomData.put("Room Details", rooms);

                response.put("data", roomData);
                response.put("success", true);
                response.put("msg", "Rooms Found Successfully");
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping("/getbyroomid")
    public ResponseEntity<Map<String, Object>> getRoomById(
            @RequestParam("roomId") Long roomId) {

        Map<String, Object> response = new HashMap<>();

        try {

            RoomMasterResponseDto room =
                    roomMasterService.getRoomById(roomId);

            if (room == null) {

                response.put("success", false);
                response.put("msg", "Room Not Found");

            } else {

                List<RoomMasterResponseDto> rooms = new ArrayList<>();

                rooms.add(room);

                Map<String, Object> roomData = new HashMap<>();

                roomData.put("Room Details", rooms);

                response.put("data", roomData);
                response.put("success", true);
                response.put("msg", "Room Found Successfully");
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteRoom(
            @RequestParam("roomId") Long roomId) {

        Map<String, Object> response = new HashMap<>();

        try {

            Boolean deleted = roomMasterService.deleteRoomById(roomId);

            response.put("success", deleted);
            response.put("msg",
                    deleted ? "Room Deleted Successfully"
                            : "Room Delete Failed");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
