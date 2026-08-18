package br.com.empresa.reunioes.web.controller;

import br.com.empresa.reunioes.application.service.AcaoService;
import br.com.empresa.reunioes.web.controller.dto.Acao.AcaoDTO;
import br.com.empresa.reunioes.web.controller.dto.Acao.AcaoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reunioes/acoes")
@Tag(name = "Ações", description = "CRUD de ações")
public class AcaoController {

    private final AcaoService service;

    @Operation(summary = "Lista ações",
            description = "Devolve a lista de ações cadastradas.")
    @ApiResponse(responseCode = "200", description = "Lista de ações devolvida")
    @GetMapping()
    public ResponseEntity<List<AcaoDTO>> listar() {

        return ResponseEntity.ok(service.listar());
    }

    @Operation(summary = "Busca uma ação pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ação encontrada"),
            @ApiResponse(responseCode = "404", description = "Nenhuma ação com esse id"),
            @ApiResponse(responseCode = "400", description = "Id em formato inválido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AcaoDTO> buscarPorId(@PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Cria uma ação",
            description = "Os ids dos responsáveis e da reunião são resolvidos para as entidades correspondentes.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ação criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Responsável ou reunião informada não existe")
    })
    @PostMapping()
    public ResponseEntity<AcaoDTO> salvar(@Valid @RequestBody AcaoRequest request) {

        return new ResponseEntity<>(service.salvar(request), HttpStatus.CREATED);
    }

    // PUT dando 500 por algum motivo
    @Operation(summary = "Substitui uma ação por completo")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Ação atualizada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Nenhuma ação com esse id")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AcaoDTO> atualizar(@PathVariable Long id,
                                             @Valid @RequestBody AcaoRequest request) {

        return new ResponseEntity<>(service.atualizar(id, request), HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Atualiza parcialmente uma ação",
            description = "Campo ausente ou nulo é ignorado — só o que vier no corpo é alterado.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Ação atualizada"),
            @ApiResponse(responseCode = "404", description = "Nenhuma ação com esse id")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AcaoDTO> atualizarParcial(@PathVariable Long id,
                                                    @RequestBody AcaoRequest request) {

        return new ResponseEntity<>(service.atualizarParcial(id, request), HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Exclui uma ação")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ação excluída"),
            @ApiResponse(responseCode = "404", description = "Nenhuma ação com esse id")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        service.deletar(id);

        return ResponseEntity.noContent().build();
    }
}