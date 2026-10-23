package br.com.empresa.reunioes.colaborador;

import br.com.empresa.reunioes.application.colaborador.ColaboradorService;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.enums.Papel;
import br.com.empresa.reunioes.domain.exception.EmailCadastradoExistenteException;
import br.com.empresa.reunioes.domain.exception.RecursoNaoEncontradoException;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.web.colaborador.dto.request.ColaboradorPatchRequest;
import br.com.empresa.reunioes.web.colaborador.dto.request.ColaboradorRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ColaboradorService")
class ColaboradorServiceTest {

    private static final String MENSAGEM_EMAIL_DUPLICADO = "Este e-mail já existe, cadastre outro.";

    @Mock
    private ColaboradorRepository colaboradorRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private ColaboradorService service;

    private Colaborador colaborador(Long id, String nome, String email) {
        Colaborador colaborador = new Colaborador();
        colaborador.setId(id);
        colaborador.setNome(nome);
        colaborador.setEmail(email);
        colaborador.setSenha("hash-existente");
        colaborador.setMonitorarReunioes(false);
        colaborador.setDataCadastro("2026-01-10");
        return colaborador;
    }

    private ColaboradorRequest requisicao(String nome, String email) {
        return new ColaboradorRequest(nome, email, "senha123", Papel.USUARIO, true, "2026-02-01");
    }

    private ColaboradorPatchRequest requisicaoParcial(String nome, String email) {
        return new ColaboradorPatchRequest(nome, email, null, null, null, null);
    }

    @Nested
    @DisplayName("salvar")
    class Salvar {

