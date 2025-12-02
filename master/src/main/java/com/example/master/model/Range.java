package com.example.master.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a range of phone numbers for password cracking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Range {
    private long rangeFrom;
    private long rangeTo;
}