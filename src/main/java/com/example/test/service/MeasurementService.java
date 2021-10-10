package com.example.test.service;

import com.example.test.model.Alert;
import com.example.test.model.Measurement;
import com.example.test.model.Sensor;

public interface MeasurementService {

    /**
     * This method is used to create a {@link Measurement} for a {@link Sensor}.
     * In the background, it will check the {@link Sensor#getCurrentStatus()} of the sensor and adapt it accordingly.
     * Sensor-status adaptation rules:
     * <p><ul>
     * <li>OK -> WARN - 1 critical measurement (created one)
     * <li>WARN -> OK - 1 non-critical measurement (created one)
     * <li>WARN -> ALERT - 3 consecutive critical measurements including the created one (the {@link Alert} will be saved)
     * <li>ALERT -> OK - 3 consecutive non-critical measurements including the created one
     * </ul><p>
     *
     * @param sensorId          id of the associated sensor
     * @param measurement       measurement to create
     * @return                  created measurement
     */
    Measurement create(long sensorId, Measurement measurement);
}
