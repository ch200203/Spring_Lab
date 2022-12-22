package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository = new MemoryMemberRepository();
    private DiscountPolicy discountPolicy;
    // private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    // private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
    /*
        FixDiscountPolicy -> RateDiscountPolicy 로 변경
        이 때의 문제점,

        DIP에서의 문제점 :
        OrderServiceImpl은 DiscountPolicy 인터페이스에 의존하면서 DIP를 지킨것 처럼 보이나
        클래스 의존관계를 분석해보면
        추상 의존 : DiscountPolicy
        구현 클래스 : FixDiscountPolicy, RateDiscountPolicy
        OrderServiceImpl이 DiscountPolicy 인터페이스 뿐만 아니라 FixDiscountPolicy인
        구체 클래스도 함께 의존하고 있다. 따라서 DIP 위반

        ---

        OCP에서의 문제점 :
        지금 코드는 기능을 확장해서 변경하려면, 클라이언트에 영향을 주게됨
        FixDiscountPolicy 를 RateDiscountPolicy 로 변경하는 순간 OrderServiceImpl 의
        소스 코드도 함께 변경해야 한다! 따라서, OCP 위반


        private DiscountPolicy discountPolicy; 와 같이 변경한다
        단, 이렇게만 구현하면 NullPointerException이 발생한다.
        따라서, OrderServiceImpl 에 DiscountPolicy 의 구현 객체를 대신 생성하고 주입해야 한다.
     */

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice); // 단일 책임 원칙을 잘 지킨 사례

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
