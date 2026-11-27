package br.com.empresa.reunioes.domain.entity;

import br.com.empresa.reunioes.domain.enums.Papel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "colaborador")
public class Colaborador implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "papel")
    private Papel papel;

    @Column(name = "monitorar_reunioes")
    private Boolean monitorarReunioes;

    @Column(name = "data_cadastro")
    private String dataCadastro;

    @ManyToMany(mappedBy = "responsavel")
    private List<Acao> acoes;


    /** Lado inverso. Quem grava o vinculo e Reuniao.participantes. */
    @ManyToMany(mappedBy = "participantes")
    private List<Reuniao> reunioes;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<SimpleGrantedAuthority> grantAutority = new ArrayList<>();
        grantAutority.add(new SimpleGrantedAuthority(papel.name()));
        return grantAutority;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
