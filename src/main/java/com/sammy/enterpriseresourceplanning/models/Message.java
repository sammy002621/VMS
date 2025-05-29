package com.sammy.enterpriseresourceplanning.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer month;
    
    private Integer year;
    
    private boolean sent;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime sentAt;
}