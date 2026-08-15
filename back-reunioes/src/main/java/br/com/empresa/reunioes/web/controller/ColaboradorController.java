package br.com.empresa.reunioes.web.controller;

import br.com.empresa.reunioes.application.service.ColaboradorService;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorDTO;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorRequest;
import br.com.empresa.reunioes.web.controller.dto.PaginaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reunioes/colaboradores")
@RequiredArgsConstructor
public class ColaboradorController {

    private final ColaboradorService service;

    @PostMapping()
    public ResponseEntity<ColaboradorDTO> salvar (@Valid @RequestBody ColaboradorRequest request) {

        return new ResponseEntity<>(this.service.salvar(request), HttpStatus.CREATED);
    }

    /** Listagem paginada no envelope padrao: content, page, size, totalElements, totalPages. */
    @GetMapping()
    public ResponseEntity<PaginaResponse<ColaboradorDTO>> listar (Pageable paginacao) {

        return ResponseEntity.ok(service.listar(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColaboradorDTO> buscarPorId (@PathVariable Long id) {

        return ResponseEntity.ok(this.service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColaboradorDTO> atualizar (@PathVariable Long id, @Valid @RequestBody ColaboradorRequest request) {

        return new ResponseEntity<>(this.service.atualizar(id, request), HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ColaboradorDTO> atualizarParcial (@PathVariable Long id, @RequestBody ColaboradorRequest request) {

        return new ResponseEntity<>(this.service.atualizarParcial(id, request), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        this.service.deletar(id);

        return ResponseEntity.noContent().build();
    }

}
