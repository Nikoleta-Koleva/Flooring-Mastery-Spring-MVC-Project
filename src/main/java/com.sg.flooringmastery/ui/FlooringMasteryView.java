package com.sg.flooringmastery.ui;

import com.sg.flooringmastery.model.Order;

import java.util.Date;
import java.util.List;

public class FlooringMasteryView {
    private UserIO io;
    public FlooringMasteryView(UserIO io) {
        this.io = io;
    }

    public int displayMenuAndGetChoice() {
        io.print("Flooring Program");
        io.print("1. Display Orders");
        io.print("2. Add an Order");
        io.print("3. Edit an Order");
        io.print("4. Remove an Order");
        io.print("5. Export all Orders");
        io.print("6. Quit");

        return io.readInt("Please select an option:", 1, 6);
    }

    public Date promptForDate() {
        return io.readDate("Please enter order date:");
    }

    public int promptForOrderNum() {
        return io.readInt("Please enter order number:");
    }

    public Date getUserInputForOrderDate() {
        Date date;
        while (true) {
            date = io.readDate("Please enter order date:");
            if (isValidDate(date)) {
                break;
            }
            io.print("Invalid order date. Please enter a future date.");
        }
        return date;
    }

    private boolean isValidDate(Date date) {
        Date currentDate = new Date();

        return date.after(currentDate);
    }

    public void displayOrderSummaries(List<Order> orders) {
        for (Order order : orders) {
            displayOrderSummary(order);
        }
    }

    public void displayOrderSummary(Order order) {
        io.print("\nOrder Summary:");
        io.print("Order number: " + order.getOrderNumber());
        io.print("Customer Name: " + order.getCustomerName());
        io.print("State: " + order.getState());
        io.print("Tax rate: " + order.getTaxRate());
        io.print("Product Type: " + order.getProductType());
        io.print("Area: " + order.getArea());
        io.print("Cost per square foot: " + order.getCostPerSquareFoot());
        io.print("Labor cost per square foot: " + order.getLaborCostPerSquareFoot());
        io.print("Material Cost: " + order.getMaterialCost());
        io.print("Labor Cost: " + order.getLaborCost());
        io.print("Tax: " + order.getTax());
        io.print("Total: " + order.getTotal() + "\n");
    }

    public void displayExitBanner() {
        io.print("Exiting Order System");
    }

    public void displayUnknownCommandBanner() {
        io.print("Please enter a valid command");
    }

    public void createOrderCancelled() {
        io.print("Order creation cancelled.");
    }

    public String placeOrderYN() {
        return io.readString("Place the order? (Y/N): ").trim().toUpperCase();
    }
    public void invalidInputYN() {
        io.print("Invalid input. Please enter Y or N.");
    }

    public void orderNumNotFound() {
        io.print("Order number not found.\n");
    }

    public boolean confirmChanges() {
        String confirmation = io.readString("\nDo you want to save changes? (Y/N): ").trim().toUpperCase();
        return confirmation.equals("Y");
    }

    public void updatedSuccessfullyBanner() {
        io.print("Order updated successfully.\n");
    }

    public void updateCancelledBanner() {
        io.print("Order update cancelled.\n");
    }

    public String enterNewStateWithEnter() {
        return io.readString("Enter new state abbreviation (hit Enter to keep existing): ");
    }

    public void invalidState() {
        io.print("Invalid state abbreviation. List of states:\n");
    }

    public String enterNewStateName() {
        return io.readString("Enter new state name: ");
    }

    public String enterNewProductTypeWithEnter() {
        return io.readString("Enter new product type (hit Enter to keep existing): ");
    }

    public void invalidProductType() {
        io.print("Invalid product type. List of products and cost:\n");
    }

    public String enterNewProductType() {
        return io.readString("Enter new product type: ");
    }

    public String userSureToRemove() {
        return io.readString("Are you sure you want to remove this order? (Y/N): ");
    }

    public void orderRemovedSuccess() {
        io.print("Order removed successfully.");
    }

    public void orderFailedToRemove() {
        io.print("Error: Failed to remove order.");
    }

    public void orderRemovalCancelled() {
        io.print("Order removal cancelled.");
    }

    public void orderNotFound() {
        io.print("Order not found.");
    }

    public void promptNumBiggerThan100ft() {
        io.print("Please enter a number bigger than 100 sq ft.");
    }

    public String enterArea() {
        return io.readString("Please enter the area: ");
    }
}
