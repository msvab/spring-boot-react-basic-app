package net.svab.project.domain;

import net.svab.project.domain.validation.SinglePricePerCurrencyCheck;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "products")
@SequenceGenerator(name = "product_id_seq", sequenceName = "product_id_seq")
public class Product {
    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "product_id_seq")
    private Long id;

    @NotBlank(message = "error.product.name.blank")
    @Length(max = 200, message = "error.product.name.long")
    private String name;

    @NotBlank(message = "error.product.description")
    private String description;

    @ElementCollection
    @CollectionTable(name = "product_tags")
    @Column(name = "name")
    private List<String> tags;

    @Valid
    @SinglePricePerCurrencyCheck
    @Size(min = 1, message = "error.product.prices")
    @OneToMany(targetEntity = Price.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private List<Price> prices;

    private Product() { }

    public Product(Long id, String name, String description, List<String> tags, List<Price> prices) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.prices = prices;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
    }
    public void setTags(List<String> tags) { this.tags = tags; }

    public List<Price> getPrices() {
        if (prices == null) {
            prices = new ArrayList<>();
        }
        return prices;
    }

    @Override public String toString() {
        return "Product{id=" + id + ", name='" + name + '\'' + ", description='" + description
                + '\'' + ", tags=" + tags + ", prices=" + prices + '}';
    }
}
