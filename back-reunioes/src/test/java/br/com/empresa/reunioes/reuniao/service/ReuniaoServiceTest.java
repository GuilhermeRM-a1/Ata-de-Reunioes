package br.com.empresa.reunioes.reuniao.service;

import br.com.empresa.reunioes.application.reuniao.ReuniaoService;
import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.enums.StatusReuniao;
import br.com.empresa.reunioes.domain.enums.StatusTranscricao;
import br.com.empresa.reunioes.domain.exception.RecursoNaoEncontradoException;
import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.reuniao.dto.request.ReuniaoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Service de reuniões")
class ReuniaoServiceTest {

    private static final String MENSAGEM_REUNIAO_NAO_ENCONTRADA = "Reunião não encontrada.";
    private static final String MENSAGEM_COLABORADOR_NAO_ENCONTRADO =
            "Colaborador não encontrado entre os participantes informados.";
    private static final String MENSAGEM_ACAO_NAO_ENCONTRADA =
            "Ação não encontrada entre as ações informadas.";

    @Mock
    ReuniaoRepository reuniaoRepository;

    @Mock
    ColaboradorRepository colaboradorRepository;

    @Mock
    AcaoRepository acaoRepository;

    @InjectMocks
    ReuniaoService reuniaoService;

    private ReuniaoRequest criarRequestValido() {
        return new ReuniaoRequest(
                "Reunião de Alinhamento",
                "2026-06-01T10:00:00",
                StatusTranscricao.CONCLUIDA,
                StatusReuniao.PENDENTE,
                List.of("TI"),
                List.of("Ponto chave 1"),
                List.of(1L),
                List.of(10L),
                "Resumo executivo"
        );
    }

    private ReuniaoRequest criarRequestSemRelacionamentos(List<Long> participantes, List<Long> acoes) {
        return new ReuniaoRequest(
                "Reunião",
                "2026-06-01T10:00:00",
                StatusTranscricao.CONCLUIDA,
                StatusReuniao.PENDENTE,
                null, null, participantes, acoes, null
        );
    }

    private ReuniaoRequest criarRequestParcial(List<Long> participantes, List<Long> acoes) {
        return new ReuniaoRequest(
                null, null, null, null,
                null, null, participantes, acoes, null
        );
    }

    private Colaborador criarColaborador(Long id) {
        var colaborador = new Colaborador();
        colaborador.setId(id);
        return colaborador;
    }

    private Acao criarAcao(Long id) {
        var acao = new Acao();
        acao.setId(id);
        return acao;
    }

    private Reuniao criarReuniaoExistente(Long id, Colaborador colaborador, Acao acao) {
        var reuniao = new Reuniao();
        reuniao.setId(id);
        reuniao.setTitulo("Título original");
        reuniao.setData("2026-01-10");
        reuniao.setStatusTranscricao(StatusTranscricao.RECEBIDA);
        reuniao.setStatusReuniao(StatusReuniao.PENDENTE);
        reuniao.setAreas(List.of("Tecnologia", "Gestão"));
        reuniao.setPontosChaves(List.of("Definir requisitos", "Estabelecer prazos"));
        reuniao.setResumo("Resumo executivo da reunião original");
        reuniao.setParticipantes(new ArrayList<>(List.of(colaborador)));
        reuniao.setAcoes(new ArrayList<>(List.of(acao)));
        reuniao.setTotalAcoes(1);
        return reuniao;
    }

