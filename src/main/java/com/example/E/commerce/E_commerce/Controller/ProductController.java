package com.example.E.commerce.E_commerce.Controller;
import com.example.E.commerce.E_commerce.DTO.Product.ProductPageResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import com.example.E.commerce.E_commerce.Service.Product.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping

    public ProductPageResponseDTO<ProductResponseDTO> getAllProducts
    (
            @RequestParam(required = false) int categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5")int pageSize,
            @RequestParam (defaultValue = "id") String sortBy,
            @RequestParam (defaultValue = "asc") String sortDir
    )
    {
        try {
            return productService.getAllProducts(pageNumber,pageSize,sortBy,sortDir,categoryId,minPrice,maxPrice,keyword);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getProductById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Product addProduct(@RequestBody ProductRequestDTO dto) {
        return productService.addProduct(dto);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequestDTO dto)
    {
        try {
            return productService.updateProductById(id, dto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id)
    {
        try
        {
            return productService.deleteProductById(id);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
