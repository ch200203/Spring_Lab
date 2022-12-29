# Spring 핵심원리 기본편_5

## ComponentScan과 의존관계 자동 주입

- 등록해야할 스프링 빈이 많아지면 일일이 등록이 쉽지 않음.
- 스프링은 설정 정보가 없어도 자동으로 스프링 빈을 등록하는 **컴포넌트 스캔**기능을 제공한다.
- 또한, 의존관계도 자동으로 주입하는 `@Autowired`라는 기능도 제공한다.
- 컴포넌트 스캔을 사용하려면 먼저 `@ComponentScan`을 설정 정보에 붙여주면 된다.
- `@Configuration` 이 컴포넌트 스캔의 대상이 된 이유도 `@Configuration` 소스코드를 열어보면 `@Component` 애노테이션이 붙어있기 때문이다.
- 각 클래스가 컴포넌트 스캔의 대상이 되도록 `@Component` 어노테이션을 붙여준다.
- 컴포넌트 스캔은 이름 그대로 `@Component` 애노테이션이 붙은 클래스를 스캔해서 스프링 빈으로등록한다. 

- `@Autowired` 는 의존관계를 자동으로 주입해준다
- 생정자에 어노테이션을 명시하면, `ApplicationContext`에서 자동으로 의존 관계를 주입해서 사용하게 해준다.


### ComponentScan

- `@ComponentScan`은 `@Component`가 붙은 모든 클래스를 스프링 빈으로 등록한다.
- 이때, 기본 이름은 **클래스명**을 사용하되 맨 앞글자만 소문자로 사용
    - 이름 기본 전략: `MemberServiceImpl` 클래스 `memberServiceImpl`
    - 빈 이름 직접 지정: 만약 스프링 빈의 이름을 직접 지정하고 싶으면 `@Component("memberService2")` 이런식으로 이름을 부여하면 된다.

### Autowired
- 생성자에 `@Autowired` 를 지정하면, 스프링 컨테이너가 자동으로 해당 스프링 빈을 찾아서 주입한다.
- 기본 조회 전략은 타입이 같은 빈을 찾아서 주입한다.
    - `ac.getBean(MemberRepository.class)`와 동일함.
- 생성자에 파라미터가 많아도 다 찾아서 자동으로 주입한다.



## 탐색위치와 기본스캔대상


### 탐색할 패키지의 시작 위치 지정
-모든 자바 클래스를 다 컴포넌트 스캔하면 시간이 오래 걸린다. 그래서 꼭 필요한 위치부터 탐색하도록 시작 위치를 지정할 수 있다.
```java
@ComponentScan(
    basePackages = "hello.core", 
)
```

- `basePackages`: 탐색할 패지키의 시작위치를 지정한다. 해당 패캐지와 하위 패키지 전체를 대상으로 탐색한다.
    - `basePackages = {"hello.core", "hello.service"}` 과 같이 여러 시작위치를 지정할 수 있다.
- `basePackageClasses` : 지정한 클래스의 패키지를 탐색 시작 위치로 지정한다.
- 만약 지정하지 않으면 `@ComponentScan` 이 붙은 설정 정보 클래스의 패키지가 시작 위치가 된다.

    **권장하는 방법**

    패키지 위치를 지정하지 않고, 설정 정보 클래스의 위치를 프로젝트
    최상단에 두는 것이다. 최근 스프링 부트도 이 방법을 기본으로 제공한다

- Spring Boot에서는 `@SpringBootApplication` 를 이 프로젝트 시작 루트 위치에 두는 것이 관례이다. 
    - 이 설정안에 바로 `@ComponentScan` 이 들어있다.

### 컴포넌트 스캔 기본 대상

- `@Component` : 컴포넌트 스캔에서 사용
- `@Controlller` : 스프링 MVC 컨트롤러에서 사용
- `@Service` : 스프링 비즈니스 로직에서 사용
- `@Repository` : 스프링 데이터 접근 계층에서 사용
- `@Configuration` : 스프링 설정 정보에서 사용

부가 기능을 수행하는 어노테이션
- `@Controller` : 스프링 MVC 컨트롤러로 인식
- `@Repository` : 스프링 데이터 접근 계층으로 인식하고, 데이터 계층의 예외를 스프링 예외로 변환해준다.
- `@Configuration` : 앞서 보았듯이 스프링 설정 정보로 인식하고, 스프링 빈이 싱글톤을 유지하도록 추가
처리를 한다.
- `@Service` : 특별한 처리를 하지 않는다. 대신 개발자들이 핵심 비즈니스 로직이 여기에 있겠구나 라고 비즈니스 계층을 인식하는데 도움이 된다.

### 컴포넌트 스캔 필터
- `includeFilters` : 컴포넌트 스캔 대상을 추가로 지정한다.
- `excludeFilters` : 컴포넌트 스캔에서 제외할 대상을 지정한다

- FilterType은 5가지 옵션이 있다.
    - ANNOTATION: 기본값, 애노테이션을 인식해서 동작한다.
        - ex) org.example.SomeAnnotation
    - ASSIGNABLE_TYPE: 지정한 타입과 자식 타입을 인식해서 동작한다.
        - ex) org.example.SomeClass
    - ASPECTJ: AspectJ 패턴 사용
        - ex) org.example..*Service+
    - REGEX: 정규 표현식
        - ex) org\.example\.Default.*
    - CUSTOM: TypeFilter 이라는 인터페이스를 구현해서 처리
        - ex) org.example.MyTypeFilter

### 컴포넌트 스캔 중복등록 및 충돌

컴포넌트 스캔에서 같은 빈 이름으로 등록한경우

1. 자동 빈 등록 vs 자동 빈 등록
    - 컴포넌트 스캔으로 자동으로 빈이 등록 중 이름이 같은 경우 `ConflictingBeanDefinitionException` 에외 발생
2. 수동 빈 등록 vs 자동 빈 등록
    - 수동 빈 등록이 우선권을 가진다.
        - 수동 빈이 자동 빈을 Overriding 해버린다.
    - 대부분 이런경우는 개발자가 의도하지 않는 경우에 발생함.
        - **이렇게 되면, 잡기 어려운 버그가 발생해버림**
        - 가장 어려운 버그는 애매한 버그
- Spring Boot 에서는 수동 빈 등록과 자동 빈 등록이 충돌나면 오류가 발생하도록 기본값이 바뀌어 있음
```
    Consider renaming one of the beans or enabling overriding by setting 
    spring.main.allow-bean-definition-overriding=true
```