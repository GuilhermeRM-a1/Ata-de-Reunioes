package br.com.empresa.reunioes.web.controller;

import br.com.empresa.reunioes.application.service.ReuniaoService;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoDTO;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReuniaoController {

    private final ReuniaoService reuniaoService;

    @GetMapping()
    public ResponseEntity<List<ReuniaoDTO>> listar () {

        return ResponseEntity.ok(reuniaoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReuniaoDTO> buscarPorId (@PathVariable Long id) {

        return new ResponseEntity<>(reuniaoService.buscarPorId(id), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<ReuniaoDTO> salvar (@RequestBody ReuniaoRequest request) {

        return new ResponseEntity<>(reuniaoService.salvar(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReuniaoDTO> atualizar (@PathVariable Long id, @RequestBody ReuniaoRequest request) {

        return new ResponseEntity<>(reuniaoService.atualizar(id, request), HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReuniaoDTO> atualizarParcial (@PathVariable Long id, @RequestBody ReuniaoRequest request) {

        return new ResponseEntity<>(reuniaoService.atualizarParcial(id, request), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        reuniaoService.deletar(id);

        return ResponseEntity.noContent().build();
    }

}
