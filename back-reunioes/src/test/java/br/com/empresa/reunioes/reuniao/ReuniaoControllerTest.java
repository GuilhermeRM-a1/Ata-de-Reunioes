package br.com.empresa.reunioes.reuniao;

import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.acao.AcaoController;
import br.com.empresa.reunioes.web.reuniao.ReuniaoController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class ReuniaoControllerTest {

    @Autowired
    ReuniaoController reuniaoController;

    @MockitoBean
    ReuniaoRepository reuniaoRepository;

    // testes abaixo
}
