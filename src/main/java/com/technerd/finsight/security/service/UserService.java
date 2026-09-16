package com.technerd.finsight.security.service;

import com.technerd.finsight.security.entity.User;
import com.technerd.finsight.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    public User save(User user) {
        return userRepository.save(user);
    }

//    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
//        return loadUserById(id);
//    }

}
