package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.application.mapper.ColaboradorMapper;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorDTO;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorPatchRequest;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorRequest;
import br.com.empresa.reunioes.web.controller.dto.PaginaResponse;
import br.com.empresa.reunioes.web.exception.RecursoNaoEncontradoException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Todo metodo publico devolve ENTIDADE. O campo senha fica na entidade e nunca
 *
 */
@Service
@RequiredArgsConstructor
public class ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;
    private final ColaboradorMapper mapper;

    @Transactional
    public Colaborador salvar(ColaboradorRequest request) {
        Colaborador colaborador = mapper.toEntity(request);

        return this.colaboradorRepository.save(colaborador);
    }

    public Colaborador buscarPorId(Long id) {
        return this.buscarEntidade(id);
    }

    public List<Colaborador> listar() {
        return colaboradorRepository.findAll();
    }

    @Transactional
    public Colaborador atualizar(Long id, ColaboradorRequest request) {
        Colaborador colaborador = buscarEntidade(id);
        mapper.updateEntity(colaborador, request);

        return this.colaboradorRepository.save(colaborador);
    }

    @Transactional
    public Colaborador atualizarParcial(Long id, ColaboradorPatchRequest request) {

        Colaborador colaborador = buscarEntidade(id);
        mapper.updateParsiEntity(colaborador, request);

        return this.colaboradorRepository.save(colaborador);
    }

    @Transactional
    public void deletar(Long id) {
        Colaborador colaborador = buscarEntidade(id);
        colaboradorRepository.delete(colaborador);
    }

    /**
     * Uso interno da propria camada de servico
     */
    private Colaborador buscarEntidade(Long id) {
        return colaboradorRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Colaborador", id));
    }

}
