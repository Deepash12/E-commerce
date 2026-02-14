package com.example.E.commerce.E_commerce.Service.Cart;
import com.example.E.commerce.E_commerce.DTO.Cart.CartItemsRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Cart.CartItemsResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Cart.CartResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Cart.Cart;
import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Entity.Cart.CartItems;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Cart.CartItemsRepository;
import com.example.E.commerce.E_commerce.Repository.Cart.CartRepository;
import com.example.E.commerce.E_commerce.Repository.Product.ProductRepository;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CartService
{
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemsRepository cartItemsRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository, CartItemsRepository cartItemsRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemsRepository = cartItemsRepository;
    }

    public CartResponseDTO viewCart(String username)
    {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        Cart cart1 = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        List<CartItemsResponseDTO> items = cart1.getItems().stream()
                .map(item-> new CartItemsResponseDTO
                        (
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getProduct().getPrice(),
                                item.getQuantity(),
                                item.getProduct().getPrice()* item.getQuantity()
                        )).toList();
        double totalAmount = items.stream().mapToDouble(CartItemsResponseDTO::getTotalPrice).sum();

        int totalItems = items.stream().mapToInt(CartItemsResponseDTO::getQuantity).sum();

        return new CartResponseDTO(items,totalAmount,totalItems);
    }

    @Transactional
    public String removeProductFromCart(String username, Long productId)
    {
        try
        {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BadRequestException("User not found"));


            Cart cart1 =cartRepository.findByUser(user).
                    orElseThrow(() -> new BadRequestException("cart not found"));

            int delete = cartRepository.RemoveItemFromCart(cart1.getId(), productId);
            if(delete == 0)
            {
                throw new BadRequestException("Product not found in cart");
            }
            else{
                return "Product Deleted Successfully";
            }


        } catch (Exception e) {
            throw new BadRequestException("Product Does Not Exist!!!");
        }

    }

    public String addItems(CartItemsRequestDTO cartItemsRequestDTO,
                           String username)
    {

        if (cartItemsRequestDTO.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        User user = userRepository.findByUsername(username)
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


        Optional<CartItems> existingItems =
                cartItemsRepository.findByCartAndProduct(cart, product);

        int currentCartQuantity = existingItems.map(CartItems::getQuantity).orElse(0);
        int totalRequestedQuantity = currentCartQuantity + requestedQuantity;
        if(totalRequestedQuantity>productStockQuantity)
        {
            throw new BadRequestException("Only " + productStockQuantity + " items available in stock");
        }
        if (existingItems.isPresent()) {

            CartItems cartItems = existingItems.get();
            cartItems.setQuantity(
                    cartItems.getQuantity() + requestedQuantity
            );
            cartItemsRepository.save(cartItems);
            return "Product added to cart successfully";

        } else {

            CartItems cartItems = new CartItems();
            cartItems.setCart(cart);
            cartItems.setProduct(product);
            cartItems.setQuantity(cartItemsRequestDTO.getQuantity().intValue());

            cartItemsRepository.save(cartItems);
            return "Product added to cart successfully";
        }


    }

    public String updateQuantityInCart(CartItemsRequestDTO request,
                                       String username) {

        if (request.getQuantity() < 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User does not exist"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Cart not found"));


        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BadRequestException("Product not found"));

        CartItems cartItems = cartItemsRepository
                .findByCartAndProduct(cart, product)
                .orElseThrow(() -> new BadRequestException("Item not in cart"));

        if (request.getQuantity() == 0) {
            cartItemsRepository.delete(cartItems);
            return "Item removed from cart";
        }
        cartItems.setQuantity(request.getQuantity().intValue());

        cartItemsRepository.save(cartItems);

        return "Quantity updated successfully";
    }

}
