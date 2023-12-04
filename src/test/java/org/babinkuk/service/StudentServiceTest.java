package org.babinkuk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.validator.ActionType;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
import org.babinkuk.vo.StudentVO;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;

import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StudentServiceTest {
	
	public static final Logger log = LogManager.getLogger(StudentServiceTest.class);
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private CourseService courseService;
	
	@Value("${sql.script.review.insert}")
	private String sqlAddReview;
	
	@Value("${sql.script.review.delete}")
	private String sqlDeleteReview;
	
	@Value("${sql.script.course.insert}")
	private String sqlAddCourse;
	
	@Value("${sql.script.course.delete}")
	private String sqlDeleteCourse;
	
	@Value("${sql.script.user.insert-instructor}")
	private String sqlAddUserInstructor;
	
	@Value("${sql.script.user.insert-student}")
	private String sqlAddUserStudent;
	
	@Value("${sql.script.user.delete}")
	private String sqlDeleteUser;
	
	@Value("${sql.script.instructor.insert}")
	private String sqlAddInstructor;
	
	@Value("${sql.script.instructor.delete}")
	private String sqlDeleteInstructor;
	
	@Value("${sql.script.instructor-detail.insert}")
	private String sqlAddInstructorDetail;
	
	@Value("${sql.script.instructor-detail.delete}")
	private String sqlDeleteInstructorDetail;
	
	@Value("${sql.script.student.insert}")
	private String sqlAddStudent;
	
	@Value("${sql.script.student.delete}")
	private String sqlDeleteStudent;
	
	@Value("${sql.script.course-student.insert}")
	private String sqlAddCourseStudent;
	
	@Value("${sql.script.course-student.delete}")
	private String sqlDeleteCourseStudent;
	
	@Value("${sql.script.image.insert-instructor-1}")
	private String sqlAddImageInstructor1;
	
	@Value("${sql.script.image.insert-instructor-2}")
	private String sqlAddImageInstructor2;
	
	@Value("${sql.script.image.insert-student-1}")
	private String sqlAddImageStudent1;
	
	@Value("${sql.script.image.insert-student-2}")
	private String sqlAddImageStudent2;
	
	@Value("${sql.script.image.delete}")
	private String sqlDeleteImage;
	
	@BeforeEach
    public void setupDatabase() {

		jdbc.execute(sqlAddInstructorDetail);
		jdbc.execute(sqlAddUserInstructor);
		jdbc.execute(sqlAddInstructor);
		jdbc.execute(sqlAddImageInstructor1);
		jdbc.execute(sqlAddImageInstructor2);
		jdbc.execute(sqlAddCourse);
		jdbc.execute(sqlAddReview);
		jdbc.execute(sqlAddUserStudent);
		jdbc.execute(sqlAddStudent);
		jdbc.execute(sqlAddImageStudent1);
		jdbc.execute(sqlAddImageStudent2);
		jdbc.execute(sqlAddCourseStudent);
	}
	
	@AfterEach
	public void setupAfterTransaction() {

		jdbc.execute(sqlDeleteCourseStudent);
		jdbc.execute(sqlDeleteStudent);
		jdbc.execute(sqlDeleteReview);
		jdbc.execute(sqlDeleteCourse);
		jdbc.execute(sqlDeleteInstructor);
		jdbc.execute(sqlDeleteInstructorDetail);
		jdbc.execute(sqlDeleteImage);
		jdbc.execute(sqlDeleteUser);
		
//		// check
//		List<Map<String,Object>> userList = new ArrayList<Map<String,Object>>();
//		userList = jdbc.queryForList("select * from user");
//		log.info("size() " + userList.size());
//		for (Map m : userList) {
//			m.forEach((key, value) -> log.info(key + " : " + value));
//		}
	}
	
	@Test
	void getAllStudents() {
		
		// get all students
		Iterable<StudentVO> students = studentService.getAllStudents();
		
		// assert
		if (students instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) students).size(), "students size not 1");
		}
		
		// add another student
		StudentVO studentVO = ApplicationTestUtils.createStudent();
		
		studentService.saveStudent(studentVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		students = studentService.getAllStudents();
		
		// assert
		if (students instanceof Collection<?>) {
			assertEquals(2, ((Collection<?>) students).size(), "students size not 2 after insert");
		}
	}
	
	@Test
	void getStudentById() {
		
		// get student id=2
		StudentVO studentVO = studentService.findById(2);
		
		// assert
		validateExistingStudent(studentVO);
		
		// assert not existing student
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			studentService.findById(11);
		});
				
		String expectedMessage = "Student with id=11 not found.";
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void getStudentByEmail() {
		
		// get student
		StudentVO studentVO  = studentService.findByEmail(STUDENT_EMAIL);

		validateExistingStudent(studentVO);
		
		// get non existing student
		studentVO  = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		// assert
		assertNull(studentVO, "studentVO null");
	}
	
	@Test
	void addStudent() {
		
		// first create student
		// set id 0: this is to force a save of new item ... instead of update
		StudentVO studentVO = ApplicationTestUtils.createStudent();
		
		studentService.saveStudent(studentVO);

		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();

		studentVO = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		// assert
		validateNewStudent(studentVO);
	}
	
	@Test
	void updateStudent() {
		
		// get student
		StudentVO studentVO = studentService.findById(2);
		
		// update with new data
		studentVO = ApplicationTestUtils.updateExistingStudent(studentVO);
		
		studentService.saveStudent(studentVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again
		studentVO = studentService.findById(2);
		
		// assert
		validateUpdatedStudent(studentVO);
	}
	
	@Test
	void deleteStudent() {
		
		// get student
		StudentVO studentVO = studentService.findById(2);
		
		// assert
		assertNotNull(studentVO, "return true");
		assertEquals(2, studentVO.getId());
		
		// delete
		studentService.deleteStudent(2);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// assert not existing student
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			studentService.findById(studentVO.getId());
		});
				
		String expectedMessage = "Student with id=" + studentVO.getId() + " not found.";
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));

		// delete not existing student
		exception = assertThrows(EmptyResultDataAccessException.class, () -> {
			studentService.deleteStudent(22);
		});
	}
	
	/**
	 * testing scenario
	 * 1. create new student
	 * 2. create new course
	 * 3. associate both students with both courses
	 * 4. withdraw both students from both courses
	 * 5. associate non existing student
	 * 6. associate non existing course 
	 */
	@Test
	void setCourse() {
		
		// check existing student
		StudentVO studentVO = studentService.findById(2);
		
		validateExistingStudent(studentVO);
		
		// check existing course
		CourseVO courseVO = courseService.findById(1);
		
		// assert course
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.courseVO() null");
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() not null");
		assertEquals(COURSE, courseVO.getTitle(),"courseVO.getTitle() NOK");
		assertNotNull(courseVO.getStudentsVO(),"courseVO.getStudentsVO() null");
		assertEquals(1, courseVO.getStudentsVO().size(), "courseVO.getStudentsVO() size not 1");
		assertTrue(courseVO.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
		));
		
		// 1. create new student
		StudentVO studentVO2 = ApplicationTestUtils.createStudent();
		
		studentService.saveStudent(studentVO2);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		studentVO2 = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		// assert
		validateNewStudent(studentVO2);
		//assertEquals(2, studentVO2.getId());
		assertEquals(STUDENT_FIRSTNAME_NEW, studentVO2.getFirstName(),"getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_NEW, studentVO2.getLastName(),"getLastName() NOK");
		assertEquals(STUDENT_EMAIL_NEW, studentVO2.getEmail(),"getEmail() NOK");
		
		// 2. create course
		CourseVO courseVO2 = ApplicationTestUtils.createCourse();
		
		courseService.saveCourse(courseVO2);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		courseVO2 = courseService.findByTitle(COURSE_NEW);
		
		// assert new course
		//assertEquals(2, courseVO2.getId());
		assertNotNull(courseVO2,"courseVO2 null");
		assertEquals(COURSE_NEW, courseVO2.getTitle(),"courseVO.getTitle() NOK");
		
		// 3. associate both students with both courses
		// set old course id=1 for new student
		studentService.setCourse(studentVO2, courseVO, ActionType.ENROLL);
		// set new course id=2 for new student
		studentService.setCourse(studentVO2, courseVO2, ActionType.ENROLL);
		// set new course id=2 for old student
		studentService.setCourse(studentVO, courseVO2, ActionType.ENROLL);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again old student
		studentVO = studentService.findById(2);
		
		// assert
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME, studentVO.getLastName(),"getLastName() NOK");
		assertEquals(STUDENT_EMAIL, studentVO.getEmail(),"getEmail() NOK");
		assertEquals(2, studentVO.getCoursesVO().size(), "getCourses size not 2");
		assertTrue(studentVO.getCoursesVO().stream().anyMatch(course ->
			course.getTitle().equals(COURSE) && course.getId() == 1
		));
		assertTrue(studentVO.getCoursesVO().stream().anyMatch(course ->
			course.getTitle().equals(COURSE_NEW)// && course.getId() == 2
		));
		
		// fetch again new student
		studentVO2 = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		// assert
		validateNewStudent(studentVO2);
		assertEquals(2, studentVO2.getCoursesVO().size(), "getCourses size not 2");
		assertTrue(studentVO2.getCoursesVO().stream().anyMatch(course ->
			course.getTitle().equals(COURSE) && course.getId() == 1
		));
		assertTrue(studentVO2.getCoursesVO().stream().anyMatch(course ->
			course.getTitle().equals(COURSE_NEW)// && course.getId() == 2
		));
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again courses
		courseVO = courseService.findById(1);
		courseVO2 = courseService.findByTitle(COURSE_NEW);
		
		// assert old course
		assertEquals(COURSE, courseVO.getTitle(),"getTitle() NOK");
		assertNotNull(courseVO.getStudentsVO(),"getStudentsVO() null");
		assertEquals(2, courseVO.getStudentsVO().size(), "getStudentsVO() size not 2");
		assertTrue(courseVO.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
		));
		assertTrue(courseVO.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME_NEW) && student.getLastName().equals(STUDENT_LASTNAME_NEW)
		));
		
		// assert new course
		assertEquals(COURSE_NEW, courseVO2.getTitle(),"getTitle() NOK");
		assertNotNull(courseVO2.getStudentsVO(),"getStudentsVO() null");
		assertEquals(2, courseVO2.getStudentsVO().size(), "getStudentsVO() size not 2");
		assertTrue(courseVO2.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
		));
		assertTrue(courseVO2.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME_NEW) && student.getLastName().equals(STUDENT_LASTNAME_NEW)
		));
		
		// withdraw both students from both courses
		studentService.setCourse(studentVO2, courseVO2, ActionType.WITHDRAW);
		studentService.setCourse(studentVO2, courseVO, ActionType.WITHDRAW);
		studentService.setCourse(studentVO, courseVO2, ActionType.WITHDRAW);
		studentService.setCourse(studentVO, courseVO, ActionType.WITHDRAW);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again old student
		studentVO = studentService.findById(2);
		
		// assert
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME, studentVO.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(STUDENT_EMAIL, studentVO.getEmail(),"studentVO.getEmail() NOK");
		assertEquals(0, studentVO.getCoursesVO().size(), "studentVO.getCourses size not 0");
		
		// fetch again new student
		studentVO2 = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		// assert
		assertEquals(STUDENT_FIRSTNAME_NEW, studentVO2.getFirstName(),"studentVO2.getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_NEW, studentVO2.getLastName(),"studentVO2.getLastName() NOK");
		assertEquals(STUDENT_EMAIL_NEW, studentVO2.getEmail(),"studentVO2.getEmail() NOK");
		assertEquals(0, studentVO2.getCoursesVO().size(), "studentVO2.getCourses size not 0");
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again courses
		courseVO = courseService.findById(1);
		courseVO2 = courseService.findByTitle(COURSE_NEW);
		
		// assert old course
		assertEquals(COURSE, courseVO.getTitle(),"courseVO.getTitle() NOK");
		assertNotNull(courseVO.getStudentsVO(),"courseVO.getStudentsVO() null");
		assertEquals(0, courseVO.getStudentsVO().size(), "courseVO.getStudentsVO() size not 0");
		
		// assert new course
		assertEquals(COURSE_NEW, courseVO2.getTitle(),"courseVO2.getTitle() NOK");
		assertNotNull(courseVO2.getStudentsVO(),"courseVO2.getStudentsVO() null");
		assertEquals(0, courseVO2.getStudentsVO().size(), "courseVO2.getStudentsVO() size not 0");
		
		// not mandatory
		// 5. associate non existing student
		StudentVO nonExistingStudent = new StudentVO("firstName", "lastName", "email");
		nonExistingStudent.setId(33);
		
		// for avoiding Local variable instructorVO defined in an enclosing scope must be final or effectively final
		final CourseVO finalCourseVO = courseVO;
		
		// assert not existing course
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			studentService.setCourse(nonExistingStudent, finalCourseVO, ActionType.ENROLL);
		});
		
		String expectedMessage = "Student with id=33 not found.";
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));

		// 6. associate non existing course
		CourseVO nonExistingCourseVO = new CourseVO("non existing course");
		nonExistingCourseVO.setId(3);
		
		// for avoiding Local variable instructorVO defined in an enclosing scope must be final or effectively final
		final StudentVO finalStudentVO = studentVO;
		
		// assert not existing course
		exception = assertThrows(ObjectNotFoundException.class, () -> {
			studentService.setCourse(finalStudentVO, nonExistingCourseVO, ActionType.ENROLL);
		});
		
		expectedMessage = "Course with id=3 not found.";
		actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));	    
	}
	
	private void validateExistingStudent(StudentVO studentVO) {
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(2, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertNotNull(studentVO.getLastName(),"studentVO.getLastName() null");
		assertNotNull(studentVO.getEmail(),"studentVO.getEmail() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME, studentVO.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(STUDENT_EMAIL, studentVO.getEmail(),"studentVO.getEmail() NOK");
		assertEquals(STUDENT_STREET, studentVO.getStreet(),"studentVO.getStreet() NOK");
		assertEquals(STUDENT_CITY, studentVO.getCity(),"studentVO.getCity() NOK");
		assertEquals(STUDENT_ZIPCODE, studentVO.getZipCode(),"studentVO.getZipCode() NOK");
		assertEquals(STUDENT_STATUS, studentVO.getStatus(),"studentVO.getStatus() NOK");
		assertEquals(2, studentVO.getImages().size(), "studentVO.getImages size not 2");
		assertThat(studentVO.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_2, STUDENT_IMAGE_2));
		assertThat(studentVO.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_22, STUDENT_IMAGE_22));
		assertEquals(1, studentVO.getCoursesVO().size(), "studentVO.getCourses size not 1");
//		assertThat(studentVO.getCoursesVO(), contains(
//		    hasProperty("id", is(1))
//		));
//		assertThat(studentVO.getCoursesVO(), contains(
//			hasProperty("title", is("test course"))
//		));
		assertTrue(studentVO.getCoursesVO().stream().anyMatch(course ->
			course.getTitle().equals(COURSE) && course.getId() == 1
		));
		
		// not neccessary
		assertNotEquals("test", studentVO.getFirstName(),"studentVO.getFirstName() NOK");
	}
	
	private void validateNewStudent(StudentVO studentVO) {
		
		assertNotNull(studentVO,"studentVO null");
		//assertEquals(2, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"getFirstName() null");
		assertNotNull(studentVO.getLastName(),"getLastName() null");
		assertNotNull(studentVO.getEmail(),"getEmail() null");
		assertEquals(STUDENT_FIRSTNAME_NEW, studentVO.getFirstName(),"getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_NEW, studentVO.getLastName(),"getLastName() NOK");
		assertEquals(STUDENT_EMAIL_NEW, studentVO.getEmail(),"getEmail() NOK");
		assertEquals(STUDENT_STREET_NEW, studentVO.getStreet(),"getStreet() NOK");
		assertEquals(STUDENT_CITY_NEW, studentVO.getCity(),"getCity() NOK");
		assertEquals(STUDENT_ZIPCODE_NEW, studentVO.getZipCode(),"getZipCode() NOK");
		assertEquals(STUDENT_STATUS_NEW, studentVO.getStatus(),"getStatus() NOK");
		assertEquals(1, studentVO.getImages().size(), "getImages size not 2");
		assertThat(studentVO.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_NEW, STUDENT_IMAGE_NEW));
//		assertEquals(1, studentVO.getCoursesVO().size(), "studentVO.getCourses size not 1");
//		assertThat(studentVO.getCoursesVO(), contains(
//		    hasProperty("id", is(1))
//		));
//		assertThat(studentVO.getCoursesVO(), contains(
//			hasProperty("title", is("test course"))
//		));
//		assertTrue(studentVO.getCoursesVO().stream().anyMatch(course ->
//			course.getTitle().equals(COURSE) && course.getId() == 1
//		));
		
		// not neccessary
		assertNotEquals("test", studentVO.getFirstName(),"getFirstName() NOK");
	}
	
	private void validateUpdatedStudent(StudentVO studentVO) {
		
		assertNotNull(studentVO,"studentVO null");
		assertNotNull(studentVO.getFirstName(),"getFirstName() null");
		assertNotNull(studentVO.getLastName(),"getLastName() null");
		assertNotNull(studentVO.getEmail(),"getEmail() null");
		assertEquals(2, studentVO.getId());
		assertEquals(STUDENT_FIRSTNAME_UPDATED, studentVO.getFirstName(),"getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_UPDATED, studentVO.getLastName(),"getLastName() NOK");
		assertEquals(STUDENT_EMAIL_UPDATED, studentVO.getEmail(),"getEmailAddress() NOK");
		assertEquals(STUDENT_STREET_UPDATED, studentVO.getStreet(),"getStreet() NOK");
		assertEquals(STUDENT_CITY_UPDATED, studentVO.getCity(),"getCity() NOK");
		assertEquals(STUDENT_ZIPCODE_UPDATED, studentVO.getZipCode(),"getZipCode() NOK");
		assertEquals(STUDENT_STATUS_UPDATED, studentVO.getStatus(),"getStatus() NOK");
		assertEquals(3, studentVO.getImages().size(), "studentVO.getImages size not 3");
		assertThat(studentVO.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_2, STUDENT_IMAGE_2));
		assertThat(studentVO.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_22, STUDENT_IMAGE_22));
		assertThat(studentVO.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_UPDATED, STUDENT_IMAGE_UPDATED));
		assertEquals(1, studentVO.getCoursesVO().size(), "studentVO.getCourses size not 1");		
	}
}
