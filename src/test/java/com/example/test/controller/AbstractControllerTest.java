package com.example.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public abstract class AbstractControllerTest {

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    protected static final String BASE_LOCALHOST_URL = "http://localhost";

    @BeforeAll
    static void init() {
        // to enable date/time binding
        OBJECT_MAPPER.findAndRegisterModules();
    }

    @Autowired
    protected MockMvc mockMvc;

}
