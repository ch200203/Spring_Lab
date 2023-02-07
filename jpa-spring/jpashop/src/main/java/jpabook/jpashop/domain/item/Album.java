package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("A") // 괄호안에 입력하지 않으면 않으면 클래스명으로 들어감
@Getter
@Setter
public class Album extends Item {

    private String artist;
    private String etc;
}
