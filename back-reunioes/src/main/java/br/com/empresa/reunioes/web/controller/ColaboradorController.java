package br.com.empresa.reunioes.web.controller;

import br.com.empresa.reunioes.application.service.ColaboradorService;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorDTO;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorRequest;
import br.com.empresa.reunioes.web.controller.dto.PaginaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reunioes/colaboradores")
@RequiredArgsConstructor
@Tag(name = "Colaboradores", description = "CRUD de colaboradores")
public class ColaboradorController {

    private final ColaboradorService service;

    @Operation(summary = "Cria um colaborador",
            description = "A senha é gravada na entidade, mas nunca volta na resposta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Colaborador criado"),
            @ApiResponse(responseCode = "400", description = "Campos obrigatórios ausentes ou inválidos")
    })
    @PostMapping()
    public ResponseEntity<ColaboradorDTO> salvar (@Valid @RequestBody ColaboradorRequest request) {

        return new ResponseEntity<>(this.service.salvar(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Lista colaboradores paginados",
            description = "Devolve o envelope padrão com content, page, size, totalElements e totalPages.")
    @ApiResponse(responseCode = "200", description = "Página de colaboradores devolvida")
    @GetMapping()
    public ResponseEntity<PaginaResponse<ColaboradorDTO>> listar (Pageable paginacao) {

        return ResponseEntity.ok(service.listar(paginacao));
    }

    @Operation(summary = "Busca um colaborador pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Colaborador encontrado"),
            @ApiResponse(responseCode = "404", description = "Nenhum colaborador com esse id")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ColaboradorDTO> buscarPorId (@PathVariable Long id) {

        return ResponseEntity.ok(this.service.buscarPorId(id));
    }

    @Operation(summary = "Substitui um colaborador por completo")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Colaborador atualizado"),
            @ApiResponse(responseCode = "400", description = "Campos obrigatórios ausentes ou inválidos"),
            @ApiResponse(responseCode = "404", description = "Nenhum colaborador com esse id")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ColaboradorDTO> atualizar (@PathVariable Long id, @Valid @RequestBody ColaboradorRequest request) {

        return new ResponseEntity<>(this.service.atualizar(id, request), HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Atualiza parcialmente um colaborador",
            description = "Campo ausente ou nulo é ignorado — só o que vier no corpo é alterado.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Colaborador atualizado"),
            @ApiResponse(responseCode = "404", description = "Nenhum colaborador com esse id")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ColaboradorDTO> atualizarParcial (@PathVariable Long id, @RequestBody ColaboradorRequest request) {

        return new ResponseEntity<>(this.service.atualizarParcial(id, request), HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Exclui um colaborador")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Colaborador excluído"),
            @ApiResponse(responseCode = "404", description = "Nenhum colaborador com esse id")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        this.service.deletar(id);

        return ResponseEntity.noContent().build();
    }

}
