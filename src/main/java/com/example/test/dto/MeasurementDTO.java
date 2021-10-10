package com.example.test.dto;

import com.example.test.model.Measurement;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import lombok.Data;

import java.util.Date;

/**
 * This is a data transfer object (DTO) of {@link Measurement}
 */
@Data
public class MeasurementDTO {

    private int co2;

    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    private Date time;

}
