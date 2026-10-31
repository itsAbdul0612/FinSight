package com.technerd.finsight.ai.contextbuilder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FinanceContextCacheService {

    @Autowired
    private CacheManager cacheManager;

    public void invalidateFinanceContextCache(Long userId){
        Cache financeContext = cacheManager.getCache("finance_context");
        if (financeContext != null) {
            financeContext.evict(userId);
        log.info("Invalidated finance context for user: {}", userId);
        }
        else  {
            log.info("Cache is null for user: {}", userId);
        }
    }
}
