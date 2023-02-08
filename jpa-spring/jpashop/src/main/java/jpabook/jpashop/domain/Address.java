package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    /**
     * JPA 스펙상 엔티티나 임베디드 타입은 자바 기본생성자를 public 또는 protected 로 설정해야한다.
     * -> JPA 구현 라이브러리가 객체 생성지 리플렉션과 같은 기술을 사용할 수 있도록 지원해 주어야한다.
     */
    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
