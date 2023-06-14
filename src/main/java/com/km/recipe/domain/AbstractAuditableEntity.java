package com.km.recipe.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class AbstractAuditableEntity extends AbstractEntity {

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreationTimestamp
    @NotNull
    protected Instant createdDate;

    @Column(name = "updated_date", nullable = false)
    @UpdateTimestamp
    @NotNull
    protected Instant updatedDate;

    @Version
    @Column
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractAuditableEntity)) {
            return false;
        }
        return id != null && id.equals(((AbstractAuditableEntity) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
