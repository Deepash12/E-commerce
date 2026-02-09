package com.example.E.commerce.E_commerce.Service;

import com.example.E.commerce.E_commerce.DTO.ProductRequestDTO;
import com.example.E.commerce.E_commerce.Entity.Category;
import com.example.E.commerce.E_commerce.Entity.Product;
import com.example.E.commerce.E_commerce.Repository.CategoryRepository;
import com.example.E.commerce.E_commerce.Repository.ProductRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService
{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> getAllProducts()
    {
        return productRepository.findAll();
//                .findByActiveTrue();
    }


public Product addProduct(ProductRequestDTO productRequestDTO) {

    Category category = categoryRepository.findById(
            productRequestDTO.getCategoryId()
    ).orElseThrow(() -> new RuntimeException("Category not found"));

    Product product = new Product();
    product.setName(productRequestDTO.getName());
    product.setDescription(productRequestDTO.getDescription());
    product.setPrice(productRequestDTO.getPrice());
    product.setStockQuantity(productRequestDTO.getStockQuantity());
    product.setCategory(category);

    return productRepository.save(product);
}


    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
