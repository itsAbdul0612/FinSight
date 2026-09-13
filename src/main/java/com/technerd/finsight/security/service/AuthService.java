package com.technerd.finsight.security.service;

import com.technerd.finsight.security.dto.LoginDTO;
import com.technerd.finsight.security.dto.LoginResponse;
import com.technerd.finsight.security.dto.SignUpDTO;
import com.technerd.finsight.security.dto.SignUpResponse;
import com.technerd.finsight.security.entity.User;
import com.technerd.finsight.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;

    public LoginResponse login(LoginDTO loginDTO) {

        // Authenticating user.
        Authentication authenticated = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

        User user = (User) authenticated.getPrincipal();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse(user.getId(), accessToken, refreshToken);
    }

    public SignUpResponse signUp(SignUpDTO signUpDTO) {

        userRepository.findByEmail(signUpDTO.getEmail()).ifPresent(user -> {
            throw new UsernameNotFoundException("Username with this email already exist.");
        });

        User user = mapper.map(signUpDTO, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        SignUpResponse response = mapper.map(savedUser, SignUpResponse.class);
        response.setRefreshToken(refreshToken);
        response.setAccessToken(accessToken);

        return response;
    }

}
