package com.example.E.commerce.E_commerce.Service.Product;

import com.example.E.commerce.E_commerce.DTO.Product.ProductPageResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Authorization.Users;
import com.example.E.commerce.E_commerce.Entity.Product.Category;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import com.example.E.commerce.E_commerce.Entity.Product.SubCategory;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Product.CategoryRepository;
import com.example.E.commerce.E_commerce.Repository.Product.ProductRepository;
import com.example.E.commerce.E_commerce.Repository.Product.SubCategoryRepository;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import com.example.E.commerce.E_commerce.Repository.Wishlist.WishlistRepository;
import com.example.E.commerce.E_commerce.Service.File.FileService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.E.commerce.E_commerce.Entity.Cart.Cart;
import com.example.E.commerce.E_commerce.Repository.Cart.CartRepository;
import com.example.E.commerce.E_commerce.Repository.Cart.CartItemsRepository;
import java.math.RoundingMode;

@RequiredArgsConstructor
@Service
public class ProductService
{
    private final ProductRepository productRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final FileService fileService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;

    public ProductPageResponseDTO<ProductResponseDTO> getAllProducts(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            Integer subCategoryId,
            Integer categoryId,
            Double minPrice,
            Double maxPrice,
            String keyword
    ) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Boolean flag = true;

        Page<Product> productPage =
                productRepository.findWithFilter(
                        subCategoryId,categoryId, minPrice, maxPrice, keyword, flag, pageable
                );

