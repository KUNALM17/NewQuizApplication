package com.example.demo.Controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.QuestionWrapper;
import com.example.demo.Model.Quiz;
import com.example.demo.Model.Response;
import com.example.demo.Service.QuizService;

@RestController
@RequestMapping("quiz")

public class QuizController {
	@Autowired
	QuizService quizService;
	@GetMapping("/get/{id}")
	public ResponseEntity<List<QuestionWrapper>> getQuizQuestions (@PathVariable int id){
		return quizService.getQuizQuestions(id);
	}
	
	
	@PostMapping("/create")
	public ResponseEntity<String> createQuiz(@RequestParam String category,@RequestParam int numQ, @RequestParam String title ){
		return quizService.createQuize(category,numQ,title);
	}
	
	@PostMapping ("/submit/{id}") 
	public ResponseEntity <Integer> submitQuiz(@PathVariable int id, @RequestBody List<Response> responses)  {
		return quizService.calculateResult(id, responses);
	}
	
	
	

	
}
