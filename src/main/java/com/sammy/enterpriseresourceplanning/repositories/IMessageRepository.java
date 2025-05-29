package com.sammy.enterpriseresourceplanning.repositories;

import com.sammy.enterpriseresourceplanning.models.Message;
import com.sammy.enterpriseresourceplanning.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IMessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByEmployee(User employee);
    List<Message> findByEmployeeAndMonthAndYear(User employee, Integer month, Integer year);
    List<Message> findByMonthAndYear(Integer month, Integer year);
    List<Message> findBySent(boolean sent);
}