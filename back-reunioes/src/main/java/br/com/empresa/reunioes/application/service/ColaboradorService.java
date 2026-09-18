package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.application.mapper.ColaboradorMapper;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorPatchRequest;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorRequest;
import br.com.empresa.reunioes.web.exception.EmailCadastradoExistenteException;
import br.com.empresa.reunioes.web.exception.RecursoNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Todo metodo publico devolve ENTIDADE.
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;
    private final ColaboradorMapper mapper;

    @Transactional
    public Colaborador salvar(ColaboradorRequest request) {
        log.info ("Iniciando processo de salvar colaborador.");

        log.debug("Verificando se já colaborador existe.");
        Colaborador colaboradorExistente = colaboradorRepository.findByEmail(request.email());

        if(colaboradorExistente != null) {
            log.warn("Email existente, não é possível salvar colaborador.");
            throw new EmailCadastradoExistenteException("Este email já existe, cadastre outro.");
        }

        Colaborador colaborador = mapper.toEntity(request);

        log.debug("Requisição convertida para entidade.");

        Colaborador colaboradorSalvo = this.colaboradorRepository.save(colaborador);
        log.debug("Colaborador salvo com sucesso.");

        return colaboradorSalvo;
    }

    @Transactional(readOnly = true)
    public Colaborador buscarPorId(Long id) {
        log.info("Iniciando busca por Colaborador pelo id.");

        log.debug("Buscando Colaborador por id.");
        Colaborador colaboradorEncontrado = colaboradorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Colaborador não encontrado"));
        log.debug("Colaborador encontrado: {}", colaboradorEncontrado);

        return colaboradorEncontrado;
    }

    @Transactional(readOnly = true)
    public Colaborador buscarPorEmail(String email) {
        log.info("Iniciando busca do Colaborador pelo email.");

        log.debug("Verificando se colaborador existe.");
        Colaborador colaboradorEncontrado = colaboradorRepository.findByEmail(email);

        if (colaboradorEncontrado == null) {
            throw new RecursoNaoEncontradoException("Colaborador com email" + email + "não encontrado");
        }

        return colaboradorEncontrado;
    }

    @Transactional(readOnly = true)
    public Page<Colaborador> listar(Pageable paginacao) {
        log.info("Iniciando listagem de Colaboradores.");

        Page<Colaborador> listaPaginada = colaboradorRepository.findAll(paginacao);
        log.debug("Listagem de colaboradores concluída. Total encontrado: {}", listaPaginada.getTotalElements());

        return listaPaginada;
    }

    @Transactional
    public Colaborador atualizar(Long id, ColaboradorRequest request) {
        Colaborador colaborador = buscarPorId(id);
        mapper.updateEntity(colaborador, request);

        return this.colaboradorRepository.save(colaborador);
    }

    @Transactional
    public Colaborador atualizarParcial(Long id, ColaboradorPatchRequest request) {
        log.info("Iniciando atualização parcial do Colaborador com id: {}", id);

        log.debug("Buscando Colaborador existente por id.");
        Colaborador colaborador = buscarPorId(id);

        log.debug("Atualizando com mapper");
        mapper.updateParcialEntity(colaborador, request);

        Colaborador colaboradorAtualizado = colaboradorRepository.save(colaborador);
        log.info("Colaborador atualizado salvo com sucesso.");

        return colaboradorAtualizado;
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Iniciando processo de deletar Colaborador.");

        log.debug("Buscando Colaborador existente por id");
        Colaborador colaborador = buscarPorId(id);

        colaboradorRepository.delete(colaborador);
        log.info("Colaborador Removido com sucesso: ID: {}", id);
    }

}