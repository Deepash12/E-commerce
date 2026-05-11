package com.example.E.commerce.E_commerce.Service.Cart;

import com.example.E.commerce.E_commerce.DTO.Cart.CartItemsRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Cart.CartItemsResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Cart.CartResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Cart.Cart;
import com.example.E.commerce.E_commerce.Entity.Authorization.Users;
import com.example.E.commerce.E_commerce.Entity.Cart.CartItems;
import com.example.E.commerce.E_commerce.Entity.Coupon.Coupon;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Cart.CartItemsRepository;
import com.example.E.commerce.E_commerce.Repository.Cart.CartRepository;
import com.example.E.commerce.E_commerce.Repository.Product.ProductRepository;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService
{
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemsRepository cartItemsRepository;

    private BigDecimal calculateCartTotal(Cart cart)
    {
        return cart.getItems().stream()
                .filter(item -> item.getProduct().isActive())
                .map(item -> item.getProduct().getFinalPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
    private void clearAppliedCoupon(Cart cart)
    {
        cart.setCoupon(null);
        cart.setDiscountAmount(BigDecimal.ZERO);

        cartRepository.save(cart);
    }

    private String revalidateAndClearCouponIfInvalid(Cart cart)
    {
        Coupon coupon = cart.getCoupon();
        if (coupon == null)
        {
            return null; // nothing to validate
        }

        String reason = null;

        if (!coupon.getIsActive())
        {
            reason = "Coupon '" + coupon.getCouponCode() + "' is no longer active and has been removed from your cart.";
        }
        else if (coupon.getExpiryAt() != null && coupon.getExpiryAt().isBefore(LocalDateTime.now()))
        {
            reason = "Coupon '" + coupon.getCouponCode() + "' has expired and has been removed from your cart.";
        }
        else if (coupon.getGlobalUsageLimit() != null && coupon.getUsedCount() >= coupon.getGlobalUsageLimit())
        {
            reason = "Coupon '" + coupon.getCouponCode() + "' has reached its usage limit and has been removed from your cart.";
        }
        else
        {
            BigDecimal cartTotal = calculateCartTotal(cart);
            if (coupon.getMinOrderAmount() != null && cartTotal.compareTo(coupon.getMinOrderAmount()) < 0)
            {
                reason = "Coupon '" + coupon.getCouponCode() + "' requires a minimum order of ₹"
                        + coupon.getMinOrderAmount() + ". It has been removed because your cart total is now ₹"
                        + cartTotal + ".";
            }
        }

        if (reason != null)
        {
            cart.setCoupon(null);
            // cart will be saved by the calling method, no extra save needed here
        }

        return reason;
    }

    public CartResponseDTO viewCart(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        List<CartItemsResponseDTO> items = cart.getItems().stream()
                .filter(cartItems -> cartItems.getProduct().isActive())
                .map(item -> new CartItemsResponseDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getFinalPrice(),
                        item.getQuantity(),
                        item.getProduct().getProductImageUrl(),
                        item.getProduct().getFinalPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity())),
                        true
                )).toList();

        BigDecimal grandTotal = items.stream()
                .map(CartItemsResponseDTO::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream().mapToInt(CartItemsResponseDTO::getQuantity).sum();

        // ✅ Coupon info include karo response mein
        Coupon coupon = cart.getCoupon();


        if (coupon != null) {
            BigDecimal discountAmount = cart.getDiscountAmount() != null
                    ? cart.getDiscountAmount()   // ← DB se lo, recalculate mat karo
                    : BigDecimal.ZERO;
            BigDecimal finalAmount = grandTotal.subtract(discountAmount);
            return new CartResponseDTO(items, grandTotal, totalItems,
                    coupon.getCouponCode(), discountAmount, finalAmount);
        }

        return new CartResponseDTO(items, grandTotal, totalItems);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Remove product from cart
    // ─────────────────────────────────────────────────────────────────────────
    @Transactional
    public String removeProductFromCart(String username, Long productId)
    {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Cart not found"));

        int deleted = cartRepository.RemoveItemFromCart(cart.getId(), productId);
        if (deleted == 0)
        {
            throw new BadRequestException("Product not found in cart");
        }

        // Reload items so the cart total reflects the removal
        cartItemsRepository.flush();
        cartRepository.flush();
        Cart refreshedCart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Cart not found"));

        String couponWarning = revalidateAndClearCouponIfInvalid(refreshedCart);
        cartRepository.save(refreshedCart);

        if (couponWarning != null)
        {
            return "Product removed from cart. Note: " + couponWarning;
        }
        return "Product Deleted Successfully";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Add item to cart
    // ─────────────────────────────────────────────────────────────────────────
    @Transactional
    public String addItems(CartItemsRequestDTO cartItemsRequestDTO, String username)
    {
        if (cartItemsRequestDTO.getQuantity() <= 0)
        {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User does not exist"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        Product product = productRepository
                .findById(cartItemsRequestDTO.getProductId())
                .orElseThrow(() -> new BadRequestException("Product does not exist"));

        int requestedQuantity = Math.toIntExact(cartItemsRequestDTO.getQuantity());
        int productStockQuantity = product.getStockQuantity();

        Optional<CartItems> existingItems = cartItemsRepository.findByCartAndProduct(cart, product);

        int currentCartQuantity = existingItems.map(CartItems::getQuantity).orElse(0);
        int totalRequestedQuantity = currentCartQuantity + requestedQuantity;

        if (totalRequestedQuantity > productStockQuantity)
        {
            throw new BadRequestException("Only " + productStockQuantity + " items available in stock");
        }

        CartItems cartItems;
        if (existingItems.isPresent())
        {
            cartItems = existingItems.get();
            cartItems.setQuantity(cartItems.getQuantity() + requestedQuantity);
        }
        else
        {
            cartItems = new CartItems();
            cartItems.setCart(cart);
            cartItems.setProduct(product);
            cartItems.setQuantity(cartItemsRequestDTO.getQuantity().intValue());
        }
        cartItemsRepository.save(cartItems);
        if (cart.getCoupon() != null)
        {
            clearAppliedCoupon(cart);
        }



        return "Product added to cart successfully. Applied coupon removed due to cart changes.";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Update quantity in cart
    // ─────────────────────────────────────────────────────────────────────────
    @Transactional
    public String updateQuantityInCart(CartItemsRequestDTO request, String username)
    {
        if (request.getQuantity() < 0)
        {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User does not exist"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Cart not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BadRequestException("Product not found"));

        CartItems cartItems = cartItemsRepository
                .findByCartAndProduct(cart, product)
                .orElseThrow(() -> new BadRequestException("Item not in cart"));

        if (request.getQuantity() == 0)
        {
            cartItemsRepository.delete(cartItems);

            // Removing an item reduces cart total — coupon may no longer be valid
            String couponWarning = revalidateAndClearCouponIfInvalid(cart);
            cartRepository.save(cart);

            if (couponWarning != null)
            {
                return "Item removed from cart. Note: " + couponWarning;
            }
            return "Item removed from cart";
        }

        if (request.getQuantity() > product.getStockQuantity())
        {
            throw new BadRequestException("Only " + product.getStockQuantity() + " items available in stock");
        }

        int oldQuantity = cartItems.getQuantity();
        cartItems.setQuantity(request.getQuantity().intValue());
        cartItemsRepository.save(cartItems);

        if (cart.getCoupon() != null)
        {
            clearAppliedCoupon(cart);
        }

        // Only re-validate coupon if quantity was reduced (cart total went down)
        if (request.getQuantity() < oldQuantity)
        {
            String couponWarning = revalidateAndClearCouponIfInvalid(cart);
            if (couponWarning != null)
            {
                cartRepository.save(cart);
                return "Quantity updated successfully. Note: " + couponWarning;
            }
        }

        return "Quantity updated successfully. Applied coupon removed due to cart changes.";
    }
    @Transactional
    public CartResponseDTO removeCoupon(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Cart not found"));

        if (cart.getCoupon() == null) {
            throw new BadRequestException("No coupon applied on cart");
        }

        // ✅ Coupon clear karo
        cart.setCoupon(null);
        cart.setDiscountAmount(BigDecimal.ZERO);
        cart.setFinalAmount(calculateCartTotal(cart));
        cartRepository.save(cart);

        return viewCart(username);
    }
}