package com.technerd.finsight.category;

import com.technerd.finsight.category.dto.CategoryDto;
import com.technerd.finsight.security.entity.User;
import com.technerd.finsight.security.service.JWTService;
import com.technerd.finsight.security.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RequestMapping("/category")
@RequiredArgsConstructor
@RestController
public class CategoryController {

    private final CategoryService categoryService;
    private final AuthenticationManager authenticationManager;
    private final JWTService  jwtService;
    private final UserService userService;
    private final ModelMapper mapper;

    @PostMapping("/create")
    public ResponseEntity<CategoryDto> createCategory(
            @Valid @RequestBody CategoryDto categoryDto,
            HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new AuthenticationServiceException("Invalid cookies");
        }

        String token = Arrays.stream(cookies)
                .filter(cookie -> "REFRESH_TOKEN".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue).orElseThrow(() -> new AuthenticationServiceException("Cookie not found"));

        // Loading user.
        Long userIdFromToken = jwtService.getUserIdFromToken(token);
        User user = userService.findById(userIdFromToken);

        categoryService.createCategory(categoryDto, user);

        return ResponseEntity.ok(categoryDto);
    }

}
