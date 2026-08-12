package br.com.empresa.reunioes.web.controller;

import br.com.empresa.reunioes.application.service.ReuniaoService;
import br.com.empresa.reunioes.web.controller.dto.ColaboradorRequest;
import br.com.empresa.reunioes.web.controller.dto.ColaboradorResponse;
import br.com.empresa.reunioes.web.controller.dto.ReuniaoRequest;
import br.com.empresa.reunioes.web.controller.dto.ReuniaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReuniaoController {

    private final ReuniaoService reuniaoService;

    @GetMapping()
    public ResponseEntity<ReuniaoResponse> listar (ReuniaoRequest request) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReuniaoResponse> buscarPorId (Long id) {
        return null;
    }

    @PostMapping()
    public ResponseEntity<ReuniaoResponse> salvar (ReuniaoRequest request) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReuniaoResponse> atualizar (Long id, ReuniaoRequest request) {
        return null;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReuniaoResponse> atualizarParcial (Long id, ReuniaoRequest request) {
        return null;
    }

    @DeleteMapping()
    public ResponseEntity<ReuniaoResponse> deletar(Long id) {
        return null;
    }

}
