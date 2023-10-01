package com.devsu.dcifuentes.msvc.cuentas.adapters.http.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CuentaDto {
    private String numeroCuenta;
    private String tipoCuenta;
    private Double saldoInicial;
    private boolean estado;
    private Long clienteId;

}
