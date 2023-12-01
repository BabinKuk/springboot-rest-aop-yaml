package org.babinkuk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.entity.Course;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
import org.babinkuk.vo.StudentVO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CourseServiceTest {
	
	public static final Logger log = LogManager.getLogger(CourseServiceTest.class);
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private InstructorService instructorService;
	
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
	
	@Test
	void getAllCourses() {
		
		// get all courses
		Iterable<CourseVO> courses = courseService.getAllCourses();
		
		// assert
		if (courses instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) courses).size(), "courses size not 1");
		}
		
		// add another course
		CourseVO courseVO = ApplicationTestUtils.createCourse();
		
		courseService.saveCourse(courseVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		courses = courseService.getAllCourses();
		
		// assert
		if (courses instanceof Collection<?>) {
			assertEquals(2, ((Collection<?>) courses).size(), "courses size not 2 after insert");
		}
		
		// delete course
		courseService.deleteCourse(1);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		courses = courseService.getAllCourses();
		
		// assert
		if (courses instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) courses).size(), "courses size not 1 after delete");
		}
	}
	
	@Test
	void getCourseById() {
		
		// get course id=1
		CourseVO courseVO = courseService.findById(1);
		
		// assert
		validateExistingCourse(courseVO);
		
		// assert not existing course
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			courseService.findById(2);
		});
		
		String expectedMessage = "Course with id=2 not found.";
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void getCourseByTitle() {
		
		// get course
		CourseVO courseVO = courseService.findByTitle(COURSE);
		
		// assert
		validateExistingCourse(courseVO);
		
		// assert not existing course
		courseVO = courseService.findByTitle(COURSE_NEW);
				
		// assert
		assertNull(courseVO, "courseVO null");
	}
	
	@Test
	void addCourse() {
		
		// create course
		CourseVO courseVO = ApplicationTestUtils.createCourse();
		
		courseService.saveCourse(courseVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		courseVO = courseService.findByTitle(COURSE_NEW);
		
		// assert
		validateNewCourse(courseVO);
	}
	
	@Test
	void updateCourse() {

		// get course id=1
		CourseVO courseVO = courseService.findById(1);
		
		validateExistingCourse(courseVO);
		
		// update with new data
		courseVO = ApplicationTestUtils.updateExistingCourse(courseVO);
		
		courseService.saveCourse(courseVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again
		CourseVO courseVO2 = courseService.findById(1);
		
		// assert
		validateUpdatedCourse(courseVO2);
	}
	
	@Test
	void deleteCourse() {
		
		// first get course id=1
		CourseVO courseVO = courseService.findById(1);
		
		// assert
		validateExistingCourse(courseVO);
		
		// delete
		courseService.deleteCourse(1);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// assert not existing student
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			courseService.findById(1);
		});
			
		String expectedMessage = "Course with id=1 not found.";
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));

		// delete not existing course
		exception = assertThrows(EmptyResultDataAccessException.class, () -> {
			courseService.deleteCourse(2);
		});
	}
	
	private void validateExistingCourse(CourseVO courseVO) {
		
		assertNotNull(courseVO,"course null");
		assertNotNull(courseVO.getTitle(),"getTitle() null");
		assertNotNull(courseVO.getStudentsVO(),"getStudents() null");
		assertNotNull(courseVO.getReviewsVO(),"getReviews() null");
		assertNull(courseVO.getInstructorVO(),"getInstructor() null");
		assertEquals(1, courseVO.getId());
		assertEquals(COURSE, courseVO.getTitle(),"getTitle() NOK");
		//assertEquals(1, course.getInstructor().getId(),"getInstructor().getId() NOK");
		assertEquals(1, courseVO.getReviewsVO().size(), "getReviews size not 1");
		assertTrue(courseVO.getReviewsVO().stream().anyMatch(review ->
			review.getComment().equals(REVIEW) && review.getId() == 1
		));
		assertEquals(1, courseVO.getStudentsVO().size(), "getStudents size not 1");
		assertTrue(courseVO.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
		));
	}
	
	private void validateUpdatedCourse(CourseVO courseVO) {
		
		assertNotNull(courseVO,"course null");
		assertNotNull(courseVO.getTitle(),"getTitle() null");
		assertNotNull(courseVO.getStudentsVO(),"getStudents() null");
		assertNotNull(courseVO.getReviewsVO(),"getReviews() null");
		//assertNotNull(course.getInstructor(),"getInstructor() null");
		assertEquals(1, courseVO.getId());
		assertEquals(COURSE_UPDATED, courseVO.getTitle(),"getTitle() NOK");
		assertNull(courseVO.getInstructorVO(),"getInstructor() NOK");
		assertEquals(1, courseVO.getReviewsVO().size(), "getReviews size not 1");
		assertTrue(courseVO.getReviewsVO().stream().anyMatch(review ->
			review.getComment().equals(REVIEW) && review.getId() == 1
		));
		assertEquals(1, courseVO.getStudentsVO().size(), "getStudents size not 1");
		assertTrue(courseVO.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
		));
	}
	
	private void validateNewCourse(CourseVO courseVO) {
		
		assertNotNull(courseVO,"course null");
		assertNotNull(courseVO.getTitle(),"getTitle() null");
		assertNotNull(courseVO.getStudentsVO(),"getStudents() null");
		assertNull(courseVO.getInstructorVO(),"getInstructor() null");
		//assertEquals(1, course.getId());
		assertEquals(COURSE_NEW, courseVO.getTitle(),"getTitle() NOK");
		assertEquals(0, courseVO.getStudentsVO().size(), "getStudents size not 0");
		assertEquals(0, courseVO.getReviewsVO().size(), "getReviews size not 0");
	}
	
	private CourseVO updateCourse(CourseVO courseVO) {
				
		// update with new data
		courseVO.setTitle(COURSE_UPDATED);
		
		return courseVO;
	}
}
