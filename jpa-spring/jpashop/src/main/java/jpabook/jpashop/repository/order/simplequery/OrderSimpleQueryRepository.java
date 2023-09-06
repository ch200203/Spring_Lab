package jpabook.jpashop.repository.order.simplequery;

import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) "
                    + "from Order o"
                    + " join o.member m"
                    + " join o.delivery d", OrderSimpleQueryDto.class)
            .getResultList();
    }

    /**
     * 기존 Repository 는 보통 순수한 엔티티를 조회하는데 사용함
     * 복잡한 Query를 위한 리포지토리를 새로 만드는 것이 유지보수에 더 좋음
     */

}
