package com.devsu.dcifuentes.msvc.cuentas.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name="cuentas", indexes = {
        @Index(name="idx_cliente_id", columnList = "clienteId")
})
@Getter
@Setter
@ToString
public class Cuenta {
    @Id
    private String numeroCuenta;
    private String tipoCuenta;
    private Double saldoInicial;
    private boolean estado;
    private Long clienteId;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Cuenta cuenta = (Cuenta) o;
        return getNumeroCuenta() != null && Objects.equals(getNumeroCuenta(), cuenta.getNumeroCuenta());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
