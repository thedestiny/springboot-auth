# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Cloud microservices project implementing OAuth2.0 authorization and Spring Security for user authentication and authorization. The system uses MySQL for data storage and Redis for configuration caching, with Nacos for service discovery and configuration management.

## Build and Run Commands

### Build the project
```bash
mvn clean compile
```

### Package all modules
```bash
mvn clean package
```

### Run a specific module (from module directory)
```bash
# For auth-server
cd auth-server
mvn spring-boot:run

# For auth-gateway
cd auth-gateway
mvn spring-boot:run

# For sandbox modules
cd sandbox-stock
mvn spring-boot:run
```

### Run tests
```bash
# Run all tests
mvn test

# Run tests for a specific module
mvn test -pl auth-server
```

## Technology Stack

- **Java**: 1.8
- **Spring Boot**: 2.7.0 (varies across modules)
- **Spring Cloud**: 2021.0.3
- **Spring Cloud Alibaba**: 2021.0.1.0
- **Nacos**: Service discovery and configuration center (localhost:8848)
- **Redis**: Caching (localhost:6379, database: 2)
- **MySQL**: Data storage (auth_db database)
- **MyBatis Plus**: 3.4.3 (ORM)
- **OAuth2.0**: Spring Cloud Starter OAuth2 2.2.5.RELEASE
- **Spring Cloud Gateway**: API Gateway
- **Feign**: Service-to-service communication with OkHttp
- **Flink**: 1.17.2 (Stream processing in data-flink module)
- **Kafka**: Message queue (spring-kafka 3.1.10)
- **Hutool**: 5.8.9 (Utility library)
- **FastJSON**: 2.0.12 (JSON serialization)

## Architecture

### Core Modules

1. **auth-server** (Port 9401)
   - OAuth2.0 Authorization Server
   - User authentication and token issuance
   - Key classes: `OAuth2AuthorizationServer`, `WebSecurityConfig`, `JwtTokenEnhancer`
   - Stores tokens using JWT with RSA key pairs (jwt.jks in classpath)

2. **auth-gateway** (Port 9201)
   - API Gateway using Spring Cloud Gateway
   - Routes requests to downstream services
   - OAuth2.0 Resource Server for JWT validation
   - Key classes: `ResourceServerConfig`, `AuthorizationManager`, `AppGatewayFilter`
   - Route configuration in application.yml

3. **auth-common**
   - Shared library containing common utilities, DTOs, and APIs
   - Structure:
     - `api/`: Feign client interfaces (OrderApi, OrderInfoApi)
     - `dto/`: Data transfer objects (AuthConstant, etc.)
     - `utils/`: Common utility classes
     - `config/`: Shared configuration classes
     - `exception/`: Custom exception handling

4. **order-server**
   - Order management service
   - Includes payment integration (WeChat Pay, Alipay)
   - Uses PowerMock for testing

5. **product-server**
   - Product management service
   - Exposes APIs through the gateway

### Sandbox/Experimental Modules

- **sandbox-stock**: Stock-related features with WeChat Pay, QR code generation, encryption/decryption
- **sandbox-flex**: MyBatis Flex testing module
- **sandbox-message**: Message handling
- **sandbox-practice**: Practice/experimental code
- **sandbox-desen**: Data desensitization utilities

### Data Processing Modules

- **data-flink**: Apache Flink stream processing with Kafka connector
- **data-cleaning**: Data cleaning and migration services

## Key Configuration

### OAuth2.0 Authorization Flow

1. **Authorization Code Flow**:
   - Get authorization code: `GET http://127.0.0.1:9401/oauth/authorize?client_id=client_001&response_type=code&scope=all`
   - Callback endpoint receives code
   - Exchange code for token via `/oauth/token`

2. **JWT Configuration**:
   - RSA key pairs stored in `jwt.jks` (class path resource)
   - Password: "123456"
   - Gateway validates JWT via `/rsa/publicKey` endpoint

### Gateway Routes

Routes are configured in `auth-gateway/src/main/resources/application.yml`:
- `/api/**` → product-server
- `/auth/**` → auth-server
- White-listed paths: `/actuator/**`, `/auth/oauth/token`, `/api/user/info`

### Database Configuration

- **Database**: auth_db (MySQL)
- **Connection**: HikariCP pool
- **ORM**: MyBatis Plus with code generation support

### Service Discovery

All services register with Nacos at `localhost:8848` in namespace `163c702f-c805-42f2-a1e1-a491f086f3a2`, group `springboot-oauth`.

## Code Organization Patterns

### Feign Client Definition
Feign clients are defined in `auth-common/src/main/java/com/platform/authcommon/api/` and include configuration in `OrderFeignConfig.java`.

### Security Configuration
- Authorization server configuration: `auth-server/config/OAuth2AuthorizationServer.java`
- Resource server configuration: `auth-gateway/config/ResourceServerConfig.java`
- Web security: `auth-server/config/WebSecurityConfig.java`

### Filter Chain
Gateway filters are in `auth-gateway/filter/`:
- `AppGatewayFilter`: Custom gateway filter
- `IgnoreUrlsRemoveJwtFilter`: Removes JWT from white-listed requests

## Task Workflow (6A)

The project uses a 6A workflow defined in `.trae/rules/project_rules.md`:

1. **Align**: Clarify requirements and understand project context
2. **Architect**: Design architecture and interfaces
3. **Atomize**: Break down into atomic tasks
4. **Approve**: Review and approve the plan
5. **Automate**: Implement code and tests
6. **Assess**: Evaluate quality and deliver

When working on features, follow this workflow and create documentation in `docs/任务名/` directory.

## Important Notes

- API keys and sensitive information should be stored in `.env` files and not committed to git
- The project uses both JWT with RSA key pairs and Redis for token management
- Custom exception handling is implemented in each module's `exception/` package
- Swagger documentation is available at `/swagger-ui.html` when configured
- The sandbox modules are experimental and may contain unstable code
- Database migration scripts may be in the `data/` directory