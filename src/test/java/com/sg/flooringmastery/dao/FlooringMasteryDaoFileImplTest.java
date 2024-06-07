package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.service.ProductDao;
import com.sg.flooringmastery.service.ProductDaoImpl;
import com.sg.flooringmastery.service.TaxDao;
import com.sg.flooringmastery.service.TaxDaoImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.*;

public class FlooringMasteryDaoFileImplTest {
    TaxDao testTaxDao;
    ProductDao testProductDao;

    @BeforeEach
    void setTaxFileUp() throws IOException {
        String testFile = "src/main/java/com.sg.flooringmastery/Taxes.txt";
        new FileWriter(testFile);

        try (PrintWriter writer = new PrintWriter(new FileWriter(testFile))) {
            writer.println("State,StateName,TaxRate");
            writer.println("TX,Texas,4.45");
            writer.println("WA,Washington,9.25");
            writer.println("KY,Kentucky,6.00");
            writer.println("CA,California,25.00");
        }
    }

    // Sets up the tax file with valid data and verifies the loaded tax rates
    @Test
    void testLoadValidTaxRatesFromFile() {
        try {
            setTaxFileUp();

            testTaxDao = new TaxDaoImpl("src/main/java/com.sg.flooringmastery/Taxes.txt");

            Map<String, BigDecimal> taxRates = testTaxDao.loadTaxRatesFromFile();

            assertEquals(4, taxRates.size());
            assertEquals(new BigDecimal("4.45"), taxRates.get("TX"));
            assertEquals(new BigDecimal("9.25"), taxRates.get("WA"));
        } catch (OrderPersistenceException | IOException e) {
            fail("Exception should not be thrown");
        }
    }

    // Sets up the tax file with an invalid tax rate and throws an exception due to the invalid data
    @Test
    void testLoadInvalidTaxRatesFromFile() {
        try {
            String testFile = "src/main/java/com.sg.flooringmastery/Taxes.txt";
            new FileWriter(testFile);

            try (PrintWriter writer = new PrintWriter(new FileWriter(testFile))) {
                writer.println("State,StateName,TaxRate");
                writer.println("TX,Texas,4.45");
                writer.println("WA,Washington,invalid");
                writer.println("KY,Kentucky,6.00");
                writer.println("CA,California,25.00");
            }

            testTaxDao = new TaxDaoImpl(testFile);

            assertThrows(OrderPersistenceException.class, () -> {
                testTaxDao.loadTaxRatesFromFile();
            });
        } catch (IOException e) {
            fail("IOException should not be thrown");
        }
    }

    // Tests the case where the tax file is not found
    @Test
    void testLoadTaxRatesFromFile_FileNotFound() {
        TaxDaoImpl taxDao = new TaxDaoImpl("src/main/java/com.sg.flooringmastery/nonexistent_file.txt");
        assertThrows(OrderPersistenceException.class, () -> {
            taxDao.loadTaxRatesFromFile();
        });
    }

    @BeforeEach
    void setUp() {
        testProductDao = new ProductDaoImpl();
    }

    // Checks specific product types and their associated costs, received from the Products.txt file
    // Handles OrderPersistenceException
    @Test
    void testGetProductTypesFromFile() {
        try {
            Map<String, ProductDaoImpl> productTypes = testProductDao.getProductTypesFromFile();
            assertNotNull(productTypes);
            assertEquals(4, productTypes.size());

            assertEquals(new BigDecimal("2.25"), productTypes.get("Carpet").getCostPerSquareFoot());
            assertEquals(new BigDecimal("2.10"), productTypes.get("Carpet").getLaborCostPerSquareFoot());

            assertEquals(new BigDecimal("1.75"), productTypes.get("Laminate").getCostPerSquareFoot());
            assertEquals(new BigDecimal("2.10"), productTypes.get("Laminate").getLaborCostPerSquareFoot());

            assertEquals(new BigDecimal("3.50"), productTypes.get("Tile").getCostPerSquareFoot());
            assertEquals(new BigDecimal("4.15"), productTypes.get("Tile").getLaborCostPerSquareFoot());

            assertEquals(new BigDecimal("5.15"), productTypes.get("Wood").getCostPerSquareFoot());
            assertEquals(new BigDecimal("4.75"), productTypes.get("Wood").getLaborCostPerSquareFoot());
        } catch (OrderPersistenceException e) {
            e.printStackTrace();
        }
    }

}
