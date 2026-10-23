package br.com.empresa.reunioes.acao;

import br.com.empresa.reunioes.application.acao.AcaoService;
import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.web.acao.AcaoController;
import br.com.empresa.reunioes.web.acao.dto.AcaoDTO;
import br.com.empresa.reunioes.web.acao.dto.AcaoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AcaoControllerTest {



    @Mock
    private AcaoService acaoService;

    @InjectMocks
    private AcaoController acaoController;


    @Test
    void cenario_salvarAcao(){

        Long idSimulacao = 1L;


        AcaoRequest request = new AcaoRequest("Título Atualizado",
                "Descrição",
                "Tecnica",
                "2026-10-20",
                false,
                null);

        Reuniao reuniao = new Reuniao();
        reuniao.setId(idSimulacao);

        Acao acaoSimulacao = new Acao();
        acaoSimulacao.setId(1L);
        acaoSimulacao.setReuniao(reuniao);
        when(acaoService.salvar(eq(idSimulacao), any(AcaoRequest.class))).thenReturn(acaoSimulacao);



        ResponseEntity<AcaoDTO> retorno = acaoController.salvar(idSimulacao, request);

        assertNotNull(retorno);
        assertEquals(HttpStatus.CREATED, retorno.getStatusCode());
        assertNotNull(retorno.getBody());
    }

    // testes abaixo
}
