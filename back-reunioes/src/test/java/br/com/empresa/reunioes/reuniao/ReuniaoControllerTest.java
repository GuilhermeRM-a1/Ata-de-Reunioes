
package br.com.empresa.reunioes.reuniao;

import br.com.empresa.reunioes.application.reuniao.FeriadoService;
import br.com.empresa.reunioes.application.reuniao.IngestaoService;
import br.com.empresa.reunioes.application.reuniao.RelatorioService;
import br.com.empresa.reunioes.application.reuniao.ReuniaoService;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.web.reuniao.ReuniaoController;
import br.com.empresa.reunioes.web.reuniao.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class ReuniaoControllerTest {

    @Mock
    ReuniaoService reuniaoService;

    @Mock
    IngestaoService ingestaoService;

    @Mock
    RelatorioService relatorioService;

    @Mock
    FeriadoService feriadoService;

    @InjectMocks
    ReuniaoController controller;

    @BeforeEach
    void setUp() {
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/reunioes");
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(request)
        );
    }

    @Test
    void listarRetornaPaginaDeReunioes() {
        var pageable = PageRequest.of(0, 10);
        Page<Reuniao> pagina = new PageImpl<>(List.of());

        when(reuniaoService.listar(pageable)).thenReturn(pagina);

        ResponseEntity<Page<ReuniaoDTO>> response =
                controller.listar(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getTotalElements());
        verify(reuniaoService).listar(pageable);
    }

    @Test
    void buscarPorIdRetornaReuniao() {
        Reuniao reuniao = mock(Reuniao.class);
        when(reuniaoService.buscarPorId(1L)).thenReturn(reuniao);

        ResponseEntity<ReuniaoDTO> response =
                controller.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reuniaoService).buscarPorId(1L);
    }

    @Test
    void salvarRetornaCreatedComLocation() {
        Reuniao reuniao = mock(Reuniao.class);
        ReuniaoRequest request = mock(ReuniaoRequest.class);

        when(reuniao.getId()).thenReturn(1L);

        when(reuniaoService.salvar(request)).thenReturn(reuniao);

        ResponseEntity<ReuniaoDTO> response = controller.salvar(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getHeaders().getLocation().getPath().endsWith("/api/reunioes/1"));
        assertNotNull(response.getBody());
        verify(reuniaoService).salvar(request);
    }

    @Test
    void atualizarRetornaReuniaoAtualizada() {
        Reuniao reuniao = mock(Reuniao.class);
        ReuniaoRequest request = mock(ReuniaoRequest.class);

        when(reuniaoService.atualizar(1L, request))
                .thenReturn(reuniao);

        ResponseEntity<ReuniaoDTO> response =
                controller.atualizar(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reuniaoService).atualizar(1L, request);
    }

    @Test
    void atualizarParcialRetornaReuniaoAtualizada() {
        Reuniao reuniao = mock(Reuniao.class);
        ReuniaoPatchRequest request = mock(ReuniaoPatchRequest.class);
        ReuniaoRequest requestConvertido = mock(ReuniaoRequest.class);

        when(request.paraRequest()).thenReturn(requestConvertido);
        when(reuniaoService.atualizarParcial(1L, requestConvertido))
                .thenReturn(reuniao);

        ResponseEntity<ReuniaoDTO> response =
                controller.atualizarParcial(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(request).paraRequest();
        verify(reuniaoService).atualizarParcial(1L, requestConvertido);
    }

    @Test
    void receberAnaliseRetornaAccepted() {
        Reuniao reuniao = mock(Reuniao.class);
        ReuniaoResumoRequest request = mock(ReuniaoResumoRequest.class);

        when(ingestaoService.receberAnalise(1L, request))
                .thenReturn(reuniao);

        ResponseEntity<ReuniaoDTO> response =
                controller.receberAnalise(1L, request);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(ingestaoService).receberAnalise(1L, request);
    }

    @Test
    void gerarRelatorioRetornaRelatorio() {
        Reuniao reuniao = mock(Reuniao.class);

        when(relatorioService.validarReuniaoParaRelatorio(1L))
                .thenReturn(reuniao);

        ResponseEntity<RelatorioReuniaoResponse> response =
                controller.gerarRelatorio(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(relatorioService).validarReuniaoParaRelatorio(1L);
    }

    @Test
    void verificarFeriadoRetornaResultado() {
        ReuniaoFeriadoDTO feriado = mock(ReuniaoFeriadoDTO.class);

        when(feriadoService.verificarReuniao(1L))
                .thenReturn(feriado);

        ResponseEntity<ReuniaoFeriadoDTO> response =
                controller.verificarFeriado(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(feriado, response.getBody());
        verify(feriadoService).verificarReuniao(1L);
    }

    @Test
    void deletarRetornaNoContent() {
        doNothing().when(reuniaoService).deletar(1L);

        ResponseEntity<Void> response = controller.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(reuniaoService).deletar(1L);
    }
}
