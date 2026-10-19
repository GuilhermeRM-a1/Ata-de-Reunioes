package br.com.empresa.reunioes.colaborador;

import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.web.acao.AcaoController;
import br.com.empresa.reunioes.web.colaborador.ColaboradorController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class ColaboradorControllerTest {

    @Autowired
    ColaboradorController colaboradorController;

    @MockitoBean
    ColaboradorRepository colaboradorRepository;

    // testes abaixo
}
