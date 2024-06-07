package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.OrderPersistenceException;

import java.math.BigDecimal;
import java.util.Map;

public interface TaxDao {
    Map<String, BigDecimal> loadTaxRatesFromFile() throws OrderPersistenceException;
}
