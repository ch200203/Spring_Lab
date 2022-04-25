package com.example.restfulwebservice.user;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserController {
	
	// @Autowired
	private UserDaoService service;
	
	public UserController(UserDaoService service) {
		this.service = service;
	}
	
	@GetMapping("/users")
	public List<User> retrieveAllUsers() {return service.fincAll();	}

	@GetMapping("/users2")
	public ResponseEntity<CollectionModel<EntityModel<User>>> retrieveAllUserList2() {
		List<EntityModel<User>> result = new ArrayList<>();
		List<User> users = service.fincAll();

		/*
		 EntityModel<T>를 반환하는 컨트롤러 매핑 메소드 구현
		 1. 담고싶은 객체를 인자로 받은 EntityModel 객체 생성
		 2. 생성한 객체에 링크 추가

		 EntityModel<T>
		 	도메인 객체를 감싸고 그에 링크를 추가하는 객체
			 add() : 링크추가 메서드 ex) sampleEntityModel.add(new Link("https://myhost/people/42")); 처럼 링크 인스턴스 추가 가능

		WebMvcLinkBuilder 
			linkTo( ) : 컨트롤러 클래스를 가리키는 WebMvcLinkBuilder 객체를 반환
			methodOn() : 타겟 메소드의 가짜 메소드 콜이 있는 컨트롤러 프록시 클래스를 생성 -> 직접 메소드 객체를 만드는 것 보다 유연한 표현 가능
		
		LinkBuilderSupport
			withSelfRel() : build() 의 역할 -> 현재 빌더 인스턴스를 self relationship으로 하여 링크 객체 생성
		 */
		for(User user : users){
			EntityModel entityModel = EntityModel.of(user);
			entityModel.add(linkTo(
					methodOn(this.getClass()).retrieveAllUsers()).withSelfRel());

			result.add(entityModel);
		}

		return ResponseEntity.ok(CollectionModel.of(result,
				linkTo(	methodOn(this.getClass()).retrieveAllUsers()).withSelfRel())); // => static 링크 걸기
	}
	
	// GET /users/1 or /users/10
	// 사용자 상세정보
	@GetMapping("/users/{id}")
	public ResponseEntity<EntityModel<User>> retrieveUser(@PathVariable int id) {
		User user =  service.findOne(id);

		if(user == null ) {
			throw new UserNotFoundException(String.format("ID[%s] not found", id));
		}

		// HATEOAS
		EntityModel entityModel = EntityModel.of(user);

		WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
		entityModel.add(linkTo.withRel("all-users"));

		return ResponseEntity.ok(entityModel);
	}

	
	@PostMapping("/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		User savedUser = service.save(user);
		
		URI location =  ServletUriComponentsBuilder.fromCurrentRequest()
			.path("{id}")
			.buildAndExpand(savedUser.getId())
			.toUri();
		
		 return ResponseEntity.created(location).build();
	}
	
	@DeleteMapping({"/users/{id}"})
	public void deleteUser(@PathVariable int id) {
		User user =  service.deleteById(id);
		
		if(user == null ) {
			throw new UserNotFoundException(String.format("ID[%s] not found", id));
		}
	}
}
