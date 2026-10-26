package com.technerd.finsight.core.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technerd.finsight.core.category.dto.CategoryCreateDto;
import com.technerd.finsight.core.category.dto.CategoryResponseDto;
import com.technerd.finsight.core.transaction.enums.TransactionType;
import com.technerd.finsight.security.service.JWTService;
import com.technerd.finsight.security.service.UserSecurityService;
import com.technerd.finsight.user.User;
import com.technerd.finsight.user.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;


import static org.mockito.Mockito.doNothing;


import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private UserSecurityService userSecurityService;

    @MockitoBean
    private ModelMapper mapper;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .role(Role.USER)
                .isActive(true)
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Nested
    @DisplayName("POST /category/create")
    class CreateCategoryTests {

        @Test
        @DisplayName("Should successfully create a category")
        void createCategory_Success() throws Exception {
            CategoryCreateDto requestDto = new CategoryCreateDto();
            requestDto.setName("Food");
            requestDto.setDescription("Groceries and food");
            requestDto.setTransactionType(TransactionType.EXPENSE);
            requestDto.setIcon("food-icon");
            requestDto.setAllocatedAmount(new BigDecimal("500.00"));
            requestDto.setSpentAmount(BigDecimal.ZERO);

            given(userSecurityService.findById(1L)).willReturn(mockUser);
            given(categoryService.createCategory(any(CategoryCreateDto.class), any(User.class)))
                    .willReturn(requestDto);

            MvcResult result = mockMvc.perform(post("/category/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CategoryCreateDto responseDto = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    CategoryCreateDto.class
            );

            assertThat(responseDto).isNotNull();
            assertThat(responseDto.getName()).isEqualTo("Food");
            assertThat(responseDto.getDescription()).isEqualTo("Groceries and food");
            assertThat(responseDto.getTransactionType()).isEqualTo(TransactionType.EXPENSE);

            verify(userSecurityService).findById(1L);
            verify(categoryService).createCategory(any(CategoryCreateDto.class), eq(mockUser));
        }
    }

    @Nested
    @DisplayName("GET /category/get-all")
    class GetAllCategoriesTests {

        @Test
        @DisplayName("Should return list of categories when categories exist")
        void getAllCategories_Success() throws Exception {
            CategoryResponseDto cat1 = new CategoryResponseDto();
            cat1.setId(10L);
            cat1.setName("Food");
            cat1.setDescription("Food & Groceries");
            cat1.setTransactionType(TransactionType.EXPENSE);
            cat1.setIcon("food-icon");

            CategoryResponseDto cat2 = new CategoryResponseDto();
            cat2.setId(11L);
            cat2.setName("Salary");
            cat2.setDescription("Monthly salary");
            cat2.setTransactionType(TransactionType.INCOME);
            cat2.setIcon("salary-icon");

            given(categoryService.findAllByUserId(1L)).willReturn(List.of(cat1, cat2));

            MvcResult result = mockMvc.perform(get("/category/get-all"))
                    .andExpect(status().isOk())
                    .andReturn();

            List<CategoryResponseDto> responseList = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CategoryResponseDto.class)
            );

            assertThat(responseList).hasSize(2);
            assertThat(responseList.get(0).getName()).isEqualTo("Food");
            assertThat(responseList.get(1).getName()).isEqualTo("Salary");

            verify(categoryService).findAllByUserId(1L);
        }

        @Test
        @DisplayName("Should return 404 Not Found when category list is empty")
        void getAllCategories_Empty_ReturnsNotFound() throws Exception {
            given(categoryService.findAllByUserId(1L)).willReturn(Collections.emptyList());

            mockMvc.perform(get("/category/get-all"))
                    .andExpect(status().isNotFound());

            verify(categoryService).findAllByUserId(1L);
        }
    }

    @Nested
    @DisplayName("PATCH /category/patch-update/{id}")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Should successfully update category by id")
        void updateCategory_Success() throws Exception {
            Long categoryId = 10L;

            CategoryCreateDto updateRequest = new CategoryCreateDto();
            updateRequest.setName("Updated Food");
            updateRequest.setDescription("Updated description");
            updateRequest.setTransactionType(TransactionType.EXPENSE);
            updateRequest.setIcon("new-food-icon");

            Category mappedCategory = Category.builder()
                    .name("Updated Food")
                    .description("Updated description")
                    .transactionType(TransactionType.EXPENSE)
                    .icon("new-food-icon")
                    .build();

            Category updatedEntity = Category.builder()
                    .id(categoryId)
                    .name("Updated Food")
                    .description("Updated description")
                    .transactionType(TransactionType.EXPENSE)
                    .icon("new-food-icon")
                    .build();

            CategoryResponseDto responseDto = new CategoryResponseDto();
            responseDto.setId(categoryId);
            responseDto.setName("Updated Food");
            responseDto.setDescription("Updated description");
            responseDto.setTransactionType(TransactionType.EXPENSE);
            responseDto.setIcon("new-food-icon");

            given(mapper.map(any(CategoryCreateDto.class), eq(Category.class))).willReturn(mappedCategory);
            given(categoryService.updateById(eq(categoryId), eq(mappedCategory))).willReturn(updatedEntity);
            given(mapper.map(eq(updatedEntity), eq(CategoryResponseDto.class))).willReturn(responseDto);

            MvcResult result = mockMvc.perform(patch("/category/patch-update/{id}", categoryId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            CategoryResponseDto actualResponse = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    CategoryResponseDto.class
            );

            assertThat(actualResponse).isNotNull();
            assertThat(actualResponse.getId()).isEqualTo(categoryId);
            assertThat(actualResponse.getName()).isEqualTo("Updated Food");

            verify(categoryService).updateById(eq(categoryId), eq(mappedCategory));
        }
    }

    @Nested
    @DisplayName("DELETE /category/delete/{id}")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should successfully delete category by id")
        void deleteCategory_Success() throws Exception {
            Long categoryId = 10L;

            doNothing().when(categoryService).softDeleteById(categoryId, 1L);

            mockMvc.perform(delete("/category/delete/{id}", categoryId))
                    .andExpect(status().isOk());

            verify(categoryService).softDeleteById(categoryId, 1L);
        }
    }
}
