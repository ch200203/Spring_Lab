# Spring 핵심원리 기본편_7

##  빈 스코프
- 스프링 빈이 존재할 수 있는 범위

**스프링 스코프의 종류**
- 싱글톤 : 기본스코프, 스프링 컨테이너의 시작 ~ 종료까지 유지되는 가장 넓은 범위의 스코프
- 프로토타입 : 스프링 컨테이너는 프로토 타입 빈의 생성과 의존관계 주입까지만 관여하는 매우 짧은 범위의 스코프
- 웹 관련 스코프
    - **request** : 웹 요청이 들어오고 나갈때 까지 유지되는 스코프
    - **session** : 웹 셔센이 생성되고 종료될 때 까지 유지되는 스코프
    - **application** : 웹 서블릿 컨텍스트와 같은 범위로 유지되는 스코프

- 빈스코프 지정방법
    - **컴포넌트 스캔 자동 등록**
    ```java
    @Scope("prototype")
    @Component
    public class HelloBean {
        ...
    }
    ```

    - **수동 등록**
    ```java
    @Scope("prototype")
    @Bean
    PrototypeBean HelloBean() {
        return new HelloBean();
    }
    ```
## 프로토타입 스코프

싱글톤 스코프의 빈을 조회하면 스프링 컨테이너는 항상 같은 인스턴스의 스프링 빈을 반환한다.반면에 프로토타입 스코프를 스프링 컨테이너에 조회하면 스프링 컨테이너는 항상 새로운 인스턴스를 반환한다.

- 싱글톤 빈 요청
    1. 싱글톤 스코프의 빈을 스프링 컨테이너에 요청한다.
    2. 스프링 컨테이너는 본인이 관리하는 스프링 빈을 반환하다.
    3. 이후 스프링 컨테이너에 같은 요청이 와도 같은 객체 인스턴스의 스프링 빈을 반환한다.
- 프로토타입 빈 요청
    1. 프토로타입 스코프의 빈을 스프링 컨테이너에 요청한다.
    2. 스프링 컨테이너는 이 시점에 스프링 빈을 생성하고, 필요한 의존관계를 주입힌다.
    3. 스프링 컨테이너는 생성한 프로토타입 빈을 클라이언트에 반환한다.
    4. 이후에 같은 요청이 오면 항상 새로운 프로토 타입 빈을 생성하여 반환한다.

### 정리
- 스프링 컨테이너는 빈을 생성, 의존관계 주입, 초기화까지만 처리한다.
- **클라이언트에 빈을 반환하고 스프링 컨테이너는 더 이상 빈을 관리하지 않는다.**
- 프로토타입 빈을 관리할 책임은 빈을 받은 클라이언트에 있다.
- 따라서, `@Destory`와 같은 종료 메서드가 호출되지 않는다.


### 프로토타입 빈의 특징 정리
- 스프링 컨테이너에 요청할 때 마다 새로 생성된다.
- 스프링 컨테이너는 프로토타입 빈의 생성과 의존관계 주입 그리고 초기화까지만 관여한다.
- 종료 메서드가 호출되지 않는다.
- 그래서 프로토타입 빈은 프로토타입 빈을 조회한 클라이언트가 관리해야 한다. 종료 메서드에 대한 호출도 **클라이언트가 직접 해야한다.**

## 프로토타입 스코프 - 싱글톤 빈과 함께 사용시 문제점

싱글톤 빈과 함께 사용할 때는 의도한 대로 잘 동작하지 않으므로 주의해야 한다.

### 프로토타입 빈 직접 요청

- 스프링 컨테이너에 프로토타입 직접 요청1
     1. 클라이언트A는 스프링 컨테이너에 프토로타입 빈을 요청한다.
     2. 스프링 컨테이너는 프로토타입 빈을 새로 생성하여 반환(**x01**)한다. 해당 빈의 count 필드 값은 0이다.
     3. 클라이언트는 조회한 프로토타입 빈에 `addCount()`를 호춯하면서 count 필드를 +1 한다.
     4. 결과적으로 프로토타입빈(**x01**)의 count는 1이 된다.

