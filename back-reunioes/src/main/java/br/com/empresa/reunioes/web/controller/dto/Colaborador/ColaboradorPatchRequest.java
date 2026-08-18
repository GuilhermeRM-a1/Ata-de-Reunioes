package br.com.empresa.reunioes.web.controller.dto.Colaborador;

import jakarta.validation.constraints.Pattern;

/**
 * Corpo do PATCH. Diferente do ColaboradorRequest, aqui nenhum campo e
 * obrigatorio: nulo continua significando "nao mexer". O que se cobra e que o
 * campo enviado tenha conteudo — @Pattern aceita nulo e barra vazio ou so
 * espaco, que antes passavam direto para o banco.
 */
public record ColaboradorPatchRequest(@Pattern(regexp = ".*\\S.*", message = "O nome não pode ser vazio")
                                      String nome,

                                      String senha,

                                      Boolean monitorarReunioes,

                                      @Pattern(regexp = ".*\\S.*", message = "A data de cadastro não pode ser vazia")
                                      String dataCadastro) {}
