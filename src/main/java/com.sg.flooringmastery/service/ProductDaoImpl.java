package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.OrderPersistenceException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductDaoImpl implements ProductDao {
    private String productType;
    private BigDecimal costPerSquareFoot;
    private BigDecimal laborCostPerSquareFoot;
    private final String PRODUCTS_DIRECTORY_PATH = "src/main/java/com.sg.flooringmastery/Products.txt";

    public ProductDaoImpl() {

    }

    public ProductDaoImpl(BigDecimal costPerSquareFoot, BigDecimal laborCostPerSquareFoot) {
        this.costPerSquareFoot = costPerSquareFoot;
        this.laborCostPerSquareFoot = laborCostPerSquareFoot;
    }

    public BigDecimal getCostPerSquareFoot() {
        return costPerSquareFoot;
    }

    public BigDecimal getLaborCostPerSquareFoot() {
        return laborCostPerSquareFoot;
    }

    // Gets product types from the Products.txt file, skips the first line
    // .Stream() method retrieves Product type, Cost per square foot and Labor cost per square foot
    public Map<String, ProductDaoImpl> getProductTypesFromFile() throws OrderPersistenceException {
        try {
            List<String> lines = Files.readAllLines(Paths.get(PRODUCTS_DIRECTORY_PATH));

            lines = lines.stream().skip(1).collect(Collectors.toList());

            return lines.stream()
                    .map(line -> line.split(","))
                    .filter(parts -> parts.length >= 3)
                    .collect(Collectors.toMap(
                            parts -> parts[0].trim(),
                            parts -> new ProductDaoImpl(
                                    new BigDecimal(parts[1].trim()),
                                    new BigDecimal(parts[2].trim())
                            )
                    ));
        } catch (IOException e) {
            throw new OrderPersistenceException("Error: Unable to read products file. ", e);
        }
    }
}
