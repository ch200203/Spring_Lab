package jpabook.jpashop.repository.order.query;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
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
                + "where oi.order.id = :orderId", OrderItemQueryDto.class )
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
}
