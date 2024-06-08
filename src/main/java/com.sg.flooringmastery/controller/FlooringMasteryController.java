package com.sg.flooringmastery.controller;

import com.sg.flooringmastery.exceptions.OrderPersistenceException;
import com.sg.flooringmastery.model.Order;
import com.sg.flooringmastery.service.FlooringMasteryServiceLayerImpl;
import com.sg.flooringmastery.dao.ProductDaoImpl;
import com.sg.flooringmastery.ui.FlooringMasteryView;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FlooringMasteryController {
    private FlooringMasteryView view;
    private FlooringMasteryServiceLayerImpl service;

    public FlooringMasteryController(FlooringMasteryServiceLayerImpl service, FlooringMasteryView view) {
        this.service = service;
        this.view = view;
    }

    public void run() {
        boolean keepGoing = true;

        try {
            while (keepGoing) {
                int choice = getMenuSelection();

                switch (choice) {
                    case 1:
                        getOrdersForDate();
                        break;
                    case 2:
                        addOrder();
                        break;
                    case 3:
                        editOrder();
                        break;
                    case 4:
                        removeOrder();
                        break;
                    case 5:
                        exportAllOrders();
                    case 6:
                        keepGoing = false;
                        break;
                    default:
                        unknownCommand();
                }
            }
            exitMessage();
        } catch (OrderPersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    private int getMenuSelection() {
        return view.displayMenuAndGetChoice();
    }

    private void getOrdersForDate() {
        Date date = view.promptForDate();
        List<Order> orders = service.getOrdersByDate(date);
        view.displayOrderSummaries(orders);
    }

    public void addOrder() throws OrderPersistenceException {
        Date date = view.getUserInputForOrderDate();
        int nextOrderNumber = service.getNextOrderNumber(date);
        String customerName = service.getUserInputForCustomerName();

        Map<String, BigDecimal> taxRates = service.loadTaxRatesFromFile();
        String state = service.getUserInputForState(taxRates);

        Map<String, ProductDaoImpl> productTypes = service.getProductTypesFromFile();
        String productType = service.getUserInputForProductType(productTypes);

        BigDecimal area = getUserInputForArea();
        Order order = service.calculateOrderDetails(nextOrderNumber, customerName, state, productType, area);

        view.displayOrderSummary(order);

        confirmOrderPlacement(order, date);
    }

    public BigDecimal getUserInputForArea() {
        BigDecimal area;
        BigDecimal minimumOrderSize = new BigDecimal("100");
        while (true) {
            String areaString = view.enterArea();
            area = new BigDecimal(areaString);
            if (area.compareTo(minimumOrderSize) >= 0) {
                break;
            }
            view.promptNumBiggerThan100ft();
        }
        return area;
    }

    private void editOrder() throws OrderPersistenceException {
        while (true) {
            Date date = view.promptForDate();
            List<Order> orders = service.readOrdersFromFileByDate(date);
            int orderNum = view.promptForOrderNum();
            Order orderToEdit = service.checkIfOrderExists(orders, orderNum);

            if (orderToEdit == null) {
                view.orderNumNotFound();
                break;
            }

            view.displayOrderSummary(orderToEdit);
            service.updateOrderDetails(orderToEdit);
            service.calculateUpdatedOrderDetails(orderToEdit);
            confirmChanges(orderToEdit, date, orders);
            break;
        }
    }

    private void removeOrder() throws OrderPersistenceException {
        Date date = view.promptForDate();
        int orderNum = view.promptForOrderNum();

        List<Order> orders = service.readOrdersFromFileByDate(date);
        Order orderToRemove = service.getOrderToRemove(orders, orderNum);

        if(orderToRemove != null) {
            view.displayOrderSummary(orderToRemove);
        }

        if (orderToRemove != null) {
            String confirmation = view.userSureToRemove().trim().toUpperCase();

            if (confirmation.equals("Y")) {
                if (orders.remove(orderToRemove)) {
                    service.writeOrdersToFileByDate(date, orders);
                    view.orderRemovedSuccess();
                } else {
                    view.orderFailedToRemove();
                }
            } else {
                view.orderRemovalCancelled();
            }
        } else {
            view.orderNotFound();
        }

    }

    private void exportAllOrders() throws OrderPersistenceException {
        service.exportAllOrders();
    }

    private void unknownCommand() {
        view.displayUnknownCommandBanner();
    }

    private void exitMessage() {
        view.displayExitBanner();
    }

    private void confirmOrderPlacement(Order order, Date date) throws OrderPersistenceException {
        while (true) {
            String confirmation = view.placeOrderYN();
            if (confirmation.equals("Y")) {
                service.addOrder(date, order);
                break;
            } else if (confirmation.equals("N")) {
                view.createOrderCancelled();
                break;
            } else {
                view.invalidInputYN();
            }
        }
    }

    private void confirmChanges(Order orderToEdit, Date date, List<Order> orders) throws OrderPersistenceException {
        view.displayOrderSummary(orderToEdit);

        if (view.confirmChanges()) {
            service.writeOrdersToFileByDate(date, orders);
            view.updatedSuccessfullyBanner();
        } else {
            view.updateCancelledBanner();
        }
    }
}