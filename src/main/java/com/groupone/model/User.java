package com.groupone.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a user with attributes like ID, email, password, and owned stocks.
 */
public class User {
    private int id;    
    private String email;    
    private String password;    
    private Boolean isLocked = false;    
    private double availableFunds = 0.0;    
    private List<Stock> ownedStocks = new ArrayList<>();    

    public User() {
        // Default constructor
    }

    public User(int id, String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and Password cannot be null");
        }
        this.id = id;
        this.email = email;
        this.password = password;
    }

    // Other constructors...

    public void addStock(String symbol, double volume, double value) {
        ownedStocks.add(new Stock(symbol, volume, value));
    }

    public void addStock(Stock stock) {
        ownedStocks.add(stock);
    }
    
    public List<Stock> getStocksBySymbol(String symbol){
        List<Stock> symbolStock = new ArrayList<>();
        for (Stock stock : ownedStocks) {
            if (Objects.equals(stock.getSymbol(), symbol)) {
                symbolStock.add(stock);
            }
        }
        return symbolStock;
    }

    // ... existing methods ...

    public List<Stock> getStocks() {
        return new ArrayList<>(ownedStocks);
    }

    public void setStocks(List<Stock> stocks) {
        this.ownedStocks = new ArrayList<>(stocks);
    }

    public Boolean isLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    // ... other getters and setters ...

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", ownedStocks=" + ownedStocks +
                '}';
    }
}
