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
	private List<Course> courses;
	
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
		
		courses.add(course);
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", email=" + email + ", address=" + address
				//+ ", courses=" + courses
				+ "]";
	}
	
}
