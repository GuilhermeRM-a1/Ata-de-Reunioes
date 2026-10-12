package br.com.empresa.reunioes.acao;

import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.web.acao.AcaoController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class AcaoControllerTest {

    @Autowired
    AcaoController acaoController;

    @MockitoBean
    AcaoRepository acaoRepository;

    // testes abaixo
}
