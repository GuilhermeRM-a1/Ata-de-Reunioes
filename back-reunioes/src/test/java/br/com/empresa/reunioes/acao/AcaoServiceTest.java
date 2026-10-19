package br.com.empresa.reunioes.acao;

import br.com.empresa.reunioes.application.acao.AcaoService;
import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.exception.RecursoNaoEncontradoException;
import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.acao.dto.AcaoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

@SpringBootTest
public class AcaoServiceTest {


    @Mock
    private AcaoRepository acaoRepository;

    @Mock
    private ColaboradorRepository colaboradorRepository;

    @Mock
    private ReuniaoRepository reuniaoRepository;

    @InjectMocks
    private AcaoService acaoService;


    // salvar,
    // buscarPorId,
    // Listar,
    // listarPorReuniao,
    // atualizar,
    // atualizarParcial,
    // deletar,
    // buscarPorReuniao,
    // BuscarResponsaveis

    @Test
    @DisplayName("Testando o processo de salvar ação")
    void cenario_salvar() {

        Long reuniaoId = 1L;
        List<Long> listaTeste = List.of(123L);
        AcaoRequest request = new AcaoRequest("Revisar código",
                "Revisar o PR do módulo de relatórios",
                "Tecnica",
                "2026-10-20",
                false,
                listaTeste);

        when(reuniaoRepository.findById(reuniaoId)).thenReturn(Optional.of(new Reuniao()));

        when(colaboradorRepository.findAllById(request.responsavel())).thenReturn(List.of(new Colaborador()));

        Acao acaoSimulacao = new Acao();
        acaoSimulacao.setId(10L);
        when(acaoRepository.save(any(Acao.class))).thenReturn(acaoSimulacao);

        Acao resultado = acaoService.salvar(reuniaoId, request);


        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());

