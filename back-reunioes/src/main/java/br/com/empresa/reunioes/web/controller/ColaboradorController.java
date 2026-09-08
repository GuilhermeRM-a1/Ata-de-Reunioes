package br.com.empresa.reunioes.web.controller;

import br.com.empresa.reunioes.application.mapper.ColaboradorMapper;
import br.com.empresa.reunioes.application.service.ColaboradorService;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorDTO;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorPatchRequest;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorRequest;
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

import java.util.List;

@RestController
@RequestMapping("/api/reunioes/colaboradores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Colaboradores", description = "CRUD de colaboradores")
public class ColaboradorController {

    private final ColaboradorService service;
    private final ColaboradorMapper mapper;

    @Operation(summary = "Cria um colaborador",
            description = "A senha é gravada na entidade, mas nunca volta na resposta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Colaborador criado"),
            @ApiResponse(responseCode = "400", description = "Campos obrigatórios ausentes ou inválidos")
    })
    @PostMapping()
    public ResponseEntity<ColaboradorDTO> salvar(@Valid @RequestBody ColaboradorRequest request) {

        Colaborador colaborador = this.service.salvar(request);
        ColaboradorDTO dto = mapper.toDTO(colaborador);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Operation(summary = "Lista colaboradores paginados",
            description = "Devolve o envelope padrão com content, page, size, totalElements e totalPages.")
    @ApiResponse(responseCode = "200", description = "Página de colaboradores devolvida")
    @GetMapping()
    public ResponseEntity<List<ColaboradorDTO>> listar(Pageable paginacao) {

        List<ColaboradorDTO> listagemDTO = service.listar()
                .stream()
                .map(mapper::toDTO)
                .toList();

        return ResponseEntity.ok(listagemDTO);
    }

    @Operation(summary = "Busca um colaborador pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Colaborador encontrado"),
            @ApiResponse(responseCode = "404", description = "Nenhum colaborador com esse id")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ColaboradorDTO> buscarPorId(@PathVariable Long id) {
        Colaborador colaborador = service.buscarPorId(id);
        ColaboradorDTO dto = mapper.toDTO(colaborador);

        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Substitui um colaborador por completo")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Colaborador atualizado"),
            @ApiResponse(responseCode = "400", description = "Campos obrigatórios ausentes ou inválidos"),
            @ApiResponse(responseCode = "404", description = "Nenhum colaborador com esse id")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ColaboradorDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ColaboradorRequest request) {
        Colaborador colaborador = service.atualizar(id, request);
        ColaboradorDTO dto = mapper.toDTO(colaborador);

        return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Atualiza parcialmente um colaborador",
            description = "Campo ausente ou nulo é ignorado — só o que vier no corpo é alterado. "
                    + "O campo que vier preenchido não pode ser vazio.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Colaborador atualizado"),
            @ApiResponse(responseCode = "400", description = "Campo enviado veio vazio"),
            @ApiResponse(responseCode = "404", description = "Nenhum colaborador com esse id")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ColaboradorDTO> atualizarParcial(@PathVariable Long id,
                                                           @Valid @RequestBody ColaboradorPatchRequest request) {

        Colaborador colaborador = service.atualizarParcial(id, request);
        ColaboradorDTO dto = mapper.toDTO(colaborador);

        return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
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
