package com.devsu.dcifuentes.msvc.cuentas.application.ports;

import com.devsu.dcifuentes.msvc.cuentas.adapters.http.dto.MovimientoDto;
import com.devsu.dcifuentes.msvc.cuentas.exceptions.ApiException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MovimientoUseCase {
    List<MovimientoDto> listar();
    Optional<MovimientoDto> porId(Long id);
    MovimientoDto guardar(MovimientoDto movimiento) throws ApiException;
    MovimientoDto actualizar(MovimientoDto movimiento, Double valorActual, Double saldoActual) throws ApiException;
    void eliminar(Long id);
    List<MovimientoDto> listarEstadoCuenta(String numeroCuenta, Date fechaIni, Date fechaFin);
}
