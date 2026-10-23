package br.com.empresa.reunioes.acao;

import br.com.empresa.reunioes.application.acao.AcaoService;
import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.exception.RecursoNaoEncontradoException;
import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.acao.dto.request.AcaoRequest;
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
import org.springframework.data.domain.Sort;

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
@DisplayName("Service de ações")
class AcaoServiceTest {

    private static final String MENSAGEM_ACAO_NAO_ENCONTRADA = "Ação não encontrada";
    private static final String MENSAGEM_REUNIAO_NAO_ENCONTRADA = "Reunião não encontrada";
    private static final String MENSAGEM_RESPONSAVEL_NAO_ENCONTRADO =
            "Colaborador não encontrado entre os responsáveis informados";

    @Mock
    private AcaoRepository acaoRepository;

    @Mock
    private ColaboradorRepository colaboradorRepository;

    @Mock
    private ReuniaoRepository reuniaoRepository;

    @InjectMocks
    private AcaoService acaoService;

    private AcaoRequest criarRequestCompleto(List<Long> responsaveis) {
        return new AcaoRequest(
                "Revisar código",
                "Revisar o PR do módulo de relatórios",
                "Tecnica",
                "2026-10-20",
                false,
                responsaveis
        );
    }

    private AcaoRequest criarRequestParcial(String titulo, Boolean concluida, List<Long> responsaveis) {
        return new AcaoRequest(titulo, null, null, null, concluida, responsaveis);
    }

    private Colaborador criarColaborador(Long id) {
        var colaborador = new Colaborador();
        colaborador.setId(id);
        return colaborador;
    }

    private Reuniao criarReuniao(Long id) {
        var reuniao = new Reuniao();
        reuniao.setId(id);
        return reuniao;
    }

    private Acao criarAcaoExistente(Long id) {
        var acao = new Acao();
        acao.setId(id);
        acao.setTitulo("Título antigo");
        acao.setDescricao("Descrição antiga");
        acao.setTipo("Gestao");
        acao.setPrazo("2026-01-10");
        acao.setConcluida(true);
        acao.setResponsavel(new ArrayList<>(List.of(criarColaborador(99L))));
        return acao;
    }

