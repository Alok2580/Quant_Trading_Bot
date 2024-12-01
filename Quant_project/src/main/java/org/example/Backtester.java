package org.example;

import java.util.List;

public class Backtester {

    private List<StockData> stockDataList;
    private List<String> signals;

    public Backtester(List<StockData> stockDataList, List<String> signals) {
        this.stockDataList = stockDataList;
        this.signals = signals;
    }
    

    public double calculateCumulativeReturn() {

        double cumulativeReturn = 1.0;
        boolean isHolding = false;
        double entryPrice = 0.0;

        for (int i = 0; i < signals.size(); i++) {

            String signal = signals.get(i);
            double currentPrice = stockDataList.get(i).getClose();

            if (signal.startsWith("Buy") && !isHolding) {
                entryPrice = currentPrice;
                isHolding = true;
            } else if (signal.startsWith("Sell") && isHolding) {
                cumulativeReturn *= (currentPrice / entryPrice);
                isHolding = false;
            }
        }



        if (isHolding) {
            cumulativeReturn *= (stockDataList.get(stockDataList.size() - 1).getClose() / entryPrice);
        }

        return cumulativeReturn - 1;

    }

    public double calculateAnnualizedReturn(double cumulativeReturn, int tradingDays) {
        double years = tradingDays / 252.0;
        return Math.pow(1 + cumulativeReturn, 1 / years) - 1;
    }


    public double calculateSharpeRatio(double cumulativeReturn, double riskFreeRate, int tradingDays) {
        double annualizedReturn = calculateAnnualizedReturn(cumulativeReturn, tradingDays);
        double stdDev = calculatePortfolioStdDev();
        return (annualizedReturn - riskFreeRate) / stdDev;
    }

    private double calculatePortfolioStdDev() {
        double meanReturn = calculateMeanDailyReturn();
        double sumSquaredDeviations = 0.0;

        for (int i = 1; i < stockDataList.size(); i++) {
            double dailyReturn = (stockDataList.get(i).getClose() / stockDataList.get(i - 1).getClose()) - 1;
            sumSquaredDeviations += Math.pow(dailyReturn - meanReturn, 2);
        }

        return Math.sqrt(sumSquaredDeviations / (stockDataList.size() - 1));

    }

    private double calculateMeanDailyReturn() {
        double sumDailyReturns = 0.0;

        for (int i = 1; i < stockDataList.size(); i++) {
            double dailyReturn = (stockDataList.get(i).getClose() / stockDataList.get(i - 1).getClose()) - 1;
            sumDailyReturns += dailyReturn;
        }

        return sumDailyReturns / (stockDataList.size() - 1);
    }
}