        @Test
        @DisplayName("Dado e-mail inédito, quando salvar, então criptografa a senha e persiste o colaborador")
        void emailInedito_deveCriptografarSenhaEPersistirColaborador() {
            ColaboradorRequest request = requisicao("Maria Souza", "maria.souza@empresa.com");
            when(colaboradorRepository.existsByEmail("maria.souza@empresa.com")).thenReturn(false);
            when(passwordEncoder.encode("senha123")).thenReturn("hash-bcrypt");
            when(colaboradorRepository.save(any(Colaborador.class))).thenAnswer(invocacao -> {
                Colaborador entidade = invocacao.getArgument(0);
                entidade.setId(10L);
                return entidade;
            });

            Colaborador resultado = service.salvar(request);

            ArgumentCaptor<Colaborador> captor = ArgumentCaptor.forClass(Colaborador.class);
            verify(colaboradorRepository).save(captor.capture());
            Colaborador persistido = captor.getValue();
            assertThat(persistido.getNome()).isEqualTo("Maria Souza");
            assertThat(persistido.getEmail()).isEqualTo("maria.souza@empresa.com");
            assertThat(persistido.getSenha()).isEqualTo("hash-bcrypt").isNotEqualTo("senha123");
            assertThat(resultado.getId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("Dado e-mail já cadastrado, quando salvar, então lança exceção e não persiste nem criptografa")
        void emailJaCadastrado_deveLancarExcecaoENaoPersistir() {
            ColaboradorRequest request = requisicao("Maria Souza", "maria.souza@empresa.com");
            when(colaboradorRepository.existsByEmail("maria.souza@empresa.com")).thenReturn(true);

            assertThatThrownBy(() -> service.salvar(request))
                    .isInstanceOf(EmailCadastradoExistenteException.class)
                    .hasMessage(MENSAGEM_EMAIL_DUPLICADO);

            verify(colaboradorRepository, never()).save(any());
            verifyNoInteractions(passwordEncoder);
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("Dado id existente, quando buscar, então retorna o colaborador encontrado")
        void idExistente_deveRetornarColaborador() {
            Colaborador existente = colaborador(1L, "Ana Lima", "ana.lima@empresa.com");
            when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(existente));

            Colaborador resultado = service.buscarPorId(1L);

            assertThat(resultado).isSameAs(existente);
            assertThat(resultado.getEmail()).isEqualTo("ana.lima@empresa.com");
        }

        @Test
        @DisplayName("Dado id inexistente, quando buscar, então lança RecursoNaoEncontradoException com o id na mensagem")
        void idInexistente_deveLancarRecursoNaoEncontrado() {
            when(colaboradorRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorId(99L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage("Colaborador não encontrado com ID: 99");
        }
    }

    @Nested
    @DisplayName("buscarPorEmail")
    class BuscarPorEmail {

        @Test
        @DisplayName("Dado e-mail cadastrado, quando buscar, então retorna o colaborador")
        void emailCadastrado_deveRetornarColaborador() {
            Colaborador existente = colaborador(2L, "Bruno Alves", "bruno.alves@empresa.com");
            when(colaboradorRepository.findByEmail("bruno.alves@empresa.com")).thenReturn(existente);

            Colaborador resultado = service.buscarPorEmail("bruno.alves@empresa.com");

            assertThat(resultado).isSameAs(existente);
            assertThat(resultado.getId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("Dado e-mail não cadastrado, quando buscar, então lança RecursoNaoEncontradoException com o e-mail na mensagem")
        void emailNaoCadastrado_deveLancarRecursoNaoEncontrado() {
            when(colaboradorRepository.findByEmail("ninguem@empresa.com")).thenReturn(null);

            assertThatThrownBy(() -> service.buscarPorEmail("ninguem@empresa.com"))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage("Colaborador com e-mail ninguem@empresa.com não encontrado");
        }
    }

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("Dada paginação válida, quando listar, então devolve a página do repositório sem alterar o conteúdo")
        void paginacaoValida_deveDevolverPaginaDoRepositorio() {
            Pageable paginacao = PageRequest.of(0, 10);
            Colaborador primeiro = colaborador(1L, "Ana Lima", "ana.lima@empresa.com");
            Colaborador segundo = colaborador(2L, "Bruno Alves", "bruno.alves@empresa.com");
            Page<Colaborador> pagina = new PageImpl<>(List.of(primeiro, segundo), paginacao, 2);
            when(colaboradorRepository.findAll(paginacao)).thenReturn(pagina);

            Page<Colaborador> resultado = service.listar(paginacao);

            assertThat(resultado.getContent()).containsExactly(primeiro, segundo);
            assertThat(resultado.getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("Dado repositório sem registros, quando listar, então devolve página vazia")
        void semRegistros_deveDevolverPaginaVazia() {
            Pageable paginacao = PageRequest.of(0, 10);
            when(colaboradorRepository.findAll(paginacao)).thenReturn(Page.empty(paginacao));

            Page<Colaborador> resultado = service.listar(paginacao);

            assertThat(resultado.getContent()).isEmpty();
            assertThat(resultado.getTotalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("atualizar")
    class Atualizar {

        @Test
        @DisplayName("Dado o mesmo e-mail, quando atualizar, então altera todos os dados, criptografa a senha e não consulta duplicidade")
        void mesmoEmail_deveAtualizarTodosOsCamposSemConsultarDuplicidade() {
            Colaborador existente = colaborador(1L, "Ana Lima", "ana.lima@empresa.com");
            when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(passwordEncoder.encode("senha123")).thenReturn("hash-novo");
            when(colaboradorRepository.save(any(Colaborador.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
            ColaboradorRequest request = requisicao("Ana Lima Santos", "ana.lima@empresa.com");

            Colaborador resultado = service.atualizar(1L, request);

            assertThat(resultado.getNome()).isEqualTo("Ana Lima Santos");
            assertThat(resultado.getEmail()).isEqualTo("ana.lima@empresa.com");
            assertThat(resultado.getSenha()).isEqualTo("hash-novo");
            assertThat(resultado.getPapel()).isEqualTo(Papel.USUARIO);
            assertThat(resultado.getMonitorarReunioes()).isTrue();
            assertThat(resultado.getDataCadastro()).isEqualTo("2026-02-01");
            verify(colaboradorRepository, never()).existsByEmail(anyString());
            verify(colaboradorRepository).save(existente);
        }

        @Test
        @DisplayName("Dado novo e-mail inédito, quando atualizar, então verifica duplicidade e grava o novo e-mail")
        void novoEmailInedito_deveAtualizarEmail() {
            Colaborador existente = colaborador(1L, "Ana Lima", "ana.lima@empresa.com");
            when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(colaboradorRepository.existsByEmail("ana.nova@empresa.com")).thenReturn(false);
            when(colaboradorRepository.save(any(Colaborador.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
            ColaboradorRequest request = requisicao("Ana Lima", "ana.nova@empresa.com");

            Colaborador resultado = service.atualizar(1L, request);

            assertThat(resultado.getEmail()).isEqualTo("ana.nova@empresa.com");
            verify(colaboradorRepository).existsByEmail("ana.nova@empresa.com");
        }

        @Test
        @DisplayName("Dado novo e-mail de outro colaborador, quando atualizar, então lança exceção e não grava")
        void novoEmailDeOutroColaborador_deveLancarExcecaoENaoGravar() {
            Colaborador existente = colaborador(1L, "Ana Lima", "ana.lima@empresa.com");
            when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(colaboradorRepository.existsByEmail("bruno.alves@empresa.com")).thenReturn(true);
            ColaboradorRequest request = requisicao("Ana Lima", "bruno.alves@empresa.com");

            assertThatThrownBy(() -> service.atualizar(1L, request))
                    .isInstanceOf(EmailCadastradoExistenteException.class)
                    .hasMessage(MENSAGEM_EMAIL_DUPLICADO);

            assertThat(existente.getEmail()).isEqualTo("ana.lima@empresa.com");
            verify(colaboradorRepository, never()).save(any());
        }

        @Test
        @DisplayName("Dado id inexistente, quando atualizar, então lança RecursoNaoEncontradoException e não grava")
        void idInexistente_deveLancarRecursoNaoEncontrado() {
            when(colaboradorRepository.findById(99L)).thenReturn(Optional.empty());
            ColaboradorRequest request = requisicao("Ana Lima", "ana.lima@empresa.com");

            assertThatThrownBy(() -> service.atualizar(99L, request))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage("Colaborador não encontrado com ID: 99");

            verify(colaboradorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("atualizarParcial")
    class AtualizarParcial {

        @Test
        @DisplayName("Dado apenas o nome, quando atualizar parcialmente, então altera só o nome e não toca em e-mail nem senha")
        void apenasNome_deveAlterarNomeEManterDemaisCampos() {
            Colaborador existente = colaborador(1L, "Ana Lima", "ana.lima@empresa.com");
            when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(colaboradorRepository.save(any(Colaborador.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

            Colaborador resultado = service.atualizarParcial(1L, requisicaoParcial("Ana Santos", null));

            assertThat(resultado.getNome()).isEqualTo("Ana Santos");
            assertThat(resultado.getEmail()).isEqualTo("ana.lima@empresa.com");
            assertThat(resultado.getSenha()).isEqualTo("hash-existente");
            verify(colaboradorRepository, never()).existsByEmail(anyString());
            verifyNoInteractions(passwordEncoder);
        }

        @Test
        @DisplayName("Dado o mesmo e-mail, quando atualizar parcialmente, então não consulta duplicidade")
        void mesmoEmail_naoDeveConsultarDuplicidade() {
            Colaborador existente = colaborador(1L, "Ana Lima", "ana.lima@empresa.com");
            when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(colaboradorRepository.save(any(Colaborador.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

            Colaborador resultado = service.atualizarParcial(1L, requisicaoParcial(null, "ana.lima@empresa.com"));

            assertThat(resultado.getEmail()).isEqualTo("ana.lima@empresa.com");
            verify(colaboradorRepository, never()).existsByEmail(anyString());
        }

        @Test
        @DisplayName("Dado novo e-mail inédito, quando atualizar parcialmente, então grava o novo e-mail")
        void novoEmailInedito_deveAtualizarEmail() {
            Colaborador existente = colaborador(1L, "Ana Lima", "ana.lima@empresa.com");
            when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(colaboradorRepository.existsByEmail("ana.nova@empresa.com")).thenReturn(false);
            when(colaboradorRepository.save(any(Colaborador.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

            Colaborador resultado = service.atualizarParcial(1L, requisicaoParcial(null, "ana.nova@empresa.com"));

            assertThat(resultado.getEmail()).isEqualTo("ana.nova@empresa.com");
            assertThat(resultado.getNome()).isEqualTo("Ana Lima");
        }

        @Test
        @DisplayName("Dado novo e-mail de outro colaborador, quando atualizar parcialmente, então lança exceção e não grava")
        void novoEmailDeOutroColaborador_deveLancarExcecaoENaoGravar() {
            Colaborador existente = colaborador(1L, "Ana Lima", "ana.lima@empresa.com");
            when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(colaboradorRepository.existsByEmail("bruno.alves@empresa.com")).thenReturn(true);

            assertThatThrownBy(() -> service.atualizarParcial(1L, requisicaoParcial(null, "bruno.alves@empresa.com")))
                    .isInstanceOf(EmailCadastradoExistenteException.class)
                    .hasMessage(MENSAGEM_EMAIL_DUPLICADO);

            assertThat(existente.getEmail()).isEqualTo("ana.lima@empresa.com");
            verify(colaboradorRepository, never()).save(any());
        }

        @Test
        @DisplayName("Dados todos os campos, quando atualizar parcialmente, então altera todos e criptografa a senha")
        void todosOsCampos_deveAlterarTudoECriptografarSenha() {
            Colaborador existente = colaborador(1L, "Ana Lima", "ana.lima@empresa.com");
            when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(colaboradorRepository.existsByEmail("ana.nova@empresa.com")).thenReturn(false);
            when(passwordEncoder.encode("novaSenha")).thenReturn("hash-novo");
            when(colaboradorRepository.save(any(Colaborador.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
            ColaboradorPatchRequest request = new ColaboradorPatchRequest(
                    "Ana Santos", "ana.nova@empresa.com", "novaSenha", Papel.USUARIO, true, "2026-03-15");

            Colaborador resultado = service.atualizarParcial(1L, request);

            assertThat(resultado.getNome()).isEqualTo("Ana Santos");
            assertThat(resultado.getEmail()).isEqualTo("ana.nova@empresa.com");
            assertThat(resultado.getSenha()).isEqualTo("hash-novo");
            assertThat(resultado.getPapel()).isEqualTo(Papel.USUARIO);
            assertThat(resultado.getMonitorarReunioes()).isTrue();
            assertThat(resultado.getDataCadastro()).isEqualTo("2026-03-15");
        }

        @Test
        @DisplayName("Dado id inexistente, quando atualizar parcialmente, então lança RecursoNaoEncontradoException")
        void idInexistente_deveLancarRecursoNaoEncontrado() {
            when(colaboradorRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.atualizarParcial(99L, requisicaoParcial("Qualquer", null)))
                    .isInstanceOf(RecursoNaoEncontradoException.class);

            verify(colaboradorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deletar")
    class Deletar {

        @Test
        @DisplayName("Dado id existente, quando deletar, então remove exatamente o colaborador encontrado")
        void idExistente_deveRemoverColaborador() {
            Colaborador existente = colaborador(1L, "Ana Lima", "ana.lima@empresa.com");
            when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(existente));

            service.deletar(1L);

            verify(colaboradorRepository).delete(existente);
        }

        @Test
        @DisplayName("Dado id inexistente, quando deletar, então lança RecursoNaoEncontradoException e não remove nada")
        void idInexistente_deveLancarRecursoNaoEncontradoENaoRemover() {
            when(colaboradorRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deletar(99L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage("Colaborador não encontrado com ID: 99");

            verify(colaboradorRepository, never()).delete(any());
        }
    }
}