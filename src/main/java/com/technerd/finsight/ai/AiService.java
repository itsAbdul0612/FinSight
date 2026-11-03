package com.technerd.finsight.ai;

import com.technerd.finsight.ai.contextbuilder.FinanceContextBuilderDto;
import com.technerd.finsight.ai.contextbuilder.FinanceContextBuilderService;
import com.technerd.finsight.ai.tools.FinsightTools;
import com.technerd.finsight.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final FinsightTools finsightTools;

    private final FinanceContextBuilderService financeContextBuilderService;

    public String chat(String message) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        FinanceContextBuilderDto financeContext = financeContextBuilderService.createFinanceContext(userId);

        String context = financeContext
                .getAllBudget()
                .toString()
                .concat("\n" + "Last Ten Transactions of the user:" + "\n" +
                        financeContext
                                .getLastTenTransactions()
                                .toString()
                                .concat("\n" + "Monthly summary in rupees:" + "\n" +
                                        financeContext
                                                .getMonthlySummary()
                                                .toString()
                                )
                );

        return chatClient
                .prompt()
                .system("""
                        You are Finsight, an intelligent personal finance assistant.
                        
                        Your responsibility is to help users understand their
                        finances, analyze expenses, monitor budgets and
                        make informed financial decisions.
                        
                        Use the financial context provided in the user's message
                        when answering.
                        
                        Never fabricate financial data.
                        """)
                .user(u -> u.text("""
                                Here is my finance context that starts with all my budgets: 
                                
                                {context}
                                
                                My question is: 
                                
                                {message}
                                """)
                        .param("context", context)
                        .param("message", message))
                .tools(finsightTools)
                .call()
                .content();
    }

}
