package com.technerd.finsight.systemevent.listner;

import com.technerd.finsight.notification.MailService;
import com.technerd.finsight.systemevent.event.BudgetBreachEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Slf4j
@Component
public class BudgetBreachListener {

    private final MailService mailService;

    @Async
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleBudgetBreachEvent(BudgetBreachEvent event) {
        mailService.sendBudgetBreachAlertEmail(
                event.email(),
                event.name(),
                event.category()
        );
        log.info("Sent budget breach alert for {} to {}", event.category(), event.email());
    }
}
