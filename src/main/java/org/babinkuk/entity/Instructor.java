package org.babinkuk.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Instructor extends User {

	private Double salary;

	// mapping with instructor_detail table 
	// foreign key (instructor_detail.id column)
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "instructor_detail_id")
	private InstructorDetail instructorDetail;
	
	// bi-directional
	@OneToMany(mappedBy = "instructor", // refers to instructor property in Course class
			fetch = FetchType.LAZY,	
			cascade = {
					CascadeType.PERSIST,
					CascadeType.DETACH,
					CascadeType.MERGE,
					CascadeType.REFRESH}) // cascade.REMOVE not used, if instructor is deleted, do not delete associated courses!!!
	private List<Course> courses;
	
	public Instructor(String firstName, String lastName, String email, Status status, Double salary) {
		super(firstName, lastName, email, status);
		this.salary = salary;
	}
	
	public Instructor() {
		// TODO Auto-generated constructor stub
	}
	
	public Instructor(String firstName, String lastName, String email) {
		super(firstName, lastName, email);
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

	public InstructorDetail getInstructorDetail() {
		return instructorDetail;
	}

	public void setInstructorDetail(InstructorDetail instructorDetail) {
		this.instructorDetail = instructorDetail;
	}

	public List<Course> getCourses() {
		return courses;
	}

	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}
	
	// convenience method for bi-directional relationship
	public void addCourse(Course course) {
		if (courses == null) {
			courses = new ArrayList<Course>();
		}
		
		boolean add = true;
		
		for (Course obj : courses) {
	        if (obj.getId() == (course.getId())) {
	        	System.out.println("course already exist");
	        	add = false;
	        }
	    }
		
		if (add) {
			courses.add(course);
			System.out.println("course NOT exist. course ADDED");
		}
		
		course.setInstructor(this);
	}
	
	// convenience method for bi-directional relationship
	public void removeCourse(Course course) {
		if (courses == null) {
			courses = new ArrayList<Course>();
		}
		
		boolean remove = false;
		
		for (Course obj : courses) {
	        if (obj.getId() == (course.getId())) {
	        	System.out.println("course already exist");
	        	remove = true;
	        }
	    }
		
		if (remove) {
			courses.remove(course);
			System.out.println("course exist. course REMOVED");
		}
		
		course.setInstructor(null);
	}

	@Override
	public String toString() {
		return "Instructor [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", status=" + status + ", images=" + images
				+ ", email=" + email + ", instructorDetail=" + instructorDetail
				//+ ", courses=" + courses // DO NOT LOG NestedServletException
				
				+ "]";
	}
}
