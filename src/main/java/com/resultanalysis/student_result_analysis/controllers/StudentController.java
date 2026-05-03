package com.resultanalysis.student_result_analysis.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.resultanalysis.student_result_analysis.exception.EmptyFileException;
import com.resultanalysis.student_result_analysis.serviceImpl.StudentService;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/user/result_analysis")
public class StudentController {
	
	@Autowired
	private StudentService studentService;
	
	@GetMapping("/hi")
	public ResponseEntity<String> getRes() {
		System.out.println("1st request");
		return ResponseEntity.ok("Hello");
	}
	

	@PostMapping("/verify_usn")
	public ResponseEntity<String> studentLogin(@RequestBody Map<String, String> usn){
		
		if(usn==null) {
			throw new IllegalArgumentException("Please enter your USN");
		}
		
		System.out.println(usn);
		boolean checkUsn=studentService.checkUSN(usn.get("USN"));
		
		if(!checkUsn) {
			return ResponseEntity.badRequest().body("Student not found");
		}
		
		return ResponseEntity.ok("Valid Student");
		
	}
	
	@PostMapping("upload_result_sheet")
	public ResponseEntity<String> uploadResSheet(@RequestParam("file") MultipartFile resSheet){
		
		String extension = StringUtils.getFilenameExtension(resSheet.getOriginalFilename());
		
		if (extension == null || !extension.equalsIgnoreCase("pdf")) {
			throw new IllegalArgumentException("Only PDF files are allowed");
		}
		
		if (resSheet.isEmpty()) {
			throw new EmptyFileException("Please upload a PDF file");
        }
		
		studentService.extractTableFromPdf(resSheet);
		
		return ResponseEntity.ok("File uploaded successfully");
	}
}
