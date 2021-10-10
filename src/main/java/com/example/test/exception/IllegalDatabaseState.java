package com.example.test.exception;

import com.example.test.model.Alert;
import com.example.test.model.Sensor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class represents exceptions that appear when our database is in inconsistent state, e.g. there is no opened {@link Alert}
 * when {@link Sensor} is in the ALERT status
 */
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class IllegalDatabaseState extends RuntimeException {

    private final String message;

}