        verify(acaoRepository, times(1)).save(any(Acao.class));

    }


    @Test
    @DisplayName("")
    void cenario_buscarPorId() {
        Long idDeBusca = 10L;

        Acao acaoSimulacao = new Acao();
        acaoSimulacao.setId(idDeBusca);
        acaoSimulacao.setTitulo("Reunião de Alinhamento");

        when(acaoRepository.findById(idDeBusca)).thenReturn(Optional.of(acaoSimulacao));

        Acao resultado = acaoService.buscarPorId(idDeBusca);

        assertNotNull(resultado);
        assertEquals(idDeBusca, resultado.getId());
        assertEquals("Reunião de Alinhamento", resultado.getTitulo());

        verify(acaoRepository, times(1)).findById(idDeBusca);

    }


    @Test
    @DisplayName("")
    void cenario_buscarPorId_Error() {
        Long idDeBusca = 10L;

        when(acaoRepository.findById(idDeBusca)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> acaoService.buscarPorId(idDeBusca));

        verify(acaoRepository, times(1)).findById(idDeBusca);
    }

    @Test
    @DisplayName("")
    void cenario_listar() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Acao acao = new Acao();
        acao.setId(1L);


        Page<Acao> paginaDeAcoes = new PageImpl<>(List.of(acao));
        when(acaoRepository.findAll(pageable)).thenReturn(paginaDeAcoes);

        Page<Acao> resultado = acaoService.listar(pageable);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getTotalElements());

        verify(acaoRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("")
    void cenario_listarPorReuniao() {
        Long idSimulacao = 1L;

        when(reuniaoRepository.existsById(idSimulacao)).thenReturn(true);

        List<Acao> reuniaoSimulacao = new ArrayList<>();
        Acao acaoSimulacao = new Acao();
        acaoSimulacao.setId(1L);

        reuniaoSimulacao.add(acaoSimulacao);

        when(acaoRepository.findAllByReuniaoId(idSimulacao)).thenReturn(reuniaoSimulacao);

        List<Acao> resultado = acaoService.listarPorReuniao(idSimulacao);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());

        verify(acaoRepository, (times(1))).findAllByReuniaoId(idSimulacao);
    }

    @Test
    @DisplayName("Deve lançar exceção se tentar listar ações de uma reunião que não existe")
    void cenario_listarPorReuniao_Error() {
        Long idSimulacao = 1L;

        when(reuniaoRepository.existsById(idSimulacao)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> acaoService.listarPorReuniao(idSimulacao));

        verify(reuniaoRepository, times(1)).existsById(idSimulacao);

    }


    @Test
    @DisplayName("")
    void cenario_atualizarAcao() {
        Long idSimulacao = 1L;
        List<Long> listaTeste = List.of(123L);
        AcaoRequest request = new AcaoRequest("Revisar código",
                "Revisar o PR do módulo de relatórios",
                "Tecnica",
                "2026-10-20",
                false,
                listaTeste);

        when(colaboradorRepository.findAllById(request.responsavel())).thenReturn(List.of(new Colaborador()));

        Acao acaoSimulacao = new Acao();
        acaoSimulacao.setId(10L);
        acaoSimulacao.setTitulo("Título Antigo");

        when(acaoRepository.findById(idSimulacao)).thenReturn(Optional.of(acaoSimulacao));

        when(acaoRepository.save(any(Acao.class))).thenReturn(acaoSimulacao);

        Acao resultado = acaoService.atualizar(idSimulacao, request);

        assertNotNull(resultado);
        assertEquals("Revisar código", resultado.getTitulo());

        verify(acaoRepository, times(1)).findById(idSimulacao);
        verify(colaboradorRepository, times(1)).findAllById(request.responsavel());
        verify(acaoRepository, times(1)).save(any(Acao.class));
    }

    @Test
    @DisplayName("")
    void cenario_atualizarAcaoParcial() {
        Long idSimulacao = 1L;

        Acao acaoSimulacao = new Acao();
        acaoSimulacao.setId(idSimulacao);
        acaoSimulacao.setTitulo("Título Antigo");
        acaoSimulacao.setDescricao("Descrição Antiga");
        acaoSimulacao.setTipo("Tecnica");

        AcaoRequest request = new AcaoRequest("Título Novo",
                null,
                null,
                null,
                false,
                null);



        when(acaoRepository.findById(idSimulacao)).thenReturn(Optional.of(acaoSimulacao));

        when(acaoRepository.save(any(Acao.class))).thenReturn(acaoSimulacao);

        Acao resultado = acaoService.atualizarParcial(idSimulacao,request);

        assertNotNull(resultado);
        assertEquals("Título Novo",resultado.getTitulo());
        assertEquals("Descrição Antiga",resultado.getDescricao());
        assertEquals("Tecnica",resultado.getTipo());

        verify(acaoRepository, times(1)).findById(idSimulacao);
        verify(acaoRepository,times(1)).save(any(Acao.class));

    }

    @Test
    @DisplayName("Deve atualizar os responsáveis da ação quando a lista for enviada no request")
    void cenario_atualizarAcaoComResponsaveis() {
        Long idSimulacao = 1L;
        List<Long> listaResponsaveisIds = List.of(10L, 20L);


        Acao acaoSimulacao = new Acao();
        acaoSimulacao.setId(idSimulacao);
        acaoSimulacao.setTitulo("Título Original");


        AcaoRequest request = new AcaoRequest(
                null,
                null,
                null,
                null,
                false,
                listaResponsaveisIds
        );

        when(acaoRepository.findById(idSimulacao)).thenReturn(Optional.of(acaoSimulacao));

        List<Colaborador> colaboradoresSimulados = List.of(new Colaborador(), new Colaborador());
        when(colaboradorRepository.findAllById(listaResponsaveisIds)).thenReturn(colaboradoresSimulados);

        when(acaoRepository.save(any(Acao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Acao resultado = acaoService.atualizarParcial(idSimulacao, request);


        assertNotNull(resultado);
        verify(acaoRepository, times(1)).findById(idSimulacao);
        verify(colaboradorRepository, times(1)).findAllById(listaResponsaveisIds);
        verify(acaoRepository, times(1)).save(any(Acao.class));
    }

    @Test
    @DisplayName("")
    void cenario_deletar(){
        Long idSimulacao = 1L;

        Acao acaoSimulacao = new Acao();
        acaoSimulacao.setId(idSimulacao);

        Reuniao reuniaoSimulacao = new Reuniao();
        reuniaoSimulacao.setId(100L);
        reuniaoSimulacao.setAcoes(new ArrayList<>(List.of(acaoSimulacao)));

        acaoSimulacao.setReuniao(reuniaoSimulacao);

        when(acaoRepository.findById(idSimulacao)).thenReturn(Optional.of(acaoSimulacao));
        acaoService.deletar(idSimulacao);

        verify(acaoRepository, times(1)).findById(idSimulacao);
        verify(acaoRepository, times(1)).delete(acaoSimulacao);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a reunião não for encontrada")
    void cenario_buscarReuniao_Erro_NaoEncontrada() {
        Long idInexistente = 99L;

        when(reuniaoRepository.existsById(idInexistente)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            acaoService.listarPorReuniao(idInexistente);
        });

        verify(reuniaoRepository, times(1)).existsById(idInexistente);
    }

    @Test
    @DisplayName("Deve lançar exceção quando algum colaborador informado não for encontrado")
    void cenario_buscarResponsaveis_Erro_ColaboradorNaoEncontrado() {
        Long idAcao = 1L;
        List<Long> idsEnviados = List.of(10L, 20L);

        AcaoRequest request = new AcaoRequest(
                "Título", "Descrição", "Tecnica", "2026-10-20", false, idsEnviados
        );

        Acao acaoSimulacao = new Acao();
        acaoSimulacao.setId(idAcao);

        when(acaoRepository.findById(idAcao)).thenReturn(Optional.of(acaoSimulacao));

        when(colaboradorRepository.findAllById(idsEnviados)).thenReturn(List.of(new Colaborador()));

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            acaoService.atualizarParcial(idAcao, request);
        });

        verify(colaboradorRepository, times(1)).findAllById(idsEnviados);
    }


}
