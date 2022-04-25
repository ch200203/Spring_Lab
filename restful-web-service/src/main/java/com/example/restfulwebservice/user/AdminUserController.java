package com.example.restfulwebservice.user;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.BeanUtils;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminUserController {

	// @Autowired
	private UserDaoService service;

	public AdminUserController(UserDaoService service) {
		this.service = service;
	}
	
	@GetMapping("/users")
	public MappingJacksonValue retrieveAllUsers() {
		List<User> users = service.fincAll();

		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter
				.filterOutAllExcept("id", "name", "joinDate","password", "ssn");

		FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfo", filter); // User domain의 filter이름

		MappingJacksonValue mapping = new MappingJacksonValue(users);
		mapping.setFilters(filters); // mapping에 필터 적용.

		return mapping;
	}
	
	// GET /admin/users/1 -> /admin/v1/user/1
	// @GetMapping("/v1/users/{id}")
	// @GetMapping(value = "/users/{id}/", params = "version=1")
	// @GetMapping(value = "/users/{id}", headers = "X-API-VERSION=1")
	@GetMapping(value = "/v1/users/{id}", produces = "application/vnd.company.appv1+json")
	public MappingJacksonValue retrieveUserV1(@PathVariable int id) {
		User user =  service.findOne(id);
		
		if(user == null ) {
			throw new UserNotFoundException(String.format("ID[%s] not found", id));
		}

		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter
				.filterOutAllExcept("id", "name", "password", "ssn");

		FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfo", filter); // User domain의 filter이름

		MappingJacksonValue mapping = new MappingJacksonValue(user);
		mapping.setFilters(filters); // mapping에 필터 적용.

		return mapping;
	}

	//@GetMapping("/v2/users/{id}")
	// @GetMapping(value = "/users/{id}/", params = "version=2")
	// @GetMapping(value = "/users/{id}", headers = "X-API-VERSION=2")
	@GetMapping(value = "/v2/users/{id}", produces = "application/vnd.company.appv2+json")
	public MappingJacksonValue retrieveUserV2(@PathVariable int id) {
		User user =  service.findOne(id);

		if(user == null ) {
			throw new UserNotFoundException(String.format("ID[%s] not found", id));
		}

		// User -> UserV2
		UserV2 userV2 = new UserV2();
		BeanUtils.copyProperties(user, userV2); // id, name, joinDate, password, ssn
		userV2.setGrade("VIP");

		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter
				.filterOutAllExcept("id", "name", "joinDate", "grade");

		FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfoV2", filter); // User domain의 filter이름

		MappingJacksonValue mapping = new MappingJacksonValue(userV2);
		mapping.setFilters(filters); // mapping에 필터 적용.

		return mapping;
	}
}