package com.backend.java.productapi.service;

import com.backend.java.productapi.dto.DTOConverter;
import com.backend.java.productapi.dto.ProductDTO;
import com.backend.java.productapi.model.Product;
import com.backend.java.productapi.repository.CategoryRepository;
import com.backend.java.productapi.repository.ProductRepository;
import com.santana.java.back.end.exceptions.CategoryNotFoundException;
import com.santana.java.back.end.exceptions.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductDTO> getAll() {
        List<Product> products = productRepository.findAll();
        return products
                .stream()
                .map(ProductDTO::convert)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductByCategoryId(Long categoryId) {
        List<Product> products = productRepository.getProductByCategory(categoryId);
        return products
                .stream()
                .map(ProductDTO::convert)
                .collect(Collectors.toList());
    }

    public ProductDTO findByProductIdentifier(String productIdentifier) {
        Product product = productRepository.findByProductIdentifier(productIdentifier);
        if (product != null) {
            return ProductDTO.convert(product);
        }
        throw new ProductNotFoundException();
    }

    public ProductDTO save(ProductDTO productDTO) {
        Boolean existsCategory = categoryRepository.existsById(productDTO.getCategory().getId());
        if (!existsCategory) {
            throw new CategoryNotFoundException();
        }
        Product product = productRepository
                .save(Product.convert(productDTO));
        return DTOConverter.convert(product);

    }

    public void delete(long productId) {
        Optional<Product> product =
                productRepository.findById(productId);
        if (product.isPresent()) {
            productRepository.delete(product.get());
        }

        throw new ProductNotFoundException();
    }

    public ProductDTO editProduct(long id, ProductDTO dto) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Product	not	found"));
        if (dto.getNome() != null || !dto.getNome().isEmpty()) {
            product.setNome(dto.getNome());
        }
        if (dto.getPreco() != null) {
            product.setPreco(dto.getPreco());
        }
        return ProductDTO.convert(productRepository.save(product));
    }

    public Page<ProductDTO> getAllPage(Pageable page) {
        Page<Product> users = productRepository.findAll(page);
        return users
                .map(ProductDTO::convert);
    }

}

