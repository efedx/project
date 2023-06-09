package com.defect.controllers;

import com.defect.dto.RegisterDefectDto;
import com.defect.entities.Defect;
import com.defect.entities.Vehicle;
import com.defect.services.DefectImageService;
import com.defect.services.ListDefectsService;
import com.defect.services.RegisterDefectsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DefectsController {

    private final ListDefectsService listDefectsService;
    private final DefectImageService defectImageService;
    private final RegisterDefectsService registerDefectsService;

    //-----------------------------------------------------------------------------------------------

    @PostMapping("/registerDefects")
    public String registerDefects(@RequestPart("registerDefectDto") List<RegisterDefectDto> registerDefectDtoList,
                                  @RequestPart("defectImage") MultipartFile defectImage,
                                  @RequestHeader("Authorization") String authorizationHeader) throws Exception {

        byte[] defectImageByte;

        try {
            defectImageByte = defectImage.getBytes();
        } catch (IOException e) {
            // handle the exception, e.g. log an error message or return an error response
            return "An error occurred while creating defect image";
        }
        registerDefectsService.registerDefects(registerDefectDtoList, defectImageByte);
        return "Defects saved";
    }

    @GetMapping("/defects/getDefectImage/{defectId}")
    public ResponseEntity<byte[]> getDefectImage(@PathVariable Long defectId) throws Exception {

        byte[] combinedImageByte = defectImageService.getDefectImage(defectId);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(combinedImageByte);
    }

    @GetMapping("/defects/getDefectsByVehicle/{vehicleId}/page/{pageNumber}")
    public ResponseEntity<Page<Defect>> getDefectsByVehicleId(@PathVariable int pageNumber,
                                                              @PathVariable Long vehicleId,
                                                              @RequestParam String sortDirection,
                                                              @RequestParam String sortField) {
        return ResponseEntity.ok().body(listDefectsService.getDefectsByVehicleId(vehicleId, pageNumber, sortField, sortDirection));
    }

    @GetMapping("/defects/getAllDefects/page/{pageNumber}")
    public ResponseEntity<Page<Vehicle>> getAllDefects(@PathVariable int pageNumber,
                                                       @RequestParam String sortDirection,
                                                       @RequestParam String sortField,
                                                       @RequestParam(required = false) String defectName) {
        if(defectName != null) {
            return ResponseEntity.ok().body(listDefectsService.getAllDefects(pageNumber, sortField, sortDirection, defectName));
        }
        else {
            return ResponseEntity.ok().body(listDefectsService.getAllDefects(pageNumber, sortField, sortDirection));
        }
    }
}
