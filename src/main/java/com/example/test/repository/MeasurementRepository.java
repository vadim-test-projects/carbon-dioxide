package com.example.test.repository;

import com.example.test.model.Measurement;
import com.example.test.model.Sensor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MeasurementRepository extends CrudRepository<Measurement, Long> {

    /**
     * This method returns 3 last measurements associated with the given sensor ordered by time of sampling in descending order
     *
     * @param sensor        the sensor to get measurements from
     * @return              3 last measurements ordered by time of sampling in descending order
     */
    default List<Measurement> getLast3MeasurementsForSensor(Sensor sensor) {
        return findTop3BySensorIdOrderByTimeDesc(sensor.getId());
    }

    List<Measurement> findTop3BySensorIdOrderByTimeDesc(long sensorId);

}
