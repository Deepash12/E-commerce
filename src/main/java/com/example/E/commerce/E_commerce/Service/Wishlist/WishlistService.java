package com.example.E.commerce.E_commerce.Service.Wishlist;

import com.example.E.commerce.E_commerce.DTO.Product.ProductResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Wishlist.WishlistResponse;
import com.example.E.commerce.E_commerce.Entity.Authorization.Users;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import com.example.E.commerce.E_commerce.Entity.Wishlist.Wishlist;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Product.ProductRepository;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import com.example.E.commerce.E_commerce.Repository.Wishlist.WishlistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishlistService
{
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;

    private ProductResponseDTO mapToDTO(Product product)
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

    public Page<ProductResponseDTO> getAllWishlistProduct(Integer pageNumber, Integer pageSize)
    {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("addedAt").descending());

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));

        Page<Wishlist> wishlistPage = wishlistRepository.findByUser(user, pageable);

        return wishlistPage.map(wishlist -> {
            Product product = wishlist.getProduct();

            ProductResponseDTO dto = mapToDTO(product);

            dto.setIsWishlist(true); // always true because it's wishlist page

            return dto;
        });
    }

    @Transactional
    public WishlistResponse toggleWishlist(Long productId)
    {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product Not Found!!!"));

        Optional<Wishlist> existingWishlist =
                wishlistRepository.findByUserAndProduct(user, product);

        if (existingWishlist.isPresent())
        {
            wishlistRepository.delete(existingWishlist.get());

            return new WishlistResponse(false, "Product removed from wishlist");
        }
        else
        {

            Wishlist wishlist = new Wishlist();
            wishlist.setUser(user);
            wishlist.setProduct(product);
            wishlist.setAddedAt(LocalDateTime.now());
            wishlistRepository.save(wishlist);
            return new WishlistResponse(true, "Product added to wishlist");
        }
    }
}
