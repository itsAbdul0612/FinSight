package com.technerd.finsight.ai.tools;

import com.technerd.finsight.core.category.CategoryService;
import com.technerd.finsight.core.category.dto.CategoryResponseDto;
import com.technerd.finsight.core.transaction.Transaction;
import com.technerd.finsight.core.transaction.TransactionService;
import com.technerd.finsight.core.transaction.dto.TransactionDto;
import com.technerd.finsight.core.transaction.dto.TransactionResponse;
import com.technerd.finsight.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class FinsightTools {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;


    /*
     * Two problems were here:
     *
     * 1. The AI couldn't differentiate between Food and food, so I made the query finding category from Db case-insensitive.
     *
     * 2. AI couldn't handle typos, so I gave it the cached list of category names so that it can figure out Foid is just Food.
     * And now, user don't even have to mention the name of category, generally, AI will figure it out on itself
     *
     * */

    // -----------------------------------------------------------------------------------------------
    @Tool(
            name = "create_transaction",
            description = """
                               Creates a transaction of a category.
                    
                    Rules:
                    1. If the user provides a category name, use it directly.
                    2. If no category is specified, MUST call get-all-categories
                       to retrieve available categories and select the most appropriate one.
                    3. Never ask the user for a category before fetching available categories.
                    4. Never invent categories or expose category IDs.
                    5. Generate a short, informative, one-line transaction description.
                    """
    )
    @Transactional
    public TransactionResponse createTransactionTool(
            @ToolParam(description = """ 
                    Transaction DTO that contains amount spent, description of it and categoryId
                    """) String description,
            @ToolParam(description = "The amount that user has spent.") BigDecimal amount,
            @ToolParam(description = "Case-insensitive Name of the category to which the transaction is belongs to.") String name) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = user.getId();

        CategoryResponseDto category = categoryService.findByNameIgnoreCaseAndUserId(name, userId);

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setCategoryId(category.getId());
        transactionDto.setAmount(amount);
        transactionDto.setDescription(description);

        Transaction transaction = transactionService.createTransaction(transactionDto, user);
        return modelMapper.map(transaction, TransactionResponse.class);
    }
    // -----------------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------------
    @Tool(
            name = "get_all_categories",
            description = """
                    This method gets you a list of all the categories a user has.
                    
                    Rule(s):
                    1. Use this tool when the user don't provide category name while creating a transaction.
                    2. Don't ask the user before you fetch all the categories.
                    3. Don't mention category ID to user in any case.
                    
                    """
    )
    public List<CategoryResponseDto> findAllCategories() {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = user.getId();

        log.info("hit get_all_categories ");
        return categoryService.findAllByUserId(userId);
    }
    // -----------------------------------------------------------------------------------------------

}
