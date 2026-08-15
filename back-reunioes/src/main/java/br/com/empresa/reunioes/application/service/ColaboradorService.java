package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.domain.model.Colaborador;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorDTO;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Todo metodo publico devolve DTO. O campo senha fica na entidade e nunca
 * chega a camada web, porque ColaboradorDTO nao o carrega.
 */
@Service
@RequiredArgsConstructor
public class ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;

    public ColaboradorDTO salvar(ColaboradorRequest request) {
        Colaborador colaborador = new Colaborador();
        colaborador.setNome(request.nome());
        colaborador.setSenha(request.senha());
        colaborador.setMonitorarReunioes(request.monitorarReunioes());
        colaborador.setDataCadastro(request.dataCadastro());

        return ColaboradorDTO.de(this.colaboradorRepository.save(colaborador));
    }

    public ColaboradorDTO buscarPorId(Long id) {
        return ColaboradorDTO.de(buscarEntidade(id));
    }

    public List<ColaboradorDTO> listar() {
        return colaboradorRepository.findAll().stream()
                .map(ColaboradorDTO::de)
                .toList();
    }

    public ColaboradorDTO atualizar(Long id, ColaboradorRequest request) {
        Colaborador colaborador = buscarEntidade(id);
        colaborador.setNome(request.nome());
        colaborador.setMonitorarReunioes(request.monitorarReunioes());
        colaborador.setDataCadastro(request.dataCadastro());

        return ColaboradorDTO.de(colaboradorRepository.save(colaborador));
    }

    public ColaboradorDTO atualizarParcial(Long id, ColaboradorRequest request) {

         Colaborador colaborador = buscarEntidade(id);

        if(request.nome() != null)
            colaborador.setNome(request.nome());
        if(request.monitorarReunioes() != null)
            colaborador.setMonitorarReunioes(request.monitorarReunioes());
        if(request.dataCadastro() != null)
            colaborador.setDataCadastro(request.dataCadastro());

        return ColaboradorDTO.de(colaboradorRepository.save(colaborador));
    }

    public void deletar(Long id) {
        Colaborador colaborador = buscarEntidade(id);
        colaboradorRepository.delete(colaborador);
    }

    /** Uso interno da propria camada de servico — a web recebe DTO. */
    private Colaborador buscarEntidade(Long id) {
        return colaboradorRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Colaborador não encontrado"));
    }

}
