package com.devsu.dcifuentes.msvc.clientes.application.services;

import com.devsu.dcifuentes.msvc.clientes.adapters.http.dto.ClienteDto;
import com.devsu.dcifuentes.msvc.clientes.application.ports.ClienteUseCase;
import com.devsu.dcifuentes.msvc.clientes.domain.entities.Cliente;
import com.devsu.dcifuentes.msvc.clientes.domain.repositories.ClienteRepository;
import com.devsu.dcifuentes.msvc.clientes.exceptions.ApiException;
import com.devsu.dcifuentes.msvc.clientes.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClienteService implements ClienteUseCase {
    private ClienteRepository repository;
    public ClienteService(ApplicationContext applicationContext) {
        this.repository = applicationContext.getBean(ClienteRepository.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDto> listar() {
        List<ClienteDto> result = ((List<Cliente>)repository.findAll()).stream()
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
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteDto> porId(Long id) {
        Optional<Cliente> optionalCliente = repository.findById(id);
        if(!optionalCliente.isPresent())
            throw new ResourceNotFoundException("Cliente con Id " + id + " no encontrado.");
        Cliente clienteDb = optionalCliente.get();
        return Optional.of(convertToDto(clienteDb));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteDto> porNombre(String nombre) {
        Optional<Cliente> optionalCliente = repository.findByNombre(nombre);
        if(!optionalCliente.isPresent())
            throw new ResourceNotFoundException("Cliente con nombre " + nombre + " no encontrado.");
        Cliente clienteDb = optionalCliente.get();
        return Optional.of(convertToDto(clienteDb));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteDto> porNombreEIdentificacion(String nombre, String identificacion) {
        Optional<Cliente> optionalCliente = repository.findByNombreAndIdentificacion(nombre, identificacion);
        if(!optionalCliente.isPresent())
            return Optional.empty();
        Cliente clienteDb = optionalCliente.get();
        return Optional.of(convertToDto(clienteDb));
    }

    @Override
    @Transactional
    public ClienteDto guardar(ClienteDto clienteDto) throws ApiException {
        Cliente clienteEntity = new Cliente();
        clienteEntity.setNombre(clienteDto.getNombre());
        clienteEntity.setGenero(clienteDto.getGenero());
        clienteEntity.setEdad(clienteDto.getEdad());
        clienteEntity.setIdentificacion(clienteDto.getIdentificacion());
        clienteEntity.setDireccion(clienteDto.getDireccion());
        clienteEntity.setTelefono(clienteDto.getTelefono());
        clienteEntity.setContrasena(clienteDto.getContrasena());
        clienteEntity.setEstado(clienteDto.isEstado());
        try {
            clienteEntity = repository.save(clienteEntity);
            clienteDto.setId(clienteEntity.getId());
        } catch (Exception e){
            throw new ApiException("No se pudo crear el cliente. Error: " + e.getMessage());
        }
        return clienteDto;
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    private ClienteDto convertToDto(Cliente clienteDb) {
        return new ClienteDto(
                clienteDb.getId(),
                clienteDb.getNombre(),
                clienteDb.getGenero(),
                clienteDb.getEdad(),
                clienteDb.getIdentificacion(),
                clienteDb.getDireccion(),
                clienteDb.getTelefono(),
                clienteDb.getContrasena(),
                clienteDb.isEstado());
    }
}
