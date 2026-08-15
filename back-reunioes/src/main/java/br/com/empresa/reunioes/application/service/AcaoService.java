package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.domain.model.Acao;
import br.com.empresa.reunioes.domain.model.Colaborador;
import br.com.empresa.reunioes.domain.model.Reuniao;
import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.controller.dto.Acao.AcaoRequest;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AcaoService {

    private final AcaoRepository repository;
    private final ColaboradorRepository colaboradorRepository;
    private final ReuniaoRepository reuniaoRepository;

    public Acao salvar(AcaoRequest request) {

            Acao acao = new Acao();

            acao.setTitulo(request.titulo());
            acao.setDescricao(request.descricao());
            acao.setTipo(request.tipo());
            acao.setPrazo(request.prazo());

            acao.setResponsavel(buscarResponsaveis(request.responsavel()));

            Reuniao reuniao = reuniaoRepository
                    .findById(request.reuniao())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Reunião não encontrada"
                            )
                    );

            acao.setReuniao(reuniao);

            return repository.save(acao);
        }

        public Acao buscarPorId(Long id) {

            return repository.findById(id)
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Ação não encontrada"
                            )
                    );
        }

        public List<Acao> listar() {

            return repository.findAll();
        }

        public Acao atualizar(Long id, AcaoRequest request) {

            Acao acao = buscarPorId(id);

            acao.setTitulo(request.titulo());
            acao.setDescricao(request.descricao());
            acao.setTipo(request.tipo());
            acao.setPrazo(request.prazo());

            acao.setResponsavel(buscarResponsaveis(request.responsavel()));

            Reuniao reuniao = reuniaoRepository
                    .findById(request.reuniao())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Reunião não encontrada"
                            )
                    );

            acao.setReuniao(reuniao);

            return repository.save(acao);
        }

        public Acao atualizarParcial(Long id, AcaoRequest request) {

            Acao acao = buscarPorId(id);

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
                Reuniao reuniao = reuniaoRepository
                        .findById(request.reuniao())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Reunião não encontrada"
                                )
                        );

                acao.setReuniao(reuniao);
            }

            return repository.save(acao);
        }

        public void deletar(Long id) {

            Acao acao = buscarPorId(id);

            repository.delete(acao);
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
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Colaborador não encontrado entre os responsáveis informados");
            }

            return colaboradores;
        }

}
