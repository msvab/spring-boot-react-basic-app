package net.svab.project.controllers;

import net.svab.project.controllers.ValidationHandler.ValidationErrors;
import net.svab.project.domain.Price;
import net.svab.project.domain.PricePoint;
import net.svab.project.domain.Product;
import net.svab.project.repositories.ProductRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static javax.money.Monetary.getCurrency;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProductControllerTest {
    private static final CurrencyUnit USD = getCurrency("USD");
    private static final CurrencyUnit EUR = getCurrency("EUR");
    private static final CurrencyUnit CZK = getCurrency("CZK");

    @Autowired TestRestTemplate restTemplate;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired ProductRepository productRepository;

    @After public void wipeData() {
        jdbcTemplate.execute("DELETE FROM products");
    }

    @Test public void shouldReturnAllProducts() {
        Product product = new Product(null, "Cake", "Soo delish!",
                asList("cake", "yummy"),
                asList(new Price(USD, new BigDecimal("1.25")), new Price(CZK, new BigDecimal("16.50"))));
        productRepository.saveAndFlush(product);

        ResponseEntity<List<Product>> response = restTemplate.exchange("/products", GET, null, new ParameterizedTypeReference<List<Product>>() {});

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).usingElementComparatorIgnoringFields("id", "prices").containsOnly(product);
        assertThat(response.getBody().get(0).getPrices()).containsAll(product.getPrices());
    }

    @Test public void shouldReturnDetailsOfSingleProduct() {
        Product product = new Product(null, "Cake", "Soo delish!",
                asList("cake", "yummy"),
                asList(new Price(USD, new BigDecimal("1.25")), new Price(CZK, new BigDecimal("16.50"))));
        productRepository.saveAndFlush(product);

        ResponseEntity<Product> response = restTemplate.getForEntity("/products/" + product.getId(), Product.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualToIgnoringGivenFields(product, "id", "prices");
        assertThat(response.getBody().getPrices()).containsAll(product.getPrices());
    }

    @Test public void getShouldReturnNotFoundForInvalidProductId() {
        ResponseEntity<Product> response = restTemplate.getForEntity("/products/9999999", Product.class);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test public void shouldCreateNewProduct() {
        Product product = new Product(null, "Cake", "Soo delish!",
                asList("cake", "yummy"),
                singletonList(new Price(USD, new BigDecimal("1.25"))));

        ResponseEntity<Product> response = restTemplate.postForEntity("/products", product, Product.class);

        assertThat(response.getStatusCode()).isEqualTo(CREATED);

        String createdProductLocation = response.getHeaders().getLocation().toString();
        assertThat(createdProductLocation).matches("^/products/\\d+$");

        long createdProductId = Long.valueOf(createdProductLocation.substring(createdProductLocation.lastIndexOf('/') + 1));
        Product createdProduct = restTemplate.getForObject("/products/" + createdProductId, Product.class);

        assertThat(createdProduct).isEqualToIgnoringGivenFields(product, "id", "prices");
        assertThat(createdProduct.getPrices()).usingElementComparatorIgnoringFields("id").containsAll(product.getPrices());
    }

    @Test public void shouldNotLetYouCreateProductWithRequiredFieldsMissing() {
        Product product = new Product(null, null, "", emptyList(), emptyList());

        ResponseEntity<ValidationErrors> response = restTemplate.postForEntity("/products", product, ValidationErrors.class);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody().getErrors())
                .containsOnly("Product description is required", "Product name is required", "Product has to have at least one price point");
    }

    @Test public void shouldNotLetYouCreateProductWithInvalidPrice() {
        Product product = new Product(null, "name", "desc", emptyList(), singletonList(new Price(null, null)));

        ResponseEntity<ValidationErrors> response = restTemplate.postForEntity("/products", product, ValidationErrors.class);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody().getErrors()).containsOnly("Price currency is required", "Price amount is required");
    }

    @Test public void shouldNotLetYouCreateProductWithTwoPricesInSameCurrency() {
        Product product = new Product(null, "name", "desc", emptyList(), asList(new Price(USD, new BigDecimal("1.25")), new Price(USD, new BigDecimal("10.25"))));

        ResponseEntity<ValidationErrors> response = restTemplate.postForEntity("/products", product, ValidationErrors.class);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody().getErrors()).containsOnly("Each currency can have only single price point");
    }

    @Test public void shouldUpdateProduct() {
        Product originalProduct = new Product(null, "Cake", "Soo delish!",
                asList("cake", "yummy"),
                singletonList(new Price(USD, new BigDecimal("1.25"))));
        productRepository.saveAndFlush(originalProduct);
        Product updatedProduct = new Product(originalProduct.getId(), "Carrot cake", "Soo yummy!",
                asList("carrot", "icing"),
                originalProduct.getPrices());

        ResponseEntity<String> response = restTemplate.exchange("/products/" + originalProduct.getId(), PUT, new HttpEntity<>(updatedProduct), String.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);

        Product actualProduct = restTemplate.getForObject("/products/" + originalProduct.getId(), Product.class);

        assertThat(actualProduct).isEqualToIgnoringGivenFields(updatedProduct, "id", "prices");
        assertThat(actualProduct.getPrices()).containsAll(updatedProduct.getPrices());
    }

    @Test public void shouldUpdateCurrenciesWhenUpdatingProduct() {
        Product originalProduct = new Product(null, "Cake", "Soo delish!",
                asList("cake", "yummy"),
                asList(new Price(USD, new BigDecimal("1.25")), new Price(EUR, new BigDecimal("54.90"))));
        productRepository.saveAndFlush(originalProduct);
        Product updatedProduct = new Product(originalProduct.getId(), "Carrot cake", "Soo yummy!",
                asList("carrot", "icing"),
                asList(new Price(USD, new BigDecimal("9.95")), new Price(CZK, new BigDecimal("109.00"))));

        ResponseEntity<String> response = restTemplate.exchange("/products/" + originalProduct.getId(), PUT, new HttpEntity<>(updatedProduct), String.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);

        Product actualProduct = restTemplate.getForObject("/products/" + originalProduct.getId(), Product.class);

        assertThat(actualProduct).isEqualToIgnoringGivenFields(updatedProduct, "id", "prices");
        assertThat(actualProduct.getPrices()).usingElementComparatorIgnoringFields("id").containsAll(updatedProduct.getPrices());
    }

    @Test public void putShouldReturnNotFoundForInvalidProductId() {
        Product product = new Product(null, "Cake", "Soo delish!",
                asList("cake", "yummy"),
                singletonList(new Price(USD, new BigDecimal("1.25"))));

        ResponseEntity<String> response = restTemplate.exchange("/products/9999999", PUT, new HttpEntity<>(product), String.class);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test public void shouldSetNewPricePoint() {
        Price usd = new Price(USD, new BigDecimal("1.25"));
        Product originalProduct = new Product(null, "Cake", "Soo delish!", asList("cake", "yummy"), singletonList(usd));
        productRepository.saveAndFlush(originalProduct);
        PricePoint newPricePoint = new PricePoint(new BigDecimal("45.63"));

        ResponseEntity<String> response = restTemplate.exchange("/products/" + originalProduct.getId() + "/prices/CZK", PUT,
                new HttpEntity<>(newPricePoint), String.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);

        Product actualProduct = restTemplate.getForObject("/products/" + originalProduct.getId(), Product.class);
        assertThat(actualProduct.getPrices())
                .extracting(price -> tuple(price.getCurrency(), price.getAmount()))
                .containsOnly(tuple(USD, usd.getAmount()), tuple(CZK, newPricePoint.getAmount()));
    }

    @Test public void shouldUpdateExistingPricePoint() {
        Price usd = new Price(USD, new BigDecimal("1.25"));
        Product originalProduct = new Product(null, "Cake", "Soo delish!", asList("cake", "yummy"), singletonList(usd));
        productRepository.saveAndFlush(originalProduct);
        PricePoint newPricePoint = new PricePoint(new BigDecimal("45.63"));

        ResponseEntity<String> response = restTemplate.exchange("/products/" + originalProduct.getId() + "/prices/USD", PUT,
                new HttpEntity<>(newPricePoint), String.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);

        Product actualProduct = restTemplate.getForObject("/products/" + originalProduct.getId(), Product.class);
        assertThat(actualProduct.getPrices())
                .extracting(price -> tuple(price.getCurrency(), price.getAmount()))
                .containsOnly(tuple(USD, newPricePoint.getAmount()));
    }

    @Test public void setPriceShouldReturnNotFoundForInvalidProductId() {
        PricePoint pricePoint = new PricePoint(new BigDecimal("45.63"));

        ResponseEntity<String> response = restTemplate.exchange("/products/9/prices/USD", PUT, new HttpEntity<>(pricePoint), String.class);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }
}