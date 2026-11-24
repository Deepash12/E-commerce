
package com.example.E.commerce.E_commerce.Service.Email;

import com.example.E.commerce.E_commerce.Entity.Order.Order;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final BrevoEmailClient brevoEmailClient;

    public void sendResetPasswordEmail(@Email String email, String resetLink) {

        String subject = "Reset Email Password";

        String body = "Click the link below to reset your password:\n\n"
                + resetLink
                + "\n\nThis link will expire in 15 minutes.";

        brevoEmailClient.sendEmail(email, subject, body);
    }

    public void confirmedOrderSendMail(@Email String email, Order order) {

        String products = order.getOrderItems().stream()
                .map(orderItem -> orderItem.getProductName()
                        + " (Qty: " + orderItem.getQuantity() + ")")
                .collect(Collectors.joining("\n"));

        String body = "Hello " + order.getUser().getUsername() + ",\n\n"
                + "Your order has been successfully placed.\n\n"
                + "Order ID: " + order.getId() + "\n"
                + "Products:\n" + products + "\n\n"
                + "Track your order:\n"
                + "https://e-commerce-frontend-iota-eight.vercel.app/orders/"
                + order.getId() + "\n\n"
                + "Thank you for shopping with us!";

        brevoEmailClient.sendEmail(
                email,
                "Order Confirmation",
                body
        );
    }

    public void sendRegisteredEmail(
            @Email(message = "Invalid Email is Written")
            @NotBlank(message = "Email is Required") String email,
            @NotBlank String username) {

        String body = "Dear " + username + ",\n\n"
                + "Thank you for registering with our platform.\n\n"
                + "Your account has been successfully created. "
                + "You can now log in and start exploring our products, "
                + "add items to your cart, and place orders easily.\n\n"
                + "If you have any questions or need assistance, "
                + "feel free to contact our support team.\n\n"
                + "We're happy to have you with us and hope you enjoy "
                + "your shopping experience.";

        brevoEmailClient.sendEmail(
                email,
                "Welcome to Our Store!",
                body
        );
    }
}
