package br.com.empresa.reunioes.web.controller;

import br.com.empresa.reunioes.application.service.ColaboradorService;
import br.com.empresa.reunioes.domain.model.Colaborador;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorRequest;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reunioes/colaboradores")
@RequiredArgsConstructor
public class ColaboradorController {

    private final ColaboradorService service;

    @PostMapping()
    public ResponseEntity<ColaboradorDTO> salvar (@RequestBody ColaboradorRequest request) {
        try {
            Colaborador colaborador = this.service.salvar(request);

            return new ResponseEntity<>(ColaboradorDTO.de(colaborador),HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    public ResponseEntity<List<ColaboradorDTO>> listar () {
        List<ColaboradorDTO> colaboradores = service.listar().stream()
                .map(ColaboradorDTO::de)
                .toList();

        return ResponseEntity.ok(colaboradores);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ColaboradorDTO> buscarPorId (@PathVariable Long id) {

        Colaborador colaborador = this.service.buscarPorId(id);

        return ResponseEntity.ok(ColaboradorDTO.de(colaborador));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ColaboradorDTO> atualizar (@PathVariable Long id, @RequestBody ColaboradorRequest request) {
        try {
            Colaborador colaborador = this.service.atualizar(id, request);

            return new ResponseEntity<>(ColaboradorDTO.de(colaborador), HttpStatus.ACCEPTED);

        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ColaboradorDTO> atualizarParcial (@PathVariable Long id, @RequestBody ColaboradorRequest request) {
        try {
            Colaborador colaborador = this.service.atualizarParcial(id, request);
            
            return new ResponseEntity<>(ColaboradorDTO.de(colaborador), HttpStatus.ACCEPTED);

        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping()
    public ResponseEntity<ColaboradorDTO> deletar(@PathVariable Long id) {
        this.service.deletar(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


}
