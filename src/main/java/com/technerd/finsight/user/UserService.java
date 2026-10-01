package com.technerd.finsight.user;

import com.technerd.finsight.user.dto.UserResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    // Get profile
    // -----------------------------------------------------------------------------------------------
    public UserResponseDto getProfile(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return modelMapper.map(user, UserResponseDto.class);
    }
    // -----------------------------------------------------------------------------------------------


    // Update user's profile
    // -----------------------------------------------------------------------------------------------
    @Transactional
    public UserResponseDto updateUser(User incomingData, Long userId){
        User user = userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        if (incomingData.getTotalBalance() != null) {
            user.setTotalBalance(incomingData.getTotalBalance());
        }
        if (incomingData.getName() != null) {
            user.setName(incomingData.getName());
        }
        if (incomingData.getEmail() != null) {
            user.setEmail(incomingData.getEmail());
        }
        if (incomingData.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(incomingData.getPassword()));
        }
        if (incomingData.getBaseCurrency() != null) {
            user.setBaseCurrency(incomingData.getBaseCurrency());
        }

        user.setUpdatedAt(LocalDateTime.now());

//        User updatedData = userRepository.save(user); is not needed
//        because the user is a managed hibernate entity and any update on it will be persisted by dirty checking

        log.info("Updating user with id {}", userId);
        return modelMapper.map(user, UserResponseDto.class);
    }
    // -----------------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------------
    @Transactional
    public void softDelete(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        user.setIsActive(false);
        log.info("Soft-deleted user with id {}", userId);

        // Can make this feature even better by maybe adding a goodbye page or something...
    }

}
