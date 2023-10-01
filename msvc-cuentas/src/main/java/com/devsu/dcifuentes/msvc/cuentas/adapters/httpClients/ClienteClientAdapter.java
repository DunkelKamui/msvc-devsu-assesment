package com.devsu.dcifuentes.msvc.cuentas.adapters.httpClients;

import com.devsu.dcifuentes.msvc.cuentas.adapters.httpClients.dto.ClienteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="msvc-clientes", url="${msvc-clientes.url}:8001/clientes")
public interface ClienteClientAdapter {
    @GetMapping("/{id}")
    ResponseEntity<ClienteDto> detalle(@PathVariable Long id);

    @PostMapping
    ResponseEntity<ClienteDto> crear(@RequestBody ClienteDto cliente);

    @DeleteMapping("/{id}")
    ResponseEntity<?> eliminar(@PathVariable Long id);
}
