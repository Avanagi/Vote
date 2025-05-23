package com.example.vote.service;

import com.example.vote.entity.PollEntity;
import com.example.vote.entity.StudentEntity;

import java.util.List;

public interface EmailService {

    void sendEmail(String to, String subject, String text);

    void sendEmailsToStudents(List<StudentEntity> students, PollEntity pollEntity);

}
