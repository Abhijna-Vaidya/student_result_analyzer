package com.resultanalysis.student_result_analysis.exception;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ResultAnalysisGlobalExceptionHandler extends ResponseEntityExceptionHandler{

	//handles general exceptions
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		ExceptionResponse response = new ExceptionResponse(LocalDate.now(), ex.getMessage(),
				request.getDescription(false), "Internal Server Error");
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	//handles empty CSV file exception
	@ExceptionHandler(EmptyFileException.class)
	public final ResponseEntity<?> handleEmptyFileExceptions(EmptyFileException emptyFile, WebRequest request) {
		ExceptionResponse response = new ExceptionResponse(LocalDate.now(), emptyFile.getMessage(),
				request.getDescription(false), "Bad Request");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	//handles CSV file exception
	@ExceptionHandler(IllegalArgumentException.class)
	public final ResponseEntity<Object> handleIllegalArgumentExceptions(IllegalArgumentException illArg, WebRequest request) {
		ExceptionResponse response = new ExceptionResponse(LocalDate.now(), illArg.getMessage(),
				request.getDescription(false), "Bad Request");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	//handles Subject not found exception
	@ExceptionHandler(SubjectNotfoundException.class)
	public final ResponseEntity<Object> handleSubjectNotfoundException(SubjectNotfoundException subject, WebRequest request) {
		ExceptionResponse response = new ExceptionResponse(LocalDate.now(), subject.getMessage(),
				request.getDescription(false), "Not Found");
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	//handles User Already Registered exception
	@ExceptionHandler(UserAlreadyRegisteredException.class)
	public final ResponseEntity<Object> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException user, WebRequest request) {
		ExceptionResponse response = new ExceptionResponse(LocalDate.now(), user.getMessage(),
				request.getDescription(false), "Already exists");
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}
	
	//handles IOException
	@ExceptionHandler(FileProcessingException.class)
	public final ResponseEntity<Object> handleFileProcessingException(FileProcessingException file, WebRequest request) {
		ExceptionResponse response = new ExceptionResponse(LocalDate.now(), file.getMessage(),
				request.getDescription(false), "UNPROCESSABLE CONTENT");
		return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_CONTENT);
	}
	
	@ExceptionHandler(StudentNotFoundException.class)
	public final ResponseEntity<Object> handleStudentNotFoundException(StudentNotFoundException student, WebRequest request) {
		ExceptionResponse response = new ExceptionResponse(LocalDate.now(), student.getMessage(),
				request.getDescription(false), "Not Found");
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(InvalidResultSheetException.class)
	public final ResponseEntity<Object> handleInvalidResultSheetException(InvalidResultSheetException resSheet, WebRequest request) {
		ExceptionResponse response = new ExceptionResponse(LocalDate.now(), resSheet.getMessage(),
				request.getDescription(false), "Bad Request");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(DuplicateResultSheetException.class)
	public final ResponseEntity<Object> handleInvalidResultSheetException(DuplicateResultSheetException resSheet, WebRequest request) {
	ExceptionResponse response = new ExceptionResponse(LocalDate.now(), resSheet.getMessage(),
			request.getDescription(false), "Conflict");
	return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}
}
