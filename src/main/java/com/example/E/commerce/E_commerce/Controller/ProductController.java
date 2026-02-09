package com.example.E.commerce.E_commerce.Controller;

import com.example.E.commerce.E_commerce.DTO.ProductRequestDTO;
import com.example.E.commerce.E_commerce.Entity.Product;
import com.example.E.commerce.E_commerce.Service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api")
@RestController
public class ProductController
{
    private final ProductService productService;

    public ProductController(ProductService productService)
    {
        this.productService = productService;
    }

    @GetMapping("/products")
    private List<Product> getAllProducts()
    {
        return productService.getAllProducts();
    }

    @PostMapping("/add")
    private Product addProduct(@RequestBody ProductRequestDTO productRequestDTO)
    {
        return productService.addProduct(productRequestDTO);
    }

@GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

}
