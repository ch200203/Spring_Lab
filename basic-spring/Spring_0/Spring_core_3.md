# Spring 핵심원리 기본편_3

## Spring으로 전환하기

---
### 스프링 컨테이너
- `ApplicationContext`를 스프링 컨테이너라 한다.
- 스프링 컨테이너는 `@Configuration`이 붙은 `AppConfig`를 구성 정보로 사용한다.
    여기서 `@Bean`이라 적힌 메서드를 모두 호출해서 반환된 객체를 스프링 컨테이너에 등록한다. 이렇게 스프링 컨테이너에 등록된 객체를 스프링 빈이라고 한다.
- 스프링 빈은 `@Bean`태그가 붙은 메서드 명을 빈의 이름으로 사용한다.
- 스프링 빈은 `applicationContext.getBean()` 메서드를 사용해서 찾을 수 있다.