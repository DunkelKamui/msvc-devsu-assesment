package com.devsu.dcifuentes.msvc.cuentas.adapters.http.dto;

import com.devsu.dcifuentes.msvc.cuentas.domain.entities.Cuenta;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoDto {
    private Long id;
    private Date fecha;
    private String tipoMovimiento;
    private Double valor;
    private Double saldo;
    private String numeroCuenta;
}
