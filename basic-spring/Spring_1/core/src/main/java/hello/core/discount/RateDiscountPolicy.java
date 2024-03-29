package hello.core.discount;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.order.MainDiscountPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
// @Primary
// @Qualifier("mainDiscountPolicy")
@MainDiscountPolicy
public class RateDiscountPolicy implements DiscountPolicy {

    private int discountPercent = 10; // 10% 할인률 적용

    @Override
    public int discount(Member member, int price) {
        if(member.getGrade() == Grade.VIP) {
            return price * discountPercent / 100;
        } else  {
            return 0;
        }
    }
}
