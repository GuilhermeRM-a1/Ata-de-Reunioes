package br.com.empresa.reunioes.application.colaborador.mapper;

import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.web.colaborador.dto.ColaboradorPatchRequest;
import br.com.empresa.reunioes.web.colaborador.dto.ColaboradorRequest;
import org.springframework.stereotype.Component;

@Component
public class ColaboradorMapper {

    public static Colaborador colaboradorToEntity(ColaboradorRequest request) {
        Colaborador colaborador = new Colaborador();

        colaborador.setNome(request.nome());
        colaborador.setEmail(request.email());
        colaborador.setSenha(request.senha());
        colaborador.setPapel(request.papel());
        colaborador.setMonitorarReunioes(request.monitorarReunioes());
        colaborador.setDataCadastro(request.dataCadastro());

        return colaborador;
    }


    public static void colaboradorUpdateEntity(Colaborador colaborador, ColaboradorRequest request) {
        colaborador.setNome(request.nome());
        colaborador.setEmail(request.email());
        colaborador.setSenha(request.senha());
        colaborador.setPapel(request.papel());
        colaborador.setMonitorarReunioes(request.monitorarReunioes());
        colaborador.setDataCadastro(request.dataCadastro());
    }

    public static void colaboradorUpdateParcialEntity(Colaborador colaborador, ColaboradorPatchRequest request) {

        if (request.nome() != null)
            colaborador.setNome(request.nome());
        if (request.email() != null)
            colaborador.setEmail(request.email());
        if (request.senha() != null)
            colaborador.setSenha(request.senha());
        if (request.papel() != null)
            colaborador.setPapel(request.papel());
        if (request.monitorarReunioes() != null)
            colaborador.setMonitorarReunioes(request.monitorarReunioes());
        if (request.dataCadastro() != null)
            colaborador.setDataCadastro(request.dataCadastro());
    }
}