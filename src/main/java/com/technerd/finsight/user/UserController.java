package com.technerd.finsight.user;

import com.technerd.finsight.user.dto.UserRequestDto;
import com.technerd.finsight.user.dto.UserResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    // Gets you the current user's id which is pretty obvious.
    private Long getUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> getProfile(){

        UserResponseDto profile = userService.getProfile(getUserId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/update-profile")
    public ResponseEntity<UserResponseDto> updateProfile(@Valid @RequestBody UserRequestDto incomingData){

        User toBeSavedInDB = modelMapper.map(incomingData, User.class);
        UserResponseDto userResponseDto = userService.updateUser(toBeSavedInDB, getUserId());

        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping("/soft-delete")
    public ResponseEntity<UserResponseDto> softDelete(){
        userService.softDelete(getUserId());

        return ResponseEntity.ok().build();
    }
}
