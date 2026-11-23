package com.technerd.finsight;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.profiles.active=test")
class FinsightApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("Context Load Test");
    }

}
