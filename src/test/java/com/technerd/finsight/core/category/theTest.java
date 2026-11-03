package com.technerd.finsight.core.category;

import com.technerd.finsight.ai.contextbuilder.FinanceContextBuilderDto;
import com.technerd.finsight.ai.contextbuilder.FinanceContextBuilderService;
import com.technerd.finsight.core.category.dto.CategoryResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class theTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FinanceContextBuilderService financeContextBuilderService;

    @Test
    void Test(){
//
//        List<CategoryResponseDto> allByUserId = categoryService.findAllByUserId(1L);
//        for (CategoryResponseDto category : allByUserId) {
//            System.out.println(category.getName());

        FinanceContextBuilderDto financeContext = financeContextBuilderService.createFinanceContext(1L);
        String context = financeContext
                .getAllBudget()
                .toString()
                .concat("\n"+"Last Ten Transactions of the user:"+"\n"+
                        financeContext
                                .getLastTenTransactions()
                                .toString()
                                .concat("\n"+"Monthly summary in rupees:"+"\n"+
                                        financeContext
                                                .getMonthlySummary()
                                                .toString()
                                )
                );

        System.out.println(context);
    }

}
