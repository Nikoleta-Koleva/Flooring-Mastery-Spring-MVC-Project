package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Order;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class OrderDaoFileImplTest {
    private static Path ORDERS_DIRECTORY;
    private static Path BACKUP_DIRECTORY;
    private static final String FILE_PATH = "src/main/java/com.sg.flooringmastery/orders/Orders_05012027.txt";

    //Create a mock instance of the OrderDaoFileImpl class and assign it to the orderDao variable
    //Does not have the actual implementation of the class methods but can be configured to simulate behavior for testing purposes
    @Mock
    private OrderDaoFileImpl orderDao;

    @BeforeAll
    static void setUpClass() throws Exception {
        ORDERS_DIRECTORY = Files.createTempDirectory("test_orders");
        BACKUP_DIRECTORY = Files.createTempDirectory("test_backup");
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterAll
    static void deleteClass() throws Exception {
        Files.walk(ORDERS_DIRECTORY).map(Path::toFile).forEach(java.io.File::delete);
        Files.walk(BACKUP_DIRECTORY).map(Path::toFile).forEach(java.io.File::delete);
    }

    // Helper method to clean the orders directory for a specified date
    private void cleanOrdersDirectory(Date date) {
        // Construct the file path for the orders directory
        Path ordersPath = Paths.get(ORDERS_DIRECTORY.toString());

        // Construct the file name for the specified date
        String fileName = String.format("Orders_%tm%<td%<tY.txt", date);

        // Construct the full file path
        Path filePath = ordersPath.resolve(fileName);

        // Delete the file if it exists
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetOrdersForDate_NoOrders() {
        // Create a date for which no orders exist
        Date date = new Date();

        // Ensure that the orders directory is empty for the specified date
        cleanOrdersDirectory(date);

        // Retrieve orders for the specified date
        List<Order> orders = orderDao.getOrdersForDate(date);

        // Verify that no orders are returned
        assertTrue(orders.isEmpty());
    }

    @Test
    void testAddOrder_Success() throws Exception {
        Date date = new Date();
        Order order = new Order(1, "John Doe", "CA", new BigDecimal("25.00"), "Tile", new BigDecimal("249.00"),
                new BigDecimal("3.50"), new BigDecimal("4.15"), new BigDecimal("871.50"),
                new BigDecimal("1033.35"), new BigDecimal("476.21"), new BigDecimal("2381.06"));

        orderDao.addOrder(date, order);

        Path filePath = Paths.get(FILE_PATH);
        assertTrue(Files.exists(filePath));
    }

    @Test
    void testAddOrder_OrderPersistenceException() throws OrderPersistenceException {
        // Create a Date object representing the current date and time
        Date date = new Date();

        // Create an Order object with sample data
        Order order = new Order(1, "John Doe", "CA", new BigDecimal("25.00"), "Tile", new BigDecimal("249.00"),
                new BigDecimal("3.50"), new BigDecimal("4.15"), new BigDecimal("871.50"),
                new BigDecimal("1033.35"), new BigDecimal("476.21"), new BigDecimal("2381.06"));

        // Mock the behavior of the addOrder method of the orderDao mock object
        // to throw an OrderPersistenceException when invoked with the specified parameters
        doThrow(OrderPersistenceException.class).when(orderDao).addOrder(date, order);

        // Assert that invoking the addOrder method with the specified parameters
        // throws an OrderPersistenceException
        assertThrows(OrderPersistenceException.class, () -> orderDao.addOrder(date, order));
    }

    @Test
    void testCheckIfOrderExists_OrderExists() {
        // Create an order with order number 1
        Order order = new Order(1, "John Doe", "CA", new BigDecimal("25.00"), "Tile", new BigDecimal("249.00"),
                new BigDecimal("3.50"), new BigDecimal("4.15"), new BigDecimal("871.50"),
                new BigDecimal("1033.35"), new BigDecimal("476.21"), new BigDecimal("2381.06"));

        // Create a list containing the above order
        List<Order> orders = Collections.singletonList(order);

        // Mock the behavior of orderDao.checkIfOrderExists to return the order with order number 1
        when(orderDao.checkIfOrderExists(orders, 1)).thenReturn(order);

        // Call the method under test
        Order result = orderDao.checkIfOrderExists(orders, 1);

        // Verify that the returned order is not null
        assertNotNull(result);

        // Verify that the order number of the returned order matches the expected order number
        assertEquals(1, result.getOrderNumber());
    }

    @Test
    void testCheckIfOrderExists_OrderDoesNotExist() {
        List<Order> orders = Collections.emptyList();

        Order result = orderDao.checkIfOrderExists(orders, 1);
        assertNull(result);
    }

    @Test
    void testWriteOrdersToFileByDate_Success() throws Exception {
        Date date = new Date();
        Order order = new Order(1, "John Doe", "CA", new BigDecimal("25.00"), "Tile", new BigDecimal("249.00"),
                new BigDecimal("3.50"), new BigDecimal("4.15"), new BigDecimal("871.50"),
                new BigDecimal("1033.35"), new BigDecimal("476.21"), new BigDecimal("2381.06"));
        List<Order> orders = Collections.singletonList(order);

        orderDao.writeOrdersToFileByDate(date, orders);

        Path filePath = Paths.get("src/main/java/com.sg.flooringmastery/orders/Orders_05012027.txt");
        assertTrue(Files.exists(filePath));
    }

    @Test
    void testWriteOrdersToFileByDate_OrderPersistenceException() throws OrderPersistenceException {
        Date date = new Date();
        Order order = new Order(1, "John Doe", "CA", new BigDecimal("25.00"), "Tile", new BigDecimal("249.00"),
                new BigDecimal("3.50"), new BigDecimal("4.15"), new BigDecimal("871.50"),
                new BigDecimal("1033.35"), new BigDecimal("476.21"), new BigDecimal("2381.06"));
        List<Order> orders = Collections.singletonList(order);

        // Stub the method of the mock object
        doThrow(OrderPersistenceException.class).when(orderDao).writeOrdersToFileByDate(date, orders);

        // Invoke the method and assert the exception
        assertThrows(OrderPersistenceException.class, () -> orderDao.writeOrdersToFileByDate(date, orders));
    }
}