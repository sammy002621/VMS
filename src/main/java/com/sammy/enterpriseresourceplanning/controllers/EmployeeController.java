package com.sammy.enterpriseresourceplanning.controllers;

import com.sammy.enterpriseresourceplanning.dtos.request.auth.UpdateUserDTO;
import com.sammy.enterpriseresourceplanning.dtos.request.user.CreateAdminDTO;
import com.sammy.enterpriseresourceplanning.dtos.request.user.CreateEmployeeDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.user.UserResponseDTO;
import com.sammy.enterpriseresourceplanning.models.User;
import com.sammy.enterpriseresourceplanning.services.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "APIs for managing employees")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final IUserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all employees", description = "Retrieves a list of all employees")
    public ResponseEntity<List<UserResponseDTO>> getAllEmployees() {
        List<User> users = userService.getUsers();
        List<UserResponseDTO> userResponseDTOs = users.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponseDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurity.isCurrentUser(#id)")
    @Operation(summary = "Get employee by ID", description = "Retrieves an employee by their ID")
    public ResponseEntity<UserResponseDTO> getEmployeeById(@PathVariable UUID id) {
        UserResponseDTO userResponseDTO = userService.getUserById(id);
        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current employee", description = "Retrieves the currently logged-in employee")
    public ResponseEntity<UserResponseDTO> getCurrentEmployee() {
        User currentUser = userService.getLoggedInUser();
        return ResponseEntity.ok(new UserResponseDTO(currentUser));
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create admin", description = "Creates a new admin user")
    public ResponseEntity<UserResponseDTO> createAdmin(@Valid @RequestBody CreateAdminDTO createAdminDTO) {
        UserResponseDTO userResponseDTO = userService.createAdmin(createAdminDTO);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Create employee", description = "Creates a new employee with specified role (MANAGER or EMPLOYEE)")
    public ResponseEntity<UserResponseDTO> createEmployee(@Valid @RequestBody CreateEmployeeDTO createEmployeeDTO) {
        UserResponseDTO userResponseDTO = userService.createEmployee(createEmployeeDTO);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    @Operation(summary = "Update employee", description = "Updates an employee's information")
    public ResponseEntity<UserResponseDTO> updateEmployee(@PathVariable UUID id, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        UserResponseDTO userResponseDTO = userService.updateUser(id, updateUserDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete employee", description = "Deletes an employee")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
