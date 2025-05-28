package com.sammy.vehiclemanagementsystem.services;



import com.sammy.vehiclemanagementsystem.dtos.request.auth.UpdateUserDTO;
import com.sammy.vehiclemanagementsystem.dtos.request.user.CreateAdminDTO;
import com.sammy.vehiclemanagementsystem.dtos.response.user.UserResponseDTO;
import com.sammy.vehiclemanagementsystem.dtos.request.user.UserRoleModificationDTO;
import com.sammy.vehiclemanagementsystem.models.User;

import java.util.List;
import java.util.UUID;

public interface IUserService {

    User findUserById(UUID userId);

    User getLoggedInUser();

    UserResponseDTO createAdmin(CreateAdminDTO createUserDTO);

    List<User> getUsers();

    UserResponseDTO getUserById(UUID uuid);

    UserResponseDTO updateUser(UUID userId, UpdateUserDTO updateUserDTO);

    UserResponseDTO addRoles(UUID userId, UserRoleModificationDTO userRoleModificationDTO);

    UserResponseDTO removeRoles(UUID userId, UserRoleModificationDTO userRoleModificationDTO);

    void deleteUser(UUID userId);
}
