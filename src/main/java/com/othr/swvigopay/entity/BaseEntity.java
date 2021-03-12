package com.othr.swvigopay.entity;

import com.sun.istack.NotNull;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity {

    // Attributes
    // -----------------------------------------------------
    @Id
    @NotNull
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    // Methods
    // -----------------------------------------------------
    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BaseEntity))
            return false;
        BaseEntity that = (BaseEntity) o;

        return this.id == that.getId();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getId());
    }
}
