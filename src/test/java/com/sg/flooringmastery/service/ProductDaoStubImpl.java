package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.ProductDao;
import com.sg.flooringmastery.dao.ProductDaoImpl;
import com.sg.flooringmastery.exceptions.OrderPersistenceException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ProductDaoStubImpl implements ProductDao {

    private Map<String, ProductDaoImpl> products = new HashMap<>();

    public ProductDaoStubImpl() {
        // Initialize with some default products
        ProductDaoImpl product1 = new ProductDaoImpl(new BigDecimal("2.25"), new BigDecimal("2.10"));
        ProductDaoImpl product2 = new ProductDaoImpl(new BigDecimal("1.75"), new BigDecimal("2.10"));
        ProductDaoImpl product3 = new ProductDaoImpl(new BigDecimal("3.50"), new BigDecimal("4.15"));

        products.put("Carpet", product1);
        products.put("Laminate", product2);
        products.put("Tile", product3);
    }

    @Override
    public BigDecimal getCostPerSquareFoot() {
        return null;
    }

    @Override
    public BigDecimal getLaborCostPerSquareFoot() {
        return null;
    }

    @Override
    public Map<String, ProductDaoImpl> getProductTypesFromFile() throws OrderPersistenceException {
        return products;
    }
}
