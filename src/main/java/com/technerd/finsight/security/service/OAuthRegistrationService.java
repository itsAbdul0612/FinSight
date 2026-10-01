package com.technerd.finsight.security.service;

import com.technerd.finsight.category.exampleseed.ExampleCategory;
import com.technerd.finsight.category.exampleseed.ExampleCategoryCreator;
import com.technerd.finsight.systemevent.event.UserRegisteredEvent;
import com.technerd.finsight.user.User;
import com.technerd.finsight.user.enums.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OAuthRegistrationService {


    private final UserSecurityService userSecurityService;
    private final ExampleCategoryCreator categoryCreator;
    private final ExampleCategory exampleCategory;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public User registerOAuthUser(
            String email,
            String name
    ) {

        User user = User.builder()
                .email(email)
                .name(name)
                .role(Role.USER)
                .isActive(true)
                .build();

        User savedUser = userSecurityService.save(user);

        categoryCreator.createCategory(exampleCategory, savedUser);

        eventPublisher.publishEvent(
                new UserRegisteredEvent(
                        savedUser.getEmail(),
                        savedUser.getName()
                )
        );

        return savedUser;
    }
}
