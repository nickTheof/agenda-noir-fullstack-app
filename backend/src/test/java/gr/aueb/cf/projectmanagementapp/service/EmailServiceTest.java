package gr.aueb.cf.projectmanagementapp.service;

import gr.aueb.cf.projectmanagementapp.core.exceptions.AppServerException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private final String testEmail = "test@example.com";
    private final String testToken = "test-token-123";

    @Test
    void testSendVerificationEmailShouldSendEmailSuccessfully() throws AppServerException {
        emailService.sendVerificationEmail(testEmail, testToken);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendVerificationEmailShouldSetCorrectProperties() throws AppServerException {
        SimpleMailMessage capturedMessage = new SimpleMailMessage();
        doAnswer(invocation -> {
            SimpleMailMessage msg = invocation.getArgument(0);
            assertNotNull(msg.getTo());
            capturedMessage.setTo(msg.getTo());
            capturedMessage.setSubject(msg.getSubject());
            capturedMessage.setText(msg.getText());
            return null;
        }).when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendVerificationEmail(testEmail, testToken);

        assertEquals("Verification Email", capturedMessage.getSubject());
        assertNotNull(capturedMessage.getText());
        assertTrue(capturedMessage.getText().contains(testToken));
    }

    @Test
    void testSendVerificationEmailWhenMailExceptionShouldThrowAppServerException() {
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(AppServerException.class, () ->
                emailService.sendVerificationEmail(testEmail, testToken)
        );
    }

    @Test
    void testSendPasswordResetEmailShouldSendEmailSuccessfully() throws AppServerException {
        emailService.sendPasswordResetEmail(testEmail, testToken);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendPasswordResetEmailShouldSetCorrectProperties() throws AppServerException {
        SimpleMailMessage capturedMessage = new SimpleMailMessage();
        doAnswer(invocation -> {
            SimpleMailMessage msg = invocation.getArgument(0);
            assertNotNull(msg.getTo());
            capturedMessage.setTo(msg.getTo());
            capturedMessage.setSubject(msg.getSubject());
            capturedMessage.setText(msg.getText());
            return null;
        }).when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendPasswordResetEmail(testEmail, testToken);

        assertEquals("Password Reset Request", capturedMessage.getSubject());
        assertNotNull(capturedMessage.getText());
        assertTrue(capturedMessage.getText().contains(testToken));
    }

    @Test
    void testSendPasswordResetEmailWhenMailExceptionShouldThrowAppServerException() {
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(AppServerException.class, () ->
                emailService.sendPasswordResetEmail(testEmail, testToken)
        );
    }

}