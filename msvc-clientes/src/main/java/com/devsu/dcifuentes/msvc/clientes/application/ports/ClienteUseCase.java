package com.devsu.dcifuentes.msvc.clientes.application.ports;

import com.devsu.dcifuentes.msvc.clientes.adapters.http.dto.ClienteDto;
import com.devsu.dcifuentes.msvc.clientes.exceptions.ApiException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ClienteUseCase {
    List<ClienteDto> listar();
    Optional<ClienteDto> porId(Long id);
    Optional<ClienteDto> porNombre(String nombre);
    @Transactional(readOnly = true)
    Optional<ClienteDto> porNombreEIdentificacion(String nombre, String identificacion);
    ClienteDto guardar(ClienteDto cliente) throws ApiException;
    public ClienteDto actualizar(ClienteDto clienteDto) throws ApiException;
    void eliminar(Long id);
}
