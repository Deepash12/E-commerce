//package com.example.E.commerce.E_commerce.Service.Order;
//
//import com.example.E.commerce.E_commerce.DTO.Address.AddressResponseDTO;
//import com.example.E.commerce.E_commerce.DTO.Coupon.CouponDtoOrderResponse;
//import com.example.E.commerce.E_commerce.DTO.Order.CheckoutOrderRequestDTO;
//import com.example.E.commerce.E_commerce.DTO.Order.CheckoutOrderResponseDTO;
//import com.example.E.commerce.E_commerce.DTO.Order.OrderItemsResponseDTO;
//import com.example.E.commerce.E_commerce.DTO.Order.OrderResponseDTO;
//import com.example.E.commerce.E_commerce.Entity.Address.UserAddresses;
//import com.example.E.commerce.E_commerce.Entity.Authorization.User;
//import com.example.E.commerce.E_commerce.Entity.Cart.Cart;
//import com.example.E.commerce.E_commerce.Entity.Cart.CartItems;
//import com.example.E.commerce.E_commerce.Entity.Coupon.Coupon;
//import com.example.E.commerce.E_commerce.Entity.Order.Order;
//import com.example.E.commerce.E_commerce.Entity.Order.OrderItem;
//import com.example.E.commerce.E_commerce.Entity.Order.OrderStatus;
//import com.example.E.commerce.E_commerce.Entity.Payment.PaymentStatus;
//import com.example.E.commerce.E_commerce.Entity.Product.Product;
//import com.example.E.commerce.E_commerce.Exception.BadRequestException;
//import com.example.E.commerce.E_commerce.Repository.Address.AddressRepository;
//import com.example.E.commerce.E_commerce.Repository.Cart.CartItemsRepository;
//import com.example.E.commerce.E_commerce.Repository.Cart.CartRepository;
//import com.example.E.commerce.E_commerce.Repository.Order.OrderRepository;
//import com.example.E.commerce.E_commerce.Repository.Product.ProductRepository;
//import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
//import com.example.E.commerce.E_commerce.Service.Coupon.CouponCalculationService;
//import com.example.E.commerce.E_commerce.Service.Coupon.CouponValidationService;
//import jakarta.transaction.Transactional;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDateTime;
//import java.util.*;
//
//@Service
//public class OrderService
//{
//    private final UserRepository userRepository;
//    private final CartRepository cartRepository;
//    private final OrderRepository orderRepository;
//    private final ProductRepository productRepository;
//    private final AddressRepository addressRepository;
//    private final CartItemsRepository cartItemsRepository;
//    private final CouponCalculationService couponCalculationService;
//    private final CouponValidationService couponValidationService;
//
//    public OrderService(UserRepository userRepository, CartRepository cartRepository,
//                        OrderRepository orderRepository, ProductRepository productRepository,
//                        AddressRepository addressRepository, CartItemsRepository cartItemsRepository,
//                        CouponCalculationService couponCalculationService,
//                        CouponValidationService couponValidationService) {
//        this.userRepository = userRepository;
//        this.cartRepository = cartRepository;
//        this.orderRepository = orderRepository;
//        this.productRepository = productRepository;
//        this.addressRepository = addressRepository;
//        this.cartItemsRepository = cartItemsRepository;
//        this.couponCalculationService = couponCalculationService;
//        this.couponValidationService = couponValidationService;
//    }
//
//    private OrderResponseDTO mapToDTO(Order order)
//    {
//        UserAddresses address = addressRepository.findById(order.getUserAddress().getId())
//                .orElseThrow(() -> new BadRequestException("Address Not Exist!!!"));
//
//        AddressResponseDTO addressDto = new AddressResponseDTO();
//        addressDto.setCountry(address.getCountry());
//        addressDto.setState(address.getState());
//        addressDto.setAddressLine1(address.getAddressLine1());
//        addressDto.setAddressLine2(address.getAddressLine2());
//        addressDto.setPhone(address.getPhone());
//        addressDto.setCity(address.getCity());
//        addressDto.setLandmark(address.getLandmark());
//        addressDto.setPostalCode(address.getPostalCode());
//        addressDto.setFullName(address.getFullName());
//
//        CouponDtoOrderResponse couponResponseDTO = null;
//        if (order.getCoupon() != null)
//        {
//            Coupon coupon = order.getCoupon();
//            couponResponseDTO = new CouponDtoOrderResponse();
//            couponResponseDTO.setCouponCode(coupon.getCouponCode());
//            couponResponseDTO.setDiscountAmount(order.getDiscountAmount());
//        }
//
//        List<OrderItemsResponseDTO> items = order.getOrderItems().stream()
//                .map(item -> new OrderItemsResponseDTO(
//                        item.getId(),
//                        item.getProduct().getId(),
//                        item.getProduct().getName(),
//                        item.getProduct().getDescription(),
//                        item.getProduct().getProductImageUrl(),
//                        item.getPriceAtPurchase(),
//                        item.getQuantity(),
//                        item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()))
//                ))
//                .toList();
//
//        OrderResponseDTO dto = new OrderResponseDTO();
//        dto.setId(order.getId());
//        dto.setTotalAmount(order.getTotalAmount());
//        dto.setOrderStatus(order.getStatus().name());
//        dto.setPaymentStatus(order.getPaymentStatus().name());
//        dto.setPaymentMethod(order.getPaymentMethod());
//        dto.setCancelReason(order.getCancelReason());
//        dto.setEstimatedDeliveryDate(order.getCreatedAt().plusDays(4));
//        dto.setCreatedAt(order.getCreatedAt());
//        dto.setAddress(addressDto);
//        dto.setCoupon(couponResponseDTO);
//        dto.setDiscountAmount(order.getDiscountAmount());
//        dto.setItems(items);
//
//        return dto;
//    }
//
//    private AddressResponseDTO mapToAddressDTO(UserAddresses addresses)
//    {
//        return new AddressResponseDTO(
//                addresses.getId(), addresses.getFullName(), addresses.getPhone(),
//                addresses.getAddressLine1(), addresses.getAddressLine2(), addresses.getLandmark(),
//                addresses.getCity(), addresses.getState(), addresses.getPostalCode(),
//                addresses.getCountry(), addresses.getIsDefault(), addresses.getCreatedAt(),
//                addresses.getUpdatedAt()
//        );
//    }
//
//    public OrderResponseDTO checkOrderById(String username, Long id)
//    {
//        Order order = checkOrder(username, id);
//        return mapToDTO(order);
//    }
//
//    @Transactional
//    public CheckoutOrderResponseDTO checkoutOrders(CheckoutOrderRequestDTO dto)
//    {
//        String username = Objects.requireNonNull(
//                SecurityContextHolder.getContext().getAuthentication()
//        ).getName();
//
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));
//
//        Cart cart = cartRepository.findByUser(user)
//                .orElseThrow(() -> new BadRequestException("Cart Not Found!!!"));
//
//        if (cart.getItems() == null || cart.getItems().isEmpty())
//        {
//            throw new BadRequestException("Cart is empty, cannot proceed to checkout!");
//        }
//
//        BigDecimal totalAmount = BigDecimal.ZERO;
//        for (CartItems item : cart.getItems())
//        {
//            Product product = productRepository.findById(item.getProduct().getId())
//                    .orElseThrow(() -> new BadRequestException("Product Not Found!!!"));
//            BigDecimal itemTotal = product.getFinalPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
//            totalAmount = totalAmount.add(itemTotal);
//        }
//        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
//
//        BigDecimal discountAmount = BigDecimal.ZERO;
//        Coupon coupon = null;
//
//        if (dto.getCouponCode() != null && !dto.getCouponCode().isBlank())
//        {
//            coupon = couponCalculationService.validateCoupon(dto.getCouponCode(), totalAmount);
//            discountAmount = couponCalculationService.calculateDiscount(coupon, totalAmount);
//        }
//
//        BigDecimal finalAmount = totalAmount.subtract(discountAmount);
//        if (finalAmount.compareTo(BigDecimal.ZERO) < 0)
//        {
//            finalAmount = BigDecimal.ZERO;
//        }
//
//        UserAddresses addresses = addressRepository.findById(dto.getAddressId())
//                .orElseThrow(() -> new BadRequestException("Address Not Found!!!"));
//
//        AddressResponseDTO addressResponse = mapToAddressDTO(addresses);
//        return new CheckoutOrderResponseDTO(addressResponse, finalAmount);
//    }
//
//    private Order checkOrder(String username, Long id)
//    {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));
//
//        Order order = orderRepository.findById(id)
//                .orElseThrow(() -> new BadRequestException("You have not placed any order yet"));
//
//        if (!order.getUser().getId().equals(user.getId()))
//        {
//            throw new BadRequestException("Unauthorized access to this order!!!");
//        }
//        return order;
//    }
//
//    @Transactional
//    public String cancelOrder(String username, Long id)
//    {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));
//
//        Order order = orderRepository.findById(id)
//                .orElseThrow(() -> new BadRequestException("Order Does Not Exist!!!"));
//
//        if (!user.getId().equals(order.getUser().getId()))
//        {
//            throw new BadRequestException("Unauthorized access to this order!!!");
//        }
//
//        if (order.getStatus().equals(OrderStatus.CANCELLED))
//        {
//            return "Order is already Cancelled!!!";
//        }
//
//        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED)
//        {
//            return "Order cannot be cancelled now!";
//        }
//
//        for (OrderItem item : order.getOrderItems())
//        {
//            Product product = item.getProduct();
//            int updatedStock = item.getQuantity() + product.getStockQuantity();
//            product.setStockQuantity(updatedStock);
//        }
//        order.setStatus(OrderStatus.CANCELLED);
//        return "Order Successfully Cancelled";
//    }
//
//    public Page<OrderResponseDTO> viewOrders(Integer pageNumber, Integer pageSize)
//    {
//        String username = Objects.requireNonNull(
//                SecurityContextHolder.getContext().getAuthentication()
//        ).getName();
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));
//        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
//        Page<Order> orders = orderRepository.findByUser(user, pageable);
//        return orders.map(this::mapToDTO);
//    }
//
//    @Transactional
//    public Order placeOrderAfterPayment(Long addressId)
//    {
//        String username = Objects.requireNonNull(
//                SecurityContextHolder.getContext().getAuthentication()
//        ).getName();
//
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));
//
//        Cart cart = cartRepository.findByUser(user)
//                .orElseThrow(() -> new BadRequestException("Cart Not Found!!!"));
//
//        if (cart.getItems() == null || cart.getItems().isEmpty())
//        {
//            throw new BadRequestException("Cart Is Empty!!!");
//        }
//
//        UserAddresses addresses = addressRepository.findById(addressId)
//                .orElseThrow(() -> new BadRequestException("Address Not Exist!!!"));
//
//        Order order = new Order();
//        order.setUser(user);
//        order.setStatus(OrderStatus.CONFIRMED);
//        order.setPaymentStatus(PaymentStatus.SUCCESS);
//        order.setUserAddress(addresses);
//
//        BigDecimal totalAmount = BigDecimal.ZERO;
//        List<OrderItem> orderItems = new ArrayList<>();
//
//        for (CartItems items : cart.getItems())
//        {
//            Product product = productRepository.findByIdForUpdate(items.getProduct().getId())
//                    .orElseThrow(() -> new BadRequestException("Product Not Found!!!"));
//
//            if (product.getStockQuantity() < items.getQuantity())
//            {
//                throw new BadRequestException("Insufficient Stock For Product: " + product.getName());
//            }
//
//            product.setStockQuantity(product.getStockQuantity() - items.getQuantity());
//
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(order);
//            orderItem.setProduct(product);
//            orderItem.setProductName(product.getName());
//            orderItem.setQuantity(items.getQuantity());
//            orderItem.setPriceAtPurchase(product.getFinalPrice());
//            orderItems.add(orderItem);
//
//            BigDecimal itemTotal = product.getFinalPrice().multiply(BigDecimal.valueOf(items.getQuantity()));
//            totalAmount = totalAmount.add(itemTotal);
//        }
//
//        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
//
//        BigDecimal discountAmount = BigDecimal.ZERO;
//        Coupon coupon = cart.getCoupon();
//
//        // FIX: Re-validate the coupon one final time at order placement.
//        // This guards against edge cases where the coupon became invalid between
//        // the time it was applied and the time payment completed (e.g. expired,
//        // global limit hit by another user, etc.).
//        if (coupon != null)
//        {
//            boolean couponStillValid = coupon.getIsActive()
//                    && (coupon.getExpiryAt() == null || coupon.getExpiryAt().isAfter(LocalDateTime.now()))
//                    && (coupon.getValidFrom() == null || coupon.getValidFrom().isBefore(LocalDateTime.now()))
//                    && (coupon.getGlobalUsageLimit() == null || coupon.getUsedCount() < coupon.getGlobalUsageLimit())
//                    && totalAmount.compareTo(coupon.getMinOrderAmount()) >= 0;
//
//            if (couponStillValid)
//            {
//                discountAmount = couponCalculationService.calculateDiscount(coupon, totalAmount);
//                order.setCoupon(coupon);  // FIX: Link coupon to the order
//            }
//            // If coupon is no longer valid, we silently proceed without the discount.
//            // The payment was already collected for the discounted amount, so a
//            // production system would handle this with a refund flow. For now,
//            // this protects against data inconsistency.
//        }
//
//        BigDecimal finalAmount = totalAmount.subtract(discountAmount);
//        if (finalAmount.compareTo(BigDecimal.ZERO) < 0)
//        {
//            finalAmount = BigDecimal.ZERO;
//        }
//
//        order.setDiscountAmount(discountAmount);
//        order.setTotalAmount(finalAmount);
//        order.setOrderItems(orderItems);
//
//        orderRepository.saveAndFlush(order);
//
//        cartItemsRepository.deleteAll(cart.getItems());
//        cart.getItems().clear();
//        cart.setCoupon(null);
//        cartRepository.save(cart);
//
//        return order;
//    }
//}
package com.example.E.commerce.E_commerce.Service.Order;

