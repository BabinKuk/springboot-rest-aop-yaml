package org.babinkuk.validator;

import java.util.Arrays;

public enum ValidatorType {
	
	STUDENT,
	INSTRUCTOR,
	COURSE,
	REVIEW;
	
	public static ValidatorType valueOfIgnoreCase(String str) {
		return Arrays.stream(ValidatorType.values())
				.filter(e -> e.name().equalsIgnoreCase(str))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Cannot find enum constant for " + str));
	}
}
