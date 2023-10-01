package com.devsu.dcifuentes.msvc.clientes.application.ports;

import com.devsu.dcifuentes.msvc.clientes.domain.entities.Persona;

import java.util.List;
import java.util.Optional;

public interface PersonaUseCase {
    List<Persona> listar();
    Optional<Persona> porId(Long id);
    Persona guardar(Persona persona);
    void eliminar(Long id);
}
