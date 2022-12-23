# Spring 핵심원리 기본편_3

## Spring으로 전환하기

---
### 스프링 컨테이너
- `ApplicationContext`를 스프링 컨테이너라 한다.
- 스프링 컨테이너는 `@Configuration`이 붙은 `AppConfig`를 구성 정보로 사용한다.
    여기서 `@Bean`이라 적힌 메서드를 모두 호출해서 반환된 객체를 스프링 컨테이너에 등록한다. 이렇게 스프링 컨테이너에 등록된 객체를 스프링 빈이라고 한다.
- 스프링 빈은 `@Bean`태그가 붙은 메서드 명을 빈의 이름으로 사용한다.
    - 단, 빈은 항상 다른 이름을 부여해야 한다.
- 스프링 빈은 `applicationContext.getBean()` 메서드를 사용해서 찾을 수 있다.
- 스프링 컨테이너는 설정 정보를 참고해서 의존관계를 주입(DI)한다.


### 스프링 빈 조회 상속관계 

- 부모 타입으로 조회하면, 자식 타입도 함께 조회한다.
- 래서 모든 자바 객체의 최고 부모인 `Object` 타입으로 조회하면, 모든 스프링 빈을 조회한다.

### BeanFactory와 ApplicationContext


### BeanFactory와

- 스프링 컨테이너의 최상위 인터페이스다.
- 스프링 빈을 관리하고 조회하는 역할을 담당한다.
- `getBean()` 을 제공한다.
- 지금까지 우리가 사용했던 대부분의 기능은 BeanFactory가 제공하는 기능이다


### ApplicationContext
- BeanFactory 기능을 모두 상속받아서 제공한다.
- 애플리케이션을 개발할 때는 빈을 관리하고 조회하는 기능은 물론이고, 수 많은 부가기능이 필요하다.
    - 메시지소스를 활용한 국제화 기능
    - 환경변수
        - 개발환경, 로컬 환경, 테스트환경, 스테이지 환경 등...
    - 애플리케이션 이벤트
    - 편리한 리소스 조회
        - 파일, 클래스패스, 외부 리소스를 편리하게 조회


### 정리
- ApplicationContext는 BeanFactory의 기능을 상속받는다.
- ApplicationContext는 빈 관리기능 + 편리한 부가 기능을 제공한다.
- BeanFactory를 직접 사용할 일은 거의 없다. 부가기능이 포함된 ApplicationContext를 사용한다.
- BeanFactory나 ApplicationContext를 스프링 컨테이너라 한다.



## 스프링 빈 설정 메타 정보 - Bean Definition

- XML을 읽어서 BeanDefinition을 만들면 된다.
- 자바 코드를 읽어서 BeanDefinition을 만들면 된다.
- 스프링 컨테이너는 자바 코드인지, XML인지 몰라도 된다. 오직 BeanDefinition만 알면 된다
- `BeanDefinition` 을 빈 설정 메타정보라 한다.
    - `@Bean` , `<bean>` 당 각각 하나씩 메타 정보가 생성
- 스프링 컨테이너는 이 메타정보를 기반으로 스프링 빈을 생성한다.

- `AnnotationConfigApplicationContext` 는 `AnnotatedBeanDefinitionReader` 를 사용해서
- `AppConfig.class` 를 읽고 `BeanDefinition` 을 생성한다.
- `GenericXmlApplicationContext` 는 `XmlBeanDefinitionReader` 를 사용해서 `appConfig.xml` 설정
- 정보를 읽고 `BeanDefinition` 을 생성한다.
새로운 형식의 설정 정보가 추가되면, XxxBeanDefinitionReader를 만들어서 `BeanDefinition` 을 생성하면 된다