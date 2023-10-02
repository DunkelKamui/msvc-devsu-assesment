package com.devsu.dcifuentes.msvc.cuentas.application.services;

import com.devsu.dcifuentes.msvc.cuentas.adapters.http.dto.MovimientoDto;
import com.devsu.dcifuentes.msvc.cuentas.application.ports.MovimientoUseCase;
import com.devsu.dcifuentes.msvc.cuentas.domain.entities.Cuenta;
import com.devsu.dcifuentes.msvc.cuentas.domain.entities.Movimiento;
import com.devsu.dcifuentes.msvc.cuentas.domain.repositories.CuentaRepository;
import com.devsu.dcifuentes.msvc.cuentas.domain.repositories.MovimientoRepository;
import com.devsu.dcifuentes.msvc.cuentas.exceptions.ApiException;
import com.devsu.dcifuentes.msvc.cuentas.exceptions.BusinessException;
import com.devsu.dcifuentes.msvc.cuentas.exceptions.ResourceNotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovimientoService implements MovimientoUseCase {
    private final MovimientoRepository repository;
    private final CuentaRepository cuentaRepository;

    public MovimientoService(ApplicationContext applicationContext) {
        this.repository = applicationContext.getBean(MovimientoRepository.class);
        this.cuentaRepository = applicationContext.getBean(CuentaRepository.class);
    }
    @Override
    @Transactional(readOnly = true)
    public List<MovimientoDto> listar() {
        return ((List<Movimiento>) repository.findAll()).stream()
                .map(movimiento -> new MovimientoDto(
                    movimiento.getId(),
                    movimiento.getFecha(),
                    movimiento.getTipoMovimiento(),
                    movimiento.getValor(),
                    movimiento.getSaldo(),
                    movimiento.getCuenta().getNumeroCuenta()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MovimientoDto> porId(Long id) {
        Optional<Movimiento> optionalMovimiento = repository.findById(id);
        if(optionalMovimiento.isEmpty())
            throw new ResourceNotFoundException("Movimiento con Id " + id + " no encontrado.");
        Movimiento movimientoDb = optionalMovimiento.get();
        return Optional.of(convertToDto(movimientoDb));
    }

    @Override
    @Transactional
    public MovimientoDto guardar(MovimientoDto movimiento) throws ApiException {

        Optional<Cuenta> optionalCuenta = cuentaRepository.findById(movimiento.getNumeroCuenta());
        if(optionalCuenta.isEmpty())
            throw new ResourceNotFoundException("La cuenta " + movimiento.getNumeroCuenta() + " no existe");

        Cuenta cuentaDb = optionalCuenta.get();
        double nuevoSaldo = cuentaDb.getSaldoInicial() + movimiento.getValor();
        // consulta saldo y actualizar la cuenta:
        if (nuevoSaldo < 0) {
            throw new BusinessException("Saldo no disponible.");
        }

        try {
            cuentaDb.setSaldoInicial(nuevoSaldo);
            cuentaRepository.save(cuentaDb);
        } catch (Exception ae) {
            throw new ApiException("No se pudo actualizar los saldos: " + ae.getMessage());
        }

        // Guardamos el movimiento
        movimiento.setNumeroCuenta(cuentaDb.getNumeroCuenta());
        movimiento.setFecha(Date.from(Instant.now()));
        movimiento.setSaldo(cuentaDb.getSaldoInicial());
        movimiento.setTipoMovimiento(movimiento.getTipoMovimiento());
        movimiento.setValor(movimiento.getValor());

        Movimiento movimientoEntity = getMovimiento(movimiento, cuentaDb);
        try {
            Movimiento movimientoDb = repository.save(movimientoEntity);
            movimiento.setId(movimientoDb.getId());
        } catch (Exception e){
            Double saldoRevertido = cuentaDb.getSaldoInicial() - movimiento.getValor();
            cuentaDb.setSaldoInicial(saldoRevertido);
            cuentaRepository.save(cuentaDb);
            throw new ApiException("No se pudo registrar el movimiento. Error: " + e.getMessage());
        }
        return movimiento;
    }

    @Override
    @Transactional
    public MovimientoDto actualizar(MovimientoDto movimiento, Double valorActual, Double saldoActual) throws ApiException {

        Optional<Cuenta> optionalCuenta = cuentaRepository.findById(movimiento.getNumeroCuenta());
        if(optionalCuenta.isEmpty())
            throw new ResourceNotFoundException("La cuenta " + movimiento.getNumeroCuenta() + " no existe");

        Cuenta cuentaDb = optionalCuenta.get();
        double nuevoSaldo = cuentaDb.getSaldoInicial() - valorActual + movimiento.getValor();
        // consulta saldo y actualizar la cuenta:
        if (nuevoSaldo < 0) {
            throw new BusinessException("Saldo no disponible.");
        }

        try {
            cuentaDb.setSaldoInicial(nuevoSaldo);
            cuentaRepository.save(cuentaDb);
        } catch (Exception ae) {
            throw new ApiException("No se pudo actualizar los saldos: " + ae.getMessage());
        }

        // Guardamos el movimiento
        movimiento.setNumeroCuenta(cuentaDb.getNumeroCuenta());
        movimiento.setFecha(Date.from(Instant.now()));
        movimiento.setSaldo(saldoActual - valorActual + movimiento.getValor());
        movimiento.setTipoMovimiento(movimiento.getTipoMovimiento());
        movimiento.setValor(movimiento.getValor());

        Movimiento movimientoEntity = getMovimiento(movimiento, cuentaDb);
        movimientoEntity.setId(movimiento.getId());
        try {
            Movimiento movimientoDb = repository.save(movimientoEntity);
            movimiento.setId(movimientoDb.getId());
        } catch (Exception e){
            cuentaDb.setSaldoInicial(saldoActual);
            cuentaRepository.save(cuentaDb);
            throw new ApiException("No se pudo registrar el movimiento. Error: " + e.getMessage());
        }
        return movimiento;
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoDto> listarEstadoCuenta(String numeroCuenta, Date fechaIni, Date fechaFin) {
        return ((List<Movimiento>)repository.findByCuentaNumeroCuentaAndFechaGreaterThanEqualAndFechaLessThanEqual(numeroCuenta, fechaIni, fechaFin))
                .stream()
                .map(movimiento -> new MovimientoDto(
                        movimiento.getId(),
                        movimiento.getFecha(),
                        movimiento.getTipoMovimiento(),
                        movimiento.getValor(),
                        movimiento.getSaldo(),
                        movimiento.getCuenta().getNumeroCuenta()))
                .collect(Collectors.toList());
    }

    private MovimientoDto convertToDto(Movimiento movimientoDb) {
        return new MovimientoDto(
                movimientoDb.getId(),
                movimientoDb.getFecha(),
                movimientoDb.getTipoMovimiento(),
                movimientoDb.getValor(),
                movimientoDb.getSaldo(),
                movimientoDb.getCuenta().getNumeroCuenta());
    }

    private Movimiento getMovimiento(MovimientoDto movimiento, Cuenta cuentaDb) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(cuentaDb.getNumeroCuenta());
        cuenta.setTipoCuenta(cuentaDb.getTipoCuenta());
        cuenta.setSaldoInicial(cuentaDb.getSaldoInicial());
        cuenta.setEstado(cuentaDb.isEstado());
        cuenta.setClienteId(cuentaDb.getClienteId());

        Movimiento movimientoEntity= new Movimiento();
        movimientoEntity.setFecha(movimiento.getFecha());
        movimientoEntity.setTipoMovimiento(movimiento.getTipoMovimiento());
        movimientoEntity.setValor(movimiento.getValor());
        movimientoEntity.setSaldo(movimiento.getSaldo());
        movimientoEntity.setCuenta(cuenta);
        return movimientoEntity;
    }
}
