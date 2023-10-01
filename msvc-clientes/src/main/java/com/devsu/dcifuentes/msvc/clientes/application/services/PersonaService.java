package com.devsu.dcifuentes.msvc.clientes.application.services;

import com.devsu.dcifuentes.msvc.clientes.application.ports.PersonaUseCase;
import com.devsu.dcifuentes.msvc.clientes.domain.entities.Persona;
import com.devsu.dcifuentes.msvc.clientes.domain.repositories.PersonaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PersonaService implements PersonaUseCase {

    private PersonaRepository repository;
    public PersonaService(ApplicationContext applicationContext) {
        this.repository = applicationContext.getBean(PersonaRepository.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Persona> listar() {
        return (List<Persona>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Persona> porId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Persona guardar(Persona persona) {
        return repository.save(persona);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
