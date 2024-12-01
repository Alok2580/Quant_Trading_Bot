package org.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


public class DataFetcherDSMA {


//    private static final String API_KEY = "5T7UFV7SLMBKMV74";
//    private static final String BASE_URL = "https://www.alphavantage.co/query";
    private static final String BASE_URL = "https://query1.finance.yahoo.com/v7/finance/chart/";



    public List<StockData> fetchStockData(String symbol) {
        List<StockData> stockDataList = new ArrayList<>();
        try {

            String endpoint = BASE_URL + symbol + "?interval=1d&range=12mo";



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

    public List<String> generateSignals(List<StockData> stockDataList, int shortPeriod, int longPeriod) {
        List<Double> shortSMA = calculateSMA(stockDataList, shortPeriod);
        List<Double> longSMA = calculateSMA(stockDataList, longPeriod);

//        System.out.println(shortSMA.size());
//        System.out.println(longSMA.size());
//


        List<String> signals = new ArrayList<>();
        boolean isHolding = false;

        // Ensure we only process valid indices
//        int startIndex = Math.max(shortPeriod, longPeriod) - 1;

        int startIndex = 0;
        int n=Math.min(shortSMA.size(), longSMA.size());
        for (int i = 1; i < n; i++) {
            // Use safe indexing for SMA lists
            double currentShortSMA = shortSMA.get(i);
            double currentLongSMA = longSMA.get(i);
            double previousShortSMA = shortSMA.get(i-1);
            double previousLongSMA= longSMA.get(i-1);

//            double currentShortSMA = i - shortPeriod + 1 >= 0 && i - shortPeriod + 1 < shortSMA.size()
//                    ? shortSMA.get(i) : 0;
//            double currentLongSMA = i - longPeriod + 1 >= 0 && i - longPeriod + 1 < longSMA.size()
//                    ? longSMA.get(i) : 0;
//            double previousShortSMA = i - shortPeriod >= 0 && i - shortPeriod < shortSMA.size()
//                    ? shortSMA.get(i - 1) : 0;
//            double previousLongSMA = i - longPeriod >= 0 && i - longPeriod < longSMA.size()
//                    ? longSMA.get(i - 1) : 0;


            if (previousShortSMA <= previousLongSMA && currentShortSMA > currentLongSMA && !isHolding) {
                signals.add("Buy on day " + (i + 1) + " at price " + stockDataList.get(i).getClose());
                isHolding = true;
            } else if (previousShortSMA >= previousLongSMA && currentShortSMA < currentLongSMA && isHolding) {
                signals.add("Sell on day " + (i + 1) + " at price " + stockDataList.get(i).getClose());
                isHolding = false;
            } else {
                signals.add("Hold on day " + (i + 1));
            }
        }
        return signals;
    }


    public static void main(String[] args) {
        DataFetcherDSMA fetcher = new DataFetcherDSMA();

        List<StockData> stockDataList = fetcher.fetchStockData("AAPL");
        System.out.println("Fetched Stock Data: ");
        for (StockData data : stockDataList) {
            System.out.println(data);
        }

        int shortPeriod = 20 ;
        int longPeriod = 50;

        List<String> signals = fetcher.generateSignals(stockDataList, shortPeriod, longPeriod);
        System.out.println("Buy/Sell Signals based on SMA Crossovers:");
        for (String signal : signals) {
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
