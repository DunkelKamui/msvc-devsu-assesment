package com.devsu.dcifuentes.msvc.cuentas.application.ports;

import com.devsu.dcifuentes.msvc.cuentas.adapters.http.dto.CuentaDto;
import com.devsu.dcifuentes.msvc.cuentas.adapters.http.dto.MovimientoDto;
import com.devsu.dcifuentes.msvc.cuentas.exceptions.ApiException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CuentaUseCase {
    List<CuentaDto> listar(); // Read
    Optional<CuentaDto> porId(String numeroCuenta); // Read
    List<CuentaDto> porClienteId(Long id); // Read
    CuentaDto guardar(CuentaDto cuenta) throws ApiException; // Create/Update
    void eliminar(String numeroCuenta); // Delete
    CuentaDto actualizarSaldo(String numeroCuenta, Double nuevoSaldo) throws ApiException;
    List<MovimientoDto> generarEstadoCuenta(String numeroCuenta, Date fechaInicio, Date fechaFin);
}
