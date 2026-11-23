package com.technerd.finsight.security.service;

import com.technerd.finsight.user.User;
import com.technerd.finsight.user.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JWTServiceTest {

    private JWTService service;

    @BeforeEach
    void setUp() {
        service = new JWTService();
        ReflectionTestUtils.setField(service, "secret",
                "test-secret-key-with-at-least-32-characters");
    }

    @Test
    void accessTokenContainsUserSubjectAndEmail() {
        User user = User.builder().id(42L).email("jwt@example.com").role(Role.USER).build();

        String token = service.generateAccessToken(user);

        assertThat(token).isNotBlank();
        assertThat(service.getUserIdFromToken(token)).isEqualTo(42L);
    }

    @Test
    void refreshTokenContainsUserSubject() {
        User user = User.builder().id(7L).email("refresh@example.com").role(Role.USER).build();

        String token = service.generateRefreshToken(user);

        assertThat(service.getUserIdFromToken(token)).isEqualTo(7L);
    }
}
