package com.example.test.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * This is a base domain entity that is used to encapsulate id and logic related to its generation
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    private static final long serialVersionUID = 1L;

    public static final int SEQUENCE_INITIAL_VALUE = 10_000_001;

    /**
     * Entity identifier / primary key
     */
    @Id
    @SequenceGenerator(name = "app-sequence", sequenceName = "APP_SEQUENCE", allocationSize = 1, initialValue = SEQUENCE_INITIAL_VALUE)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app-sequence")
    protected Long id;

}
