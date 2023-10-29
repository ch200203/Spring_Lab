package jpabook.jpashop.repository.order.query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        /*List<OrderQueryDto> result = findOrders();

        result.forEach(o -> {
            // collection 부분 직접 채워주기
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;*/

        // 람다표현식으로 변경
        return findOrders().stream()
            .peek(o -> o.setOrderItems(findOrderItems(o.getOrderId())))
            .collect(Collectors.toList());
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi, orderPrice, oi.count) "
                    + "from OrderItem oi "
                    + "join oi.item i "
                    + "where oi.order.id = :orderId", OrderItemQueryDto.class)
            .setParameter("orderId", orderId)
            .getResultList();
    }


    public List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) "
                    + "from Order o "
                    + "join o.member m "
                    + "join o.delivery d", OrderQueryDto.class)
            .getResultList();
    }

    /**
     * v4에서는 루프를 돌릴 때 마다 query가 날라갔는데
     * v5(findAllByDto_optimization) 최적화를 진행하게 되면 query가 딱 두번 날라감.
     * findOrders(); 쿼리 1번, List<OrderItemQueryDto> orderItems 쿼리 한번.
     * orderItems 쿼리를 가져오고 메모리에서 매칭을 해서 값을 세팅을 해줌.
     *
     * ToOne 관계들을 먼저 조회하고(findOrders()), 여기서 얻은 식별자 orderId로 ToMany 관계인 OrderItem 을 한꺼번 에 조회
     * MAP을 사용해서 매칭 성능 향상(O(1))
     */
    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi, orderPrice, oi.count) "
                    + "from OrderItem oi "
                    + "join oi.item i "
                    + "where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
            .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
        // orderItemQueryDto::getOrderId
        return orderItemMap;
    }

    private static List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
        return orderIds;
    }
}