    private void simularSalvarRetornandoArgumento() {
        when(acaoRepository.save(any(Acao.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    @Nested
    @DisplayName("Cadastro de ações")
    class Cadastro {

        @Test
        @DisplayName("Dada requisição válida, quando salvar, então persiste a ação com responsáveis e reunião")
        void requisicaoValida_devePersistirAcaoComResponsaveisEReuniao() {
            var reuniao = criarReuniao(1L);
            var request = criarRequestCompleto(List.of(123L));
            when(colaboradorRepository.findAllById(List.of(123L)))
                    .thenReturn(List.of(criarColaborador(123L)));
            when(reuniaoRepository.findById(1L)).thenReturn(Optional.of(reuniao));
            simularSalvarRetornandoArgumento();

            var resultado = acaoService.salvar(1L, request);

            assertThat(resultado.getTitulo()).isEqualTo("Revisar código");
            assertThat(resultado.getDescricao()).isEqualTo("Revisar o PR do módulo de relatórios");
            assertThat(resultado.getTipo()).isEqualTo("Tecnica");
            assertThat(resultado.getPrazo()).isEqualTo("2026-10-20");
            assertThat(resultado.getResponsavel())
                    .extracting(Colaborador::getId)
                    .containsExactly(123L);
            assertThat(resultado.getReuniao()).isSameAs(reuniao);

            verify(acaoRepository).save(resultado);
        }

        @Test
        @DisplayName("Dados responsáveis nulos, quando salvar, então usa lista vazia e não consulta colaboradores")
        void responsaveisNulos_deveUsarListaVazia() {
            var request = criarRequestCompleto(null);
            when(reuniaoRepository.findById(1L)).thenReturn(Optional.of(criarReuniao(1L)));
            simularSalvarRetornandoArgumento();

            var resultado = acaoService.salvar(1L, request);

            assertThat(resultado.getResponsavel()).isEmpty();
            verifyNoInteractions(colaboradorRepository);
        }

        @Test
        @DisplayName("Dados responsáveis vazios, quando salvar, então usa lista vazia e não consulta colaboradores")
        void responsaveisVazios_deveUsarListaVazia() {
            var request = criarRequestCompleto(List.of());
            when(reuniaoRepository.findById(1L)).thenReturn(Optional.of(criarReuniao(1L)));
            simularSalvarRetornandoArgumento();

            var resultado = acaoService.salvar(1L, request);

            assertThat(resultado.getResponsavel()).isEmpty();
            verifyNoInteractions(colaboradorRepository);
        }

        @Test
        @DisplayName("Dada reunião inexistente, quando salvar, então lança RecursoNaoEncontradoException e não persiste")
        void reuniaoInexistente_deveLancarExcecaoENaoPersistir() {
            var request = criarRequestCompleto(null);
            when(reuniaoRepository.findById(0L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> acaoService.salvar(0L, request))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage(MENSAGEM_REUNIAO_NAO_ENCONTRADA);

            verify(acaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Dado responsável inexistente, quando salvar, então lança RecursoNaoEncontradoException, não busca reunião e não persiste")
        void responsavelInexistente_deveLancarExcecaoENaoPersistir() {
            var request = criarRequestCompleto(List.of(20L, 2L));
            when(colaboradorRepository.findAllById(List.of(20L, 2L)))
                    .thenReturn(List.of(criarColaborador(20L)));

            assertThatThrownBy(() -> acaoService.salvar(1L, request))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage(MENSAGEM_RESPONSAVEL_NAO_ENCONTRADO);

            verifyNoInteractions(reuniaoRepository);
            verify(acaoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Consultas de ações")
    class Consultas {

        @Test
        @DisplayName("Dado id existente, quando buscar, então retorna a ação encontrada")
        void idExistente_deveRetornarAcao() {
            var acao = criarAcaoExistente(10L);
            when(acaoRepository.findById(10L)).thenReturn(Optional.of(acao));

            var resultado = acaoService.buscarPorId(10L);

            assertThat(resultado).isSameAs(acao);
        }

        @Test
        @DisplayName("Dado id inexistente, quando buscar, então lança RecursoNaoEncontradoException")
        void idInexistente_deveLancarRecursoNaoEncontrado() {
            when(acaoRepository.findById(10L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> acaoService.buscarPorId(10L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage(MENSAGEM_ACAO_NAO_ENCONTRADA);
        }

        @Test
        @DisplayName("Dada paginação, quando listar, então devolve a página do repositório")
        void paginacao_deveDevolverPaginaDoRepositorio() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
            var acao = criarAcaoExistente(1L);
            when(acaoRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(acao)));

            Page<Acao> resultado = acaoService.listar(pageable);

            assertThat(resultado.getContent()).containsExactly(acao);
            assertThat(resultado.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("Dada reunião existente, quando listar por reunião, então devolve as ações dela")
        void reuniaoExistente_deveDevolverAcoesDaReuniao() {
            var acao = criarAcaoExistente(1L);
            when(reuniaoRepository.existsById(1L)).thenReturn(true);
            when(acaoRepository.findAllByReuniaoId(1L)).thenReturn(List.of(acao));

            List<Acao> resultado = acaoService.listarPorReuniao(1L);

            assertThat(resultado).containsExactly(acao);
        }

        @Test
        @DisplayName("Dada reunião inexistente, quando listar por reunião, então lança RecursoNaoEncontradoException e não consulta ações")
        void reuniaoInexistente_deveLancarExcecaoENaoConsultarAcoes() {
            when(reuniaoRepository.existsById(1L)).thenReturn(false);

            assertThatThrownBy(() -> acaoService.listarPorReuniao(1L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage(MENSAGEM_REUNIAO_NAO_ENCONTRADA);

            verifyNoInteractions(acaoRepository);
        }
    }

    @Nested
    @DisplayName("Atualização completa de ações")
    class Atualizacao {

        @Test
        @DisplayName("Dada requisição válida, quando atualizar, então substitui todos os dados e os responsáveis")
        void requisicaoValida_deveSubstituirTodosOsDados() {
            var existente = criarAcaoExistente(1L);
            var request = criarRequestCompleto(List.of(123L));
            when(acaoRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(colaboradorRepository.findAllById(List.of(123L)))
                    .thenReturn(List.of(criarColaborador(123L)));
            simularSalvarRetornandoArgumento();

            var resultado = acaoService.atualizar(1L, request);

            assertThat(resultado.getTitulo()).isEqualTo("Revisar código");
            assertThat(resultado.getDescricao()).isEqualTo("Revisar o PR do módulo de relatórios");
            assertThat(resultado.getTipo()).isEqualTo("Tecnica");
            assertThat(resultado.getPrazo()).isEqualTo("2026-10-20");
            assertThat(resultado.getConcluida()).isFalse();
            assertThat(resultado.getResponsavel())
                    .extracting(Colaborador::getId)
                    .containsExactly(123L);

            verify(acaoRepository).save(existente);
        }

        @Test
        @DisplayName("Dado campo concluída nulo, quando atualizar, então marca a ação como não concluída")
        void concluidaNula_deveMarcarComoNaoConcluida() {
            var existente = criarAcaoExistente(1L);
            var request = new AcaoRequest("Revisar código", "Descrição", "Tecnica", "2026-10-20", null, null);
            when(acaoRepository.findById(1L)).thenReturn(Optional.of(existente));
            simularSalvarRetornandoArgumento();

            var resultado = acaoService.atualizar(1L, request);

            assertThat(resultado.getConcluida()).isFalse();
            assertThat(resultado.getResponsavel()).isEmpty();
        }

        @Test
        @DisplayName("Dado id inexistente, quando atualizar, então lança RecursoNaoEncontradoException e não persiste")
        void idInexistente_deveLancarExcecaoENaoPersistir() {
            var request = criarRequestCompleto(null);
            when(acaoRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> acaoService.atualizar(1L, request))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage(MENSAGEM_ACAO_NAO_ENCONTRADA);

            verify(acaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Dado responsável inexistente, quando atualizar, então lança RecursoNaoEncontradoException e não persiste")
        void responsavelInexistente_deveLancarExcecaoENaoPersistir() {
            var request = criarRequestCompleto(List.of(123L));
            when(acaoRepository.findById(1L)).thenReturn(Optional.of(criarAcaoExistente(1L)));
            when(colaboradorRepository.findAllById(List.of(123L))).thenReturn(List.of());

            assertThatThrownBy(() -> acaoService.atualizar(1L, request))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage(MENSAGEM_RESPONSAVEL_NAO_ENCONTRADO);

            verify(acaoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Atualização parcial de ações")
    class AtualizacaoParcial {

        @Test
        @DisplayName("Dados todos os campos nulos, quando atualizar parcialmente, então mantém os valores originais")
        void camposNulos_deveManterValoresOriginais() {
            var existente = criarAcaoExistente(1L);
            when(acaoRepository.findById(1L)).thenReturn(Optional.of(existente));
            simularSalvarRetornandoArgumento();

            var resultado = acaoService.atualizarParcial(1L, criarRequestParcial(null, null, null));

            assertThat(resultado.getTitulo()).isEqualTo("Título antigo");
            assertThat(resultado.getDescricao()).isEqualTo("Descrição antiga");
            assertThat(resultado.getTipo()).isEqualTo("Gestao");
            assertThat(resultado.getPrazo()).isEqualTo("2026-01-10");
            assertThat(resultado.getConcluida()).isTrue();
            assertThat(resultado.getResponsavel())
                    .extracting(Colaborador::getId)
                    .containsExactly(99L);
            verifyNoInteractions(colaboradorRepository);
        }

        @Test
        @DisplayName("Dados todos os campos, quando atualizar parcialmente, então altera todos")
        void todosOsCampos_deveAlterarTodos() {
            var existente = criarAcaoExistente(1L);
            var request = criarRequestCompleto(List.of(123L));
            when(acaoRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(colaboradorRepository.findAllById(List.of(123L)))
                    .thenReturn(List.of(criarColaborador(123L)));
            simularSalvarRetornandoArgumento();

            var resultado = acaoService.atualizarParcial(1L, request);

            assertThat(resultado.getTitulo()).isEqualTo("Revisar código");
            assertThat(resultado.getDescricao()).isEqualTo("Revisar o PR do módulo de relatórios");
            assertThat(resultado.getTipo()).isEqualTo("Tecnica");
            assertThat(resultado.getPrazo()).isEqualTo("2026-10-20");
            assertThat(resultado.getConcluida()).isFalse();
            assertThat(resultado.getResponsavel())
                    .extracting(Colaborador::getId)
                    .containsExactly(123L);
        }

        @Test
        @DisplayName("Dado apenas o título, quando atualizar parcialmente, então altera só o título")
        void apenasTitulo_deveAlterarSoTitulo() {
            var existente = criarAcaoExistente(1L);
            when(acaoRepository.findById(1L)).thenReturn(Optional.of(existente));
            simularSalvarRetornandoArgumento();

            var resultado = acaoService.atualizarParcial(1L, criarRequestParcial("Título novo", null, null));

            assertThat(resultado.getTitulo()).isEqualTo("Título novo");
            assertThat(resultado.getDescricao()).isEqualTo("Descrição antiga");
            assertThat(resultado.getTipo()).isEqualTo("Gestao");
            assertThat(resultado.getConcluida()).isTrue();
        }

        @Test
        @DisplayName("Dada apenas a conclusão, quando atualizar parcialmente, então altera só a conclusão")
        void apenasConclusao_deveAlterarSoConclusao() {
            var existente = criarAcaoExistente(1L);
            when(acaoRepository.findById(1L)).thenReturn(Optional.of(existente));
            simularSalvarRetornandoArgumento();

            var resultado = acaoService.atualizarParcial(1L, criarRequestParcial(null, false, null));

            assertThat(resultado.getConcluida()).isFalse();
            assertThat(resultado.getTitulo()).isEqualTo("Título antigo");
        }

        @Test
        @DisplayName("Dados apenas responsáveis, quando atualizar parcialmente, então troca os responsáveis e mantém o resto")
        void apenasResponsaveis_deveTrocarResponsaveis() {
            var existente = criarAcaoExistente(1L);
            when(acaoRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(colaboradorRepository.findAllById(List.of(12L, 80L)))
                    .thenReturn(List.of(criarColaborador(12L), criarColaborador(80L)));
            simularSalvarRetornandoArgumento();

            var resultado = acaoService.atualizarParcial(1L, criarRequestParcial(null, null, List.of(12L, 80L)));

            assertThat(resultado.getResponsavel())
                    .extracting(Colaborador::getId)
                    .containsExactly(12L, 80L);
            assertThat(resultado.getTitulo()).isEqualTo("Título antigo");
        }

        @Test
        @DisplayName("Dado id inexistente, quando atualizar parcialmente, então lança RecursoNaoEncontradoException e não persiste")
        void idInexistente_deveLancarExcecaoENaoPersistir() {
            var request = criarRequestParcial("Título novo", null, null);
            when(acaoRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> acaoService.atualizarParcial(1L, request))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage(MENSAGEM_ACAO_NAO_ENCONTRADA);

            verify(acaoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Exclusão de ações")
    class Exclusao {

        @Test
        @DisplayName("Dado id existente, quando deletar, então remove a ação da reunião e do repositório")
        void idExistente_deveRemoverAcaoDaReuniaoEDoRepositorio() {
            var acao = criarAcaoExistente(1L);
            var reuniao = criarReuniao(100L);
            reuniao.setAcoes(new ArrayList<>(List.of(acao)));
            acao.setReuniao(reuniao);
            when(acaoRepository.findById(1L)).thenReturn(Optional.of(acao));

            acaoService.deletar(1L);

            assertThat(reuniao.getAcoes()).isEmpty();
            verify(acaoRepository).delete(acao);
        }

        @Test
        @DisplayName("Dado id inexistente, quando deletar, então lança RecursoNaoEncontradoException e não remove nada")
        void idInexistente_deveLancarExcecaoENaoRemover() {
            when(acaoRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> acaoService.deletar(1L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage(MENSAGEM_ACAO_NAO_ENCONTRADA);

            verify(acaoRepository, never()).delete(any());
        }
    }
}