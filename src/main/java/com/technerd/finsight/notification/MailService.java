package com.technerd.finsight.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender mailSender;

    public void sendWelcomeEmail(String to, String name) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Welcome to FinSight!");
        message.setText("Hello, " + name + "! We are so exited to have you on FinSight, An AI powered personal finance dashboard.");
        mailSender.send(message);
    }

    public void sendBudgetBreachAlertEmail(String to, String name, String category) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Budget Breach Alert!");
        message.setText("Alert! " + name + ". You have spent all of your " + category + " amount this month. Check your dashboard.");
        mailSender.send(message);
    }

    public void send80PercentSpentEmail(String to, String name, String category) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("80% Spent amount of your " + category + " alert.");
        message.setText("Hey " + name + ",you have spent 80% of your allocated monthly " + category + "! Check your dashboard.");
        mailSender.send(message);
    }


    public void sendHTML80PercentSpentEmail(String to, String categoryFromUser) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        helper.setTo(to);
        helper.setSubject("80% budget exhausted alert.");

        String category = categoryFromUser;
        String html = """
        <!DOCTYPE html>
        <html>
        <body style="margin:0; padding:0; background-color:#f1f5f9; font-family:Arial,Helvetica,sans-serif;">

            <table width="100%%" cellpadding="0" cellspacing="0" border="0"
                   style="background-color:#f1f5f9; padding:30px 10px;">
                <tr>
                    <td align="center">

                        <table width="100%%" cellpadding="0" cellspacing="0" border="0"
                               style="max-width:550px; background-color:#ffffff; border-radius:12px;">

                            <!-- Header -->
                            <tr>
                                <td style="padding:25px 30px; border-bottom:1px solid #e2e8f0;">
                                    <h2 style="margin:0; color:#2563eb; font-size:22px;">
                                        Finsight
                                    </h2>
                                </td>
                            </tr>

                            <!-- Content -->
                            <tr>
                                <td style="padding:30px;">

                                    <p style="margin:0 0 12px; color:#64748b; font-size:12px; font-weight:bold; letter-spacing:1px;">
                                        BUDGET NOTIFICATION
                                    </p>

                                    <h1 style="margin:0 0 15px; color:#0f172a; font-size:24px; line-height:1.4;">
                                        You're approaching your budget limit
                                    </h1>

                                    <p style="margin:0 0 25px; color:#475569; font-size:15px; line-height:1.7;">
                                        You've spent 80%% of your monthly
                                        <strong style="color:#0f172a;">""" + category + """
                                        </strong> budget. Keep an eye on your spending.
                                    </p>

                                    <!-- Progress Card -->
                                    <table width="100%%" cellpadding="0" cellspacing="0" border="0"
                                           style="background-color:#f8fafc; border-radius:10px;">
                                        <tr>
                                            <td style="padding:20px;">

                                                <table width="100%%" cellpadding="0" cellspacing="0" border="0">
                                                    <tr>
                                                        <td style="color:#475569; font-size:14px;">
                                                            Budget used
                                                        </td>
                                                        <td align="right" style="color:#2563eb; font-size:16px; font-weight:bold;">
                                                            80%%
                                                        </td>
                                                    </tr>
                                                </table>

                                                <table width="100%%" cellpadding="0" cellspacing="0" border="0"
                                                       style="margin-top:15px; background-color:#e2e8f0; border-radius:10px;">
                                                    <tr>
                                                        <td width="80%%" height="8"
                                                            style="background-color:#3b82f6; border-radius:10px; font-size:0; line-height:8px;">
                                                            &nbsp;
                                                        </td>
                                                        <td width="20%%" height="8"
                                                            style="font-size:0; line-height:8px;">
                                                            &nbsp;
                                                        </td>
                                                    </tr>
                                                </table>

                                                <p style="margin:12px 0 0; color:#64748b; font-size:12px;">
                                                    20%% of your budget remaining
                                                </p>

                                            </td>
                                        </tr>
                                    </table>

                                    <p style="margin:25px 0 0; color:#475569; font-size:14px; line-height:1.7;">
                                        Consider reviewing your recent expenses to stay within your budget.
                                    </p>

                                </td>
                            </tr>

                            <!-- Footer -->
                            <tr>
                                <td style="padding:20px 30px; background-color:#f8fafc; border-top:1px solid #e2e8f0; text-align:center;">
                                    <p style="margin:0; color:#64748b; font-size:13px; font-weight:bold;">
                                        Finsight
                                    </p>
                                    <p style="margin:6px 0 0; color:#94a3b8; font-size:12px;">
                                        Making every rupee count.
                                    </p>
                                </td>
                            </tr>

                        </table>

                    </td>
                </tr>
            </table>

        </body>
        </html>
        """;

        helper.setText(html, true);
        mailSender.send(message);
    }
}
