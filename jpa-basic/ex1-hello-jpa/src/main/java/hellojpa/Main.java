package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

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



            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}