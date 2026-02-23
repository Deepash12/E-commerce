package com.example.E.commerce.E_commerce.Service.Payments;

import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Entity.Order.Order;
import com.example.E.commerce.E_commerce.Entity.Order.OrderStatus;
import com.example.E.commerce.E_commerce.Entity.Payment.PaymentStatus;
import com.example.E.commerce.E_commerce.Entity.Payment.Payment;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Order.OrderRepository;
import com.example.E.commerce.E_commerce.Repository.Payments.PaymentGateway;
import com.example.E.commerce.E_commerce.Repository.Payments.PaymentRepository;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import jakarta.transaction.Transactional;
import org.hibernate.usertype.UserTypeSupport;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class PaymentService
{
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;

    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository, OrderRepository orderRepository, PaymentGateway paymentGateway) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.paymentGateway = paymentGateway;
    }


    public Payment initiatePayment(Long orderId,String method)
    {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new BadRequestException("User NOt Found!!!"));

        Order order =orderRepository.findByIdAndUsername(orderId,username)
                .orElseThrow(()-> new BadRequestException("Order Does Not Exist!!!"));

        if(order.getStatus() == OrderStatus.CANCELLED)
        {
            throw new BadRequestException("Order is Cancelled!!!");
        }
        if(order.getPaymentStatus() == PaymentStatus.SUCCESS)
        {
            throw new BadRequestException("Order Already Paid!!!");
        }
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentMethod(method);
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        return paymentRepository.save(payment);

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
                    .findByIdAndOrderUserUsername(paymentId, username)
                    .orElseThrow(() -> new BadRequestException("Payment Not Found"));

            if (!payment.getOrder().getUser().getId().equals(user.getId()))
            {
                throw new BadRequestException("You are not allowed to process this payment");
            }

            if (payment.getStatus() == PaymentStatus.SUCCESS)
            {
                throw new BadRequestException("Payment already successful");
            }

            Order order = payment.getOrder();

            if (order.getStatus() == OrderStatus.CANCELLED)
            {
                throw new BadRequestException("Order already cancelled");
            }

            if (payment.getFailureCount() >= 3)
            {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                throw new BadRequestException("Maximum retry attempts reached. Order cancelled.");
            }

            if(payment.getExpiresAt().isBefore(LocalDateTime.now()))
            {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason("Payment Link Expired!!!");
                order.setStatus(OrderStatus.CANCELLED);
                order.setPaymentStatus(PaymentStatus.FAILED);
                orderRepository.save(order);
                paymentRepository.save(payment);
                throw new BadRequestException("Payment Session Expired , Please Initiate Again");
            }

            boolean success = paymentGateway.processPayment(payment.getAmount());

            if (success)
            {
                payment.setStatus(PaymentStatus.SUCCESS);
                order.setPaymentStatus(PaymentStatus.SUCCESS);
                order.setStatus(OrderStatus.CONFIRMED);
            }
            else
            {
                payment.setFailureCount(payment.getFailureCount() + 1);
                payment.setFailureReason("Gateway Declined Transaction");
                payment.setStatus(PaymentStatus.FAILED);
                order.setPaymentStatus(PaymentStatus.FAILED);

                if (payment.getFailureCount() >= 3)
                {
                    order.setStatus(OrderStatus.CANCELLED);
                }
            }

            orderRepository.save(order);
            return paymentRepository.save(payment);
        }
        catch (ObjectOptimisticLockingFailureException ex)
        {
            throw new BadRequestException("Payment is already being processed. Please try again.");
        }
    }
}
