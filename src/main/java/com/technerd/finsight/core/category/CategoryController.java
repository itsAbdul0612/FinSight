package com.technerd.finsight.core.category;

import com.technerd.finsight.core.category.dto.CategoryCreateDto;
import com.technerd.finsight.core.category.dto.CategoryResponseDto;
import com.technerd.finsight.user.User;
import com.technerd.finsight.security.service.JWTService;
import com.technerd.finsight.security.service.UserSecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Category",
description = "A category is a specific finance.\ne.g. Food, Netflix, etc.")
@RequestMapping("/category")
@RequiredArgsConstructor
@RestController
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;
    private final AuthenticationManager authenticationManager;
    private final JWTService  jwtService;
    private final UserSecurityService userSecurityService;
    private final ModelMapper mapper;

    // Create a category.
    // -----------------------------------------------------------------------------------------------
    @PostMapping("/create")
    @Operation(summary = "Create a category", description = "Creates a finance category for the authenticated user. Optionally initialize its allocated budget.")
    public ResponseEntity<CategoryCreateDto> createCategory(@Valid @RequestBody CategoryCreateDto categoryDto) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new AuthenticationServiceException("User not found");
        }

        Long userId = user.getId();
        User userEntity = userSecurityService.findById(userId);

        categoryService.createCategory(categoryDto, userEntity);
        return ResponseEntity.ok(categoryDto);
    }
    // -----------------------------------------------------------------------------------------------


    // GET all Category of a user.
    // -----------------------------------------------------------------------------------------------
    @GetMapping("/get-all")
    @Operation(summary = "List categories", description = "Returns all categories belonging to the authenticated user. Returns 404 when no categories are available.")
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
    @Operation(summary = "Update a category", description = "Updates the category identified by its ID using the supplied category data.")
    public ResponseEntity<CategoryResponseDto> getCategory(
            @Parameter(description = "ID of the category to update", required = true)
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
    @Operation(summary = "Delete a category", description = "Soft-deletes a category owned by the authenticated user.")
    public ResponseEntity<?> deleteCategory(
            @Parameter(description = "ID of the category to delete", required = true)
            @PathVariable Long id) {
        
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();
        categoryService.softDeleteById(id, userId);

       return ResponseEntity.ok().build();
    }
}
