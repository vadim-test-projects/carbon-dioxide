package com.example.test.controller;

import com.example.test.exception.ResourceNotFoundException;
import com.example.test.mapper.MeasurementMapper;
import com.example.test.service.MeasurementService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import javax.validation.ValidationException;

import static com.example.test.Sandbox.getCreatedMeasurement;
import static com.example.test.Sandbox.getMeasurementDTO;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MeasurementControllerImpl.class)
class MeasurementControllerTest extends AbstractControllerTest {

    private final static String DEFAULT_ID = "1234534";

    private final static int DEFAULT_CO2 = 1800;

    @MockBean
    private MeasurementService measurementService;

    @MockBean
    private MeasurementMapper measurementMapper;

    @Test
    @SuppressWarnings("squid:S2699")
    void postMeasurementShouldReturnOk() throws Exception {

        val createdMeasurement = getCreatedMeasurement(DEFAULT_CO2);

        when(measurementService.create(anyLong(), any())).thenReturn(createdMeasurement);
        when(measurementMapper.toEntity(any())).thenReturn(createdMeasurement);

        val postUrl = MeasurementController.REST_URL.replace("{sensorId}", String.valueOf(createdMeasurement.getSensor().getId()));

        val measurementDtoJson = OBJECT_MAPPER.writeValueAsString(getMeasurementDTO(DEFAULT_CO2));

        mockMvc.perform(
                post(postUrl)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(measurementDtoJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(
                        header().string("Location",
                                BASE_LOCALHOST_URL + postUrl + "/" + createdMeasurement.getId()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @SuppressWarnings("squid:S2699")
    void postMeasurementShouldReturnBadRequest_whenResourceNotFound() throws Exception {

        val notFoundMessage = "Not found message";

        when(measurementService.create(anyLong(), any())).thenThrow(new ResourceNotFoundException(notFoundMessage));

        val result = mockMvc.perform(
                post(MeasurementController.REST_URL.replace("{sensorId}", DEFAULT_ID))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(getMeasurementDTO(DEFAULT_CO2))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).hasToString(notFoundMessage);
    }

    @Test
    @SuppressWarnings("squid:S2699")
    void postMeasurementShouldInternalServerError_whenUnknownExceptionOccurs() throws Exception {

        when(measurementService.create(anyLong(), any())).thenThrow(new ValidationException("Some message"));

        val result = mockMvc.perform(
                post(MeasurementController.REST_URL.replace("{sensorId}", DEFAULT_ID))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(getMeasurementDTO(DEFAULT_CO2))))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();

        // 'Unknown error' message is defined in the RestExceptionHandler class
        assertThat(result.getResponse().getContentAsString()).hasToString("Unknown error");
    }

}
