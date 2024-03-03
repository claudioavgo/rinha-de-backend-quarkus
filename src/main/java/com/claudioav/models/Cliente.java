package com.claudioav.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Cacheable
@RegisterForReflection
public class Cliente extends PanacheEntity {
    public Integer saldo;
    public Integer limite;

    public Cliente(Integer saldo, Integer limite) {
        this.saldo = saldo;
        this.limite = limite;
    }

    public Cliente() {
    }
}
