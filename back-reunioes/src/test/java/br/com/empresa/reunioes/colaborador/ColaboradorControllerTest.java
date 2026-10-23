package br.com.empresa.reunioes.colaborador;

import br.com.empresa.reunioes.application.colaborador.ColaboradorService;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.enums.Papel;
import br.com.empresa.reunioes.domain.exception.RecursoNaoEncontradoException;
import br.com.empresa.reunioes.web.colaborador.ColaboradorController;
import br.com.empresa.reunioes.web.colaborador.dto.request.ColaboradorPatchRequest;
import br.com.empresa.reunioes.web.colaborador.dto.request.ColaboradorRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColaboradorControllerTest {

    @Mock
    private ColaboradorService service;

    @InjectMocks
    private ColaboradorController colaboradorController;

    private ColaboradorRequest criarRequestValido() {
        return new ColaboradorRequest(
                "Maria Souza",
                "maria.souza@empresa.com",
                "senha123",
                Papel.USUARIO,
                true,
                "2026-02-01"
        );
    }

    private Colaborador criarColaboradorValido() {
        var colaborador = new Colaborador();
        colaborador.setId(1L);
        colaborador.setNome("Maria Souza");
        colaborador.setEmail("maria.souza@empresa.com");
        colaborador.setSenha("hash-bcrypt");
        colaborador.setPapel(Papel.USUARIO);
        colaborador.setMonitorarReunioes(true);
        colaborador.setDataCadastro("2026-02-01");
        return colaborador;
    }

    @Test
    void deveSalvarColaborador() {
        var request = criarRequestValido();
        var colaborador = criarColaboradorValido();

        when(service.salvar(request)).thenReturn(colaborador);

        var resposta = colaboradorController.salvar(request);

        assertNotNull(resposta);
        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1L, resposta.getBody().id());
        assertEquals("Maria Souza", resposta.getBody().nome());

        verify(service).salvar(request);
    }

    @Test
    void deveBuscarPorId() {
        var colaborador = criarColaboradorValido();

        when(service.buscarPorId(1L)).thenReturn(colaborador);

        var resposta = colaboradorController.buscarPorId(1L);

        assertNotNull(resposta);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1L, resposta.getBody().id());

        verify(service).buscarPorId(1L);
    }

    @Test
    void deveLancarExceptionCasoColaboradorNaoEncontradoPorId() {
        when(service.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException(
                        "Colaborador não encontrado com ID: 99"
                ));

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> colaboradorController.buscarPorId(99L)
        );

        verify(service).buscarPorId(99L);
    }

    @Test
    void deveBuscarPorEmail() {
        var colaborador = criarColaboradorValido();
        var email = "maria.souza@empresa.com";

        when(service.buscarPorEmail(email)).thenReturn(colaborador);

        var resposta = colaboradorController.buscarPorEmail(email);

        assertNotNull(resposta);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(email, resposta.getBody().email());

        verify(service).buscarPorEmail(email);
    }

    @Test
    void deveBuscarPorEmailRequestParam() {
        var colaborador = criarColaboradorValido();
        var email = "maria.souza@empresa.com";

        when(service.buscarPorEmail(email)).thenReturn(colaborador);

        var resposta =
                colaboradorController.buscarPorEmailRequestParam(email);

        assertNotNull(resposta);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(email, resposta.getBody().email());

        verify(service).buscarPorEmail(email);
    }

    @Test
    void deveListarColaboradoresPaginados() {
        Pageable pageable = PageRequest.of(0, 10);
        var colaborador = criarColaboradorValido();

        when(service.listar(pageable))
                .thenReturn(new PageImpl<>(List.of(colaborador)));

        var resposta = colaboradorController.listar(pageable);

        assertNotNull(resposta);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertFalse(resposta.getBody().isEmpty());

        verify(service).listar(pageable);
    }

    @Test
    void deveAtualizarColaboradorCompleto() {
        var id = 1L;
        var request = criarRequestValido();
        var colaborador = criarColaboradorValido();

        when(service.atualizar(id, request)).thenReturn(colaborador);

        var resposta = colaboradorController.atualizar(id, request);

        assertNotNull(resposta);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());

        verify(service).atualizar(id, request);
    }

    @Test
    void deveAtualizarParcial() {
        var id = 1L;
        var request = new ColaboradorPatchRequest(
                "Nome Alterado", null, null, null, null, null
        );
        var colaborador = criarColaboradorValido();

        when(service.atualizarParcial(id, request))
                .thenReturn(colaborador);

        var resposta = colaboradorController.atualizarParcial(id, request);

        assertNotNull(resposta);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());

        verify(service).atualizarParcial(id, request);
    }

    @Test
    void deveDeletarColaborador() {
        var id = 1L;

        doNothing().when(service).deletar(id);

        var resposta = colaboradorController.deletar(id);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());

        verify(service).deletar(id);
    }
}