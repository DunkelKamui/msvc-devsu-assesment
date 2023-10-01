package com.devsu.dcifuentes.msvc.cuentas.domain.repositories;

import com.devsu.dcifuentes.msvc.cuentas.domain.entities.Cuenta;
import org.springframework.data.repository.CrudRepository;

public interface CuentaRepository extends CrudRepository<Cuenta, String> {
    Iterable<Cuenta> findByClienteId(Long clienteId);
}
