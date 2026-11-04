package com.technerd.finsight.ai;

import com.technerd.finsight.analytics.AnalyticsService;
import com.technerd.finsight.core.budget.BudgetService;
import com.technerd.finsight.core.budget.dto.BudgetResponse;
import com.technerd.finsight.core.category.CategoryService;
import com.technerd.finsight.core.category.dto.CategoryResponseDto;
import com.technerd.finsight.core.transaction.Transaction;
import com.technerd.finsight.core.transaction.TransactionService;
import com.technerd.finsight.core.transaction.dto.TransactionDto;
import com.technerd.finsight.core.transaction.dto.TransactionResponse;
import com.technerd.finsight.core.transaction.enums.TransactionType;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class FinsightTools {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final BudgetService budgetService;
    private final AnalyticsService analyticsService;

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
                    Creates a new EXPENSE transaction for the authenticated user.
                    
                    WHEN TO USE:
                    Invoke this tool when the user explicitly requests to record,
                    add, or create an expense transaction.
                    
                    CATEGORY RESOLUTION:
                    1. If the user explicitly provides a category name, use that
                       category name directly.
                    2. If the user does not provide a category name, you MUST first
                       invoke get_all_categories.
                    3. After retrieving the available categories, select the most
                       appropriate category based on the user's expense description.
                    4. If no available category reasonably matches the expense,
                       ask the user to choose a category.
                    5. Never invent categories or ask for a category before checking
                       the available categories when one was not provided.
                    
                    DESCRIPTION:
                    Generate a short, informative, one-line description based
                    on the user's actual expense.
                    Do not invent details about the transaction.
                    
                    CONSTRAINTS:
                    - Never expose category IDs to the user.
                    - Never create a transaction without a valid category.
                    - Never assume an amount the user did not provide.
                    - Do not claim success until the tool confirms creation.
                    """
    )
    @Transactional
    public TransactionResponse createTransactionTool(
            @ToolParam(description = """
                    A short, informative, one-line description of the expense.
                    Derive it from the information explicitly provided by the user.
                    """)
            String description,

            @ToolParam(description = """
                    The monetary amount spent by the user.
                    Must be the actual amount explicitly provided by the user.
                    """)
            BigDecimal amount,

            @ToolParam(description = """
                    The name of the expense category.
                    Use the user's explicitly provided category name, or select
                    an appropriate name from get_all_categories when the user
                    has not specified one.
                    Category matching is case-insensitive.
                    Never provide a category ID.
                    """)
            String name) {

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
                    Retrieves all expense categories available to the authenticated user.
                    
                    WHEN TO USE:
                    1. When creating a transaction and the user has not specified
                       a category name.
                    2. When you need to identify which categories are available
                       to the user.
                    3. When you need to resolve an expense into an existing category.
                    
                    RULES:
                    - Use the returned categories as the only source of valid categories.
                    - Never invent categories.
                    - Never expose category IDs to the user.
                    - Do not ask the user to provide a category before retrieving
                      the available categories when creating a transaction.
                    - Do not call this tool repeatedly if the required category
                      information has already been retrieved in the current task.
                    """
    )
    public List<CategoryResponseDto> findAllCategories() {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = user.getId();

        return categoryService.findAllByUserId(userId);
    }
    // -----------------------------------------------------------------------------------------------

    @Tool(
            name = "get_monthly_budget",
            description = """
                    Retrieves the authenticated user's budgets for a specified month.
                    
                    WHEN TO USE:
                    Invoke this tool when the user asks about their budgets,
                    budget allocations, remaining budget, or budget utilization.
                    
                    DATE HANDLING:
                    - If the user specifies a month, retrieve that month.
                    - If the user does not specify a month, use the current month.
                    - Use get_current_time to determine the current month when needed.
                    - The month must be provided in YYYY-MM format.
                    
                    RULES:
                    - Use the returned data as the source of truth.
                    - Never invent budget allocations or spending figures.
                    - Never expose budget IDs or other internal identifiers.
                    - Distinguish allocated amounts from actual spending.
                    - Do not assume a category has a budget if it is absent
                      from the returned results.
                    """
    )
    public List<BudgetResponse> currentMonthBudget(
            @ToolParam(description = """
                    The target month in YYYY-MM format.
                    If the user specifies a month, use that month.
                    Otherwise, use the current month obtained through get_current_time.
                    """) String yearMonth) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = user.getId();

        return budgetService.findAllByUserIdAndMonth(userId, yearMonth);
    }

    // -----------------------------------------------------------------------------------------------
    @Tool(
            name = "get_monthly_summary",
            description = """
                    Retrieves the total EXPENSE expenditure of the authenticated user
                    for a specified month.
                    
                    WHEN TO USE:
                    Invoke this tool when the user asks about:
                    - Total monthly expenditure.
                    - How much they spent during a particular month.
                    - Their overall monthly spending.
                    - A monthly spending summary.
                    
                    DATE HANDLING:
                    - If the user specifies a month, retrieve that month.
                    - Otherwise, use the current month.
                    - Use get_current_time to determine the current month when needed.
                    - The month must be provided in YYYY-MM format.
                    
                    RULES:
                    - The returned value represents aggregate expense expenditure.
                    - Never interpret this result as an individual transaction.
                    - Never invent expenditure figures.
                    - You may invoke other available tools when their results
                      are necessary to answer the user's question.
                    - Do not invoke unrelated tools merely to provide additional data.
                    """
    )
    public BigDecimal monthlySummary(
            @ToolParam(description = """
                    The target month in YYYY-MM format.
                    Use the month specified by the user, or the current month
                    obtained through get_current_time if no month was specified.
                    """)
            String yearMonth) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = user.getId();

        return analyticsService.monthlySummary(userId, TransactionType.EXPENSE, yearMonth);
    }
    // -----------------------------------------------------------------------------------------------

    @Tool(
            name = "get_current_time",
            description = """
                    Retrieves the actual current date and time in the user's timezone.
                    
                    WHEN TO USE:
                    Invoke this tool when you need to:
                    - Determine the current month or year.
                    - Interpret relative dates such as today, yesterday, or last month.
                    - Resolve date-dependent financial queries.
                    - Determine the current date and time in the user's timezone.
                    
                    RULES:
                    - Use the returned date and time instead of guessing.
                    - Respect the timezone included in the result.
                    - Do not call this tool repeatedly within the same task
                      if its result is already available and still relevant.
                    """
    )
    public String currentTime() {
        return LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toString();
    }
    // -----------------------------------------------------------------------------------------------

}
