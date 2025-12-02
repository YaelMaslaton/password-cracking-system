package com.example.master.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HashFileValidator.
 */
class HashFileValidatorTest {

    private HashFileValidator validator;

    @BeforeEach
    void setUp() {
        validator = new HashFileValidator();
    }

    @Test
    void testValidFileWithMultipleHashes() throws IOException {
        String content = "5d41402abc4b2a76b9719d911017c592\n" +
                        "098f6bcd4621d373cade4e832627b4f6\n" +
                        "ad0234829205b9033196ba818f7a872b";
        
        MockMultipartFile file = new MockMultipartFile(
            "file", "hashes.txt", "text/plain", content.getBytes());

        List<String> hashes = validator.validateAndExtractHashes(file);

        assertEquals(3, hashes.size());
        assertEquals("5d41402abc4b2a76b9719d911017c592", hashes.get(0));
        assertEquals("098f6bcd4621d373cade4e832627b4f6", hashes.get(1));
        assertEquals("ad0234829205b9033196ba818f7a872b", hashes.get(2));
    }

    @Test
    void testEmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
            "file", "empty.txt", "text/plain", new byte[0]);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validateAndExtractHashes(file)
        );

        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    void testNullFile() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validateAndExtractHashes(null)
        );

        assertEquals("File is missing", exception.getMessage());
    }

    @Test
    void testInvalidMD5Hash() {
        String content = "5d41402abc4b2a76b9719d911017c592\n" +
                        "invalid_hash\n" +
                        "ad0234829205b9033196ba818f7a872b";
        
        MockMultipartFile file = new MockMultipartFile(
            "file", "hashes.txt", "text/plain", content.getBytes());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validateAndExtractHashes(file)
        );

        assertEquals("Invalid MD5 hash at line 2: invalid_hash", exception.getMessage());
    }

    @Test
    void testDuplicateHashes() {
        String content = "5d41402abc4b2a76b9719d911017c592\n" +
                        "098f6bcd4621d373cade4e832627b4f6\n" +
                        "5d41402abc4b2a76b9719d911017c592";
        
        MockMultipartFile file = new MockMultipartFile(
            "file", "hashes.txt", "text/plain", content.getBytes());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validateAndExtractHashes(file)
        );

        assertEquals("Duplicate hash detected: 5d41402abc4b2a76b9719d911017c592", exception.getMessage());
    }

    @Test
    void testBlankLinesAreFiltered() throws IOException {
        String content = "5d41402abc4b2a76b9719d911017c592\n" +
                        "\n" +
                        "   \n" +
                        "098f6bcd4621d373cade4e832627b4f6\n" +
                        "\t\n" +
                        "ad0234829205b9033196ba818f7a872b";
        
        MockMultipartFile file = new MockMultipartFile(
            "file", "hashes.txt", "text/plain", content.getBytes());

        List<String> hashes = validator.validateAndExtractHashes(file);

        assertEquals(3, hashes.size());
        assertEquals("5d41402abc4b2a76b9719d911017c592", hashes.get(0));
        assertEquals("098f6bcd4621d373cade4e832627b4f6", hashes.get(1));
        assertEquals("ad0234829205b9033196ba818f7a872b", hashes.get(2));
    }

    @Test
    void testUnsupportedContentType() {
        String content = "5d41402abc4b2a76b9719d911017c592";
        
        MockMultipartFile file = new MockMultipartFile(
            "file", "hashes.json", "application/json", content.getBytes());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validateAndExtractHashes(file)
        );

        assertEquals("Unsupported file type", exception.getMessage());
    }

    @Test
    void testFileWithOnlyBlankLines() {
        String content = "\n   \n\t\n  ";
        
        MockMultipartFile file = new MockMultipartFile(
            "file", "blank.txt", "text/plain", content.getBytes());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validateAndExtractHashes(file)
        );

        assertEquals("File contains no hashes", exception.getMessage());
    }

    @Test
    void testMixOfValidAndInvalidHashes() {
        String content = "5d41402abc4b2a76b9719d911017c592\n" +
                        "098f6bcd4621d373cade4e832627b4f6\n" +
                        "short_hash\n" +
                        "ad0234829205b9033196ba818f7a872b";
        
        MockMultipartFile file = new MockMultipartFile(
            "file", "hashes.txt", "text/plain", content.getBytes());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validateAndExtractHashes(file)
        );

        assertEquals("Invalid MD5 hash at line 3: short_hash", exception.getMessage());
    }

    @Test
    void testCsvContentType() throws IOException {
        String content = "5d41402abc4b2a76b9719d911017c592\n" +
                        "098f6bcd4621d373cade4e832627b4f6";
        
        MockMultipartFile file = new MockMultipartFile(
            "file", "hashes.csv", "text/csv", content.getBytes());

        List<String> hashes = validator.validateAndExtractHashes(file);

        assertEquals(2, hashes.size());
    }

    @Test
    void testHashWithWrongLength() {
        String content = "5d41402abc4b2a76b9719d911017c59"; // 31 chars
        
        MockMultipartFile file = new MockMultipartFile(
            "file", "hashes.txt", "text/plain", content.getBytes());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validateAndExtractHashes(file)
        );

        assertEquals("Invalid MD5 hash at line 1: 5d41402abc4b2a76b9719d911017c59", exception.getMessage());
    }

    @Test
    void testHashWithInvalidCharacters() {
        String content = "5d41402abc4b2a76b9719d911017c59G"; // Contains 'G'
        
        MockMultipartFile file = new MockMultipartFile(
            "file", "hashes.txt", "text/plain", content.getBytes());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validateAndExtractHashes(file)
        );

        assertEquals("Invalid MD5 hash at line 1: 5d41402abc4b2a76b9719d911017c59G", exception.getMessage());
    }
}