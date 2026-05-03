package com.resultanalysis.student_result_analysis.pojo;

import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class StudentSubjectMarksId {
	
	private String studentUsn;
    private String subjectCode;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentSubjectMarksId)) return false;
        StudentSubjectMarksId that = (StudentSubjectMarksId) o;
        return Objects.equals(studentUsn, that.studentUsn) &&
               Objects.equals(subjectCode, that.subjectCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentUsn, subjectCode);
    }

}
