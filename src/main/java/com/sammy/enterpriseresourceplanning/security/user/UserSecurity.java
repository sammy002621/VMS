package com.sammy.enterpriseresourceplanning.security.user;

import com.sammy.enterpriseresourceplanning.models.User;
import com.sammy.enterpriseresourceplanning.repositories.IEmploymentRepository;
import com.sammy.enterpriseresourceplanning.repositories.IMessageRepository;
import com.sammy.enterpriseresourceplanning.repositories.IPaySlipRepository;
import com.sammy.enterpriseresourceplanning.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserSecurity {

    private final IUserService userService;
    private final IEmploymentRepository employmentRepository;
    private final IPaySlipRepository paySlipRepository;
    private final IMessageRepository messageRepository;

    public boolean isCurrentUser(UUID userId) {
        User currentUser = userService.getLoggedInUser();
        return currentUser.getId().equals(userId);
    }

    public boolean isEmployeeOfEmployment(UUID employmentId) {
        User currentUser = userService.getLoggedInUser();
        return employmentRepository.findById(employmentId)
                .map(employment -> employment.getEmployee().getId().equals(currentUser.getId()))
                .orElse(false);
    }

    public boolean isEmployeeOfPaySlip(UUID paySlipId) {
        User currentUser = userService.getLoggedInUser();
        return paySlipRepository.findById(paySlipId)
                .map(paySlip -> paySlip.getEmployee().getId().equals(currentUser.getId()))
                .orElse(false);
    }

    public boolean isEmployeeOfMessage(UUID messageId) {
        User currentUser = userService.getLoggedInUser();
        return messageRepository.findById(messageId)
                .map(message -> message.getEmployee().getId().equals(currentUser.getId()))
                .orElse(false);
    }
}