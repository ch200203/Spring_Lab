package jpabook.jpashop.domain;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    @NotEmpty // @Valid 어노테이션 사용시 필수 값 지정
    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member") // order 테이블에서 매핑됨
    private List<Order> orders = new ArrayList<>();

}
