package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll() {
        return em.createQuery("select o from Order o", Order.class)
            .getResultList();
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {

        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
            .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    // 문제가 되는 부분
    public List<Order> findALlOrigin(OrderSearch orderSearch) {
        return em.createQuery("select o from Order o join o.member m" +
                " where o.status = :status" +
                " and m.name like :name", Order.class)
            .setParameter("status", orderSearch.getOrderStatus())
            .setParameter("name", orderSearch.getMemberName())
            // .setFirstResult() -> 시작 값 페이징 처리에 활용
            .setMaxResults(1000) // 최대 1000건
            .getResultList();
    }

    /**
     * JPA Criteria JPA 표준 스펙이지만 실무에서 사용하기에 너무 복잡 JPA 에서 동적 쿼리생성을 위한 기능 -> 실무에서 활용하지 않음 따라서 이를 해결하기
     * 위해 Querydsl 이 사용되게됨
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                orderSearch.getOrderStatus());
            criteria.add(status);
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건

        return query.getResultList();
    }

    /**
     * 재사용성이 findOrderDtos 에 비해 높음
     */
    public List<Order> finAllWithMemberDelivery() {
        return em.createQuery(
            "select o from Order o "
                + "join fetch o.member m "
                + "join fetch o.delivery d ", Order.class
        ).getResultList();
    }

    /**
     * new 명령어를 사용하여 JPQL의 결과를 DTO로 즉시 변환
     * SELECT 절에서 원하는 데이터를 직접 선택하기 떄문에 DB + 어플리케이션 네트웍 용량 최적화(생각보다 미비)
     * 리포지토리 재사용성이 떨어짐, API 스펙에 맞춘 코드가 리포지토리에 들어가는 단점이 있음.
     */
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery("select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) "
                + "from Order o"
                + " join o.member m"
                + " join o.delivery d", OrderSimpleQueryDto.class)
            .getResultList();
    }

    /**
     * JPA distinct 키워드의 기능
     * 1. SQL 에 distinct
     * 2. Entity 의 객체가 중복인 경우 걸러서 컬렉션에 담아줌
     *
     * 참고: 컬렉션 페치 조인을 사용하면 페이징이 불가능하다.
     * 하이버네이트는 경고 로그를 남기면서 모든 데이 터를 DB에서 읽어오고, 메모리에서 페이징 해버린다(매우 위험하다).
     *      -> 메모리 문제가 발생할 가능성이 매우크다.
     * 컬렉션 페치 조인은 1개만 사용할 수 있다. (NToMany)
     * 컬렉션 둘 이상에 페치 조인을 사용하면 안된다.
     * 데이터가 부정합하게 조회될 수 있다.
     */
    public List<Order> findAllByWithItem() {
        return em.createQuery(
                "select distinct o from Order o"
                    + " join fetch o.member m"
                    + " join fetch o.delivery d"
                    + " join fetch o.orderItems oi"
                    + " join fetch oi.item i", Order.class
            ). getResultList();
    }
}
