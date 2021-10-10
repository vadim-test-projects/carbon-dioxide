package com.example.test.controller;

import com.example.test.dto.MeasurementDTO;
import com.example.test.mapper.MeasurementMapper;
import com.example.test.service.MeasurementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MeasurementControllerImpl implements MeasurementController {

    private final MeasurementService measurementService;

    private final MeasurementMapper measurementMapper;

    @Override
    public ResponseEntity<MeasurementDTO> create(final Long sensorId, final MeasurementDTO measurementDTO) {

        val created = measurementService.create(sensorId, measurementMapper.toEntity(measurementDTO));

        val headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getSensor().getId(), created.getId()).toUri());

        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(measurementMapper.toDTO(created), headers, HttpStatus.CREATED);
    }
}
