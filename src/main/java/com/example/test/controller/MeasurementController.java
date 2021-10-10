package com.example.test.controller;

import com.example.test.dto.MeasurementDTO;
import com.example.test.model.Alert;
import com.example.test.model.Measurement;
import com.example.test.model.Sensor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = MeasurementController.REST_URL)
public interface MeasurementController {

    String REST_URL = "/api/v1/sensors/{sensorId}/measurements";

    /**
     * This method is used to post a {@link MeasurementDTO} for a {@link Sensor}.
     * In the background, it will check the {@link Sensor#getCurrentStatus()} of the sensor and adapt it accordingly.
     * Sensor-status adaptation rules:
     * <p><ul>
     * <li>OK -> WARN - 1 critical measurement (created one)
     * <li>WARN -> OK - 1 non-critical measurement (created one)
     * <li>WARN -> ALERT - 3 consecutive critical measurements including the created one (the {@link Alert} will be saved)
     * <li>ALERT -> OK - 3 consecutive non-critical measurements including the created one
     * </ul><p>
     *
     * @param id                    id of the associated sensor
     * @param measurementDTO        DTO of the {@link Measurement} to post
     * @return                      DTO of the created {@link Measurement}
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MeasurementDTO> create(
            @PathVariable Long id,
            @RequestBody MeasurementDTO measurementDTO);

}
