package com.technerd.finsight.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MailTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailService mailService;

    @Test
    @DisplayName("Should send welcome email with correct recipient, subject, and body")
    void shouldSendWelcomeEmailSuccessfully() {
        String recipient = "itsabdul0612@gmail.com";
        String name = "Abdul";

        mailService.sendWelcomeEmail(recipient, name);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message).isNotNull();
        assertThat(message.getTo()).containsExactly(recipient);
        assertThat(message.getSubject()).isEqualTo("Welcome to FinSight!");
        assertThat(message.getText())
                .contains("Hello, " + name + "!")
                .contains("FinSight");
    }

    @Test
    @DisplayName("Should send budget breach alert email with correct details")
    void shouldSendBudgetBreachAlertEmailSuccessfully() {
        String recipient = "itsabdul0612@gmail.com";
        String name = "Abdul";
        String category = "Dining";

        mailService.sendBudgetBreachAlertEmail(recipient, name, category);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message).isNotNull();
        assertThat(message.getTo()).containsExactly(recipient);
        assertThat(message.getSubject()).isEqualTo("Budget Breach Alert!");
        assertThat(message.getText())
                .contains("Alert! " + name)
                .contains(category)
                .contains("You have spent all of your " + category + " amount this month.");
    }

    @Test
    @DisplayName("Should send 80% spent alert email with correct details")
    void shouldSend80PercentSpentEmailSuccessfully() {
        String recipient = "itsabdul0612@gmail.com";
        String name = "Abdul";
        String category = "Groceries";

        mailService.send80PercentSpentEmail(recipient, name, category);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message).isNotNull();
        assertThat(message.getTo()).containsExactly(recipient);
        assertThat(message.getSubject()).isEqualTo("80% Spent amount of your " + category + " alert.");
        assertThat(message.getText())
                .contains("Hey " + name)
                .contains("80% of your allocated monthly " + category);
    }

    @Test
    @DisplayName("Should send HTML 80% spent email with correct recipient and subject")
    void shouldSendHtml80PercentSpentEmailSuccessfully() throws MessagingException {
        String recipient = "itsabdul0612@gmail.com";
        String category = "Entertainment";
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        assertThatCode(() -> mailService.sendHTML80PercentSpentEmail(recipient, category))
                .doesNotThrowAnyException();

        verify(mailSender).send(mimeMessage);
    }
}
