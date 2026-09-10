package br.com.empresa.reunioes.web.controller.dto.Colaborador;

public record ColaboradorDTO(
        Long id,
        String nome,
        String email,
        Boolean monitorarReunioes,
        String dataCadastro) {
}