package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.exceptions.OrderPersistenceException;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TaxDaoImpl implements TaxDao {
    String stateAbbreviation;
    String stateName;
    BigDecimal taxRate;
    private static final String DELIMITER = ",";
    private final String TAXES_DIRECTORY;

    public TaxDaoImpl() {
        TAXES_DIRECTORY = "files/Taxes.txt";
    }

    public TaxDaoImpl(String testFile){
        TAXES_DIRECTORY = testFile;
    }

    // Loads tax rates from the Taxes.txt file and skips the header line
    // Retrieves the state abbreviation and the tax rate
    public Map<String, BigDecimal> loadTaxRatesFromFile() throws OrderPersistenceException {
        Map<String, BigDecimal> taxRates = new HashMap<>();

        try {
            File file = new File(TAXES_DIRECTORY);
            Scanner scanner = new Scanner(file);

            if (!file.exists()) {
                throw new OrderPersistenceException("Error: File not found.");
            }

            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(DELIMITER);

                if (parts.length == 3) {
                    String stateAbbreviation = parts[0].trim().toUpperCase();
                    String stateName = parts[1].trim(); // Unused in this context
                    try {
                        BigDecimal taxRate = new BigDecimal(parts[2].trim());
                        taxRates.put(stateAbbreviation, taxRate);
                    } catch (NumberFormatException e) {
                        throw new OrderPersistenceException("Error: Invalid tax rate format for state " + stateAbbreviation, e);
                    }
                } else {
                    throw new OrderPersistenceException("Error: Invalid line format in taxes file");
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            throw new OrderPersistenceException("Error: Tax file not found. ", e);
        }
        return taxRates;
    }
}
