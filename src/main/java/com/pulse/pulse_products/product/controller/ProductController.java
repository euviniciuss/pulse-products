package com.pulse.pulse_products.product.controller;

import com.pulse.pulse_products.product.dto.ProductRequestDTO;
import com.pulse.pulse_products.product.dto.ProductResponseDTO;
import com.pulse.pulse_products.product.entity.Product;
import com.pulse.pulse_products.product.service.ProductService;
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
        Product product = toEntity(request);
        Product createdProduct = productService.create(product);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(createdProduct));
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
        Page<Product> products = productService.findAll(pageable);
        return ResponseEntity.ok(products.map(this::toResponse));
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
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable UUID id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(toResponse(product));
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
        Product productToUpdate = toEntity(request);
        Product updatedProduct = productService.update(id, productToUpdate);
        return ResponseEntity.ok(toResponse(updatedProduct));
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

    private Product toEntity(ProductRequestDTO request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setAmount(request.amount());
        return product;
    }

    private ProductResponseDTO toResponse(Product product) {
        return ProductResponseDTO.from(product);
    }
}
