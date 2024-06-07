package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.OrderPersistenceException;

import java.math.BigDecimal;
import java.util.Map;

public interface ProductDao {
    BigDecimal getCostPerSquareFoot();

    BigDecimal getLaborCostPerSquareFoot();

    Map<String, ProductDaoImpl> getProductTypesFromFile() throws OrderPersistenceException;
}
