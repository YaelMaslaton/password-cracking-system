package com.example.master.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.master.common.AppConstants.*;

/**
 * Validator for hash files uploaded to the system.
 */
@Slf4j
@Component
public class HashFileValidator {

    /**
     * Validates and extracts MD5 hashes from uploaded file.
     *
     * @param file The uploaded file to validate
     * @return List of valid MD5 hashes
     * @throws IllegalArgumentException if validation fails
     * @throws IOException if file reading fails
     */
    public List<String> validateAndExtractHashes(MultipartFile file) throws IOException {
        validateFileNotNull(file);
        validateFileNotEmpty(file);
        validateContentType(file);

        List<String> lines = readAndFilterLines(file);
        validateHasContent(lines);

        List<String> validHashes = new ArrayList<>();
        Set<String> seenHashes = new HashSet<>();

        for (int i = 0; i < lines.size(); i++) {
            String hash = lines.get(i);
            validateMD5Format(hash, i + 1);
            validateNoDuplicate(hash, seenHashes);

            validHashes.add(hash);
            seenHashes.add(hash);
        }

        log.info(LOG_VALIDATED_HASHES, validHashes.size());
        return validHashes;
    }

    private void validateFileNotNull(MultipartFile file) {
        if (file == null) {
            throw new IllegalArgumentException(ERROR_FILE_MISSING);
        }
    }

    private void validateFileNotEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(ERROR_FILE_EMPTY);
        }
    }

    private void validateContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ACCEPTED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(ERROR_UNSUPPORTED_FILE_TYPE);
        }
    }

    private List<String> readAndFilterLines(MultipartFile file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty()) {
                    lines.add(trimmedLine);
                }
            }
        }
        return lines;
    }

    private void validateHasContent(List<String> lines) {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException(ERROR_FILE_NO_HASHES);
        }
    }

    private void validateMD5Format(String hash, int lineNumber) {
        if (!MD5_PATTERN.matcher(hash).matches()) {
            throw new IllegalArgumentException(ERROR_INVALID_MD5_HASH + lineNumber + ": " + hash);
        }
    }

    private void validateNoDuplicate(String hash, Set<String> seenHashes) {
        if (seenHashes.contains(hash)) {
            throw new IllegalArgumentException(ERROR_DUPLICATE_HASH + hash);
        }
    }

}