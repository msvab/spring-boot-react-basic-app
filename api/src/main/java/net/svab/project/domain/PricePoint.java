package net.svab.project.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PricePoint {
    @NotNull(message = "error.price.amount")
    private final BigDecimal amount;

    @JsonCreator
    public PricePoint(@JsonProperty("amount") BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() { return amount; }

    @Override public String toString() {
        return "PricePoint{amount=" + amount + '}';
    }
}
