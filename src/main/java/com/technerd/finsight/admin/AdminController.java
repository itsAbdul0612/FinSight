package com.technerd.finsight.admin;

import com.technerd.finsight.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
@Tag(name = "Administration", description = "Administrative operations. All endpoints require the ADMIN role.")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/soft-delete")
    @Operation(summary = "Soft-delete a user", description = "Marks the specified user inactive. Requires the ADMIN role.")
    public ResponseEntity<?> softDeleteAUser(
            @Parameter(description = "ID of the user to deactivate", required = true)
            @RequestParam Long userId) {
        adminService.softDeleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-all-users")
    @Operation(summary = "List users", description = "Returns a page of users for administrators. The internal User schema is intentionally hidden from OpenAPI.")
    public Page<User> allUsers(
            @Parameter(description = "Maximum number of users per page", example = "10")
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @Parameter(description = "One-based page number", example = "1")
            @RequestParam(required = false, defaultValue = "1") int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        return adminService.getAllUsers(pageable);
    }
}
