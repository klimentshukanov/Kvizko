package com.example.kvizko.service;

import com.example.kvizko.models.Quiz;

import java.util.List;


public interface QuizService {

    List<Quiz> listAll();
    List<Quiz> quizzesByCategoryID(Long categoryid);
    Quiz quizById(Long id);
}
