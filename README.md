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
git clone https://github.com/IsraelMatusse/METICAL-EXCHANGE-RATE-API.git
cd METICAL-EXCHANGE-RATE-API
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8084`

## 🔧 Configuration

The application uses Spring Boot's auto-configuration. Key configurations include:

- **Caching**: Enabled with multiple cache regions
- **Web Client**: Configured for Bank of Mozambique API integration
- **SSL**: Handles SSL connections for external API calls

## 📚 API Endpoints

The api is documented using Swagger, so you can check all endpoints and it's details by accessing: http://localhost:8084/swagger-ui/index.html#/


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
- **RateLimitExceededException**: Thrown when the client exceds the limit of the requests permited per ip on a time window.

## Rate Limiting

For better rate limiting control, you can include the following headers:

- `X-API-Key`: Your API key (optional)
- `X-Client-ID`: Your client/application identifier (optional)

If not provided, rate limiting will be based on your IP address.

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


## 📦 Building

Create a production build:
```bash
mvn clean package
```

## 🌐 External Integration

The application integrates with:
- **Bank of Mozambique API**: Primary source for exchange rates
- **SSL/TLS**: Secure communication protocols

## 📝 Notes

- The application uses lazy loading for self-referencing service calls
- Custom key generation is implemented for cache optimization

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 🆘 Support

For support and questions, please refer to the official Spring Boot documentation or create an issue in the project repository. But you can also reach me at **devmathusse@gmail.com**
