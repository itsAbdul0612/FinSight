package com.technerd.finsight.appconfig;

import com.technerd.finsight.core.budget.dto.BudgetResponse;
import com.technerd.finsight.core.category.dto.CategoryResponseDto;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory,
            ObjectMapper objectMapper) {

        // GenericJacksonJsonRedisSerializer serializer is configed here
        // -----------------------------------------------------------------------------------------------
        BasicPolymorphicTypeValidator validator = BasicPolymorphicTypeValidator
                .builder()
                .allowIfSubType("java.util.")
                .allowIfSubType("com.technerd.finsight.")
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
//                .enableTimeToIdle()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(serializer));

        // Custom configs for different caches:
        // -----------------------------------------------------------------------------------------------
        JavaType budgetListType = objectMapper
                .getTypeFactory()
                .constructCollectionType(List.class, BudgetResponse.class);

        var BudgetSerializer = new JacksonJsonRedisSerializer<List<BudgetResponse>>(budgetListType);

        RedisCacheConfiguration budgetStatusConfig = defaultConfig
                .entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair.fromSerializer(BudgetSerializer));
        // ----------------------------------------------------------------------------------------------

        // -----------------------------------------------------------------------------------------------
        JavaType categoryListType = objectMapper
                .getTypeFactory()
                .constructCollectionType(List.class, CategoryResponseDto.class);

        var CategorySerializer = new JacksonJsonRedisSerializer<List<CategoryResponseDto>>(categoryListType);

        RedisCacheConfiguration allCategories = defaultConfig
                .entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair.fromSerializer(CategorySerializer));
        // -----------------------------------------------------------------------------------------------

        return RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(
                        Map.of(
                                "all-budget", budgetStatusConfig,
                                "all-categories", allCategories)
                )
                .build();
    }

}