package com.resultanalysis.student_result_analysis.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.resultanalysis.student_result_analysis.exception.SubjectNotfoundException;
import com.resultanalysis.student_result_analysis.pojo.Subjects;
import com.resultanalysis.student_result_analysis.repository.SubjectsRepository;

import jakarta.transaction.Transactional;


@Service
public class SubjectService {
	
	@Autowired
	private SubjectsRepository subjectsRepository;
	
	@Transactional
	public void addSubjects(List<Subjects> subject) {
		// TODO Auto-generated method stub
		subjectsRepository.saveAll(subject);
	}


	@Transactional
	public void deleteSubjectByID(String subjectId) {
		// TODO Auto-generated method stub
		
		Subjects subject=subjectsRepository.findById(subjectId)
				.orElseThrow(()->new SubjectNotfoundException(subjectId+" subject not found"));
		
		subjectsRepository.deleteById(subjectId);
		
	}


	@Transactional
	public void deleteAllSubjects() {
		// TODO Auto-generated method stub
		subjectsRepository.deleteAll();
		
	}


//	@Transactional
//	public void editSubject(String subjectId) {
//		// TODO Auto-generated method stub
//		Subjects subject=subjectsRepository.findById(subjectId).orElseThrow();
//		
//		
//		
//	}

}
