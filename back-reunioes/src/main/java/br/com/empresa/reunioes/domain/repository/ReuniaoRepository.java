package br.com.empresa.reunioes.domain.repository;

import br.com.empresa.reunioes.domain.model.Reuniao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {
}
