package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.exceptions.OrderPersistenceException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TaxDaoImplTests {
    private static Path TAXES_FILE;
    private static final String TAXES_CONTENT = "StateAbbreviation,StateName,TaxRate\n" +
            "TX,Texas,4.45\n" +
            "WA,Washington,9.25\n" +
            "KY,Kentucky,6.00\n" +
            "CA,California,25.00";

    private TaxDaoImpl taxDao;

    @BeforeAll
    static void setUpClass() throws Exception {
        TAXES_FILE = Files.createTempFile("Taxes", ".txt");
        Files.write(TAXES_FILE, TAXES_CONTENT.getBytes());
    }

    @BeforeEach
    void setUp() {
        taxDao = new TaxDaoImpl(TAXES_FILE.toString());
    }

    @AfterAll
    static void deleteClass() throws Exception {
        Files.deleteIfExists(TAXES_FILE);
    }

    @Test
    void testLoadTaxRatesSuccess() throws OrderPersistenceException {
        Map<String, BigDecimal> taxRates = taxDao.loadTaxRatesFromFile();

        assertEquals(4, taxRates.size());

        assertEquals(new BigDecimal("4.45"), taxRates.get("TX"));
        assertEquals(new BigDecimal("9.25"), taxRates.get("WA"));
        assertEquals(new BigDecimal("6.00"), taxRates.get("KY"));
        assertEquals(new BigDecimal("25.00"), taxRates.get("CA"));
    }

    @Test
    void testLoadTaxRatesInvalidFormat() {
        // Create a temporary file with invalid content
        Path invalidFile = null;
        try {
            invalidFile = Files.createTempFile("InvalidTaxes", ".txt");
            String invalidContent = "StateAbbreviation,StateName,TaxRate\n" +
                    "TX,Texas,six.25\n" +
                    "CA,California,seven.25\n";
            Files.write(invalidFile, invalidContent.getBytes());

            TaxDaoImpl invalidTaxDao = new TaxDaoImpl(invalidFile.toString());
            assertThrows(OrderPersistenceException.class, invalidTaxDao::loadTaxRatesFromFile);
        } catch (Exception e) {
            fail("Exception should not be thrown during setup");
        } finally {
            if (invalidFile != null) {
                try {
                    Files.deleteIfExists(invalidFile);
                } catch (Exception e) {
                    // Ignored
                }
            }
        }
    }

    @Test
    void testLoadTaxRatesFileNotFound() {
        TaxDaoImpl invalidTaxDao = new TaxDaoImpl("non_existing_file.txt");
        assertThrows(OrderPersistenceException.class, invalidTaxDao::loadTaxRatesFromFile);
    }
}
