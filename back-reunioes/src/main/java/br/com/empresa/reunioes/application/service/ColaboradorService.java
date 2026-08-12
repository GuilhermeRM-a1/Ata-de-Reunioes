package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.domain.model.Colaborador;
import br.com.empresa.reunioes.domain.model.Reuniao;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColaboradorService {

    private final ColaboradorRepository reuniaoRepository;

    public Colaborador salvar(Colaborador colaborador) {
        return null;
    }

    public Colaborador buscarPorId(Long id) {
        return null;
    }

    public List<Colaborador> listar() {
        return null;
    }

    public Colaborador atualizar(Colaborador colaborador) {
        return null;
    }

    public Colaborador atualizarParcial(Colaborador colaborador) {
        return null;
    }

    public void deletar(Long id) {
    }

}
