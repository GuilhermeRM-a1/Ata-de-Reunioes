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
class AcaoServiceTest {


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
    void cenario_buscarPorId_Error() {
        Long idDeBusca = 10L;

        when(acaoRepository.findById(idDeBusca)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> acaoService.buscarPorId(idDeBusca));

        verify(acaoRepository, times(1)).findById(idDeBusca);
    }

    @Test
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
    void cenario_listarPorReuniao_Error() {
        Long idSimulacao = 1L;

        when(reuniaoRepository.existsById(idSimulacao)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> acaoService.listarPorReuniao(idSimulacao));

        verify(reuniaoRepository, times(1)).existsById(idSimulacao);

    }


    @Test
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

        Acao resultado = acaoService.atualizarParcial(idSimulacao, request);

        assertNotNull(resultado);
        assertEquals("Título Novo", resultado.getTitulo());
        assertEquals("Descrição Antiga", resultado.getDescricao());
        assertEquals("Tecnica", resultado.getTipo());

        verify(acaoRepository, times(1)).findById(idSimulacao);
        verify(acaoRepository, times(1)).save(any(Acao.class));

    }

    @Test
    void cenario_atualizarResponsaveis_ComSucesso(){
        Long idSimulacao = 1L;

        Acao acaoSimulacao = new Acao();
        acaoSimulacao.setId(idSimulacao);

        List<Long> idsResponsaveis = List.of(12L,80L);

        AcaoRequest request = new AcaoRequest("Título Atualizado",
                "Descrição",
                "Tecnica",
                "2026-10-20",
                false,
                idsResponsaveis);

        Colaborador c1 = new Colaborador();
        c1.setId(10L);
        Colaborador c2 = new Colaborador();
        c2.setId(20L);

        when(acaoRepository.findById(idSimulacao)).thenReturn(Optional.of(acaoSimulacao));

        when(colaboradorRepository.findAllById(any())).thenReturn(List.of(c1,c2));

        when(acaoRepository.save(any(Acao.class))).thenReturn(acaoSimulacao);

        Acao resultado = acaoService.atualizarParcial(idSimulacao, request);

        assertNotNull(resultado);
        verify(acaoRepository, times(1)).findById(idSimulacao);
        verify(acaoRepository, times(1)).save(any(Acao.class));
    }

    @Test
    void cenario_deletar() {
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
    void cenario_buscarReuniao_Erro() {
        Long idInexistente = 0L;


        AcaoRequest request = new AcaoRequest("Título Atualizado",
                "Descrição",
                "Tecnica",
                "2026-10-20",
                false,
                null);

        when(reuniaoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            acaoService.salvar(idInexistente, request);
        });

        verify(reuniaoRepository, times(1)).findById(idInexistente);    }

    @Test
    void cenario_buscarReuniao() {
        Long idSimulacao = 1L;
        Reuniao reuniaoSimulacao = new Reuniao();
        reuniaoSimulacao.setId(idSimulacao);

        Acao acaoSimulacao = new Acao();
        acaoSimulacao.setId(idSimulacao);

        when(reuniaoRepository.findById(idSimulacao)).thenReturn(Optional.of(reuniaoSimulacao));

        when(acaoRepository.save(any(Acao.class))).thenReturn(acaoSimulacao);


        AcaoRequest request = new AcaoRequest("Título", "Desc", "Tec", "2026-10-20", false, List.of());

        Acao resultado = acaoService.salvar(idSimulacao,request);

        assertNotNull(resultado);

        verify(reuniaoRepository, times(1)).findById(idSimulacao);
        verify(acaoRepository, times(1)).save(any(Acao.class));
    }

    @Test
    void Cenario_buscarResponsaveis(){


        List<Long> idsResponsaveis = List.of(20L, 2L);
        Colaborador c1 = new Colaborador();
        c1.setId(10L);

        AcaoRequest request = new AcaoRequest("Revisar código",
                "Revisar o PR do módulo de relatórios",
                "Tecnica",
                "2026-10-20",
                false,
                idsResponsaveis);

        Long idSimulacao = 1L;

        Reuniao reuniaoSimulacao = new Reuniao();
        reuniaoSimulacao.setId(idSimulacao);

        when(colaboradorRepository.findAllById(request.responsavel())).thenReturn(List.of(new Colaborador()));

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            acaoService.salvar(idSimulacao, request);
        });

        verify(colaboradorRepository, times(1)).findAllById(idsResponsaveis);
        

    }


    @Test
    void Cenario_buscarResponsaveis_Error() {

        Long idReuniao = 1L;

        Reuniao reuniaoSimulacao = new Reuniao();
        reuniaoSimulacao.setId(idReuniao);
        when(reuniaoRepository.findById(idReuniao)).thenReturn(Optional.of(reuniaoSimulacao));

        AcaoRequest request = new AcaoRequest(
                "Título", "Desc", "Tec", "2026-10-20", false, null
        );
        Acao acaoSalvaMock = new Acao();
        acaoSalvaMock.setId(10L);

        when(acaoRepository.save(any(Acao.class))).thenReturn(acaoSalvaMock);

        Acao resultado = acaoService.salvar(idReuniao, request);

        assertNotNull(resultado);
        verify(reuniaoRepository, times(1)).findById(idReuniao);
        verify(acaoRepository, times(1)).save(any(Acao.class));
    }


}
