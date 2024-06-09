package com.sg.flooringmastery.service;

import com.sg.flooringmastery.exceptions.OrderPersistenceException;
import com.sg.flooringmastery.model.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderServiceImplTests {
    @Test
    public void testGetOrdersForDate() {
        OrderDaoStubImpl orderDaoStub = new OrderDaoStubImpl();
        List<Order> orders = orderDaoStub.getOrdersForDate(new Date());
        assertNotNull(orders);
        assertEquals(2, orders.size());
    }

    @Test
    public void testAddOrder() throws OrderPersistenceException {
        OrderDaoStubImpl orderDaoStub = new OrderDaoStubImpl();
        Date date = new Date();
        Order order = new Order(3, "Customer3", "NY", new BigDecimal("7.25"), "Carpet", new BigDecimal("150.00"), new BigDecimal("2.25"), new BigDecimal("2.10"), new BigDecimal("337.50"), new BigDecimal("315.00"), new BigDecimal("24.38"), new BigDecimal("676.88"));
        orderDaoStub.addOrder(date, order);
        List<Order> orders = orderDaoStub.getOrdersForDate(date);
        assertEquals(3, orders.size());
    }
}