        String username = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            username = auth.getName();
        }

        Set<Object> wishlistProductIds;

        if (username != null) {

            Users user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BadRequestException("User Not Found!!!"));

            wishlistProductIds = wishlistRepository.findByUser(user)
                    .stream()
                    .map(w->w.getProduct().getId())
                    .collect(Collectors.toSet());

        } else {
            wishlistProductIds = new HashSet<>();
        }

        List<ProductResponseDTO> dtoList =
                productPage.getContent()
                        .stream()
                        .map(product -> {
                            ProductResponseDTO dto = convertToDTO(product);
                             dto.setIsWishlist(wishlistProductIds.contains(product.getId()));
                            return dto;
                        })
                        .toList();

        ProductPageResponseDTO<ProductResponseDTO> response =
                new ProductPageResponseDTO<>();

        response.setContent(dtoList);
        response.setCurrentPage(productPage.getNumber());
        response.setPageSize(productPage.getSize());
        response.setTotalPages(productPage.getTotalPages());
        response.setTotalElements(productPage.getTotalElements());
        response.setLast(productPage.isLast());

        return response;
    }


    private ProductResponseDTO convertToDTO(Product product)
    {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryName(product.getCategory())
                .discountPrice(product.getDiscountPrice())
                .finalPrice(product.getPrice().subtract(product.getDiscountPrice()))
                .stockQuantity(product.getStockQuantity())
                .subCategoryName(product.getSubCategory())
                .productImageUrl(product.getProductImageUrl())
                .isActive(product.isActive())
                .build();
    }

    private void removeProductFromAffectedCarts(Long productId)
    {
        List<Long> cartIds =
                cartItemsRepository.findAffectedCartIds(productId);

        for (Long cartId : cartIds)
        {
            Cart cart = cartRepository.findByIdForUpdate(cartId)
                    .orElse(null);

            if (cart == null)
            {
                continue;
            }

            boolean removed = cart.getItems().removeIf(
                    item -> item.getProduct().getId().equals(productId)
            );

            if (!removed)
            {
                continue;
            }

            BigDecimal totalAmount = cart.getItems().stream()
                    .filter(item -> item.getProduct().isActive())
                    .filter(item -> item.getQuantity() > 0)
                    .filter(item ->
                            item.getProduct().getStockQuantity()
                                    >= item.getQuantity()
                    )
                    .map(item -> item.getProduct().getFinalPrice()
                            .multiply(
                                    BigDecimal.valueOf(item.getQuantity())
                            ))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            cart.setCoupon(null);
            cart.setDiscountAmount(BigDecimal.ZERO);
            cart.setTotalAmount(totalAmount);
            cart.setFinalAmount(totalAmount);

            cartRepository.save(cart);
        }
    }

    public Product addProduct(@Valid ProductRequestDTO productRequestDTO,
                              MultipartFile image) throws IOException
    {
        SubCategory subCategory = subCategoryRepository.findById(
                productRequestDTO.getSubcategoryId()
        ).orElseThrow(() ->
                new BadRequestException("SubCategory not found"));

        Category category = categoryRepository.findById(
                productRequestDTO.getCategoryId()
        ).orElseThrow(() ->
                new BadRequestException("Category not found"));

        boolean existingProduct = productRepository.existsByNameIgnoreCase(
                productRequestDTO.getName().trim()
        );

        if (existingProduct)
        {
            throw new BadRequestException(
                    "Product with this name already exists"
            );
        }

        String productImageUrl = fileService.uploadFile(image);

        BigDecimal discountPrice = productRequestDTO.getDiscountPrice();

        if (discountPrice == null)
        {
            discountPrice = BigDecimal.ZERO;
        }

        if (discountPrice.compareTo(productRequestDTO.getPrice()) > 0)
        {
            throw new BadRequestException(
                    "Discount price cannot be greater than actual price"
            );
        }

        BigDecimal finalPrice = productRequestDTO.getPrice()
                .subtract(discountPrice);

        Product product = new Product();
        product.setName(productRequestDTO.getName().trim());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setDiscountPrice(discountPrice);
        product.setFinalPrice(finalPrice);

        if(productRequestDTO.getStockQuantity()<0)
        {
            throw new BadRequestException("Stock quanty should be either 0 or more than 0 , not be negative");
        }
        else
        {
            product.setStockQuantity(productRequestDTO.getStockQuantity());
        }
        product.setCategory(category);
        product.setSubCategory(subCategory);
        product.setProductImageUrl(productImageUrl);

        return productRepository.save(product);
    }

    public ProductResponseDTO getProductById(Long id) {
        if(id==null||id<=0)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Product id must be greater than 0");
        }
        Product product =  productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product not found"));
        return convertToDTO(product);
    }

    @Transactional
    public String deleteProductById(Long id, Boolean flag)
    {
        if (flag == null)
        {
            throw new BadRequestException(
                    "Product active status is required"
            );
        }

        Product product = productRepository.findByIdForUpdate(id)
                .orElseThrow(() ->
                        new BadRequestException("Product Not Exist!!!")
                );

        product.setActive(flag);

        if (!flag)
        {
            removeProductFromAffectedCarts(id);
        }

        productRepository.save(product);

        return flag
                ? "Product Enabled Successfully"
                : "Product Disabled Successfully";
    }


    public Product updateProductById(Long id,
                                     ProductRequestDTO productRequestDTO,
                                     MultipartFile image)
            throws IOException
    {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() ->
                        new BadRequestException("Product does not exist!!!"));

        Category category = categoryRepository.findById(
                productRequestDTO.getCategoryId()
        ).orElseThrow(() ->
                new BadRequestException("Category Not Exist!!!"));

        SubCategory subCategory = subCategoryRepository.findById(
                productRequestDTO.getSubcategoryId()
        ).orElseThrow(() ->
                new BadRequestException("SubCategory not found"));

        boolean productExists =
                productRepository.existsByNameIgnoreCaseAndIdNot(
                        productRequestDTO.getName().trim(),
                        id
                );

        if (productExists)
        {
            throw new BadRequestException(
                    "Product with this name already exists"
            );
        }

        if (Boolean.TRUE.equals(productRequestDTO.getRemoveImage())) {
            existingProduct.setProductImageUrl(null);
        } else if (image != null && !image.isEmpty()) {
            String productImageUrl = fileService.uploadFile(image);
            existingProduct.setProductImageUrl(productImageUrl);
        }

        if (productRequestDTO.getStockQuantity() < 0)
        {
            throw new BadRequestException(
                    "Stock quantity cannot be negative"
            );
        }
        BigDecimal discountPrice = productRequestDTO.getDiscountPrice();

        if (discountPrice == null)
        {
            discountPrice = BigDecimal.ZERO;
        }

        if (discountPrice.compareTo(productRequestDTO.getPrice()) > 0)
        {
            throw new BadRequestException(
                    "Discount price cannot be greater than actual price"
            );
        }
        existingProduct.setName(productRequestDTO.getName().trim());
        existingProduct.setDescription(productRequestDTO.getDescription());
        existingProduct.setPrice(productRequestDTO.getPrice());
        existingProduct.setStockQuantity(productRequestDTO.getStockQuantity());
        existingProduct.setDiscountPrice(discountPrice);

        existingProduct.setFinalPrice(
                productRequestDTO.getPrice().subtract(discountPrice)
        );

        existingProduct.setCategory(category);
        existingProduct.setSubCategory(subCategory);

        return productRepository.save(existingProduct);
    }


    public ProductPageResponseDTO<ProductResponseDTO> viewAllProduct(
            Integer pageNumber, Integer pageSize, String sortBy, String sortDir,
            Integer subCategoryId,Integer categoryId, Double minPrice, Double maxPrice, String keyword)
    {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        if(keyword==null)
        {
            keyword="";
        }

        Page<Product> productPage =
                productRepository.findWithFilters(
                        subCategoryId,categoryId, minPrice, maxPrice, keyword, pageable
                );

        List<ProductResponseDTO> dtoList =
                productPage.getContent()
                        .stream()
                        .map(this::convertToDTO)
                        .toList();

        ProductPageResponseDTO<ProductResponseDTO> response = new ProductPageResponseDTO<>();
        response.setContent(dtoList);
        response.setCurrentPage(productPage.getNumber());
        response.setPageSize(productPage.getSize());
        response.setTotalPages(productPage.getTotalPages());
        response.setTotalElements(productPage.getTotalElements());
        response.setLast(productPage.isLast());

        return response;
    }
}
