package com.technerd.finsight.admin;

import com.technerd.finsight.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/soft-delete")
    public ResponseEntity<?> softDeleteAUser(@RequestParam Long userId){
        adminService.softDeleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-all-users")
    public Page<User> allUsers(@RequestParam(required = false, defaultValue = "10") int pageSize,
                               @RequestParam(required = false, defaultValue = "1") int pageNumber){

        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        return adminService.getAllUsers(pageable);
    }
}
