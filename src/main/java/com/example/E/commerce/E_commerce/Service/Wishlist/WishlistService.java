package com.example.E.commerce.E_commerce.Service.Wishlist;

import com.example.E.commerce.E_commerce.DTO.Wishlist.ProductAddToWatchlistDTO;
import com.example.E.commerce.E_commerce.Entity.Authorization.User;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WishlistService
{
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;

    public Page<Wishlist> getAllWishlistProduct(Integer pageNumber, Integer pageSize)
    {
        String username =
                SecurityContextHolder.getContext().getAuthentication().getName();
        Sort sort = Sort.by(Sort.Order.desc("addedAt"));
        Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);
        User user = userRepository.findByUsername(username).
                orElseThrow(()-> new BadRequestException("User Not Found!!!"));
        return wishlistRepository.findByUser(user,pageable);
    }

    @Transactional
    public Wishlist productAddToWatchlist(ProductAddToWatchlistDTO productAddToWatchlistDTO)
    {
        String username =
                SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username).
                orElseThrow(()-> new BadRequestException("User Not Found!!!"));

        Product product = productRepository.findById(productAddToWatchlistDTO.getProduct_id()).
                orElseThrow(()-> new BadRequestException("Product Not Found!!!"));

        if(wishlistRepository.existsByUserAndProduct(user,product))
        {
            throw new BadRequestException("Product Already in Watchlist");
        }
        Wishlist wishlist = new Wishlist();
        wishlist.setProduct(product);
        wishlist.setUser(user);
        wishlist.setAddedAt(LocalDateTime.now());
        return wishlistRepository.save(wishlist);
    }

    @Transactional
    public String deleteProductFromWishlist(Long productId)
    {
        String username =
                SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username).
                orElseThrow(()-> new BadRequestException("User not found!!!"));

        Product product = productRepository.findById(productId).
                orElseThrow(()-> new BadRequestException("Product Not Found!!!"));

        Wishlist wishlist = wishlistRepository.findByUserAndProduct(user, product).
                orElseThrow(()-> new BadRequestException("Product not in Wishlist"));

        wishlistRepository.delete(wishlist);
        return "Product Deleted From Wishlist!!!";
    }
}
