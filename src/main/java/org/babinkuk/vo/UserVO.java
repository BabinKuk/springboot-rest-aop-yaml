package org.babinkuk.vo;

import java.util.SortedMap;

import org.babinkuk.entity.Status;

public abstract class UserVO {
	
	public abstract int getId();
	
	public abstract String getFirstName();
	
	public abstract String getLastName();
	
	public abstract String getEmail();
	
	public abstract SortedMap<String, String> getImages();
	
	public abstract Status getStatus();
}
