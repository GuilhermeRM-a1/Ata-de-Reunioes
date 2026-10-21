package br.com.empresa.reunioes.reuniao.service;

import br.com.empresa.reunioes.application.reuniao.ReuniaoService;
import br.com.empresa.reunioes.application.reuniao.mapper.ReuniaoMapper;
import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.enums.StatusReuniao;
import br.com.empresa.reunioes.domain.enums.StatusTranscricao;
import br.com.empresa.reunioes.domain.exception.RecursoNaoEncontradoException;
import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.reuniao.dto.ReuniaoRequest;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReuniaoServiceTest {

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

    @Test
    void deveSalvarReuniao() {
        var request = criarRequestValido();
        var reuniaoSalva = new Reuniao();
        reuniaoSalva.setId(1L);

        when(colaboradorRepository.findAllById(any()))
                .thenReturn(List.of(new Colaborador()));
        when(acaoRepository.findAllById(any()))
                .thenReturn(List.of(new Acao()));
        when(reuniaoRepository.save(any(Reuniao.class)))
                .thenReturn(reuniaoSalva);

        var resultado = reuniaoService.salvar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertNotNull(resultado.getParticipantes());
        assertNotNull(resultado.getAcoes());

        verify(reuniaoRepository).save(any(Reuniao.class));
    }

    @Test
    void deveBuscarPorId() {
        var reuniao = new Reuniao();
        reuniao.setId(1L);

        when(reuniaoRepository.findById(1L))
                .thenReturn(Optional.of(reuniao));

        var resultado = reuniaoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void deveLancarExcecaoQuandoReuniaoNaoEncontrada() {
        when(reuniaoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> reuniaoService.buscarPorId(1L)
        );
    }

    @Test
    void deveListarReunioesPaginadas() {
        Pageable pageable = PageRequest.of(0, 10);
        var reuniao = new Reuniao();

        when(reuniaoRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(reuniao)));

        Page<Reuniao> resultado = reuniaoService.listar(pageable);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getTotalElements());

        verify(reuniaoRepository).findAll(pageable);
    }

    @Test
    void deveAtualizarReuniaoCompleta() {
        var id = 1L;
        var request = criarRequestValido();
        var reuniaoExistente = new Reuniao();
        reuniaoExistente.setId(id);

        when(reuniaoRepository.findById(id))
                .thenReturn(Optional.of(reuniaoExistente));
        when(colaboradorRepository.findAllById(any()))
                .thenReturn(List.of(new Colaborador()));
        when(acaoRepository.findAllById(any()))
                .thenReturn(List.of(new Acao()));
        when(reuniaoRepository.save(any(Reuniao.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        var resultado = reuniaoService.atualizar(id, request);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());

        verify(reuniaoRepository).save(reuniaoExistente);
    }

    @Test
    void deveAtualizarParcialmenteReuniaoComTodosOsValoresNulos() {
        var id = 1L;
        var request = new ReuniaoRequest(
                null, null, null, null,
                null, null, null, null, null
        );

        var colaborador = new Colaborador();
        colaborador.setId(1L);

        var acao = new Acao();
        acao.setId(1L);

        var reuniaoExistente = new Reuniao();
        reuniaoExistente.setId(id);
        reuniaoExistente.setTitulo("Título original");
        reuniaoExistente.setData("2026-01-10");
        reuniaoExistente.setStatusTranscricao(StatusTranscricao.RECEBIDA);
        reuniaoExistente.setStatusReuniao(StatusReuniao.PENDENTE);
        reuniaoExistente.setAreas(List.of("Tecnologia", "Gestão"));
        reuniaoExistente.setPontosChaves(List.of("Definir requisitos", "Estabelecer prazos"));
        reuniaoExistente.setResumo("Resumo executivo da reunião original");
        reuniaoExistente.setParticipantes(new ArrayList<>(List.of(colaborador)));
        reuniaoExistente.setAcoes(new ArrayList<>(List.of(acao)));
        reuniaoExistente.setTotalAcoes(1);

        when(reuniaoRepository.findById(id))
                .thenReturn(Optional.of(reuniaoExistente));
        when(reuniaoRepository.save(any(Reuniao.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        var resultado = reuniaoService.atualizarParcial(id, request);

        assertNotNull(resultado);
        assertNotNull(resultado.getTitulo());
        assertNotNull(resultado.getData());
        assertNotNull(resultado.getStatusTranscricao());
        assertNotNull(resultado.getStatusReuniao());
        assertNotNull(resultado.getAreas());
        assertNotNull(resultado.getPontosChaves());
        assertNotNull(resultado.getResumo());
        assertNotNull(resultado.getParticipantes());
        assertNotNull(resultado.getAcoes());
        assertNotNull(resultado.getTotalAcoes());

        verify(reuniaoRepository).save(reuniaoExistente);
    }

    @Test
    void deveAtualizarParcialmenteReuniaoComTodosOsValores() {

        var id = 1L;
        var request = criarRequestValido();

        var colaborador = new Colaborador();
        colaborador.setId(1L);

        var acao = new Acao();
        acao.setId(10L);

        var reuniaoExistente = new Reuniao();
        reuniaoExistente.setId(id);
        reuniaoExistente.setTitulo("Título original");
        reuniaoExistente.setData("2026-01-10");
        reuniaoExistente.setStatusTranscricao(StatusTranscricao.RECEBIDA);
        reuniaoExistente.setStatusReuniao(StatusReuniao.PENDENTE);
        reuniaoExistente.setAreas(List.of("Tecnologia", "Gestão"));
        reuniaoExistente.setPontosChaves(List.of("Definir requisitos", "Estabelecer prazos"));
        reuniaoExistente.setResumo("Resumo executivo da reunião original");
        reuniaoExistente.setParticipantes(new ArrayList<>(List.of(colaborador)));
        reuniaoExistente.setAcoes(new ArrayList<>(List.of(acao)));
        reuniaoExistente.setTotalAcoes(1);

        when(reuniaoRepository.findById(id))
                .thenReturn(Optional.of(reuniaoExistente));
        when(colaboradorRepository.findAllById(any()))
                .thenReturn(List.of(colaborador));
        when(acaoRepository.findAllById(any()))
                .thenReturn(List.of(acao));
        when(reuniaoRepository.save(any(Reuniao.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        var resultado = reuniaoService.atualizarParcial(id, request);

        assertNotNull(resultado);
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
    void deveDeletarReuniao() {
        var reuniao = new Reuniao();
        reuniao.setId(1L);

        when(reuniaoRepository.findById(1L))
                .thenReturn(Optional.of(reuniao));

        assertDoesNotThrow(() -> reuniaoService.deletar(1L));

        verify(reuniaoRepository).delete(reuniao);
    }

    @Test
    void deveLancarExcecaoQuandoColaboradorNaoEncontrado() {
        var request = criarRequestValido();

        when(colaboradorRepository.findAllById(any()))
                .thenReturn(List.of());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> reuniaoService.salvar(request)
        );
    }

    @Test
    void deveLancarExcecaoQuandoAcaoNaoEncontrada() {
        var request = criarRequestValido();

        when(colaboradorRepository.findAllById(any()))
                .thenReturn(List.of(new Colaborador()));
        when(acaoRepository.findAllById(any()))
                .thenReturn(List.of());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> reuniaoService.salvar(request)
        );
    }

    @Test
    void deveIgnorarBuscaDeParticipantesEAcoesQuandoIdsForemNulos() {
        var request = new ReuniaoRequest(
                "Reunião",
                "2026-06-01T10:00:00",
                StatusTranscricao.CONCLUIDA,
                StatusReuniao.PENDENTE,
                null, null, null, null, null
        );
        var reuniaoSalva = new Reuniao();
        reuniaoSalva.setId(1L);

        when(reuniaoRepository.save(any(Reuniao.class)))
                .thenReturn(reuniaoSalva);

        var resultado = reuniaoService.salvar(request);

        assertNotNull(resultado);

        verify(colaboradorRepository, never()).findAllById(any());
        verify(acaoRepository, never()).findAllById(any());
    }
}