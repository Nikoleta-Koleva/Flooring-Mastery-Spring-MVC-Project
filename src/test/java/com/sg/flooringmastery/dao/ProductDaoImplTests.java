package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.exceptions.OrderPersistenceException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ProductDaoImplTests {
    private static Path PRODUCTS_FILE;
    private static final String PRODUCTS_CONTENT = "ProductType,CostPerSquareFoot,LaborCostPerSquareFoot\n" +
            "Carpet,2.25,2.10\n" +
            "Laminate,1.75,2.10\n" +
            "Tile,3.50,4.15\n" +
            "Wood,5.15,4.75";

    private ProductDaoImpl productDao;

    @BeforeAll
    static void setUpClass() throws Exception {
        PRODUCTS_FILE = Files.createTempFile("Products", ".txt");
        Files.write(PRODUCTS_FILE, PRODUCTS_CONTENT.getBytes());
    }

    @BeforeEach
    void setUp() {
        productDao = new ProductDaoImpl(PRODUCTS_FILE);
    }

    @AfterAll
    static void deleteClass() throws Exception {
        Files.deleteIfExists(PRODUCTS_FILE);
    }

    @Test
    void testGetProductTypesFromFileSuccess() throws OrderPersistenceException {
        Map<String, ProductDaoImpl> products = productDao.getProductTypesFromFile();

        assertEquals(4, products.size());

        assertTrue(products.containsKey("Carpet"));
        assertEquals(new BigDecimal("2.25"), products.get("Carpet").getCostPerSquareFoot());
        assertEquals(new BigDecimal("2.10"), products.get("Carpet").getLaborCostPerSquareFoot());

        assertTrue(products.containsKey("Laminate"));
        assertEquals(new BigDecimal("1.75"), products.get("Laminate").getCostPerSquareFoot());
        assertEquals(new BigDecimal("2.10"), products.get("Laminate").getLaborCostPerSquareFoot());

        assertTrue(products.containsKey("Tile"));
        assertEquals(new BigDecimal("3.50"), products.get("Tile").getCostPerSquareFoot());
        assertEquals(new BigDecimal("4.15"), products.get("Tile").getLaborCostPerSquareFoot());

        assertTrue(products.containsKey("Wood"));
        assertEquals(new BigDecimal("5.15"), products.get("Wood").getCostPerSquareFoot());
        assertEquals(new BigDecimal("4.75"), products.get("Wood").getLaborCostPerSquareFoot());
    }

    @Test
    void testGetProductTypesFromFileFileNotFound() {
        Path invalidPath = Paths.get("invalid/Products.txt");

        productDao = new ProductDaoImpl(invalidPath);

        OrderPersistenceException exception = assertThrows(OrderPersistenceException.class, productDao::getProductTypesFromFile);
        assertTrue(exception.getMessage().contains("Error: Unable to read products file."));
    }
}
