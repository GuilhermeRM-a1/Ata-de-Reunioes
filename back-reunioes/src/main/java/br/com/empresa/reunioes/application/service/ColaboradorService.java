package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.application.mapper.ColaboradorMapper;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorPatchRequest;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorRequest;
import br.com.empresa.reunioes.web.exception.EmailCadastradoExistenteException;
import br.com.empresa.reunioes.web.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;
    private final ColaboradorMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Todo método público devolve ENTIDADE.
     */

    @Transactional
    public Colaborador salvar(ColaboradorRequest request) {
        log.info("Iniciando processo de salvar colaborador.");

        Colaborador colaborador = mapper.toEntity(request);
        log.debug("Requisição convertida para entidade.");

        log.debug("Verificando se o e-mail já está cadastrado.");
        boolean existe = colaboradorRepository.existsByEmail(colaborador.getEmail());

        if (existe) {
            log.warn("Tentativa de cadastro com e-mail já existente.");
            throw new EmailCadastradoExistenteException(
                    "Este e-mail já existe, cadastre outro."
            );
        }

        colaborador.setSenha(passwordEncoder.encode(colaborador.getSenha()));
        log.debug("Senha do colaborador criptografada.");

        Colaborador colaboradorSalvo = colaboradorRepository.save(colaborador);

        log.info("Colaborador salvo com sucesso: id={}", colaboradorSalvo.getId());

        return colaboradorSalvo;
    }

    @Transactional(readOnly = true)
    public Colaborador buscarPorId(Long id) {
        log.debug("Buscando colaborador por id={}", id);

        Colaborador colaboradorEncontrado = colaboradorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Colaborador não encontrado com ID: " + id
                ));

        log.debug("Colaborador encontrado: id={}", id);

        return colaboradorEncontrado;
    }

    @Transactional(readOnly = true)
    public Colaborador buscarPorEmail(String email) {
        log.info("Iniciando busca do colaborador pelo e-mail.");

        log.debug("Verificando se existe colaborador com o e-mail informado.");
        Colaborador colaboradorEncontrado = colaboradorRepository.findByEmail(email);

        if (colaboradorEncontrado == null) {
            throw new RecursoNaoEncontradoException(
                    "Colaborador com e-mail " + email + " não encontrado"
            );
        }

        log.debug("Colaborador encontrado pelo e-mail.");

        return colaboradorEncontrado;
    }

    @Transactional(readOnly = true)
    public Page<Colaborador> listar(Pageable paginacao) {
        log.info("Iniciando listagem paginada de colaboradores.");

        Page<Colaborador> listaPaginada =
                colaboradorRepository.findAll(paginacao);

        log.debug(
                "Listagem de colaboradores concluída. Total encontrado: {}",
                listaPaginada.getTotalElements()
        );

        return listaPaginada;
    }

    @Transactional
    public Colaborador atualizar(Long id, ColaboradorRequest request) {
        log.info("Iniciando atualização do colaborador: id={}", id);

        Colaborador colaborador = buscarPorId(id);

        log.debug("Verificando se o novo e-mail já está cadastrado.");

        boolean emailAlterado = !request.email().equals(colaborador.getEmail());

        if (emailAlterado
                && colaboradorRepository.existsByEmail(request.email())) {
            log.warn("Tentativa de alteração para e-mail já existente: id={}", id);
            throw new EmailCadastradoExistenteException(
                    "Este e-mail já existe, cadastre outro."
            );
        }

        log.debug("Atualizando colaborador com mapper: id={}", id);
        mapper.updateEntity(colaborador, request);

        Colaborador colaboradorAtualizado =
                colaboradorRepository.save(colaborador);

        log.info("Colaborador atualizado com sucesso: id={}", id);

        return colaboradorAtualizado;
    }

    @Transactional
    public Colaborador atualizarParcial(Long id, ColaboradorPatchRequest request) {
        log.info("Iniciando atualização parcial do colaborador: id={}", id);

        Colaborador colaborador = buscarPorId(id);

        if (request.email() != null) {
            log.debug("Verificando se o novo e-mail já está cadastrado.");

            boolean emailAlterado = !request.email().equals(colaborador.getEmail());

            if (emailAlterado
                    && colaboradorRepository.existsByEmail(request.email())) {
                log.warn(
                        "Tentativa de alteração para e-mail já existente: id={}",
                        id
                );
                throw new EmailCadastradoExistenteException(
                        "Este e-mail já existe, cadastre outro."
                );
            }
        }

        log.debug("Atualizando parcialmente o colaborador com mapper: id={}", id);
        mapper.updateParcialEntity(colaborador, request);

        Colaborador colaboradorAtualizado =
                colaboradorRepository.save(colaborador);

        log.info("Colaborador atualizado parcialmente com sucesso: id={}", id);

        return colaboradorAtualizado;
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Iniciando processo de remoção do colaborador: id={}", id);

        Colaborador colaborador = buscarPorId(id);

        colaboradorRepository.delete(colaborador);

        log.info("Colaborador removido com sucesso: id={}", id);
    }
}