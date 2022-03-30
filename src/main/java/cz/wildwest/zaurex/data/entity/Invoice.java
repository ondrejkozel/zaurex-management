package cz.wildwest.zaurex.data.entity;

import cz.wildwest.zaurex.data.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "invoices")
public class Invoice extends AbstractEntity {

    @NotNull
    private LocalDateTime issuedAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "of")
    private List<Item> items;

    private String issuedBy;

    public Invoice() {
    }

    public Invoice(User issuedBy, Collection<Item> items) {
        issuedAt = LocalDateTime.now();
        this.issuedBy = issuedBy.getName();
        this.items = items.stream().peek(item -> item.setOf(this)).sorted(Comparator.comparing(Item::getVariantLabel)).collect(Collectors.toList());
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public double getTotalPrice() {
        return items.stream().mapToDouble(Item::getTotalPrice).sum();
    }

    @Entity
    @Table(name = "invoice_items")
    public static class Item extends AbstractEntity {

        @ManyToOne
        @JoinColumn
        private Invoice of;

        @NotBlank
        private String label;

        @NotBlank
        private String variantLabel;

        @Min(1)
        private int amount;

        private double pricePerOne;

        public Item() {
        }

        public Item(String label, String variantLabel, int amount, double pricePerOne) {
            this.label = label;
            this.variantLabel = variantLabel;
            this.amount = amount;
            this.pricePerOne = pricePerOne;
        }

        public Item(WarehouseItem.Variant warehouseItemVariant, int amount) {
            this(warehouseItemVariant.getOf().getTitle(), warehouseItemVariant.getColour(), amount, warehouseItemVariant.getPrice());
        }

        public Invoice getOf() {
            return of;
        }

        private void setOf(Invoice of) {
            this.of = of;
        }

        public String getVariantLabel() {
            return variantLabel;
        }

        public int getAmount() {
            return amount;
        }

        public double getTotalPrice() {
            return pricePerOne * amount;
        }

        public String getLabel() {
            return label;
        }

        public double getPricePerOne() {
            return pricePerOne;
        }
    }

}
