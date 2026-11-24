package br.com.empresa.reunioes.application.login;

import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Ensina o Spring Security a achar um usuario pelo email.
 *
 * O Colaborador ja implementa UserDetails, entao nao ha conversao: o proprio
 * registro do banco e devolvido. O AuthenticationManager usa este service,
 * junto com o BCryptPasswordEncoder, para conferir email e senha no login.
 */
@Service
@RequiredArgsConstructor
public class ColaboradorDetailsService implements UserDetailsService {

    private final ColaboradorRepository colaboradorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // findByEmail devolve null quando nao acha; o Spring espera a excecao
        Colaborador colaborador = colaboradorRepository.findByEmail(email);

        if (colaborador == null) {
            throw new UsernameNotFoundException("Nenhum colaborador com este email.");
        }

        return colaborador;
    }
}