- 스프링 컨테이너에 프로토타입 빈 직접 요청2
    1. 클라이언트B는 스프링 컨테이너에 프로토타입 빈을 요청한다.
    2. 스프링 컨테이너는 프로토타입 빈을 새로 생성해서 반환(**x02**)한다. 해당 빈의 필드 값은 0이다.
    3. 클아이언트는 조회한 프로토타입 빈에 `addCount()`를 호출하면서 count 필드를 +1 한다.
    4. 결과적으로 프로토타입 빈(**x02**)의 count는 1이 된다.


## 싱글톤 빈에서 프로토타입 빈 사용
`clientBean` 이라는 싱글톤 빈이 의존관계 주입을 통해서 프로토타입 빈을 주입받아서 사용하는 예시

### 싱글톤에서 프로토타입 빈 사용
- `clientBean`은 싱글톤이므로, 보통 스프링 컨테이너 생성 시점에서 함께 생성되고, 의존관계 주입도 발생한다.
    1. `clientBean`은 의존관계 자동주입을 사용한다. 주입 시점에서 스프링 컨테이너에 프로토타입 빈을 요청한다.
    2. 스프링 컨테이너는 프로토타입 빈을 생성해서 `clientBean`에 반환한다. 프로토타입 빈의 `count`필드 값은 0이다.

-  이제 `clientBean`은 프로토타입 빈을 내부 필드에 보관한다.(정환하게는 참조값)
- 클라이언트A는 `clientBean`을 스프링 컨테이너에 요청해서 받는다. 싱글톤으미로 항상 같은 `clientBean`이 반환된다.

    3. 클라이언트A는 `clientBean.logic()`을 호출한다.
    4. `clientBean`은 prototype의 `addCount`를 호출해서 프로토타입 빈의 count를 증가한다. &rarr; count 값은 1이 된다.
- 클라이언트B는 `clientBean`을 스프링 컨테이너에 요청해서 받는다. 싱글톤으미로 항상 같은 `clientBean`이 반환된다.
- 여기서 **clientBean이 내부에 가지고 있는 프로토타입 빈은 이미 과거에 주입이 끝난 빈이다. 주입 시점에 스프링 컨테이너에 요청해서 프로토타입 빈이 새로 생성이 된 것이지, 사용 할 때마다 새로 생성되는 것이 아니다!**

    5. 클라이언트B는 `clientBean.logic()`을 호출한다.
    6. `clientBean`은 PrototypeBean의 `addCount`를 호출해서 프로토타입 빈의 count 를 증가한다. 원래 count 값이 1이었으므로 2가 된다.

스프링은 일반적으로 싱글톤 빈을 사용하므로, 싱글톤 빈이 프로토타입 빈을 사용하게 된다. 그런데 싱글톤 빈은 생성 시점에만 의존관계 주입을 받기 때문에, 프로토타입 빈이 새로 생성되기는 하지만, 싱글톤 빈과 함께 계속 유지되는 것이 문제다.


> 참고: 여러 빈에서 같은 프로토타입 빈을 주입 받으면, 주입 받는 시점에 각각 새로운 프로토타입 빈이 생성된다. 예를 들어서 clientA, clientB가 각각 의존관계 주입을 받으면 각각 다른 인스턴스의 프로토타입 빈을 주입 받는다.
> clientA &rarr; prototypeBean@x01
> clientB &rarr; prototypeBean@x02
> 물론 사용할 때 마다 새로 생성되는 것은 아니다.

## 프로토타입 스코프 - 싱글톤 빈과 함께 사용시 Provider로 문제 해결

사용할 때 마다 항상 새로운 프로토타입 빈을 생성하는 방법

### 스프링 컨테이너에 요청

가장 간단한 방법은 싱글톤 빈이 프로토타입을 사용할 때 마다 스프링컨테이너에 새로 요청

```java
@Autowired
private ApplicationContext ac;

public int logic() {
    PrototypeBean prototypeBean = ac.getBean(PrototypeBean.class);
    prototypeBean.addCount();
    int count = prototypeBean.getCount();
    return count;
}
```
- 실행해보면 `ac.getBean()`을 통해서 항상 새로운 프로토타입이 생성된다.
- 의존관계를 외부에서 주입 받는게 아니라 필요한 의존관계를 찾는 것을 Denpendency Lookup(DL) 의존관계 조회 라고 한다.
- 이렇게 항상 어플리케이션 컨텍스트 전체를 주입받게 되면, 스프링 컨테이너에 종속적인 코드가 되고, 단위 테스트가 어려워 진다.

