package hello.core.member;

import java.util.HashMap;
import java.util.Map;
    import org.springframework.stereotype.Component;

@Component
public class MemoryMemberRepository implements MemberRepository {

    // 동시성 이슈가 있을 수 있기 때문에 => 실무에서는 ConcurrnetHashMap을 보통 사용 
    private static Map<Long, Member> store = new HashMap<>();

    @Override
    public void save(Member member) {
        store.put(member.getId(), member);
    }

    @Override
    public Member findById(Long memberId) {
        return store.get(memberId);
    }
}
