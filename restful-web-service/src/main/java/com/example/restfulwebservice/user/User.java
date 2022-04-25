package com.example.restfulwebservice.user;

import java.util.Date;

import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor // 디폴트 생성자
// @JsonIgnoreProperties(value={"password"}) => 해당 값 숨겨줌
// @JsonFilter("UserInfo")
@ApiResponse(description = "사용자 상세 정보를 위한 도메인 객체")
public class User {
	private Integer id;
	
	@Size(min = 2, max=20, message = "Name은 2글자 이상 20글자 미만으로 입력해주세요")
	private String name;
	@Past
	private Date joinDate;

	//@JsonIgnore 해당 데이터 전달 배제
	private String password;
	private String ssn;
}
