package cz.wildwest.zaurex.data.entity;

import cz.wildwest.zaurex.data.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "invoices")
public class Invoice extends AbstractEntity {

    public static final Period TRANSFER_MATURITY_LIMIT = Period.ofMonths(1);

    @NotNull
    private LocalDateTime issuedAt;

    @NotNull
    private LocalDateTime maturityDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "of")
    private List<Item> items;

    private String issuedBy;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentForm paymentForm;

    public Invoice() {
    }

    public Invoice(User issuedBy, Collection<Item> items, PaymentForm paymentForm) {
        this.paymentForm = paymentForm;
        issuedAt = LocalDateTime.now();
        maturityDate = issuedAt;
        if (paymentForm == PaymentForm.TRANSFER) maturityDate = maturityDate.plus(TRANSFER_MATURITY_LIMIT);
        this.issuedBy = issuedBy.getName();
        this.items = items.stream().peek(item -> item.setOf(this)).sorted(Comparator.comparing(Item::getVariantLabel).reversed()).collect(Collectors.toList());
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int getTotalAmount() {
        return items.stream().mapToInt(value -> value.amount).sum();
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public double getTotalPrice() {
        return items.stream().mapToDouble(Item::getTotalPrice).sum();
    }

    public String getNumber() {
        return String.valueOf(issuedAt.getYear()) + getId();
    }

    public PaymentForm getPaymentForm() {
        return paymentForm;
    }

    public LocalDateTime getMaturityDate() {
        return maturityDate;
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

    public enum PaymentForm {
        CASH("hotově"), CARD("kartou"), TRANSFER("převodem");

        private final String text;

        PaymentForm(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
