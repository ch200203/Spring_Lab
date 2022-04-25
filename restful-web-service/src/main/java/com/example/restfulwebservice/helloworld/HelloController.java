package com.example.restfulwebservice.helloworld;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	@Autowired
	private MessageSource messageSource;
	
	// GET
	// hello-world (endpoint)
	// @RequestMapping(method=RequestMethod.GET, path="hello-world") <- 기존 사용방식
	@GetMapping(path = "/hello-world")
	public String helloWorld() {
		return "Hello World";
	}
	
	
	// alt + enter(인텔리제이 기준)
	@GetMapping(path = "/hello-world-bean")
	public HelloWorldBean helloWorldBean() {
		return new HelloWorldBean("Hello World");
	}
	
	@GetMapping(path = "/hello-world-bean/path-variable/{name}")
	public HelloWorldBean helloWorldBean(@PathVariable String name) {
		return new HelloWorldBean(String.format("Hello World, %s ", name));
	}
	
	@GetMapping(path= "/hello-world-internationalized")
	public String helloworldinternationalized(
			@RequestHeader(name="Accept-Language", required = false) Locale locale) {
		return messageSource.getMessage("greeting.message", null, locale);
	}

}
