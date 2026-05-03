package com.resultanalysis.student_result_analysis.pojo;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.context.properties.bind.DefaultValue;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Subjects {
	
	@Id
	private String code;
	
	@NotBlank
	private String Name;
	
	@NotNull
    @Min(0)
    @Max(4)
	private Integer Credits;
	
	@NotNull
	@Min(0)
	@Max(100)
	private Integer maxMarks;
	
}
