package com.example.test.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;
import java.util.List;

/**
 * It is a domain object that represents a list of consecutive critical measurements for a single sensor
 */
@Entity
@Table
@Data
@EqualsAndHashCode(callSuper = false)
public class Alert extends BaseEntity {

    /**
     * Time when alert was created ({@link Sensor} moved back to ALERT status)
     */
    @Past
    @NotNull(message = "Alert must contain starting time")
    private LocalDateTime startTime;

    /**
     * Time when alert was closed ({@link Sensor} moved back to OK status)
     */
    @Past
    private LocalDateTime endTime;

    /**
     * A sensor that is associated with an alert
     */
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "SENSOR_ID")
    @NotNull(message = "Alert should be associated with a sensor")
    private Sensor sensor;

    /**
     * A list of critical measurements
     */
    @OneToMany
    @JoinColumn(name="ALERT_ID", referencedColumnName = "ID")
    @NotEmpty(message = "There should be at least one measurement")
    List<Measurement> measurements;

}
