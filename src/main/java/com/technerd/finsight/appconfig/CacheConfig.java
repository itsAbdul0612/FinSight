package com.technerd.finsight.appconfig;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;


import java.time.Duration;
import java.util.Map;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory) {

        // GenericJacksonJsonRedisSerializer serializer is configed here
        // -----------------------------------------------------------------------------------------------
        BasicPolymorphicTypeValidator validator = BasicPolymorphicTypeValidator
                .builder()
//              .allowIfSubType(TransactionResponse.class)
                .allowIfSubType(Object.class)
                .build();

        GenericJacksonJsonRedisSerializer serializer = GenericJacksonJsonRedisSerializer
                .builder()
                .enableDefaultTyping(validator)
                .build();
        // -----------------------------------------------------------------------------------------------

        // Redis config gets built here.
        // -----------------------------------------------------------------------------------------------
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .prefixCacheNameWith("redis-cache-")
                .entryTtl(Duration.ofMinutes(10))
                .enableTimeToIdle()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(serializer));

        RedisCacheConfiguration budgetStatusConfig = defaultConfig
                .enableTimeToIdle()
                .entryTtl(Duration.ofMinutes(30));

        RedisCacheConfiguration financeContext = defaultConfig
                .enableTimeToIdle()
                .entryTtl(Duration.ofMinutes(30));
        // -----------------------------------------------------------------------------------------------

        return RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(
                        Map.of(
                        "all_budget", budgetStatusConfig,
                        "finance_context", financeContext)
                )
                .withCacheConfiguration("all_budget", budgetStatusConfig)
                .build();
    }

}
