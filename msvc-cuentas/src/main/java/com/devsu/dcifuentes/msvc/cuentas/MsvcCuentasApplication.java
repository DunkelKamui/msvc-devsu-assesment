package com.devsu.dcifuentes.msvc.cuentas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsvcCuentasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcCuentasApplication.class, args);
	}

}
