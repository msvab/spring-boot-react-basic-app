package net.svab.project.controllers;

import net.svab.project.domain.Price;
import net.svab.project.domain.PricePoint;
import net.svab.project.domain.Product;
import net.svab.project.errors.ProductNotFoundException;
import net.svab.project.repositories.ProductRepository;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

import static javax.money.Monetary.getCurrency;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static net.svab.project.utils.ProductUpdater.setProductPrice;
import static net.svab.project.utils.ProductUpdater.updateProductWithDataFrom;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository repository;

    @Autowired public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(path = "", method = GET, produces = APPLICATION_JSON_VALUE)
    public Collection<Product> getAllProducts() {
        return repository.findAllByOrderByNameAsc();
    }

    @RequestMapping(path = "", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Product createProduct(@Validated @RequestBody Product product, HttpServletResponse response) {
        Product createdProduct = repository.save(product);
        response.setStatus(SC_CREATED);
        response.addHeader(LOCATION, "/products/" + createdProduct.getId());
        return createdProduct;
    }

    @RequestMapping(path = "/{id}", method = GET, produces = APPLICATION_JSON_VALUE)
    public Product getProductDetails(@PathVariable("id") @NotEmpty Long productId) {
        return repository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @RequestMapping(path = "/{id}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    public void updateProductDetails(@PathVariable("id") @NotEmpty Long productId, @Validated @RequestBody Product product) {
        Product existingProduct = getProductDetails(productId);
        updateProductWithDataFrom(existingProduct, product);
        repository.save(existingProduct);
    }

    @RequestMapping(path = "/{id}/prices/{currency}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    public void setPrice(@PathVariable("id") @NotEmpty Long productId,
                         @PathVariable("currency") @NotEmpty String currency,
                         @RequestBody @Validated PricePoint pricePoint) {
        Product existingProduct = getProductDetails(productId);
        setProductPrice(existingProduct, new Price(getCurrency(currency), pricePoint.getAmount()));
        repository.save(existingProduct);
    }
}
