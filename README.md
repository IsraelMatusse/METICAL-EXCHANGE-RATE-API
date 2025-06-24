# Metical Converter

A Spring Boot application for currency exchange rate conversion, specifically designed for Mozambican Metical (MZN) currency operations. The application integrates with Bank of Mozambique (BM) web services to provide real-time exchange rates.

## 🚀 Features

- **Real-time Exchange Rates**: Fetches current exchange rates from Bank of Mozambique
- **Currency Conversion**: Convert amounts between different currencies
- **Caching System**: Implements Spring Cache for improved performance
- **RESTful API**: Provides REST endpoints for currency operations
- **Exception Handling**: Robust error handling for currency not found scenarios

## 🏗️ Architecture

The application follows a layered architecture pattern:

- **Services Layer**: Business logic for exchange rate operations
- **Integration Layer**: External API communication with BM web services
- **Infrastructure Layer**: Exception handling and utilities
- **Interfaces Layer**: Response DTOs and API contracts

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Internet connection (for fetching exchange rates)

## 🛠️ Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd metical-converter
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 🔧 Configuration

The application uses Spring Boot's auto-configuration. Key configurations include:

- **Caching**: Enabled with multiple cache regions
- **Web Client**: Configured for Bank of Mozambique API integration
- **SSL**: Handles SSL connections for external API calls

## 📚 API Endpoints

### Get All Exchange Rates
```http
GET /api/exchange-rates
```
Returns all available exchange rates from Bank of Mozambique.

### Get Exchange Rate by Currency
```http
GET /api/exchange-rates/{currency}
```
Returns exchange rate for a specific currency.

### Get Available Currencies
```http
GET /api/currencies
```
Returns list of all available currencies.

### Convert Amount
```http
GET /api/convert?amount={amount}&currency={currency}
```
Converts a specific amount to/from the given currency.

## 🏦 Supported Operations

### ExchangeRateService Methods

- `getExchangeRates()`: Fetch all current exchange rates
- `getExchangeRatesByCurrency(String currency)`: Get rates for specific currency
- `getCurrencys()`: List all available currencies
- `getExchangeRateByAmountAndCurrency(BigDecimal amount, String currency)`: Convert amounts with buy/sell rates

## 💾 Caching Strategy

The application implements multi-level caching:

- **exchangeRates**: Caches all exchange rates
- **exchangeRatesByCurrency**: Caches individual currency rates
- **currencys**: Caches available currencies list
- **exchangeRateByAmountAndCurrency**: Caches conversion calculations

## 🔄 Data Flow

1. **Request**: Client requests exchange rate information
2. **Cache Check**: System checks if data exists in cache
3. **External API**: If not cached, fetches from Bank of Mozambique API
4. **Processing**: Processes and calculates conversion rates
5. **Response**: Returns formatted response with buy/sell rates

## 🧮 Currency Conversion

The service provides both buy and sell rates:

- **Buy Rate**: Rate for purchasing foreign currency
- **Sell Rate**: Rate for selling foreign currency
- **Precision**: All calculations use 2 decimal places with HALF_EVEN rounding

## 🚨 Error Handling

- **NotFoundException**: Thrown when requested currency is not available
- **SSLException**: Handled for secure API communications
- **Validation**: Input validation for amounts and currency codes

## 🔧 Development

### Project Structure
```
src/main/java/com/metical_converter/
├── services/           # Business logic
├── integration/        # External API clients
├── infrastructure/     # Exceptions and utilities
└── interfaces/         # DTOs and responses
```

### Key Dependencies

- Spring Boot 3.3.13
- Spring Web (MVC & Reactive)
- Spring Cache
- Spring DevTools
- Maven

## 🧪 Testing

Run tests with:
```bash
mvn test
```

## 📦 Building

Create a production build:
```bash
mvn clean package
```

Create Docker image:
```bash
mvn spring-boot:build-image
```

## 🌐 External Integration

The application integrates with:
- **Bank of Mozambique API**: Primary source for exchange rates
- **SSL/TLS**: Secure communication protocols

## 📝 Notes

- Original package name was invalid (`com..metical-converter`) and has been corrected to `com.metical_converter`
- The application uses lazy loading for self-referencing service calls
- Custom key generation is implemented for cache optimization

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the terms specified in the parent POM configuration.

## 🆘 Support

For support and questions, please refer to the official Spring Boot documentation or create an issue in the project repository.
