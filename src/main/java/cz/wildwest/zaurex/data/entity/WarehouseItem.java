package cz.wildwest.zaurex.data.entity;

import cz.wildwest.zaurex.data.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "warehouse_items")
public class WarehouseItem extends AbstractEntity {

    @Size(max = 100, message = "Název může být dlouhý maximálně 100 znaků")
    private String title;

    @Size(max = 500, message = "Krátký popis může být dlouhý maximálně 500 znaků")
    private String briefDescription;

    private boolean sellable;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "of")
    private Set<Variant> variants;

    public WarehouseItem() {
        sellable = true;
    }

    public WarehouseItem(String title, String briefDescription) {
        this();
        this.title = title;
        this.briefDescription = briefDescription;
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

    @Entity
    @Table(name = "warehouse_item_variants")
    public static class Variant extends AbstractEntity {

        public Variant(){}

        private Variant(WarehouseItem of) {
            this.of = of;
        }

        @ManyToOne
        @JoinColumn
        private WarehouseItem of;

        @Size(max = 50, message = "Barva může mít maximálně 50 znaků")
        private String colour;

        @Min(value = 0, message = "Počet kusů nemůže být záporný")
        private int quantity;

        private float price;

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

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }
    }
}
