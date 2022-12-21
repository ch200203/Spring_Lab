package hello.core.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MemberServiceTest {

    MemberService memberService = new MemberServiceImpl();

    /**
     * 테스트 코드작성은 필수적이다. 중요!
     */
    @Test
    void join() {
        // given => 테스트를 위해 준비하는 과정, 테스트용 변수, 입력 값 정의, Mock 객체를 정의
        Member member = new Member(1L, "memberA", Grade.VIP);

        // when => 실제 Action 하는 테스트 실행
        memberService.join(member);
        Member findMember = memberService.findMember(1L);

        // then => 테스트 검증과정, 예상한 값, 실행을 통해서 나온 값을 검증
        Assertions.assertThat(member).isEqualTo(findMember);
    }

}
