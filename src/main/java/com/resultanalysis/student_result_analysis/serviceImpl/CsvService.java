package com.resultanalysis.student_result_analysis.serviceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.resultanalysis.student_result_analysis.exception.FileProcessingException;
import com.resultanalysis.student_result_analysis.pojo.Student;
import com.resultanalysis.student_result_analysis.repository.StudentRepository;

import jakarta.transaction.Transactional;

@Service
public class CsvService {
	
	@Autowired
    private StudentRepository studentRepository;

	@Transactional
	public void readCsv(MultipartFile file) {

		BufferedReader reader;
		try {
			reader = new BufferedReader(
					new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
		
		CSVFormat format = CSVFormat.DEFAULT.builder().setHeader() // first row as header
				.setSkipHeaderRecord(true) // skip header while reading data
				.setTrim(true) // trim spaces
				.build();

		try (CSVParser csvParser = new CSVParser(reader, format);

		) {
			for (CSVRecord record : csvParser) {
				String usn = record.get("USN");
				String name = record.get("Name");

				System.out.println(usn + " " + name);

				// save to DB
				// Avoid duplicates
                if (!studentRepository.existsByUsn(usn)) {
                    Student student = new Student();
                    student.setUsn(usn);
                    student.setStudentName(name);

                    studentRepository.save(student);
                }
				
				
			}
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new FileProcessingException("Corrupted file");
		}
	}
}
