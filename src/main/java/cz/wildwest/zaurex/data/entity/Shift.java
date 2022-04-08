package cz.wildwest.zaurex.data.entity;

import cz.wildwest.zaurex.data.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "shifts")
public class Shift extends AbstractEntity {

    @ManyToOne
    @JoinColumn
    @NotNull
    private User owner;

    @NotNull
    private LocalDateTime fromDateTime;

    @NotNull
    private LocalDateTime toDateTime;

    public Shift() {
    }

    public Shift(User owner, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        this.owner = owner;
        this.fromDateTime = fromDateTime;
        this.toDateTime = toDateTime;
    }

    public User getOwner() {
        return owner;
    }

    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    public LocalDateTime getToDateTime() {
        return toDateTime;
    }
}
