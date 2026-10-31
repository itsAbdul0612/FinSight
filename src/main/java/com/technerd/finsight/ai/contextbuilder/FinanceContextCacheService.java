package com.technerd.finsight.ai.contextbuilder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FinanceContextCacheService {

    @CacheEvict(cacheNames = "finance_context", key = "#userId")
    public void invalidateFinanceContextCache(Long userId){
        log.info("Invalidated finance context for user: {}", userId);
    }
}
