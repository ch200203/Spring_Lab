package hello.core.order;

import static org.junit.jupiter.api.Assertions.*;

import hello.core.discount.FixDiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderServiceImplTest {
    
    @Test
    void createOrder() {
        MemoryMemberRepository memoryMemberRepository = new MemoryMemberRepository();
        memoryMemberRepository.save(new Member(1L, "name", Grade.VIP));

        // 생성자 주입으로 하는 경우에는 new OrderServiceImpl(); 비워져 있을때, 컴파일 오류가 발생하여 쉽게 알아차릴 수 있음
        OrderServiceImpl orderService = new OrderServiceImpl(memoryMemberRepository, new FixDiscountPolicy());
        Order order = orderService.createOrder(1L, "itemA", 100000);

        Assertions.assertThat(order.getDiscountPrice()).isEqualTo(1000);
    }
    
}