package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) throws AppServerException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verification Email");
        message.setText("To verify your account, use the below token:\n\n"
                + "http://localhost:5173/auth/verify-account?token="  + token
                + "\n\nThis token will expire in 24 hours.");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new AppServerException("EmailServiceException", e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String to, String token) throws AppServerException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, use the below token:\n\n"
                + "http://localhost:5173/auth/reset-password?token="  + token
                + "\n\nThis token will expire in 30 minutes.");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new AppServerException("EmailServiceException", e.getMessage());
        }
    }
}