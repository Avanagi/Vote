package com.example.vote.service.impl;

import com.example.vote.entity.PollEntity;
import com.example.vote.entity.StudentEntity;
import com.example.vote.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Override
    public void sendEmailsToStudents(List<StudentEntity> students, PollEntity pollEntity) {
        if (students == null || students.isEmpty()) {
            log.debug("Нет пользователей для уведомления по опросу ID: {}", pollEntity.getId());
            return;
        }

        for (StudentEntity student : students) {
            try {
                sendEmail(
                        student.getEmail(),
                        "Доступен новый опрос: " + pollEntity.getQuestion(),
                        "Пожалуйста, зайдите в свой профиль и проголосуйте в опросе: " + pollEntity.getQuestion()
                );
                log.debug("Уведомление отправлено на email: {} по опросу ID: {}", student.getEmail(), pollEntity.getId());
            } catch (Exception e) {
                log.error("Не удалось отправить email для {} по опросу ID: {}", student.getEmail(), pollEntity.getId(), e);
            }
        }
    }
}