    private void simularSalvarRetornandoArgumento() {
        when(reuniaoRepository.save(any(Reuniao.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    @Nested
    @DisplayName("Cadastro de reuniões")
    class Cadastro {

        @Test
        @DisplayName("Dada requisição válida, quando salvar, então persiste a reunião com participantes, ações e total de ações")
        void requisicaoValida_devePersistirReuniaoComRelacionamentos() {

            var request = criarRequestValido();

            when(colaboradorRepository.findAllById(List.of(1L)))
                    .thenReturn(List.of(criarColaborador(1L)));
            when(acaoRepository.findAllById(List.of(10L)))
                    .thenReturn(List.of(criarAcao(10L)));
            simularSalvarRetornandoArgumento();


            var resultado = reuniaoService.salvar(request);


            assertThat(resultado.getTitulo()).isEqualTo(request.titulo());
            assertThat(resultado.getData()).isEqualTo(request.data());
            assertThat(resultado.getParticipantes())
                    .extracting(Colaborador::getId)
                    .containsExactly(1L);
            assertThat(resultado.getAcoes())
                    .extracting(Acao::getId)
                    .containsExactly(10L);
            assertThat(resultado.getTotalAcoes()).isEqualTo(1);

            verify(reuniaoRepository).save(resultado);
        }

        @Test
        @DisplayName("Dado colaborador inexistente, quando salvar, então lança RecursoNaoEncontradoException e não persiste")
        void colaboradorInexistente_deveLancarExcecaoENaoPersistir() {

            var request = criarRequestValido();

            when(colaboradorRepository.findAllById(any()))
                    .thenReturn(List.of());


            assertThatThrownBy(() -> reuniaoService.salvar(request))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage(MENSAGEM_COLABORADOR_NAO_ENCONTRADO);

            verify(reuniaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Dada ação inexistente, quando salvar, então lança RecursoNaoEncontradoException e não persiste")
        void acaoInexistente_deveLancarExcecaoENaoPersistir() {

            var request = criarRequestValido();

            when(colaboradorRepository.findAllById(any()))
                    .thenReturn(List.of(criarColaborador(1L)));
            when(acaoRepository.findAllById(any()))
                    .thenReturn(List.of());


            assertThatThrownBy(() -> reuniaoService.salvar(request))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage(MENSAGEM_ACAO_NAO_ENCONTRADA);

            verify(reuniaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Dados ids nulos, quando salvar, então usa listas vazias, total zero e não consulta os repositórios")
        void idsNulos_deveUsarListasVazias() {

            var request = criarRequestSemRelacionamentos(null, null);
            simularSalvarRetornandoArgumento();


            var resultado = reuniaoService.salvar(request);


            assertThat(resultado.getParticipantes()).isEmpty();
            assertThat(resultado.getAcoes()).isEmpty();
            assertThat(resultado.getTotalAcoes()).isZero();

            verifyNoInteractions(colaboradorRepository, acaoRepository);
        }

        @Test
        @DisplayName("Dados ids vazios, quando salvar, então usa listas vazias, total zero e não consulta os repositórios")
        void idsVazios_deveUsarListasVazias() {

            var request = criarRequestSemRelacionamentos(List.of(), List.of());
            simularSalvarRetornandoArgumento();


            var resultado = reuniaoService.salvar(request);


            assertThat(resultado.getParticipantes()).isEmpty();
            assertThat(resultado.getAcoes()).isEmpty();
            assertThat(resultado.getTotalAcoes()).isZero();

            verifyNoInteractions(colaboradorRepository, acaoRepository);
        }

        @Test
        @DisplayName("Dados ids de participantes repetidos, quando salvar, então considera cada colaborador uma vez e não lança exceção")
        void idsRepetidos_deveConsiderarCadaIdUmaVez() {

            var request = criarRequestSemRelacionamentos(List.of(1L, 1L), null);
            when(colaboradorRepository.findAllById(any()))
                    .thenReturn(List.of(criarColaborador(1L)));
            simularSalvarRetornandoArgumento();


            var resultado = reuniaoService.salvar(request);


            assertThat(resultado.getParticipantes())
                    .extracting(Colaborador::getId)
                    .containsExactly(1L);
        }
    }

    @Nested
    @DisplayName("Consultas de reuniões")
    class Consultas {

        @Test
        @DisplayName("Dado id existente, quando buscar, então retorna a reunião encontrada")
        void idExistente_deveRetornarReuniao() {

            var reuniao = new Reuniao();
            reuniao.setId(1L);
            when(reuniaoRepository.findById(1L))
                    .thenReturn(Optional.of(reuniao));


            var resultado = reuniaoService.buscarPorId(1L);


            assertThat(resultado).isSameAs(reuniao);
        }

        @Test
        @DisplayName("Dado id inexistente, quando buscar, então lança RecursoNaoEncontradoException")
        void idInexistente_deveLancarRecursoNaoEncontrado() {

            when(reuniaoRepository.findById(1L))
                    .thenReturn(Optional.empty());


            assertThatThrownBy(() -> reuniaoService.buscarPorId(1L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage(MENSAGEM_REUNIAO_NAO_ENCONTRADA);
        }

        @Test
        @DisplayName("Dada paginação, quando listar, então devolve a página do repositório")
        void paginacao_deveDevolverPaginaDoRepositorio() {

            Pageable pageable = PageRequest.of(0, 10);
            var reuniao = new Reuniao();
            when(reuniaoRepository.findAll(pageable))
                    .thenReturn(new PageImpl<>(List.of(reuniao)));


            Page<Reuniao> resultado = reuniaoService.listar(pageable);


            assertThat(resultado.getContent()).containsExactly(reuniao);
            assertThat(resultado.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("Dado repositório sem registros, quando listar, então devolve página vazia")
        void semRegistros_deveDevolverPaginaVazia() {

            Pageable pageable = PageRequest.of(0, 10);
            when(reuniaoRepository.findAll(pageable))
                    .thenReturn(Page.empty(pageable));


            Page<Reuniao> resultado = reuniaoService.listar(pageable);


            assertThat(resultado.getContent()).isEmpty();
            assertThat(resultado.getTotalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("Atualização completa de reuniões")
    class Atualizacao {

        @Test
        @DisplayName("Dada requisição válida, quando atualizar, então substitui todos os dados e vincula as novas ações à reunião")
        void requisicaoValida_deveSubstituirTodosOsDados() {

            var id = 1L;
            var request = criarRequestValido();
            var reuniaoExistente = criarReuniaoExistente(id, criarColaborador(99L), criarAcao(98L));
            var acaoNova = criarAcao(10L);

            when(reuniaoRepository.findById(id))
                    .thenReturn(Optional.of(reuniaoExistente));
            when(colaboradorRepository.findAllById(any()))
                    .thenReturn(List.of(criarColaborador(1L)));
            when(acaoRepository.findAllById(any()))
                    .thenReturn(List.of(acaoNova));
            simularSalvarRetornandoArgumento();


            var resultado = reuniaoService.atualizar(id, request);


            assertThat(resultado.getId()).isEqualTo(id);
            assertThat(resultado.getTitulo()).isEqualTo(request.titulo());
            assertThat(resultado.getData()).isEqualTo(request.data());
            assertThat(resultado.getStatusTranscricao()).isEqualTo(request.statusTranscricao());
            assertThat(resultado.getStatusReuniao()).isEqualTo(request.statusReuniao());
            assertThat(resultado.getAreas()).isEqualTo(request.areas());
            assertThat(resultado.getPontosChaves()).isEqualTo(request.pontosChaves());
            assertThat(resultado.getResumo()).isEqualTo(request.resumo());
            assertThat(resultado.getParticipantes())
                    .extracting(Colaborador::getId)
                    .containsExactly(1L);
            assertThat(resultado.getAcoes())
                    .extracting(Acao::getId)
                    .containsExactly(10L);
            assertThat(resultado.getTotalAcoes()).isEqualTo(1);
            assertThat(acaoNova.getReuniao()).isSameAs(reuniaoExistente);

            verify(reuniaoRepository).save(reuniaoExistente);
        }

        @Test
        @DisplayName("Dado id inexistente, quando atualizar, então lança RecursoNaoEncontradoException e não persiste")
        void idInexistente_deveLancarExcecaoENaoPersistir() {

            var request = criarRequestValido();
            when(reuniaoRepository.findById(99L))
                    .thenReturn(Optional.empty());


            assertThatThrownBy(() -> reuniaoService.atualizar(99L, request))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage(MENSAGEM_REUNIAO_NAO_ENCONTRADA);

            verify(reuniaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Dado colaborador inexistente, quando atualizar, então lança RecursoNaoEncontradoException e não persiste")
        void colaboradorInexistente_deveLancarExcecaoENaoPersistir() {

            var request = criarRequestValido();
            var reuniaoExistente = criarReuniaoExistente(1L, criarColaborador(99L), criarAcao(98L));
            when(reuniaoRepository.findById(1L))
                    .thenReturn(Optional.of(reuniaoExistente));
            when(colaboradorRepository.findAllById(any()))
                    .thenReturn(List.of());


            assertThatThrownBy(() -> reuniaoService.atualizar(1L, request))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage(MENSAGEM_COLABORADOR_NAO_ENCONTRADO);

            verify(reuniaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Dada ação inexistente, quando atualizar, então lança RecursoNaoEncontradoException e não persiste")
        void acaoInexistente_deveLancarExcecaoENaoPersistir() {

            var request = criarRequestValido();
            var reuniaoExistente = criarReuniaoExistente(1L, criarColaborador(99L), criarAcao(98L));
            when(reuniaoRepository.findById(1L))
                    .thenReturn(Optional.of(reuniaoExistente));
            when(colaboradorRepository.findAllById(any()))
                    .thenReturn(List.of(criarColaborador(1L)));
            when(acaoRepository.findAllById(any()))
                    .thenReturn(List.of());


            assertThatThrownBy(() -> reuniaoService.atualizar(1L, request))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage(MENSAGEM_ACAO_NAO_ENCONTRADA);

            verify(reuniaoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Atualização parcial de reuniões")
    class AtualizacaoParcial {

        @Test
        @DisplayName("Dados todos os campos nulos, quando atualizar parcialmente, então mantém os valores originais e não consulta os repositórios")
        void camposNulos_deveManterValoresOriginais() {

            var id = 1L;
            var request = criarRequestParcial(null, null);
            var reuniaoExistente = criarReuniaoExistente(id, criarColaborador(1L), criarAcao(1L));

            when(reuniaoRepository.findById(id))
                    .thenReturn(Optional.of(reuniaoExistente));
            simularSalvarRetornandoArgumento();


            var resultado = reuniaoService.atualizarParcial(id, request);


            assertThat(resultado.getTitulo()).isEqualTo("Título original");
            assertThat(resultado.getData()).isEqualTo("2026-01-10");
            assertThat(resultado.getStatusTranscricao()).isEqualTo(StatusTranscricao.RECEBIDA);
            assertThat(resultado.getStatusReuniao()).isEqualTo(StatusReuniao.PENDENTE);
            assertThat(resultado.getAreas()).containsExactly("Tecnologia", "Gestão");
            assertThat(resultado.getPontosChaves())
                    .containsExactly("Definir requisitos", "Estabelecer prazos");
            assertThat(resultado.getResumo()).isEqualTo("Resumo executivo da reunião original");
            assertThat(resultado.getParticipantes())
                    .extracting(Colaborador::getId)
                    .containsExactly(1L);
            assertThat(resultado.getAcoes())
                    .extracting(Acao::getId)
                    .containsExactly(1L);
            assertThat(resultado.getTotalAcoes()).isEqualTo(1);

            verify(reuniaoRepository).save(reuniaoExistente);
            verifyNoInteractions(colaboradorRepository, acaoRepository);
        }

        @Test
        @DisplayName("Dados todos os campos, quando atualizar parcialmente, então altera todos")
        void todosOsCampos_deveAlterarTodos() {

            var id = 1L;
            var request = criarRequestValido();
            var reuniaoExistente = criarReuniaoExistente(id, criarColaborador(99L), criarAcao(98L));

            when(reuniaoRepository.findById(id))
                    .thenReturn(Optional.of(reuniaoExistente));
            when(colaboradorRepository.findAllById(any()))
                    .thenReturn(List.of(criarColaborador(1L)));
            when(acaoRepository.findAllById(any()))
                    .thenReturn(List.of(criarAcao(10L)));
            simularSalvarRetornandoArgumento();


            var resultado = reuniaoService.atualizarParcial(id, request);


            assertThat(resultado.getTitulo()).isEqualTo(request.titulo());
            assertThat(resultado.getData()).isEqualTo(request.data());
            assertThat(resultado.getStatusTranscricao()).isEqualTo(request.statusTranscricao());
            assertThat(resultado.getStatusReuniao()).isEqualTo(request.statusReuniao());
            assertThat(resultado.getAreas()).isEqualTo(request.areas());
            assertThat(resultado.getPontosChaves()).isEqualTo(request.pontosChaves());
            assertThat(resultado.getResumo()).isEqualTo(request.resumo());
            assertThat(resultado.getParticipantes())
                    .extracting(Colaborador::getId)
                    .containsExactlyElementsOf(request.participantes());
            assertThat(resultado.getAcoes())
                    .extracting(Acao::getId)
                    .containsExactlyElementsOf(request.acoes());
            assertThat(resultado.getTotalAcoes()).isEqualTo(1);

            verify(reuniaoRepository).save(reuniaoExistente);
        }

        @Test
        @DisplayName("Dados apenas participantes, quando atualizar parcialmente, então troca os participantes e mantém as ações")
        void apenasParticipantes_deveTrocarParticipantesEManterAcoes() {

            var id = 1L;
            var request = criarRequestParcial(List.of(2L), null);
            var reuniaoExistente = criarReuniaoExistente(id, criarColaborador(1L), criarAcao(1L));

            when(reuniaoRepository.findById(id))
                    .thenReturn(Optional.of(reuniaoExistente));
            when(colaboradorRepository.findAllById(List.of(2L)))
                    .thenReturn(List.of(criarColaborador(2L)));
            simularSalvarRetornandoArgumento();


            var resultado = reuniaoService.atualizarParcial(id, request);


            assertThat(resultado.getParticipantes())
                    .extracting(Colaborador::getId)
                    .containsExactly(2L);
            assertThat(resultado.getAcoes())
                    .extracting(Acao::getId)
                    .containsExactly(1L);
            assertThat(resultado.getTotalAcoes()).isEqualTo(1);

            verifyNoInteractions(acaoRepository);
        }

        @Test
        @DisplayName("Dadas apenas ações, quando atualizar parcialmente, então troca as ações, recalcula o total e mantém os participantes")
        void apenasAcoes_deveTrocarAcoesERecalcularTotal() {

            var id = 1L;
            var request = criarRequestParcial(null, List.of(10L, 11L));
            var reuniaoExistente = criarReuniaoExistente(id, criarColaborador(1L), criarAcao(1L));
            var primeiraAcao = criarAcao(10L);
            var segundaAcao = criarAcao(11L);

            when(reuniaoRepository.findById(id))
                    .thenReturn(Optional.of(reuniaoExistente));
            when(acaoRepository.findAllById(List.of(10L, 11L)))
                    .thenReturn(List.of(primeiraAcao, segundaAcao));
            simularSalvarRetornandoArgumento();


            var resultado = reuniaoService.atualizarParcial(id, request);


            assertThat(resultado.getAcoes())
                    .extracting(Acao::getId)
                    .containsExactly(10L, 11L);
            assertThat(resultado.getTotalAcoes()).isEqualTo(2);
            assertThat(primeiraAcao.getReuniao()).isSameAs(reuniaoExistente);
            assertThat(segundaAcao.getReuniao()).isSameAs(reuniaoExistente);
            assertThat(resultado.getParticipantes())
                    .extracting(Colaborador::getId)
                    .containsExactly(1L);

            verifyNoInteractions(colaboradorRepository);
        }

        @Test
        @DisplayName("Dado id inexistente, quando atualizar parcialmente, então lança RecursoNaoEncontradoException e não persiste")
        void idInexistente_deveLancarExcecaoENaoPersistir() {

            var request = criarRequestParcial(null, null);
            when(reuniaoRepository.findById(99L))
                    .thenReturn(Optional.empty());


            assertThatThrownBy(() -> reuniaoService.atualizarParcial(99L, request))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage(MENSAGEM_REUNIAO_NAO_ENCONTRADA);

            verify(reuniaoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Exclusão de reuniões")
    class Exclusao {

        @Test
        @DisplayName("Dado id existente, quando deletar, então remove exatamente a reunião encontrada")
        void idExistente_deveRemoverReuniao() {

            var reuniao = new Reuniao();
            reuniao.setId(1L);
            when(reuniaoRepository.findById(1L))
                    .thenReturn(Optional.of(reuniao));


            reuniaoService.deletar(1L);


            verify(reuniaoRepository).delete(reuniao);
        }

        @Test
        @DisplayName("Dado id inexistente, quando deletar, então lança RecursoNaoEncontradoException e não remove nada")
        void idInexistente_deveLancarExcecaoENaoRemover() {

            when(reuniaoRepository.findById(99L))
                    .thenReturn(Optional.empty());


            assertThatThrownBy(() -> reuniaoService.deletar(99L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage(MENSAGEM_REUNIAO_NAO_ENCONTRADA);

            verify(reuniaoRepository, never()).delete(any());
        }
    }
}