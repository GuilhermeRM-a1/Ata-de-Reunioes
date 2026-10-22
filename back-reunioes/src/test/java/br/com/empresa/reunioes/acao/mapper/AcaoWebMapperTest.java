package br.com.empresa.reunioes.acao.mapper;

import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.enums.Papel;
import br.com.empresa.reunioes.web.acao.mapper.AcaoWebMapper;
import br.com.empresa.reunioes.web.colaborador.mapper.ColaboradorWebMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AcaoWebMapperTest {


    @Test
    void deveConverterEntidadeParaDTO() {

        // Arrange
        var reuniao = new Reuniao();
        reuniao.setId(1L);

        var responsavel = new Colaborador();
        responsavel.setId(1L);
        responsavel.setNome("Carlos Ribeiro");

        var acaoEntity = new Acao();
        acaoEntity.setId(1L);
        acaoEntity.setTitulo("Testar DTO");
        acaoEntity.setDescricao("Realizar teste de conversão");
        acaoEntity.setConcluida(false);
        acaoEntity.setPrazo("2026-10-28");
        acaoEntity.setTipo("ACAO");
        acaoEntity.setResponsavel(List.of(responsavel));
        acaoEntity.setReuniao(reuniao);

        // Act
        var acaoDTO = AcaoWebMapper.acaoToDTO(acaoEntity);

        // Assert
        assertAll(
                () -> assertEquals(acaoEntity.getId(), acaoDTO.id()),
                () -> assertEquals(acaoEntity.getTitulo(), acaoDTO.titulo()),
                () -> assertEquals(acaoEntity.getDescricao(), acaoDTO.descricao()),
                () -> assertEquals(acaoEntity.getConcluida(), acaoDTO.concluida()),
                () -> assertEquals(acaoEntity.getPrazo(), acaoDTO.prazo()),
                () -> assertEquals(acaoEntity.getTipo(), acaoDTO.tipo())
        );
    }


}
