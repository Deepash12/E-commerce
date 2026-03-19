package com.example.E.commerce.E_commerce.Service.Order;
import com.example.E.commerce.E_commerce.DTO.Address.AddressResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Coupon.CouponDtoOrderResponse;
import com.example.E.commerce.E_commerce.DTO.Coupon.CouponResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Order.CheckoutOrderRequestDTO;
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
import com.example.E.commerce.E_commerce.Entity.Payment.PaymentStatus;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Address.AddressRepository;
import com.example.E.commerce.E_commerce.Repository.Cart.CartItemsRepository;
import com.example.E.commerce.E_commerce.Repository.Cart.CartRepository;
import com.example.E.commerce.E_commerce.Repository.Coupon.CouponRepository;
import com.example.E.commerce.E_commerce.Repository.Order.OrderRepository;
import com.example.E.commerce.E_commerce.Repository.Product.ProductRepository;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import com.example.E.commerce.E_commerce.Service.Coupon.CouponCalculationService;
import com.example.E.commerce.E_commerce.Service.Coupon.CouponService;
import jakarta.mail.Address;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
public class OrderService
{
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final CartItemsRepository cartItemsRepository;
    private final CouponRepository couponRepository;
    private final CouponCalculationService couponCalculationService;

    public OrderService(UserRepository userRepository, CartRepository cartRepository, OrderRepository orderRepository, ProductRepository productRepository, AddressRepository addressRepository, CartItemsRepository cartItemsRepository, CouponRepository couponRepository, CouponCalculationService couponCalculationService) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        this.cartItemsRepository = cartItemsRepository;
        this.couponRepository = couponRepository;
        this.couponCalculationService = couponCalculationService;
    }

    @Transactional
    public OrderResponseDTO checkoutOrders(CheckoutOrderRequestDTO dto)
    {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Order order = checkoutOrder(username, dto);
        return mapToDTO(order);
    }

    private OrderResponseDTO mapToDTO(Order order)
    {
       UserAddresses address = addressRepository.findById(order.getUserAddress().getId()).orElseThrow(()-> new BadRequestException("Address Not Exist!!!"));
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

        Coupon coupon = couponRepository.findById(order.getCoupon().getId())
                .orElseThrow(()-> new BadRequestException("Coupon Not Used!!!"));

        CouponDtoOrderResponse couponResponseDTO = new CouponDtoOrderResponse();
        couponResponseDTO.setCouponCode(coupon.getCouponCode());
        couponResponseDTO.setDiscountAmount(couponResponseDTO.getDiscountAmount());





        List<OrderItemsResponseDTO> items = order.getOrderItems().stream()
                .map
                        (item -> new OrderItemsResponseDTO
                                        (
                                                item.getId(),
                                            item.getProduct().getId(),
                                            item.getProduct().getName(),
                                            item.getProduct().getDescription(),
                                            item.getProduct().getProductImageUrl(),
                                            item.getPriceAtPurchase(),
                                            item.getQuantity(),
                                            item.getPriceAtPurchase()
                                                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                                        )
                )
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

    public OrderResponseDTO checkOrderById(String username, Long id)
    {
        Order order = checkOrder(username,id);
        return mapToDTO(order);
    }

//our Business Logic Methods Here


    private Order checkoutOrder(String username, CheckoutOrderRequestDTO dto) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Cart Not Found!!!"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart Is Empty, cannot proceed to checkout!!!");
        }

        Order order = new Order();
//        UUID id = UUID.randomUUID();
//        order.setId(Long.valueOf(id));
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;

        List<OrderItem> orderItems = new ArrayList<>();

        UserAddresses address = addressRepository
                .findByIdAndUser(dto.getAddressId(), user)
                .orElseThrow(() -> new BadRequestException("Address Not Found!!!"));

        order.setUserAddress(address);
        order.setShippingCity(address.getCity());
        order.setShippingCountry(address.getCountry());
        order.setShippingFullName(address.getFullName());
        order.setShippingPhone(address.getPhone());
        order.setShippingLandmark(address.getLandmark());
        order.setShippingState(address.getState());
        order.setShippingPostalCode(address.getPostalCode());
        order.setShippingAddressLine1(address.getAddressLine1());
        order.setShippingAddressLine2(address.getAddressLine2());

        for (CartItems items : cart.getItems()) {

            Product product = productRepository.findByIdForUpdate
                    (items.getProduct().getId())
                    .orElseThrow(()-> new BadRequestException("Product Not Found!!!"));

            if (product.getStockQuantity() < items.getQuantity()) {
                throw new BadRequestException(
                        "Insufficient Stock For this Product : " + product.getName());
            }

            product.setStockQuantity(
                    product.getStockQuantity() - items.getQuantity()
            );

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(items.getQuantity());
            orderItem.setPriceAtPurchase((product.getPrice()));

            orderItems.add(orderItem);

            BigDecimal itemTotal = orderItem.getPriceAtPurchase()
                    .multiply(BigDecimal.valueOf(orderItem.getQuantity()));

            totalAmount = totalAmount.add(itemTotal);
        }
        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);

        Coupon coupons = cart.getCoupon();

        if(coupons!=null && !coupons.getCouponCode().isBlank())
        {
            discountAmount = couponCalculationService.calculateDiscount(coupons,totalAmount);
            System.out.println("Discout Amount : "+discountAmount);
            order.setCoupon(coupons);
        }
        BigDecimal finalAmount = totalAmount.subtract(discountAmount);

        System.out.println("Final Amount : "+finalAmount);
        if(finalAmount.compareTo(BigDecimal.ZERO)<0)
        {
            finalAmount = BigDecimal.ZERO;
        }
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(finalAmount);


        order.setOrderItems(orderItems);
        orderRepository.saveAndFlush(order);
        cartItemsRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setCoupon(null);
        cartRepository.save(cart);
        return order;
    }


    private Order checkOrder(String username, Long id)
    {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new BadRequestException("User Not Found!!!"));

        Order order= orderRepository.findById(id)
                .orElseThrow(()-> new BadRequestException("You have not placed any order yet"));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Unauthorized access to this order!!!");
        }

        return order;
    }

    @Transactional
    public String cancelOrder(String username, Long id)
    {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new BadRequestException("User Not Found!!!"));

        Order order= orderRepository.findById(id)
                .orElseThrow(()-> new BadRequestException("Order Does Nor Exist!!!"));

        if(!user.getId().equals(order.getUser().getId()))
        {
            throw new BadRequestException("Unauthorized access ton this order!!!");
        }

        if(order.getStatus().equals(OrderStatus.CANCELLED))
        {
            return "Order is already Cancelled!!!";
        }

        if (order.getStatus() != OrderStatus.PENDING &&
                order.getStatus() != OrderStatus.CONFIRMED) {

            return "Order cannot be cancelled now!";
        }
        for (OrderItem item : order.getOrderItems())
        {
            Product product = item.getProduct();
            int updateStock = item.getQuantity()+product.getStockQuantity();
            product.setStockQuantity(updateStock);
        }
        order.setStatus(OrderStatus.CANCELLED);
        return "Order Successfully Cancelled";
    }

    public Page<OrderResponseDTO> viewOrders(Integer pageNumber, Integer pageSize)
    {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(()-> new BadRequestException("User Not Found!!!"));
        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by("createdAt").descending()
        );
        Page<Order> orders = orderRepository.findByUser(user,pageable);
        return orders.map(this::mapToDTO);
    }
}
