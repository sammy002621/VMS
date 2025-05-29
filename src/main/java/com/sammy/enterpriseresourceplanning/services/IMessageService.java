package com.sammy.enterpriseresourceplanning.services;

import com.sammy.enterpriseresourceplanning.dtos.response.message.MessageResponseDTO;
import com.sammy.enterpriseresourceplanning.models.Message;
import com.sammy.enterpriseresourceplanning.models.PaySlip;
import com.sammy.enterpriseresourceplanning.models.User;

import java.util.List;
import java.util.UUID;

public interface IMessageService {
    MessageResponseDTO createMessage(PaySlip paySlip);
    MessageResponseDTO sendMessage(UUID id);
    List<MessageResponseDTO> sendAllPendingMessages();
    MessageResponseDTO getMessageById(UUID id);
    List<MessageResponseDTO> getMessagesByEmployee(UUID employeeId);
    List<MessageResponseDTO> getMessagesByMonthAndYear(Integer month, Integer year);
    List<MessageResponseDTO> getAllPendingMessages();
    List<MessageResponseDTO> getAllSentMessages();
    List<MessageResponseDTO> getAllMessages();
    void deleteMessage(UUID id);
    Message findMessageEntity(UUID id);
    
    // Helper methods
    String generateMessageContent(User employee, PaySlip paySlip);
}