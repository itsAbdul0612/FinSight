package com.technerd.finsight.category;

import com.technerd.finsight.category.dto.CategoryCreateDto;
import com.technerd.finsight.category.dto.CategoryResponseDto;
import com.technerd.finsight.security.entity.User;
import com.technerd.finsight.security.service.JWTService;
import com.technerd.finsight.security.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping("/category")
@RequiredArgsConstructor
@RestController
public class CategoryController {

    private final CategoryService categoryService;
    private final AuthenticationManager authenticationManager;
    private final JWTService  jwtService;
    private final UserService userService;
    private final ModelMapper mapper;

    // Create a category.
    // -----------------------------------------------------------------------------------------------
    @PostMapping("/create")
    public ResponseEntity<CategoryCreateDto> createCategory(@Valid @RequestBody CategoryCreateDto categoryDto) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new AuthenticationServiceException("User not found");
        }

        Long userId = user.getId();
        User userEntity = userService.findById(userId);

        categoryService.createCategory(categoryDto, userEntity);
        return ResponseEntity.ok(categoryDto);
    }
    // -----------------------------------------------------------------------------------------------


    // GET all Category of a user.
    // -----------------------------------------------------------------------------------------------
    @GetMapping("/get-all")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        List<CategoryResponseDto> categoryList = categoryService.findAllByUserId(userId);
        if (categoryList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categoryList);
    }
    // -----------------------------------------------------------------------------------------------

    // PATCH update a category by id.
    // -----------------------------------------------------------------------------------------------
    @PatchMapping("/patch-update/{id}")
    public ResponseEntity<CategoryResponseDto> getCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryCreateDto createDto) {

        Category category = mapper.map(createDto, Category.class);
        Category updatedCategory = categoryService.updateById(id, category);

        return ResponseEntity.ok(mapper.map(updatedCategory, CategoryResponseDto.class));
    }
    // -----------------------------------------------------------------------------------------------

    // DELETE a category by id.
    // -----------------------------------------------------------------------------------------------
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();
        categoryService.deleteById(id, userId);

       return ResponseEntity.ok().build();
    }
}