import com.example.E.commerce.E_commerce.DTO.Address.AddressResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Coupon.CouponDtoOrderResponse;
import com.example.E.commerce.E_commerce.DTO.Order.CheckoutOrderRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Order.CheckoutOrderResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Order.OrderItemsResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Order.OrderResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Address.UserAddresses;
import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Entity.Cart.Cart;
import com.example.E.commerce.E_commerce.Entity.Cart.CartItems;
import com.example.E.commerce.E_commerce.Entity.Coupon.Coupon;
import com.example.E.commerce.E_commerce.Entity.Order.Order;
import com.example.E.commerce.E_commerce.Entity.Order.OrderItem;
import com.example.E.commerce.E_commerce.Entity.Order.OrderStatus;
import com.example.E.commerce.E_commerce.Entity.Payment.PaymentMethod;
import com.example.E.commerce.E_commerce.Entity.Payment.PaymentStatus;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Address.AddressRepository;
import com.example.E.commerce.E_commerce.Repository.Cart.CartItemsRepository;
import com.example.E.commerce.E_commerce.Repository.Cart.CartRepository;
import com.example.E.commerce.E_commerce.Repository.Order.OrderRepository;
import com.example.E.commerce.E_commerce.Repository.Product.ProductRepository;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import com.example.E.commerce.E_commerce.Service.Coupon.CouponCalculationService;
import com.example.E.commerce.E_commerce.Service.Coupon.CouponValidationService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService
{
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final CartItemsRepository cartItemsRepository;
    private final CouponCalculationService couponCalculationService;
    private final CouponValidationService couponValidationService;

    public OrderService(UserRepository userRepository, CartRepository cartRepository,
                        OrderRepository orderRepository, ProductRepository productRepository,
                        AddressRepository addressRepository, CartItemsRepository cartItemsRepository,
                        CouponCalculationService couponCalculationService,
                        CouponValidationService couponValidationService)
    {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        this.cartItemsRepository = cartItemsRepository;
        this.couponCalculationService = couponCalculationService;
        this.couponValidationService = couponValidationService;
    }

    private OrderResponseDTO mapToDTO(Order order)
    {
        UserAddresses address = addressRepository.findById(order.getUserAddress().getId())
                .orElseThrow(() -> new BadRequestException("Address Not Exist!!!"));

        AddressResponseDTO addressDto = new AddressResponseDTO();
        addressDto.setCountry(address.getCountry());
        addressDto.setState(address.getState());
        addressDto.setAddressLine1(address.getAddressLine1());
        addressDto.setAddressLine2(address.getAddressLine2());
        addressDto.setPhone(address.getPhone());
        addressDto.setCity(address.getCity());
        addressDto.setLandmark(address.getLandmark());
        addressDto.setPostalCode(address.getPostalCode());
        addressDto.setFullName(address.getFullName());

        CouponDtoOrderResponse couponResponseDTO = null;
        if (order.getCoupon() != null)
        {
            Coupon coupon = order.getCoupon();
            couponResponseDTO = new CouponDtoOrderResponse();
            couponResponseDTO.setCouponCode(coupon.getCouponCode());
            couponResponseDTO.setDiscountAmount(order.getDiscountAmount());
        }

        List<OrderItemsResponseDTO> items = order.getOrderItems().stream()
                .map(item -> new OrderItemsResponseDTO(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getDescription(),
                        item.getProduct().getProductImageUrl(),
                        item.getPriceAtPurchase(),
                        item.getQuantity(),
                        item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderStatus(order.getStatus().name());
        dto.setPaymentStatus(order.getPaymentStatus().name());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCancelReason(order.getCancelReason());
        dto.setEstimatedDeliveryDate(order.getCreatedAt().plusDays(4));
        dto.setCreatedAt(order.getCreatedAt());
        dto.setAddress(addressDto);
        dto.setCoupon(couponResponseDTO);
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setItems(items);

        return dto;
    }

    private AddressResponseDTO mapToAddressDTO(UserAddresses addresses)
    {
        return new AddressResponseDTO(
                addresses.getId(), addresses.getFullName(), addresses.getPhone(),
                addresses.getAddressLine1(), addresses.getAddressLine2(), addresses.getLandmark(),
                addresses.getCity(), addresses.getState(), addresses.getPostalCode(),
                addresses.getCountry(), addresses.getIsDefault(), addresses.getCreatedAt(),
                addresses.getUpdatedAt()
        );
    }

    public OrderResponseDTO checkOrderById(String username, Long id)
    {
        Order order = checkOrder(username, id);
        return mapToDTO(order);
    }

    // ─────────────────────────────────────────────────────────────
    // STEP 1 — Checkout: sirf price + coupon calculate karke return karo
    // Koi bhi DB write nahi hoti, cart touch nahi hoti
    // User back ja sakta hai freely
    // ─────────────────────────────────────────────────────────────
    @Transactional
    public CheckoutOrderResponseDTO checkoutOrders(CheckoutOrderRequestDTO dto)
    {
        String username = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()
        ).getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Cart Not Found!!!"));

        if (cart.getItems() == null || cart.getItems().isEmpty())
        {
            throw new BadRequestException("Cart is empty, cannot proceed to checkout!");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItems item : cart.getItems())
        {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new BadRequestException("Product Not Found!!!"));
            BigDecimal itemTotal = product.getFinalPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);

        BigDecimal discountAmount = BigDecimal.ZERO;
        Coupon coupon = null;

        if (dto.getCouponCode() != null && !dto.getCouponCode().isBlank())
        {
            coupon = couponCalculationService.validateCoupon(dto.getCouponCode(), totalAmount);
            discountAmount = couponCalculationService.calculateDiscount(coupon, totalAmount);
        }

        BigDecimal finalAmount = totalAmount.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            finalAmount = BigDecimal.ZERO;
        }

        UserAddresses addresses = addressRepository.findById(dto.getAddressId())
                .orElseThrow(() -> new BadRequestException("Address Not Found!!!"));

        AddressResponseDTO addressResponse = mapToAddressDTO(addresses);
        return new CheckoutOrderResponseDTO(addressResponse, finalAmount);
    }

    // ─────────────────────────────────────────────────────────────
    // STEP 2 — Payment success ke baad call hota hai
    // Order create + CONFIRMED + cart clear — sab ek saath
    // ─────────────────────────────────────────────────────────────
    @Transactional
    public Order placeOrderAfterPayment(Long addressId, PaymentMethod paymentMethod)
    {
        String username = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()
        ).getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Cart Not Found!!!"));

        if (cart.getItems() == null || cart.getItems().isEmpty())
        {
            throw new BadRequestException("Cart Is Empty!!!");
        }

        UserAddresses addresses = addressRepository.findById(addressId)
                .orElseThrow(() -> new BadRequestException("Address Not Exist!!!"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        order.setPaymentMethod(paymentMethod);
        order.setUserAddress(addresses);
        order.setConfirmedAt(LocalDateTime.now());

        // Shipping snapshot — address change hone par bhi order history safe rahe
        order.setShippingFullName(addresses.getFullName());
        order.setShippingPhone(addresses.getPhone());
        order.setShippingAddressLine1(addresses.getAddressLine1());
        order.setShippingAddressLine2(addresses.getAddressLine2());
        order.setShippingCity(addresses.getCity());
        order.setShippingState(addresses.getState());
        order.setShippingPostalCode(addresses.getPostalCode());
        order.setShippingLandmark(addresses.getLandmark());
        order.setShippingCountry(addresses.getCountry());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItems items : cart.getItems())
        {
            Product product = productRepository.findByIdForUpdate(items.getProduct().getId())
                    .orElseThrow(() -> new BadRequestException("Product Not Found!!!"));

            if (product.getStockQuantity() < items.getQuantity())
            {
                throw new BadRequestException("Insufficient Stock For Product: " + product.getName());
            }

            // Stock deduct karo
            product.setStockQuantity(product.getStockQuantity() - items.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(items.getQuantity());
            orderItem.setPriceAtPurchase(product.getFinalPrice());
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(
                    product.getFinalPrice().multiply(BigDecimal.valueOf(items.getQuantity()))
            );
        }

        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);

        BigDecimal discountAmount = BigDecimal.ZERO;
        Coupon coupon = cart.getCoupon();

        if (coupon != null)
        {
            boolean couponStillValid = coupon.getIsActive()
                    && (coupon.getExpiryAt() == null || coupon.getExpiryAt().isAfter(LocalDateTime.now()))
                    && (coupon.getValidFrom() == null || coupon.getValidFrom().isBefore(LocalDateTime.now()))
                    && (coupon.getGlobalUsageLimit() == null || coupon.getUsedCount() < coupon.getGlobalUsageLimit())
                    && totalAmount.compareTo(coupon.getMinOrderAmount()) >= 0;

            if (couponStillValid)
            {
                discountAmount = couponCalculationService.calculateDiscount(coupon, totalAmount);
                order.setCoupon(coupon);
            }
        }

        BigDecimal finalAmount = totalAmount.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            finalAmount = BigDecimal.ZERO;
        }

        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(finalAmount);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.saveAndFlush(order);

        // Cart clear karo — sirf payment success ke baad
        cartItemsRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setCoupon(null);
        cartRepository.save(cart);

        return savedOrder;
    }

    private Order checkOrder(String username, Long id)
    {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("You have not placed any order yet"));

        if (!order.getUser().getId().equals(user.getId()))
        {
            throw new BadRequestException("Unauthorized access to this order!!!");
        }
        return order;
    }

    @Transactional
    public String cancelOrder(String username, Long id)
    {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Order Does Not Exist!!!"));

        if (!user.getId().equals(order.getUser().getId()))
        {
            throw new BadRequestException("Unauthorized access to this order!!!");
        }

        if (order.getStatus().equals(OrderStatus.CANCELLED))
        {
            return "Order is already Cancelled!!!";
        }

        if (order.getStatus() != OrderStatus.CONFIRMED)
        {
            return "Order cannot be cancelled now!";
        }

        for (OrderItem item : order.getOrderItems())
        {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        return "Order Successfully Cancelled";
    }

    public Page<OrderResponseDTO> viewOrders(Integer pageNumber, Integer pageSize)
    {
        String username = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()
        ).getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        Page<Order> orders = orderRepository.findByUser(user, pageable);
        return orders.map(this::mapToDTO);
    }
}