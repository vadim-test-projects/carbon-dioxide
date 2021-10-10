package com.example.test.repository;

import com.example.test.model.Alert;
import com.example.test.model.Sensor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AlertRepository extends CrudRepository<Alert, Long> {

    default Optional<Alert> getOpenedAlertForSensor(Sensor sensor) {
        return findBySensorIdAndEndTimeIsNull(sensor.getId());
    }

    Optional<Alert> findBySensorIdAndEndTimeIsNull(long sensorId);

}
