package org.babinkuk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.entity.Status;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.validator.ActionType;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
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
public class InstructorServiceTest {
	
	public static final Logger log = LogManager.getLogger(InstructorServiceTest.class);
	
	@Autowired
	private InstructorService instructorService;
	
	@Autowired
	private CourseService courseService;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private JdbcTemplate jdbc;
	
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
	void getAllInstructors() {
		
		// get all instructors
		Iterable<InstructorVO> instructors = instructorService.getAllInstructors();
		
		// assert
		if (instructors instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) instructors).size(), "instructors size not 1");
		}
		
		// add another instructor
		// set id=0: this is to force a save of new item ... instead of update
		InstructorVO instructorVO = ApplicationTestUtils.createInstructor();
		
		instructorService.saveInstructor(instructorVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		instructors = instructorService.getAllInstructors();
		
		// assert
		if (instructors instanceof Collection<?>) {
			assertEquals(2, ((Collection<?>) instructors).size(), "instructors size not 2 after insert");
		}
	}
	
	@Test
	void getInstructorById() {
		
		// get instructor id=1
		InstructorVO instructorVO = instructorService.findById(1);
		
		validateExistingInstructor(instructorVO);
		
		// assert not existing instructor
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			instructorService.findById(11);
		});
				
		String expectedMessage = "Instructor with id=11 not found.";
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void getInstructorByEmail() {
		
		// get instructor
		InstructorVO instructorVO = instructorService.findByEmail(INSTRUCTOR_EMAIL);
		
		validateExistingInstructor(instructorVO);
		
		// get not existing instructor
		instructorVO = instructorService.findByEmail(INSTRUCTOR_EMAIL_NEW);
		
		// assert
		assertNull(instructorVO, "instructorVO null");
	}
	
	@Test
	void addInstructor() {
		
		// create instructor
		InstructorVO instructorVO = ApplicationTestUtils.createInstructor();
		
		instructorService.saveInstructor(instructorVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		instructorVO = instructorService.findByEmail(INSTRUCTOR_EMAIL_NEW);
		
		// assert
		//assertEquals(2, instructorVO2.getId());
		validateNewInstructor(instructorVO);
	}	
	
	@Test
	void updateInstructor() {
		
		// get instructor id=1
		InstructorVO instructorVO = instructorService.findById(1);
				
		// update with new data
		instructorVO = ApplicationTestUtils.updateExistingInstructor(instructorVO);
		
		instructorService.saveInstructor(instructorVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again
		instructorVO = instructorService.findById(1);
		
		// assert
		validateUpdatedInstructor(instructorVO);
	}
	
	@Test
	void deleteInstructor() {
		
		// set course for instructor
		jdbc.execute(sqlUpdateCourse);

		// get instructor
		InstructorVO instructorVO = instructorService.findById(1);
		
		// assert
		assertNotNull(instructorVO, "return true");
		assertEquals(1, instructorVO.getId());
		
		// delete instructor
		instructorService.deleteInstructor(1);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// assert not existing instructor
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			instructorService.findById(1);
		});
		
		String expectedMessage = "Instructor with id=1 not found.";
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));
	    
		// delete non-existing instructor
		exception = assertThrows(EmptyResultDataAccessException.class, () -> {
			instructorService.deleteInstructor(2);
		});
		
		// check other cascading entities
		// get course with id=1
		CourseVO courseVO = courseService.findById(1);
		
		// assert
		// course must be unchanged (except instructor=null)
		assertNotNull(courseVO, "courseVO null");
		assertEquals(1, courseVO.getId(), "course.getId()");
		assertEquals(COURSE, courseVO.getTitle(), "course.getTitle()");
		assertEquals(1, courseVO.getReviewsVO().size(), "course.getReviews().size()");
		assertTrue(courseVO.getReviewsVO().stream().anyMatch(review ->
			review.getComment().equals(REVIEW) && review.getId() == 1
		));
		assertNull(courseVO.getInstructorVO(), "course.getInstructor() null");
		assertEquals(1, courseVO.getStudentsVO().size(), "course.getStudents().size()");
		assertTrue(courseVO.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
		));
	}
	
	@Test
	void setCourse() {
		
		// get instructor
		InstructorVO instructorVO = instructorService.findById(1);
		
		validateExistingInstructor(instructorVO);
		
		// get course
		CourseVO courseVO = courseService.findById(1);
		
		// assert
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO() null");
		assertNotNull(courseVO.getStudentsVO(),"getStudentsVO() null");
		assertNull(courseVO.getInstructorVO(),"getInstructorVO() not null");
		assertEquals(COURSE, courseVO.getTitle(),"getTitle() NOK");
		
		// set course for instructor
		instructorService.setCourse(instructorVO, courseVO, ActionType.ENROLL);

		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again
		instructorVO = instructorService.findById(1);
		
		// assert
		validateExistingInstructor(instructorVO);
		
		// assert instructor -> course
		assertEquals(1, instructorVO.getCourses().size(), "instrucors.getCourses size not 1");
		assertTrue(instructorVO.getCourses().stream().anyMatch(course ->
			course.getTitle().equals(COURSE) && course.getId() == 1
		));
		
		// get course again
		courseVO = courseService.findById(1);
		
		// assert
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO() null");
		assertNotNull(courseVO.getStudentsVO(),"getStudentsVO() null");
		assertNotNull(courseVO.getInstructorVO(),"getInstructorVO() not null");
		assertEquals(COURSE, courseVO.getTitle(),"getTitle() NOK");
		assertEquals(INSTRUCTOR_FIRSTNAME, courseVO.getInstructorVO().getFirstName(),"getTitle() NOK");
		
		// add another course
		CourseVO courseVO2 = ApplicationTestUtils.createCourse();
		
		courseService.saveCourse(courseVO2);
		
		courseVO2 = courseService.findByTitle(COURSE_NEW);

		// set course 
		instructorService.setCourse(instructorVO, courseVO2, ActionType.ENROLL);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again
		instructorVO = instructorService.findById(1);
		
		// assert
		validateExistingInstructor(instructorVO);
		
		// assert course
		assertEquals(2, instructorVO.getCourses().size(), "instructors.getCourses size not 2");
		assertTrue(instructorVO.getCourses().stream().anyMatch(course ->
			course.getTitle().equals(COURSE) && course.getId() == 1
		));
		assertTrue(instructorVO.getCourses().stream().anyMatch(course ->
			course.getTitle().equals(COURSE_NEW)// && course.getId() == 2
		));
		
		// get new course again
		courseVO2 = courseService.findByTitle(COURSE_NEW);
		
		// assert
		assertNotNull(courseVO2,"courseVO null");
		//assertEquals(2, courseVO2.getId());
		assertNotNull(courseVO2.getTitle(),"courseVO2() null");
		assertNotNull(courseVO2.getStudentsVO(),"getStudentsVO() null");
		assertNotNull(courseVO2.getInstructorVO(),"getInstructorVO() not null");
		assertEquals(COURSE_NEW, courseVO2.getTitle(),"getTitle() NOK");
		assertEquals(INSTRUCTOR_FIRSTNAME, courseVO2.getInstructorVO().getFirstName(),"getTitle() NOK");
		
		// now withdraw courses
		instructorService.setCourse(instructorVO, courseVO, ActionType.WITHDRAW);
		instructorService.setCourse(instructorVO, courseVO2, ActionType.WITHDRAW);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again
		instructorVO = instructorService.findById(1);
		
		// assert
		validateExistingInstructor(instructorVO);
		
		// assert course
		assertEquals(0, instructorVO.getCourses().size(), "instructor.getCourses");
		
		// get courses again
		courseVO = courseService.findById(1);
		courseVO2 = courseService.findByTitle(COURSE_NEW);

		// assert
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO() null");
		assertNotNull(courseVO.getStudentsVO(),"getStudentsVO() null");
		assertNull(courseVO.getInstructorVO(),"getInstructorVO() not null");
		assertEquals(COURSE, courseVO.getTitle(),"getTitle() NOK");
		
		assertNotNull(courseVO2,"courseVO null");
		//assertEquals(1, courseVO.getId());
		assertNotNull(courseVO2.getTitle(),"courseVO() null");
		assertNotNull(courseVO2.getStudentsVO(),"getStudentsVO() null");
		assertNull(courseVO2.getInstructorVO(),"getInstructorVO() not null");
		assertEquals(COURSE_NEW, courseVO2.getTitle(),"getTitle() NOK");
		
		// not mandatory
		// setting non existing course
		CourseVO courseVO3 = new CourseVO("non existing course");
		courseVO3.setId(3);
		
		// for avoiding Local variable instructorVO defined in an enclosing scope must be final or effectively final
		final InstructorVO insVO = instructorVO;
		
		// assert non-existing course
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			instructorService.setCourse(insVO, courseVO3, ActionType.ENROLL);
		});
		
		String expectedMessage = "Course with id=3 not found.";
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	    
	    // not mandatory
 		// setting course to non existing instructor
 		// for avoiding Local variable instructorVO defined in an enclosing scope must be final or effectively final
 		final InstructorVO insVO2 = new InstructorVO("ime", "prezime", "ime@babinkuk.com");
 		insVO2.setId(22);
 		
 		final CourseVO crsVO2 = courseVO;
 		// assert non-existing instructor
 		exception = assertThrows(ObjectNotFoundException.class, () -> {
 			instructorService.setCourse(insVO2, crsVO2, ActionType.ENROLL);
 		});
 		
 		expectedMessage = "Instructor with id=22 not found.";
 		actualMessage = exception.getMessage();

 	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	private void validateExistingInstructor(InstructorVO instructorVO) {
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"getFirstName() null");
		assertNotNull(instructorVO.getLastName(),"getLastName() null");
		assertNotNull(instructorVO.getEmail(),"getEmail() null");
		assertNotNull(instructorVO.getSalary(),"getSalary() null");
		assertNotNull(instructorVO.getStatus(),"getStatus() null");
		assertNotNull(instructorVO.getImages(),"getImages() null");
		assertNotNull(instructorVO.getYoutubeChannel(),"getYoutubeChannel() null");
		assertNotNull(instructorVO.getHobby(),"getHobby() null");
		assertEquals(INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"getFirstName() NOK");
		assertEquals(INSTRUCTOR_LASTNAME, instructorVO.getLastName(),"getLastName() NOK");
		assertEquals(INSTRUCTOR_EMAIL, instructorVO.getEmail(),"getEmail() NOK");
		assertEquals(1000, instructorVO.getSalary(),"getSalary() NOK");
		assertEquals(Status.ACTIVE, instructorVO.getStatus(),"getStatus() NOK");
		assertEquals(2, instructorVO.getImages().size(), "getImages size not 2");
		assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_1, INSTRUCTOR_IMAGE_1));
		assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_11, INSTRUCTOR_IMAGE_11));
		assertEquals(INSTRUCTOR_YOUTUBE, instructorVO.getYoutubeChannel(),"getYoutubeChannel() NOK");
		assertEquals(INSTRUCTOR_HOBBY, instructorVO.getHobby(),"getHobby() NOK");
		// not neccessary
		assertNotEquals("test hobb", instructorVO.getHobby(),"getHobby() NOK");
		
		// assert non-existing instructor
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			instructorService.findById(22);
		});
		
		String expectedMessage = "Instructor with id=22 not found.";
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	private void validateUpdatedInstructor(InstructorVO instructorVO) {
		
		assertNotNull(instructorVO,"instructor null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"getFirstName() null");
		assertNotNull(instructorVO.getLastName(),"getLastName() null");
		assertNotNull(instructorVO.getEmail(),"getEmail() null");
		assertNotNull(instructorVO.getSalary(),"getSalary() null");
		assertNotNull(instructorVO.getStatus(),"getStatus() null");
		assertNotNull(instructorVO.getImages(),"getImages() null");
		assertNotNull(instructorVO.getYoutubeChannel(),"getYoutubeChannel() null");
		assertNotNull(instructorVO.getHobby(),"getHobby() null");
		assertEquals(INSTRUCTOR_FIRSTNAME_UPDATED, instructorVO.getFirstName(),"getFirstName() NOK");
		assertEquals(INSTRUCTOR_LASTNAME_UPDATED, instructorVO.getLastName(),".getLastName() NOK");
		assertEquals(INSTRUCTOR_EMAIL_UPDATED, instructorVO.getEmail(),"getEmail() NOK");
		assertEquals(INSTRUCTOR_SALARY_UPDATED, instructorVO.getSalary(),"getSalary() NOK");
		assertEquals(INSTRUCTOR_STATUS_UPDATED, instructorVO.getStatus(),"getStatus() NOK");
		assertEquals(3, instructorVO.getImages().size(), "getImages size not 2");
		assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_1, INSTRUCTOR_IMAGE_1));
		assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_11, INSTRUCTOR_IMAGE_11));
		assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_UPDATED, INSTRUCTOR_IMAGE_UPDATED));
		assertEquals(INSTRUCTOR_YOUTUBE_UPDATED, instructorVO.getYoutubeChannel(),"getYoutubeChannel() NOK");
		assertEquals(INSTRUCTOR_HOBBY_UPDATED, instructorVO.getHobby(),"getHobby() NOK");
		// not neccessary
		assertNotEquals("test hobb", instructorVO.getHobby(),"getHobby() NOK");
	}
	
	private void validateNewInstructor(InstructorVO instructorVO) {
		
		assertNotNull(instructorVO,"instructor null");
		//assertEquals(1, instructor.getId());
		assertNotNull(instructorVO.getFirstName(),"getFirstName() null");
		assertNotNull(instructorVO.getLastName(),"getLastName() null");
		assertNotNull(instructorVO.getEmail(),"getEmail() null");
		assertNotNull(instructorVO.getSalary(),"getSalary() null");
		assertNotNull(instructorVO.getStatus(),"getStatus() null");
		assertNotNull(instructorVO.getImages(),"getImages() null");
		assertNotNull(instructorVO.getYoutubeChannel(),"getYoutubeChannel() null");
		assertNotNull(instructorVO.getHobby(),"getHobby() null");
		assertEquals(INSTRUCTOR_FIRSTNAME_NEW, instructorVO.getFirstName(),"getFirstName() NOK");
		assertEquals(INSTRUCTOR_LASTNAME_NEW, instructorVO.getLastName(),"getLastName() NOK");
		assertEquals(INSTRUCTOR_EMAIL_NEW, instructorVO.getEmail(),"getEmail() NOK");
		assertEquals(INSTRUCTOR_SALARY_NEW, instructorVO.getSalary(),"getSalary() NOK");
		assertEquals(INSTRUCTOR_STATUS_NEW, instructorVO.getStatus(),"getStatus() NOK");
		assertEquals(1, instructorVO.getImages().size(), "getImages size not 1");
		assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_NEW, INSTRUCTOR_IMAGE_NEW));
		assertEquals(INSTRUCTOR_YOUTUBE_NEW, instructorVO.getYoutubeChannel(),"getYoutubeChannel() NOK");
		assertEquals(INSTRUCTOR_HOBBY_NEW, instructorVO.getHobby(),"getHobby() NOK");
		// not neccessary
		assertNotEquals("test hobb", instructorVO.getHobby(),"getHobby() NOK");
	}
}