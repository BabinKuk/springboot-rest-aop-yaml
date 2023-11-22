package org.babinkuk.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.entity.Status;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
import org.babinkuk.vo.StudentVO;

import static org.babinkuk.utils.ApplicationTestConstants.*;

public class ApplicationTestUtils {
	
	public static final Logger log = LogManager.getLogger(ApplicationTestUtils.class);
	
	public static InstructorVO updateExistingInstructor(InstructorVO instructorVO) {
		//log.info("updateExistingInstructor");
		
		// update with new data
		instructorVO.setFirstName(INSTRUCTOR_FIRSTNAME_UPDATED);
		instructorVO.setLastName(INSTRUCTOR_LASTNAME_UPDATED);
		instructorVO.setEmail(INSTRUCTOR_EMAIL_UPDATED);
		instructorVO.setYoutubeChannel(INSTRUCTOR_YOUTUBE_UPDATED);
		instructorVO.setHobby(INSTRUCTOR_HOBBY_UPDATED);
		instructorVO.setSalary(INSTRUCTOR_SALARY_UPDATED);
		instructorVO.setStatus(INSTRUCTOR_STATUS_UPDATED);
		instructorVO.getImages().put(INSTRUCTOR_FILE_UPDATED, INSTRUCTOR_IMAGE_UPDATED);
		
		return instructorVO;
	}
	
	public static InstructorVO createInstructor() {
		//log.info("createInstructor");
		
		// set id 0: this is to force a save of new item ... instead of update
		InstructorVO instructorVO = new InstructorVO(
				INSTRUCTOR_FIRSTNAME_NEW, 
				INSTRUCTOR_LASTNAME_NEW, 
				INSTRUCTOR_EMAIL_NEW, 
				INSTRUCTOR_YOUTUBE_NEW, 
				INSTRUCTOR_HOBBY_NEW,
				INSTRUCTOR_STATUS_NEW);
		instructorVO.setId(0);
		
		return instructorVO;
	}
	
	public static StudentVO updateExistingStudent(StudentVO studentVO) {
		//log.info("updateExistingStudent");
		
		// update with new data
		studentVO.setFirstName(STUDENT_FIRSTNAME_UPDATED);
		studentVO.setLastName(STUDENT_LASTNAME_UPDATED);
		studentVO.setEmail(STUDENT_EMAIL_UPDATED);
		studentVO.setStreet(STUDENT_STREET_UPDATED);
		studentVO.setCity(STUDENT_CITY_UPDATED);
		studentVO.setZipCode(STUDENT_ZIPCODE_UPDATED);
		studentVO.setStatus(STUDENT_STATUS_UPDATED);
		studentVO.getImages().put(STUDENT_FILE_UPDATED, STUDENT_IMAGE_UPDATED);
		
		return studentVO;
	}
	
	public static StudentVO createStudent() {
		//log.info("createStudent");
		
		// set id 0: this is to force a save of new item ... instead of update
		StudentVO studentVO = new StudentVO(
				STUDENT_FIRSTNAME_NEW, 
				STUDENT_LASTNAME_NEW, 
				STUDENT_EMAIL_NEW, 
				STUDENT_STATUS_NEW);
		
		studentVO.setId(0);
		studentVO.setStreet(STUDENT_STREET_NEW);
		studentVO.setCity(STUDENT_CITY_NEW);
		studentVO.setZipCode(STUDENT_ZIPCODE_NEW);
		
		return studentVO;
	}
	
	public static CourseVO createCourse() {
		// create course
		// set id 0: this is to force a save of new item ... instead of update
		CourseVO courseVO = new CourseVO(COURSE_NEW);
		courseVO.setId(0);
		
		return courseVO;
	}
	
	public static CourseVO updateExistingCourse(CourseVO courseVO, StudentVO studentVO, InstructorVO instructorVO) {
		//log.info("updateExistingCourse");
		
		// update with new data
		courseVO.setTitle(COURSE_UPDATED);
		courseVO.setInstructorVO(instructorVO);
		courseVO.addStudentVO(studentVO);
		
		return courseVO;
	}
}
