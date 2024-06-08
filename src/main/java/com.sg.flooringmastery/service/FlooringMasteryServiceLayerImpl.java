package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.*;
import com.sg.flooringmastery.exceptions.OrderPersistenceException;
import com.sg.flooringmastery.model.Order;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FlooringMasteryServiceLayerImpl implements FlooringMasteryServiceLayer {
    private OrderDao orderDao;
    private TaxDao taxDao;
    private ProductDao productDao;
    private Scanner userInput = new Scanner(System.in);

    public FlooringMasteryServiceLayerImpl(OrderDao dao, TaxDao taxDao, ProductDao productDao) {
        this.orderDao = dao;
        this.taxDao = taxDao;
        this.productDao = productDao;
    }

    public List<Order> getOrdersByDate(Date date) {
        List<Order> orders = orderDao.getOrdersForDate(date);
        return orders;
    }

    public List<Order> readOrdersFromFileByDate(Date date) throws OrderPersistenceException {
        List<Order> orders = orderDao.readOrdersFromFileByDate(date);
        return orders;
    }

    public void addOrder(Date date, Order order) throws OrderPersistenceException {
        orderDao.addOrder(date, order);
    }

    // Determines the maximum order number
    public int getNextOrderNumber(Date date) throws OrderPersistenceException {
        List<Order> orders = readOrdersFromFileByDate(date);

        int maxOrderNumber = orders.stream()
                .mapToInt(Order::getOrderNumber)
                .max()
                .orElse(0);
        // Increments the maximum order number to get the next order number
        return maxOrderNumber + 1;
    }

    public String getUserInputForCustomerName() {
        String customerName;
        while (true) {
            System.out.println("Please enter customer name:");
            customerName = userInput.nextLine();
            if (isValidCustomerName(customerName)) {
                break;
            }
            System.out.println("Invalid customer name format.");
        }
        return customerName;
    }

    public String getUserInputForState(Map<String, BigDecimal> taxRates) throws OrderPersistenceException {
        String state;
        while (true) {
            displayStateAndTaxRate();
            System.out.println("Enter state abbreviation");
            state = userInput.nextLine().trim().toUpperCase();
            if (isValidState(taxRates, state)) {
                break;
            }
            System.out.println("Please enter a state from the list:");
        }
        return state;
    }

    public void displayStateAndTaxRate() throws OrderPersistenceException {
        Map<String, BigDecimal> taxRates = loadTaxRatesFromFile();
        taxRates.forEach((stateAbbreviation, taxRate) ->
                System.out.println(stateAbbreviation + ": " + taxRate));
    }

    public String getUserInputForProductType(Map<String, ProductDaoImpl> productTypes) {
        String productType;
        while (true) {
            displayProductTypesWithCost(productTypes);
            System.out.println("Enter product type:");
            productType = userInput.nextLine();
            if (isValidProductType(productType)) {
                break;
            }
            System.out.println("Please enter a product type from the list:");
        }
        return productType;
    }

    public void displayProductTypesWithCost(Map<String, ProductDaoImpl> productTypes) {
        if (productTypes != null) {
            for (Map.Entry<String, ProductDaoImpl> entry : productTypes.entrySet()) {
                System.out.println("Product Type: " + entry.getKey());
                System.out.println("Cost Per Square Foot: " + entry.getValue().getCostPerSquareFoot());
                System.out.println("Labor Cost Per Square Foot: " + entry.getValue().getLaborCostPerSquareFoot() + "\n");
            }
        }
    }

    public Order calculateOrderDetails(int nextOrderNumber, String customerName, String state, String productType, BigDecimal area) throws OrderPersistenceException {
        Map<String, BigDecimal> taxRates = taxDao.loadTaxRatesFromFile();
        BigDecimal taxRate = taxRates.get(state.toUpperCase());

        Map<String, ProductDaoImpl> productTypes = productDao.getProductTypesFromFile();
        ProductDaoImpl selectedProduct = productTypes.get(productType);
        BigDecimal costPerSquareFoot = selectedProduct.getCostPerSquareFoot();
        BigDecimal laborCostPerSquareFoot = selectedProduct.getLaborCostPerSquareFoot();

        BigDecimal materialCost = area.multiply(costPerSquareFoot);
        BigDecimal laborCost = area.multiply(laborCostPerSquareFoot);
        BigDecimal tax = (materialCost.add(laborCost)).multiply(taxRate.divide(new BigDecimal("100")));

        materialCost = materialCost.setScale(2, RoundingMode.HALF_UP);
        laborCost = laborCost.setScale(2, RoundingMode.HALF_UP);
        tax = tax.setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = materialCost.add(laborCost).add(tax);
        total = total.setScale(2, RoundingMode.HALF_UP);

        return new Order(nextOrderNumber, customerName, state, taxRate, productType, area, costPerSquareFoot, laborCostPerSquareFoot, materialCost, laborCost, tax, total);
    }

    @Override
    public void updateOrderDetails(Order orderToEdit) throws OrderPersistenceException {
        Scanner userInput = new Scanner(System.in);
        // Update customer name
        System.out.println("Enter new customer name (hit Enter to keep existing): ");
        String newCustomerName = userInput.nextLine();
        if (!newCustomerName.isEmpty()) {
            while (!isValidCustomerName(newCustomerName)) {
                System.out.println("Invalid customer name format.\n");
                System.out.println("Enter new customer name:");
                newCustomerName = userInput.nextLine();
            }
            orderToEdit.setCustomerName(newCustomerName);
        }

        // Update state
        System.out.println("Enter new state abbreviation (hit Enter to keep existing");
        String newState = userInput.nextLine();
        if (!newState.isEmpty()) {
            while (!isValidState(taxDao.loadTaxRatesFromFile(), newState)) {
                System.out.println("Invalid state.\n");
                System.out.println("Enter new state abbreviation: ");
                newState = userInput.nextLine();
            }
            orderToEdit.setState(newState);

            // Update tax rate
            orderToEdit.setTaxRate(taxDao.loadTaxRatesFromFile().get(newState.toUpperCase()));
        }

        // Update product type
        System.out.println("Enter new product type (hit Enter to keep existing): ");
        String newProductType = userInput.nextLine();
        if (!newProductType.isEmpty()) {
            while (!isValidProductType(newProductType)) {
                System.out.println("Invalid product type.\n");
                System.out.println("Enter new product type: ");
                newProductType = userInput.nextLine();
            }
            ProductDaoImpl productDaoNewProductType = productDao.getProductTypesFromFile().get(newProductType);
            orderToEdit.setProductType(newProductType);

            // Update cost per square foot
            orderToEdit.setCostPerSquareFoot(productDaoNewProductType.getCostPerSquareFoot());
            // Update labor cost per square foot
            orderToEdit.setLaborCostPerSquareFoot(productDaoNewProductType.getLaborCostPerSquareFoot());
        }

        // Update area
        System.out.println("Please enter new area (hit Enter to keep existing): ");
        String areaString = userInput.nextLine();
        if (!areaString.isEmpty()) {
            BigDecimal newArea = new BigDecimal(areaString);
            BigDecimal minimumOrderSize = new BigDecimal("100");
            while (newArea.compareTo(minimumOrderSize) < 0) {
                System.out.println("Enter new area bigger than 100 sq ft. ");
                areaString = userInput.nextLine();
                newArea = new BigDecimal(areaString);
            }
            orderToEdit.setArea(newArea);
        }
    }

    private boolean isValidCustomerName(String customerName) {
        String regex = "^[a-zA-Z0-9.,\\s]+$";
        return !customerName.isBlank() && customerName.matches(regex);
    }

    public boolean isValidState(Map<String, BigDecimal> taxRates, String state) {
        return taxRates.containsKey(state.toUpperCase());
    }

    public boolean isValidProductType(String productType) {
        try {
            List<String> productTypes = Files.readAllLines(Paths.get("files/Products.txt"))
                    .stream()
                    .map(line -> line.split(",")[0].trim())
                    .collect(Collectors.toList());
            return productTypes.contains(productType);
        } catch (IOException e) {
            System.out.println("Error: Unable to read products file.");
            return false;
        }
    }

    public void calculateUpdatedOrderDetails(Order order) {
        BigDecimal materialCost = order.getArea().multiply(order.getCostPerSquareFoot());
        BigDecimal laborCost = order.getArea().multiply(order.getLaborCostPerSquareFoot());
        BigDecimal tax = (materialCost.add(laborCost)).multiply(order.getTaxRate().divide(new BigDecimal("100")));
        BigDecimal total = materialCost.add(laborCost).add(tax);

        order.setMaterialCost(materialCost.setScale(2, RoundingMode.HALF_UP));
        order.setLaborCost(laborCost.setScale(2, RoundingMode.HALF_UP));
        order.setTax(tax.setScale(2, RoundingMode.HALF_UP));
        order.setTotal(total.setScale(2, RoundingMode.HALF_UP));
    }

    public Map<String, ProductDaoImpl> getProductTypesFromFile() throws OrderPersistenceException {
        return productDao.getProductTypesFromFile();
    }

    public Map<String, BigDecimal> loadTaxRatesFromFile() throws OrderPersistenceException {
        return taxDao.loadTaxRatesFromFile();
    }

    public void writeOrdersToFileByDate(Date date, List<Order> orders) throws OrderPersistenceException {
        orderDao.writeOrdersToFileByDate(date, orders);
    }

    public Order checkIfOrderExists(List<Order> orders, int orderNumber) {
        return orderDao.checkIfOrderExists(orders, orderNumber);
    }

    public Order getOrderToRemove(List<Order> orders, int orderNum) {
        return orderDao.getOrderToRemove(orders, orderNum);
    }

    public void exportAllOrders() throws OrderPersistenceException {
        orderDao.exportAllOrders();
    }

}
