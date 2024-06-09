package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.ProductDaoImpl;
import com.sg.flooringmastery.exceptions.OrderPersistenceException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProductServiceImplTests {
    @Test
    public void testGetProductTypesFromFile() throws OrderPersistenceException {
        ProductDaoStubImpl productDaoStub = new ProductDaoStubImpl();
        Map<String, ProductDaoImpl> products = productDaoStub.getProductTypesFromFile();
        assertNotNull(products);
        assertEquals(3, products.size());
    }
}
