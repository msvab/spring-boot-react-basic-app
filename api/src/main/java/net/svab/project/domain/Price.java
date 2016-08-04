package net.svab.project.domain;

import org.hibernate.annotations.Type;

import javax.money.CurrencyUnit;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "product_prices")
@SequenceGenerator(name = "product_price_id_seq", sequenceName = "product_price_id_seq")
public class Price {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "product_price_id_seq")
    private Long id;

    @NotNull(message = "error.price.currency")
    @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentCurrencyUnit")
    private CurrencyUnit currency;

    @NotNull(message = "error.price.amount")
    private BigDecimal amount;

    private Price() { }

    public Price(CurrencyUnit currency, BigDecimal amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public Long getId() { return id; }

    public CurrencyUnit getCurrency() { return currency; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    @Override public int hashCode() {return Objects.hash(id, currency, amount);}

    @Override public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || getClass() != obj.getClass()) {return false;}
        final Price other = (Price) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.currency, other.currency)
                && Objects.equals(this.amount, other.amount);
    }

    @Override public String toString() {
        return "Price{id=" + id + ", currency=" + currency + ", amount=" + amount + '}';
    }
}
