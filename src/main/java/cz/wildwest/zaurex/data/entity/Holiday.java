package cz.wildwest.zaurex.data.entity;

import cz.wildwest.zaurex.data.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

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

    @Size(max = 400, message = "Odpověď může mít maximálně 400 znaků")
    @NotNull
    private String managerResponse;

    @Size(max = 400, message = "Zpráva může mít maximálně 400 znaků")
    @NotNull
    private String userMessage;

    public Holiday(User owner, LocalDate fromDate, LocalDate toDate) {
        this(owner);
        this.owner = owner;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Holiday(User owner) {
        this();
        this.owner = owner;
    }

    public Holiday() {
        status = Status.PENDING;
        managerResponse = "";
        userMessage = "";
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

    public String getManagerResponse() {
        return managerResponse;
    }

    public void setManagerResponse(String message) {
        this.managerResponse = message;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public enum Status {
        PENDING("nevyřízeno"), APPROVED("schváleno"), DENIED("zamítnuto");

        private final String text;

        Status(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }


        @Override
        public String toString() {
            return text;
        }
    }
}
