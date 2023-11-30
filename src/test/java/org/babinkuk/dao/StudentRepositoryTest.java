package org.babinkuk.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.entity.Address;
import org.babinkuk.entity.Course;
import org.babinkuk.entity.Student;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ActiveProfiles("test")
@DataJpaTest
public class StudentRepositoryTest {
	
	public static final Logger log = LogManager.getLogger(StudentRepositoryTest.class);
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@PersistenceContext
	private EntityManager entityManager;
	
//	@Autowired
//	private TransactionTemplate transactionTemplate;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Value("${sql.script.review.insert}")
	private String sqlAddReview;
	
	@Value("${sql.script.review.delete}")
	private String sqlDeleteReview;
	
	@Value("${sql.script.course.insert}")
	private String sqlAddCourse;
	
	@Value("${sql.script.course.delete}")
	private String sqlDeleteCourse;
	
	@Value("${sql.script.course.update}")
	private String sqlUpdateCourse;
	
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
	
	@BeforeAll
	public static void setup() {
		
	}
	
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
		Iterable<Student> students = studentRepository.findAll();
		
		// assert
		assertNotNull(students,"students null");
		
		if (students instanceof Collection) {
			assertEquals(1, ((Collection<?>) students).size(), "students size not 1");
		}
		
		List<Student> studentList = new ArrayList<Student>();
		students.forEach(studentList::add);

