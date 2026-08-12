package br.com.empresa.reunioes.domain.repository;

import br.com.empresa.reunioes.domain.model.ArquivoProcessado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArquivoProcessadoRepository extends JpaRepository<ArquivoProcessado, Long> {

    boolean existsByFileId(Long fileId);
}
