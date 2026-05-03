package com.resultanalysis.student_result_analysis.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.resultanalysis.student_result_analysis.dto.RegisterUserDto;
import com.resultanalysis.student_result_analysis.serviceImpl.UserService;

@RestController
@RequestMapping("/common")
public class UserController {
	
	@Autowired
	public UserService userService;

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody RegisterUserDto registerUser) {
		
		if(registerUser==null) {
			throw new IllegalArgumentException("Please enter Username and Password");
		}
		
		String createdUser=null;
		createdUser = userService.registerUser(registerUser);
	    return new ResponseEntity<String>(createdUser, HttpStatus.CREATED);
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody RegisterUserDto registeredUser) {

		if(registeredUser==null) {
			throw new IllegalArgumentException("Please enter Username and Password");
		}
		
		String token=null;
		token = userService.loginUser(registeredUser);
		
	    return new ResponseEntity<String>(token, HttpStatus.OK);
	}
}
