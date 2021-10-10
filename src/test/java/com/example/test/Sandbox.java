package com.example.test;

import com.example.test.dto.MeasurementDTO;
import com.example.test.model.Alert;
import com.example.test.model.Measurement;
import com.example.test.model.Sensor;
import lombok.val;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

import static java.time.LocalDateTime.now;

public class Sandbox {

    private static long INITIAL_ID = 1000;

    private static long getId() {
        return ++INITIAL_ID;
    }

    public static Alert getAlert(final LocalDateTime endTime) {
        val alert = new Alert();

        alert.setStartTime(now().minusHours(1));
        alert.setEndTime(endTime);

        return alert;
    }

    public static Sensor getCreatedSensor(final Sensor.Status status) {
        val sensor = new Sensor();

        sensor.setId(getId());
        sensor.setAlerts(Collections.emptyList());
        sensor.setCurrentStatus(status);

        return sensor;
    }

    @SuppressWarnings("SameParameterValue")
    public static Measurement getCreatedMeasurement(final int co2) {
        val measurement = new Measurement();

        measurement.setId(getId());
        measurement.setCo2(co2);
        measurement.setTime(now());
        measurement.setSensor(getCreatedSensor(Sensor.Status.OK));

        return measurement;
    }

    @SuppressWarnings("SameParameterValue")
    public static MeasurementDTO getMeasurementDTO(final int co2) {
        val measurementRequestDTO = new MeasurementDTO();

        measurementRequestDTO.setCo2(co2);
        measurementRequestDTO.setTime(new Date());

        return measurementRequestDTO;
    }

}
