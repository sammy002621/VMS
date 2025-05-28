package com.sammy.vehiclemanagementsystem.utils.helpers;


import com.sammy.vehiclemanagementsystem.dtos.request.owner.CreateOwnerDTO;
import com.sammy.vehiclemanagementsystem.models.Owner;
import com.sammy.vehiclemanagementsystem.models.Role;
import com.sammy.vehiclemanagementsystem.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class OwnerHelper {
    // create a user from a CreateOwnerDTO and a role
    public User buildUserFromDto(CreateOwnerDTO dto, Role role, PasswordEncoder passwordEncoder){
        return  User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .fullName(dto.getFirstName() + " " + dto.getLastName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nationalId(dto.getNationalId())
                .phoneNumber(dto.getPhoneNumber())
                .roles(Set.of(role))
                .build();
    }

// create an owner from a user and a CreateOwnerDTO (used to obtain the address of the owner)
    public Owner buildOwner(User user, CreateOwnerDTO dto){
        return Owner.builder()
                .profile(user)
                .address(dto.getAddress())
                .build();
    }
}