### ObjectFactory, ObjectProvider

지정한 빈을 컨테이너에서 대신 찾아주는 DL 서비스를 제공하는 것이 바로 `ObjectProvider` 이다. 

```java
@Autowired
private ObjectProvider<PrototypeBean> prototypeBeanProvider;
public int logic() {
    PrototypeBean prototypeBean = prototypeBeanProvider.getObject();
    prototypeBean.addCount();
    int count = prototypeBean.getCount();
    return count;
}
```

- 실행해보면 `prototypeBeanProvider.getObject()` 을 통해서 항상 새로운 프로토타입 빈이 생성되는 것을 확인할 수 있다.
- `ObjectProvider` 의 `getObject()` 를 호출하면 내부에서는 스프링 컨테이너를 통해 해당 빈을 찾아서 반환한다. (**DL**)
- 스프링이 제공하는 기능을 사용하지만, 기능이 단순하므로 단위테스트를 만들거나 mock 코드를 만들기는 훨씬 쉬워진다.
- `ObjectProvider` 는 지금 딱 필요한 DL 정도의 기능만 제공한다.

**특징**
- ObjectFactory : 기능이 단순, 별도의 라이브러리 필요 없음, 스프링에 의존
- ObjectProvider : ObjectFactory 상속, 옵션, 스트림 처리 등 편의 기능이 많고, 별도의 라이브러리 필요 없음, 스프링 의존


### JSR-330 Provider

`javax.inject.Provider`라는 JSR-330 자바 표준을 사용하는 방법
`javax.inject:javax.inject:1` 라이브러를 gradle에 추가해야한다.

**javax.inject.Provider 참고용 코드**

```java
package javax.inject;
public interface Provider<T> {
    T get(); 
}
```

```java
//implementation 'javax.inject:javax.inject:1' gradle 추가 필수 
@Autowired
private Provider<PrototypeBean> provider;

public int logic() {
      PrototypeBean prototypeBean = provider.get();
      prototypeBean.addCount();
      int count = prototypeBean.getCount();
      return count;
}
```

- 실행해보면 `provider.get()` 을 통해서 항상 새로운 프로토타입 빈이 생성되는 것을 확인할 수 있다. 
- `provider` 의 `get()` 을 호출하면 내부에서는 스프링 컨테이너를 통해 해당 빈을 찾아서 반환한다. (**DL**)
- 자바 표준이고, 기능이 단순하므로 단위테스트를 만들거나 mock 코드를 만들기는 훨씬 쉬워진다. 
- `Provider` 는 지금 딱 필요한 DL 정도의 기능만 제공한다.

**특징**
- get() 메서드 하나로 기능이 매우 단순하다.
- 별도의 라이브러리가 필요하다.
- 자바 표준이므로 스프링이 아닌 다른 컨테이너에서도 사용할 수 있다.

**정리**
- 매번 사용할 떄마다 의존관계 중비이 완료된 새로운 객체가 필요하면 사용한다.
- 대부분은 싱글톤 빈으로 문제가 해결이 가능하다.
- `ObjectProvider`, `JSR-330`등은 프로토타입 뿐만 아니라 DL이 필요한 경우는 언제든지 사용할 수 있다.


> **참고**: 실무에서 자바 표준인 JSR-330 Provider를 사용할 것인지, 아니면 스프링이 제공하는 ObjectProvider를 사용할 것인지 고민이 될 것이다. ObjectProvider는 DL을 위한 편의 기능을 많이 제공해주고 스프링 외에 별도의 의존관계 추가가 필요 없기 때문에 편리하다. 만약(정말 그럴일은 거의 없겠지만) 코드를 스프링이 아닌 다른 컨테이너에서도 사용할 수 있어야 한다면 JSR-330 Provider를 사용해야한다.
>
> 스프링을 사용하다 보면 이 기능 뿐만 아니라 다른 기능들도 자바 표준과 스프링이 제공하는 기능이
겹칠때가 많이 있다. 대부분 스프링이 더 다양하고 편리한 기능을 제공해주기 때문에, 특별히 다른 컨테이너를 사용할 일이 없다면, 스프링이 제공하는 기능을 사용하면 된다.

## 웹 스코프
