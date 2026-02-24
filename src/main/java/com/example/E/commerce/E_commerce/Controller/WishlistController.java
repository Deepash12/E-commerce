package com.example.E.commerce.E_commerce.Controller;

import com.example.E.commerce.E_commerce.DTO.Wishlist.ProductAddToWatchlistDTO;
import com.example.E.commerce.E_commerce.Entity.Wishlist.Wishlist;
import com.example.E.commerce.E_commerce.Service.Wishlist.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController
{
    private final WishlistService wishlistService;

    @PostMapping("/addToWishlist")
    private ResponseEntity<?> addToWatchlist(@RequestBody ProductAddToWatchlistDTO productAddToWatchlistDTO)
    {
        return ResponseEntity.ok(wishlistService.productAddToWatchlist(productAddToWatchlistDTO));
    }
    @GetMapping
    private Page<Wishlist> getAllWishlistProduct(@PathVariable Integer PageNumber , @PathVariable Integer PageSize)
    {
        return wishlistService.getAllWishlistProduct(PageNumber,PageSize);
    }

    @DeleteMapping("/delete/{ProductId}")
    private ResponseEntity<String> deleteProductFromWatchlist(@PathVariable Long ProductId)
    {
        return ResponseEntity.ok(wishlistService.deleteProductFromWishlist(ProductId));
    }
}
