package com.resultanalysis.student_result_analysis.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.resultanalysis.student_result_analysis.enums.ResultStatus;
import com.resultanalysis.student_result_analysis.exception.FileProcessingException;
import com.resultanalysis.student_result_analysis.exception.SubjectNotfoundException;
import com.resultanalysis.student_result_analysis.pojo.Student;
import com.resultanalysis.student_result_analysis.pojo.StudentResult;
import com.resultanalysis.student_result_analysis.pojo.StudentSubjectMarks;
import com.resultanalysis.student_result_analysis.pojo.StudentSubjectMarksId;
import com.resultanalysis.student_result_analysis.pojo.Subjects;
import com.resultanalysis.student_result_analysis.repository.StudentResultRepository;
import com.resultanalysis.student_result_analysis.repository.StudentSubjectMarksRepository;
import com.resultanalysis.student_result_analysis.repository.SubjectsRepository;

import jakarta.transaction.Transactional;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

@Service
public class AdminResultService {
	
	@Autowired
	private SubjectsRepository subjectsRepository;
	
	@Autowired
	private StudentResultRepository studentResultRepository;
	
	@Autowired
	private StudentSubjectMarksRepository studentSubjectMarksRepository;
	
	public void extractTableFromPdf(String path) {

	    File[] files = new File(path).listFiles((d, n) -> n.endsWith(".pdf"));

	    for (File file : files) {
	        try (InputStream is = new FileInputStream(file)) {

	            extractTableFromPdf(is);

//	            if (studentRepository.existsById(student.getUsn())) {
//	                System.out.println("Skipping: " + student.getUsn());
//	                continue;
//	            }

//	            resultService.saveFinalResult(student);

	        } catch (Exception e) {
	            System.err.println("Failed: " + file.getName());
	        }
	    }
	}
	
	@Transactional
	private void extractTableFromPdf(InputStream is) {
		// TODO Auto-generated method stub

		try {
			
			Student student=null;
			
			List<StudentSubjectMarks> list = new ArrayList<>();
			
			
			PDDocument document = PDDocument.load(is);

			ObjectExtractor extractor = new ObjectExtractor(document);
			SpreadsheetExtractionAlgorithm algorithm = new SpreadsheetExtractionAlgorithm();

			int totalPages = document.getNumberOfPages();
			

			for (int page = 1; page <= totalPages; page++) {

				Page pdfPage = extractor.extract(page);
				List<Table> tables = algorithm.extract(pdfPage);

				for (Table table : tables) {
					
					if ((table.getRows().getFirst().getFirst().getText()).equals("University Seat Number")) {

							student=Student.builder()
							.usn(table.getRows().getFirst().get(1).getText().substring(1).trim())
							.studentName(table.getRows().get(1).get(0).getText())
							.build();
							

					}

					if (table.getRows().get(0).get(0).getText().contains("Code")) {

						List<List<RectangularTextContainer>> rows = table.getRows();

						for (List<RectangularTextContainer> cells : rows) {

							List<String> rowValues = new ArrayList<>();

							for (RectangularTextContainer cell : cells) {
								rowValues.add(cell.getText());
							}

							rowValues.remove(1);      //exclude subject_name
							rowValues.remove(3);      //exclude total_marks
							rowValues.removeLast();   //exclude result_announced date
							
							if(rowValues.get(0).contains("Code")) { // ignoring header row
								continue;
							}else {
								System.out.println(rowValues);
								StudentSubjectMarksId id = StudentSubjectMarksId.builder()
										.studentUsn(student.getUsn())
										.subjectCode(rowValues.get(0))
										.build();
								
								System.out.println(id);
								
								Subjects subject = subjectsRepository
								        .findById(rowValues.get(0))
								        .orElseThrow(() -> new SubjectNotfoundException("Subject not found"));

								StudentSubjectMarks marks = StudentSubjectMarks.builder()
								        .id(id)
								        .student(student)
								        .subjects(subject)
								        .internalMarks(Integer.parseInt(rowValues.get(1)))
								        .externalMarks(Integer.parseInt(rowValues.get(2)))
								        .resultStatus(ResultStatus.valueOf(rowValues.get(3)))
								        .build();

								System.out.println(marks);
								
								list.add(marks);
							}
						}
						studentSubjectMarksRepository.saveAll(list);
						student.setStudentSubjectMarks(list);
					}
				}
			}

			extractor.close();
			document.close();
			
			
			List<Subjects> subjects=subjectsRepository.findAll();
			
			System.out.println(subjects);
			
			Integer totalCredits=subjects.stream()
					.map(subject -> subject.getCredits())
					.reduce(0, Integer::sum);
			
			Double SGPA=Math.round(caluculateSGPA(student,subjects,totalCredits)*100.00)/100.00;
			
			Integer sumTotalMarks=calculateSumTotalMarks(student.getUsn());
			
//			Integer totalMAxMarks=subjects.stream()
//					.map(subject -> subject.getMaxMarks())
//					.reduce(0, Integer::sum);
			
//			Integer totalMAxMarks=800;
			
//			System.out.println("totalMarks:  "+totalMAxMarks);
			
//			Double percentage=sumTotalMarks*100.00/totalMAxMarks;
			
			Double percentage=Math.round((SGPA*10)*100.00)/100.00;
			
			System.out.println("percentage:  "+percentage);
			
			ResultStatus resStatus=CheckResultStatus(student.getUsn());
			
			
			StudentResult result=StudentResult.builder()
					.student(student)
					.sgpa(SGPA)
					.totalCredits(totalCredits)
					.percentage(percentage)
					.total_marks(sumTotalMarks)
					.resultStatus(resStatus)
					.build();
			
			studentResultRepository.save(result);
			
		} catch (IOException e) {
			throw new FileProcessingException("Corrupted resultSheet");
		}
	}
	
	private ResultStatus CheckResultStatus(String currStudent) {
		// TODO Auto-generated method stub
		ResultStatus resStatus=ResultStatus.P;
		List<ResultStatus> list=new ArrayList<>();
		
		list=studentSubjectMarksRepository.findAllByStudentUsn(currStudent);
		
		System.out.println(list);
		
		for(ResultStatus res:list) {
			if(res.equals(ResultStatus.F)) {
				resStatus=ResultStatus.F;
				break;
			}
		}
		return resStatus;
	}



	private Integer calculateSumTotalMarks(String currStudent) {
		// TODO Auto-generated method stub
		return studentSubjectMarksRepository.countSumTotalMarksbyUSN(currStudent);
		
	}



	private double caluculateSGPA(Student student, List<Subjects> subjects, Integer totalCredits) {
		
		List<StudentSubjectMarks> studentMarks=studentSubjectMarksRepository.findByIdStudentUsn(student.getUsn());
		
		double gpCreditProduct=0.0;
		
		 double SGPA;
		 for (StudentSubjectMarks mark : studentMarks) {
			    for (Subjects subject : subjects) {
			        if (subject.getCode().equals(mark.getId().getSubjectCode())) {
			            gpCreditProduct += mark.getGradePoints() * subject.getCredits();
			        }
			    }
			}
		 
		SGPA=gpCreditProduct/totalCredits;
		return SGPA;
	}

}
