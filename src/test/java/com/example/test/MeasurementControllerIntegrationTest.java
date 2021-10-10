package com.example.test;

import com.example.test.controller.MeasurementController;
import com.example.test.dto.MeasurementDTO;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;

import static com.example.test.Sandbox.getMeasurementDTO;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ActiveProfiles("test")
class MeasurementControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void assertThatMeasurementIsPostedCorrectly() throws Exception {

        val toPost = getMeasurementDTO(1800);

        val toPostJson = JSON_MAPPER.writeValueAsString(toPost);

        val request = new HttpEntity<>(toPostJson, HEADERS);

        val postUrl = MeasurementController.REST_URL.replace("{sensorId}", String.valueOf(3));

        val created = restTemplate.postForObject(postUrl, request, MeasurementDTO.class);

        assertThat(created).isEqualTo(toPost);
    }

}
