package com.pulse.pulse_products.service.impl;
import com.pulse.pulse_products.exceptions.NotFoundException;
import com.pulse.pulse_products.entity.Product;
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

    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Product findById(UUID id) {
        return findProductOrThrow(id);
    }

    @Override
    @Transactional
    public Product update(UUID id, Product productToUpdate) {
        Product product = findProductOrThrow(id);

        product.setName(productToUpdate.getName());
        product.setDescription(productToUpdate.getDescription());
        product.setPrice(productToUpdate.getPrice());
        product.setAmount(productToUpdate.getAmount());

        return product;
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
