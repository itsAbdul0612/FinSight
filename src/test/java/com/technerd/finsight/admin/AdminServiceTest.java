package com.technerd.finsight.admin;

import com.technerd.finsight.user.User;
import com.technerd.finsight.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private UserRepository userRepository;

    @Test
    void getAllUsersReturnsRepositoryPage() {
        Page<User> page = new PageImpl<>(List.of(User.builder().id(1L).build()));
        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        assertThat(new AdminService(userRepository).getAllUsers(PageRequest.of(0, 10)))
                .isSameAs(page);
    }

    @Test
    void softDeleteMarksUserInactive() {
        User user = User.builder().id(1L).isActive(true).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        new AdminService(userRepository).softDeleteUser(1L);

        assertThat(user.getIsActive()).isFalse();
    }

    @Test
    void softDeleteRejectsUnknownUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new AdminService(userRepository).softDeleteUser(99L))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
