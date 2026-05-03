package com.resultanalysis.student_result_analysis.pojo;


import com.resultanalysis.student_result_analysis.enums.ResultStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString(exclude = "student")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentResult {
   
	@EqualsAndHashCode.Include
	@Id
    private String studentUsn;

	@Min(0)
    private Integer totalCredits;
    
    @Min(0)
    @Max(10)
    private Double sgpa;
    
    @Min(0)
    private Integer total_marks;
    
    @Min(0)
    @Max(100)
    private Double percentage;
    
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "usn")
    private Student student;
    
    @Min(0)
    private long totalCCPoints;
    
    @Enumerated(EnumType.STRING)
	private ResultStatus resultStatus;
    
    
    @PrePersist
    @PreUpdate
    private void calculatetotalCGPoints() {
    	if(sgpa!=null && totalCredits!=null) {
    		totalCCPoints=Math.round(sgpa*totalCredits);
    	}
    }
    
}
