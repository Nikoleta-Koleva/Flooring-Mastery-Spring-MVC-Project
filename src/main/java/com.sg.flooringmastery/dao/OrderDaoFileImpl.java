package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.exceptions.OrderPersistenceException;
import com.sg.flooringmastery.model.Order;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class OrderDaoFileImpl implements OrderDao {
    private final String BACKUP_DIRECTORY_PATH;
    private final String ORDERS_DIRECTORY_PATH;
    private static final String DELIMITER = ",";

    public OrderDaoFileImpl() {
        this.BACKUP_DIRECTORY_PATH = "backup";
        this.ORDERS_DIRECTORY_PATH = "files/orders";
    }

    public OrderDaoFileImpl(String ordersDirectoryPath, String backupDirectoryPath) {
        this.BACKUP_DIRECTORY_PATH = backupDirectoryPath;
        this.ORDERS_DIRECTORY_PATH = ordersDirectoryPath;
    }

    //Gives a list of orders by date
    @Override
    public List<Order> getOrdersForDate(Date date) {
        return readOrdersFromFileByDate(date);
    }

    // Adds an order by setting the filename with placeholders(directory, filename and date)
    // Opens the file in append mode and writes order details to the file
    @Override
    public void addOrder(Date date, Order order) throws OrderPersistenceException {
        try {
            String fileName = String.format("%s/Orders_%tm%<td%<tY.txt", ORDERS_DIRECTORY_PATH, date);

            PrintWriter writer = new PrintWriter(new FileWriter(fileName, true));

            writer.println(orderToTxt(order));
            writer.close();
            System.out.println("Order added successfully.");

        } catch (IOException e) {
            throw new OrderPersistenceException("Error writing order to file. ", e);
        }
    }

    @Override
    public Order getOrderToRemove(List<Order> orders, int orderNumber) {
        return checkIfOrderExists(orders, orderNumber);
    }

    public List<Order> readOrdersFromFileByDate(Date date) {
        String fileName = String.format("%s/Orders_%tm%<td%<tY.txt", ORDERS_DIRECTORY_PATH, date);

        try {
            return readOrdersFromFile(Paths.get(fileName));
        } catch (OrderPersistenceException e) {
            System.out.println("There is no file with this name.");
            return Collections.emptyList();
        }
    }

    private List<Order> readOrdersFromFile(Path file) throws OrderPersistenceException {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Order order = parseOrder(line);
                if (order != null) {
                    orders.add(order);
                }
            }
        } catch (IOException e) {
            throw new OrderPersistenceException("Error reading orders from file. ", e);
        }
        return orders;
    }

    // Searches for the order with the specified order number
    public Order checkIfOrderExists(List<Order> orders, int orderNumber) {
        for (Order order : orders) {
            if (order.getOrderNumber() == orderNumber) {
                return order;
            }
        }
        return null;
    }

    // Rewrites the modified list back to the file
    public void writeOrdersToFileByDate(Date date, List<Order> orders) throws OrderPersistenceException {
        String fileName = String.format("%s/Orders_%tm%<td%<tY.txt", ORDERS_DIRECTORY_PATH, date);

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Order order: orders) {
                writer.println(orderToTxt(order));
            }
            System.out.println("Orders written to file successfully.");
        } catch (IOException e) {
            throw new OrderPersistenceException("Error writing orders to file. ", e);
        }
    }

    // Marshall order - convert order object to a line of text
    private String orderToTxt(Order order) {
        return String.format("%d,%s,%s,%.2f,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f",
                order.getOrderNumber(),
                order.getCustomerName(),
                order.getState(),
                order.getTaxRate(),
                order.getProductType(),
                order.getArea(),
                order.getCostPerSquareFoot(),
                order.getLaborCostPerSquareFoot(),
                order.getMaterialCost(),
                order.getLaborCost(),
                order.getTax(),
                order.getTotal());
    }

    //Unmarshall order - reads a line from the file and returns order object
    private Order parseOrder(String line) throws OrderPersistenceException {
        if (line.startsWith("OrderNumber")) {
            return null;
        }

        String[] parts = line.split(DELIMITER);
        try {
            int orderNumber = Integer.parseInt(parts[0]);
            String customerName = parts[1];
            String state = parts[2];
            BigDecimal taxRate = new BigDecimal(parts[3]);
            String productType = parts[4];
            BigDecimal area = new BigDecimal(parts[5]);
            BigDecimal costPerSquareFoot = new BigDecimal(parts[6]);
            BigDecimal laborCostPerSquareFoot = new BigDecimal(parts[7]);
            BigDecimal materialCost = new BigDecimal(parts[8]);
            BigDecimal laborCost = new BigDecimal(parts[9]);
            BigDecimal tax = new BigDecimal(parts[10]);
            BigDecimal total = new BigDecimal(parts[11]);

            return new Order(orderNumber, customerName, state, taxRate, productType, area,
                    costPerSquareFoot, laborCostPerSquareFoot, materialCost,
                    laborCost, tax, total);
        } catch (NumberFormatException e) {
            throw new OrderPersistenceException("Error parsing order. ", e);
        }
    }

    public void exportAllOrders() throws OrderPersistenceException {
        List<Order> allOrders = readAllOrders();
        writeOrdersToFile(allOrders);
    }

    // Gets all order files in the directory and reads orders from each file
    private List<Order> readAllOrders() throws OrderPersistenceException {
        List<Order> allOrders = new ArrayList<>();
        try {
            List<Path> orderFiles = getOrderFiles();

            for (Path file : orderFiles) {
                List<Order> ordersFromFile = readOrdersFromFile(file);
                allOrders.addAll(ordersFromFile);
            }
        } catch (IOException e) {
            throw new OrderPersistenceException("Error reading order files. ", e);
        }
        return allOrders;
    }

    private List<Path> getOrderFiles() throws IOException {
        return Files.walk(Paths.get(ORDERS_DIRECTORY_PATH))
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().startsWith("Orders_"))
                .collect(Collectors.toList());
    }

    //Writes all orders from all files in the Orders directory to a DataExport.txt file.
    private void writeOrdersToFile(List<Order> orders) throws OrderPersistenceException {
        Path directoryPath = Paths.get(BACKUP_DIRECTORY_PATH);

        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new OrderPersistenceException("Error creating directories. ", e);
            }
        }

        Path filePath = directoryPath.resolve("DataExport.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(String.valueOf(filePath)))) {
            // Writes header
            writer.println("OrderNumber,CustomerName,State,TaxRate,ProductType,Area," +
                    "CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total,Date");

            for (Order order : orders) {
                writer.println(orderToTxt(order));
            }
            System.out.println("All orders exported successfully.");
        } catch (IOException e) {
            throw new OrderPersistenceException("Error exporting orders. ", e);
        }
    }

}
