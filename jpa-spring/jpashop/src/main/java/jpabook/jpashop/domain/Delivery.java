package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // enum은 Enumerated 필요 -> ORDINAL 장애를 유발시킬 수 있음 꼭 String으로 넣을것
    private DeliveryStatus status; // READY, COMP
}
