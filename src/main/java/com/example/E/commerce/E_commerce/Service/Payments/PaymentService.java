//package com.example.E.commerce.E_commerce.Service.Payments;
//
//import com.example.E.commerce.E_commerce.DTO.Payment.PaymentMapper;
//import com.example.E.commerce.E_commerce.DTO.Payment.PaymentResponseDTO;
//import com.example.E.commerce.E_commerce.Entity.Authorization.User;
//import com.example.E.commerce.E_commerce.Entity.Coupon.Coupon;
//import com.example.E.commerce.E_commerce.Entity.Order.Order;
//import com.example.E.commerce.E_commerce.Entity.Order.OrderStatus;
//import com.example.E.commerce.E_commerce.Entity.Payment.PaymentMethod;
//import com.example.E.commerce.E_commerce.Entity.Payment.PaymentStatus;
//import com.example.E.commerce.E_commerce.Entity.Payment.Payment;
//import com.example.E.commerce.E_commerce.Exception.BadRequestException;
//import com.example.E.commerce.E_commerce.Repository.Coupon.CouponRepository;
//import com.example.E.commerce.E_commerce.Repository.Order.OrderRepository;
//import com.example.E.commerce.E_commerce.Repository.Payments.PaymentGateway;
//import com.example.E.commerce.E_commerce.Repository.Payments.PaymentRepository;
//import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
//import com.example.E.commerce.E_commerce.Service.Email.EmailService;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.hibernate.usertype.UserTypeSupport;
//import org.springframework.orm.ObjectOptimisticLockingFailureException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Objects;
//import java.util.UUID;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class PaymentService
//{
//    private final PaymentRepository paymentRepository;
//    private final UserRepository userRepository;
//    private final OrderRepository orderRepository;
//    private final PaymentGateway paymentGateway;
//    private final PaymentMapper paymentMapper;
//    private final CouponRepository couponRepository;
//    private final EmailService emailService;
//
//    public PaymentResponseDTO initiatePayment(Long orderId, PaymentMethod method)
//    {
//        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
//
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(()-> new BadRequestException("User NOt Found!!!"));
//
//        Order order =orderRepository.findByIdAndUsername(orderId,username)
//                .orElseThrow(()-> new BadRequestException("Order Does Not Exist!!!"));
//
//        if(order.getStatus() == OrderStatus.CANCELLED)
//        {
//            throw new BadRequestException("Order is Cancelled!!!");
//        }
//        if(order.getPaymentStatus() == PaymentStatus.SUCCESS)
//        {
//            throw new BadRequestException("Order Already Paid!!!");
//        }
//        Payment payment = new Payment();
//        payment.setOrder(order);
//        payment.setAmount(order.getTotalAmount());
//        payment.setStatus(PaymentStatus.PENDING);
//        payment.setPaymentMethod(method);
//        payment.setTransactionId(UUID.randomUUID().toString());
//        payment.setCreatedAt(LocalDateTime.now());
//        payment.setExpiresAt(LocalDateTime.now().plusMinutes(15));
//        Payment savePayment = paymentRepository.save(payment);
//        return paymentMapper.toPaymentResponse(savePayment);
//
//    }
//
//    public Payment completePayment(Long paymentId)
//    {
//        try
//        {
//            String username = Objects.requireNonNull(
//                    SecurityContextHolder.getContext().getAuthentication()
//            ).getName();
//
//            User user = userRepository.findByUsername(username)
//                    .orElseThrow(() -> new BadRequestException("User Not Found"));
//
//            Payment payment = paymentRepository
//                    .findByIdAndOrderUserUsername(paymentId, username)
//                    .orElseThrow(() -> new BadRequestException("Payment Not Found"));
//
//            if (!payment.getOrder().getUser().getId().equals(user.getId()))
//            {
//                throw new BadRequestException("You are not allowed to process this payment");
//            }
//
//            if (payment.getStatus() == PaymentStatus.SUCCESS)
//            {
//                throw new BadRequestException("Payment already successful");
//            }
//
//            Order order = payment.getOrder();
//
//            if (order.getStatus() == OrderStatus.CANCELLED)
//            {
//                throw new BadRequestException("Order already cancelled");
//            }
//
//            if (payment.getFailureCount() >= 3)
//            {
//                order.setStatus(OrderStatus.CANCELLED);
//                orderRepository.save(order);
//                throw new BadRequestException("Maximum retry attempts reached. Order cancelled.");
//            }
//
//            if(payment.getExpiresAt().isBefore(LocalDateTime.now()))
//            {
//                payment.setStatus(PaymentStatus.FAILED);
//                payment.setFailureReason("Payment Link Expired!!!");
//                order.setStatus(OrderStatus.CANCELLED);
//                order.setPaymentStatus(PaymentStatus.FAILED);
//                orderRepository.save(order);
//                paymentRepository.save(payment);
//                throw new BadRequestException("Payment Session Expired , Please Initiate Again");
//            }
//
//            boolean success = paymentGateway.processPayment(payment.getAmount());
//            System.out.println(success);
//            if (success)
//            {
//                payment.setStatus(PaymentStatus.SUCCESS);
//                order.setPaymentStatus(PaymentStatus.SUCCESS);
//                order.setStatus(OrderStatus.CONFIRMED);
//                Coupon coupon = order.getCoupon();
//                if(coupon!=null)
//                {
//                    coupon.setUsedCount(coupon.getUsedCount()+1);
//                    couponRepository.save(coupon);
//                }
//            }
//            else
//            {
//                payment.setFailureCount(
//                        (payment.getFailureCount() == null ? 0 : payment.getFailureCount()) + 1
//                );
////                payment.setFailureCount(payment.getFailureCount() + 1);
//                payment.setFailureReason("Gateway Declined Transaction");
//                payment.setStatus(PaymentStatus.FAILED);
//                order.setPaymentStatus(PaymentStatus.FAILED);
//
//                if (payment.getFailureCount() >= 3)
//                {
//                    order.setStatus(OrderStatus.CANCELLED);
//                }
//            }
//
//            orderRepository.save(order);
//            emailService.confirmedOrderSendMail(user.getEmail(),order);
//            return paymentRepository.save(payment);
//        }
//        catch (ObjectOptimisticLockingFailureException ex)
//        {
//            throw new BadRequestException("Payment is already being processed. Please try again.");
//        }
//    }
//
//    public String createPaymentOrder(BigDecimal finalAmount) {
//    }
//}


