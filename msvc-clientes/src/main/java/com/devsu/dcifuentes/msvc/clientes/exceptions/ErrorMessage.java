package com.devsu.dcifuentes.msvc.clientes.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ErrorMessage {
    private int estadoHttp;
    private Date fechaHora;
    private String mensaje;
    private String descripcion;
}