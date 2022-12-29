# Spring 핵심원리 기본편_6

## 다양한 의존관계 주입방법

- 생성자 주입
- 수정자 주입(setter 주입)
- 필드 주입
- 일반 메서드 주입

### 생성자 주입

- 생성자를 통해서 의존관계를 주입 받는 방법
- 특징
    - 생성자 호출시점에 딱 1번만 호출되는 것이 보장
    - **불변, 필수** 의존관계에 사용
- 빈을 등록하면서 의존관계 주입이 같이 일어남
```java
private final MemberRepository memberRepository;
private DiscountPolicy discountPolicy;

@Autowired
public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
    this.discountPolicy = discountPolicy;
    this.memberRepository = memberRepository;
}

```
**생성자가 딱 1개만 있으면 @AutoWired를 생략해도 자동 주입된다.**
단, 스프링 빈에만 해당

### 수정자 주입(setter 주입)
- setter라 불리는 필드의 값을 변경하는 수정자 메서드를 통해서 의존관계를 주입하는 방법이다.
- 생성자를 
- 특징
    - **선택, 변경** 가능성있는 의존관계에 사용
    - 자바 빈 프로퍼티 규약 수정자 메소드 방식을 사용하는 방법
    - ex ) set + 필드명, get + 필드명
```java
    private DiscountPolicy discountPolicy;

    @Autowired
    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }
```

### 필드 주입
- 필드에 바로 주입하는 방법
- 특징
    - 코드가 간결하다는 장점이 있으나, **외부에서 변경이 불가능**하여, 테스트가 어렵다는 단점이 있다.
    - DI 프레임 워크가 없으면 아무것도 할 수 없음
    - ***사용하지 않는 것을 강력하게 권장***
        - 어플리케이션의 실제 코드와 관계없는 테스트 코드
        -  스프링 설정을 목적으로하는 `@Configuration` 같은 곳에서만 특별한 용도로 사용
```java
@Component
public class OrderServiceImpl implements OrderService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private DiscountPolicy discountPolicy;
}
```

### 일반 메서드 주입
- 일반 메서드를 통해서 주입을 받을 수 있다.
- 특징
    - 한번에 여러 필드를 주입 받을 수 있다.
    - 일반적으로 잘 사용하지는 않음

```java
    private MemberRepository memberRepository;
    private DiscountPolicy discountPolicy;

    @Autowired
    public void init(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
```
| 단, 의존관계 자동주입은 스프링 컨테이너가 관리하는 빈에서만 작동한다.