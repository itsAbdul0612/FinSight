package com.technerd.finsight.notification;

import com.technerd.finsight.analytics.AnalyticsService;
import com.technerd.finsight.analytics.dto.CategorySpendDto;
import com.technerd.finsight.analytics.dto.SixMonthsTrendDto;
import com.technerd.finsight.analytics.dto.TopExpenseOfAMonth;
import com.technerd.finsight.user.User;
import com.technerd.finsight.user.UserRepository;
import jakarta.mail.Message;
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
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static com.technerd.finsight.core.transaction.enums.TransactionType.EXPENSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MailTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private AnalyticsService analyticsService;

    @Mock
    private UserRepository userRepository;

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
    @DisplayName("Should send HTML 80% spent email with correct recipient, subject, and content")
    void shouldSendHtml80PercentSpentEmailSuccessfully() throws MessagingException, IOException {
        String recipient = "itsabdul0612@gmail.com";
        String category = "Entertainment";
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailService.sendHTML80PercentSpentEmail(recipient, category);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());

        MimeMessage sentMessage = captor.getValue();
        assertThat(sentMessage).isNotNull();
        assertThat(sentMessage.getRecipients(Message.RecipientType.TO)).hasSize(1);
        assertThat(sentMessage.getRecipients(Message.RecipientType.TO)[0].toString()).isEqualTo(recipient);
        assertThat(sentMessage.getSubject()).isEqualTo("80% budget exhausted alert.");
        assertThat(sentMessage.getContent().toString())
                .contains("You're approaching your budget limit")
                .contains(category)
                .contains("80%%")
                .contains("20%% of your budget remaining")
                .contains("Finsight");
    }

    @Test
    @DisplayName("Should propagate exception when mailSender fails to send HTML 80% spent email")
    void shouldPropagateExceptionWhenSendingHtml80PercentSpentEmailFails() {
        String recipient = "itsabdul0612@gmail.com";
        String category = "Entertainment";
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Failed to deliver email"))
                .when(mailSender).send(mimeMessage);

        assertThatThrownBy(() -> mailService.sendHTML80PercentSpentEmail(recipient, category))
                .isInstanceOf(MailSendException.class)
                .hasMessageContaining("Failed to deliver email");
    }

    @Test
    @DisplayName("Should send monthly finance report email to all active users with analytics data")
    void shouldSendMonthlyReportSuccessfully() {
        String reportMonth = YearMonth.now(ZoneId.of("Asia/Kolkata")).minusMonths(1).toString();

        User user = User.builder()
                .id(1L)
                .name("Abdul")
                .email("itsabdul0612@gmail.com")
                .build();

        BigDecimal monthlySummary = BigDecimal.valueOf(15000);
        List<SixMonthsTrendDto> sixMonthsTrend = List.of(new SixMonthsTrendDto("2026-09", BigDecimal.valueOf(12000)));
        List<CategorySpendDto> categorySpend = List.of(new CategorySpendDto("Groceries", BigDecimal.valueOf(5000)));
        TopExpenseOfAMonth topExpense = mock(TopExpenseOfAMonth.class);
        when(topExpense.getCategoryName()).thenReturn("Rent");
        when(topExpense.getTotalSpent()).thenReturn(BigDecimal.valueOf(10000));
        List<TopExpenseOfAMonth> topExpenses = List.of(topExpense);

        when(userRepository.finaAllActiveUsers()).thenReturn(List.of(user));
        when(analyticsService.monthlySummary(user.getId(), EXPENSE, reportMonth)).thenReturn(monthlySummary);
        when(analyticsService.sixMonthTrend(user.getId(), EXPENSE)).thenReturn(sixMonthsTrend);
        when(analyticsService.totalMonthlySpendingOfACategory(user.getId(), EXPENSE, reportMonth)).thenReturn(categorySpend);
        when(analyticsService.topExpensesOfAMonths(user.getId(), reportMonth, EXPENSE, 5)).thenReturn(topExpenses);

        mailService.sendMonthlyReport();

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertThat(sentMessage).isNotNull();
        assertThat(sentMessage.getTo()).containsExactly("itsabdul0612@gmail.com");
        assertThat(sentMessage.getSubject()).isEqualTo("Monthly Finance Report");
        assertThat(sentMessage.getText())
                .contains("Hi Abdul,")
                .contains(reportMonth)
                .contains("Groceries: ₹5000")
                .contains("FinSight");
    }

    @Test
    @DisplayName("Should not send any email when there are no active users")
    void shouldNotSendEmailWhenNoActiveUsers() {
        when(userRepository.finaAllActiveUsers()).thenReturn(Collections.emptyList());

        mailService.sendMonthlyReport();

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
        verifyNoInteractions(analyticsService);
    }
}
