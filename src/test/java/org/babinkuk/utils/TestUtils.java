package org.babinkuk.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.entity.Status;
import org.babinkuk.vo.InstructorVO;

public class TestUtils {
	
	public static final Logger log = LogManager.getLogger(TestUtils.class);
	
	public static final String VALIDATION_ROLE = "validationRole";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_INSTRUCTOR = "ROLE_INSTRUCTOR";
	public static final String ROLE_STUDENT = "ROLE_STUDENT";
	public static final String ROLE_NOT_EXIST = "ROLE_NOT_EXIST";
		
	public static final String REVIEW = "test review";
	public static final String REVIEW_NEW = "new review";
	public static final String REVIEW_UPDATE = "update test review";
	
	public static final String INSTRUCTOR_FIRSTNAME = "firstNameInstr";
	public static final String INSTRUCTOR_LASTNAME = "lastNameInstr";
	public static final String INSTRUCTOR_EMAIL = "firstNameInstr@babinkuk.com";
	public static final String INSTRUCTOR_HOBBY = "test hobby";
	public static final String INSTRUCTOR_YOUTUBE = "ytb test";
	public static final Status INSTRUCTOR_STATUS = Status.ACTIVE;
	public static final Double INSTRUCTOR_SALARY = 1000.0;
	public static final String INSTRUCTOR_FILE_1 = "file1";
	public static final String INSTRUCTOR_IMAGE_1 = "image1";
	public static final String INSTRUCTOR_FILE_11 = "file11";
	public static final String INSTRUCTOR_IMAGE_11 = "image11";
	
	public static final String INSTRUCTOR_FIRSTNAME_NEW = "firstName";
	public static final String INSTRUCTOR_LASTNAME_NEW = "lastName";
	public static final String INSTRUCTOR_EMAIL_NEW = "emailAddress@babinkuk.com";
	public static final String INSTRUCTOR_HOBBY_NEW = "hobby";
	public static final String INSTRUCTOR_YOUTUBE_NEW = "youtubeChannel";
	public static final Status INSTRUCTOR_STATUS_NEW = Status.ACTIVE;
	public static final Double INSTRUCTOR_SALARY_NEW = 1500.0;
	
	public static final String INSTRUCTOR_FIRSTNAME_UPDATED = "ime";
	public static final String INSTRUCTOR_LASTNAME_UPDATED = "prezime";
	public static final String INSTRUCTOR_EMAIL_UPDATED = "email@babinkuk.com";
	public static final String INSTRUCTOR_HOBBY_UPDATED = "hobi";
	public static final String INSTRUCTOR_YOUTUBE_UPDATED = "jutub";
	public static final Status INSTRUCTOR_STATUS_UPDATED = Status.INACTIVE;
	public static final Double INSTRUCTOR_SALARY_UPDATED = 500.0;
	public static final String INSTRUCTOR_FILE_UPDATED = "file111";
	public static final String INSTRUCTOR_IMAGE_UPDATED = "image111";
	
	public static InstructorVO updateExistingInstructor(InstructorVO instructorVO) {
		log.info("updateExistingInstructor");
		
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
		log.info("createInstructor");
		
		// set id 0: this is to force a save of new item ... instead of update
		InstructorVO instructorVO = new InstructorVO(
				TestUtils.INSTRUCTOR_FIRSTNAME_NEW, 
				TestUtils.INSTRUCTOR_LASTNAME_NEW, 
				TestUtils.INSTRUCTOR_EMAIL_NEW, 
				TestUtils.INSTRUCTOR_YOUTUBE_NEW, 
				TestUtils.INSTRUCTOR_HOBBY_NEW,
				TestUtils.INSTRUCTOR_STATUS_NEW);
		instructorVO.setId(0);
		
		return instructorVO;
	}
}
