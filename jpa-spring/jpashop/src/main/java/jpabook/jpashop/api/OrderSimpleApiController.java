package jpabook.jpashop.api;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * xToNone 관계의 성능 최적화 (ManyToOne, OneToOne) Order Order -> Member Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {

        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
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

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        // 실제로는 List가 아니라 Result로 반환을 해야 함
        // ORDER 2개
        // N + 1 문제 -> 1 + 회원 N + 배송 N
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
            .map(o -> new SimpleOrderDto(o))
            .collect(Collectors.toList());

        return result;
        /*
        return orderRepository.findAllByString(new OrderSearch()).stream()
            .map(SimpleOrderDto::new)
            .collect(toList());
        */

        /**
         * orderV1 과 orderV2의 문제점
         * 쿼리가 1 + N + N 번 실행된다.
         * N + 1 문제가 발생함
         */
    }

    /**
     * V3. 엔티티를 조회해서 DTO로 변환 (fetch join 사용) - fetch join으로 쿼리 1번 호출 * 자세한 내용은 기본편의 fetch 조인을 참고
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.finAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
            .map(o -> new SimpleOrderDto(o))
            .collect(toList());
        return result;
    }

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4() {
        return orderRepository.findOrderDtos();
    }

    /**
     * 정리
     * <p>
     * 엔티티를 DTO로 변환하거나, DTO로 바로 조회하는 두가지 방법은 각각 장단점이 있다. 둘중 상황에 따라서 더 나은 방법을 선택하면된다. 엔티티로 조회하면 리포지토리
     * 재사용성도 좋고, 개발도 단순해진다. 따라서 권장하는 방법은 아래와 같다.
     * <p>
     * 쿼리 방식 선택 권장 순서 1. 엔티티를 DTO로 변환하는 방법을 선택한다. 2. 필요하면 페치 조인으로 성능을 최적화 한다 -> 대부분의 성능 이슈가 해결된다. 3.
     * 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다. 4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을
     * 직접 사용한다.
     */

    @Data
    @AllArgsConstructor
    static class SimpleOrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }
}
