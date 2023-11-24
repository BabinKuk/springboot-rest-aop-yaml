package org.babinkuk.entity;

import java.util.ArrayList;
import java.util.List;

import org.babinkuk.entity.Course;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class Student extends User {

	@Embedded
	private Address address;
	
	@ManyToMany(fetch = FetchType.LAZY,
			cascade = {
				CascadeType.PERSIST,
				CascadeType.DETACH,
				CascadeType.MERGE,
				CascadeType.REFRESH})
	// cascade.REMOVE not used, if student is deleted, do not delete associated courses!!!
	@JoinTable(
			name = "course_student",
			joinColumns = @JoinColumn(name = "student_id"),
			inverseJoinColumns = @JoinColumn(name = "course_id"))
	private List<Course> courses = new ArrayList<Course>();
	
	public Student(String firstName, String lastName, String email, Status status) {
		super(firstName, lastName, email, status);
	}

	public Student() {
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
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
			boolean removeCourse = courses.remove(course);
			System.out.println("course exist. course REMOVED " + removeCourse);
		}
		System.out.println(courses.size());
	}
	
	@Override
	public String toString() {
		return "Student [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", email=" + email + ", address=" + address
				//+ ", courses=" + courses
				+ "]";
	}
	
}
