package br.com.empresa.reunioes.acao.mapper;

import br.com.empresa.reunioes.application.acao.mapper.AcaoMapper;
import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.web.acao.dto.request.AcaoRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AcaoMapperTest {

    @Test
    void deveConverterRequestParaEntity() {

        // Arrange
        var request = new AcaoRequest("Fazer relatório semanal",
                "Realizar um relatório semanal",
                "TAREFA",
                "2026-09-19",
                false,
                List.of(1L));


        // Act
        Acao acao = AcaoMapper.acaoToEntity(request);

        // Assert
        assertAll(
                () -> assertEquals(request.titulo(), acao.getTitulo()),
                () -> assertEquals(request.descricao(), acao.getDescricao()),
                () -> assertEquals(request.tipo(), acao.getTipo()),
                () -> assertEquals(request.prazo(), acao.getPrazo()),
                () -> assertEquals(request.concluida(), acao.getConcluida())
        );
    }

}