package com.example.E.commerce.E_commerce.Controller;

import com.example.E.commerce.E_commerce.DTO.ProductRequestDTO;
import com.example.E.commerce.E_commerce.Entity.Product;
import com.example.E.commerce.E_commerce.Service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

@GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PreAuthorize("Admin")
    @PostMapping("/add")
    public Product addProduct(@RequestBody ProductRequestDTO productRequestDTO)
    {
        return productService.addProduct(productRequestDTO);
    }

    @PreAuthorize("Admin")
    @DeleteMapping("products/{id}")
    public ResponseEntity<String> DeleteProduct(@PathVariable Long id)
    {
        return ResponseEntity.ok(productService.deleteProductById(id));
    }


    @PreAuthorize("Admin")
    @PutMapping("products/{id}")
    public ResponseEntity<?> UpdateProductById(@PathVariable Long id, @RequestBody ProductRequestDTO productRequestDTO)
    {
        try{
            Product updateProduct = productService.updateProductById(id,productRequestDTO);
            return ResponseEntity.ok(updateProduct);
        }
        catch (RuntimeException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

    }

}
