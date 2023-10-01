package com.devsu.dcifuentes.msvc.clientes.adapters.http;

import com.devsu.dcifuentes.msvc.clientes.adapters.http.dto.ClienteDto;
import com.devsu.dcifuentes.msvc.clientes.exceptions.ApiException;
import com.devsu.dcifuentes.msvc.clientes.exceptions.BusinessException;
import com.devsu.dcifuentes.msvc.clientes.exceptions.ResourceNotFoundException;
import com.devsu.dcifuentes.msvc.clientes.application.ports.ClienteUseCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteUseCase clienteUseCase;
    private final Logger logger = LogManager.getLogger(ClienteController.class);

    public ClienteController(ApplicationContext applicationContext) {
        this.clienteUseCase = applicationContext.getBean(ClienteUseCase.class);
    }

    @GetMapping
    public ResponseEntity<List<ClienteDto>> listarClientes() {
        logger.info("Invocacion a método listarClientes...");
        List<ClienteDto> clientes = clienteUseCase.listar().stream()
                .map(cliente -> new ClienteDto(
                        cliente.getId(),
                        cliente.getNombre(),
                        cliente.getGenero(),
                        cliente.getEdad(),
                        cliente.getIdentificacion(),
                        cliente.getDireccion(),
                        cliente.getTelefono(),
                        cliente.getContrasena(),
                        cliente.isEstado()))
                .collect(Collectors.toList());
        logger.info(String.format("Respondiendo Ok con %d registros...", clientes.size()));
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalleCliente(@PathVariable String id) {
        // Verificar si el criterio de búsqueda es numérico o alfabético:
        logger.info("Invocacion a método detalleCliente...");
        long idCliente = 0L;
        boolean busquedaPorNombre = false;
        try {
            idCliente = Long.parseLong(id);
            logger.info(String.format("Se realiza la búsqueda por id [%d]", idCliente));
        } catch (NumberFormatException nfe) {
            busquedaPorNombre = true;
            logger.info(String.format("Se realiza la búsqueda por nombre [%s]", id));
        }

        Optional<ClienteDto> optionalCliente;
        if(busquedaPorNombre)
            optionalCliente = clienteUseCase.porNombre(id);
        else
            optionalCliente = clienteUseCase.porId(idCliente);
        if(optionalCliente.isPresent()) {
            logger.info(String.format("Devolviendo Ok con idCliente [%s]", optionalCliente.get().getId()));
            return ResponseEntity.ok(optionalCliente.get());
        }
        else
            throw new ResourceNotFoundException("Cliente con id/nombre = [" + id + "], no ha sido encontrado.");
    }

    @PostMapping
    public ResponseEntity<?> crearCliente(@RequestBody ClienteDto cliente) throws ApiException {
        logger.info("Invocacion a método crearCliente...");
        Optional<ClienteDto> optionalCliente = clienteUseCase.porNombreEIdentificacion(
                cliente.getNombre(),
                cliente.getIdentificacion());
        if (optionalCliente.isPresent())
            throw new BusinessException(String.format(
                    "Cliente con nombre [%s] e identificación [%s] ya se encuentra registrado.",
                    cliente.getNombre(), cliente.getIdentificacion()));
        logger.info(String.format("Se envía a guardar cliente con nombre/identificación [%s/%s]",
                cliente.getNombre(), cliente.getIdentificacion()));
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteUseCase.guardar(cliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(@RequestBody ClienteDto cliente, @PathVariable Long id) throws ApiException {
        logger.info("Invocacion a método actualizarCliente...");
        Optional<ClienteDto> optionalCliente = clienteUseCase.porId(id);
        if (optionalCliente.isEmpty())
            throw new ResourceNotFoundException("Cliente con Id "+ id + " no encontrado.");

        ClienteDto clienteDb = optionalCliente.get();
        clienteDb.setNombre(cliente.getNombre());
        clienteDb.setGenero(cliente.getGenero());
        clienteDb.setEdad(cliente.getEdad());
        clienteDb.setIdentificacion(cliente.getIdentificacion());
        clienteDb.setTelefono(cliente.getTelefono());
        clienteDb.setEstado(cliente.isEstado());
        clienteDb.setContrasena(cliente.getContrasena());
        logger.info(String.format("Se envía a guardar cliente con idCliente [%d]", optionalCliente.get().getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteUseCase.guardar(clienteDb));
    }

    @PatchMapping("/actualizarNombre/{id}/{nuevoNombre}")
    public ResponseEntity<?> actualizarNombre(@PathVariable String nuevoNombre, @PathVariable Long id) throws ApiException {
        logger.info("Invocacion a método actualizarNombre...");
        Optional<ClienteDto> optionalCliente = clienteUseCase.porId(id);
        if (optionalCliente.isEmpty())
            throw new ResourceNotFoundException("Cliente con Id "+ id + " no encontrado.");

        ClienteDto clienteDb = optionalCliente.get();
        clienteDb.setNombre(nuevoNombre);
        logger.info(String.format("Se envía a actualizar cliente con idCliente [%d] con nombre [%s]", id, nuevoNombre));
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteUseCase.guardar(clienteDb));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        logger.info("Invocacion a método eliminarCliente...");
        Optional<ClienteDto> optionalCliente = clienteUseCase.porId(id);
        if(optionalCliente.isPresent()) {
            clienteUseCase.eliminar(id);
            logger.info(String.format("Cliente con idCliente [%d] eliminado", id));
            return ResponseEntity.noContent().build();
        }
        logger.info("No se pudo eliminar al cliente...");
        throw new ResourceNotFoundException("Cliente con Id "+ id + " no encontrado.");
    }
}
