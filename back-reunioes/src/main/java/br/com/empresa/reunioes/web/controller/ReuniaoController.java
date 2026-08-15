package br.com.empresa.reunioes.web.controller;

import br.com.empresa.reunioes.application.service.ReuniaoService;
import br.com.empresa.reunioes.domain.model.Colaborador;
import br.com.empresa.reunioes.domain.model.Reuniao;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoRequest;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoDTO;
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

        List<ReuniaoDTO> reunioes = reuniaoService.listar().stream()
                .map(ReuniaoDTO::de)
                .toList();

        return ResponseEntity.ok(reunioes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReuniaoDTO> buscarPorId (@PathVariable Long id) {

        Reuniao reuniao = reuniaoService.buscarPorId(id);

        return new ResponseEntity<>(ReuniaoDTO.de(reuniao), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<ReuniaoDTO> salvar (@RequestBody ReuniaoRequest request) {
        try {
            Reuniao reuniao = reuniaoService.salvar(request);
            return new ResponseEntity<>(ReuniaoDTO.de(reuniao), HttpStatus.CREATED);

        } catch(Exception e) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReuniaoDTO> atualizar (@PathVariable Long id, @RequestBody ReuniaoRequest request) {

        Reuniao reuniao = reuniaoService.atualizarParcial(id, request);

        return new ResponseEntity<>(ReuniaoDTO.de(reuniao), HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReuniaoDTO> atualizarParcial (@PathVariable Long id, @RequestBody ReuniaoRequest request) {

        Reuniao reuniao = reuniaoService.atualizarParcial(id, request);

        return new ResponseEntity<>(ReuniaoDTO.de(reuniao), HttpStatus.ACCEPTED);
    }

    @DeleteMapping()
    public ResponseEntity<ReuniaoDTO> deletar(Long id) {
        reuniaoService.deletar(id);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
