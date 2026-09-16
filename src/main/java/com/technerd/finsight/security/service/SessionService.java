package com.technerd.finsight.security.service;

import com.technerd.finsight.security.repository.SessionRepository;
import com.technerd.finsight.security.entity.Session;
import com.technerd.finsight.security.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final JWTService jwtService;
    private static final int SESSION_LIMIT = 2;

    public void generateSession(User user, String refreshToken) {
        List<Session> sessionList = sessionRepository.findByUser(user);

        if (sessionList.size() >= SESSION_LIMIT) {
            sessionList.sort(Comparator.comparing(
                    Session::getLastUsedAt,
                    Comparator.nullsFirst(Comparator.naturalOrder())));
            Session leastRecentSession = sessionList.getFirst();
            sessionRepository.delete(leastRecentSession);
        }

        Session newSession = Session.builder()
                .refreshToken(refreshToken)
                .lastUsedAt(LocalDateTime.now())
                .isValid(true)
                .user(user)
                .build();

        sessionRepository.save(newSession);
    }

    public Session validateSession(String refreshToken) {
        Session session = sessionRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new SessionAuthenticationException("No session found for the refresh token"));

        if (session.getIsValid()) {
            return session;
        }
        throw new SessionAuthenticationException("Session has expired");
    }

    public void invalidateSession(String refreshToken) {
        Session session = sessionRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new SessionAuthenticationException("No session found for the refresh token"));
        session.setIsValid(false);
        sessionRepository.save(session);
    }

}
