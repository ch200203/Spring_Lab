package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;

public class MemberApp {

    public static void main(String[] args) {
        // MemberService 호출 => 구현체 MemberServiceImpl 선택
        MemberService memberService = new MemberServiceImpl();

        // Member 객체 생성 => (Ctrl + ALT + V 단축키 사용)
        Member member = new Member(1L, "memberA", Grade.VIP);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);

        // 아래와 같이 테스트 하는 방법은 좋은 로직이 아님 => JUnit 테스트를 사용하는 것이 좋은 방법!
        System.out.println("new member = " + member.getName());
        System.out.println("findMember = " + findMember.getName());
    }

}
