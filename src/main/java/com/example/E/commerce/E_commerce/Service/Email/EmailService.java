package com.example.E.commerce.E_commerce.Service.Email;

import com.example.E.commerce.E_commerce.Entity.Order.Order;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EmailService
{
    private final JavaMailSender javaMailSender;
    
    public void sendResetPasswordEmail(@Email String email, String resetLink)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("deepashrathibhl@gmail.com");
        message.setTo(email);
        message.setSubject("Reset Email Password ");
        message.setText
                (
                        "Click the link below to reset your password:\n\n"
                                + resetLink +
                                "\n\nThis link will expire in 15 minutes."
                );
        javaMailSender.send(message);
    }
    public void confirmedOrderSendMail(@Email String email, Order order)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("deepashrathibhl@gmail.com");
        message.setTo(email);
        message.setSubject("Order Confirmation");
        String products = order.getOrderItems().stream().map
                (orderItem -> orderItem.getProductName()+
                        "(Qty : "+orderItem.getQuantity()+")").collect(Collectors.joining("\n"));
        String body =
                "Hello " + order.getUser().getUsername() + ",\n\n" +
                        "Your order has been successfully placed.\n\n" +
                        "Order ID: " + order.getId() + "\n" +
                        "Product: \n" + products + "\n\n" +
                        "Track your order:\n" +
                        "http://localhost:3000/orders/" + order.getId() + "\n\n" +
                        "Thank you for shopping with us!";

        message.setText(body);
        javaMailSender.send(message);
    }
}
