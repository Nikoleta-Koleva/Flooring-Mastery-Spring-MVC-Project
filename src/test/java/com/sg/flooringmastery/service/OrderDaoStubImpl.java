package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.OrderDao;
import com.sg.flooringmastery.exceptions.OrderPersistenceException;
import com.sg.flooringmastery.model.Order;

import java.math.BigDecimal;
import java.util.*;

public class OrderDaoStubImpl implements OrderDao {

    private Map<Date, List<Order>> orders = new HashMap<>();

    public OrderDaoStubImpl() {
        // Initialize with some default orders
        Date date = new Date();
        Order order1 = new Order(1, "Customer1", "OH", new BigDecimal("6.25"), "Wood", new BigDecimal("100.00"), new BigDecimal("5.15"), new BigDecimal("4.75"), new BigDecimal("515.00"), new BigDecimal("475.00"), new BigDecimal("61.88"), new BigDecimal("1051.88"));
        Order order2 = new Order(2, "Customer2", "PA", new BigDecimal("6.75"), "Tile", new BigDecimal("200.00"), new BigDecimal("3.50"), new BigDecimal("4.15"), new BigDecimal("700.00"), new BigDecimal("830.00"), new BigDecimal("106.05"), new BigDecimal("1636.05"));

        orders.put(date, new ArrayList<>(Arrays.asList(order1, order2)));
    }

    @Override
    public List<Order> getOrdersForDate(Date date) {
        return orders.getOrDefault(date, new ArrayList<>());
    }

    @Override
    public void addOrder(Date date, Order order) throws OrderPersistenceException {
        orders.computeIfAbsent(date, k -> new ArrayList<>()).add(order);
    }

    @Override
    public Order getOrderToRemove(List<Order> orders, int orderNumber) {
        for (Order order : orders) {
            if (order.getOrderNumber() == orderNumber) {
                return order;
            }
        }
        return null;
    }

    @Override
    public List<Order> readOrdersFromFileByDate(Date date) {
        return orders.getOrDefault(date, new ArrayList<>());
    }

    @Override
    public void writeOrdersToFileByDate(Date date, List<Order> orders) {
        this.orders.put(date, new ArrayList<>(orders));
    }

    @Override
    public Order checkIfOrderExists(List<Order> orders, int orderNumber) {
        for (Order order : orders) {
            if (order.getOrderNumber() == orderNumber) {
                return order;
            }
        }
        return null;
    }

    @Override
    public void exportAllOrders() {
        List<Order> allOrders = new ArrayList<>();
        for (List<Order> orderList : orders.values()) {
            allOrders.addAll(orderList);
        }
        allOrders.forEach(order -> System.out.println(orderToTxt(order)));
    }

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
}