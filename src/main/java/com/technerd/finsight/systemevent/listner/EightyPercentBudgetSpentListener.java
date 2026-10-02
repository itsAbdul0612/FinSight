package com.technerd.finsight.systemevent.listner;

import com.technerd.finsight.notification.MailService;
import com.technerd.finsight.systemevent.event.EightyPercentBudgetSpentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
@Slf4j
public class EightyPercentBudgetSpentListener {

    private final MailService mailService;

    @Async
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle80PercentBudgetSpent(EightyPercentBudgetSpentEvent event){
        mailService.send80PercentSpentEmail(
                event.email(),
                event.name(),
                event.category()
        );
       log.info("80% spent email has been sent to {}", event.email());
    }
}
