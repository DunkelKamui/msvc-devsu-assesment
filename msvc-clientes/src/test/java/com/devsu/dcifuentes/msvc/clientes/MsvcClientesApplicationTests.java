package com.devsu.dcifuentes.msvc.clientes;

import com.devsu.dcifuentes.msvc.clientes.domain.entities.Cliente;
import com.devsu.dcifuentes.msvc.clientes.domain.repositories.ClienteRepository;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MsvcClientesApplicationTests {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private ClienteRepository clienteRepository;

	Cliente cliente = new Cliente();

	@Test
	void whenFindByName_thenReturnCliente_UnitTest() {
		cliente.setNombre("Angel Reyes");
		cliente.setGenero("Masculino");
		cliente.setEdad(37);
		cliente.setIdentificacion("43768048");
		cliente.setDireccion("Av. Nicolini 575, Urb. Palao, San Martin de Porres");
		cliente.setTelefono("988997707");
		cliente.setEstado(true);
		cliente.setContrasena("123456");

		entityManager.persistAndFlush(cliente);

		Optional<Cliente> encontrado = clienteRepository.findByNombre("Angel Reyes");
		assertThat(encontrado.get().getNombre().equals(cliente.getNombre()));

		// Actualizamos el ID para el @AfterEach
		cliente.setId(encontrado.get().getId());
	}

	@AfterEach
	void resetTest() {
		clienteRepository.deleteById(cliente.getId());
	}

}
