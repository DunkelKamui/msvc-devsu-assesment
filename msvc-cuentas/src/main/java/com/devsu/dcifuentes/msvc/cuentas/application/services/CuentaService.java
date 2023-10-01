package com.devsu.dcifuentes.msvc.cuentas.application.services;

import com.devsu.dcifuentes.msvc.cuentas.adapters.http.dto.CuentaDto;
import com.devsu.dcifuentes.msvc.cuentas.adapters.http.dto.MovimientoDto;
import com.devsu.dcifuentes.msvc.cuentas.application.ports.CuentaUseCase;
import com.devsu.dcifuentes.msvc.cuentas.application.ports.MovimientoUseCase;
import com.devsu.dcifuentes.msvc.cuentas.domain.entities.Cuenta;
import com.devsu.dcifuentes.msvc.cuentas.domain.entities.Movimiento;
import com.devsu.dcifuentes.msvc.cuentas.domain.repositories.CuentaRepository;
import com.devsu.dcifuentes.msvc.cuentas.domain.repositories.MovimientoRepository;
import com.devsu.dcifuentes.msvc.cuentas.exceptions.ApiException;
import com.devsu.dcifuentes.msvc.cuentas.exceptions.ResourceNotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CuentaService implements CuentaUseCase {

    private CuentaRepository repository;
    private MovimientoRepository movimientoRepository;
    public CuentaService(ApplicationContext applicationContext) {
        this.repository = applicationContext.getBean(CuentaRepository.class);
        this.movimientoRepository = applicationContext.getBean(MovimientoRepository.class);
    }
    @Override
    @Transactional(readOnly = true)
    public List<CuentaDto> listar() {
        List<CuentaDto> result = ((List<Cuenta>)repository.findAll()).stream()
                .map(cuenta -> new CuentaDto(
                        cuenta.getNumeroCuenta(),
                        cuenta.getTipoCuenta(),
                        cuenta.getSaldoInicial(),
                        cuenta.isEstado(),
                        cuenta.getClienteId()))
                .collect(Collectors.toList());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CuentaDto> porId(String numeroCuenta) {
        Optional<Cuenta> optionalCuenta = repository.findById(numeroCuenta);
        if(optionalCuenta.isEmpty())
            return Optional.empty();
        Cuenta cuentaDb = optionalCuenta.get();
        return Optional.of(convertToDto(cuentaDb));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaDto> porClienteId(Long id) {
        List<Cuenta> cuentas = (List<Cuenta>)repository.findByClienteId(id);
        if(cuentas.isEmpty())
            throw new ResourceNotFoundException("El cliente con id:" + id + " no tiene cuentas registradas.");
        List<CuentaDto> result = cuentas.stream()
                .map(cuenta -> new CuentaDto(
                        cuenta.getNumeroCuenta(),
                        cuenta.getTipoCuenta(),
                        cuenta.getSaldoInicial(),
                        cuenta.isEstado(),
                        cuenta.getClienteId()))
                .collect(Collectors.toList());
        return result;
    }

    @Override
    @Transactional
    public CuentaDto guardar(CuentaDto cuenta) throws ApiException {
        Cuenta cuentaEntity = new Cuenta();
        cuentaEntity.setNumeroCuenta(cuenta.getNumeroCuenta());
        cuentaEntity.setTipoCuenta(cuenta.getTipoCuenta());
        cuentaEntity.setSaldoInicial(cuenta.getSaldoInicial());
        cuentaEntity.setEstado(cuenta.isEstado());
        cuentaEntity.setClienteId(cuenta.getClienteId());
        try {
            repository.save(cuentaEntity);
        } catch (Exception e){
            throw new ApiException("No se pudo crear al cuenta. Error: " + e.getMessage());
        }
        return cuenta;
    }

    @Override
    @Transactional
    public void eliminar(String numeroCuenta) {
        repository.deleteById(numeroCuenta);
    }

    @Override
    @Transactional
    public CuentaDto actualizarSaldo(String numeroCuenta, Double nuevoSaldo) throws ApiException {
        Cuenta cuenta = repository.findById(numeroCuenta).get();
        cuenta.setSaldoInicial(nuevoSaldo);
        try {
            repository.save(cuenta);
        } catch (Exception e){
            throw new ApiException("No se pudo crear el cliente. Error: " + e.getMessage());
        }
        return convertToDto(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoDto> generarEstadoCuenta(String numeroCuenta, Date fechaInicio, Date fechaFin) {
        List<MovimientoDto> movimientosCuenta =
                ((List<Movimiento>)movimientoRepository.findByCuentaNumeroCuentaAndFechaGreaterThanEqualAndFechaLessThanEqual(numeroCuenta, fechaInicio, fechaFin))
                .stream()
                .map(movimiento -> new MovimientoDto(
                        movimiento.getId(),
                        movimiento.getFecha(),
                        movimiento.getTipoMovimiento(),
                        movimiento.getValor(),
                        movimiento.getSaldo(),
                        movimiento.getCuenta().getNumeroCuenta()))
                .collect(Collectors.toList());
        return movimientosCuenta;
    }

    private CuentaDto convertToDto(Cuenta clienteDb) {
        return new CuentaDto(
                clienteDb.getNumeroCuenta(),
                clienteDb.getTipoCuenta(),
                clienteDb.getSaldoInicial(),
                clienteDb.isEstado(),
                clienteDb.getClienteId());
    }
}