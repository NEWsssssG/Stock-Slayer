package com.groupone.model;

/**
 * Represents a stock that a user has purchased.
 * Contains information like the unique stock ID (transaction), the owner ID, 
 * the stock symbol, volume, and value of the purchase.
 */
public class Stock {

	private int id;		// Unique ID for every stock transaction
	private int ownerID;	// ID linked to the user who purchased the stock
	private String symbol;	// Symbol of the stock, e.g., "IBM" or "AAPL"
	private double volume;	// Number of shares purchased
	private double value;	// Value of each share purchased

	public Stock(int id, int ownerID, String symbol, double volume, double value) {
		this.id = id;
		this.ownerID = ownerID;
		this.symbol = symbol;
		this.volume = volume;
		this.value = value;
	}

	public Stock(int ownerID, String symbol, double volume, double value) {
		this.ownerID = ownerID;
		this.symbol = symbol;
		this.volume = volume;
		this.value = value;
	}

	public Stock(String symbol, double volume, double value) {
		this.symbol = symbol;
		this.volume = volume;
		this.value = value;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOwnerId() {
		return this.ownerID;
	}

	public void setOwnerId(int ownerID) {
		this.ownerID = ownerID;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public double getVolume() {
		return this.volume;
	}

	public double getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return "Stock{" +
				"stockID=" + id +
				", ownerID=" + ownerID +
				", symbol='" + symbol + '\'' +
				", volume=" + volume +
				", value=" + value +
				'}';
	}
}
