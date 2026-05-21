package com.Project.Mechanic.ExceptionHandler;

public class BadRequestException extends RuntimeException{
	
	public BadRequestException(String message) {
		
		super(message);
	}

}
