
# Quantitative Trading Bot  

This repository contains a **Quantitative Trading Bot** that utilizes the **Simple Moving Average (SMA)** technique to generate buy/sell signals and perform backtesting on historical equity data.  

## Features  
- **Data Fetching**: Retrieves equity data via the Yahoo Finance API.  
- **Technical Indicators**: Implements the Simple Moving Average (SMA) for trading signal generation.  
- **Backtesting Engine**: Simulates historical trading performance to evaluate the SMA strategy.  
- **Performance Metrics**: Outputs key performance indicators such as:  
  - Annualized Return  
  - Sharpe Ratio
  - Cumulative Return 

## Repository Link  
Find the code and project details in the GitHub repository: [Quantitative Trading Bot](https://github.com/Alok2580/Quant_Trading_Bot).  

## Prerequisites  
### Tools and Libraries  
- **Java**: Primary programming language  
- **APIs**: Yahoo Finance API for equity data  
- **Libraries**:  
  - `javax.xml`: For XML configuration (if applicable)  
  - Statistical and financial calculation libraries  

Ensure you have Java 8+ installed on your system.  

## Getting Started  
### Installation  
1. Clone the repository:  
   ```bash  
   git clone https://github.com/Alok2580/Quant_Trading_Bot.git  
   cd Quant_Trading_Bot  
   ```  

2. Build the project:  
   ```bash  
   mvn clean install  
   ```  

3. Configure the API keys and stock universe in the `config.xml` file.  

### Running the Bot  
1. Start the bot using the following command:  
   ```bash  
   java -jar target/quant-trading-bot.jar  
   ```  

2. The bot will fetch data, execute the SMA strategy, and output performance metrics.  

## Key Components  
- **`Backtester.java`**: Core logic for backtesting the strategy.  
- **`IndicatorCalculator.java`**: Calculates SMA and other indicators.  
- **`PerformanceMetrics.java`**: Computes annualized returns and the Sharpe Ratio.  
- **`DataFetcher.java`**: Integrates with the Yahoo Finance API to fetch data.  

## Example Outputs  
### Sample Performance Metrics  
| Metric             | Value   |  
|--------------------|---------|  
| Annualized Return  | 12.5%   |  
| Sharpe Ratio       | 1.8     |  

### Sample Trading Signals  
| Date       | Stock  | Signal | SMA    | Price   |  
|------------|--------|--------|--------|---------|  
| 2024-01-01 | AAPL   | Buy    | 150.00 | 145.00  |  
| 2024-01-02 | AAPL   | Hold   | 152.00 | 149.00  |  

## Future Improvements  
- Add support for rebalancing strategies.  
- Implement Value-at-Risk (VaR) for risk analysis.  
- Explore advanced technical indicators (e.g., RSI, Bollinger Bands).  

## License  
This project is licensed under the MIT License. See the `LICENSE` file for details.  

