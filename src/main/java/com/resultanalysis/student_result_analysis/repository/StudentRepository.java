package com.resultanalysis.student_result_analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.resultanalysis.student_result_analysis.pojo.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    boolean existsByUsn(String usn);
}
