package hello.core.member;

public class MemberServiceImpl implements MemberService {

    // private final MemberRepository memberRepository = new MemoryMemberRepository();
    // 실제 할당하는 부분이 추상화, 객체화에 모두 의존하고 있음 => DIP를 위반

    private final MemberRepository memberRepository;
    // DIP를 지킴, 추상화에만 의존함

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
