package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        /**
         * EntityManagerFactory -> 하나만 생성해서 어플리케이션 전체에서 공유
         */
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        /**
         * 엔티티 매니저는 쓰레드별로 생성해서 사용
         */
        EntityManager em = emf.createEntityManager();

        /**
         * JPA 모든 데이터 변경은 트랜잭션 안에서 실행되어야 한다.
         */
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // code

        try {
/*
            Member member = new Member();
            member.setId(1L);
            member.setName("HelloA");

            em.persist(member);
*/
/*
            // find
            Member findMember = em.find(Member.class, 1L);
            // delete
            em.remove(findMember)
            // update -> save 함수를 호출 할 필요가 없음
            findMember.setName("HelloJPA")
*/
            // JPQL -> 객체지향 쿼리
/*            List<Member> result = em.createQuery("select m from Member", Member.class)
                    .setFirstResult(1)
                    .setMaxResults(5)
                    .getResultList();*/

            // 비영속
            Member member = new Member();
            member.setId(100L);
            member.setName("HelloJPA");
            // 영속
            System.out.println("===BEFORE===");
            em.persist(member);
            em.detach(member); // 준영속 상태태로 객체를 영속성에서 분리
            System.out.println("===AFTER===");
            // 트랜잭션에서 커밋하는 순간 쿼리가 날라감.
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}