package com.pulse.pulse_products.controller;

import com.pulse.pulse_products.dto.request.ProductRequestDTO;
import com.pulse.pulse_products.dto.response.ProductResponseDTO;
import com.pulse.pulse_products.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Produtos", description = "Endpoints para gerenciamento do catálogo de produtos.")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(
            summary = "Criar produto",
            description = "Cadastra um novo produto no catálogo."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Produto criado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida ou dados que não passaram na validação.",
                    content = @Content
            )
    })

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(
            @Valid @RequestBody ProductRequestDTO request
    ) {
        ProductResponseDTO response = productService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Listar produtos",
            description = """
            Retorna os produtos cadastrados em páginas.
            A paginação começa em zero. É possível definir o tamanho
            da página e ordenar os resultados por um ou mais campos.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produtos retornados com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parâmetros de paginação ou ordenação inválidos.",
                    content = @Content
            )
    })

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> findAll(
            @ParameterObject
            @PageableDefault(page = 0, size = 10)
            Pageable pageable
    ) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }


    @Operation(
            summary = "Buscar produto por ID",
            description = "Retorna os detalhes de um produto identificado pelo Id."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto encontrado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "O Id informado não é um UUID válido.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado.",
                    content = @Content
            )
    })

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @Operation(
            summary = "Atualizar produto",
            description = "Atualiza os dados de um produto existente pelo Id."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto atualizado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Id inválido ou dados que não passaram na validação.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado.",
                    content = @Content
            )
    })

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequestDTO request
    ) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @Operation(
            summary = "Excluir produto",
            description = "Exclui um produto do catálogo pelo Id."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Produto excluído com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "O ID informado não é um Id válido.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado.",
                    content = @Content
            )
    })

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
