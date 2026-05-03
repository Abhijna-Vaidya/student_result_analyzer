package com.resultanalysis.student_result_analysis.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.resultanalysis.student_result_analysis.dto.RegisterUserDto;
import com.resultanalysis.student_result_analysis.enums.Role;
import com.resultanalysis.student_result_analysis.exception.IncorrectPasswordException;
import com.resultanalysis.student_result_analysis.exception.StudentNotFoundException;
import com.resultanalysis.student_result_analysis.exception.UserAlreadyRegisteredException;
import com.resultanalysis.student_result_analysis.pojo.RegisteredUser;
import com.resultanalysis.student_result_analysis.pojo.Student;
import com.resultanalysis.student_result_analysis.repository.RegisteredUserRepository;
import com.resultanalysis.student_result_analysis.repository.StudentRepository;

@Service
public class UserService {

	@Autowired
	private RegisteredUserRepository registeredUserRepository;

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(5);

	@Autowired
	private AuthenticationManager authManager;

	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private StudentRepository studentRepository;

	public String registerUser(RegisterUserDto registerUser){
		// TODO Auto-generated method stub
		
		Student student=studentRepository.findById(registerUser.getUsername())
				.orElseThrow(()->new StudentNotFoundException(registerUser.getUsername()+" doesn't exists"));
		
		registerUser.setPassword(encoder.encode(registerUser.getPassword()));
		
		Optional<RegisteredUser> optionalUser=registeredUserRepository.findById(registerUser.getUsername());
		
		if(optionalUser.isPresent()) {
			throw new UserAlreadyRegisteredException("User Already Registered");
		}
		
		RegisteredUser registerUserObj=new RegisteredUser(
				registerUser.getUsername(),registerUser.getPassword(), Role.ROLE_USER);
		
		registeredUserRepository.save(registerUserObj);
		return "User "+registerUser.getUsername()+" sucessfully registered";
	}

	public String loginUser(RegisterUserDto registerUser){
		// TODO Auto-generated method stub
		System.out.println("In login function: " + registerUser);
		RegisteredUser user = registeredUserRepository.findById(registerUser.getUsername())
				.orElseThrow(()->new IllegalArgumentException("User not Registered"));

			if (encoder.matches(registerUser.getPassword(), user.getPassword())) {
				Authentication authentication = authManager.authenticate(
						new UsernamePasswordAuthenticationToken(registerUser.getUsername(), 
								registerUser.getPassword()));

				System.out.println("Login Success");

				return jwtService.generateToken(registerUser.getUsername());

			} else {
				throw new IncorrectPasswordException("Password is incorrect");
			}
	}

}
