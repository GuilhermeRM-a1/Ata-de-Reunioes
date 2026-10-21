package br.com.empresa.reunioes.colaborador;

import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.enums.Papel;
import br.com.empresa.reunioes.domain.exception.RecursoNaoEncontradoException;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.web.colaborador.ColaboradorController;
import br.com.empresa.reunioes.web.colaborador.dto.ColaboradorPatchRequest;
import br.com.empresa.reunioes.web.colaborador.dto.ColaboradorRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ColaboradorControllerTest {

    @Autowired
    ColaboradorController colaboradorController;

    @MockitoBean
    ColaboradorRepository colaboradorRepository;

    @MockitoBean
    BCryptPasswordEncoder passwordEncoder;

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
        var colaboradorSalvo = criarColaboradorValido();

        when(colaboradorRepository.existsByEmail("maria.souza@empresa.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hash-bcrypt");
        when(colaboradorRepository.save(any(Colaborador.class))).thenReturn(colaboradorSalvo);

        var resposta = colaboradorController.salvar(request);

        assertNotNull(resposta);
        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1L, resposta.getBody().id());
        assertEquals("Maria Souza", resposta.getBody().nome());
        verify(colaboradorRepository).save(any(Colaborador.class));
    }

    @Test
    void deveBuscarPorId() {
        var colaborador = criarColaboradorValido();
        when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(colaborador));

        var resposta = colaboradorController.buscarPorId(1L);

        assertNotNull(resposta);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1L, resposta.getBody().id());
        verify(colaboradorRepository).findById(1L);
    }

    @Test
    void deveLancarExceptionCasoColaboradorNaoEncontradoPorId() {
        when(colaboradorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> colaboradorController.buscarPorId(99L));
    }

    @Test
    void deveBuscarPorEmail() {
        var colaborador = criarColaboradorValido();
        when(colaboradorRepository.findByEmail("maria.souza@empresa.com")).thenReturn(colaborador);

        var resposta = colaboradorController.buscarPorEmail("maria.souza@empresa.com");

        assertNotNull(resposta);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals("maria.souza@empresa.com", resposta.getBody().email());
        verify(colaboradorRepository).findByEmail("maria.souza@empresa.com");
    }

    @Test
    void deveBuscarPorEmailRequestParam() {
        var colaborador = criarColaboradorValido();
        when(colaboradorRepository.findByEmail("maria.souza@empresa.com")).thenReturn(colaborador);

        var resposta = colaboradorController.buscarPorEmailRequestParam("maria.souza@empresa.com");

        assertNotNull(resposta);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals("maria.souza@empresa.com", resposta.getBody().email());
        verify(colaboradorRepository).findByEmail("maria.souza@empresa.com");
    }

    @Test
    void deveListarColaboradoresPaginados() {
        Pageable pageable = PageRequest.of(0, 10);
        when(colaboradorRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(criarColaboradorValido())));

        var resposta = colaboradorController.listar(pageable);

        assertNotNull(resposta);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertFalse(resposta.getBody().isEmpty());
        verify(colaboradorRepository).findAll(pageable);
    }

    @Test
    void deveAtualizarColaboradorCompleto() {
        var id = 1L;
        var request = criarRequestValido();
        var colaboradorExistente = criarColaboradorValido();

        when(colaboradorRepository.findById(id)).thenReturn(Optional.of(colaboradorExistente));
        when(colaboradorRepository.save(any(Colaborador.class))).thenReturn(colaboradorExistente);

        var resposta = colaboradorController.atualizar(id, request);

        assertNotNull(resposta);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        verify(colaboradorRepository).save(colaboradorExistente);
    }

    @Test
    void deveAtualizarParcial() {
        var id = 1L;
        var request = new ColaboradorPatchRequest("Nome Alterado", null, null, null, null, null);
        var colaboradorExistente = criarColaboradorValido();

        when(colaboradorRepository.findById(id)).thenReturn(Optional.of(colaboradorExistente));
        when(colaboradorRepository.save(any(Colaborador.class))).thenReturn(colaboradorExistente);

        var resposta = colaboradorController.atualizarParcial(id, request);

        assertNotNull(resposta);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        verify(colaboradorRepository).save(colaboradorExistente);
    }

    @Test
    void deveDeletarColaborador() {
        var colaborador = criarColaboradorValido();
        when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(colaborador));

        var resposta = colaboradorController.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        verify(colaboradorRepository).delete(colaborador);
    }
}