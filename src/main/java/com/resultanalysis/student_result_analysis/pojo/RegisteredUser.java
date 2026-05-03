package com.resultanalysis.student_result_analysis.pojo;

import com.resultanalysis.student_result_analysis.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredUser {
	
	@Id
	private String username;
	
	private String password;
	
	@Enumerated(EnumType.STRING)
    private Role role;
	

}
