package cz.wildwest.zaurex.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.wildwest.zaurex.data.AbstractEntity;
import cz.wildwest.zaurex.data.Role;

import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User extends AbstractEntity {

    public User() {
        registeredSince = LocalDateTime.now();
    }

    @Size(min = 4, max = 50)
    @NotBlank
    private String username;

    @Size(min = 4, max = 50)
    @NotBlank
    private String name;

    @JsonIgnore
    private String hashedPassword;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @NotNull
    private LocalDateTime registeredSince;

    private LocalDateTime lastLogIn;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public LocalDateTime getRegisteredSince() {
        return registeredSince;
    }

    public LocalDateTime getLastLogIn() {
        return lastLogIn;
    }

    public void setLastLogIn(LocalDateTime lastLogIn) {
        this.lastLogIn = lastLogIn;
    }
}
