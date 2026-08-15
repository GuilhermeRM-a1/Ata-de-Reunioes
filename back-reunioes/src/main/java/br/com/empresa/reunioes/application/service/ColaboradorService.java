package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.domain.model.Colaborador;
import br.com.empresa.reunioes.domain.model.Reuniao;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;

    public Colaborador salvar(ColaboradorRequest request) {
        Colaborador colaborador = new Colaborador();
        colaborador.setNome(request.nome());
        colaborador.setSenha(request.senha());
        colaborador.setMonitorarReunioes(request.monitorarReunioes());
        colaborador.setDataCadastro(request.dataCadastro());

        return this.colaboradorRepository.save(colaborador);
    }

    public Colaborador buscarPorId(Long id) {
        Colaborador colaborador = colaboradorRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "ID não encontrado!"));

        return colaborador;
    }

    public List<Colaborador> listar() {
        return colaboradorRepository.findAll();
    }

    public Colaborador atualizar(Long id, ColaboradorRequest request) {
        Colaborador colaborador = buscarPorId(id);
        colaborador.setNome(request.nome());
        colaborador.setMonitorarReunioes(request.monitorarReunioes());
        colaborador.setDataCadastro(request.dataCadastro());

        return colaboradorRepository.save(colaborador);
    }

    public Colaborador atualizarParcial(Long id, ColaboradorRequest request) {

         Colaborador colaborador = buscarPorId(id);

        if(request.nome() != null)
            colaborador.setNome(request.nome());
        if(request.monitorarReunioes() != null)
            colaborador.setMonitorarReunioes(request.monitorarReunioes());
        if(request.dataCadastro() != null)
            colaborador.setDataCadastro(request.dataCadastro());

        return colaboradorRepository.save(colaborador);
    }

    public void deletar(Long id) {
        Colaborador colaborador = buscarPorId(id);
        colaboradorRepository.delete(colaborador);
    }

}
