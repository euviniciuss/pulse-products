package com.pulse.pulse_products.service.impl;
import com.pulse.pulse_products.dto.request.ProductRequestDTO;
import com.pulse.pulse_products.dto.response.ProductResponseDTO;
import com.pulse.pulse_products.exceptions.AlreadyExistsException;
import com.pulse.pulse_products.exceptions.NotFoundException;
import com.pulse.pulse_products.entity.Product;
import com.pulse.pulse_products.mapper.ProductMapper;
import com.pulse.pulse_products.repository.ProductRepository;


import com.pulse.pulse_products.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDTO create(ProductRequestDTO request) {
        if (productRepository.existsByName(request.name())) {
            throw new AlreadyExistsException(
                    "Já existe um produto cadastrado com esse nome."
            );
        }

        Product product = productMapper.toEntity(request);

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponseDTO(savedProduct);
    }

    @Override
    public Page<ProductResponseDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponseDTO);
    }

    @Override
    public ProductResponseDTO findById(UUID id) {
        Product product = findProductOrThrow(id);

        return productMapper.toResponseDTO(product);
    }

    @Override
    @Transactional
    public ProductResponseDTO update(
            UUID id,
            ProductRequestDTO request
    ) {
        Product product = findProductOrThrow(id);

        if (productRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new AlreadyExistsException(
                    "Já existe outro produto cadastrado com esse nome."
            );
        }

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setAmount(request.amount());

        return productMapper.toResponseDTO(product);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Product product = findProductOrThrow(id);

        productRepository.delete(product);
    }


    private Product findProductOrThrow(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Produto não encontrado!"
                ));
    }

}
