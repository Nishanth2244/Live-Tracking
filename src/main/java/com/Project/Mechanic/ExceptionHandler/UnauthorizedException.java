package com.Project.Mechanic.ExceptionHandler;

public class UnauthorizedException extends RuntimeException {
	
	public UnauthorizedException(String message) {
		super(message);
	}

}
