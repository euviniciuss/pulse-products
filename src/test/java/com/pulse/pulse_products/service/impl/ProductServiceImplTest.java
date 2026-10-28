package com.pulse.pulse_products.service.impl;

import com.pulse.pulse_products.dto.request.ProductRequestDTO;
import com.pulse.pulse_products.dto.response.ProductResponseDTO;
import com.pulse.pulse_products.entity.Product;
import com.pulse.pulse_products.exceptions.AlreadyExistsException;
import com.pulse.pulse_products.exceptions.NotFoundException;
import com.pulse.pulse_products.mapper.ProductMapper;
import com.pulse.pulse_products.repository.ProductRepository;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequestDTO newProductRequest(String name) {
        return new ProductRequestDTO(
                name,
                "Produto de teste",
                new BigDecimal("10.50"),
                5
        );
    }

    private Product newProduct(String name) {
        return newProduct(UUID.randomUUID(), name);
    }

    private Product newProduct(UUID id, String name) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription("Produto de teste");
        product.setPrice(new BigDecimal("10.50"));
        product.setAmount(5);
        return product;
    }

    private ProductResponseDTO newProductResponse(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getAmount(),
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    @DisplayName("Deve criar um produto com sucesso")
    void shouldCreateProduct() {
        ProductRequestDTO request = newProductRequest("Mouse");
        Product product = newProduct("Mouse");
        ProductResponseDTO expectedResponse = newProductResponse(product);

        when(productRepository.existsByName("Mouse")).thenReturn(false);
        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDTO(product)).thenReturn(expectedResponse);

        ProductResponseDTO response = productService.create(request);

        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResponse);

        verify(productRepository).existsByName("Mouse");
        verify(productMapper).toEntity(request);
        verify(productRepository).save(product);
        verify(productMapper).toResponseDTO(product);
    }

    @Test
    @DisplayName("Deve lançar AlreadyExistsException ao tentar criar produto com nome já existente")
    void shouldThrowExceptionWhenProductAlreadyExists() {
        ProductRequestDTO request = newProductRequest("Mouse");

        when(productRepository.existsByName("Mouse")).thenReturn(true);

        AlreadyExistsException exception = assertThrows(
                AlreadyExistsException.class,
                () -> productService.create(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Já existe um produto cadastrado com esse nome.");
        verify(productRepository).existsByName("Mouse");
        verify(productMapper, never()).toEntity(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar página de produtos com sucesso")
    void shouldFindAllProductsPaged() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = newProduct("Mouse");
        ProductResponseDTO responseDTO = newProductResponse(product);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(productMapper.toResponseDTO(product)).thenReturn(responseDTO);

        Page<ProductResponseDTO> response = productService.findAll(pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0)).isEqualTo(responseDTO);

        verify(productRepository).findAll(pageable);
        verify(productMapper).toResponseDTO(product);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver produtos cadastrados")
    void shouldReturnEmptyPageWhenNoProductsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(productRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<ProductResponseDTO> response = productService.findAll(pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEmpty();

        verify(productRepository).findAll(pageable);
        verify(productMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void shouldFindProductByIdWhenExists() {
        UUID id = UUID.randomUUID();
        Product product = newProduct(id, "Mouse");
        ProductResponseDTO expectedResponse = newProductResponse(product);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDTO(product)).thenReturn(expectedResponse);

        ProductResponseDTO response = productService.findById(id);

        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResponse);

        verify(productRepository).findById(id);
        verify(productMapper).toResponseDTO(product);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao buscar produto por ID inexistente")
    void shouldThrowNotFoundExceptionWhenProductDoesNotExistOnFindById() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productService.findById(id)
        );

        assertThat(exception.getMessage()).isEqualTo("Produto não encontrado!");
        verify(productRepository).findById(id);
        verify(productMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void shouldUpdateProductSuccessfully() {
        UUID id = UUID.randomUUID();
        Product existingProduct = newProduct(id, "Mouse Antigo");
        ProductRequestDTO updateRequest = new ProductRequestDTO(
                "Mouse Novo",
                "Nova descrição",
                new BigDecimal("29.90"),
                15
        );
        ProductResponseDTO expectedResponse = new ProductResponseDTO(
                id,
                updateRequest.name(),
                updateRequest.description(),
                updateRequest.price(),
                updateRequest.amount(),
                Instant.now(),
                Instant.now()
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByNameAndIdNot("Mouse Novo", id)).thenReturn(false);
        when(productMapper.toResponseDTO(existingProduct)).thenReturn(expectedResponse);

        ProductResponseDTO response = productService.update(id, updateRequest);

        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResponse);
        assertThat(existingProduct.getName()).isEqualTo("Mouse Novo");
        assertThat(existingProduct.getDescription()).isEqualTo("Nova descrição");
        assertThat(existingProduct.getPrice()).isEqualTo(new BigDecimal("29.90"));
        assertThat(existingProduct.getAmount()).isEqualTo(15);

        verify(productRepository).findById(id);
        verify(productRepository).existsByNameAndIdNot("Mouse Novo", id);
        verify(productMapper).toResponseDTO(existingProduct);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar atualizar produto inexistente")
    void shouldThrowNotFoundExceptionWhenProductDoesNotExistOnUpdate() {
        UUID id = UUID.randomUUID();
        ProductRequestDTO updateRequest = newProductRequest("Mouse Atualizado");

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productService.update(id, updateRequest)
        );

        assertThat(exception.getMessage()).isEqualTo("Produto não encontrado!");
        verify(productRepository).findById(id);
        verify(productRepository, never()).existsByNameAndIdNot(anyString(), any(UUID.class));
        verify(productMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Deve lançar AlreadyExistsException ao tentar atualizar produto com nome já existente em outro produto")
    void shouldThrowAlreadyExistsExceptionWhenUpdatingProductWithExistingName() {
        UUID id = UUID.randomUUID();
        Product existingProduct = newProduct(id, "Mouse");
        ProductRequestDTO updateRequest = newProductRequest("Teclado");

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByNameAndIdNot("Teclado", id)).thenReturn(true);

        AlreadyExistsException exception = assertThrows(
                AlreadyExistsException.class,
                () -> productService.update(id, updateRequest)
        );

        assertThat(exception.getMessage()).isEqualTo("Já existe outro produto cadastrado com esse nome.");
        verify(productRepository).findById(id);
        verify(productRepository).existsByNameAndIdNot("Teclado", id);
        verify(productMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void shouldDeleteProductSuccessfully() {
        UUID id = UUID.randomUUID();
        Product existingProduct = newProduct(id, "Mouse");

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));

        assertDoesNotThrow(() -> productService.delete(id));

        verify(productRepository).findById(id);
        verify(productRepository).delete(existingProduct);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar deletar produto inexistente")
    void shouldThrowNotFoundExceptionWhenProductDoesNotExistOnDelete() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productService.delete(id)
        );

        assertThat(exception.getMessage()).isEqualTo("Produto não encontrado!");
        verify(productRepository).findById(id);
        verify(productRepository, never()).delete(any());
    }
}
