package com.example.E.commerce.E_commerce.Scheduler;

import com.example.E.commerce.E_commerce.Entity.Order.Order;
import com.example.E.commerce.E_commerce.Entity.Order.OrderStatus;
import com.example.E.commerce.E_commerce.Repository.Order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusScheduler {

    private final OrderRepository orderRepository;

    // ─────────────────────────────────────────────────────────────────────
    // Runs every 30 minutes automatically — no manual trigger needed
    // cron = "0 */30 * * * *"  →  at second 0, every 30 minutes
    // ─────────────────────────────────────────────────────────────────────
    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void progressOrderStatuses() {

        LocalDateTime now = LocalDateTime.now();
        log.info("[ OrderScheduler ] Running at {}", now);

        // ── CONFIRMED → IN_PROGRESS  (after 12 hours) ────────────────────
        List<Order> confirmedOrders = orderRepository
                .findByStatus(OrderStatus.CONFIRMED);

        for (Order order : confirmedOrders) {
            if (order.getConfirmedAt() != null &&
                    order.getConfirmedAt().plusHours(12).isBefore(now)) {

                order.setStatus(OrderStatus.IN_PROGRESS);
                order.setInProgressAt(now);
                orderRepository.save(order);
                log.info("Order {} → IN_PROGRESS", order.getId());
            }
        }

        // ── IN_PROGRESS → SHIPPED  (after 24 hours of IN_PROGRESS) ───────
        List<Order> inProgressOrders = orderRepository
                .findByStatus(OrderStatus.IN_PROGRESS);

        for (Order order : inProgressOrders) {
            if (order.getInProgressAt() != null &&
                    order.getInProgressAt().plusHours(24).isBefore(now)) {

                order.setStatus(OrderStatus.SHIPPED);
                order.setShippedAt(now);
                orderRepository.save(order);
                log.info("Order {} → SHIPPED", order.getId());
            }
        }

        // ── SHIPPED → OUT_FOR_DELIVERY  (after 12 hours of SHIPPED) ──────
        List<Order> shippedOrders = orderRepository
                .findByStatus(OrderStatus.SHIPPED);

        for (Order order : shippedOrders) {
            if (order.getShippedAt() != null &&
                    order.getShippedAt().plusHours(12).isBefore(now)) {

                order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
                order.setOutForDeliveryAt(now);
                orderRepository.save(order);
                log.info("Order {} → OUT_FOR_DELIVERY", order.getId());
            }
        }

        // ── OUT_FOR_DELIVERY → DELIVERED  (after 12 hours) ───────────────
        List<Order> outForDeliveryOrders = orderRepository
                .findByStatus(OrderStatus.OUT_FOR_DELIVERY);

        for (Order order : outForDeliveryOrders) {
            if (order.getOutForDeliveryAt() != null &&
                    order.getOutForDeliveryAt().plusHours(12).isBefore(now)) {

                order.setStatus(OrderStatus.DELIVERED);
                order.setDeliveredAt(now);
                orderRepository.save(order);
                log.info("Order {} → DELIVERED", order.getId());
            }
        }
    }
}