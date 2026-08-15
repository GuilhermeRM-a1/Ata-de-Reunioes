package br.com.empresa.reunioes.domain.repository;

import br.com.empresa.reunioes.domain.model.Acao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcaoRepository extends JpaRepository<Acao, Long> {

}
