package com.sammy.enterpriseresourceplanning.security.user;

import com.sammy.enterpriseresourceplanning.exceptions.BadRequestException;
import com.sammy.enterpriseresourceplanning.models.User;
import com.sammy.enterpriseresourceplanning.repositories.IUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final IUserRepository userRepository;


    @Autowired
    public CustomUserDetailsService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDetails loadByUserId(UUID id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserByUsername(String s) throws BadRequestException {
        User user = userRepository.findUserByEmail(s).orElseThrow(() -> new UsernameNotFoundException("user not found with email of " + s));
        return UserPrincipal.create(user);
    }
}
