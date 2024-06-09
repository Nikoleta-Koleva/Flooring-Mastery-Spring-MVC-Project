package com.sg.flooringmastery.service;

import com.sg.flooringmastery.exceptions.OrderPersistenceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaxServiceImplTests {
    @Test
    public void testLoadTaxRatesFromFile() throws OrderPersistenceException {
        TaxDaoStubImpl taxDaoStub = new TaxDaoStubImpl();
        Map<String, BigDecimal> taxRates = taxDaoStub.loadTaxRatesFromFile();
        Assertions.assertNotNull(taxRates);
        assertEquals(3, taxRates.size());
    }
}
