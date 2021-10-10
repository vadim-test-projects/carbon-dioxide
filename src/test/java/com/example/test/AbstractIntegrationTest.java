package com.example.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public abstract class AbstractIntegrationTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    protected static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    protected static final HttpHeaders HEADERS = new HttpHeaders();

    @BeforeAll
    static void setUp() {
        JSON_MAPPER.findAndRegisterModules();
        HEADERS.setContentType(MediaType.APPLICATION_JSON);
    }
}
