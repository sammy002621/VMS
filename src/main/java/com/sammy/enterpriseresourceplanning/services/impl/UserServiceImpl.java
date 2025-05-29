package com.sammy.enterpriseresourceplanning.services.impl;

import com.sammy.enterpriseresourceplanning.dtos.request.auth.UpdateUserDTO;
import com.sammy.enterpriseresourceplanning.dtos.request.user.CreateAdminDTO;
import com.sammy.enterpriseresourceplanning.dtos.request.user.CreateEmployeeDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.user.UserResponseDTO;
import com.sammy.enterpriseresourceplanning.dtos.request.user.UserRoleModificationDTO;
import com.sammy.enterpriseresourceplanning.enums.ERole;
import com.sammy.enterpriseresourceplanning.exceptions.BadRequestException;
import com.sammy.enterpriseresourceplanning.exceptions.NotFoundException;
import com.sammy.enterpriseresourceplanning.models.Role;
import com.sammy.enterpriseresourceplanning.models.User;
import com.sammy.enterpriseresourceplanning.repositories.IRoleRepository;
import com.sammy.enterpriseresourceplanning.repositories.IUserRepository;
import com.sammy.enterpriseresourceplanning.services.IRoleService;
import com.sammy.enterpriseresourceplanning.services.IUserService;
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
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new BadRequestException("User not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public UserResponseDTO createEmployee(CreateEmployeeDTO createEmployeeDTO) {
        if (isUserPresent(createEmployeeDTO.getEmail())) {
            throw new BadRequestException("User with the email already exists");
        }

        User user = User.builder()
                .email(createEmployeeDTO.getEmail())
                .firstName(createEmployeeDTO.getFirstName())
                .lastName(createEmployeeDTO.getLastName())
                .fullName(createEmployeeDTO.getFirstName() + " " + createEmployeeDTO.getLastName())
                .nationalId(createEmployeeDTO.getNationalId())
                .phoneNumber(createEmployeeDTO.getPhoneNumber())
                .password(passwordEncoder.encode(createEmployeeDTO.getPassword()))
                .roles(new HashSet<>(Collections.singletonList(roleService.getRoleByName(createEmployeeDTO.getRole()))))
                .build();

        userRepository.save(user);
        return UserResponseDTO.builder().user(user).build();
    }
}
