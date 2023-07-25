package jpabook.jpashop.api;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController // ResponseBody + Controller
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
        /*
            - 현재
                - 응답 값으로 엔티티를 직접 외부에 노출하고 있음
            - 문제점
                - 엔티티에 프레젠테이션 계층을 위한 로직이 추가됨
                - 엔티티의 모든 값이 노출되게 됨
                - 응답 스펙을 맞추기 위한 로직이 추가됨(@JsonIgnore, 별도의 뷰로직)
                - 실무에서는 같은 엔티티에 대해 API가 용도에 따라 다양하게 만들어 지는데, 한 엔티티에 각각의
                  API를 위한 프레젠테이션 응답 로직을 담기는 어렵다.
                - 엔티티가 변경되면 API 스펙이 변한다.
                - 추가로 컬렉션을 직접 반환하면 항후 API 스펙을 변경하기 어렵다.
             - 해결책
                - API 응답 스펙에 맞춰 별도의 DTO를 반환한다.
                - 절때 엔티티를 외부에 노출하지 말 것.
         */
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
            .map(m -> new MemberDto(m.getName()))
            .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    /**
     * 등록 V1: 요청 값으로 Member 엔티티를 직접 받는다. 문제점 - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다. - 엔티티에 API 검증을 위한 로직이
     * 들어간다. (@NotEmpty 등등) - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위 한 모든 요청 요구사항을 담기는
     * 어렵다. - 엔티티가 변경되면 API 스펙이 변한다. 결론 - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받는다.
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 등록 V2: 요청 값으로 Member 엔티티 대신에 별도의 DTO를 받는다.
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
        @PathVariable("id") Long id,
        @RequestBody @Valid UpdateMemberRequest request) {

        memberService.updateMember(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    /**
     * 엔티티와 프레젠 테이션 계층을 분리하기위한 DTO 생성
     */
    @Data
    static class CreateMemberRequest {

        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {

        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    @AllArgsConstructor // DTO 에는 Lombok 써도 괜찮음
    static class UpdateMemberResponse {

        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest {

        private String name;
    }

}
