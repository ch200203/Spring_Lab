package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if(item.getId() == null) {
            em.persist(item); // 신규로 등록
        } else {
            em.merge(item); // 아이디가 있는경우 -> 이미 db에 등록된 객체임 -> merge 강제 업데이튼
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from  Item i", Item.class)
                .getResultList();
    }

}
