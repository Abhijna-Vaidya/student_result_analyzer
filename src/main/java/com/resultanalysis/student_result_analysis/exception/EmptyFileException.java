package com.resultanalysis.student_result_analysis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class EmptyFileException extends RuntimeException{
	
	public EmptyFileException(String msg) {
		// TODO Auto-generated constructor stub
		super(msg);
	}

}
