package br.com.empresa.reunioes.web.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Um feriado como a BrasilAPI devolve.
 *
 * Exemplo do retorno:
 * {"date":"2026-01-01","name":"Confraternização mundial","type":"national","weekday":"quinta-feira"}
 *
 * JsonIgnoreProperties porque o contrato e de terceiro: se a BrasilAPI
 * acrescentar campo, a desserializacao nao pode quebrar.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FeriadoDTO(String date,
                         String name,
                         String type,
                         String weekday) {
}
