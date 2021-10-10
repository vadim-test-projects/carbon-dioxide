package com.example.test.mapper;

import com.example.test.dto.MeasurementDTO;
import com.example.test.model.Measurement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MeasurementMapper {

    Measurement toEntity(MeasurementDTO measurementDTO);

    MeasurementDTO toDTO(Measurement measurement);

}
