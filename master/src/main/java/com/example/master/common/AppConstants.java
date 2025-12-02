package com.example.master.common;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Application constants for the Master Service.
 */
public final class AppConstants {

    private AppConstants() {
        // Utility class
    }

    // PHONE RANGES
    public static final long PHONE_RANGE_START = 0L;
    public static final long PHONE_RANGE_END = 99999999L;

    // STATUS VALUES
    public static final String STATUS_RUNNING = "RUNNING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FOUND = "FOUND";
    public static final String STATUS_NOT_FOUND = "NOT_FOUND";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_SUBMITTED = "SUBMITTED";

    // ERROR MESSAGES
    public static final String ERROR_FILE_MISSING = "File is missing";
    public static final String ERROR_FILE_EMPTY = "File is empty";
    public static final String ERROR_UNSUPPORTED_FILE_TYPE = "Unsupported file type";
    public static final String ERROR_FILE_NO_HASHES = "File contains no hashes";
    public static final String ERROR_INVALID_MD5_HASH = "Invalid MD5 hash at line ";
    public static final String ERROR_DUPLICATE_HASH = "Duplicate hash detected: ";
    public static final String ERROR_SUBTASK_NOT_FOUND = "SubTask not found: ";
    public static final String ERROR_NO_MINIONS_CONFIGURED = "No minions configured";
    public static final String ERROR_FILE_PROCESSING = "File processing error";
    public static final String ERROR_INTERNAL = "Internal error";

    // FILE VALIDATION
    public static final Pattern MD5_PATTERN = Pattern.compile("^[0-9a-fA-F]{32}$");
    public static final Set<String> ACCEPTED_CONTENT_TYPES = Set.of("text/plain", "text/csv");

    // NUMERIC CONSTANTS
    public static final int ZERO = 0;
    public static final int ONE = 1;

    // LOG MESSAGES
    public static final String LOG_VALIDATED_HASHES = "Successfully validated {} hashes from file";

    // VALIDATION MESSAGES
    public static final String ERROR_MINIONS_MUST_BE_POSITIVE = "Number of minions must be positive";

    // TIMEOUT SETTINGS
    public static final long SUBTASK_TIMEOUT_MINUTES = 30;
    public static final String STATUS_TIMEOUT = "TIMEOUT";
    public static final String STATUS_FAILED = "FAILED";
}