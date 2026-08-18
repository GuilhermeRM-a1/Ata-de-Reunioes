package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.controller.dto.Acao.AcaoDTO;
import br.com.empresa.reunioes.web.controller.dto.Acao.AcaoRequest;
import br.com.empresa.reunioes.web.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Todo metodo publico devolve DTO — a camada web nao ve entidade JPA. */
@Service
@RequiredArgsConstructor
public class AcaoService {

    private final AcaoRepository repository;
    private final ColaboradorRepository colaboradorRepository;
    private final ReuniaoRepository reuniaoRepository;

    public AcaoDTO salvar(AcaoRequest request) {

            Acao acao = new Acao();

            acao.setTitulo(request.titulo());
            acao.setDescricao(request.descricao());
            acao.setTipo(request.tipo());
            acao.setPrazo(request.prazo());

            acao.setResponsavel(buscarResponsaveis(request.responsavel()));

            acao.setReuniao(buscarReuniao(request.reuniao()));

            return AcaoDTO.de(repository.save(acao));
        }

        public AcaoDTO buscarPorId(Long id) {

            return AcaoDTO.de(buscarEntidade(id));
        }

        public List<AcaoDTO> listar() {

            return repository.findAll().stream()
                    .map(AcaoDTO::de)
                    .toList();
        }

        public AcaoDTO atualizar(Long id, AcaoRequest request) {

            Acao acao = buscarEntidade(id);

            acao.setTitulo(request.titulo());
            acao.setDescricao(request.descricao());
            acao.setTipo(request.tipo());
            acao.setPrazo(request.prazo());

            acao.setResponsavel(buscarResponsaveis(request.responsavel()));

            acao.setReuniao(buscarReuniao(request.reuniao()));

            return AcaoDTO.de(repository.save(acao));
        }

        public AcaoDTO atualizarParcial(Long id, AcaoRequest request) {

            Acao acao = buscarEntidade(id);

            if (request.titulo() != null) {
                acao.setTitulo(request.titulo());
            }

            if (request.descricao() != null) {
                acao.setDescricao(request.descricao());
            }

            if (request.tipo() != null) {
                acao.setTipo(request.tipo());
            }

            if (request.prazo() != null) {
                acao.setPrazo(request.prazo());
            }

            if (request.responsavel() != null) {
                acao.setResponsavel(buscarResponsaveis(request.responsavel()));
            }

            if (request.reuniao() != null) {
                acao.setReuniao(buscarReuniao(request.reuniao()));
            }

            return AcaoDTO.de(repository.save(acao));
        }

        public void deletar(Long id) {

            Acao acao = buscarEntidade(id);

            repository.delete(acao);
        }

        /** Uso interno da propria camada de servico — a web recebe DTO. */
        private Acao buscarEntidade(Long id) {

            return repository.findById(id)
                    .orElseThrow(() -> RecursoNaoEncontradoException.de("Ação", id));
        }

        private Reuniao buscarReuniao(Long id) {

            return reuniaoRepository.findById(id)
                    .orElseThrow(() -> RecursoNaoEncontradoException.de("Reunião", id));
        }

        /**
         * Troca os ids da request pelas entidades do banco. findAllById descarta
         * id inexistente em silencio, entao a contagem e conferida para o cliente
         * receber 404 em vez de uma acao salva sem o responsavel pedido.
         */
        private List<Colaborador> buscarResponsaveis(List<Long> ids) {

            if (ids == null || ids.isEmpty()) {
                return new ArrayList<>();
            }

            List<Colaborador> colaboradores = colaboradorRepository.findAllById(ids);

            if (colaboradores.size() != ids.stream().distinct().count()) {
                throw new RecursoNaoEncontradoException(
                        "Colaborador não encontrado entre os responsáveis informados");
            }

            return colaboradores;
        }

}
