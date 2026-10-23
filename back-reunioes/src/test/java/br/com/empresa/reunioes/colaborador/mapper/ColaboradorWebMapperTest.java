package br.com.empresa.reunioes.colaborador.mapper;

import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.enums.Papel;
import br.com.empresa.reunioes.web.colaborador.mapper.ColaboradorWebMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ColaboradorWebMapperTest {

    @Test
    void deveConverterEntidadeParaDTO() {

        // Arrange
        var colaboradorEntity = new Colaborador();
        colaboradorEntity.setId(1L);
        colaboradorEntity.setNome("Carlos Vieira");
        colaboradorEntity.setEmail("carlos.vieira@email.com");
        colaboradorEntity.setSenha("senha123");
        colaboradorEntity.setPapel(Papel.USUARIO);

        // Act
        var colaboradorDTO = ColaboradorWebMapper.colaboradorToDTO(colaboradorEntity);

        // Arrange
        assertAll(
                () -> assertEquals(colaboradorEntity.getId(), colaboradorDTO.id()),
                () -> assertEquals(colaboradorEntity.getNome(), colaboradorDTO.nome()),
                () -> assertEquals(colaboradorEntity.getEmail(), colaboradorDTO.email()),
                () -> assertEquals(colaboradorEntity.getPapel(), colaboradorDTO.papel())
        );

    }
}
