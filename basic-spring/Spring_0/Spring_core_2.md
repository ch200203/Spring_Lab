# Spring 핵심원리 기본편_2

## 관심사의 분리
---
```java
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository = new MemoryMemberRepository();
    private DiscountPolicy discountPolicy;
    // private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    // private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
    ...
}
```

`FixDiscountPolicy` -> `RateDiscountPolicy` 로 변경 이 때의 문제점

* DIP에서의 문제점 :
`OrderServiceImpl`은 `DiscountPolicy` 인터페이스에 의존하면서 DIP를 지킨것 처럼 보이나, 클래스 의존관계를 분석해보면  
    ```
    추상 의존 : `DiscountPolicy`  
    구현 클래스 : `FixDiscountPolicy`, `RateDiscountPolicy`
    ```
`OrderServiceImpl`이 `DiscountPolicy` 인터페이스 뿐만 아니라 `FixDiscountPolicy`인 구체 클래스도 함께 의존하고 있다. 따라서 **DIP 위반**


* OCP에서의 문제점 :
지금 코드는 기능을 확장해서 변경하려면, 클라이언트에 영향을 주게됨  
`FixDiscountPolicy` 를 `RateDiscountPolicy` 로 변경하는 순간 `OrderServiceImpl` 의
소스 코드도 함께 변경해야 한다! 따라서, **OCP 위반**

`private DiscountPolicy discountPolicy` 와 같이 변경한다
단, 이렇게만 구현하면 `NullPointerException`이 발생한다.
따라서, `OrderServiceImpl` 에 `DiscountPolicy` 의 구현 객체를 대신 생성하고 주입해야 한다.

## AppConfig의 등장
---
어플리케이션의 전체 동작 방식을 구석하기 위해 **구현 객체를 생성**하고, **연결**하는 책임을 가지는 별도의 설정 클래스를 만들어야함


```java
public class OrderServiceImpl implements OrderService {
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;
    
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    ...
}
```

- 설계 변경으로 `OrderServiceImpl` 은 `FixDiscountPolicy` 를 의존하지 않는다 => 단지 `DiscountPolicy` 인터페이스만 의존한다
- `OrderServiceImpl` 입장에서 생성자를 통해 어떤 구현 객체가 들어올지(주입될지)는 알 수 없다.
- `OrderServiceImpl` 의 생성자를 통해서 어떤 구현 객체을 주입할지는 오직 외부( `AppConfig` )에서
결정한다.
- `appConfig` 객체는 `DiscountPolicy`객체를 생성하고 그 참조값을 `OrderServiceImpl`을
생성하면서 생성자로 전달한다.

- `AppConfig`는 애플리케이션의 실제 동작에 필요한 구현 객체를 생성한다
- `AppConfig`는 생성한 객체 인스턴스의 참조(레퍼런스)를 생성자를 통해서 주입(연결)해준다.
- 따라서, 의존관계에 대한 고민은 외부에 맡기고 실행에만 집중하면 된다.
- 관심사의 분리: 객체를 생성하고 연결하는 역할과 실행하는 역할이 명확히 분리되었다.

- `AppConfig`의 등장으로 애플리케이션이 크게 사용 영역과, 객체를 생성하고 구성(Configuration)하는 영역으로 분리


## IoC, DI, 그리고 컨테이너

### IoC 제어역전

- AppConfig가 등장한 이후에 구현 객체는 자신의 로직을 실행하는 역할만 담당한다. 프로그램의
제어 흐름은 이제 AppConfig가 가져간다. 예를 들어서 `OrderServiceImpl` 은 필요한 인터페이스들을 호출하지만 어떤 구현 객체들이 실행될지 모른다.
- 프로그램의 제어 흐름을 직접 제어하는 것이 아니라 외부에서 관리하는 것을 제어의 역전(IoC)이라한다.

### 프레임워크 vs 라이브러리
- 프레임워크가 내가 작성한 코드를 제어하고, 대신 실행하면 그것은 프레임워크가 맞다. 
(JUnit)
- 반면에 내가 작성한 코드가 직접 제어의 흐름을 담당한다면 그것은 프레임워크가 아니라 라이브러리다

### 의존관계 주입 DI(Dependency Injection)

- `OrderServiceImpl` 은 `DiscountPolicy` 인터페이스에 의존한다. 실제 어떤 구현 객체가 사용될지는 모른다.
의존관계는 정적인 클래스 의존 관계와, 실행 시점에 결정되는 동적인 객체(인스턴스) 의존 관계 둘을 분리해서 생각해야 한다.