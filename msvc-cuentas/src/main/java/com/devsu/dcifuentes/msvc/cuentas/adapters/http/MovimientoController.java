package com.devsu.dcifuentes.msvc.cuentas.adapters.http;

import com.devsu.dcifuentes.msvc.cuentas.adapters.http.dto.CuentaDto;
import com.devsu.dcifuentes.msvc.cuentas.application.ports.CuentaUseCase;
import com.devsu.dcifuentes.msvc.cuentas.application.ports.MovimientoUseCase;
import com.devsu.dcifuentes.msvc.cuentas.adapters.http.dto.MovimientoDto;
import com.devsu.dcifuentes.msvc.cuentas.exceptions.ApiException;
import com.devsu.dcifuentes.msvc.cuentas.exceptions.BusinessException;
import com.devsu.dcifuentes.msvc.cuentas.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movimientos")
@Slf4j
public class MovimientoController {
    private MovimientoUseCase movimientoUseCase;
    private CuentaUseCase cuentaUseCase;

    public MovimientoController(ApplicationContext applicationContext) {
        this.movimientoUseCase = applicationContext.getBean(MovimientoUseCase.class);
        this.cuentaUseCase = applicationContext.getBean(CuentaUseCase.class);
    }

    @GetMapping
    public ResponseEntity<List<MovimientoDto>> listar() {
        return ResponseEntity.ok(movimientoUseCase.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        Optional<MovimientoDto> optionalCuenta = movimientoUseCase.porId(id);
        if(!optionalCuenta.isPresent())
            throw new ResourceNotFoundException("El movimiento con ID " + id + " no existe.");

        return ResponseEntity.ok(optionalCuenta.get());
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody MovimientoDto movimientoDto) throws ApiException {

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(movimientoUseCase.guardar(movimientoDto));
        } catch (Exception ae) {
            throw new ApiException("No se pudo actualizar registrar el movimiento. La operación fue revertida, "
                    + "Error:" + ae.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@RequestBody MovimientoDto movimiento, @PathVariable Long id) throws ApiException {
        Optional<MovimientoDto> optionalMovimiento = movimientoUseCase.porId(id);
        if (!optionalMovimiento.isPresent())
            throw new ResourceNotFoundException("El movimiento con ID " + id + " no existe.");

        MovimientoDto movimientoDb = optionalMovimiento.get();
        Double valoractual = movimientoDb.getValor();
        Double saldoActual = movimientoDb.getSaldo();
        movimientoDb.setNumeroCuenta(movimiento.getNumeroCuenta());
        movimientoDb.setFecha(movimiento.getFecha());
        movimientoDb.setSaldo(movimiento.getSaldo());
        movimientoDb.setTipoMovimiento(movimiento.getTipoMovimiento());
        movimientoDb.setValor(movimiento.getValor());
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(movimientoUseCase.actualizar(movimientoDb, valoractual, saldoActual));
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) throws ApiException {
        Optional<MovimientoDto> optionalCuenta = movimientoUseCase.porId(id);
        if(!optionalCuenta.isPresent())
            throw new ResourceNotFoundException("El movimiento con ID " + id + " no existe.");

        try {
            movimientoUseCase.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }

    }
}
