package com.sammy.enterpriseresourceplanning.services;



import com.sammy.enterpriseresourceplanning.dtos.request.auth.UpdateUserDTO;
import com.sammy.enterpriseresourceplanning.dtos.request.user.CreateAdminDTO;
import com.sammy.enterpriseresourceplanning.dtos.request.user.CreateEmployeeDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.user.UserResponseDTO;
import com.sammy.enterpriseresourceplanning.dtos.request.user.UserRoleModificationDTO;
import com.sammy.enterpriseresourceplanning.models.User;

import java.util.List;
import java.util.UUID;

public interface IUserService {

    User findUserById(UUID userId);

    User getLoggedInUser();

    UserResponseDTO createAdmin(CreateAdminDTO createUserDTO);

    UserResponseDTO createEmployee(CreateEmployeeDTO createEmployeeDTO);

    List<User> getUsers();

    UserResponseDTO getUserById(UUID uuid);

    UserResponseDTO updateUser(UUID userId, UpdateUserDTO updateUserDTO);

    UserResponseDTO addRoles(UUID userId, UserRoleModificationDTO userRoleModificationDTO);

    UserResponseDTO removeRoles(UUID userId, UserRoleModificationDTO userRoleModificationDTO);

    void deleteUser(UUID userId);
}
