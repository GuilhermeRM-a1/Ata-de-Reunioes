package br.com.empresa.reunioes.acao;

import br.com.empresa.reunioes.application.acao.AcaoService;
import br.com.empresa.reunioes.application.reuniao.ReuniaoService;
import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.web.acao.AcaoController;
import br.com.empresa.reunioes.web.acao.dto.AcaoDTO;
import br.com.empresa.reunioes.web.acao.dto.request.AcaoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AcaoControllerTest {



    @Mock
    private AcaoService acaoService;

    @InjectMocks
    private AcaoController acaoController;

    private Long idSimulacao;
    private AcaoRequest request;
    private Reuniao reuniao;
    private Acao acaoSimulacao;

    @BeforeEach
    void setup() {
        idSimulacao = 1L;

        request = new AcaoRequest(
                "Título Atualizado",
                "Descrição",
                "Tecnica",
                "2026-10-20",
                false,
                null
        );

        reuniao = new Reuniao();
        reuniao.setId(idSimulacao);

        acaoSimulacao = new Acao();
        acaoSimulacao.setId(1L);
        acaoSimulacao.setReuniao(reuniao);

    }

    @Test
    void cenario_salvarAcao(){
        when(acaoService.salvar(eq(idSimulacao), any(AcaoRequest.class))).thenReturn(acaoSimulacao);
        ResponseEntity<AcaoDTO> retorno = acaoController.salvar(idSimulacao, request);

        assertNotNull(retorno);
        assertEquals(HttpStatus.CREATED, retorno.getStatusCode());
        assertNotNull(retorno.getBody());
    }

    @Test
    void cenario_listarAcao(){
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<Acao> paginaDeAcoes = new PageImpl<>(List.of(acaoSimulacao));

        when(acaoService.listar(pageable)).thenReturn(paginaDeAcoes);

        ResponseEntity<Page<AcaoDTO>> retorno = acaoController.listar(pageable);

        assertNotNull(retorno);
        assertEquals(HttpStatus.OK, retorno.getStatusCode());
        assertNotNull(retorno.getBody());
    }

    @Test
    void cenario_listarPorReuniao(){
        List<Acao> listaDeAcoes = List.of(acaoSimulacao);

        when(acaoService.listarPorReuniao(idSimulacao)).thenReturn(listaDeAcoes);

        ResponseEntity<List<AcaoDTO>> retorno = acaoController.listarPorReuniao(idSimulacao);

        assertNotNull(retorno);
        assertEquals(HttpStatus.OK, retorno.getStatusCode());
        assertNotNull(retorno.getBody());
    }

    // testes abaixo
}
