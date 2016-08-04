# Basic projects API using Spring Boot

## Requirements:
* Maven 3
* Java 8

### Usage:
Run server on default 8080 port with `mvn spring-boot:run`

Run server on different port with `mvn spring-boot:run -Dserver.port=3000`

### Database:
It uses file based H2 database located by default in `/tmp/products-db`

You can use different path with `mvn spring-boot:run -Ddatabase.path=/mnt/database`

### API call examples:
* get all products

  `curl -i http://localhost:8080/products`
* get product details

  `curl -i http://localhost:8080/products/{ID}` e.g `curl -i http://localhost:8080/products/102`
* create new product

  `curl -i -X POST -H "Content-Type:application/json" http://localhost:8080/products -d '{"name":"cake", "description":"very yummy!", "tags":["sweet","fresh"]}'`
* update existing product

  `curl -i -X PUT -H "Content-Type:application/json" http://localhost:8080/products/102 -d '{"name":"carrot cake", "description":"very yummy!", "tags":["sweet","fresh"], "prices":[{"currency":"GBP", "amount":"3.67"}]}'`
* set price points

  `curl -i -X PUT -H "Content-Type:application/json" http://localhost:8080/products/102/prices/EUR -d '{"amount":"3.67"}'`
