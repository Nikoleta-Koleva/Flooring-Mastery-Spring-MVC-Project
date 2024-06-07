package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.OrderPersistenceException;
import com.sg.flooringmastery.model.Order;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FlooringMasteryServiceLayer {
    List<Order> getOrdersByDate(Date date);
    int getNextOrderNumber(Date date) throws OrderPersistenceException;
    Order calculateOrderDetails(int nextOrderNumber, String customerName, String state, String productType,
                                BigDecimal area) throws OrderPersistenceException;
    void updateOrderDetails(Order orderToEdit) throws OrderPersistenceException;
    void calculateUpdatedOrderDetails(Order order);
    Map<String, ProductDaoImpl> getProductTypesFromFile() throws OrderPersistenceException;
    void writeOrdersToFileByDate(Date date, List<Order> orders) throws OrderPersistenceException;
    Order checkIfOrderExists(List<Order> orders, int orderNumber);
    void exportAllOrders() throws IOException, OrderPersistenceException;
}
