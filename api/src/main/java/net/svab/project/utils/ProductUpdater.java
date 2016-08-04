package net.svab.project.utils;

import net.svab.project.domain.Price;
import net.svab.project.domain.Product;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class ProductUpdater {

    public static Product updateProductWithDataFrom(Product productToUpdate, Product data) {
        productToUpdate.setName(data.getName());
        productToUpdate.setDescription(data.getDescription());
        productToUpdate.setTags(data.getTags());

        Map<CurrencyUnit, BigDecimal> newPrices = data.getPrices().stream().collect(toMap(Price::getCurrency, Price::getAmount));
        Iterator<Price> existingPrices = productToUpdate.getPrices().iterator();
        while (existingPrices.hasNext()) {
            Price existingPrice = existingPrices.next();
            if (newPrices.containsKey(existingPrice.getCurrency())) {
                existingPrice.setAmount(newPrices.remove(existingPrice.getCurrency()));
            } else {
                existingPrices.remove();
            }
        }

        for (CurrencyUnit currencyUnit : newPrices.keySet()) {
            productToUpdate.getPrices().add(new Price(currencyUnit, newPrices.get(currencyUnit)));
        }
        return productToUpdate;
    }

    public static Product setProductPrice(Product product, Price newPrice) {
        boolean priceExists = false;
        for (Price price : product.getPrices()) {
            if (price.getCurrency().equals(newPrice.getCurrency())) {
                price.setAmount(newPrice.getAmount());
                priceExists = true;
                break;
            }
        }

        if(!priceExists)
            product.getPrices().add(newPrice);

        return product;
    }
}
