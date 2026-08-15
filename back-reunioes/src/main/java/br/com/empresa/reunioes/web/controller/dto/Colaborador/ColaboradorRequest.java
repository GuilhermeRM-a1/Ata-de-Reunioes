package br.com.empresa.reunioes.web.controller.dto.Colaborador;

public record ColaboradorRequest(String nome,
                                 String senha,
                                 Boolean monitorarReunioes,
                                 String dataCadastro) {}
