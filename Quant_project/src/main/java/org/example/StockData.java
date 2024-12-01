package org.example;

public class StockData {
    private String date;
    private double open;
    private double high;
    private double low;
    private double close;

    public StockData(String date, double open, double high, double low, double close) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }

    @Override
    public String toString() {
        return "Date: " + date + ", Open: " + open + ", High: " + high + ", Low: " + low + ", Close: " + close;
    }

    // Getters (optional, if needed later for analysis)
    public String getDate() { return date; }
    public double getOpen() { return open; }
    public double getHigh() { return high; }
    public double getLow() { return low; }
    public double getClose() { return close; }
}
