package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.exceptions.OrderPersistenceException;

import java.math.BigDecimal;
import java.util.Map;

public interface TaxDao {
    Map<String, BigDecimal> loadTaxRatesFromFile() throws OrderPersistenceException;
}
