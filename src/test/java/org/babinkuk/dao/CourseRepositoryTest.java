package org.babinkuk.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.entity.Course;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.Review;
import org.babinkuk.entity.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ActiveProfiles("test")
@DataJpaTest
public class CourseRepositoryTest {
	
	public static final Logger log = LogManager.getLogger(CourseRepositoryTest.class);
	
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
	
	@Autowired
	private InstructorRepository instructorRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
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
	void getAllCourses() {
		
		// get all courses
		Iterable<Course> courses = courseRepository.findAll();
		
		// assert
		assertNotNull(courses,"courses null");
		
		if (courses instanceof Collection) {
			assertEquals(1, ((Collection<?>) courses).size(), "courses size not 1");
		}
		
		List<Course> courseList = new ArrayList<Course>();
		courses.forEach(courseList::add);

		assertTrue(courseList.stream().anyMatch(course ->
			course.getTitle().equals(COURSE) && course.getId() == 1
		));
	}
	
	@Test
	void getCourseById() {
		
		// get course id=1
		Optional<Course> course = courseRepository.findById(1);
		
		// assert
		assertTrue(course.isPresent());
		validateExistingCourse(course.get());
		
		// get non-existing course id=22
		course = courseRepository.findById(22);
		
		// assert
		assertFalse(course.isPresent());
	}
	
	@Test
	void getCourseByTitle() {
		
		// get course
		Optional<Course> course = courseRepository.findByTitle(COURSE);
		
		// assert
		assertTrue(course.isPresent());
		validateExistingCourse(course.get());
		
		// get non-existing course
		course = courseRepository.findByTitle(COURSE_NEW);
		
		// assert
		assertFalse(course.isPresent());
	}
	
	@Test
	void updateCourse() {
		
		// get course id=1
		Optional<Course> course = courseRepository.findById(1);
		
		// assert
		assertTrue(course.isPresent());
		validateExistingCourse(course.get());
		
		// update
		// set id=1: this is to force an update of existing item
		Course updatedCourse = new Course();
		updatedCourse = updateCourse(course.get());
		
		Course savedCourse = courseRepository.save(updatedCourse);
		
		// assert
		assertNotNull(savedCourse,"savedCourse null");
		validateUpdatedCourse(savedCourse);
	}
	
	@Test
	void addCourse() {
		
		// create course
		// set id=0: this is to force a save of new item
		Course course = new Course(COURSE_NEW);
		course.setId(0);
		
		Course savedCourse = courseRepository.save(course);
		
		// assert
		assertNotNull(savedCourse,"savedCourse null");
		validateNewCourse(savedCourse);
	}

	@Test
	void deleteCourse() {
		
		// set course for instructor
		jdbc.execute(sqlUpdateCourse);
		
		// get course
		Optional<Course> course = courseRepository.findById(1);
		
		// assert
		assertTrue(course.isPresent());
				
		// delete course
		courseRepository.deleteById(1);
		
		course = courseRepository.findById(1);
		
		// assert
		assertFalse(course.isPresent());
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// check other cascading entities
		// get instructor id=1
		Optional<Instructor> instructor = instructorRepository.findById(1);
		
		// assert - must be unchanged
		assertTrue(instructor.isPresent());
		InstructorRepositoryTest.validateExistingInstructor(instructor.get());
		
		// get student id=2
		Optional<Student> student = studentRepository.findById(2);
		
		// assert - must be unchanged
		assertTrue(student.isPresent());
		StudentRepositoryTest.validateExistingStudent(student.get());
		
		// get review id=1
		Optional<Review> review = reviewRepository.findById(1);
		
		// assert - must be deleted
		assertFalse(review.isPresent());
	}
	
	private void validateExistingCourse(Course course) {
		
		assertNotNull(course,"course null");
		assertNotNull(course.getTitle(),"getTitle() null");
		assertNotNull(course.getStudents(),"getStudents() null");
		assertNotNull(course.getReviews(),"getReviews() null");
		//assertNotNull(course.getInstructor(),"getInstructor() null");
		assertEquals(1, course.getId());
		assertEquals(COURSE, course.getTitle(),"getTitle() NOK");
		//assertEquals(1, course.getInstructor().getId(),"getInstructor().getId() NOK");
		assertEquals(1, course.getReviews().size(), "getReviews size not 1");
		assertTrue(course.getReviews().stream().anyMatch(review ->
			review.getComment().equals(REVIEW) && review.getId() == 1
		));
		assertEquals(1, course.getStudents().size(), "getStudents size not 1");
		assertTrue(course.getStudents().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
		));
	}
	
	private void validateUpdatedCourse(Course course) {
		
		assertNotNull(course,"course null");
		assertNotNull(course.getTitle(),"getTitle() null");
		assertNotNull(course.getStudents(),"getStudents() null");
		assertNotNull(course.getReviews(),"getReviews() null");
		//assertNotNull(course.getInstructor(),"getInstructor() null");
		assertEquals(1, course.getId());
		assertEquals(COURSE_UPDATED, course.getTitle(),"getTitle() NOK");
		//assertEquals(1, course.getInstructor().getId(),"getInstructor().getId() NOK");
		assertEquals(1, course.getReviews().size(), "getReviews size not 1");
		assertTrue(course.getReviews().stream().anyMatch(review ->
			review.getComment().equals(REVIEW) && review.getId() == 1
		));
		assertEquals(1, course.getStudents().size(), "getStudents size not 1");
		assertTrue(course.getStudents().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
		));
	}
	
	private void validateNewCourse(Course course) {
		
		assertNotNull(course,"course null");
		assertNotNull(course.getTitle(),"getTitle() null");
		assertNotNull(course.getStudents(),"getStudents() null");
		assertNull(course.getReviews(),"getReviews() null");
		assertNull(course.getInstructor(),"getInstructor() null");
		//assertEquals(1, course.getId());
		assertEquals(COURSE_NEW, course.getTitle(),"getTitle() NOK");
		assertEquals(0, course.getStudents().size(), "getStudents size not 1");
	}
	
	private Course updateCourse(Course course) {
				
		// update with new data
		course.setTitle(COURSE_UPDATED);
		
		return course;
	}
}