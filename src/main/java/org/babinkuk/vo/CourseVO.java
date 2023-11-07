package org.babinkuk.vo;

import java.util.ArrayList;
import java.util.List;
import org.babinkuk.vo.diff.DiffField;
import org.babinkuk.vo.diff.Diffable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * instance of this class is used to represent course data
 * 
 * @author BabinKuk
 *
 */
@Diffable(id = "id")
@JsonInclude(value = Include.NON_EMPTY)
public class CourseVO {

	private int id;

	@DiffField
	private String title;
	
	@DiffField
	private InstructorVO instructorVO;
	
	@DiffField
	private List<ReviewVO> reviewsVO = new ArrayList<ReviewVO>();
	
	@DiffField
	private List<StudentVO> studentsVO = new ArrayList<StudentVO>();
	
	public CourseVO() {
		// TODO Auto-generated constructor stub
	}
	
	public CourseVO(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public InstructorVO getInstructorVO() {
		return instructorVO;
	}

	public void setInstructorVO(InstructorVO instructorVO) {
		this.instructorVO = instructorVO;
	}
	
	public List<ReviewVO> getReviewsVO() {
		return reviewsVO;
	}

	public void setReviewsVO(List<ReviewVO> reviewsVO) {
		this.reviewsVO = reviewsVO;
	}
	
	public List<StudentVO> getStudentsVO() {
		return studentsVO;
	}

	public void setStudentsVO(List<StudentVO> students) {
		this.studentsVO = students;
	}

	// convenience methods
	public void addReviewVO(ReviewVO review) {
		if (reviewsVO == null) {
			reviewsVO = new ArrayList<ReviewVO>();
		}
		
		reviewsVO.add(review);
	}
	
	public void addStudentVO(StudentVO student) {
		studentsVO.add(student);
	}
	
	public void removeStudentVO(StudentVO student) {
		System.out.println("studentsVO " + studentsVO);
		
		List<StudentVO> tempStudentsVO = getStudentsVO();
		for (StudentVO vo : tempStudentsVO) {
			System.out.println(vo);
			if (vo.getId() == student.getId()) {
				System.out.println("remove " + vo);
				tempStudentsVO.remove(vo);
			}
		}

		setStudentsVO(tempStudentsVO);
	}
	
	@Override
	public String toString() {
		return "CourseVO [id=" + id + ", title=" + title + ", instructorVO=" + instructorVO + ", studentsVO=" + studentsVO + ", reviewsVO=" + reviewsVO + "]";
	}
}
