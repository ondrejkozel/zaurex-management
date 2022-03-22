package cz.wildwest.zaurex.data.entity;

import cz.wildwest.zaurex.data.AbstractEntity;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "holidays")
public class Holiday extends AbstractEntity {
    
    @ManyToOne
    @JoinColumn
    @NotNull
    private User owner;
    
    @NotNull
    private LocalDate fromDate;
    
    @NotNull
    private LocalDate toDate;

    public Holiday() {
    }

    public Holiday(User owner, LocalDate fromDate, LocalDate toDate) {
        this.owner = owner;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
}
