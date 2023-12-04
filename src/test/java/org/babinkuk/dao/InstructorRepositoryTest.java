package org.babinkuk.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.InstructorDetail;
import org.hamcrest.collection.IsMapContaining;
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
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ActiveProfiles("test")
@DataJpaTest
public class InstructorRepositoryTest {
	
	public static final Logger log = LogManager.getLogger(InstructorRepositoryTest.class);
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@PersistenceContext
	private EntityManager entityManager;
	
//	@Autowired
//	private TransactionTemplate transactionTemplate;
	
	@Autowired
	private InstructorRepository instructorRepository;
	
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
	void getAllInstructors() {
		
		// get all instructors
		Iterable<Instructor> instructors = instructorRepository.findAll();
		
		// assert
		assertNotNull(instructors,"instructors null");
		
		if (instructors instanceof Collection) {
			assertEquals(1, ((Collection<?>) instructors).size(), "instructors size not 1");
		}
		
		List<Instructor> instructorList = new ArrayList<Instructor>();
		instructors.forEach(instructorList::add);

		assertTrue(instructorList.stream().anyMatch(instructor ->
			instructor.getFirstName().equals(INSTRUCTOR_FIRSTNAME) && instructor.getId() == 1
		));
	}
	
	@Test
	void getInstructorById() {
		
		// get instructor id=1
		Optional<Instructor> instructor = instructorRepository.findById(1);
		
		// assert
		assertTrue(instructor.isPresent());
		validateExistingInstructor(instructor.get());
		
		// get non-existing instructor id=2
		instructor = instructorRepository.findById(2);
		
		// assert
		assertFalse(instructor.isPresent());
	}
	
	@Test
	void getInstructorByEmail() {
		
		// get instructor id=1
		Optional<Instructor> instructor = instructorRepository.findByEmail(INSTRUCTOR_EMAIL);
		
		// assert
		assertTrue(instructor.isPresent());
		validateExistingInstructor(instructor.get());
		
		// get non-existing instructor id=2
		instructor = instructorRepository.findByEmail(INSTRUCTOR_EMAIL_NEW);
		
		// assert
		assertFalse(instructor.isPresent());
	}
	
	@Test
	void updateInstructor() {
		
		// get instructor id=1
		Optional<Instructor> instructor = instructorRepository.findById(1);
		
		// assert
		assertTrue(instructor.isPresent());
		validateExistingInstructor(instructor.get());
		
		// update
		// set id=1: this is to force an update of existing item
		Instructor updatedInstructor = new Instructor();
		updatedInstructor = updateInstructor(instructor.get());
		
		Instructor savedInstructor = instructorRepository.save(updatedInstructor);
		
		// assert
		assertNotNull(savedInstructor,"savedInstructor null");
		validateUpdatedInstructor(savedInstructor);
	}
	
	@Test
	void addInstructor() {
		
		// create instructor
		// set id=0: this is to force a save of new item
		Instructor instructor = createInstructor();
		
		Instructor savedInstructor = instructorRepository.save(instructor);
		
		// assert
		assertNotNull(savedInstructor,"savedInstructor null");
		validateNewInstructor(savedInstructor);
	}

	@Test
	void deleteInstructor() {
		
		// set course for instructor
		jdbc.execute(sqlUpdateCourse);
		
		// check if instructor id=1 exists
		Optional<Instructor> instructor = instructorRepository.findById(1);
		
		// assert
		assertTrue(instructor.isPresent());
		
		// delete instructor
		instructorRepository.deleteById(1);
		
//		// clear persistence context and sync with db
//		entityManager.flush();
//		entityManager.clear();
		
		instructor = instructorRepository.findById(1);
		
		// assert
		assertFalse(instructor.isPresent());
		
		// check other cascading entities executed in service test
		// because of explicitly setting inside InstructorServiceImpl:
		// course.setInstructor(null); 
//		//entityManager.flush();
//		//entityManager.clear();
//		
//		// get course with id=1
//		Optional<Course> course = courseRepository.findById(1);
//		
//		log.info(course.get());
//		// assert
//		// course must be unchanged except instructor (null)
//		assertTrue(course.isPresent());
//		assertEquals(1, course.get().getId(), "course.get().getId()");
//		assertEquals(COURSE, course.get().getTitle(), "course.get().getTitle()");
//		assertEquals(1, course.get().getReviews().size(), "course.get().getReviews().size()");
//		assertEquals(INSTRUCTOR_FIRSTNAME, course.get().getInstructor().getFirstName(), "course.get().getInstructor().getFirstName()");
//		assertEquals(1, course.get().getStudents().size(), "course.get().getStudents().size()");
//		assertTrue(course.get().getStudents().stream().anyMatch(student ->
//			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
//		));
	}
	
