package cz.wildwest.zaurex.data.entity;

import cz.wildwest.zaurex.data.AbstractEntity;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    @Size(max = 150)
    @NotNull
    private String message;

    public Holiday(User owner, LocalDate fromDate, LocalDate toDate) {
        status = Status.PENDING;
        message = "";
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public enum Status {
        PENDING, APPROVED, DENIED
    }
}