		assertTrue(studentList.stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
		));
	}
	
	@Test
	void getStudentById() {
		
		// get student id=2
		Optional<Student> student = studentRepository.findById(2);
		
		// assert
		assertTrue(student.isPresent());
		validateExistingStudent(student.get());
		
		// get non-existing student id=22
		student = studentRepository.findById(22);
		
		// assert
		assertFalse(student.isPresent());
	}
	
	@Test
	void getStudentByEmail() {
		
		// get student
		Optional<Student> student = studentRepository.findByEmail(STUDENT_EMAIL);
		
		// assert
		assertTrue(student.isPresent());
		validateExistingStudent(student.get());
		
		// get non-existing student
		student = studentRepository.findByEmail(INSTRUCTOR_EMAIL_NEW);
		
		// assert
		assertFalse(student.isPresent());
	}
	
	@Test
	void updateStudent() {
		
		// get student id=2
		Optional<Student> student = studentRepository.findById(2);
		
		// assert
		assertTrue(student.isPresent());
		validateExistingStudent(student.get());
		
		// update
		// set id=2: this is to force an update of existing item
		Student updatedStudent = new Student();
		updatedStudent = updateStudent(student.get());
		
		Student savedStudent = studentRepository.save(updatedStudent);
		
		// assert
		assertNotNull(savedStudent,"savedStudent null");
		validateUpdatedStudent(savedStudent);
	}
	
	@Test
	void addStudent() {
		
		// create student
		// set id=0: this is to force a save of new item
		Student student = createStudent();
		
		Student savedStudent = studentRepository.save(student);
		
		// assert
		assertNotNull(savedStudent,"savedStudent null");
		validateNewStudent(savedStudent);
	}

	@Test
	void deleteStudent() {
		
		// set course for instructor
		jdbc.execute(sqlUpdateCourse);
		
		// check if student id=2 exists
		Optional<Student> student = studentRepository.findById(2);
		
		// assert
		assertTrue(student.isPresent());
		
		// delete student
		studentRepository.deleteById(2);
		
		student = studentRepository.findById(2);
		
		// assert
		assertFalse(student.isPresent());
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// check other cascading entities
		// get course with id=1
		Optional<Course> course = courseRepository.findById(1);
		
		// assert
		// course must be unchanged except students (null)
		assertTrue(course.isPresent());
		assertEquals(1, course.get().getId(), "course.get().getId()");
		assertEquals(COURSE, course.get().getTitle(), "course.get().getTitle()");
		assertEquals(1, course.get().getReviews().size(), "course.get().getReviews().size()");
		assertEquals(INSTRUCTOR_FIRSTNAME, course.get().getInstructor().getFirstName(), "course.get().getInstructor().getFirstName()");
		assertEquals(0, course.get().getStudents().size(), "course.get().getStudents().size()");
//		assertTrue(course.get().getStudents().stream().anyMatch(student ->
//			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
//		));
	}
	
	public static void validateExistingStudent(Student student) {
		
		assertNotNull(student,"student null");
		assertEquals(2, student.getId());
		assertNotNull(student.getFirstName(),"getFirstName() null");
		assertNotNull(student.getLastName(),"getLastName() null");
		assertNotNull(student.getEmail(),"getEmail() null");
		assertNotNull(student.getStatus(),"getStatus() null");
		assertNotNull(student.getImages(),"getImages() null");
		assertNotNull(student.getAddress().getCity(),"getCity() null");
		assertNotNull(student.getAddress().getStreet(),"getStreet() null");
		assertNotNull(student.getAddress().getZipCode(),"getZipCode() null");
		assertEquals(STUDENT_FIRSTNAME, student.getFirstName(),"getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME, student.getLastName(),"getLastName() NOK");
		assertEquals(STUDENT_EMAIL, student.getEmail(),"getEmail() NOK");
		assertEquals(STUDENT_STATUS, student.getStatus(),"getStatus() NOK");
		assertEquals(2, student.getImages().size(), "getImages size not 2");
		assertThat(student.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_2, STUDENT_IMAGE_2));
		assertThat(student.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_22, STUDENT_IMAGE_22));
		assertEquals(STUDENT_STREET, student.getAddress().getStreet(),"getStreet() NOK");
		assertEquals(STUDENT_CITY, student.getAddress().getCity(),"getCity() NOK");
		assertEquals(STUDENT_ZIPCODE, student.getAddress().getZipCode(),"getZipCode() NOK");
	}
	
	private void validateUpdatedStudent(Student student) {
		
		assertNotNull(student,"student null");
		assertEquals(2, student.getId());
		assertNotNull(student.getFirstName(),"getFirstName() null");
		assertNotNull(student.getLastName(),"getLastName() null");
		assertNotNull(student.getEmail(),"getEmail() null");
		assertNotNull(student.getStatus(),"getStatus() null");
		assertNotNull(student.getImages(),"getImages() null");
		assertNotNull(student.getAddress().getCity(),"getCity() null");
		assertNotNull(student.getAddress().getStreet(),"getStreet() null");
		assertNotNull(student.getAddress().getZipCode(),"getZipCode() null");
		assertEquals(STUDENT_FIRSTNAME_UPDATED, student.getFirstName(),"getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_UPDATED, student.getLastName(),"getLastName() NOK");
		assertEquals(STUDENT_EMAIL_UPDATED, student.getEmail(),"getEmail() NOK");
		assertEquals(STUDENT_STATUS_UPDATED, student.getStatus(),"getStatus() NOK");
		assertEquals(3, student.getImages().size(), "getImages size not 2");
		assertThat(student.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_2, STUDENT_IMAGE_2));
		assertThat(student.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_22, STUDENT_IMAGE_22));
		assertThat(student.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_UPDATED, STUDENT_IMAGE_UPDATED));
		assertEquals(STUDENT_STREET_UPDATED, student.getAddress().getStreet(),"getStreet() NOK");
		assertEquals(STUDENT_CITY_UPDATED, student.getAddress().getCity(),"getCity() NOK");
		assertEquals(STUDENT_ZIPCODE_UPDATED, student.getAddress().getZipCode(),"getZipCode() NOK");
	}
	
	private void validateNewStudent(Student student) {
		
		assertNotNull(student,"student null");
		//assertEquals(1, student.getId());
		assertNotNull(student.getFirstName(),"getFirstName() null");
		assertNotNull(student.getLastName(),"getLastName() null");
		assertNotNull(student.getEmail(),"getEmail() null");
		assertNotNull(student.getStatus(),"getStatus() null");
		assertNotNull(student.getImages(),"getImages() null");
		assertNotNull(student.getAddress().getCity(),"getCity() null");
		assertNotNull(student.getAddress().getStreet(),"getStreet() null");
		assertNotNull(student.getAddress().getZipCode(),"getZipCode() null");
		assertEquals(STUDENT_FIRSTNAME_NEW, student.getFirstName(),"getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_NEW, student.getLastName(),"getLastName() NOK");
		assertEquals(STUDENT_EMAIL_NEW, student.getEmail(),"getEmail() NOK");
		assertEquals(STUDENT_STATUS_NEW, student.getStatus(),"getStatus() NOK");
		assertEquals(1, student.getImages().size(), "getImages size not 1");
		assertThat(student.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_NEW, STUDENT_IMAGE_NEW));
		assertEquals(STUDENT_STREET_NEW, student.getAddress().getStreet(),"getStreet() NOK");
		assertEquals(STUDENT_CITY_NEW, student.getAddress().getCity(),"getCity() NOK");
		assertEquals(STUDENT_ZIPCODE_NEW, student.getAddress().getZipCode(),"getZipCode() NOK");
	}
	
	private Student updateStudent(Student student) {
		
		Address address = new Address();
		address.setStreet(STUDENT_STREET_UPDATED);
		address.setCity(STUDENT_CITY_UPDATED);
		address.setZipCode(STUDENT_ZIPCODE_UPDATED);
				
		// update with new data
		student.setFirstName(STUDENT_FIRSTNAME_UPDATED);
		student.setLastName(STUDENT_LASTNAME_UPDATED);
		student.setEmail(STUDENT_EMAIL_UPDATED);
		student.setStatus(STUDENT_STATUS_UPDATED);
		student.getImages().put(STUDENT_FILE_UPDATED, STUDENT_IMAGE_UPDATED);
		student.setAddress(address);
		
		return student;
	}
	
	private Student createStudent() {
		
		// set id 0: this is to force a save of new item ... instead of update
		Student student = new Student(
				STUDENT_FIRSTNAME_NEW, 
				STUDENT_LASTNAME_NEW, 
				STUDENT_EMAIL_NEW, 
				STUDENT_STATUS_NEW);
		student.setId(0);
		
		Address address = new Address();
		address.setStreet(STUDENT_STREET_NEW);
		address.setCity(STUDENT_CITY_NEW);
		address.setZipCode(STUDENT_ZIPCODE_NEW);
		
		SortedMap<String, String> images = new TreeMap<String, String>();
		images.put(STUDENT_FILE_NEW, STUDENT_IMAGE_NEW);
		
		student.setImages(images);
		student.setAddress(address);
		
		return student;
	}
}