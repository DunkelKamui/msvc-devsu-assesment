package com.devsu.dcifuentes.msvc.cuentas;

import static org.hamcrest.CoreMatchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devsu.dcifuentes.msvc.cuentas.adapters.http.dto.CuentaDto;
import com.devsu.dcifuentes.msvc.cuentas.adapters.http.dto.MovimientoDto;
import com.devsu.dcifuentes.msvc.cuentas.adapters.httpClients.ClienteClientAdapter;
import com.devsu.dcifuentes.msvc.cuentas.adapters.httpClients.dto.ClienteDto;
import com.devsu.dcifuentes.msvc.cuentas.domain.entities.Cuenta;
import com.devsu.dcifuentes.msvc.cuentas.domain.entities.Movimiento;
import com.devsu.dcifuentes.msvc.cuentas.domain.repositories.CuentaRepository;
import com.devsu.dcifuentes.msvc.cuentas.domain.repositories.MovimientoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		classes = MsvcCuentasApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
		locations = "classpath:application.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MsvcCuentasApplicationTests {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private  CuentaRepository cuentaRepository;

	@Autowired
	private  MovimientoRepository movimientoRepository;

	@Autowired
	private  ClienteClientAdapter clientAdapter;

	ClienteDto cliente = new ClienteDto();

	@Test
	public void IntegrationTest() throws Exception {
		cliente.setNombre("Angel Reyes");
		cliente.setGenero("Masculino");
		cliente.setEdad(37);
		cliente.setIdentificacion("43768048");
		cliente.setDireccion("Av. Nicolini 575, Urb. Palao, San Martin de Porres");
		cliente.setTelefono("988997707");
		cliente.setEstado(true);
		cliente.setContrasena("123456");
		cliente = clientAdapter.crear(cliente).getBody();
        assertThat(cliente != null);
        System.out.println("Cliente creado con Id:" + cliente.getId());

		// Test Creación de la cuenta
		CuentaDto cuenta = new CuentaDto();
		cuenta.setNumeroCuenta("123456");
		cuenta.setTipoCuenta("Ahorros");
		cuenta.setSaldoInicial(5000.0);
		cuenta.setEstado(true);
		cuenta.setClienteId(cliente.getId());

		ObjectMapper objectMapper = new ObjectMapper();
		String cuentaJson = objectMapper.writeValueAsString(cuenta);

		System.out.println("Enviando creación de cuenta: " + cliente.getId());
		mvc.perform(post("/cuentas")
				.contentType(MediaType.APPLICATION_JSON)
				.content(cuentaJson));

		System.out.println("Validando creación de cuenta:" + cliente.getId());
		List<Cuenta> found = (List<Cuenta>) cuentaRepository.findAll();
		assertThat(found).extracting(Cuenta::getNumeroCuenta).contains("123456");

		System.out.println("Iniciando creación de movimientos para cuenta " + cuenta.getNumeroCuenta());
		// Test de creación de movimientos
		MovimientoDto mov1 = new MovimientoDto();
		mov1.setNumeroCuenta(cuenta.getNumeroCuenta());
		mov1.setValor(-350.0);
		mov1.setTipoMovimiento("Retiro");
		String mov1Json = objectMapper.writeValueAsString(mov1);

		MovimientoDto mov2 = new MovimientoDto();
		mov2.setNumeroCuenta(cuenta.getNumeroCuenta());
		mov2.setValor(100.0);
		mov2.setTipoMovimiento("Depósito");
		String mov2Json = objectMapper.writeValueAsString(mov2);

		System.out.println("Enviando creación de movimiento 1 para cuenta " + cuenta.getNumeroCuenta());
		mvc.perform(post("/movimientos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mov1Json));

		System.out.println("Enviando creación de movimiento 2 para cuenta " + cuenta.getNumeroCuenta());
		mvc.perform(post("/movimientos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mov2Json));

		System.out.println("Iniciando generación de reporte para cuenta " + cuenta.getNumeroCuenta());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String url = String.format("/cuentas/reportes?rangoFechas=%s-%s&clienteId=%s",
				dateFormat.format(new Date()),
				dateFormat.format(new Date()),
				cliente.getId());
		// Test de generación de Reporte
		System.out.println("Solicitando generación de reporte para cuenta " + cuenta.getNumeroCuenta());
		mvc.perform(get(url)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
				.andExpect(jsonPath("$[0].tipoCuenta", is("Ahorros")))
				.andExpect(jsonPath("$[1].tipoCuenta", is("Ahorros")));
		// @formatter:on
	}

	@AfterAll
	public void resetTests()
	{
		System.out.println("Iniciando cleanup...");
		clientAdapter.eliminar(cliente.getId());
		List<Cuenta> cuentas = (List<Cuenta>)cuentaRepository.findByClienteId(cliente.getId());

		for (Cuenta cuenta : cuentas) {
			List<Movimiento> movimientos = (List<Movimiento>)movimientoRepository.findByCuentaNumeroCuenta(cuenta.getNumeroCuenta());
			movimientoRepository.deleteAll(movimientos);
			cuentaRepository.delete(cuenta);
		}
		System.out.println("Cleanup finalizado...");
	}

}