	public static void validateExistingInstructor(Instructor instructor) {
		
		assertNotNull(instructor,"instructor null");
		assertEquals(1, instructor.getId());
		assertNotNull(instructor.getFirstName(),"getFirstName() null");
		assertNotNull(instructor.getLastName(),"getLastName() null");
		assertNotNull(instructor.getEmail(),"getEmail() null");
		assertNotNull(instructor.getSalary(),"getSalary() null");
		assertNotNull(instructor.getStatus(),"getStatus() null");
		assertNotNull(instructor.getImages(),"getImages() null");
		assertNotNull(instructor.getInstructorDetail().getYoutubeChannel(),"getYoutubeChannel() null");
		assertNotNull(instructor.getInstructorDetail().getHobby(),"getHobby() null");
		assertEquals(INSTRUCTOR_FIRSTNAME, instructor.getFirstName(),"getFirstName() NOK");
		assertEquals(INSTRUCTOR_LASTNAME, instructor.getLastName(),"getLastName() NOK");
		assertEquals(INSTRUCTOR_EMAIL, instructor.getEmail(),"getEmail() NOK");
		assertEquals(INSTRUCTOR_SALARY, instructor.getSalary(),"getSalary() NOK");
		assertEquals(INSTRUCTOR_STATUS, instructor.getStatus(),"getStatus() NOK");
		assertEquals(2, instructor.getImages().size(), "getImages size not 2");
		assertThat(instructor.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_1, INSTRUCTOR_IMAGE_1));
		assertThat(instructor.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_11, INSTRUCTOR_IMAGE_11));
		assertEquals(INSTRUCTOR_YOUTUBE, instructor.getInstructorDetail().getYoutubeChannel(),"getYoutubeChannel() NOK");
		assertEquals(INSTRUCTOR_HOBBY, instructor.getInstructorDetail().getHobby(),"getHobby() NOK");
		// not neccessary
		assertNotEquals("test hobb", instructor.getInstructorDetail().getHobby(),"getHobby() NOK");
	}
	
	private void validateUpdatedInstructor(Instructor instructor) {
		
		assertNotNull(instructor,"instructor null");
		assertEquals(1, instructor.getId());
		assertNotNull(instructor.getFirstName(),"getFirstName() null");
		assertNotNull(instructor.getLastName(),"getLastName() null");
		assertNotNull(instructor.getEmail(),"getEmail() null");
		assertNotNull(instructor.getSalary(),"getSalary() null");
		assertNotNull(instructor.getStatus(),"getStatus() null");
		assertNotNull(instructor.getImages(),"getImages() null");
		assertNotNull(instructor.getInstructorDetail().getYoutubeChannel(),"getYoutubeChannel() null");
		assertNotNull(instructor.getInstructorDetail().getHobby(),"getHobby() null");
		assertEquals(INSTRUCTOR_FIRSTNAME_UPDATED, instructor.getFirstName(),"getFirstName() NOK");
		assertEquals(INSTRUCTOR_LASTNAME_UPDATED, instructor.getLastName(),".getLastName() NOK");
		assertEquals(INSTRUCTOR_EMAIL_UPDATED, instructor.getEmail(),"getEmail() NOK");
		assertEquals(INSTRUCTOR_SALARY_UPDATED, instructor.getSalary(),"getSalary() NOK");
		assertEquals(INSTRUCTOR_STATUS_UPDATED, instructor.getStatus(),"getStatus() NOK");
		assertEquals(3, instructor.getImages().size(), "getImages size not 2");
		assertThat(instructor.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_1, INSTRUCTOR_IMAGE_1));
		assertThat(instructor.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_11, INSTRUCTOR_IMAGE_11));
		assertThat(instructor.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_UPDATED, INSTRUCTOR_IMAGE_UPDATED));
		assertEquals(INSTRUCTOR_YOUTUBE_UPDATED, instructor.getInstructorDetail().getYoutubeChannel(),"getYoutubeChannel() NOK");
		assertEquals(INSTRUCTOR_HOBBY_UPDATED, instructor.getInstructorDetail().getHobby(),"getHobby() NOK");
		// not neccessary
		assertNotEquals("test hobb", instructor.getInstructorDetail().getHobby(),"getHobby() NOK");
	}
	
	private void validateNewInstructor(Instructor instructor) {
		
		assertNotNull(instructor,"instructor null");
		//assertEquals(1, instructor.getId());
		assertNotNull(instructor.getFirstName(),"getFirstName() null");
		assertNotNull(instructor.getLastName(),"getLastName() null");
		assertNotNull(instructor.getEmail(),"getEmail() null");
		assertNotNull(instructor.getSalary(),"getSalary() null");
		assertNotNull(instructor.getStatus(),"getStatus() null");
		assertNotNull(instructor.getImages(),"getImages() null");
		assertNotNull(instructor.getInstructorDetail().getYoutubeChannel(),"getYoutubeChannel() null");
		assertNotNull(instructor.getInstructorDetail().getHobby(),"getHobby() null");
		assertEquals(INSTRUCTOR_FIRSTNAME_NEW, instructor.getFirstName(),"getFirstName() NOK");
		assertEquals(INSTRUCTOR_LASTNAME_NEW, instructor.getLastName(),"getLastName() NOK");
		assertEquals(INSTRUCTOR_EMAIL_NEW, instructor.getEmail(),"getEmail() NOK");
		assertEquals(INSTRUCTOR_SALARY_NEW, instructor.getSalary(),"getSalary() NOK");
		assertEquals(INSTRUCTOR_STATUS_NEW, instructor.getStatus(),"getStatus() NOK");
		assertEquals(1, instructor.getImages().size(), "getImages size not 1");
		assertThat(instructor.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_NEW, INSTRUCTOR_IMAGE_NEW));
		assertEquals(INSTRUCTOR_YOUTUBE_NEW, instructor.getInstructorDetail().getYoutubeChannel(),"getYoutubeChannel() NOK");
		assertEquals(INSTRUCTOR_HOBBY_NEW, instructor.getInstructorDetail().getHobby(),"getHobby() NOK");
		// not neccessary
		assertNotEquals("test hobb", instructor.getInstructorDetail().getHobby(),"getHobby() NOK");
	}
	
	private Instructor updateInstructor(Instructor instructor) {
		
		InstructorDetail instructorDetail = new InstructorDetail();
		instructorDetail.setYoutubeChannel(INSTRUCTOR_YOUTUBE_UPDATED);
		instructorDetail.setHobby(INSTRUCTOR_HOBBY_UPDATED);
				
		// update with new data
		instructor.setFirstName(INSTRUCTOR_FIRSTNAME_UPDATED);
		instructor.setLastName(INSTRUCTOR_LASTNAME_UPDATED);
		instructor.setEmail(INSTRUCTOR_EMAIL_UPDATED);
		instructor.setSalary(INSTRUCTOR_SALARY_UPDATED);
		instructor.setStatus(INSTRUCTOR_STATUS_UPDATED);
		instructor.getImages().put(INSTRUCTOR_FILE_UPDATED, INSTRUCTOR_IMAGE_UPDATED);
		instructor.setInstructorDetail(instructorDetail);
		
		return instructor;
	}
	
	private Instructor createInstructor() {
		
		// set id=0: this is to force a save of new item ... instead of update
		Instructor instructor = new Instructor(
				INSTRUCTOR_FIRSTNAME_NEW, 
				INSTRUCTOR_LASTNAME_NEW, 
				INSTRUCTOR_EMAIL_NEW, 
				INSTRUCTOR_STATUS_NEW, 
				INSTRUCTOR_SALARY_NEW);
		instructor.setId(0);
		
		InstructorDetail instructorDetail = new InstructorDetail();
		instructorDetail.setYoutubeChannel(INSTRUCTOR_YOUTUBE_NEW);
		instructorDetail.setHobby(INSTRUCTOR_HOBBY_NEW);
		
		SortedMap<String, String> images = new TreeMap<String, String>();
		images.put(INSTRUCTOR_FILE_NEW, INSTRUCTOR_IMAGE_NEW);
		
		instructor.setImages(images);
		instructor.setInstructorDetail(instructorDetail);
		
		return instructor;
	}
}
