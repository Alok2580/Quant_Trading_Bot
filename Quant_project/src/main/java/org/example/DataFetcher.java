package org.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;




public class DataFetcher {


    private static final String API_KEY = "5T7UFV7SLMBKMV74";
    private static final String BASE_URL = "https://www.alphavantage.co/query";
//    private static final String BASE_URL = "https://query1.finance.yahoo.com/v7/finance/chart/";


    public List<StockData> fetchStockData(String symbol) {
        List<StockData> stockDataList = new ArrayList<>();
        try {

            String endpoint = BASE_URL + "?function=TIME_SERIES_DAILY&symbol=" + symbol + "&apikey=" + API_KEY;


            HttpClient client = HttpClient.newHttpClient();


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .build();


            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


//            System.out.println(response.body());
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject timeSeries = jsonObject.getAsJsonObject("Time Series (Daily)");


            if (timeSeries != null) {

                for (String date : timeSeries.keySet()) {
                    JsonObject dailyData = timeSeries.getAsJsonObject(date);
                    double open = dailyData.get("1. open").getAsDouble();
                    double high = dailyData.get("2. high").getAsDouble();
                    double low = dailyData.get("3. low").getAsDouble();
                    double close = dailyData.get("4. close").getAsDouble();

                    stockDataList.add(new StockData(date, open, high, low, close));
                }
            } else {

                System.out.println("No data found for the symbol: " + symbol);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }



        return stockDataList;
    }



    public List<StockData> fetchStockDataYahoo(String symbol) {
        List<StockData> stockDataList = new ArrayList<>();
        try {

            String endpoint = BASE_URL + symbol + "?interval=1d&range=1mo";


            HttpClient client = HttpClient.newHttpClient();


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .build();


            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            System.out.println(response.body());
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject chart = jsonObject.getAsJsonObject("chart");
            JsonArray result = chart.getAsJsonArray("result");

            if (result != null && result.size() > 0) {
                JsonObject firstResult = result.get(0).getAsJsonObject();
                JsonObject indicators = firstResult.getAsJsonObject("indicators");
                JsonObject quote = indicators.getAsJsonArray("quote").get(0).getAsJsonObject();

                JsonArray timestamps = firstResult.getAsJsonArray("timestamp");
                JsonArray opens = quote.getAsJsonArray("open");
                JsonArray highs = quote.getAsJsonArray("high");
                JsonArray lows = quote.getAsJsonArray("low");
                JsonArray closes = quote.getAsJsonArray("close");

                for (int i = 0; i < timestamps.size(); i++) {
                    long timestamp = timestamps.get(i).getAsLong();
                    double open = opens.get(i).getAsDouble();
                    double high = highs.get(i).getAsDouble();
                    double low = lows.get(i).getAsDouble();
                    double close = closes.get(i).getAsDouble();

                    String date = new java.text.SimpleDateFormat("yyyy-MM-dd")
                            .format(new java.util.Date(timestamp * 1000));
                    stockDataList.add(new StockData(date, open, high, low, close));
                }
            } else {
                System.out.println("No data found for the symbol: " + symbol);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stockDataList;
    }



//    public List<Double> calculateSMA(List<StockData> stockDataList, int period) {
//        List<Double> smaValues = new ArrayList<>();
//
//        for (int i = 0; i <= stockDataList.size() - period; i++) {
//            double sum = 0.0;
//            for (int j = 0; j < period; j++) {
//                sum += stockDataList.get(i + j).getClose();
//            }
//            double sma = sum / period;
//            smaValues.add(sma);
//        }
//        return smaValues;
//    }



    public List<Double> calculateSMA(List<StockData> stockDataList, int period) {
        List<Double> smaValues = new ArrayList<>();

        int i=0,j=0;
        int n=stockDataList.size();
        double sum=0;

        
        while(j<n){

            sum+=stockDataList.get(j).getClose();

            if(j-i+1==period) {
                smaValues.add((sum) / period);
                sum-=stockDataList.get(i).getClose();
                i++;
            }
            j++;

        }
        return smaValues;

    }


    public List<String> generateSignals(List<StockData> stockDataList,int period) {
        List<Double> smaValues = calculateSMA(stockDataList, period);
        List<String> signals = new ArrayList<>();
        boolean isHolding = false;
        double margin = 0.01;
        for (int i = period - 1; i < stockDataList.size(); i++) {
            double currentPrice = stockDataList.get(i).getClose();
            double previousPrice = stockDataList.get(i - 1).getClose();
            double currentSMA = smaValues.get(i - period + 1);
            if(previousPrice<currentSMA-margin && currentPrice > currentSMA + margin && !isHolding) {
                signals.add("Buy on day " + (i + 1) + " at price " + currentPrice);
//                System.out.println("buy on day " + (i + 1) + " at price " + currentPrice);
                isHolding = true;
            }
            else if(previousPrice>currentSMA+margin && currentPrice < currentSMA - margin && isHolding) {
                signals.add("Sell on day " + (i + 1) + " at price " + currentPrice);
                isHolding = false;

            }

            else {
                signals.add("Hold on day " + (i + 1));

            }

        }
        return  signals;
    }


    public static void main(String[] args) {
        DataFetcher fetcher = new DataFetcher();

//        fetcher.fetchStockData("TSLA");
        List<StockData> stockDataList = fetcher.fetchStockData("AAPL");
//        var ans=fetcher.calculateSMA(stockDataList,3);
//        for(var d:stockDataList){
//            System.out.println(d.getClose());
//        }
//        for(double a:ans) System.out.println(a);



        System.out.println("Fetched Stock Data: ");
        for (StockData data : stockDataList) {
            System.out.println(data);
        }


        List<Double> smaValues = fetcher.calculateSMA(stockDataList, 5);
        System.out.println("SMA Values: ");
        for (double sma : smaValues) {
            System.out.println(sma);
        }

        List<String> signals = fetcher.generateSignals(stockDataList, 5);
        List<String> tempSignals = signals;
        System.out.println("Buy/Sell Signals based on 5-day SMA:");
        for (String signal : tempSignals) {
            System.out.println(signal);
        }

        Backtester backtester = new Backtester(stockDataList, signals);
        double cumulativeReturn = backtester.calculateCumulativeReturn();
        double annualizedReturn = backtester.calculateAnnualizedReturn(cumulativeReturn, stockDataList.size());
        double sharpeRatio = backtester.calculateSharpeRatio(cumulativeReturn, 0.01, stockDataList.size());

        // Display performance metrics
        System.out.printf("Cumulative Return: %.2f%%\n", cumulativeReturn * 100);
        System.out.printf("Annualized Return: %.2f%%\n", annualizedReturn * 100);
        System.out.printf("Sharpe Ratio: %.2f\n", sharpeRatio);
    }

}
