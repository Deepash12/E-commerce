package com.example.E.commerce.E_commerce.Controller.Product;
import com.example.E.commerce.E_commerce.DTO.ApiResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductPageResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import com.example.E.commerce.E_commerce.Service.Product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    
    @GetMapping("/all")

    public ProductPageResponseDTO<ProductResponseDTO> getAllProducts
    (
            @RequestParam(required = false) Integer subCategoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "5")Integer pageSize,
            @RequestParam (defaultValue = "id") String sortBy,
            @RequestParam (defaultValue = "asc") String sortDir

    )
    {
        try {
            return productService.getAllProducts(pageNumber,pageSize,sortBy,sortDir,subCategoryId,minPrice,maxPrice,keyword);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/view")
    public ApiResponseDTO<ProductPageResponseDTO<ProductResponseDTO>> viewAllProducts
            (
                    @RequestParam(required = false) Integer subCategoryId,
                    @RequestParam(required = false) Double minPrice,
                    @RequestParam(required = false) Double maxPrice,
                    @RequestParam(required = false) String keyword,
                    @RequestParam(defaultValue = "0") Integer pageNumber,
                    @RequestParam(defaultValue = "10")Integer pageSize,
                    @RequestParam (defaultValue = "id") String sortBy,
                    @RequestParam (defaultValue = "asc") String sortDir
            )
    {
        try {

            ProductPageResponseDTO<ProductResponseDTO> responseDTO = productService.viewAllProduct(pageNumber,pageSize,sortBy,sortDir,subCategoryId,minPrice,maxPrice,keyword);
            return new ApiResponseDTO<>(201,"fetch All Products", LocalDateTime.now(),responseDTO);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }





    @GetMapping("/view/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getProductById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/add",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product addProduct
            (@RequestPart("dto") String dto,
             @RequestPart(value = "image",required = false) MultipartFile image) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        ProductRequestDTO productDTO = mapper.readValue(dto, ProductRequestDTO.class);
        return productService.addProduct(productDTO,image);
    }



    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product updateProduct(
            @PathVariable Long id,
            @RequestPart("dto") String dto,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        ProductRequestDTO productDTO = mapper.readValue(dto, ProductRequestDTO.class);

        return productService.updateProductById(id, productDTO, image);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/enable-disable/{id}")
    public String deleteProduct(@PathVariable Long id,@RequestParam Boolean flag)
    {
        try
        {
            return productService.deleteProductById(id,flag);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
