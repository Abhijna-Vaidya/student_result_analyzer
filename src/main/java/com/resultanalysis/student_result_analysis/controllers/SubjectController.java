package com.resultanalysis.student_result_analysis.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.resultanalysis.student_result_analysis.pojo.Subjects;
import com.resultanalysis.student_result_analysis.serviceImpl.SubjectService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/admin/subjects")
public class SubjectController {
	
	@Autowired
	private SubjectService subjectService;
	
	@PostMapping("/addSubject")
	public ResponseEntity<String> addSubjects(@Valid @RequestBody List<Subjects> subject){
		
		subjectService.addSubjects(subject);
		
		return ResponseEntity.ok("Subjects add sucessfully");
	}
	
	@DeleteMapping("/deleteSubjectByID/{subjectId}")
	public ResponseEntity<String> deleteSubjectByID(@PathVariable String subjectId){
		
		subjectService.deleteSubjectByID(subjectId);
		
		return ResponseEntity.ok("Subject deleted sucessfully");
	}
	
//	@PutMapping("/editSubject/{subjectId}")
//	public ResponseEntity<String> editSubject(@PathVariable String subjectId){
//		
//		subjectService.editSubject(subjectId);
//		
//		return ResponseEntity.ok("Subjects add sucessfully");
//	}
	
	@DeleteMapping("/deleteAllSubjects")
	public ResponseEntity<String> deleteAllSubjects(){
		
		subjectService.deleteAllSubjects();
		
		return ResponseEntity.ok("Subjects deleted sucessfully");
	}

}
