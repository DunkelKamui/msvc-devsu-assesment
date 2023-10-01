package com.devsu.dcifuentes.msvc.clientes.domain.repositories;

import com.devsu.dcifuentes.msvc.clientes.domain.entities.Cliente;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClienteRepository extends CrudRepository<Cliente, Long> {
    Optional<Cliente> findByNombre(String nombre);
    Optional<Cliente> findByNombreAndIdentificacion(String nombre, String identificacion);
}
