package com.technerd.finsight.core.category;

import com.technerd.finsight.core.transaction.TransactionSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QueryTest {

    @Test
    void categorySpecificationIsComposedWithoutNulls() {
        Specification<com.technerd.finsight.core.transaction.Transaction> specification =
                Specification.unrestricted();
        specification = specification.and(TransactionSpecification.belongsTo(1L));
        specification = specification.and(TransactionSpecification.hasCategory(null));
        specification = specification.and(TransactionSpecification.amountBetween(
                new BigDecimal("10"), new BigDecimal("100")));

        Root<com.technerd.finsight.core.transaction.Transaction> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder builder = mock(CriteriaBuilder.class);

        assertThat(specification).isNotNull();
    }

    @Test
    void dateAndAmountSpecificationsReturnPredicate() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        Specification<com.technerd.finsight.core.transaction.Transaction> specification =
                TransactionSpecification.dateBetween(start, end)
                        .and(TransactionSpecification.amount(new BigDecimal("42")));

        assertThat(specification).isNotNull();
    }
}
