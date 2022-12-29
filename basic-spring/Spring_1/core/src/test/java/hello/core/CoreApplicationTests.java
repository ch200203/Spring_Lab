package hello.core;

import hello.core.order.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CoreApplicationTests {

	
	@Autowired
	OrderService orderService; // 필드 주입 허용 예시
	
	@Test
	void contextLoads() {
	}

}
