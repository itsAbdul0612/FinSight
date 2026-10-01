package com.technerd.finsight.systemevent.listner;

import com.technerd.finsight.systemevent.event.UserRegisteredEvent;
import com.technerd.finsight.notification.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class WelcomeEmailListener {

    private final MailService mailService;

    @Async
    @EventListener
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        mailService.sendWelcomeEmail(event.email(), event.name());
        log.info("Received registration event for {}",
                event.email());
    }

}
