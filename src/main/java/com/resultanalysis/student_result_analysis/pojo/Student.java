package com.resultanalysis.student_result_analysis.pojo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="students")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Student {
	
	@Id
	@NotBlank
	private String usn;
	
	@NotBlank
	private String studentName;
	
	@OneToMany(mappedBy = "student",cascade = CascadeType.ALL,fetch=FetchType.LAZY,orphanRemoval=true)
	private List<StudentSubjectMarks> studentSubjectMarks=new ArrayList<StudentSubjectMarks>();

}
