package com.resultanalysis.student_result_analysis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.resultanalysis.student_result_analysis.enums.ResultStatus;
import com.resultanalysis.student_result_analysis.pojo.StudentSubjectMarks;

@Repository
public interface StudentSubjectMarksRepository extends JpaRepository<StudentSubjectMarks, String>{

	List<StudentSubjectMarks> findByIdStudentUsn(String usn);
	
	@Query("SELECT SUM(totalMarks) FROM StudentSubjectMarks m where m.student.usn= :currStudent")
	Integer countSumTotalMarksbyUSN(@Param(value = "currStudent") String currStudent);

	@Query("SELECT s.resultStatus FROM StudentSubjectMarks s WHERE s.id.studentUsn = :usn")
	List<ResultStatus> findAllByStudentUsn(@Param("usn") String usn);

}
