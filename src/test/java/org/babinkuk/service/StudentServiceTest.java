package org.babinkuk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.entity.Status;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.vo.StudentVO;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.Matchers.contains;

import java.util.Collection;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StudentServiceTest {
	
	public static final Logger log = LogManager.getLogger(StudentServiceTest.class);
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private StudentService studentService;
	
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
		log.info("BeforeEach");

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
	
	@Test
	void getStudentById() {
		log.info("getStudentById");
		
		StudentVO studentVO = studentService.findById(2);
		
		validatePrimaryStudent(studentVO);
	}
	
	@Test
	void getStudentByEmail() {
		log.info("getStudentByEmail");
		
		StudentVO studentVO  = studentService.findByEmail("firstNameStudent@babinuk.com");

		validatePrimaryStudent(studentVO);
	}
	
	@Test
	void addStudent() {
		log.info("addStudent");
		
		// first create student
		// set id 0: this is to force a save of new item ... instead of update
		StudentVO studentVO = new StudentVO("firstName", "lastName", "emailAddress");
		studentVO.setId(0);
		
		studentService.saveStudent(studentVO);
		
		StudentVO studentVO2 = studentService.findByEmail("emailAddress");
		
		//log.info(studentVO2);

		// assert
		//assertEquals(2, studentVO2.getId());
		assertEquals(studentVO.getFirstName(), studentVO2.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(studentVO.getLastName(), studentVO2.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(studentVO.getEmail(), studentVO2.getEmail(),"studentVO.getEmail() NOK");
	}
	
	@Test
	void updateStudent() {
		log.info("updateStudent");
		
		StudentVO studentVO = studentService.findById(2);
		
		// update with new data
		String firstName = "ime";
		String lastName = "prezime";
		String email = "email";
		String street = "New Street";
		String city = "New City";
		String zipCode = "New ZipCode";
		String file = "file222";
		String image = "image222";
		Status status = Status.INACTIVE;
		
		studentVO.setFirstName(firstName);
		studentVO.setLastName(lastName);
		studentVO.setEmail(email);
		studentVO.setStreet(street);
		studentVO.setCity(city);
		studentVO.setZipCode(zipCode);
		studentVO.getImages().put(file, image);
		studentVO.setStatus(status);
		
		studentService.saveStudent(studentVO);
		
		// fetch again
		StudentVO studentVO2 = studentService.findById(2);
		
		// assert
		assertEquals(studentVO.getId(), studentVO2.getId());
		assertEquals(firstName, studentVO2.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(lastName, studentVO2.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(email, studentVO2.getEmail(),"studentVO.getEmailAddress() NOK");
		assertEquals(street, studentVO.getStreet(),"studentVO.getStreet() NOK");
		assertEquals(city, studentVO.getCity(),"studentVO.getCity() NOK");
		assertEquals(zipCode, studentVO.getZipCode(),"studentVO.getZipCode() NOK");
		assertEquals(Status.INACTIVE, studentVO.getStatus(),"studentVO.getStatus() NOK");
		assertEquals(3, studentVO.getImages().size(), "studentVO.getImages size not 2");
		assertThat(studentVO.getImages(), IsMapContaining.hasEntry("file2", "image2"));
		assertThat(studentVO.getImages(), IsMapContaining.hasEntry("file22", "image22"));
		assertThat(studentVO.getImages(), IsMapContaining.hasEntry(file, image));
		assertEquals(1, studentVO.getCoursesVO().size(), "studentVO.getCourses size not 1");
	}
	
	@Test
	void deleteStudent() {
		log.info("deleteStudent");
		
		// first get student
		StudentVO studentVO = studentService.findById(2);
		//log.info(studentVO);
		
		// assert
		assertNotNull(studentVO, "return true");
		assertEquals(2, studentVO.getId());
		
		// delete
		studentService.deleteStudent(2);
		
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
	
	@Test
	void getAllStudents() {
		log.info("getAllStudents");
		
		Iterable<StudentVO> students = studentService.getAllStudents();
		
		// assert
		if (students instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) students).size(), "students size not 1");
		}
		
		// create student
		// set id 0: this is to force a save of new item ... instead of update
		StudentVO studentVO = new StudentVO("firstName", "lastName", "emailAddress");
		studentVO.setId(0);
		
		studentService.saveStudent(studentVO);
		
		students = studentService.getAllStudents();
		
		// assert
		if (students instanceof Collection<?>) {
			assertEquals(2, ((Collection<?>) students).size(), "students size not 2 after insert");
		}
	}
	
	@AfterEach
	public void setupAfterTransaction() {
		log.info("AfterEach");

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
	
	private void validatePrimaryStudent(StudentVO studentVO) {
		//log.info(studentVO.toString());
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(2, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertNotNull(studentVO.getLastName(),"studentVO.getLastName() null");
		assertNotNull(studentVO.getEmail(),"studentVO.getEmail() null");
		assertEquals("firstNameStudent", studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals("lastNameStudent", studentVO.getLastName(),"studentVO.getLastName() NOK");
		assertEquals("firstNameStudent@babinuk.com", studentVO.getEmail(),"studentVO.getEmail() NOK");
		assertEquals("Street", studentVO.getStreet(),"studentVO.getStreet() NOK");
		assertEquals("City", studentVO.getCity(),"studentVO.getCity() NOK");
		assertEquals("ZipCode", studentVO.getZipCode(),"studentVO.getZipCode() NOK");
		assertEquals(Status.ACTIVE, studentVO.getStatus(),"studentVO.getStatus() NOK");
		assertEquals(2, studentVO.getImages().size(), "studentVO.getImages size not 2");
		assertThat(studentVO.getImages(), IsMapContaining.hasEntry("file2", "image2"));
		assertThat(studentVO.getImages(), IsMapContaining.hasEntry("file22", "image22"));
		assertEquals(1, studentVO.getCoursesVO().size(), "studentVO.getCourses size not 1");
//		assertThat(studentVO.getCoursesVO(), contains(
//		    hasProperty("id", is(1))
//		));
//		assertThat(studentVO.getCoursesVO(), contains(
//			hasProperty("title", is("test course"))
//		));
		assertTrue(studentVO.getCoursesVO().stream().anyMatch(course ->
			course.getTitle().equals("test course") && course.getId() == 1
		));
		
		// not neccessary
		assertNotEquals("test", studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		
		// assert not existing student
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			studentService.findById(22);
		});
		
		String expectedMessage = "Student with id=22 not found.";
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
}
