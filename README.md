# Mark Price Controller

Mark Price Controller is a Spring Boot application that provides functionality to calculate and display historical RSI (Relative Strength Index) data, current cryptocurrency prices, historical candlestick data, and the current Order Book for cryptocurrency pairs in a web interface.

## Features

- Calculate historical RSI data
- Display RSI data as a graph in the web interface
- Fetch and display current cryptocurrency prices
- Fetch and display historical candlestick data
- Fetch and display the current Order Book for cryptocurrency pairs
- Uses PostgreSQL for data storage
- Dockerized for easy setup and deployment

## Prerequisites

- Docker
- Docker Compose

## Getting Started

### Clone the repository

```sh
git clone https://github.com/yourusername/MarkPriceController.git
cd MarkPriceController
```
## Docker Usage Instructions
### Prerequisites
- Ensure Docker and Docker Compose are installed on your system.
- Create a .env file in the root directory of the project with the necessary environment variables.
### Building Docker Images
#### Development Environment

For development, use the docker-compose.dev.yml file:

```sh
docker-compose -f docker-compose.yml up --build
```

This will start the application and the PostgreSQL database. The application will be accessible at http://localhost:8085.

#### Production Environment

For production, use the docker-compose.prod.yml file:

```sh
docker-compose -f docker-compose.prod.yml up --build -d
```

This will start the application and the PostgreSQL database in detached mode. The application will be accessible at http://localhost:8085.

### Stopping Docker Containers

To stop the application and remove containers:

#### Development Environment

```sh
docker-compose -f docker-compose.yml down
```
#### Production Environment

```sh
docker-compose -f docker-compose.prod.yml down
```
### Accessing the Application
Once the application is running, it can be accessed at http://localhost:8085.

### Database Initialization
The PostgreSQL database will be automatically initialized and configured according to the settings in the .env file. You do not need to manually create or configure the database.

### Restarting the Application
To restart the application:

#### Development Environment
```sh
docker-compose -f docker-compose.yml restart
```

#### Production Environment
```sh
docker-compose -f docker-compose.prod.yml restart
```

### Running Tests
To run the tests, use the following command:
```sh
mvn test
```

### Building the JAR File
To build the JAR file, use the following command:
```sh
mvn clean package
```
The JAR file will be created in the target directory.



## Notes
- Ensure Docker and Docker Compose are installed and running on your system.
- Modify the environment variables in the .env and .env.prod files according to your setup.

