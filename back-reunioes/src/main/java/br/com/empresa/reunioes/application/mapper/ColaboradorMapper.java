package br.com.empresa.reunioes.application.mapper;

import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorDTO;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorPatchRequest;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorRequest;
import org.springframework.stereotype.Component;

@Component
public class ColaboradorMapper {

    public Colaborador toEntity(ColaboradorRequest request) {
        Colaborador colaborador = new Colaborador();

        colaborador.setNome(request.nome());
        colaborador.setEmail(request.email());
        colaborador.setSenha(request.senha());
        colaborador.setMonitorarReunioes(request.monitorarReunioes());
        colaborador.setDataCadastro(request.dataCadastro());

        return colaborador;
    }

    public ColaboradorDTO toDTO(Colaborador colaborador) {

        return new ColaboradorDTO(
                colaborador.getId(),
                colaborador.getNome(),
                colaborador.getEmail(),
                colaborador.getMonitorarReunioes(),
                colaborador.getDataCadastro()
        );
    }

    public void updateEntity(Colaborador colaborador, ColaboradorRequest request) {
        colaborador.setNome(request.nome());
        colaborador.setEmail(request.email());
        colaborador.setMonitorarReunioes(request.monitorarReunioes());
        colaborador.setDataCadastro(request.dataCadastro());
    }

    public void updateParsiEntity(Colaborador colaborador, ColaboradorPatchRequest request) {

        if (request.nome() != null)
            colaborador.setNome(request.nome());
        if (request.email() != null)
            colaborador.setEmail(request.email());
        if (request.monitorarReunioes() != null)
            colaborador.setMonitorarReunioes(request.monitorarReunioes());
        if (request.dataCadastro() != null)
            colaborador.setDataCadastro(request.dataCadastro());
    }
}