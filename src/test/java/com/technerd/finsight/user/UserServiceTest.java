package com.technerd.finsight.user;

import com.technerd.finsight.user.dto.UserResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private PasswordEncoder passwordEncoder;

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(userRepository, modelMapper, passwordEncoder);
    }

    @Test
    void getProfileMapsUser() {
        User user = User.builder().id(1L).name("Test").build();
        UserResponseDto response = new UserResponseDto();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(response);

        assertThat(service.getProfile(1L)).isSameAs(response);
    }

    @Test
    void getProfileRejectsUnknownUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getProfile(99L))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void updateUserUpdatesFieldsAndEncodesPassword() {
        User user = User.builder().id(1L).name("Old").build();
        User incoming = User.builder().name("New").password("secret").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(new UserResponseDto());

        service.updateUser(incoming, 1L);

        assertThat(user.getName()).isEqualTo("New");
        assertThat(user.getPassword()).isEqualTo("encoded");
        assertThat(user.getUpdatedAt()).isNotNull();
        verify(passwordEncoder).encode("secret");
    }

    @Test
    void softDeleteMarksUserInactive() {
        User user = User.builder().id(1L).isActive(true).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.softDelete(1L);

        assertThat(user.getIsActive()).isFalse();
    }
}
