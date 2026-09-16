package com.technerd.finsight.transaction.enums;

import com.technerd.finsight.security.entity.User;
import com.technerd.finsight.security.service.JWTService;
import com.technerd.finsight.security.service.UserService;
import com.technerd.finsight.transaction.Transaction;
import com.technerd.finsight.transaction.TransactionDto;
import com.technerd.finsight.transaction.TransactionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RequestMapping("/transaction")
@RequiredArgsConstructor
@RestController
public class TransactionController {

    private final TransactionService transactionService;
    private final JWTService  jwtService;
    private final UserService  userService;

    @PostMapping("/create")
    public ResponseEntity<Transaction> createTransaction(
            @Valid @RequestBody TransactionDto transactionDto,
            HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null){
            return ResponseEntity.badRequest().build();
        }

        String token = Arrays.stream(cookies)
                .filter(cookie -> "REFRESH_TOKEN".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Invalid cookie name"));

        Long userIdFromToken = jwtService.getUserIdFromToken(token);
        User user = userService.findById(userIdFromToken);

        Transaction transaction = transactionService.createTransaction(transactionDto, user);

        return ResponseEntity.ok(transaction);
    }
}
