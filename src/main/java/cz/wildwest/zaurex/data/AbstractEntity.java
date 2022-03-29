package cz.wildwest.zaurex.data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue
    private long id;

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return (int) id;
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractEntity)) {
            return false; // null or other class
        }

        if (id != 0) {
            return id == ((AbstractEntity) obj).id;
        }
        return super.equals(obj);
    }

    public boolean isPersisted() {
        return id != 0;
    }
}
