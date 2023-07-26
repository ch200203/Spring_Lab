package jpabook.jpashop.api;

import java.util.List;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * xToNone 관계의 성능 최적화
 * (ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {

        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for(Order order : all) {
            order.getMember().getName(); // Lazy 강제 로딩
            order.getDelivery().getAddress(); // Lazy 강제 로딩
        }
        return all;
        /**
         * 기존 -> 순환참조의 문제
         * 양방향 참조된 객체의 경우 한쪽을 끊어야함 @JsonIgnore
         * ByeBuddyInterceptor Proxy가 가짜 객체를 만들어서
         * Lazy 로딩을 구현함 여기서 Hibernate가 제대로된 객체를 찾지 못해 문제가 됨.
         *
         * ***결과적으로는 엔티티를 절때 노출하면 안됨.***
         * 가장 간단하고 좋은 해결방법 -> DTO 로 변환해서 반환하면됨!
         *
         * 그렇다고 EAGER 즉시로딩을 설정하면 안됨. - 다른 API 에서도 강제로 EAGER 가 될 수 있음.
         * 뿐만 아니라 N+1 문제가 발생할 여지가 있다.
         * 항상 지연로딩을 사용하되, 성능 최적화가 필요한 경우 'fetch join'을 사용해서 개선해야함
         */
    }

    // @GetMapping("/api/v2/orders")

}
