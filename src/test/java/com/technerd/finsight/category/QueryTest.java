package com.technerd.finsight.category;

import com.technerd.finsight.transaction.TransactionRepository;
import com.technerd.finsight.transaction.TransactionService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class QueryTest {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionService transactionService;

    @Disabled
    @Test
    public void testQuery(){
        Long  userId = 1L;
        List<Category> categoryList = categoryRepository.findAllByUserId(userId);
        for (Category category : categoryList){
            System.out.println(category.getName());
        }
    }

//    @Test
//    public void transactionTest(){
//        Pageable pageable = PageRequest.of(1, 5, Sort.by("id").descending());
//
//        List<TransactionResponse> all = transactionService.getAll(pageable, 1L);
//
//        for (TransactionResponse transactionResponse : all){
//            System.out.println(transactionResponse);
//        }
//    }
}
