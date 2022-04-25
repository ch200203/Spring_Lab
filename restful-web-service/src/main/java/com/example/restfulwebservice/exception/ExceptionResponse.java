package com.example.restfulwebservice.exception;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/*
 * 	에러처리를 위한 클래스
*/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
	private Date timestamp;
	private String message;
	private String details;
}
