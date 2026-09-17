package br.com.empresa.reunioes.web.controller;

import br.com.empresa.reunioes.application.mapper.ReuniaoMapper;
import br.com.empresa.reunioes.application.service.FeriadoService;
import br.com.empresa.reunioes.application.service.IngestaoService;
import br.com.empresa.reunioes.application.service.RelatorioService;
import br.com.empresa.reunioes.application.service.ReuniaoService;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoDTO;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.RelatorioReuniaoResponse;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoFeriadoDTO;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoRequest;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoResumoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/reunioes")
@Tag(name = "Reuniões", description = "CRUD de reuniões e suas ações")
public class ReuniaoController {

    private final ReuniaoService reuniaoService;
    private final ReuniaoMapper mapper;
    private final IngestaoService ingestaoService;
    private final RelatorioService relatorioService;
    private final FeriadoService feriadoService;

    @Operation(summary = "Lista reuniões ",
            description = "Devolve o listagem no formato dto das reunioes.")
    @ApiResponse(responseCode = "200", description = "Página de reuniões devolvida")
    @GetMapping()
    public ResponseEntity<List<ReuniaoDTO>> listar() {
        List<ReuniaoDTO> listagemDTO = reuniaoService.listar()
                .stream()
                .map(mapper::toDTO)
                .toList();

        return ResponseEntity.ok(listagemDTO);
    }

    @Operation(summary = "Busca uma reunião pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reunião encontrada"),
            @ApiResponse(responseCode = "404", description = "Nenhuma reunião com esse id"),
            @ApiResponse(responseCode = "400", description = "Id em formato inválido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReuniaoDTO> buscarPorId(@PathVariable Long id) {
        Reuniao reuniao = reuniaoService.buscarPorId(id);
        ReuniaoDTO dto = mapper.toDTO(reuniao);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Cria uma reunião",
            description = "Os ids de participantes e ações são resolvidos para as entidades correspondentes.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reunião criada"),
            @ApiResponse(responseCode = "400", description = "Campos obrigatórios ausentes ou inválidos"),
            @ApiResponse(responseCode = "404", description = "Participante ou ação informada não existe")
    })
    @PostMapping()
    public ResponseEntity<ReuniaoDTO> salvar(@Valid @RequestBody ReuniaoRequest request) {
        Reuniao reuniao = reuniaoService.salvar(request);
        ReuniaoDTO dto = mapper.toDTO(reuniao);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Operation(summary = "Substitui uma reunião por completo")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Reunião atualizada"),
            @ApiResponse(responseCode = "400", description = "Campos obrigatórios ausentes ou inválidos"),
            @ApiResponse(responseCode = "404", description = "Nenhuma reunião com esse id")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReuniaoDTO> atualizar(@PathVariable Long id,
                                                @Valid @RequestBody ReuniaoRequest request) {
        Reuniao reuniao = reuniaoService.atualizar(id, request);
        ReuniaoDTO dto = mapper.toDTO(reuniao);

        return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
    }

    /**
     * Sem @Valid de proposito: no PATCH, campo nulo significa "nao mexer".
     */
    @Operation(summary = "Atualiza parcialmente uma reunião",
            description = "Campo ausente ou nulo é ignorado — só o que vier no corpo é alterado.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Reunião atualizada"),
            @ApiResponse(responseCode = "404", description = "Nenhuma reunião com esse id")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ReuniaoDTO> atualizarParcial(@PathVariable Long id,
                                                       @RequestBody ReuniaoRequest request) {
        Reuniao reuniao = reuniaoService.atualizarParcial(id, request);
        ReuniaoDTO dto = mapper.toDTO(reuniao);

        return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Recebe a análise da IA (ingestão)",
            description = "Grava o resumo executivo e a transcrição pura na reunião. "
                    + "São os únicos textos que entram de fora — participantes, ações e "
                    + "responsáveis continuam sendo o que o administrador registrou.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Análise recebida e gravada"),
            @ApiResponse(responseCode = "400", description = "Resumo executivo ausente ou vazio"),
            @ApiResponse(responseCode = "404", description = "Nenhuma reunião com esse id")
    })
    @PatchMapping("/{id}/ingestao")
    public ResponseEntity<ReuniaoDTO> receberAnalise(@PathVariable Long id,
                                                     @Valid @RequestBody ReuniaoResumoRequest request) {

        Reuniao reuniao = ingestaoService.receberAnalise(id, request);

        return new ResponseEntity<>(mapper.toDTO(reuniao), HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Gera o relatório consolidado da reunião",
            description = "Monta o relatório com base no que está no banco: participantes, "
                    + "ações, responsáveis, áreas e pontos-chave vêm do registro operacional; "
                    + "da IA entra apenas o resumo executivo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado"),
            @ApiResponse(responseCode = "404", description = "Nenhuma reunião com esse id"),
            @ApiResponse(responseCode = "409", description = "A reunião ainda não recebeu o resumo da IA")
    })
    @GetMapping("/{id}/relatorio")
    public ResponseEntity<RelatorioReuniaoResponse> gerarRelatorio(@PathVariable Long id) {

        return ResponseEntity.ok(relatorioService.gerar(id));
    }

    @Operation(summary = "Verifica se a reunião caiu em feriado nacional",
            description = "Cruza a data da reunião com o calendário de feriados da BrasilAPI, "
                    + "consumida via Feign Client.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verificação concluída"),
            @ApiResponse(responseCode = "404", description = "Nenhuma reunião com esse id, ou reunião sem data válida"),
            @ApiResponse(responseCode = "503", description = "A API externa de feriados está indisponível")
    })
    @GetMapping("/{id}/feriado")
    public ResponseEntity<ReuniaoFeriadoDTO> verificarFeriado(@PathVariable Long id) {

        return ResponseEntity.ok(feriadoService.verificarReuniao(id));
    }

    @Operation(summary = "Exclui uma reunião")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reunião excluída"),
            @ApiResponse(responseCode = "404", description = "Nenhuma reunião com esse id")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        reuniaoService.deletar(id);

        return ResponseEntity.noContent().build();
    }

}