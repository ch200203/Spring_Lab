package jpabook.jpashop.repository;

import jdk.jfr.StackTrace;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;

@Getter @StackTrace
public class OrderSearch {

    private String memberName; // 회원 이름
    private OrderStatus orderStatus; // 주문 상태[ORDER, CANCEL]

}
