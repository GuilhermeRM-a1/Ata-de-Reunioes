package br.com.empresa.reunioes.web.controller.dto.Colaborador;

public record ColaboradorDTO(
        Long id,
        String nome,
        String email,
        String papel,
        Boolean monitorarReunioes,
        String dataCadastro) {
}