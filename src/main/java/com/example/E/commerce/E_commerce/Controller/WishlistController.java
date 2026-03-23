package com.example.E.commerce.E_commerce.Controller;

import com.example.E.commerce.E_commerce.DTO.Product.ProductResponseDTO;
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

    @PutMapping("/toggleWishlist/{ProductId}")
    private ResponseEntity<?> addToWatchlist(@PathVariable Long ProductId)
    {
        return ResponseEntity.ok(wishlistService.toggleWishlist(ProductId));
    }
    @GetMapping
    private Page<ProductResponseDTO> getAllWishlistProduct(@RequestParam (defaultValue = "0") Integer PageNumber ,
                                                           @RequestParam(defaultValue = "10") Integer PageSize)
    {
        return wishlistService.getAllWishlistProduct(PageNumber,PageSize);
    }
}
