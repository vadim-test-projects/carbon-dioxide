package com.example.test.service;

import com.example.test.exception.IllegalDatabaseState;
import com.example.test.exception.ResourceNotFoundException;
import com.example.test.model.Alert;
import com.example.test.model.Measurement;
import com.example.test.model.Sensor;
import com.example.test.repository.AlertRepository;
import com.example.test.repository.MeasurementRepository;
import com.example.test.repository.SensorRepository;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.test.Sandbox.*;
import static com.example.test.service.MeasurementServiceImpl.ILLEGAL_SB_STATE_MESSAGE_FORMAT;
import static com.example.test.service.MeasurementServiceImpl.SENSOR_NOT_FOUND_MESSAGE_FORMAT;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeasurementServiceTest {

    private static final int CO2_BELOW_EXTREME = 1800;
    private static final int CO2_ABOVE_EXTREME = 2200;

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private MeasurementServiceImpl service;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "extremeValueOk", 2000);
    }

    @Test
    void assertThatSensorIsSetFromOkToWarn_whenCO2HigherThanExtremeValue() {

        val sensor = getCreatedSensor(Sensor.Status.OK);
        val measurementToCreate = getCreatedMeasurement(CO2_ABOVE_EXTREME);

        val sensorSpied = spy(sensor);

        when(sensorRepository.findById(sensor.getId())).thenReturn(Optional.of(sensorSpied));
        when(measurementRepository.save(any())).thenReturn(measurementToCreate);

        val created = service.create(sensor.getId(), measurementToCreate);

        verify(sensorSpied, times(1)).setCurrentStatus(Sensor.Status.WARN);
        verify(sensorSpied, never()).setCurrentStatus(Sensor.Status.OK);
        verify(sensorSpied, never()).setCurrentStatus(Sensor.Status.ALERT);

        verify(alertRepository, never()).save(any(Alert.class));
        verify(alertRepository, never()).getOpenedAlertForSensor(any(Sensor.class));

        assertThat(created).isEqualTo(measurementToCreate);
    }

    @Test
    void assertThatSensorIsSetFromWarnToOk_whenCO2LowerThanExtremeValue() {

        val sensor = getCreatedSensor(Sensor.Status.WARN);
        val measurementToCreate = getCreatedMeasurement(CO2_BELOW_EXTREME);

        val sensorSpied = spy(sensor);

        when(sensorRepository.findById(sensor.getId())).thenReturn(Optional.of(sensorSpied));
        when(measurementRepository.save(any())).thenReturn(measurementToCreate);

        val created = service.create(sensor.getId(), measurementToCreate);

        verify(sensorSpied, never()).setCurrentStatus(Sensor.Status.WARN);
        verify(sensorSpied, times(1)).setCurrentStatus(Sensor.Status.OK);
        verify(sensorSpied, never()).setCurrentStatus(Sensor.Status.ALERT);

        verify(alertRepository, never()).save(any(Alert.class));
        verify(alertRepository, never()).getOpenedAlertForSensor(any(Sensor.class));

        assertThat(created).isEqualTo(measurementToCreate);
    }

    @Test
    void assertThatSensorIsSetFromWarnToAlert_whenCO2IsAboveExtreme3TimesConsecutively() {

        val sensor = getCreatedSensor(Sensor.Status.WARN);
        val measurementToCreate = getCreatedMeasurement(CO2_ABOVE_EXTREME);

        val sensorSpied = spy(sensor);

        when(sensorRepository.findById(sensor.getId())).thenReturn(Optional.of(sensorSpied));
        when(measurementRepository.save(any())).thenReturn(measurementToCreate);
        when(measurementRepository.getLast3MeasurementsForSensor(sensorSpied)).thenReturn(getMeasurements(CO2_ABOVE_EXTREME));

        val created = service.create(sensor.getId(), measurementToCreate);

        verify(sensorSpied, never()).setCurrentStatus(Sensor.Status.WARN);
        verify(sensorSpied, never()).setCurrentStatus(Sensor.Status.OK);
        verify(sensorSpied, times(1)).setCurrentStatus(Sensor.Status.ALERT);

        verify(alertRepository, only()).save(any(Alert.class));
        verify(alertRepository, never()).getOpenedAlertForSensor(any(Sensor.class));

        assertThat(created).isEqualTo(measurementToCreate);
    }

    @Test
    void assertThatSensorIsSetFromAlertToOk_whenCO2IsBelowExtreme3TimesConsecutively() {

        val sensor = getCreatedSensor(Sensor.Status.ALERT);
        val measurementToCreate = getCreatedMeasurement(CO2_BELOW_EXTREME);

        val sensorSpied = spy(sensor);

        when(sensorRepository.findById(sensor.getId())).thenReturn(Optional.of(sensorSpied));
        when(measurementRepository.save(any())).thenReturn(measurementToCreate);
        when(measurementRepository.getLast3MeasurementsForSensor(sensorSpied)).thenReturn(getMeasurements(CO2_BELOW_EXTREME));
        when(alertRepository.getOpenedAlertForSensor(sensorSpied)).thenReturn(Optional.of(getAlert(null)));

        val created = service.create(sensor.getId(), measurementToCreate);

        verify(sensorSpied, never()).setCurrentStatus(Sensor.Status.WARN);
        verify(sensorSpied, times(1)).setCurrentStatus(Sensor.Status.OK);
        verify(sensorSpied, never()).setCurrentStatus(Sensor.Status.ALERT);

        verify(alertRepository, never()).save(any(Alert.class));
        verify(alertRepository, times(1)).getOpenedAlertForSensor(sensorSpied);

        assertThat(created).isEqualTo(measurementToCreate);
    }

    @Test
    void assertThatThrowsIllegalDatabaseState_whenOpenedAlertIsNotFound() {

        val sensor = getCreatedSensor(Sensor.Status.ALERT);
        val measurementToCreate = getCreatedMeasurement(CO2_BELOW_EXTREME);

        when(sensorRepository.findById(sensor.getId())).thenReturn(Optional.of(sensor));
        when(measurementRepository.save(any())).thenReturn(measurementToCreate);
        when(measurementRepository.getLast3MeasurementsForSensor(sensor)).thenReturn(getMeasurements(CO2_BELOW_EXTREME));
        when(alertRepository.getOpenedAlertForSensor(sensor)).thenReturn(Optional.empty());

        val sensorId = sensor.getId();

        IllegalDatabaseState exception = assertThrows(
                IllegalDatabaseState.class, () -> service.create(sensorId, measurementToCreate));

        assertThat(exception.getMessage()).hasToString(String.format(ILLEGAL_SB_STATE_MESSAGE_FORMAT, sensorId));
    }

    @Test
    void assertThatThrowsResourceNotFound_whenSensorIsNotFound() {

        val sensorId = 123;

        val measurementToCreate = getCreatedMeasurement(CO2_BELOW_EXTREME);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> service.create(sensorId, measurementToCreate));

        assertThat(exception.getMessage()).hasToString(String.format(SENSOR_NOT_FOUND_MESSAGE_FORMAT, sensorId));
    }

    private static List<Measurement> getMeasurements(final int co2) {

        val measurements = new ArrayList<Measurement>();

        val mostRecentMeasurement = getCreatedMeasurement(co2);
        mostRecentMeasurement.setTime(now().minusDays(1));

        measurements.add(mostRecentMeasurement);

        val middleMeasurement = getCreatedMeasurement(co2);
        middleMeasurement.setTime(now().minusDays(2));

        measurements.add(mostRecentMeasurement);

        val eldestMeasurement = getCreatedMeasurement(co2);
        eldestMeasurement.setTime(now().minusDays(3));

        measurements.add(eldestMeasurement);

        return measurements;

    }

}
