package com.example.restfulwebservice.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

@Service // Service 용도로 사용 Component도 괜찮음 -> 빈의 용도를 알면 정확하게 적어주는게 좋음 Service 처럼.
public class UserDaoService {
	private static List<User> users = new ArrayList<>();
	private static int usersCount = 3;
	
	// db 연동안해서 가상의 데이터 만들어줌
	static {
		users.add(new User(1, "Kenneth", new Date(),"pass1", "940922-111111"));
		users.add(new User(2, "Alice", new Date(), "pass2", "940922-111111"));
		users.add(new User(3, "Emma", new Date(), "pass3", "940922-111111"));
	}

	// ------------------------------------------------------------
	
	public List<User> fincAll() {
		return users;
	}
	
	public User save(User user) {
		if(user.getId() == null) {
			user.setId(++usersCount);
		}
		
		users.add(user);
		return user;
	}
	
	public User findOne(int id) {
		for (User user: users) {
			if(user.getId() == id) {
				return user;
			}
		}
		return null;
	}
	
	public User deleteById(int id) {
		Iterator<User> iterator = users.iterator(); // 열거형 타입의 데이터로 변환
		
		while (iterator.hasNext()) {
			User user = iterator.next();
			
			if(user.getId() == id) {
				iterator.remove();
				return user;
			}
		}
		
		return null;
	}
}
