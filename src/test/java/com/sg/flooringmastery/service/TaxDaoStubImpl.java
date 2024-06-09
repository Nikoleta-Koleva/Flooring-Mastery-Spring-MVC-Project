package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.TaxDao;
import com.sg.flooringmastery.exceptions.OrderPersistenceException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TaxDaoStubImpl implements TaxDao {
    private Map<String, BigDecimal> taxRates = new HashMap<>();

    public TaxDaoStubImpl() {
        // Initialize with some default tax rates
        taxRates.put("OH", new BigDecimal("6.25"));
        taxRates.put("PA", new BigDecimal("6.75"));
        taxRates.put("MI", new BigDecimal("5.75"));
    }

    @Override
    public Map<String, BigDecimal> loadTaxRatesFromFile() throws OrderPersistenceException {
        return taxRates;
    }
}
