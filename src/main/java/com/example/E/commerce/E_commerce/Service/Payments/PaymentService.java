package com.example.E.commerce.E_commerce.Service.Payments;

import com.example.E.commerce.E_commerce.DTO.Order.CheckoutOrderResponseDTO;
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

    public String createPaymentOrder(BigDecimal finalAmount)
    {
        return "PAY_" + UUID.randomUUID();
    }

}