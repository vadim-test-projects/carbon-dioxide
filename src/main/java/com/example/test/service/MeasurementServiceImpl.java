package com.example.test.service;

import com.example.test.exception.IllegalDatabaseState;
import com.example.test.exception.ResourceNotFoundException;
import com.example.test.model.Alert;
import com.example.test.model.Measurement;
import com.example.test.model.Sensor;
import com.example.test.repository.AlertRepository;
import com.example.test.repository.MeasurementRepository;
import com.example.test.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class MeasurementServiceImpl implements MeasurementService {

    public static final String SENSOR_NOT_FOUND_MESSAGE_FORMAT = "Sensor with id %d does not exist";
    public static final String ILLEGAL_SB_STATE_MESSAGE_FORMAT = "Sensor with id %d and state ALERT must contain an opened alert";

    @Value("${alert.extremeValueOk}")
    private int extremeValueOk;

    private final MeasurementRepository measurementRepository;

    private final SensorRepository sensorRepository;

    private final AlertRepository alertRepository;

    @Override
    @Transactional
    @SuppressWarnings("java:S1066")
    public Measurement create(final long sensorId, final Measurement measurement) {

        val sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException(format(SENSOR_NOT_FOUND_MESSAGE_FORMAT, sensorId)));

        measurement.setSensor(sensor);

        val created = measurementRepository.save(measurement);

        val currentSensorStatus = sensor.getCurrentStatus();

        val measuredCo2 = measurement.getCo2();

        // OK -> WARN
        if (Sensor.Status.OK.equals(currentSensorStatus)) {

            if (measuredCo2 > extremeValueOk) {
                sensor.setCurrentStatus(Sensor.Status.WARN);
            }

        }

        // WARN -> ALERT or WARN -> OK
        if (Sensor.Status.WARN.equals(currentSensorStatus)) {

            if (measuredCo2 <= extremeValueOk) {
                sensor.setCurrentStatus(Sensor.Status.OK);
            } else {

                val measurements = measurementRepository.getLast3MeasurementsForSensor(sensor);

                if (isAlertToSet(measurements)) {
                    val alert = new Alert();
                    alert.setSensor(sensor);
                    alert.setStartTime(measurement.getTime());

                    alertRepository.save(alert);

                    sensor.setCurrentStatus(Sensor.Status.ALERT);
                }
            }

        }

        // ALERT -> OK
        if (Sensor.Status.ALERT.equals(currentSensorStatus)) {

            val measurements = measurementRepository.getLast3MeasurementsForSensor(sensor);

            if (isOkToSet(measurements)) {
                sensor.setCurrentStatus(Sensor.Status.OK);

                val openedAlert =
                        alertRepository.getOpenedAlertForSensor(sensor)
                                .orElseThrow(() -> new IllegalDatabaseState(format(ILLEGAL_SB_STATE_MESSAGE_FORMAT, sensorId)));

                openedAlert.setMeasurements(measurements);

                openedAlert.setEndTime(measurement.getTime());
            }

        }

        return created;
    }

    private boolean isAlertToSet(final List<Measurement> measurements) {
        return measurements.stream().allMatch(m -> m.getCo2() > extremeValueOk);
    }

    private boolean isOkToSet(final List<Measurement> measurements) {
        return measurements.stream().allMatch(m -> m.getCo2() <= extremeValueOk);
    }
}
