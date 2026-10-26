package com.technerd.finsight;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class FinsightApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("Context Load Test");
    }

    @Test
    @AfterAll
    @DisplayName("Set up")
    static void setUp(){
        System.out.println("Set Up");
    }

    @Test
    @DisplayName("Test 1")
    void testOne(){
        System.out.println("Test 1");
    }

    @Test
    @DisplayName("Test 2")
    void testTwo() {
        System.out.println("Test 2");
    }

    @Test
    @DisplayName("Test 3")
    void testThree() {
        System.out.println("Test 3");
    }

    @Test
    @DisplayName("Test 4")
    void testFour() {
        System.out.println("Test 4");
    }


    int divideTwoNum(int a, int b) {
        return a/b;
    }

}
