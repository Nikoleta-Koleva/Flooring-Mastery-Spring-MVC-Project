package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.exceptions.OrderPersistenceException;

import java.math.BigDecimal;
import java.util.Map;

public interface ProductDao {
    BigDecimal getCostPerSquareFoot();

    BigDecimal getLaborCostPerSquareFoot();

    Map<String, ProductDaoImpl> getProductTypesFromFile() throws OrderPersistenceException;
}
