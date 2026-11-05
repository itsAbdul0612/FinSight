package com.technerd.finsight.core.monthlyinsight;

import com.technerd.finsight.ai.AiService;
import com.technerd.finsight.user.User;
import com.technerd.finsight.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class MonthlyInsight {

    private final AiService aiService;
    private final UserRepository userRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 1 * *", zone = "Asia/Kolkata")
    public void sendMonthlyInsights(){

        log.info("Initialized generating monthly insights");
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");

        String CURRENT_MONTH = YearMonth.now(zoneId).toString();
        String LAST_MONTH = YearMonth.now(zoneId).minusMonths(1).toString();

        List<User> users = userRepository.finaAllActiveUsers();
        for (User user : users){

        }
    }
}
