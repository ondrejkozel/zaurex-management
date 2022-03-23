package cz.wildwest.zaurex.data.entity;

import cz.wildwest.zaurex.data.AbstractEntity;
import java.time.LocalDate;
import javax.persistence.*;
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

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    public Holiday(User owner, LocalDate fromDate, LocalDate toDate) {
        status = Status.PENDING;
        this.owner = owner;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Holiday() {
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        PENDING, APPROVED, DENIED;
    }
}
