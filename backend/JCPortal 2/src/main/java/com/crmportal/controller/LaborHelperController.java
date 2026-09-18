package com.crmportal.controller;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmportal.request.dto.LaborHelperRequestDto;
import com.crmportal.response.dto.EventLaborHelperSelectionResponseDto;
import com.crmportal.response.dto.LaborHelperResponseDto;
import com.crmportal.service.LaborHelperService;
import org.springframework.core.env.Environment;

@RestController
@RequestMapping({ "/v1/api/laborhelper" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class LaborHelperController {

    @Autowired
    private LaborHelperService laborHelperService;
    
    @Autowired
    private Environment environment;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addLaborHelper(@Valid @ModelAttribute LaborHelperRequestDto request) {
        Map<String, Object> response = new HashMap<>();
        try {
            LaborHelperResponseDto responseDto = laborHelperService.addOrUpdateLaborHelper(request, 0L);
            if (responseDto != null) {
                response.put("msg", "Labor Helper created successfully");
                response.put("data", responseDto);
                response.put("success", true);
            } else {
                response.put("msg", "Failed to create Labor Helper");
                response.put("success", false);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateLaborHelper(
            @Valid @ModelAttribute LaborHelperRequestDto request, 
            @RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            LaborHelperResponseDto responseDto = laborHelperService.addOrUpdateLaborHelper(request, id);
            if (responseDto != null) {
                response.put("msg", "Labor Helper updated successfully");
                response.put("data", responseDto);
                response.put("success", true);
            } else {
                response.put("msg", "Failed to update Labor Helper");
                response.put("success", false);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getallbyuserid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllLaborHelperByUserId(@RequestParam("userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<LaborHelperResponseDto> responseDtos = laborHelperService.getAllLaborHelpersByUserId(userId);
            if (responseDtos.isEmpty()) {
                response.put("msg", "No Labor Helper records found");
                response.put("success", false);
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("Labor Helper Details", responseDtos);
                response.put("data", data);
                response.put("msg", "Labor Helper records fetched successfully");
                response.put("success", true);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getallbypartyid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllLaborHelperByPartyId(@RequestParam("partyId") Long partyId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<LaborHelperResponseDto> responseDtos = laborHelperService.getAllLaborHelpersByPartyId(partyId);
            if (responseDtos.isEmpty()) {
                response.put("msg", "No Labor Helper records found for given party ID");
                response.put("success", false);
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("Labor Helper Details", responseDtos);
                response.put("data", data);
                response.put("msg", "Labor Helper records fetched successfully");
                response.put("success", true);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getallbycontactcategoryid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllLaborHelperByContactCategoryId(@RequestParam("contactCategoryId") Long contactCategoryId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<LaborHelperResponseDto> responseDtos = laborHelperService.getAllLaborHelpersByContactCategoryId(contactCategoryId);
            if (responseDtos.isEmpty()) {
                response.put("msg", "No Labor Helper records found for given category ID");
                response.put("success", false);
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("Labor Helper Details", responseDtos);
                response.put("data", data);
                response.put("msg", "Labor Helper records fetched successfully");
                response.put("success", true);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getbyid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getLaborHelperById(@RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            LaborHelperResponseDto responseDto = laborHelperService.getLaborHelperById(id);
            if (responseDto != null) {
                response.put("data", responseDto);
                response.put("msg", "Labor Helper details fetched successfully");
                response.put("success", true);
            } else {
                response.put("msg", "Labor Helper record not found");
                response.put("success", false);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteLaborHelperById(@RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isDeleted = laborHelperService.deleteLaborHelperById(id);
            if (isDeleted) {
                response.put("msg", "Labor Helper soft deleted successfully");
                response.put("success", true);
            } else {
                response.put("msg", "Failed to delete Labor Helper");
                response.put("success", false);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/getallbypartyandeventdetails")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllLaborHelpersByPartyAndEventDetails(
            @RequestParam("partyId") Long partyId,
            @RequestParam("eventId") Long eventId,
            @RequestParam("eventFunctionId") Long eventFunctionId) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<EventLaborHelperSelectionResponseDto> responseDtos = laborHelperService
                    .getAllLaborHelpersByPartyAndEventDetails(partyId, eventId, eventFunctionId);

            if (responseDtos.isEmpty()) {
                response.put("msg", "No Labor Helper records found for given parameters");
                response.put("success", false);
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("Labor Helper Details", responseDtos);
                response.put("data", data);
                response.put("msg", "Labor Helper records fetched successfully");
                response.put("success", true);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
   @GetMapping("/pdf")
    public ResponseEntity<Map<String, Object>> generatePdf(
            @RequestParam("laborHelperId") Long laborHelperId,
            @RequestParam("userId") Long userId,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {

            byte[] pdf = laborHelperService.generateLaborHelperReport(
                    laborHelperId,
                    userId
            );

            String rootPath = request.getSession()
                    .getServletContext()
                    .getRealPath("/");

            String folderName = String.valueOf(userId);

            File dir = new File(rootPath+ "resources/tempDownload/"+ folderName+ "/");

            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName ="labor-helper-"+ System.currentTimeMillis()+ ".pdf";

            File file = new File(dir, fileName);

            Files.write(file.toPath(),pdf);

            String fileUrl =
                    environment.getProperty("ws_image_path")
                    + "/api/download/pdf/"
                    + folderName
                    + "/"
                    + fileName;

            response.put("success", true);
            response.put(
                    "msg",
                    "PDF generated successfully"
            );
            response.put(
                    "fileUrl",
                    fileUrl
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put("success", false);
            response.put("msg", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}
