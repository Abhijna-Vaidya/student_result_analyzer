package com.resultanalysis.student_result_analysis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.resultanalysis.student_result_analysis.pojo.StudentResult;

@Repository
public interface StudentResultRepository extends JpaRepository<StudentResult, String>{
	
	List<StudentResult> findTop10ByOrderByPercentageDesc();

}
