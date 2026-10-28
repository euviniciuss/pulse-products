package com.pulse.pulse_products;

import com.pulse.pulse_products.product.entity.Product;
import com.pulse.pulse_products.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PulseProductsApplicationTests {

	@Autowired
	private ProductRepository productRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldSaveProductWithAutomaticTimestamps() {
		Product product = new Product();
		product.setName("Produto Teste");
		product.setDescription("Descrição do produto teste");
		product.setPrice(new BigDecimal("99.90"));
		product.setAmount(10);

		Product saved = productRepository.save(product);

		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getCreatedAt()).isNotNull();
		assertThat(saved.getUpdatedAt()).isNotNull();
	}
}
