package com.example.test.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;

/**
 * This is a domain object that represents a single sample from a sensor
 */
@Entity
@Table
@Data
@EqualsAndHashCode(callSuper = false)
public class Measurement extends BaseEntity {

    /**
     * A sensor associated with the measurement
     */
    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "SENSOR_ID")
    @NotNull(message = "Measurement must be associated with Sensor")
    private Sensor sensor;

    /**
     * Amount of co2 measured
     */
    @Min(value = 0, message = "CO2 amount can no be negative")
    @NotNull(message = "Amount of CO2 should be present")
    private int co2;

    /**
     * Timestamp of the measurement sampling
     */
    @Past
    @NotNull(message = "Time of sample should be present")
    private LocalDateTime time;

}
