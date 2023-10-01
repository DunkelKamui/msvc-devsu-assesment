package com.devsu.dcifuentes.msvc.clientes.domain.repositories;

import com.devsu.dcifuentes.msvc.clientes.domain.entities.Persona;
import org.springframework.data.repository.CrudRepository;

public interface PersonaRepository extends CrudRepository<Persona, Long> {
}
