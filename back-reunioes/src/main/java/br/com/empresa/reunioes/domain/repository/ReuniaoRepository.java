package br.com.empresa.reunioes.domain.repository;

import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.enums.StatusReuniao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {

    long countByStatusReuniao(StatusReuniao status);
    long countByStatusReuniaoAndDataGreaterThanEqual(StatusReuniao status, String inicio);
}
