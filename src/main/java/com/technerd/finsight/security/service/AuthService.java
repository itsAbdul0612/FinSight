package com.technerd.finsight.security.service;

import com.technerd.finsight.category.exampleseed.ExampleCategoryCreator;
import com.technerd.finsight.category.exampleseed.ExampleCategory;
import com.technerd.finsight.systemevent.event.UserRegisteredEvent;
import com.technerd.finsight.security.repository.SessionRepository;
import com.technerd.finsight.security.dto.LoginDTO;
import com.technerd.finsight.security.dto.LoginResponse;
import com.technerd.finsight.security.dto.SignUpDTO;
import com.technerd.finsight.security.dto.SignUpResponse;
import com.technerd.finsight.security.dto.RefreshResponse;
import com.technerd.finsight.security.entity.Session;
import com.technerd.finsight.user.User;
import com.technerd.finsight.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final SessionRepository sessionRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;

    private final ExampleCategoryCreator categoryCreator;
    private final ExampleCategory exampleCategory;

    private final ApplicationEventPublisher eventPublisher;

    public LoginResponse login(LoginDTO loginDTO) {

        // Authenticating user.
        Authentication authenticated = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

        User user = (User) authenticated.getPrincipal();

        // This is not needed because isActive is handled by spring security's isEnabled.
        // if isActive is false spring security will not let the user login.

//        if (!Boolean.TRUE.equals(user.getIsActive())) {
//            throw new UsernameNotFoundException("User does not exist");
//        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        sessionService.generateSession(user, refreshToken);

        log.info("Login Success");
        return new LoginResponse(user.getId(), accessToken, refreshToken);
    }

    @Transactional
    public SignUpResponse signUp(SignUpDTO signUpDTO) {

        userRepository.findByEmail(signUpDTO.getEmail()).ifPresent(user -> {
            throw new UsernameNotFoundException("Username with this email already exist.");
        });

        log.info("Trying to sign up");

        User user = mapper.map(signUpDTO, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        sessionService.generateSession(user, refreshToken);

        SignUpResponse response = mapper.map(savedUser, SignUpResponse.class);
        response.setRefreshToken(refreshToken);
        response.setAccessToken(accessToken);

        categoryCreator.createCategory(exampleCategory, user);

        // Triggers welcome email.
        eventPublisher.publishEvent(new UserRegisteredEvent(user.getEmail(), user.getName()));

        log.info("Sign up Success");

        return response;
    }

    public RefreshResponse refreshTheToken(String refreshToken) {

        Session session = sessionService.validateSession(refreshToken);

        User user = session.getUser();
        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        session.setRefreshToken(newRefreshToken);
        session.setLastUsedAt(LocalDateTime.now());
        sessionRepository.save(session);

        return new RefreshResponse(newRefreshToken, accessToken);
    }

}
