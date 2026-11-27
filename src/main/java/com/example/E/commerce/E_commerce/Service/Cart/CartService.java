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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService
{
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemsRepository cartItemsRepository;

    private boolean isCartItemAvailable(CartItems item)
    {
        return "AVAILABLE".equals(getAvailabilityStatus(item));
    }
    private String getAvailabilityStatus(CartItems item)
    {
        Product product = item.getProduct();

        if (product == null || !product.isActive())
        {
            return "INACTIVE";
        }

        if (item.getQuantity() == null
                || item.getQuantity() <= 0)
        {
            return "INVALID_QUANTITY";
        }

        if (product.getStockQuantity() <= 0)
        {
            return "OUT_OF_STOCK";
        }

        if (item.getQuantity() > product.getStockQuantity())
        {
            return "INSUFFICIENT_STOCK";
        }

        return "AVAILABLE";
    }

    private String getAvailabilityMessage(
            CartItems item, String status)
    {
        return switch (status)
        {
            case "INACTIVE" ->
                    "This product is currently unavailable.";

            case "OUT_OF_STOCK" ->
                    "This product is currently out of stock.";

            case "INSUFFICIENT_STOCK" ->
                    "Only " + item.getProduct().getStockQuantity()
                            + " items available in stock.";

            case "INVALID_QUANTITY" ->
                    "Please select a valid quantity.";

            default -> null;
        };
    }

    private void validateProductAvailability(
            Product product, long quantity)
    {
        if (!product.isActive())
        {
            throw new BadRequestException(
                    "Product is currently unavailable: "
                            + product.getName()
            );
        }
        if (quantity <= 0)
        {
            throw new BadRequestException(
                    "Quantity must be greater than 0"
            );
        }
        if (quantity > product.getStockQuantity())
        {
            throw new BadRequestException(
                    "Only " + product.getStockQuantity()
                            + " items available in stock"
            );
        }
    }

    private BigDecimal calculateCartTotal(Cart cart)
    {
        return cart.getItems().stream()
                .filter(this::isCartItemAvailable)
                .map(item -> item.getProduct().getFinalPrice()
                        .multiply(
                                BigDecimal.valueOf(item.getQuantity())
                        ))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
    private void clearAppliedCoupon(Cart cart)
    {
        BigDecimal cartTotal = calculateCartTotal(cart);

        cart.setCoupon(null);
        cart.setDiscountAmount(BigDecimal.ZERO);
        cart.setTotalAmount(cartTotal);
        cart.setFinalAmount(cartTotal);

        cartRepository.save(cart);
    }

    @Transactional
    public CartResponseDTO viewCart(String username)
    {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new BadRequestException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        List<CartItemsResponseDTO> items = cart.getItems()
                .stream()
                .map(item -> {

                    Product product = item.getProduct();

                    String status = getAvailabilityStatus(item);

                    boolean available = "AVAILABLE".equals(status);

                    BigDecimal totalPrice = product.getFinalPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()))
                            .setScale(2, RoundingMode.HALF_UP);

                    return new CartItemsResponseDTO(
                            product.getId(),
                            product.getName(),
                            product.getFinalPrice(),
                            item.getQuantity(),
                            product.getProductImageUrl(),
                            totalPrice,
                            product.isActive(),
                            product.getStockQuantity(),
                            available,
                            status,
                            getAvailabilityMessage(item, status)
                    );
                })
                .toList();

        BigDecimal grandTotal = calculateCartTotal(cart);

        int totalItems = items.stream()
                .mapToInt(CartItemsResponseDTO::getQuantity)
                .sum();

        int unavailableItemsCount = (int) items.stream()
                .filter(item ->
                        !Boolean.TRUE.equals(item.getIsAvailable()))
                .count();

        boolean hasUnavailableItems = unavailableItemsCount > 0;

        boolean canCheckout =
                !items.isEmpty() && !hasUnavailableItems;

        Coupon coupon = cart.getCoupon();

        if (coupon != null
                && (hasUnavailableItems
                || cart.getTotalAmount() == null
                || cart.getTotalAmount().compareTo(grandTotal) != 0))
        {
            clearAppliedCoupon(cart);
            coupon = null;
        }
        if (coupon == null
                && (cart.getTotalAmount() == null
                || cart.getTotalAmount().compareTo(grandTotal) != 0
                || cart.getFinalAmount() == null
                || cart.getFinalAmount().compareTo(grandTotal) != 0
                || cart.getDiscountAmount() == null
                || cart.getDiscountAmount().signum() != 0))
        {
            clearAppliedCoupon(cart);
        }

        BigDecimal discountAmount = BigDecimal.ZERO;

        if (coupon != null && cart.getDiscountAmount() != null)
        {
            discountAmount = cart.getDiscountAmount()
                    .max(BigDecimal.ZERO)
                    .min(grandTotal);
        }

        BigDecimal finalAmount = grandTotal
                .subtract(discountAmount)
                .max(BigDecimal.ZERO);

        return new CartResponseDTO(
                items,
                grandTotal,
                totalItems,
                coupon != null ? coupon.getCouponCode() : null,
                discountAmount,
                finalAmount,
                hasUnavailableItems,
                unavailableItemsCount,
                canCheckout
        );
    }

    @Transactional
    public String removeProductFromCart(
            String username, Long productId)
    {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new BadRequestException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new BadRequestException("Cart not found"));

        boolean removed = cart.getItems().removeIf(
                item -> item.getProduct().getId().equals(productId)
        );

        if (!removed)
        {
            throw new BadRequestException(
                    "Product not found in cart"
            );
        }

        boolean hadCoupon = cart.getCoupon() != null;

        clearAppliedCoupon(cart);

        return hadCoupon
                ? "Product removed successfully. "
                + "Applied coupon removed due to cart changes."
                : "Product Deleted Successfully";
    }

    @Transactional
    public String addItems(CartItemsRequestDTO cartItemsRequestDTO, String username)
    {
        if (cartItemsRequestDTO.getQuantity() == null
                || cartItemsRequestDTO.getQuantity() <= 0
                || cartItemsRequestDTO.getQuantity() > Integer.MAX_VALUE)
        {
            throw new BadRequestException(
                    "Quantity must be a positive integer within the supported range");
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

        int requestedQuantity =
                Math.toIntExact(cartItemsRequestDTO.getQuantity());

        Optional<CartItems> existingItems =
                cartItemsRepository.findByCartAndProduct(cart, product);

        int currentCartQuantity =
                existingItems.map(CartItems::getQuantity).orElse(0);

        long totalRequestedQuantity =
                (long) currentCartQuantity + requestedQuantity;

        validateProductAvailability(product, totalRequestedQuantity);

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
            cartItems.setQuantity(requestedQuantity);

            cart.getItems().add(cartItems);
        }
        cartItemsRepository.save(cartItems);

        boolean hadCoupon = cart.getCoupon() != null;

        clearAppliedCoupon(cart);

        return hadCoupon
                ? "Product added to cart successfully. "
                + "Applied coupon removed due to cart changes."
                : "Product added to cart successfully.";
    }


    @Transactional
    public String updateQuantityInCart(
            CartItemsRequestDTO request, String username)
    {
        if (request.getQuantity() == null
                || request.getQuantity() < 0
                || request.getQuantity() > Integer.MAX_VALUE)
        {
            throw new BadRequestException(
                    "Invalid quantity"
            );
        }

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new BadRequestException("User does not exist"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new BadRequestException("Cart not found"));

        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new BadRequestException("Product not found"));

        CartItems cartItems = cart.getItems().stream()
                .filter(item ->
                        item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElseThrow(() ->
                        new BadRequestException("Item not in cart"));

        boolean hadCoupon = cart.getCoupon() != null;

        if (request.getQuantity() == 0)
        {
            cart.getItems().remove(cartItems);

            clearAppliedCoupon(cart);

            return hadCoupon
                    ? "Item removed from cart. Applied coupon removed."
                    : "Item removed from cart";
        }

        validateProductAvailability(
                product,
                request.getQuantity()
        );

        if (cartItems.getQuantity().longValue()
                == request.getQuantity().longValue())
        {
            return "Quantity is already up to date";
        }

        cartItems.setQuantity(
                Math.toIntExact(request.getQuantity())
        );

        clearAppliedCoupon(cart);

        return hadCoupon
                ? "Quantity updated successfully. "
                + "Applied coupon removed due to cart changes."
                : "Quantity updated successfully.";
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

        clearAppliedCoupon(cart);

        return viewCart(username);
    }
}