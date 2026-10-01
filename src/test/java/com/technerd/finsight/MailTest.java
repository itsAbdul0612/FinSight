package com.technerd.finsight;

import com.technerd.finsight.notification.MailService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;


@SpringBootTest
public class MailTest {

    @Autowired
    private MailService mailService;

//    @Disabled
//    @Test
//    void sendMail() {
//        mailService.sendMail("itsabdul0612@gmail.com",
//                "Simple text mail test 1.",
//                "Test 1: " + LocalDateTime.now().toString());
//    }

}
