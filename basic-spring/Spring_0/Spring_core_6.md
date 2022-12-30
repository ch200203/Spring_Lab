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


## 옵션처리

- 주입할 스프링빈이 없어도 동작해야할 때 구성
자동 주입대상을 옵션으로 처리하는 방법
- `@Autowired(required = false)` : 자동 주입할 대상이 없으면 수정자 메서드 자체가 호출이 안됨
- `org.springframework.lang.@Nullable` : 자동 주입할 대상이 없으면 null이 입력된다.
- `Optional<>` : 자동 주입할 대상이 없으면 `Optional.empty` 가 입력된다.

```java
//호출 안됨
@Autowired(required = false)
public void setNoBean1(Member member) {
 System.out.println("setNoBean1 = " + member);
}
//null 호출
@Autowired
public void setNoBean2(@Nullable Member member) {
 System.out.println("setNoBean2 = " + member);
}
//Optional.empty 호출
@Autowired(required = false)
public void setNoBean3(Optional<Member> member) {
 System.out.println("setNoBean3 = " + member);
}
```

## 생성자 주입을 선택해야 하는 이유

**불변**
- 의존관계 주입은 한번 일어나면 어플리케이션 종료시점까지 변하면 안된다.
- 수정자 주입을 사용하면, setXxx 메서드를 public으로 열어두어야 한다.
- 누군가 실수로 변경할수 도 있고, 변경하면 안되는 메서드를 열어두는 것은 좋은 설계 방법이 아니다.
- **생성자 주입은 객체를 생성할 때 딱 1번만 호출되어 불변하게 설계가 가능하다.**

**누락**
생성자 주입을 사용하면 주입 데이터를 누락하였을 경우 컴파일 오류가 발생한다. 
뿐만 아니라, IDE에서 어떤 값을 필수로 주입해야 하는지 알 수 있다.

```java
@Test
void createOrder() {
    OrderServiceImpl orderService = new OrderServiceImpl();
    orderService.createOrder(1L, "itemA", 10000);
}
```

**final 키워드**
- 생성자 주입을 사용하면 필드에 `final` 키워드를 사용할 수 있다. 
- 생성자에서 값이 설정되지 않는 오류를 컴파일 시점에서 막아준다.
- **컴파일 오류는 세상에서 가장 빠르고, 좋은 오류이다.**

```java
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    @Autowired
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
    }
```

| 참고 : 수정자 주입을 포함한 나머지 주입 방식은 모두 생성자 이후에 호출되므로, 필드에 `final` 키워드를
사용할 수 없다. 오직 **생성자 주입 방식만 `final` 키워드를 사용**할 수 있다.

**정리**
- 생성자 주입을 선택하는 가장 큰 이유는, 순수한 자바언어의 특징(객체지향)을 잘 살리는 방법이다.
- 기본으로 생성자 주입을 사용하고, 필수 값이 아닌 경우에는 수정자 주입방식을 옵션으로 부여하면 된다.
- 생성자 주입과 수정자 주입을 동시에 사용할 수 있다.
- 항상 생성자 주입을 선택해야한다. 
- **필드 주입 사용은 지양**해야 한다.
    - 테스트 코드에서 값을 할당 할 수가 없음
    - 스프링 컨테이너가 없이는 테스트가 불가능함
    - 딱딱한 어플리케이션이 되어버림