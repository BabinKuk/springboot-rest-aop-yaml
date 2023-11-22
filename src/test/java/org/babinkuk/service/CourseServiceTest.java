package org.babinkuk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CourseServiceTest {
	
	public static final Logger log = LogManager.getLogger(CourseServiceTest.class);
	
	@Autowired
	private JdbcTemplate jdbc;
	
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
	void getCourse() {
		//log.info("getCourse");
		
		CourseVO courseVO = courseService.findById(1);
		
		//log.info(courseVO.toString());
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.courseVO() null");
		assertNotNull(courseVO.getStudentsVO(),"courseVO.getStudentsVO() null");
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() not null");
		assertEquals(COURSE, courseVO.getTitle(),"courseVO.getTitle() NOK");
		
		// assert not existing course
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			courseService.findById(2);
		});
		
		String expectedMessage = "Course with id=2 not found.";
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void addCourse() {
		//log.info("addCourse");
		
		// create course
		CourseVO courseVO = ApplicationTestUtils.createCourse();
		
		courseService.saveCourse(courseVO);
		
		CourseVO courseVO2 = courseService.findById(2);
		
		//log.info(courseVO2);

		// assert
		assertEquals(2, courseVO2.getId());
		assertNotNull(courseVO2,"courseVO2 null");
		assertEquals(courseVO.getTitle(), courseVO2.getTitle(),"courseVO.getTitle() NOK");
	}
	
	@Test
	void updateCourse() {
		//log.info("updateCourse");
		
		CourseVO courseVO = courseService.findById(1);
		
		InstructorVO instructorVO = instructorService.findById(1);
		
		// create student
		StudentVO studentVO = ApplicationTestUtils.createStudent();
		
		studentService.saveStudent(studentVO);
				
		StudentVO studentVO2 = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		// update with new data
		courseVO = ApplicationTestUtils.updateExistingCourse(courseVO, studentVO2, instructorVO);

		courseService.saveCourse(courseVO);
		
		// fetch again
		CourseVO courseVO2 = courseService.findById(1);
		
		// assert
		assertEquals(courseVO.getId(), courseVO2.getId());
		assertEquals(COURSE_UPDATED, courseVO2.getTitle(),"courseVO.getTitle() NOK");
		assertEquals(instructorVO.getId(), courseVO2.getInstructorVO().getId(),"courseVO.getInstructorVO().getId() NOK");
		assertEquals(2, courseVO2.getStudentsVO().size(),"courseVO.getStudentsVO().size() NOK");
		assertEquals(1, courseVO2.getReviewsVO().size(),"courseVO.getReviewsVO().size() NOK");
	}
	
	@Test
	void deleteCourse() {
		//log.info("deleteCourse");
		
		// first get course
		CourseVO courseVO = courseService.findById(1);
		
		// assert
		assertNotNull(courseVO, "return true");
		assertEquals(1, courseVO.getId());
		
		// delete
		courseService.deleteCourse(1);
		
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
	
	@Test
	void getAllCourses() {
		//log.info("getAllCourses");
		
		Iterable<CourseVO> courses = courseService.getAllCourses();
		
		// assert
		if (courses instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) courses).size(), "courses size not 1");
		}
		
		// create course
		CourseVO courseVO = ApplicationTestUtils.createCourse();
		
		courseService.saveCourse(courseVO);
		
		courses = courseService.getAllCourses();
		
		// assert
		if (courses instanceof Collection<?>) {
			assertEquals(2, ((Collection<?>) courses).size(), "courses size not 2 after insert");
		}
		
		// delete course
		courseService.deleteCourse(1);
		
		courses = courseService.getAllCourses();
		//log.info("after delete " + courses.toString());
		
		// assert
		if (courses instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) courses).size(), "courses size not 1 after delete");
		}
	}
	
}
