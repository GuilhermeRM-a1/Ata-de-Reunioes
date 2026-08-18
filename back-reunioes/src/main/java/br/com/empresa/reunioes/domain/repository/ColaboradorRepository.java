package br.com.empresa.reunioes.domain.repository;

import br.com.empresa.reunioes.domain.entity.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {

    Colaborador findByMonitorarReunioesTrue();
}
