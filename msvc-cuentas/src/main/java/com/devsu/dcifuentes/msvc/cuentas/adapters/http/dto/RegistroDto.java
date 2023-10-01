package com.devsu.dcifuentes.msvc.cuentas.adapters.http.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class RegistroDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    Date fecha;
    String nombreCliente;
    String numeroCuenta;
    String tipoCuenta;
    Double saldoInicial;
    boolean estado;
    Double movimiento;
    Double saldoDisponible;
}
