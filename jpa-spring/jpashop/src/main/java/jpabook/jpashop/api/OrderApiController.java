package jpabook.jpashop.api;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     * V1. 엔티티 직접 노출
     * - 엔티티가 변하면 API 스펙이 변한다.
     * - 트랜잭션 안에서 지연로딩 필요
     * - 양방향 연관관계 -> 순환참조 문제 발생가능 ->
     * @JsonIgnore
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }

        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream()
            .map(o -> new OrderDto(o))
            .collect(toList());

        return result;
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllByWithItem();
        List<OrderDto> result = orders.stream()
            .map(o -> new OrderDto(o))
            .collect(toList());

        return result;
    }

    /**
     *  - 컬렉션을 fetch join 하는경우 페이징이 불가능하다.
     *      컬렉션을 페치 조인하면 -> 1:N의 경우 N를 기준으로 row가 생성된다.
     *      이 경우 하이버네이트는 모든 DB 데이터를 읽고 메모리에서 페이징을 시도한다.
     *      최악의 경우에는 OutOfMemory 장애로 이어질 수 있다.
     *
     *  - 한계돌파
     *      NToOne 관계를 모두 페치조인한다. NToOne 관계는 row수를 증가 시키지 않는다.
     *      따라서, 페이징 쿼리에 영향을 주지 않는다.
     *      컬렉션은 지연로딩으로 조회한다.
     *      지연 로딩 성능 최적화를 위해 `hibernate.default_batch_fetch_size`, `@BatchSize`를 적용한다.
     *      `hibernate.default_batch_fetch_size` : global option
     *      `@BatchSize` : 개별 최적화
     *      이 옵션을 사용하면 컬렉션이나, 프록시 객체를 한꺼번에 설정한 size 만큼 IN 쿼리로 조회한다.
     *
     *  V3.1 엔티티를 조회해서 DTO로 변환 페이징 고려
     *  - NToOne 관계는 fetch Join으로 최적화
     *  - 컬렉션 관계는 `hibernate.default_batch_fetch_size`, `@BatchSize`
     *
     *  장점
     *      - 쿼리 호출 수가 `1+N` -> `1 + 1`로 최적화 된다.
     *      - join 보다 DB 데이터 전송량이 최적화 된다.
     *      - fetch join 방식과 비교해서 쿼리 호출수가 약간 증가하지만, DB 데이터 전송량이 감소
     *      - 컬렉션 페지조인은 불가능 하지만 이 방법은 페이징이 가능
     *  결론
     *      - ToOne 관계른 페치 조인해도 페이징에 영향을 주지 않음 따라서 ToOne 관계는 페치조인으로 쿼리수를 줄이고 해결하고,
     *      나머지(컬렉션) hibernate.default_batch_fetch_size 로 최적화 -> max 는 어지간하면 1000개...
     *      - 순간적으로 부하가 증가 할 수 있기 때문에, 잘 선택해야한다...
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
        @RequestParam(value = "offset", defaultValue = "0") int offset,
        @RequestParam(value = "limit", defaultValue = "100") int limit)
    {

        List<Order> orders = orderRepository.finAllWithMemberDelivery(offset, limit);

        List<OrderDto> result = orders.stream()
            .map(o -> new OrderDto(o))
            .collect(toList());

        return result;
    }

    /**
     * ToOne(N:1, 1:1)관계들을 먼저 조회하고, ToMany(1:N) 관계는 각각 별도로 처리한다
     *  - 이런 방식을 선택한 이유는
     *  - ToOne 관계는 조인해도 데이터 row 수가 증가하지 않는다.
     *  - ToMany 관계는 조인하면 row 수가 증가한다.
     * row 수가 증가하지 않는 ToOne 관계는 조인으로 최적화하기 쉬우므로 한번에 조회하고,
     * ToMany 관계는 최적화가 어려우므로 'findOrderItems()'같은 별도의 메서드로 처리한다.
     */
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    /**
     * v5 컬렉션 최적화
     * Query: 루트 1번, 컬렉션 1번
     * ToOne 관계들을 먼저 조회하고, 여기서 얻은 식별자 orderId로 ToMany 관계인 OrderItem 을 한꺼번 에 조회
     * MAP을 사용해서 매칭 성능 향상(O(1))
     *
     * Select의 양이 줄어든다는 장점이 있음
     */
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    /**
     * 장점
     *  - Query: 1번
     *
     * 단점
     *  - 쿼리는 한번이지만 조인으로 인해 DB에서 애플리케이션에 전달하는 데이터에 중복 데이터가 추가되 므로 상황에 따라 V5 보다 더 느릴 수 도 있다.
     *  - 애플리케이션에서 추가 작업이 크다.
     *  - 페이징 불가능(order를 기준으로는 불가능함, OrderItem은 가능)
     */
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
        return flats.stream()
            .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                    o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                    o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
            )).entrySet().stream()
            .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                e.getKey().getAddress(), e.getValue()))
            .collect(toList());
    }


    @Data
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                .map(OrderItemDto::new)
                .collect(toList());
        }
    }

    @Data
    static class OrderItemDto {

        private String itemName; // 상품 명
        private int orderPrice; // 주문가격
        private int count; // 주문수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

}
