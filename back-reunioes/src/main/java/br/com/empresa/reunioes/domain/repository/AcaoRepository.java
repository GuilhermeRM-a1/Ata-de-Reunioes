package br.com.empresa.reunioes.domain.repository;

import br.com.empresa.reunioes.domain.entity.Acao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcaoRepository extends JpaRepository<Acao, Long> {

    List<Acao> findAllByReuniaoId(Long reuniaoId);

}
