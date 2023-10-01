package com.devsu.dcifuentes.msvc.cuentas.adapters.http;

import com.devsu.dcifuentes.msvc.cuentas.adapters.http.dto.CuentaDto;
import com.devsu.dcifuentes.msvc.cuentas.adapters.http.dto.MovimientoDto;
import com.devsu.dcifuentes.msvc.cuentas.adapters.httpClients.ClienteClientAdapter;
import com.devsu.dcifuentes.msvc.cuentas.application.ports.CuentaUseCase;
import com.devsu.dcifuentes.msvc.cuentas.adapters.httpClients.dto.ClienteDto;
import com.devsu.dcifuentes.msvc.cuentas.adapters.http.dto.RegistroDto;
import com.devsu.dcifuentes.msvc.cuentas.exceptions.ApiException;
import com.devsu.dcifuentes.msvc.cuentas.exceptions.BusinessException;
import com.devsu.dcifuentes.msvc.cuentas.exceptions.ResourceNotFoundException;
import feign.FeignException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {
    private final Logger logger = LogManager.getLogger(CuentaController.class);
    private final CuentaUseCase cuentaUseCase;
    private final ClienteClientAdapter clienteClientAdapter;

    public CuentaController(ApplicationContext applicationContext) {
        this.cuentaUseCase = applicationContext.getBean(CuentaUseCase.class);
        this.clienteClientAdapter = applicationContext.getBean(ClienteClientAdapter.class);
    }

    @GetMapping
    public ResponseEntity<List<CuentaDto>> listar() {
        return ResponseEntity.ok(cuentaUseCase.listar());
    }

    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<?> detalleCuenta(@PathVariable String numeroCuenta) {
        logger.info("Invocación a detalleCuenta()...");
        Optional<CuentaDto> optionalCuenta = cuentaUseCase.porId(numeroCuenta);
        if(optionalCuenta.isPresent()) {
            logger.info("Cuenta encontrada, devolviendo datos...");
            return ResponseEntity.ok(optionalCuenta.get());
        }
        throw new ResourceNotFoundException("Cuenta con numeroCuenta " + numeroCuenta + " no registrada.");
    }

    @PostMapping
    public ResponseEntity<?> crearCuenta(@RequestBody CuentaDto cuenta) throws ApiException {
        logger.info(String.format("Invocación a crearCuenta() con numeroCuenta[%s]...", cuenta.getNumeroCuenta()));
        Optional<CuentaDto> optionalCuenta = cuentaUseCase.porId(cuenta.getNumeroCuenta());
        if(optionalCuenta.isEmpty()) {
            logger.info("Enviando a crear cuenta con numeroCuenta:" + cuenta.getNumeroCuenta());
            return ResponseEntity.status(HttpStatus.CREATED).body(cuentaUseCase.guardar(cuenta));
        }
        throw new BusinessException("La cuenta solicitada a crear ya está regsitrada.");
    }

    @PutMapping("/{numeroCuenta}")
    public ResponseEntity<?> editarCuenta(@RequestBody CuentaDto cuenta, @PathVariable String numeroCuenta) throws ApiException {
        logger.info(String.format("Invocación a editarCuenta() con numeroCuenta[%s]...", cuenta.getNumeroCuenta()));
        Optional<CuentaDto> optionalCuenta = cuentaUseCase.porId(numeroCuenta);
        if (optionalCuenta.isPresent()) {
            CuentaDto cuentaDb = optionalCuenta.get();
            cuentaDb.setEstado(cuenta.isEstado());
            cuentaDb.setSaldoInicial(cuenta.getSaldoInicial());
            cuentaDb.setTipoCuenta(cuenta.getTipoCuenta());
            logger.info("Enviando a actualizar cuenta con numeroCuenta [" + numeroCuenta + "]");
            return ResponseEntity.status(HttpStatus.CREATED).body(cuentaUseCase.guardar(cuentaDb));
        }
        logger.info("Cuenta con numeroCuenta " + numeroCuenta + " no registrada.");
        throw new ResourceNotFoundException("Cuenta con numeroCuenta " + numeroCuenta + " no registrada.");
    }

    @PatchMapping("/{numeroCuenta}")
    public ResponseEntity<?> actualizarSaldo(@RequestBody CuentaDto cuenta, @PathVariable String numeroCuenta) throws ApiException {
        logger.info(String.format("Invocación a actualizarSaldo() con numeroCuenta[%s]...", cuenta.getNumeroCuenta()));
        Optional<CuentaDto> optionalCuenta = cuentaUseCase.porId(numeroCuenta);
        if (optionalCuenta.isPresent()) {
            CuentaDto cuentaDb = optionalCuenta.get();
            cuentaDb.setSaldoInicial(cuenta.getSaldoInicial());
            logger.info("Enviando a actualizar saldo a la cuenta [" + numeroCuenta + "]");
            return ResponseEntity.status(HttpStatus.CREATED).body(cuentaUseCase.guardar(cuentaDb));
        }
        logger.info("Cuenta con numeroCuenta " + numeroCuenta + " no registrada.");
        throw new ResourceNotFoundException("Cuenta con numeroCuenta " + numeroCuenta + " no registrada.");
    }

    @DeleteMapping("/{numeroCuenta}")
    public ResponseEntity<?> eliminarCuenta(@PathVariable String numeroCuenta) {
        logger.info(String.format("Invocación a eliminarCuenta() con numeroCuenta[%s]...", numeroCuenta));
        Optional<CuentaDto> optionalCuenta = cuentaUseCase.porId(numeroCuenta);
        if(optionalCuenta.isPresent()) {
            cuentaUseCase.eliminar(numeroCuenta);
            logger.info("Cuenta " + numeroCuenta + " eliminada.");
            return ResponseEntity.noContent().build();
        }
        logger.info("Cuenta con numeroCuenta " + numeroCuenta + " no registrada.");
        throw new ResourceNotFoundException("Cuenta con numeroCuenta " + numeroCuenta + " no registrada.");
    }

    @GetMapping("/reportes")
    public ResponseEntity<?> generarEstadoCuenta(@RequestParam String rangoFechas, @RequestParam Long clienteId) throws ApiException {
        logger.info(String.format("Invocación a generarEstadoCuenta() con idCliente[%d] en rangoFechas[%s]...", clienteId, rangoFechas));
        List<RegistroDto> result = new ArrayList<>();
        ClienteDto cliente;

        try {
            cliente = clienteClientAdapter.detalle(clienteId).getBody();
        } catch (FeignException fe)
        {
            logger.info(String.format("No se pudo obtener el detalle del cliente con Id [%d].", clienteId));
            if(fe.status() == HttpStatus.NOT_FOUND.value())
                    throw new ResourceNotFoundException(fe.getMessage());
            throw new ApiException(fe.getMessage());
        }

        if(cliente == null)
            throw new ApiException("Error al obtener el Id del cliente");
        logger.info(String.format("Cliente obtenido: [%s/%s].", cliente.getNombre(), cliente.getIdentificacion()));

        // obtener fecha inicio y fecha fin
        String[] fechas =  rangoFechas.split("-", 2);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date fechaIni, fechaFin;
        try {
            fechaIni = sdf.parse(fechas[0]);
            fechaFin = sdf.parse(fechas[1]);
        } catch (Exception e) {
            logger.error(String.format("Formato de fechas incorrecto [%s/%s], debe ser 'yyyyMMdd'.", fechas[0], fechas[1]));
            throw new ApiException("Rango ["+ rangoFechas +"] incorrecto, el formato del rango debe ser 'yyyyMMdd-yyyyMMdd'. Error: " + e.getMessage());
        }

        // Buscar los movimientos
        List<CuentaDto> cuentas;
        cuentas = cuentaUseCase.porClienteId(cliente.getId());

        for (CuentaDto cuenta : cuentas) {
            logger.info(String.format("Obteniendo movimientos de cuenta [%s].", cuenta.getNumeroCuenta()));
            List<MovimientoDto> movimientos = cuentaUseCase.generarEstadoCuenta(cuenta.getNumeroCuenta(), fechaIni, fechaFin);
            logger.info(String.format("%d Movimientos obtenidos en el rango indicado.", movimientos.size()));
            String nomCliente = cliente.getNombre();
            result.addAll(movimientos.stream()
                    .map(mov -> new RegistroDto(mov.getFecha(),
                            nomCliente,
                            cuenta.getNumeroCuenta(),
                            cuenta.getTipoCuenta(),
                            mov.getSaldo() - mov.getValor(),
                            cuenta.isEstado(),
                            mov.getValor(),
                            mov.getSaldo()))
                    .toList());
        }
        logger.info("Enviando reporte...");
        return ResponseEntity.ok(result);
    }
}
