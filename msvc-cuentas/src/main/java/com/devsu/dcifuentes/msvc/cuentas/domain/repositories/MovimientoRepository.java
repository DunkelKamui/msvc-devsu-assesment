package com.devsu.dcifuentes.msvc.cuentas.domain.repositories;

import com.devsu.dcifuentes.msvc.cuentas.domain.entities.Movimiento;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public interface MovimientoRepository extends CrudRepository<Movimiento, Long> {
    Iterable<Movimiento> findByCuentaNumeroCuentaAndFechaGreaterThanEqualAndFechaLessThanEqual(String numeroCuenta, Date fechaIni, Date fechaFin);
    Iterable<Movimiento> findByCuentaNumeroCuenta(String numeroCuenta);
}
