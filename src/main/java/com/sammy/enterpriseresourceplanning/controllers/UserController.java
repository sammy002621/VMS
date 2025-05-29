package com.sammy.enterpriseresourceplanning.controllers;

import com.sammy.enterpriseresourceplanning.dtos.request.auth.UpdateUserDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.user.UserResponseDTO;
import com.sammy.enterpriseresourceplanning.dtos.request.user.UserRoleModificationDTO;
import com.sammy.enterpriseresourceplanning.models.User;
import com.sammy.enterpriseresourceplanning.payload.ApiResponse;
import com.sammy.enterpriseresourceplanning.services.IUserService;
import com.sammy.enterpriseresourceplanning.utils.ExceptionUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final IUserService userService;


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        try {
            List<User> users = userService.getUsers();
            List<UserResponseDTO> userDTOs = users.stream()
                    .map(UserResponseDTO::new)
                    .collect(Collectors.toList());

            return ApiResponse.success("Users retrieved successfully", HttpStatus.OK, userDTOs);
        } catch (Exception e) {
            ExceptionUtils.handleResponseException(e);
            return ApiResponse.fail("Failed to retrieve users", HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable UUID userId) {
        try {
            UserResponseDTO user = userService.getUserById(userId);
            return ApiResponse.success("User retrieved successfully", HttpStatus.OK, user);
        } catch (Exception e) {
            ExceptionUtils.handleResponseException(e);
            return ApiResponse.fail("Failed to retrieve user", HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable UUID userId,
            @RequestBody UpdateUserDTO updateUserDTO
    ) {
        try {
            UserResponseDTO updatedUser = userService.updateUser(userId, updateUserDTO);
            return ApiResponse.success("User updated successfully", HttpStatus.OK, updatedUser);
        } catch (Exception e) {
            ExceptionUtils.handleResponseException(e);
            return ApiResponse.fail("Failed to update user", HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{userId}/roles/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> addRoles(
            @PathVariable UUID userId,
            @RequestBody UserRoleModificationDTO userRoleModificationDTO
    ) {
        UserResponseDTO updatedUser = userService.addRoles(userId, userRoleModificationDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/{userId}/roles/remove")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> removeRoles(
            @PathVariable UUID userId,
            @RequestBody UserRoleModificationDTO userRoleModificationDTO
    ) {
        try {
            UserResponseDTO updatedUser = userService.removeRoles(userId, userRoleModificationDTO);
            return ApiResponse.success("User updated successfully", HttpStatus.OK, updatedUser);
        } catch (Exception e) {
            ExceptionUtils.handleResponseException(e);
            return ApiResponse.fail("Failed to remove roles", HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            ExceptionUtils.handleResponseException(e);
            return ApiResponse.fail("Failed to delete user", HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getLoggedInUser() {
        try {
            User loggedInUser = userService.getLoggedInUser();
            UserResponseDTO userResponseDTO = new UserResponseDTO(loggedInUser);
            return ApiResponse.success("User retrieved successfully", HttpStatus.OK, userResponseDTO);
        } catch (Exception e) {
            ExceptionUtils.handleResponseException(e);
            return ApiResponse.fail("Failed to get user", HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
