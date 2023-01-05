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
public OrderServiceImpl(MemberRepository memberRepository,  @Qualifier("mainDiscountPolicy") DiscountPolicy discountPolicy) {
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


## 어노테이션 직접 만들기
`@Qualifier(mainDiscountPolicy)` 와 같이 문자를 직접 적으면 컴파일 타입시 체크가 불가능함
=> 이를 해결하기 위하여 어노테이션을 직접 만들 수 있음.

```java
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier("mainDiscountPolicy")
public @interface MainDiscountPolicy {
    ...
}
```


```java
@Component
@MainDiscountPolicy
public class RateDiscountPolicy implements DiscountPolicy {
    ...
}
```

```java
//생성자 자동 주입
@Autowired
public OrderServiceImpl(MemberRepository memberRepository,
 @MainDiscountPolicy DiscountPolicy discountPolicy) {
 this.memberRepository = memberRepository;
 this.discountPolicy = discountPolicy;
}
//수정자 자동 주입
@Autowired
public DiscountPolicy setDiscountPolicy(@MainDiscountPolicy DiscountPolicy 
discountPolicy) {
 this.discountPolicy = discountPolicy;
}
```

- 어노테이션에는 상속 개념이 없음.
- `@Qulifier` 뿐만 아니라 다른 애노테이션들도 함께 조합해서 사용할 수 있음
- `@Autowired` 도 재정의가 가능함.
- 단, 무분별하게 재정의를 남발하는 경우 유지보수에 악영향을 초래한다.

## 조회한 빈이 모두 필요할때 List, Map

- 해당타입의 스프링 빈이 모두 필요한 경우
    - ex) 클라이언트가 할인의 종류를 선택할 수 있는경우 -> 이러한 전략패턴을 간편하게 구현할 수 있음

```java
public class AllBeanTest {
    @Test
    void findAllBean() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class, DiscountService.class);
        DiscountService discountService = ac.getBean(DiscountService.class);
        
        Member member = new Member(1L, "userA", Grade.VIP);
        int discountPrice = discountService.discount(member, 10000, "fixDiscountPolicy");
        
        assertThat(discountService).isInstanceOf(DiscountService.class);
        assertThat(discountPrice).isEqualTo(1000);
    }
    
    static class DiscountService {
        private final Map<String, DiscountPolicy> policyMap;
        private final List<DiscountPolicy> policies;
        
        public DiscountService(Map<String, DiscountPolicy> policyMap, List<DiscountPolicy> policies) {
            this.policyMap = policyMap;
            this.policies = policies;
            System.out.println("policyMap = " + policyMap);
            System.out.println("policies = " + policies);
        }
 
        public int discount(Member member, int price, String discountCode) {
            DiscountPolicy discountPolicy = policyMap.get(discountCode);
            
            System.out.println("discountCode = " + discountCode);
            System.out.println("discountPolicy = " + discountPolicy);
            
            return discountPolicy.discount(member, price);
        }
    }
}
```

**로직분석**

- `DiscountService`는 Map으로 만는 DiscountPolicy를 주입받는다. 이 때, `rateDiscountPolicy`, `fixDiscountPolicy` 주입된다.
- `discount()` 메서드는 discountCode로 `fixDiscountPolicy`가 넘어 오면 Map에 해당 빈을 찾아서 실행 하는 구조이다.

**주입분석**
- `Map<String, DiscountPolicy>` : map의 key에 스프링 빈의 이름을 넣어주고 그 값으로 DiscountPolicy 타입으로 조회한 스프링 빈을 value로 담이 준다
- `List<DiscountPolicy>` : `DiscountPolicy` 타입으로 조회한 스프링 빈을 담아준다.

스프링 컨테이너를 생성하면서, 해당 컨테이너에 동시에 `AutoAppConfig`, ``DiscountService`를 스프링 빈으로 자동 등록한다.

## 자동, 수동의 올바른 실무 운용기준

### 편리한 자동기능 - 기본으로 사용
- 대부분 자동을 선호하는 추세
- 스프링은 `@Component` 뿐만 아니라, `@Controller`, `@Service`, `@Repository` 처럼 계층에 맞추어 일반적인 어플리케이션 로직을 자동으로 스캔할 수 있도록 지원한다.
- 스프링 부트 컴포넌트 스캔을 기본으로 사용 및 스프링 붙의 다양한 빈도 조건에 맞으면 자동으로 빈으로 등록한다.
- 관리할 빈이 많아져 설정 정보가 커지면 관리가 부담이 되기 때문에 자동 등록을 권장
- 자동 등록 빈을 사용하여도 OCP, DIP 원칙을 지킬 수 있다.

### 수동 빈 등록을 사용하는 경우
- 어플리케이션은 크게 업무로직, 기술로직으로 나눌 수 있음
- **업무로직 빈** : 컨트롤러, 서비스, 리포지토리 => 업무 로직 
- **기술지원 빈** : 기술적인 문제나 공통 관심사(AOP)를 처리할때 주로 사용, DB 연결, 공통로그 처리 처럼 업무 로직을 지원하기 위한 기술, 혹은 공통기술

- 업무로직은 자동 기능을 적극 사용하는 것이 좋다. 보통 문제가 발생해도 어떤 곳에서 문제가 발생했는지 명확하게 파악하기 쉽다.
- 기술 지원 로직은 업무 로직과 비교해서 그 수가 매우 적고, 보통 애플리케이션 전반에 걸쳐서 광범위하게 영향을 미친다. 그래서 이런 기술 지원 로직들은 가급적 수동 빈 등록을 사용해서 명확하게 드러내는 것이 좋다.

**애플리케이션에 광범위하게 영향을 미치는 기술 지원 객체는 수동 빈으로 등록해서 딱! 설정 정보에 바로
나타나게 하는 것이 유지보수 하기 좋다.**

### 비즈니스 로직 중 다형성을 활용할 때의 처리
- 의존관계 자동 주입 - 조회한 빈이 모두 필요할 때, List, Map의 경우
- 자동 등록을 사용하고 있기 때문에 파악하려면 여러 코드를 찾아봐야 한다.
- 이런 경우 수동 빈으로 등록하거나 또는 자동으로하면 특정 패키지에 같이 묶어두는게 좋다.

- 예시코드
```java
@Configuration
public class DiscountPolicyConfig {
 
 @Bean
 public DiscountPolicy rateDiscountPolicy() {
    return new RateDiscountPolicy();
 }
 @Bean
 public DiscountPolicy fixDiscountPolicy() {
    return new FixDiscountPolicy();
 }
}
```
등록을 사용하고 싶으면 파악하기 좋게 DiscountPolicy 의 구현 빈들만 따로 모아서 특정 패키지에
모아두자.

- 단, **스프링과 스프링 부트가 자동으로 등록하는 수 많은 빈들은 예외**
- 스프링 부트의 경우 `DataSource`같은 데이터베이스 연결에 사용하는 기술지원 로직까지 자동으로 등록한다.
- 반면, **스프링 부트가 아니라 내가 직접 기술 지원 객체를 스프링 빈으로 등록한다면 수동으로 등록해서 명확하게 드러내는 것이 좋다**

### 정리

- 편리한 자동 기능을 기본으로 사용하자
- 직접 등록하는 기술 지원 객체는 수동 등록
- 다형성을 적극 활용하는 비즈니스 로직은 수동 등록을 고려해본다.