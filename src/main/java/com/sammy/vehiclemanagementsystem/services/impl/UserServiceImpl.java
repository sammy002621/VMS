package com.sammy.vehiclemanagementsystem.services.impl;

import com.sammy.vehiclemanagementsystem.dtos.request.auth.UpdateUserDTO;
import com.sammy.vehiclemanagementsystem.dtos.request.user.CreateAdminDTO;
import com.sammy.vehiclemanagementsystem.dtos.response.user.UserResponseDTO;
import com.sammy.vehiclemanagementsystem.dtos.request.user.UserRoleModificationDTO;
import com.sammy.vehiclemanagementsystem.enums.ERole;
import com.sammy.vehiclemanagementsystem.exceptions.BadRequestException;
import com.sammy.vehiclemanagementsystem.exceptions.NotFoundException;
import com.sammy.vehiclemanagementsystem.models.Role;
import com.sammy.vehiclemanagementsystem.models.User;
import com.sammy.vehiclemanagementsystem.repositories.IRoleRepository;
import com.sammy.vehiclemanagementsystem.repositories.IUserRepository;
import com.sammy.vehiclemanagementsystem.services.IRoleService;
import com.sammy.vehiclemanagementsystem.services.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final IRoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.security.admin.create.code}")
    private String adminCreateCode;

    public boolean isUserPresent(String email) {
        return userRepository.findUserByEmail(email).isPresent();
    }

    @Override
    public User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found with id: " + userId));
    }

    @Override
    public User getLoggedInUser() {
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        User user = userRepository.findUserByEmail(username).orElseThrow(() -> new NotFoundException("User Not Found"));
        user.setFullName(user.getFirstName() + " " + user.getLastName());
        return user;
    }

    public User createUserEntity(CreateAdminDTO createAdminDTO) {
        if (isUserPresent(createAdminDTO.getEmail())) {
            throw new BadRequestException("User with the email already exists");
        }

        return User.builder()
                .email(createAdminDTO.getEmail())
                .firstName(createAdminDTO.getFirstName())
                .lastName(createAdminDTO.getLastName())
                .fullName(createAdminDTO.getFirstName() + " " + createAdminDTO.getLastName())
                .nationalId(createAdminDTO.getNationalId())
                .phoneNumber(createAdminDTO.getPhoneNumber())
                .password(passwordEncoder.encode(createAdminDTO.getPassword()))
                .roles(new HashSet<>(Collections.singletonList(roleService.getRoleByName(ERole.ADMIN))))
                .build();
    }

    @Override
    @Transactional
    public UserResponseDTO createAdmin(CreateAdminDTO createAdminDTO) {
        if (!createAdminDTO.getAdminCreateCode().equals(adminCreateCode)) {
            throw new BadRequestException("Invalid admin creation code");
        }

        User user = createUserEntity(createAdminDTO);
        System.out.println("The CREATED ADMIN DETAILS: " + user);
        userRepository.save(user);
        return UserResponseDTO.builder().user(user).build();
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserResponseDTO getUserById(UUID userId) {
        User user = findUserById(userId);
        return new UserResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUser(UUID userId, UpdateUserDTO updateUserDTO) {
        User user = findUserById(userId);

        user.setFirstName(updateUserDTO.getFirstName());
        user.setLastName(updateUserDTO.getLastName());
        user.setFullName(updateUserDTO.getFirstName() + " " + updateUserDTO.getLastName());
        user.setPhoneNumber(updateUserDTO.getPhoneNumber());
        user.setEmail(updateUserDTO.getEmail());

        userRepository.save(user);

        return UserResponseDTO.builder().user(user).build();
    }

    @Override
    public UserResponseDTO addRoles(UUID userId, UserRoleModificationDTO userRoleModificationDTO) {
        User user = findUserById(userId);
        Set<Role> roles = user.getRoles();
        for (UUID roleId : userRoleModificationDTO.getRoles()) {
            Role role = roleRepository.findById(roleId).orElseThrow(() -> new NotFoundException("Role not found"));
            roles.add(role);
        }


        user.getRoles().addAll(roles);
        userRepository.save(user);

        return new UserResponseDTO(user);
    }

    @Override
    public UserResponseDTO removeRoles(UUID userId, UserRoleModificationDTO userRoleModificationDTO) {
        User user = findUserById(userId);
        Set<Role> roles = user.getRoles();
        for (UUID roleId : userRoleModificationDTO.getRoles()) {
            Role role = roleRepository.findById(roleId).orElseThrow(() -> new NotFoundException("Role not found"));
            roles.add(role);
        }

        user.getRoles().removeAll(roles);
        userRepository.save(user);

        return new UserResponseDTO(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new BadRequestException("User not found");
        }
        userRepository.deleteById(userId);
    }
}

