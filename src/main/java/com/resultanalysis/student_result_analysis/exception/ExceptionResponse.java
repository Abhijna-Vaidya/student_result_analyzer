package com.resultanalysis.student_result_analysis.exception;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private LocalDate timestamp;
    private String message;
    private String details;
    private String httpCodeMessage;
}