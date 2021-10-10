package com.example.test.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * It is a domain object that represents a sensor that aggregates measurements
 */
@Entity
@Table
@Data
@EqualsAndHashCode(callSuper = false)
public class Sensor extends BaseEntity {

    /**
     * All the measurements associated with the sensor
     */
    @JsonBackReference
    @OneToMany(mappedBy = "sensor")
    @ToString.Exclude
    private List<Measurement> measurements;

    /**
     * The <b>most recent</b> status of the sensor
     */
    @NotNull(message = "Sensor should always have a valid status")
    @Enumerated(EnumType.STRING)
    private Status currentStatus;

    /**
     * A list of all alerts associated with the sensor
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "sensor")
    @ToString.Exclude
    private List<Alert> alerts;

    /**
     * Possible status that the sensor can be in
     */
    public enum Status {
        OK,
        WARN,
        ALERT
    }

}
