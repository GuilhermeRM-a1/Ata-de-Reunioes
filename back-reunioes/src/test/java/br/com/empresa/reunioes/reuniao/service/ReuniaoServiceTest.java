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

import java.util.List;
import java.util.Optional;

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

        when(colaboradorRepository.findAllById(any())).thenReturn(List.of(new Colaborador()));
        when(acaoRepository.findAllById(any())).thenReturn(List.of(new Acao()));
        when(reuniaoRepository.save(any(Reuniao.class))).thenReturn(reuniaoSalva);

        var resultado = reuniaoService.salvar(request);

        assertNotNull(resultado);
        assertNotNull(resultado.getAcoes());
        assertNotNull(resultado.getParticipantes());
        assertEquals(1L, resultado.getId());
    }

    @Test
    void deveBuscarPorId() {
        var reuniao = new Reuniao();
        reuniao.setId(1L);

        when(reuniaoRepository.findById(1L)).thenReturn(Optional.of(reuniao));

        var resultado = reuniaoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void deveLancarExceptionCasoReuniaoNaoEncontrada() {
        when(reuniaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> reuniaoService.buscarPorId(99L));
    }

    @Test
    void deveListarReunioesPaginadas() {
        Pageable pageable = PageRequest.of(0, 10);
        when(reuniaoRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(new Reuniao())));

        Page<Reuniao> resultado = reuniaoService.listar(pageable);

        assertFalse(resultado.isEmpty());
        verify(reuniaoRepository).findAll(pageable);
    }

    @Test
    void deveAtualizarReuniaoCompleta() {
        var id = 1L;
        var request = criarRequestValido();
        var reuniaoExistente = new Reuniao();
        reuniaoExistente.setId(id);

        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniaoExistente));
        when(colaboradorRepository.findAllById(any())).thenReturn(List.of(new Colaborador()));
        when(acaoRepository.findAllById(any())).thenReturn(List.of(new Acao()));
        when(reuniaoRepository.save(any(Reuniao.class))).thenReturn(reuniaoExistente);

        var resultado = reuniaoService.atualizar(id, request);

        assertNotNull(resultado);
        verify(reuniaoRepository).save(reuniaoExistente);
    }

    @Test
    void deveAtualizarParcial() {
        var id = 1L;
        var request = new ReuniaoRequest("Parcial", null, null, null, null, null, List.of(), List.of(), null);
        var reuniaoExistente = new Reuniao();
        reuniaoExistente.setId(id);

        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniaoExistente));
        when(reuniaoRepository.save(any(Reuniao.class))).thenReturn(reuniaoExistente);

        var resultado = reuniaoService.atualizarParcial(id, request);

        assertNotNull(resultado);
        assertNotNull(resultado.getParticipantes());
        assertNotNull(resultado.getAcoes());
        verify(reuniaoRepository).save(reuniaoExistente);
    }

    @Test
    void deveDeletarReuniao() {
        var reuniao = new Reuniao();
        reuniao.setId(1L);

        when(reuniaoRepository.findById(1L)).thenReturn(Optional.of(reuniao));

        assertDoesNotThrow(() -> reuniaoService.deletar(1L));
        verify(reuniaoRepository).delete(reuniao);
    }

    @Test
    void deveLancarExceptionCasoColaboradorNaoEncontrado() {
        var request = criarRequestValido();
        when(colaboradorRepository.findAllById(any())).thenReturn(List.of());

        assertThrows(RecursoNaoEncontradoException.class, () -> reuniaoService.salvar(request));
    }

    @Test
    void deveLancarExceptionCasoAcaoNaoEncontrada() {
        var request = criarRequestValido();
        when(colaboradorRepository.findAllById(any())).thenReturn(List.of(new Colaborador()));
        when(acaoRepository.findAllById(any())).thenReturn(List.of());

        assertThrows(RecursoNaoEncontradoException.class, () -> reuniaoService.salvar(request));
    }

    @Test
    void deveRetornarListaVaziaQuandoIdsForemNulosOuVazios() {
        var request = new ReuniaoRequest(
                "Reunião",
                "2026-06-01T10:00:00",
                StatusTranscricao.CONCLUIDA,
                StatusReuniao.PENDENTE,
                null,
                null,
                null,
                null,
                null
        );

        var reuniaoSalva = new Reuniao();
        reuniaoSalva.setId(1L);

        when(reuniaoRepository.save(any(Reuniao.class))).thenReturn(reuniaoSalva);

        var resultado = reuniaoService.salvar(request);

        assertNotNull(resultado);
        verify(colaboradorRepository, never()).findAllById(any());
        verify(acaoRepository, never()).findAllById(any());
    }
}