package com.resultanalysis.student_result_analysis.pojo;

import com.resultanalysis.student_result_analysis.enums.ResultStatus;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "student")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Setter
@Getter
@Builder
@Entity
public class StudentSubjectMarks {
	
	@EqualsAndHashCode.Include
	@EmbeddedId
    private StudentSubjectMarksId id;
	
	@NotNull
	@Min(0)
	@Max(100)
	private Integer internalMarks;
	
	@NotNull
	@Min(0)
	@Max(100)
	private Integer externalMarks;
	
	@NotNull
	@Min(0)
	@Max(100)
	private Integer totalMarks;
	
	@NotNull
	@Min(0)
	private Integer credits_gradepoint;
	
	@NotNull
	@Min(1)
	@Max(10)
	private Integer gradePoints;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("studentUsn") // maps this field to composite key
	@JoinColumn(name="student_usn",nullable = false)
	private Student student;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("subjectCode") // maps this field to composite key
	@JoinColumn(name="subject_code",nullable = false)
	private Subjects subjects;
	
	@Enumerated(EnumType.STRING)
	private ResultStatus resultStatus;
	
	
	@PrePersist
	@PreUpdate
	public void calculateResult() {
		
		//Calculating totalMarks
		if (internalMarks != null && externalMarks != null) {
            totalMarks = internalMarks + externalMarks;
        }
		
		// Calculating grade points.
	    if (totalMarks != null) {
	        if (totalMarks >= 90) gradePoints = 10;
	        else if (totalMarks >= 80) gradePoints = 9;
	        else if (totalMarks >= 70) gradePoints = 8;
	        else if (totalMarks >= 60) gradePoints = 7;
	        else if (totalMarks >= 55) gradePoints = 6;
	        else if (totalMarks >= 50) gradePoints = 5;
	        else if (totalMarks >= 40) gradePoints = 4;
	        else gradePoints = 0;
	    }
	    
        credits_gradepoint=gradePoints*subjects.getCredits();
	    
	}

}
