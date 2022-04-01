package cz.wildwest.zaurex.data.entity;

import cz.wildwest.zaurex.data.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "configuration_")
public class Configuration extends AbstractEntity {

    @NotNull
    @Column(name = "key_")
    private String key;

    @NotNull
    @Column(name = "value_")
    private String value;

    public Configuration() {
    }

    public Configuration(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Configuration(StandardKey key, String value) {
        this(key.key, value);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public enum StandardKey {
        ICO("ic"), BANK_ACCOUNT_NUMBER("bank_accout_number");

        private final String key;

        StandardKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}
