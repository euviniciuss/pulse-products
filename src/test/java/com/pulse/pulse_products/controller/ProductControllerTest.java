package com.pulse.pulse_products.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.pulse_products.dto.request.ProductRequestDTO;
import com.pulse.pulse_products.dto.response.ProductResponseDTO;
import com.pulse.pulse_products.exceptions.AlreadyExistsException;
import com.pulse.pulse_products.exceptions.GlobalExceptionHandler;
import com.pulse.pulse_products.exceptions.NotFoundException;
import com.pulse.pulse_products.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private static final String BASE_URL = "/api/products";

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    private ProductRequestDTO newProductRequest(String name) {
        return new ProductRequestDTO(
                name,
                "Produto de teste",
                new BigDecimal("10.50"),
                5
        );
    }

    private ProductResponseDTO newProductResponse(UUID id, String name) {
        return new ProductResponseDTO(
                id,
                name,
                "Produto de teste",
                new BigDecimal("10.50"),
                5,
                Instant.parse("2026-01-01T10:00:00Z"),
                Instant.parse("2026-01-01T10:00:00Z")
        );
    }

    @Test
    @DisplayName("Deve criar um produto com sucesso retornando status 201 Created")
    void shouldCreateProductSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();
        ProductRequestDTO request = newProductRequest("Mouse");
        ProductResponseDTO expectedResponse = newProductResponse(id, "Mouse");

        when(productService.create(request)).thenReturn(expectedResponse);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Mouse"))
                .andExpect(jsonPath("$.description").value("Produto de teste"))
                .andExpect(jsonPath("$.price").value(10.50))
                .andExpect(jsonPath("$.amount").value(5));

        verify(productService).create(request);
    }

    @Test
    @DisplayName("Deve retornar status 409 Conflict quando tentar criar produto com nome já existente")
    void shouldReturnConflictWhenProductAlreadyExistsOnCreate() throws Exception {
        ProductRequestDTO request = newProductRequest("Mouse");

        when(productService.create(request))
                .thenThrow(new AlreadyExistsException("Já existe um produto cadastrado com esse nome."));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito"))
                .andExpect(jsonPath("$.detail").value("Já existe um produto cadastrado com esse nome."));

        verify(productService).create(request);
    }

    @Test
    @DisplayName("Deve retornar status 400 Bad Request ao tentar criar produto com dados inválidos")
    void shouldReturnBadRequestWhenCreatingProductWithInvalidData() throws Exception {
        ProductRequestDTO invalidRequest = new ProductRequestDTO(
                "",
                "Descrição",
                new BigDecimal("-5.00"),
                -1
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).create(any());
    }

    @Test
    @DisplayName("Deve listar produtos paginados com status 200 OK")
    void shouldFindAllProductsPagedSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();
        ProductResponseDTO responseDTO = newProductResponse(id, "Mouse");
        Page<ProductResponseDTO> page = new PageImpl<>(List.of(responseDTO), PageRequest.of(0, 10), 1);

        when(productService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.content[0].name").value("Mouse"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(productService).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não existirem produtos cadastrados")
    void shouldReturnEmptyPageWhenNoProductsFound() throws Exception {
        Page<ProductResponseDTO> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        when(productService.findAll(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(productService).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso e retornar status 200 OK")
    void shouldFindProductByIdSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();
        ProductResponseDTO expectedResponse = newProductResponse(id, "Mouse");

        when(productService.findById(id)).thenReturn(expectedResponse);

        mockMvc.perform(get(BASE_URL + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Mouse"))
                .andExpect(jsonPath("$.description").value("Produto de teste"))
                .andExpect(jsonPath("$.price").value(10.50))
                .andExpect(jsonPath("$.amount").value(5));

        verify(productService).findById(id);
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found quando produto não for encontrado por ID")
    void shouldReturnNotFoundWhenProductDoesNotExistOnFindById() throws Exception {
        UUID id = UUID.randomUUID();

        when(productService.findById(id))
                .thenThrow(new NotFoundException("Produto não encontrado!"));

        mockMvc.perform(get(BASE_URL + "/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.detail").value("Produto não encontrado!"));

        verify(productService).findById(id);
    }

    @Test
    @DisplayName("Deve retornar status 400 Bad Request ao buscar produto com ID inválido")
    void shouldReturnBadRequestWhenIdIsInvalidUUIDOnFindById() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", "id-invalido"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).findById(any());
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso e retornar status 200 OK")
    void shouldUpdateProductSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();
        ProductRequestDTO request = new ProductRequestDTO(
                "Teclado",
                "Teclado Mecânico",
                new BigDecimal("250.00"),
                10
        );
        ProductResponseDTO expectedResponse = new ProductResponseDTO(
                id,
                request.name(),
                request.description(),
                request.price(),
                request.amount(),
                Instant.parse("2026-01-01T10:00:00Z"),
                Instant.parse("2026-01-01T10:00:00Z")
        );

        when(productService.update(eq(id), eq(request))).thenReturn(expectedResponse);

        mockMvc.perform(put(BASE_URL + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Teclado"))
                .andExpect(jsonPath("$.description").value("Teclado Mecânico"))
                .andExpect(jsonPath("$.price").value(250.00))
                .andExpect(jsonPath("$.amount").value(10));

        verify(productService).update(eq(id), eq(request));
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao tentar atualizar produto inexistente")
    void shouldReturnNotFoundWhenProductDoesNotExistOnUpdate() throws Exception {
        UUID id = UUID.randomUUID();
        ProductRequestDTO request = newProductRequest("Mouse Atualizado");

        when(productService.update(eq(id), eq(request)))
                .thenThrow(new NotFoundException("Produto não encontrado!"));

        mockMvc.perform(put(BASE_URL + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.detail").value("Produto não encontrado!"));

        verify(productService).update(eq(id), eq(request));
    }

    @Test
    @DisplayName("Deve retornar status 409 Conflict ao tentar atualizar produto com nome que já pertence a outro produto")
    void shouldReturnConflictWhenProductNameAlreadyExistsOnUpdate() throws Exception {
        UUID id = UUID.randomUUID();
        ProductRequestDTO request = newProductRequest("Teclado");

        when(productService.update(eq(id), eq(request)))
                .thenThrow(new AlreadyExistsException("Já existe outro produto cadastrado com esse nome."));

        mockMvc.perform(put(BASE_URL + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito"))
                .andExpect(jsonPath("$.detail").value("Já existe outro produto cadastrado com esse nome."));

        verify(productService).update(eq(id), eq(request));
    }

    @Test
    @DisplayName("Deve retornar status 400 Bad Request ao tentar atualizar produto com dados inválidos")
    void shouldReturnBadRequestWhenUpdatingProductWithInvalidData() throws Exception {
        UUID id = UUID.randomUUID();
        ProductRequestDTO invalidRequest = new ProductRequestDTO(
                "",
                "Descrição",
                new BigDecimal("-10.00"),
                -2
        );

        mockMvc.perform(put(BASE_URL + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Deve retornar status 400 Bad Request ao tentar atualizar produto com ID inválido")
    void shouldReturnBadRequestWhenIdIsInvalidUUIDOnUpdate() throws Exception {
        ProductRequestDTO request = newProductRequest("Mouse");

        mockMvc.perform(put(BASE_URL + "/{id}", "id-invalido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Deve excluir produto com sucesso e retornar status 204 No Content")
    void shouldDeleteProductSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(productService).delete(id);

        mockMvc.perform(delete(BASE_URL + "/{id}", id))
                .andExpect(status().isNoContent());

        verify(productService).delete(id);
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao tentar excluir produto inexistente")
    void shouldReturnNotFoundWhenProductDoesNotExistOnDelete() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new NotFoundException("Produto não encontrado!"))
                .when(productService).delete(id);

        mockMvc.perform(delete(BASE_URL + "/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.detail").value("Produto não encontrado!"));

        verify(productService).delete(id);
    }

    @Test
    @DisplayName("Deve retornar status 400 Bad Request ao tentar excluir produto com ID inválido")
    void shouldReturnBadRequestWhenIdIsInvalidUUIDOnDelete() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", "id-invalido"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).delete(any());
    }
}
