package com.technerd.finsight.user;

import com.technerd.finsight.user.dto.UserRequestDto;
import com.technerd.finsight.user.dto.UserResponseDto;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
@Tag(name = "User Profile", description = "Read and update the authenticated user's profile.")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    // Gets you the current user's id which is pretty obvious.
    private Long getUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    @GetMapping("/profile")
    @Operation(summary = "Get the current profile", description = "Returns the authenticated user's profile using a public response DTO; the internal User entity is not part of this schema.")
    public ResponseEntity<UserResponseDto> getProfile(){

        UserResponseDto profile = userService.getProfile(getUserId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/update-profile")
    @Operation(summary = "Update the current profile", description = "Updates the authenticated user's profile from the supplied profile fields.")
    public ResponseEntity<UserResponseDto> updateProfile(@Valid @RequestBody UserRequestDto incomingData){

        User toBeSavedInDB = modelMapper.map(incomingData, User.class);
        UserResponseDto userResponseDto = userService.updateUser(toBeSavedInDB, getUserId());

        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping("/soft-delete")
    @Operation(summary = "Deactivate the current account", description = "Soft-deletes the authenticated user's account.")
    public ResponseEntity<UserResponseDto> softDelete(){
        userService.softDelete(getUserId());

        return ResponseEntity.ok().build();
    }
}