package com.example.E.commerce.E_commerce.Service.Payments;

import com.example.E.commerce.E_commerce.DTO.Order.CheckoutOrderResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Order.OrderResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Payment.PaymentMapper;
import com.example.E.commerce.E_commerce.DTO.Payment.PaymentResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Address.UserAddresses;
import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Entity.Coupon.Coupon;
import com.example.E.commerce.E_commerce.Entity.Order.Order;
import com.example.E.commerce.E_commerce.Entity.Order.OrderStatus;
import com.example.E.commerce.E_commerce.Entity.Payment.PaymentMethod;
import com.example.E.commerce.E_commerce.Entity.Payment.PaymentStatus;
import com.example.E.commerce.E_commerce.Entity.Payment.Payment;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Address.AddressRepository;
import com.example.E.commerce.E_commerce.Repository.Coupon.CouponRepository;
import com.example.E.commerce.E_commerce.Repository.Payments.PaymentGateway;
import com.example.E.commerce.E_commerce.Repository.Payments.PaymentRepository;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import com.example.E.commerce.E_commerce.Service.Email.EmailService;
import com.example.E.commerce.E_commerce.Service.Order.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService
{

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentGateway paymentGateway;
    private final PaymentMapper paymentMapper;
    private final CouponRepository couponRepository;
    private final EmailService emailService;
    private final OrderService orderService;
    private final AddressRepository addressRepository;


    // CREATE PAYMENT SESSION
    public PaymentResponseDTO initiatePayment(CheckoutOrderResponseDTO dto, PaymentMethod method)
    {

        String username = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()
        ).getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User Not Found"));

        UserAddresses address = addressRepository.findById(dto.getAddress().getId())
                .orElseThrow(()->new BadRequestException("Address does not exist!!!"));

        Payment payment = new Payment();
        payment.setAmount(dto.getAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentMethod(method);
        payment.setAddress(address);
        payment.setTransactionId(createPaymentOrder(dto.getAmount()));
        payment.setCreatedAt(LocalDateTime.now());
        payment.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        Payment savedPayment = paymentRepository.save(payment);

        return paymentMapper.toPaymentResponse(savedPayment);
    }


    // COMPLETE PAYMENT
    public Payment completePayment(Long paymentId)
    {

        try
        {

            String username = Objects.requireNonNull(
                    SecurityContextHolder.getContext().getAuthentication()
            ).getName();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BadRequestException("User Not Found"));

            Payment payment = paymentRepository
                    .findById(paymentId)
                    .orElseThrow(() -> new BadRequestException("Payment Not Found"));

            if (payment.getStatus() == PaymentStatus.SUCCESS)
            {
                throw new BadRequestException("Payment already successful");
            }

            if (payment.getFailureCount() != null && payment.getFailureCount() >= 3)
            {
                throw new BadRequestException("Maximum retry attempts reached");
            }

            if(payment.getExpiresAt().isBefore(LocalDateTime.now()))
            {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason("Payment Link Expired");
                paymentRepository.save(payment);

                throw new BadRequestException("Payment Session Expired");
            }

            boolean success = paymentGateway.processPayment(payment.getAmount());

            if (success)
            {

                payment.setStatus(PaymentStatus.SUCCESS);

                // CREATE ORDER AFTER SUCCESS PAYMENT
                Order order = orderService.placeOrderAfterPayment(payment.getAddress().getId());

                payment.setOrder(order);

                order.setPaymentStatus(PaymentStatus.SUCCESS);
                order.setStatus(OrderStatus.CONFIRMED);
                order.setPaymentMethod(payment.getPaymentMethod());

                Coupon coupon = order.getCoupon();

                if(coupon != null)
                {
                    coupon.setUsedCount(coupon.getUsedCount() + 1);
                    couponRepository.save(coupon);
                }

                emailService.confirmedOrderSendMail(user.getEmail(), order);

            }
            else
            {

                payment.setFailureCount(
                        (payment.getFailureCount() == null ? 0 : payment.getFailureCount()) + 1
                );

                payment.setFailureReason("Gateway Declined Transaction");

                payment.setStatus(PaymentStatus.FAILED);

            }

            return paymentRepository.save(payment);

        }
        catch (ObjectOptimisticLockingFailureException ex)
        {
            throw new BadRequestException("Payment is already being processed. Please try again.");
        }
    }



    // CREATE DUMMY PAYMENT ORDER (SIMULATION)
    public String createPaymentOrder(BigDecimal finalAmount)
    {
        return "PAY_" + UUID.randomUUID();
    }

}