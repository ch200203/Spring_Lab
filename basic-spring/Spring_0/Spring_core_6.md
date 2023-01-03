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

주입할 스프링 빈이 없어도 동작해야할 경우
자동 주입 대상을 옵션으로 처리하는 방법 
- `@Autowired(required=false)` : 자동 주입할 대상이 없으면 수정자 메서드 자체가 호출 안됨
- `org.springframework.lang.@Nullable` : 자동 주입할 대상이 없으면 수정자 메서드 자체가 호출이 안됨
- `Optional<>` : 자동 주입할 대상이 없으면 `Optional.empty`가 입력된다.

## 생성주입을 선택하야 하는 이유

스프링을 포함한 DI 프레임워크 대부분이 생성자 주입을 권장한다.

**불변**
- 대부분의 의존관계 주입은 한번 일어나면 애플리케이션 종료시점까지 의존관계를 변경할 일이 없다. 오히려
대부분의 의존관계는 애플리케이션 종료 전까지 변하면 안된다.(불변해야 한다.)
- 수정자 주입을 사용하면, setXxx 메서드를 public으로 열어두어야 한다.
- 누군가 실수로 변경할 수 도 있고, 변경하면 안되는 메서드를 열어두는 것은 좋은 설계 방법이 아니다.
- 생성자 주입은 객체를 생성할 때 딱 1번만 호출되므로 이후에 호출되는 일이 없다. 따라서 불변하게 설계할
수 있다.

**누락**
- 생성자 주입을 사용하면 주입 데이터를 누락 했을 때 컴파일 오류가 발생한다.
그리고 IDE에서 바로 어떤 값을 필수로 주입해야 하는지 알 수 있다

**final 키워드**
생성자 주입을 사용하면 필드에 `final` 키워드를 사용할 수 있다. 그래서 생성자에서 혹시라도 값이
설정되지 않는 오류를 컴파일 시점에 막아준다. 
- 기억하자! **컴파일 오류는 세상에서 가장 빠르고, 좋은 오류다**

| 참고: 수정자 주입을 포함한 나머지 주입 방식은 모두 생성자 이후에 호출되므로, 필드에 `final` 키워드를
사용할 수 없다. 오직 생성자 주입 방식만 `final` 키워드를 사용할 수 있다.

### 정리

- 생성자 주입 방식을 선택하는 이유는 여러가지가 있지만, 프레임워크에 의존하지 않고, 순수한 자바 언어의
특징을 잘 살리는 방법이기도 하다.
- 기본으로 생성자 주입을 사용하고, 필수 값이 아닌 경우에는 수정자 주입 방식을 옵션으로 부여하면 된다. 
생성자 주입과 수정자 주입을 동시에 사용할 수 있다.
- **항상 생성자 주입을 선택**해라! 가끔 옵션이 필요하면 수정자 주입을 선택해라. 
- **필드 주입은 사용하지 않는게 좋다.**

## 롬복과 최신트렌드

보통 생성자가 딱 1개만 있는 경우 `@Autowired`를 생략할 수 있음

- Lombok를 적용하면
- `@RequiredArgsConstructor` 기능을 사용하면 `final`이 붙은 필드를 모아서 생성자를 자동으로 만들어준다. (코드에는 보이지 않지만 실제 호출 가능하다.)

```java
@Component
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;
}
```

### 정리s
- 최근에는 생성자를 딱 1개 두고, `@Autowired` 를 생략하는 방법을 주로 사용한다. 
-  `@RequiredArgsConstructor` 함께 사용하면 기능은 다 제공하면서, 코드는 깔끔하게 사용할 수 있다.

## 조회 빈이 2개이상 - 문제

- `@Autowired`는 타입으로 조회한다.
- 타입으로 조회하면 선택된 빈이 2개 이상일 때 문제가 발생한다.
    - `NoUniqueBeanDefinitionException` 오류가 발생한다.
    - 이때 하위 타입으로 지정할 수 도 있지만, 하위 타입으로 지정하는 것은 DIP를 위배하고 유연성이 떨어진다.     그리고 이름만 다르고, 완전히 똑같은 타입의 스프링 빈이 2개 있을 때 해결이 안된다.
    **스프링 빈을 수동 등록**해서 문제를 해결해도 되지만, **의존 관계 자동 주입**에서 해결하는 여러 방법이 있다.

### @Autowired 필드명 매칭, @Qualifier, @Primary 사용

조회 대상 빈이 2개 이상일 때 해결 방법
- `@Autowired` 필드 명 매칭
- `@Qualifier` `@Qualifier`끼리 매칭 빈 이름 매칭 `@Primary` 사용

@Autowired 매칭 정리
1. 타입 매칭
2. 타입 매칭의 결과가 2개 이상일 때 필드 명, 파라미터 명으로 빈 이름 매칭

@Qualifier 사용
- `@Qualifier` 는 추가 구분자를 붙여주는 방법이다. 주입시 추가적인 방법을 제공하는 것이지 빈 이름을 변경하는 것은 아니다.
- 주입시에 `@Qualifier`를 붙여주고 등록한 이름을 적어준다.

```java
@Component
@Qualifier("mainDiscountPolicy")
public class RateDiscountPolicy implements DiscountPolicy {
    ...
}

@Autowired
public OrderServiceImpl(MemberRepository memberRepository,
                        @Qualifier("mainDiscountPolicy") DiscountPolicy
discountPolicy) {
    this.memberRepository = memberRepository;
    this.discountPolicy = discountPolicy;
}
```

`@Qualifier`정리
1. `@Qualifier`끼리 매칭
2. 빈 이름 매칭
3. `NoSuchBeanDefinitionException` 예외 발생

`@Primary` 사용
- `@Primary` 는 우선순위를 정하는 방법이다. `@Autowired` 시에 여러 빈이 매칭되면 `@Primary` 가 우선권을 가진다.
- `@Qualifier` 보다는 실무에서 사용빈도가 높음 -> 사용이 간편하기 떄문.

### @Primary, @Qualifier 활용
코드에서 자주 사용하는 메인 데이터베이스의 커넥션을 획득하는 스프링 빈이 있고, 코드에서 특별한 기능으로 가끔 사용하는 서브 데이터베이스의 커넥션을 획득하는 스프링 빈이 있다고 생각해보자. 메인 데이터베이스의 커넥션을 획득하는 스프링 빈은 `@Primary` 를 적용해서 조회하는 곳에서 `@Qualifier` 지정 없이 편리하게 조회하고, 서브 데이터베이스 커넥션 빈을 획득할 때는 `@Qualifier` 를 지정해서 명시적으로 획득 하는 방식으로 사용하면 코드를 깔끔하게 유지할 수 있다. 물론 이때 메인 데이터베이스의 스프링 빈을 등록할 때 `@Qualifier` 를 지정해주는 것은 상관없다.

**우선순위**
스프링은 자동보다는 수동이, 넒은 범위의 선택권 보다는 좁은 범위의 선택권이 우선 순위가 높다.
따라서 여기서도 `@Qualifier` 가 우선권이 높다.