package com.technerd.finsight.security.handler;

import com.technerd.finsight.category.exampleseed.ExampleCategoryCreator;
import com.technerd.finsight.category.exampleseed.ExampleCategory;
import com.technerd.finsight.security.service.OAuthRegistrationService;
import com.technerd.finsight.security.service.UserSecurityService;
import com.technerd.finsight.systemevent.event.UserRegisteredEvent;
import com.technerd.finsight.user.User;
import com.technerd.finsight.user.enums.Role;
import com.technerd.finsight.security.service.JWTService;
import com.technerd.finsight.security.service.SessionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserSecurityService userSecurityService;
    private final JWTService jwtService;
    private final SessionService sessionService;
    private final OAuthRegistrationService  oAuthRegistrationService;

    private final ExampleCategoryCreator categoryCreator;
    private final ExampleCategory exampleCategory;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        try {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) token.getPrincipal();

            String email = oAuth2User.getAttribute("email");
            User user = userSecurityService.findByEmail(email);

            if (user == null) {
//                User newUser = User.builder()
//                        .email(email)
//                        .name(oAuth2User.getAttribute("name"))
//                        .role(Role.USER)
//                        .isActive(true)
//                        .build();
//                user = userSecurityService.save(newUser);
//                log.info("New user has been saved through OAuth 2.0 for {}", email);
//
//                eventPublisher.publishEvent(new UserRegisteredEvent(
//                        newUser.getEmail(),
//                        newUser.getName()
//                ));
//                categoryCreator.createCategory(exampleCategory, user);

                 user = oAuthRegistrationService.registerOAuthUser(
                        email,
                        oAuth2User.getAttribute("name"));

                log.info("New user has been saved through OAuth 2.0 for {}", email);

            }

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            sessionService.generateSession(user, refreshToken);

            Cookie cookie = new Cookie("REFRESH_TOKEN", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            // This thing needs to be taken care of.
            String redirectUrl = "http://localhost:8080/home.html?token=" + accessToken;
//            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
