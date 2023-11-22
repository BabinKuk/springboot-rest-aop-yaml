package org.babinkuk.utils;

import org.babinkuk.entity.Status;

public final class ApplicationTestConstants {
	
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_INSTRUCTOR = "ROLE_INSTRUCTOR";
	public static final String ROLE_STUDENT = "ROLE_STUDENT";
	public static final String ROLE_NOT_EXIST = "ROLE_NOT_EXIST";
	
	private static final String INSTRUCTOR_ENROLL = "/instructor" + "/{instructorId}" + "/enroll";
	private static final String INSTRUCTOR_WITHDRAW = "/instructor" + "/{instructorId}" + "/withdraw";
	private static final String STUDENT_ENROLL = "/student" + "/{studentId}" + "/enroll";
	private static final String STUDENT_WITHDRAW = "/student" + "/{studentId}" + "/withdraw";
		
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
	
	public static final String INSTRUCTOR_FIRSTNAME_NEW = "firstNameInstrNew";
	public static final String INSTRUCTOR_LASTNAME_NEW = "lastNameInstrNew";
	public static final String INSTRUCTOR_EMAIL_NEW = "InstrNew@babinkuk.com";
	public static final String INSTRUCTOR_HOBBY_NEW = "hobby";
	public static final String INSTRUCTOR_YOUTUBE_NEW = "youtubeChannel";
	public static final Status INSTRUCTOR_STATUS_NEW = Status.ACTIVE;
	public static final Double INSTRUCTOR_SALARY_NEW = 1500.0;
	
	public static final String INSTRUCTOR_FIRSTNAME_UPDATED = "firstNameInstrUpdate";
	public static final String INSTRUCTOR_LASTNAME_UPDATED = "lastNameInstrUpdate";
	public static final String INSTRUCTOR_EMAIL_UPDATED = "InstrUpdate@babinkuk.com";
	public static final String INSTRUCTOR_HOBBY_UPDATED = "hobi";
	public static final String INSTRUCTOR_YOUTUBE_UPDATED = "jutub";
	public static final Status INSTRUCTOR_STATUS_UPDATED = Status.INACTIVE;
	public static final Double INSTRUCTOR_SALARY_UPDATED = 500.0;
	public static final String INSTRUCTOR_FILE_UPDATED = "file111";
	public static final String INSTRUCTOR_IMAGE_UPDATED = "image111";
	
	public static final String STUDENT_FIRSTNAME = "firstNameStudent";
	public static final String STUDENT_LASTNAME = "lastNameStudent";
	public static final String STUDENT_EMAIL = "firstNameStudent@babinkuk.com";
	public static final Status STUDENT_STATUS = Status.ACTIVE;
	public static final String STUDENT_STREET = "Street";
	public static final String STUDENT_CITY = "City";
	public static final String STUDENT_ZIPCODE = "ZipCode";
	public static final String STUDENT_FILE_2 = "file2";
	public static final String STUDENT_IMAGE_2 = "image2";
	public static final String STUDENT_FILE_22 = "file22";
	public static final String STUDENT_IMAGE_22 = "image22";
	
	public static final String STUDENT_FIRSTNAME_NEW = "firstNameStudentNew";
	public static final String STUDENT_LASTNAME_NEW = "lastNameStudentNew";
	public static final String STUDENT_EMAIL_NEW = "StudentNew@babinkuk.com";
	public static final Status STUDENT_STATUS_NEW = Status.ACTIVE;
	public static final String STUDENT_STREET_NEW = "New Street";
	public static final String STUDENT_CITY_NEW = "New City";
	public static final String STUDENT_ZIPCODE_NEW = "New ZipCode";
	
	public static final String STUDENT_FIRSTNAME_UPDATED = "firstNameStudentUpdate";
	public static final String STUDENT_LASTNAME_UPDATED = "lastNameStudentUpdate";
	public static final String STUDENT_EMAIL_UPDATED = "StudentUpdate@babinkuk.com";
	public static final Status STUDENT_STATUS_UPDATED = Status.INACTIVE;
	public static final String STUDENT_STREET_UPDATED = "Update Street";
	public static final String STUDENT_CITY_UPDATED = "Update City";
	public static final String STUDENT_ZIPCODE_UPDATED = "Update ZipCode";
	public static final String STUDENT_FILE_UPDATED = "file222";
	public static final String STUDENT_IMAGE_UPDATED = "image222";
	
	public static final String COURSE = "test course";
	public static final String COURSE_NEW = "new test";
	public static final String COURSE_UPDATED = "updated test";
	
}
