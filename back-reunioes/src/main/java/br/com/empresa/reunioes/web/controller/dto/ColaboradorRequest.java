package br.com.empresa.reunioes.web.controller.dto;

public record ColaboradorRequest(String email,
                                 String nome,
                                 Boolean monitorarReunioes,
                                 String dataCadastro) {}
