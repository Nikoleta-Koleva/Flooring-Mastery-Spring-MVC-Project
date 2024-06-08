package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.exceptions.OrderPersistenceException;
import com.sg.flooringmastery.model.Order;

import java.util.Date;
import java.util.List;

public interface OrderDao {
    List<Order> getOrdersForDate(Date date);

    void addOrder(Date date, Order Order) throws OrderPersistenceException;

    void writeOrdersToFileByDate(Date date, List<Order> orders) throws OrderPersistenceException;

    Order checkIfOrderExists(List<Order> orders, int orderNumber);

    Order getOrderToRemove(List<Order> orders, int orderNumber);

    List<Order> readOrdersFromFileByDate(Date date) throws OrderPersistenceException;

    void exportAllOrders() throws OrderPersistenceException;
}