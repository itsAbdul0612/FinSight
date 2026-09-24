package com.technerd.finsight.transaction;

import com.technerd.finsight.transaction.enums.TransactionType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionSpecification {

    public static Specification<Transaction> belongsTo(Long userId){
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("user").get("id"), userId);
        };
    }

    // Transaction Type
    public static Specification<Transaction> hasType(TransactionType type) {
        return (root, criteriaQuery, criteriaBuilder) ->{
            if (type == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("transactionType"), type);
        };
    }

    // Category
    public static Specification<Transaction> hasCategory(Long categoryId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }

    // Specific Amount
    public static Specification<Transaction> amount(BigDecimal amount) {
        return (root, query, criteriaBuilder) ->
        {
            if (amount == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("amountBetween"), amount);
        };
    }

    // Min and Max Amount
    public static Specification<Transaction> amountBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        return (root, query, criteriaBuilder) -> {
            if (minAmount == null && maxAmount == null) {
                return criteriaBuilder.conjunction();
            }
            if (minAmount != null && maxAmount != null) {
                return criteriaBuilder.between(root.get("amountBetween"), minAmount, maxAmount);
            }
            if (minAmount != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("amountBetween"), minAmount);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("amountBetween"), maxAmount);
        };
    }

    // Specific date.
    public static Specification<Transaction> date(LocalDateTime date) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }
            LocalDateTime atStartOfDay = date.toLocalDate().atStartOfDay();
            LocalDateTime atStartOfNextDay = date.toLocalDate().plusDays(1).atStartOfDay();
            return criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), atStartOfDay),
                    criteriaBuilder.lessThan(root.get("transactionDate"), atStartOfNextDay)
            );
        };
    }

    // Start and End Date
    public static Specification<Transaction> dateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (startDate == null &&  endDate == null) {
                return criteriaBuilder.conjunction();
            }
            if (startDate != null &&   endDate != null) {
                return criteriaBuilder.between(root.get("transactionDate"), startDate, endDate);
            }
            if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), startDate);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"), endDate);
        };
    }


}


//public class StudentSpecification
//{
//    //columnEqual() can be any name you want
//    public static Specification<Student> columnEqual(List<FilterDTO> filterDTOList)
//    {
//        return new Specification<Student>()
//        {
//            private static final long serialVersionUID = 1L;
//
//            @Override
//            public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder)
//            {
//                List<Predicate> predicates = new ArrayList<>();
//                filterDTOList.forEach(filter ->
//                {
//                    //I don't like lambas, too unreadable for me
//                    Predicate predicate = criteriaBuilder.equal(root.get(filter.getColumnName()),filter.getColumnValue());
//                    predicates.add(predicate);
//                }
//
//                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
//            }
//        } //columnEqual() function ends
//    }}