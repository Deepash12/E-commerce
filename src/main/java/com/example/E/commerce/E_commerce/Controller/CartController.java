package com.example.E.commerce.E_commerce.Controller;

import com.example.E.commerce.E_commerce.DTO.Cart.CartItemsRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Cart.CartResponseDTO;
import com.example.E.commerce.E_commerce.Service.Cart.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@PreAuthorize("hasRole('USER')")
public class CartController
{
    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/view")
    public ResponseEntity<CartResponseDTO> ViewCart(Authentication authentication)
    {
        String username = authentication.getName();
        return ResponseEntity.ok(cartService.viewCart(username));
    }

    @PostMapping("/addToCart")
    public ResponseEntity<String> addToCart(@RequestBody CartItemsRequestDTO cartItemsRequestDTO,Authentication authentication)
    {
        String username = authentication.getName();
        return ResponseEntity.ok(cartService.addItems(cartItemsRequestDTO,username));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> RemoveProductFromCart(@PathVariable Long productId, Authentication authentication)
    {
        String username = authentication.getName();
            return ResponseEntity.ok(cartService.removeProductFromCart(username,productId));
    }

    @PutMapping("/update/quantity")
    public ResponseEntity<String> UpdateProductQuantity(@RequestBody CartItemsRequestDTO cartItemsRequestDTO,Authentication authentication)
    {
        String username = authentication.getName();
        return ResponseEntity.ok(cartService.updateQuantityInCart(cartItemsRequestDTO,username));
    }

}
