package br.com.empresa.reunioes.web.controller;

import br.com.empresa.reunioes.web.controller.dto.ColaboradorRequest;
import br.com.empresa.reunioes.web.controller.dto.ColaboradorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reunioes/colaboradores")
public class ColaboradorController {

    @GetMapping()
    public ResponseEntity<ColaboradorResponse> listar (ColaboradorRequest request) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColaboradorResponse> buscarPorId (Long id) {
        return null;
    }

    @PostMapping()
    public ResponseEntity<ColaboradorResponse> salvar (ColaboradorRequest request) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColaboradorResponse> atualizar (Long id, ColaboradorRequest request) {
        return null;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ColaboradorResponse> atualizarParcial (Long id, ColaboradorRequest request) {
        return null;
    }

    @DeleteMapping()
    public ResponseEntity<ColaboradorResponse> deletar(Long id) {
        return null;
    }


}
