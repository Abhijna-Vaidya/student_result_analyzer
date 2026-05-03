package com.resultanalysis.student_result_analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.resultanalysis.student_result_analysis.pojo.RegisteredUser;

@Repository
public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, String>{
	
	public RegisteredUser findByUsername(String username);

}
