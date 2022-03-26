package cz.wildwest.zaurex.data.entity;

import cz.wildwest.zaurex.data.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Optional;

@Entity
@Table(name = "warehouse_items")
public class WarehouseItem extends AbstractEntity {

    @Size(max = 100, message = "Název může být dlouhý maximálně 100 znaků")
    @NotBlank
    private String title;

    @Size(max = 500, message = "Krátký popis může být dlouhý maximálně 500 znaků")
    private String briefDescription;

    private boolean sellable;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Category category;

    public WarehouseItem() {
        sellable = true;
    }

    public WarehouseItem(String title, String briefDescription, Category category) {
        this();
        this.title = title;
        this.briefDescription = briefDescription;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public boolean isSellable() {
        return sellable;
    }

    public void setSellable(boolean sellable) {
        this.sellable = sellable;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Optional<Integer> getTotalQuantity() {
        if (getTransientVariants().isEmpty()) return Optional.empty();
        return Optional.of(variants.stream().mapToInt(Variant::getQuantity).sum());
    }

    public Optional<Double> getTotalValue() {
        if (getTransientVariants().isEmpty()) return Optional.empty();
        return Optional.of(variants.stream().mapToDouble(value -> value.getQuantity() * value.getPrice()).sum());
    }

    public Optional<Boolean> isOutOfStock() {
        if (getTransientVariants().isEmpty()) return Optional.empty();
        return Optional.of(variants.stream().anyMatch(variant -> variant.getQuantity() == 0));
    }

    /**
     * only to inform other objects about it's variants
     */
    private transient Collection<Variant> variants;

    public void setTransientVariants(Collection<Variant> variants) {
        this.variants = variants;
    }

    public Optional<Collection<Variant>> getTransientVariants() {
        return Optional.ofNullable(variants);
    }
    @Entity
    @Table(name = "warehouse_item_variants")
    public static class Variant extends AbstractEntity {

        public Variant(){
            colour = "";
            quantity = 1;
            price = 0;
            note = "";
        }

        public Variant(WarehouseItem of, String colour, int quantity, double price) {
            this();
            this.of = of;
            this.colour = colour;
            this.quantity = quantity;
            this.price = price;
        }

        @ManyToOne
        @JoinColumn
        private WarehouseItem of;

        @Size(max = 50, message = "Barva může mít maximálně 50 znaků")
        @NotBlank
        private String colour;

        @Min(value = 0, message = "Počet kusů nemůže být záporný")
        private int quantity;

        private double price;

        @Size(max = 50, message = "Poznámka může mít maximálně 50 znaků")
        @NotNull
        private String note;

        public String getColour() {
            return colour;
        }

        public void setColour(String colour) {
            this.colour = colour;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public void setOf(WarehouseItem of) {
            this.of = of;
        }

        public WarehouseItem getOf() {
            return of;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }

    public enum Category {
        HIKING("turistika"), MOUNTAINEERING("lezectví"), RUNNING("běh"), OTHER("jiné");

        private final String title;

        Category(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }


        @Override
        public String toString() {
            return title;
        }
    }
}
