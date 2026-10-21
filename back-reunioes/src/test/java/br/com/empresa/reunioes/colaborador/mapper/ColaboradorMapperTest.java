package br.com.empresa.reunioes.colaborador.mapper;

import br.com.empresa.reunioes.application.colaborador.mapper.ColaboradorMapper;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.enums.Papel;
import br.com.empresa.reunioes.web.colaborador.dto.ColaboradorRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColaboradorMapperTest {

    @Test
    void deveConverterRequestParaEntity() {

        // Arrange
        var request = new ColaboradorRequest("Roberto da Silva",
                "roberto.silva@email.com",
                "senha123",
                Papel.USUARIO,
                true,
                "2026-10-20");

        // Act
        Colaborador colaborador = ColaboradorMapper.colaboradorToEntity(request);

        // Assert
        assertAll(
                () -> assertEquals(request.nome(), colaborador.getNome()),
                () -> assertEquals(request.email(), colaborador.getEmail()),
                () -> assertEquals(request.senha(), colaborador.getSenha()),
                () -> assertEquals(request.papel(), colaborador.getPapel()),
                () -> assertEquals(request.dataCadastro(), colaborador.getDataCadastro())
        );
    }
}