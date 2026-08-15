package br.com.empresa.reunioes.web.controller;

import br.com.empresa.reunioes.application.service.ReuniaoService;
import br.com.empresa.reunioes.web.controller.dto.PaginaResponse;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoDTO;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReuniaoController {

    private final ReuniaoService reuniaoService;

    /** Listagem paginada no envelope padrao: content, page, size, totalElements, totalPages. */
    @GetMapping()
    public ResponseEntity<PaginaResponse<ReuniaoDTO>> listar (Pageable paginacao) {

        return ResponseEntity.ok(reuniaoService.listar(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReuniaoDTO> buscarPorId (@PathVariable Long id) {

        return new ResponseEntity<>(reuniaoService.buscarPorId(id), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<ReuniaoDTO> salvar (@Valid @RequestBody ReuniaoRequest request) {

        return new ResponseEntity<>(reuniaoService.salvar(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReuniaoDTO> atualizar (@PathVariable Long id,
                                                 @Valid @RequestBody ReuniaoRequest request) {

        return new ResponseEntity<>(reuniaoService.atualizar(id, request), HttpStatus.ACCEPTED);
    }

    /** Sem @Valid de proposito: no PATCH, campo nulo significa "nao mexer". */
    @PatchMapping("/{id}")
    public ResponseEntity<ReuniaoDTO> atualizarParcial (@PathVariable Long id,
                                                        @RequestBody ReuniaoRequest request) {

        return new ResponseEntity<>(reuniaoService.atualizarParcial(id, request), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        reuniaoService.deletar(id);

        return ResponseEntity.noContent().build();
    }

}
